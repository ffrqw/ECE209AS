package com.rachio.iro;

import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class IroAppModule_ProvidePrefsWrapperFactory implements Factory<PrefsWrapper> {
    static final /* synthetic */ boolean $assertionsDisabled = (!IroAppModule_ProvidePrefsWrapperFactory.class.desiredAssertionStatus());
    private final IroAppModule module;

    private IroAppModule_ProvidePrefsWrapperFactory(IroAppModule module) {
        if ($assertionsDisabled || module != null) {
            this.module = module;
            return;
        }
        throw new AssertionError();
    }

    public static Factory<PrefsWrapper> create(IroAppModule module) {
        return new IroAppModule_ProvidePrefsWrapperFactory(module);
    }

    public final /* bridge */ /* synthetic */ Object get() {
        return (PrefsWrapper) Preconditions.checkNotNull(this.module.providePrefsWrapper(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
