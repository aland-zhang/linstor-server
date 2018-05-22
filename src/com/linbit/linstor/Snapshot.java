package com.linbit.linstor;

import com.linbit.linstor.security.AccessContext;

import java.util.UUID;

public interface Snapshot
{
    UUID getUuid();

    SnapshotDefinition getSnapshotDefinition();

    Node getNode();

    boolean getSuspendResource();

    void setSuspendResource(boolean suspendResource);

    boolean getTakeSnapshot();

    void setTakeSnapshot(boolean takeSnapshot);

    boolean isResourceSuspended();

    void setResourceSuspended(boolean resourceSuspended);

    boolean isSnapshotTaken();

    void setSnapshotTaken(boolean snapshotTaken);

    UUID debugGetVolatileUuid();

    SnapshotApi getApiData(AccessContext accCtx);

    public interface SnapshotApi
    {
        UUID getSnapshotUuid();
        String getSnapshotName();
        UUID getSnapshotDfnUuid();
        boolean getSuspendResource();
        boolean getTakeSnapshot();
    }
}