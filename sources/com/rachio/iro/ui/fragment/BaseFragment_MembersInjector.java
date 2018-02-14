package com.rachio.iro.ui.fragment;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.model.db.Database;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class BaseFragment_MembersInjector implements MembersInjector<BaseFragment> {
    static final /* synthetic */ boolean $assertionsDisabled = (!BaseFragment_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        BaseFragment baseFragment = (BaseFragment) obj;
        if (baseFragment == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        baseFragment.tracker = (Tracker) this.trackerProvider.get();
        baseFragment.database = (Database) this.databaseProvider.get();
        baseFragment.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
    }

    private BaseFragment_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
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

    public static MembersInjector<BaseFragment> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        return new BaseFragment_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider);
    }
}
