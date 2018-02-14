package com.rachio.iro.ui.activity.zone;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class ZoneDetailsActivity_MembersInjector implements MembersInjector<ZoneDetailsActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!ZoneDetailsActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        ZoneDetailsActivity zoneDetailsActivity = (ZoneDetailsActivity) obj;
        if (zoneDetailsActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        BaseActivity_MembersInjector.injectTracker(zoneDetailsActivity, this.trackerProvider);
        BaseActivity_MembersInjector.injectDatabase(zoneDetailsActivity, this.databaseProvider);
        BaseActivity_MembersInjector.injectPrefsWrapper(zoneDetailsActivity, this.prefsWrapperProvider);
    }

    private ZoneDetailsActivity_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        if ($assertionsDisabled || trackerProvider != null) {
            this.trackerProvider = trackerProvider;
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

    public static MembersInjector<ZoneDetailsActivity> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        return new ZoneDetailsActivity_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider);
    }
}
