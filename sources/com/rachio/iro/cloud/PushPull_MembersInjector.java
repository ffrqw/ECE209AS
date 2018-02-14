package com.rachio.iro.cloud;

import com.rachio.iro.model.db.Database;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class PushPull_MembersInjector implements MembersInjector<PushPull> {
    static final /* synthetic */ boolean $assertionsDisabled = (!PushPull_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<RestClient> restClientProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        PushPull pushPull = (PushPull) obj;
        if (pushPull == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        pushPull.restClient = (RestClient) this.restClientProvider.get();
        pushPull.database = (Database) this.databaseProvider.get();
    }

    private PushPull_MembersInjector(Provider<RestClient> restClientProvider, Provider<Database> databaseProvider) {
        if ($assertionsDisabled || restClientProvider != null) {
            this.restClientProvider = restClientProvider;
            if ($assertionsDisabled || databaseProvider != null) {
                this.databaseProvider = databaseProvider;
                return;
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public static MembersInjector<PushPull> create(Provider<RestClient> restClientProvider, Provider<Database> databaseProvider) {
        return new PushPull_MembersInjector(restClientProvider, databaseProvider);
    }
}
