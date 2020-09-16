package com.linbit.linstor.layer.resource;

import com.linbit.ExhaustedPoolException;
import com.linbit.ImplementationError;
import com.linbit.InvalidNameException;
import com.linbit.ValueInUseException;
import com.linbit.ValueOutOfRangeException;
import com.linbit.linstor.LinStorException;
import com.linbit.linstor.annotation.ApiContext;
import com.linbit.linstor.core.objects.Resource;
import com.linbit.linstor.core.objects.ResourceDefinition;
import com.linbit.linstor.core.objects.Snapshot;
import com.linbit.linstor.core.objects.Volume;
import com.linbit.linstor.core.objects.VolumeDefinition;
import com.linbit.linstor.dbdrivers.DatabaseException;
import com.linbit.linstor.layer.LayerPayload;
import com.linbit.linstor.layer.LayerPayload.OpenflexRscDfnPayload;
import com.linbit.linstor.layer.resource.CtrlRscLayerDataFactory.ChildResourceData;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.numberpool.DynamicNumberPool;
import com.linbit.linstor.numberpool.NumberPoolModule;
import com.linbit.linstor.propscon.InvalidKeyException;
import com.linbit.linstor.security.AccessContext;
import com.linbit.linstor.security.AccessDeniedException;
import com.linbit.linstor.storage.data.adapter.nvme.OpenflexRscData;
import com.linbit.linstor.storage.data.adapter.nvme.OpenflexRscDfnData;
import com.linbit.linstor.storage.data.adapter.nvme.OpenflexVlmData;
import com.linbit.linstor.storage.interfaces.categories.resource.AbsRscLayerObject;
import com.linbit.linstor.storage.interfaces.categories.resource.VlmDfnLayerObject;
import com.linbit.linstor.storage.interfaces.categories.resource.VlmProviderObject;
import com.linbit.linstor.storage.kinds.DeviceLayerKind;
import com.linbit.linstor.storage.utils.LayerDataFactory;
import com.linbit.linstor.utils.NameShortener;

import javax.inject.Inject;
import javax.inject.Provider;

import java.util.Collections;
import java.util.List;

import com.google.inject.name.Named;

public class RscOpenflexLayerHelper
    extends AbsRscLayerHelper<
        OpenflexRscData<Resource>, OpenflexVlmData<Resource>,
        OpenflexRscDfnData<Resource>, VlmDfnLayerObject
>
{
    private final NameShortener nameShortener;

    @Inject
    RscOpenflexLayerHelper(
        ErrorReporter errorReporterRef,
        @ApiContext AccessContext apiCtxRef,
        LayerDataFactory layerDataFactoryRef,
        @Named(NumberPoolModule.LAYER_RSC_ID_POOL) DynamicNumberPool layerRscIdPoolRef,
        Provider<CtrlRscLayerDataFactory> layerDataHelperProviderRef,
        @Named(NameShortener.OPENFLEX) NameShortener nameShortenerRef
    )
    {
        super(
            errorReporterRef,
            apiCtxRef,
            layerDataFactoryRef,
            layerRscIdPoolRef,
            // OpenflexRscData.class cannot directly be casted to Class<OpenflexRscData<Resource>>. because java.
            // its type is Class<OpenflexRscData> (without nested types), but that is not enough as the super constructor
            // wants a Class<RSC_PO>, where RSC_PO is OpenflexRscData<Resource>.
            (Class<OpenflexRscData<Resource>>) ((Object) OpenflexRscData.class),
            DeviceLayerKind.OPENFLEX,
            layerDataHelperProviderRef
        );
        nameShortener = nameShortenerRef;
    }

    @Override
    protected OpenflexRscDfnData<Resource> createRscDfnData(
        ResourceDefinition rscDfnRef,
        String rscNameSuffixRef,
        LayerPayload payloadRef
    ) throws AccessDeniedException, DatabaseException, ValueOutOfRangeException, ExhaustedPoolException,
        ValueInUseException
    {
        String shortName = nameShortener.shorten(rscDfnRef, rscNameSuffixRef);
        return layerDataFactory.createOpenflexRscDfnData(
            rscDfnRef.getName(),
            rscNameSuffixRef,
            shortName,
            payloadRef.ofRscDfn.nqn
        );
    }

    @Override
    protected void mergeRscDfnData(OpenflexRscDfnData<Resource> rscDfnRef, LayerPayload payloadRef)
        throws DatabaseException,
        ExhaustedPoolException, ValueOutOfRangeException, ValueInUseException, AccessDeniedException
    {
        OpenflexRscDfnPayload payload = payloadRef.ofRscDfn;

        if (payload.nqn != null)
        {
            rscDfnRef.setNqn(payload.nqn);
        }
    }

    @Override
    protected VlmDfnLayerObject createVlmDfnData(
        VolumeDefinition vlmDfnRef,
        String rscNameSuffixRef,
        LayerPayload payloadRef
    ) throws AccessDeniedException, DatabaseException, ValueOutOfRangeException, ExhaustedPoolException,
        ValueInUseException
    {
        // OpenflexLayer does not have volume-definition specific data
        return null;
    }

    @Override
    protected void mergeVlmDfnData(VlmDfnLayerObject vlmDfnDataRef, LayerPayload payloadRef)
    {
        // OpenflexLayer does not have volume-definition specific data
    }

    @Override
    protected OpenflexRscData<Resource> createRscData(
        Resource rscRef,
        LayerPayload payloadRef,
        String rscNameSuffixRef,
        AbsRscLayerObject<Resource> parentObjectRef,
        List<DeviceLayerKind> layerListRef
    ) throws AccessDeniedException, DatabaseException, ValueOutOfRangeException, ExhaustedPoolException,
        ValueInUseException, ImplementationError, InvalidNameException
    {
        RscNvmeLayerHelper.ensureTargetNodeNameIsSet(rscRef, apiCtx);
        ResourceDefinition rscDfn = rscRef.getDefinition();
        OpenflexRscDfnData<Resource> ofRscDfnData = ensureResourceDefinitionExists(
            rscDfn,
            rscNameSuffixRef,
            payloadRef
        );
        OpenflexRscData<Resource> ofRscData = layerDataFactory.createOpenflexRscData(
            layerRscIdPool.autoAllocate(),
            rscRef,
            ofRscDfnData,
            parentObjectRef
        );
        ofRscDfnData.getOfRscDataList().add(ofRscData);
        return ofRscData;
    }

    @Override
    protected List<ChildResourceData> getChildRsc(
        OpenflexRscData<Resource> rscDataRef,
        List<DeviceLayerKind> layerListRef
    ) throws AccessDeniedException, InvalidKeyException
    {
        return Collections.emptyList(); // no children.
    }

    @Override
    protected void mergeRscData(OpenflexRscData<Resource> rscDataRef, LayerPayload payloadRef)
        throws AccessDeniedException, DatabaseException
    {
        // nothing to merge
    }

    @Override
    protected boolean needsChildVlm(AbsRscLayerObject<Resource> childRscDataRef, Volume vlmRef)
        throws AccessDeniedException, InvalidKeyException
    {
        return false;
    }

    @Override
    protected OpenflexVlmData<Resource> createVlmLayerData(
        OpenflexRscData<Resource> rscDataRef,
        Volume vlmRef,
        LayerPayload payloadRef,
        List<DeviceLayerKind> layerListRef
    ) throws AccessDeniedException, DatabaseException, ValueOutOfRangeException, ExhaustedPoolException,
        ValueInUseException, LinStorException, InvalidKeyException, InvalidNameException
    {
        return layerDataFactory.createOpenflexVlmData(
            vlmRef,
            rscDataRef,
            layerDataHelperProvider.get().getStorPool(
                vlmRef,
                rscDataRef,
                payloadRef
            )
        );
    }

    @Override
    protected void mergeVlmData(
        OpenflexVlmData<Resource> vlmDataRef,
        Volume vlmRef,
        LayerPayload payloadRef,
        List<DeviceLayerKind> layerListRef
    ) throws AccessDeniedException, InvalidKeyException, InvalidNameException, DatabaseException,
        ValueOutOfRangeException, ExhaustedPoolException, ValueInUseException, LinStorException
    {
        // nothing to do
    }

    @Override
    protected void resetStoragePools(AbsRscLayerObject<Resource> rscDataRef)
        throws AccessDeniedException, DatabaseException, InvalidKeyException
    {
        // nothing to do
    }

    @Override
    protected boolean isExpectedToProvideDevice(OpenflexRscData<Resource> ofRscData) throws AccessDeniedException
    {
        return ofRscData.getAbsResource().getStateFlags().isSet(apiCtx, Resource.Flags.NVME_INITIATOR);
    }

    @Override
    protected OpenflexRscDfnData<Resource> restoreRscDfnData(
        ResourceDefinition rscDfnRef,
        AbsRscLayerObject<Snapshot> fromSnapDataRef
    ) throws AccessDeniedException, DatabaseException, ValueOutOfRangeException, ExhaustedPoolException,
        ValueInUseException
    {
        String resourceNameSuffix = fromSnapDataRef.getResourceNameSuffix();
        OpenflexRscDfnData<Snapshot> snapDfnData = fromSnapDataRef.getAbsResource().getSnapshotDefinition()
            .getLayerData(
                apiCtx,
                DeviceLayerKind.OPENFLEX,
                resourceNameSuffix
            );
        String shortName = nameShortener.shorten(rscDfnRef, resourceNameSuffix);
        return layerDataFactory.createOpenflexRscDfnData(
            rscDfnRef.getName(),
            resourceNameSuffix,
            shortName,
            snapDfnData.getNqn()
        );
    }

    @Override
    protected OpenflexRscData<Resource> restoreRscData(
        Resource rscRef,
        AbsRscLayerObject<Snapshot> fromSnapDataRef,
        AbsRscLayerObject<Resource> rscParentRef
    ) throws DatabaseException, AccessDeniedException, ExhaustedPoolException
    {
        OpenflexRscDfnData<Resource> ofRscDfnData = rscRef.getDefinition().getLayerData(
            apiCtx,
            DeviceLayerKind.OPENFLEX,
            fromSnapDataRef.getResourceNameSuffix()
        );

        OpenflexRscData<Resource> ofRscData = layerDataFactory.createOpenflexRscData(
            layerRscIdPool.autoAllocate(),
            rscRef,
            ofRscDfnData,
            rscParentRef
        );
        ofRscDfnData.getOfRscDataList().add(ofRscData);
        return ofRscData;
    }

    @Override
    protected VlmDfnLayerObject restoreVlmDfnData(
        VolumeDefinition vlmDfnRef,
        VlmProviderObject<Snapshot> fromSnapVlmDataRef
    ) throws DatabaseException, AccessDeniedException, ValueOutOfRangeException, ExhaustedPoolException,
        ValueInUseException
    {
        // OpenflexLayer does not have volume-definition specific data
        return null;
    }

    @Override
    protected OpenflexVlmData<Resource> restoreVlmData(
        Volume vlmRef,
        OpenflexRscData<Resource> rscDataRef,
        VlmProviderObject<Snapshot> snapVlmData
    ) throws DatabaseException, AccessDeniedException
    {
        return layerDataFactory.createOpenflexVlmData(vlmRef, rscDataRef, snapVlmData.getStorPool());
    }


}
