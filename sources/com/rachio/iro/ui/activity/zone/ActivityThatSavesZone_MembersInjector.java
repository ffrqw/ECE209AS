package com.rachio.iro.ui.activity.zone;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class ActivityThatSavesZone_MembersInjector implements MembersInjector<ActivityThatSavesZone> {
    static final /* synthetic */ boolean $assertionsDisabled = (!ActivityThatSavesZone_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        ActivityThatSavesZone activityThatSavesZone = (ActivityThatSavesZone) obj;
        if (activityThatSavesZone == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        BaseActivity_MembersInjector.injectTracker(activityThatSavesZone, this.trackerProvider);
        BaseActivity_MembersInjector.injectDatabase(activityThatSavesZone, this.databaseProvider);
        BaseActivity_MembersInjector.injectPrefsWrapper(activityThatSavesZone, this.prefsWrapperProvider);
    }
}
