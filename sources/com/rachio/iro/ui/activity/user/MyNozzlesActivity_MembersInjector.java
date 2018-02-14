package com.rachio.iro.ui.activity.user;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class MyNozzlesActivity_MembersInjector implements MembersInjector<MyNozzlesActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!MyNozzlesActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        MyNozzlesActivity myNozzlesActivity = (MyNozzlesActivity) obj;
        if (myNozzlesActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        BaseActivity_MembersInjector.injectTracker(myNozzlesActivity, this.trackerProvider);
        BaseActivity_MembersInjector.injectDatabase(myNozzlesActivity, this.databaseProvider);
        BaseActivity_MembersInjector.injectPrefsWrapper(myNozzlesActivity, this.prefsWrapperProvider);
        myNozzlesActivity.restClient = (RestClient) this.restClientProvider.get();
    }

    private MyNozzlesActivity_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
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

    public static MembersInjector<MyNozzlesActivity> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        return new MyNozzlesActivity_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider, restClientProvider);
    }
}
