package com.rachio.iro;

import com.rachio.iro.model.db.Database;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class IroAppModule_ProvideDatabaseFactory implements Factory<Database> {
    static final /* synthetic */ boolean $assertionsDisabled = (!IroAppModule_ProvideDatabaseFactory.class.desiredAssertionStatus());
    private final IroAppModule module;

    private IroAppModule_ProvideDatabaseFactory(IroAppModule module) {
        if ($assertionsDisabled || module != null) {
            this.module = module;
            return;
        }
        throw new AssertionError();
    }

    public static Factory<Database> create(IroAppModule module) {
        return new IroAppModule_ProvideDatabaseFactory(module);
    }

    public final /* bridge */ /* synthetic */ Object get() {
        return (Database) Preconditions.checkNotNull(this.module.provideDatabase(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
