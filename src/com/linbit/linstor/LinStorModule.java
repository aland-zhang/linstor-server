package com.linbit.linstor;

import com.google.inject.AbstractModule;

public class LinStorModule extends AbstractModule
{
    // Name for main worker pool, e.g. API call handling
    public static final String MAIN_WORKER_POOL_NAME = "MainWorkerPool";

    @Override
    protected void configure()
    {
    }
}
