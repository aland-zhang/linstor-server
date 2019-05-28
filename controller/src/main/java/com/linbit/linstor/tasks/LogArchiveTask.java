package com.linbit.linstor.tasks;

import com.linbit.linstor.InternalApiConsts;
import com.linbit.linstor.Node;
import com.linbit.linstor.NodeRepository;
import com.linbit.linstor.annotation.SystemContext;
import com.linbit.linstor.api.interfaces.serializer.CtrlClientSerializer;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.netcom.Peer;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.AccessDeniedException;
import com.linbit.locks.LockGuard;
import com.linbit.locks.LockGuardFactory;

import static com.linbit.locks.LockGuardFactory.LockObj.NODES_MAP;
import static com.linbit.locks.LockGuardFactory.LockType.READ;

import javax.inject.Inject;

public class LogArchiveTask implements TaskScheduleService.Task
{
    private static final long LOGARCHIVE_SLEEP = 24 * 60 * 60 * 1_000;

    private final ErrorReporter errorReporter;
    private final NodeRepository nodeRepository;
    private final LockGuardFactory lockGuardFactory;
    private final AccessContext sysCtx;
    private final CtrlClientSerializer ctrlClientSerializer;

    @Inject
    public LogArchiveTask(
        ErrorReporter errorReporterRef,
        NodeRepository nodeRepositoryRef,
        LockGuardFactory lockGuardFactoryRef,
        @SystemContext AccessContext sysCtxRef,
        CtrlClientSerializer ctrlClientSerializerRef
    )
    {
        errorReporter = errorReporterRef;
        nodeRepository = nodeRepositoryRef;
        lockGuardFactory = lockGuardFactoryRef;
        sysCtx = sysCtxRef;
        ctrlClientSerializer = ctrlClientSerializerRef;
    }

    @Override
    public long run()
    {
        errorReporter.archiveLogDirectory();

        try (LockGuard lg = lockGuardFactory.build(READ, NODES_MAP))
        {
            for (Node node : nodeRepository.getMapForView(sysCtx).values())
            {
                Peer nodePeer = node.getPeer(sysCtx);
                if (nodePeer != null && nodePeer.isConnected())
                {
                    nodePeer.sendMessage(
                        ctrlClientSerializer.onewayBuilder(InternalApiConsts.API_ARCHIVE_LOGS).build()
                    );
                }
            }
        }
        catch (AccessDeniedException ignored)
        {
        }
        return LOGARCHIVE_SLEEP;
    }
}
