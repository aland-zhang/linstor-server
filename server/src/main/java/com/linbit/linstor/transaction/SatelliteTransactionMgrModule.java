package com.linbit.linstor.transaction;

import com.google.inject.AbstractModule;

public class SatelliteTransactionMgrModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(TransactionMgrGenerator.class).to(SatelliteTransactionMgrGenerator.class);
    }
}
