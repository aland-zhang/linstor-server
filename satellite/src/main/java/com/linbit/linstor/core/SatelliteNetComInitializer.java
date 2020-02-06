package com.linbit.linstor.core;

import com.linbit.ImplementationError;
import com.linbit.ServiceName;
import com.linbit.SystemService;
import com.linbit.SystemServiceStartException;
import com.linbit.linstor.LinStorException;
import com.linbit.linstor.annotation.PublicContext;
import com.linbit.linstor.api.interfaces.serializer.CommonSerializer;
import com.linbit.linstor.core.cfg.StltConfig;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.netcom.MessageProcessor;
import com.linbit.linstor.netcom.TcpConnector;
import com.linbit.linstor.netcom.TcpConnectorService;
import com.linbit.linstor.netcom.ssl.SslTcpConnectorService;
import com.linbit.linstor.proto.CommonMessageProcessor;
import com.linbit.linstor.security.AccessContext;

import javax.inject.Inject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;

public final class SatelliteNetComInitializer
{
    private static final String NET_COM_CONF_TYPE_PLAIN = "plain";
    private static final String NET_COM_CONF_TYPE_SSL = "ssl";

    private final ErrorReporter errorReporter;
    private final CommonSerializer commonSerializer;
    private final AccessContext publicCtx;
    private final MessageProcessor msgProc;
    private final StltConnTracker stltConnTracker;
    private final Map<ServiceName, SystemService> systemServicesMap;
    private final StltConfig stltCfg;

    public TcpConnector netComSvc;

    @Inject
    public SatelliteNetComInitializer(
        ErrorReporter errorReporterRef,
        CommonSerializer commonSerializerRef,
        @PublicContext AccessContext publicCtxRef,
        CommonMessageProcessor msgProcRef,
        StltConnTracker stltConnTrackerRef,
        Map<ServiceName, SystemService> systemServicesMapRef,
        StltConfig stltCfgRef
    )
    {
        errorReporter = errorReporterRef;
        commonSerializer = commonSerializerRef;
        publicCtx = publicCtxRef;
        msgProc = msgProcRef;
        stltConnTracker = stltConnTrackerRef;
        systemServicesMap = systemServicesMapRef;
        stltCfg = stltCfgRef;
    }

    public boolean initMainNetComService(final AccessContext initCtx)
    {
        Path configurationDirectory = stltCfg.getConfigPath();
        String bindAddressStr = stltCfg.getNetBindAddress();
        Integer port = stltCfg.getNetPort();
        String type = stltCfg.getNetType();

        boolean success = false;
        try
        {
            InetAddress addr = InetAddress.getByName(bindAddressStr);
            SocketAddress bindAddress = new InetSocketAddress(addr, port);

            if (type.equalsIgnoreCase(NET_COM_CONF_TYPE_PLAIN))
            {
                netComSvc = new TcpConnectorService(
                    errorReporter,
                    commonSerializer,
                    msgProc,
                    bindAddress,
                    publicCtx,
                    initCtx,
                    stltConnTracker
                );
            }
            else
            if (type.equalsIgnoreCase(NET_COM_CONF_TYPE_SSL))
            {
                try
                {
                    netComSvc = new SslTcpConnectorService(
                        errorReporter,
                        commonSerializer,
                        msgProc,
                        bindAddress,
                        publicCtx,
                        initCtx,
                        stltConnTracker,
                        stltCfg.getNetSecureSslProtocol(),
                        stltCfg.getNetSecureServerCertificate(),
                        stltCfg.getNetSecureKeystorePassword().toCharArray(),
                        stltCfg.getNetSecureKeyPassword().toCharArray(),
                        stltCfg.getNetSecureTrustedCertificates(),
                        stltCfg.getNetSecureTruststorePassword().toCharArray()
                    );
                }
                catch (KeyManagementException keyMgmtExc)
                {
                    errorReporter.reportError(
                        new LinStorException(
                            "Initialization of the SSLContext failed. See cause for details",
                            keyMgmtExc
                        )
                    );
                }
                catch (UnrecoverableKeyException unrecoverableKeyExc)
                {
                    String errorMsg = "A private or public key for the initialization of SSL encryption could " +
                        "not be loaded";
                    errorReporter.reportError(
                        new LinStorException(
                            errorMsg,
                            errorMsg,
                            null,
                            "Check whether the password for the SSL keystores is correct.",
                            null,
                            unrecoverableKeyExc
                        )
                    );
                }
                catch (NoSuchAlgorithmException exc)
                {
                    errorReporter.reportError(
                        new LinStorException(
                            String.format(
                                "SSL initialization failed: " +
                                "The SSL/TLS encryption protocol '%s' is not available on this system.",
                                stltCfg.getNetSecureSslProtocol()
                            ),
                            "SSL initialization failed.",
                            String.format(
                                "The SSL/TLS protocol '%s' is not available on this system",
                                stltCfg.getNetSecureSslProtocol()
                            ),
                            "- Select a supported SSL/TLS protocol in the network communications configuration\n" +
                            "or\n" +
                            "- Enable support for the currently selected SSL/TLS protocol on this system",
                            null,
                            exc
                        )
                    );
                }
                catch (KeyStoreException keyStoreExc)
                {
                    throw new ImplementationError(
                        "Default SSL keystore type could not be found by the KeyStore instance",
                        keyStoreExc
                    );
                }
                catch (CertificateException exc)
                {
                    errorReporter.reportError(
                        new LinStorException(
                            "A required SSL certificate could not be loaded",
                            "A required SSL certificate could not be loaded from the keystore files",
                            null,
                            "Ensure that the required SSL certificates are contained in the keystore files.\n" +
                            "Refer to documentation for information on how to setup SSL encryption.",
                            null,
                            exc
                        )
                    );
                }
            }

            if (netComSvc != null)
            {
                try
                {
                    systemServicesMap.put(netComSvc.getInstanceName(), netComSvc);
                    netComSvc.start();
                    errorReporter.logInfo(
                        String.format(
                            "%s started on port %s:%d",
                            netComSvc.getInstanceName().displayValue,
                            addr, port
                        )
                    );
                    success = true;
                }
                catch (SystemServiceStartException sysSvcStartExc)
                {
                    String errorMsg = sysSvcStartExc.getMessage();
                    if (errorMsg == null)
                    {
                        errorMsg = "The initial network communications service failed to start.";
                    }
                    errorReporter.reportError(
                        new LinStorException(
                            errorMsg,
                            errorMsg, // description
                            null, // cause
                            null, // correction
                            null, // details
                            sysSvcStartExc // Nested throwable
                        )
                    );

                }
            }
            else
            {
                if (!NET_COM_CONF_TYPE_PLAIN.equalsIgnoreCase(type) &&
                    !NET_COM_CONF_TYPE_SSL.equalsIgnoreCase(type))
                {
                    errorReporter.reportError(
                        new LinStorException(
                            // Message
                            String.format(
                                "The network type has to be either %s or %s",
                                NET_COM_CONF_TYPE_PLAIN,
                                NET_COM_CONF_TYPE_SSL
                            ),
                            // Description
                            "The initial network communication service can not be started.",
                            // Cause
                            String.format(
                                "The network type '%s' is invalid.",
                                type
                            ),
                            // Error details
                            null,
                            // No nested exception
                            null
                        )
                    );
                }
            }
        }
        catch (IOException ioExc)
        {
            errorReporter.reportError(ioExc);
        }
        return success;
    }
}
