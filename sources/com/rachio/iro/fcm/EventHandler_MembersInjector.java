package com.rachio.iro.fcm;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class EventHandler_MembersInjector implements MembersInjector<EventHandler> {
    static final /* synthetic */ boolean $assertionsDisabled = (!EventHandler_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        EventHandler eventHandler = (EventHandler) obj;
        if (eventHandler == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        eventHandler.database = (Database) this.databaseProvider.get();
        eventHandler.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
        eventHandler.restClient = (RestClient) this.restClientProvider.get();
    }

    private EventHandler_MembersInjector(Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        if ($assertionsDisabled || databaseProvider != null) {
            this.databaseProvider = databaseProvider;
            if ($assertionsDisabled || prefsWrapperProvider != null) {
                this.prefsWrapperProvider = prefsWrapperProvider;
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

    public static MembersInjector<EventHandler> create(Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        return new EventHandler_MembersInjector(databaseProvider, prefsWrapperProvider, restClientProvider);
    }
}
