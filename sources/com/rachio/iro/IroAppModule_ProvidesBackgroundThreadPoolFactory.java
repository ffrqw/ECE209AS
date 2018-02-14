package com.rachio.iro;

import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.concurrent.Executor;

public final class IroAppModule_ProvidesBackgroundThreadPoolFactory implements Factory<Executor> {
    static final /* synthetic */ boolean $assertionsDisabled = (!IroAppModule_ProvidesBackgroundThreadPoolFactory.class.desiredAssertionStatus());
    private final IroAppModule module;

    private IroAppModule_ProvidesBackgroundThreadPoolFactory(IroAppModule module) {
        if ($assertionsDisabled || module != null) {
            this.module = module;
            return;
        }
        throw new AssertionError();
    }

    public static Factory<Executor> create(IroAppModule module) {
        return new IroAppModule_ProvidesBackgroundThreadPoolFactory(module);
    }

    public final /* bridge */ /* synthetic */ Object get() {
        return (Executor) Preconditions.checkNotNull(IroAppModule.providesBackgroundThreadPool(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
