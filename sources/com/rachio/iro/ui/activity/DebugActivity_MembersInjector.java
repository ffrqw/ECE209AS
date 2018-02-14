package com.rachio.iro.ui.activity;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.model.db.Database;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class DebugActivity_MembersInjector implements MembersInjector<DebugActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!DebugActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        DebugActivity debugActivity = (DebugActivity) obj;
        if (debugActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        debugActivity.tracker = (Tracker) this.trackerProvider.get();
        debugActivity.database = (Database) this.databaseProvider.get();
        debugActivity.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
    }

    private DebugActivity_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
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

    public static MembersInjector<DebugActivity> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        return new DebugActivity_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider);
    }
}
