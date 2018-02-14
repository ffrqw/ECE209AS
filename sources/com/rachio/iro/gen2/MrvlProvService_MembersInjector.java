package com.rachio.iro.gen2;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class MrvlProvService_MembersInjector implements MembersInjector<MrvlProvService> {
    static final /* synthetic */ boolean $assertionsDisabled = (!MrvlProvService_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        MrvlProvService mrvlProvService = (MrvlProvService) obj;
        if (mrvlProvService == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        mrvlProvService.restClient = (RestClient) this.restClientProvider.get();
        mrvlProvService.database = (Database) this.databaseProvider.get();
        mrvlProvService.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
    }

    private MrvlProvService_MembersInjector(Provider<RestClient> restClientProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        if ($assertionsDisabled || restClientProvider != null) {
            this.restClientProvider = restClientProvider;
            if ($assertionsDisabled || databaseProvider != null) {
                this.databaseProvider = databaseProvider;
                if ($assertionsDisabled || prefsWrapperProvider != null) {
                    this.prefsWrapperProvider = prefsWrapperProvider;
                    return;
                }
                throw new AssertionError();
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public static MembersInjector<MrvlProvService> create(Provider<RestClient> restClientProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        return new MrvlProvService_MembersInjector(restClientProvider, databaseProvider, prefsWrapperProvider);
    }
}
