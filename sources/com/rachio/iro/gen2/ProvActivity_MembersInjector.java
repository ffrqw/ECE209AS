package com.rachio.iro.gen2;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class ProvActivity_MembersInjector implements MembersInjector<ProvActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!ProvActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        ProvActivity provActivity = (ProvActivity) obj;
        if (provActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        BaseActivity_MembersInjector.injectTracker(provActivity, this.trackerProvider);
        BaseActivity_MembersInjector.injectDatabase(provActivity, this.databaseProvider);
        BaseActivity_MembersInjector.injectPrefsWrapper(provActivity, this.prefsWrapperProvider);
        provActivity.database = (Database) this.databaseProvider.get();
        provActivity.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
        provActivity.restClient = (RestClient) this.restClientProvider.get();
    }

    private ProvActivity_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
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

    public static MembersInjector<ProvActivity> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        return new ProvActivity_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider, restClientProvider);
    }
}
