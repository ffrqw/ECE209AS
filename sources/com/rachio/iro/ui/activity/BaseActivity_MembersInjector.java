package com.rachio.iro.ui.activity;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.model.db.Database;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class BaseActivity_MembersInjector implements MembersInjector<BaseActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!BaseActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        BaseActivity baseActivity = (BaseActivity) obj;
        if (baseActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        baseActivity.tracker = (Tracker) this.trackerProvider.get();
        baseActivity.database = (Database) this.databaseProvider.get();
        baseActivity.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
    }

    private BaseActivity_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
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

    public static MembersInjector<BaseActivity> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        return new BaseActivity_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider);
    }

    public static void injectTracker(BaseActivity instance, Provider<Tracker> trackerProvider) {
        instance.tracker = (Tracker) trackerProvider.get();
    }

    public static void injectDatabase(BaseActivity instance, Provider<Database> databaseProvider) {
        instance.database = (Database) databaseProvider.get();
    }

    public static void injectPrefsWrapper(BaseActivity instance, Provider<PrefsWrapper> prefsWrapperProvider) {
        instance.prefsWrapper = (PrefsWrapper) prefsWrapperProvider.get();
    }
}
