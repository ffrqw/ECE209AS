package com.rachio.iro.ui.activity.device;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class HistoryActivity_MembersInjector implements MembersInjector<HistoryActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!HistoryActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        HistoryActivity historyActivity = (HistoryActivity) obj;
        if (historyActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        BaseActivity_MembersInjector.injectTracker(historyActivity, this.trackerProvider);
        BaseActivity_MembersInjector.injectDatabase(historyActivity, this.databaseProvider);
        BaseActivity_MembersInjector.injectPrefsWrapper(historyActivity, this.prefsWrapperProvider);
    }

    private HistoryActivity_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
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

    public static MembersInjector<HistoryActivity> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        return new HistoryActivity_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider);
    }
}
