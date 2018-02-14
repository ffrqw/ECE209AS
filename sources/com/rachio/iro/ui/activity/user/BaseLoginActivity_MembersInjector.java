package com.rachio.iro.ui.activity.user;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class BaseLoginActivity_MembersInjector implements MembersInjector<BaseLoginActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!BaseLoginActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        BaseLoginActivity baseLoginActivity = (BaseLoginActivity) obj;
        if (baseLoginActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        BaseActivity_MembersInjector.injectTracker(baseLoginActivity, this.trackerProvider);
        BaseActivity_MembersInjector.injectDatabase(baseLoginActivity, this.databaseProvider);
        BaseActivity_MembersInjector.injectPrefsWrapper(baseLoginActivity, this.prefsWrapperProvider);
    }
}
