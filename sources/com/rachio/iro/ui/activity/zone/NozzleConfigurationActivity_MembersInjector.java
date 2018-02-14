package com.rachio.iro.ui.activity.zone;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class NozzleConfigurationActivity_MembersInjector implements MembersInjector<NozzleConfigurationActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!NozzleConfigurationActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        NozzleConfigurationActivity nozzleConfigurationActivity = (NozzleConfigurationActivity) obj;
        if (nozzleConfigurationActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        BaseActivity_MembersInjector.injectTracker(nozzleConfigurationActivity, this.trackerProvider);
        BaseActivity_MembersInjector.injectDatabase(nozzleConfigurationActivity, this.databaseProvider);
        BaseActivity_MembersInjector.injectPrefsWrapper(nozzleConfigurationActivity, this.prefsWrapperProvider);
    }

    private NozzleConfigurationActivity_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
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

    public static MembersInjector<NozzleConfigurationActivity> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        return new NozzleConfigurationActivity_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider);
    }
}
