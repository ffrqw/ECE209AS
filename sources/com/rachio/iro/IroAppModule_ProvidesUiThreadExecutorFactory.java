package com.rachio.iro;

import com.rachio.iro.utils.UiThreadExecutor;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class IroAppModule_ProvidesUiThreadExecutorFactory implements Factory<UiThreadExecutor> {
    static final /* synthetic */ boolean $assertionsDisabled = (!IroAppModule_ProvidesUiThreadExecutorFactory.class.desiredAssertionStatus());
    private final IroAppModule module;

    private IroAppModule_ProvidesUiThreadExecutorFactory(IroAppModule module) {
        if ($assertionsDisabled || module != null) {
            this.module = module;
            return;
        }
        throw new AssertionError();
    }

    public static Factory<UiThreadExecutor> create(IroAppModule module) {
        return new IroAppModule_ProvidesUiThreadExecutorFactory(module);
    }

    public final /* bridge */ /* synthetic */ Object get() {
        return (UiThreadExecutor) Preconditions.checkNotNull(IroAppModule.providesUiThreadExecutor(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
