package com.rachio.iro.ui.activity.user;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class PasswordResetActivity_MembersInjector implements MembersInjector<PasswordResetActivity> {
    static final /* synthetic */ boolean $assertionsDisabled = (!PasswordResetActivity_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;
    private final Provider<Tracker> trackerProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        PasswordResetActivity passwordResetActivity = (PasswordResetActivity) obj;
        if (passwordResetActivity == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        BaseActivity_MembersInjector.injectTracker(passwordResetActivity, this.trackerProvider);
        BaseActivity_MembersInjector.injectDatabase(passwordResetActivity, this.databaseProvider);
        BaseActivity_MembersInjector.injectPrefsWrapper(passwordResetActivity, this.prefsWrapperProvider);
        passwordResetActivity.restClient = (RestClient) this.restClientProvider.get();
    }

    private PasswordResetActivity_MembersInjector(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
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

    public static MembersInjector<PasswordResetActivity> create(Provider<Tracker> trackerProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        return new PasswordResetActivity_MembersInjector(trackerProvider, databaseProvider, prefsWrapperProvider, restClientProvider);
    }
}
