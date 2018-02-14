package com.rachio.iro.utils;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask.Holder;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class RestClientProgressDialogAsyncTask_Holder_MembersInjector implements MembersInjector<Holder> {
    static final /* synthetic */ boolean $assertionsDisabled = (!RestClientProgressDialogAsyncTask_Holder_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        Holder holder = (Holder) obj;
        if (holder == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        holder.restClient = (RestClient) this.restClientProvider.get();
        holder.database = (Database) this.databaseProvider.get();
        holder.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
    }

    private RestClientProgressDialogAsyncTask_Holder_MembersInjector(Provider<RestClient> restClientProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
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

    public static MembersInjector<Holder> create(Provider<RestClient> restClientProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        return new RestClientProgressDialogAsyncTask_Holder_MembersInjector(restClientProvider, databaseProvider, prefsWrapperProvider);
    }
}
