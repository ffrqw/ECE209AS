package com.rachio.iro;

import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class IroApplication_MembersInjector implements MembersInjector<IroApplication> {
    static final /* synthetic */ boolean $assertionsDisabled = (!IroApplication_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        IroApplication iroApplication = (IroApplication) obj;
        if (iroApplication == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        iroApplication.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
        iroApplication.database = (Database) this.databaseProvider.get();
        iroApplication.restClient = (RestClient) this.restClientProvider.get();
    }

    private IroApplication_MembersInjector(Provider<PrefsWrapper> prefsWrapperProvider, Provider<Database> databaseProvider, Provider<RestClient> restClientProvider) {
        if ($assertionsDisabled || prefsWrapperProvider != null) {
            this.prefsWrapperProvider = prefsWrapperProvider;
            if ($assertionsDisabled || databaseProvider != null) {
                this.databaseProvider = databaseProvider;
                if ($assertionsDisabled || restClientProvider != null) {
                    this.restClientProvider = restClientProvider;
                    return;
                }
                throw new AssertionError();
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public static MembersInjector<IroApplication> create(Provider<PrefsWrapper> prefsWrapperProvider, Provider<Database> databaseProvider, Provider<RestClient> restClientProvider) {
        return new IroApplication_MembersInjector(prefsWrapperProvider, databaseProvider, restClientProvider);
    }
}
