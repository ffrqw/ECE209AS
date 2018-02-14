package com.rachio.iro;

import com.google.android.gms.analytics.Tracker;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class IroAppModule_ProvideTrackerFactory implements Factory<Tracker> {
    static final /* synthetic */ boolean $assertionsDisabled = (!IroAppModule_ProvideTrackerFactory.class.desiredAssertionStatus());
    private final IroAppModule module;

    private IroAppModule_ProvideTrackerFactory(IroAppModule module) {
        if ($assertionsDisabled || module != null) {
            this.module = module;
            return;
        }
        throw new AssertionError();
    }

    public static Factory<Tracker> create(IroAppModule module) {
        return new IroAppModule_ProvideTrackerFactory(module);
    }

    public final /* bridge */ /* synthetic */ Object get() {
        return (Tracker) Preconditions.checkNotNull(this.module.provideTracker(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
