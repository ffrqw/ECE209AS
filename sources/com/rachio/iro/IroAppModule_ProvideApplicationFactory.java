package com.rachio.iro;

import android.app.Application;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class IroAppModule_ProvideApplicationFactory implements Factory<Application> {
    static final /* synthetic */ boolean $assertionsDisabled = (!IroAppModule_ProvideApplicationFactory.class.desiredAssertionStatus());
    private final IroAppModule module;

    public final /* bridge */ /* synthetic */ Object get() {
        return (Application) Preconditions.checkNotNull(this.module.provideApplication(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
