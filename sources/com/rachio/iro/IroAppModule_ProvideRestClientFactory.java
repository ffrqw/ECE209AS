package com.rachio.iro;

import com.rachio.iro.cloud.RestClient;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class IroAppModule_ProvideRestClientFactory implements Factory<RestClient> {
    static final /* synthetic */ boolean $assertionsDisabled = (!IroAppModule_ProvideRestClientFactory.class.desiredAssertionStatus());
    private final IroAppModule module;

    private IroAppModule_ProvideRestClientFactory(IroAppModule module) {
        if ($assertionsDisabled || module != null) {
            this.module = module;
            return;
        }
        throw new AssertionError();
    }

    public static Factory<RestClient> create(IroAppModule module) {
        return new IroAppModule_ProvideRestClientFactory(module);
    }

    public final /* bridge */ /* synthetic */ Object get() {
        return (RestClient) Preconditions.checkNotNull(this.module.provideRestClient(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
