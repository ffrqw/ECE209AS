package com.rachio.iro.ui.activity;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.model.db.Database;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class BaseCloudActivity_MembersInjector implements MembersInjector<BaseCloudActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!BaseCloudActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        BaseCloudActivity baseCloudActivity = (BaseCloudActivity) obj;
        if (baseCloudActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        baseCloudActivity.tracker = (Tracker) this.trackerProvider.get();
        baseCloudActivity.database = (Database) this.databaseProvider.get();
        baseCloudActivity.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
    }
}
