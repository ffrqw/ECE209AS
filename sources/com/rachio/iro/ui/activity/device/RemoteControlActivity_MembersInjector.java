package com.rachio.iro.ui.activity.device;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class RemoteControlActivity_MembersInjector implements MembersInjector<RemoteControlActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!RemoteControlActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        RemoteControlActivity remoteControlActivity = (RemoteControlActivity) obj;
        if (remoteControlActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        BaseActivity_MembersInjector.injectTracker(remoteControlActivity, this.trackerProvider);
        BaseActivity_MembersInjector.injectDatabase(remoteControlActivity, this.databaseProvider);
        BaseActivity_MembersInjector.injectPrefsWrapper(remoteControlActivity, this.prefsWrapperProvider);
        remoteControlActivity.database = (Database) this.databaseProvider.get();
        remoteControlActivity.restClient = (RestClient) this.restClientProvider.get();
    }

    private RemoteControlActivity_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        if ($assertionsDisabled || trackerProvider != null) {
            this.trackerProvider = trackerProvider;
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
        throw new AssertionError();
    }

    public static MembersInjector<RemoteControlActivity> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        return new RemoteControlActivity_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider, restClientProvider);
    }
}
