package com.rachio.iro;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.gcm.GcmReceiver;
import com.google.android.gms.security.ProviderInstaller;
import com.instabug.library.Feature.State;
import com.instabug.library.IBGInvocationEvent;
import com.instabug.library.Instabug;
import com.instabug.library.Instabug.Builder;
import com.rachio.iro.IroComponent.Initializer;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.gen2.MrvlProvService;
import com.rachio.iro.model.db.Database;
import io.fabric.sdk.android.Fabric;

public final class IroApplication extends MultiDexApplication {
    public static boolean DANIELMODE = false;
    private static final String TAG = IroApplication.class.getCanonicalName();
    public static int VERSIONCODE;
    private IroGraph component;
    Database database;
    public Instabug instabug;
    PrefsWrapper prefsWrapper;
    RestClient restClient;

    public final void onCreate() {
        super.onCreate();
        try {
            getPackageManager().setComponentEnabledSetting(new ComponentName(this, GcmReceiver.class), 2, 1);
        } catch (IllegalArgumentException e) {
        }
        try {
            VERSIONCODE = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e2) {
        }
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e3) {
        } catch (GooglePlayServicesNotAvailableException e4) {
        }
        this.component = Initializer.init(this);
        this.component.inject(this);
        Fabric.with(this, new Crashlytics());
        this.instabug = new Builder(this, "36fa0b174cbc944f5b6d5381e889be79").setInvocationEvent(IBGInvocationEvent.IBGInvocationEventNone).setShouldShowIntroDialog(false).setCrashReportingState(State.DISABLED).build();
        Crashlytics.setBool("debugbuild", false);
        Crashlytics.setString("githash", getGitHash());
        Crashlytics.setLong("maxmemory", Runtime.getRuntime().maxMemory());
        Crashlytics.setBool("danielmode", false);
        MrvlProvService.cleanUp(this);
    }

    public final IroGraph component() {
        return this.component;
    }

    public static IroApplication get(Context context) {
        return (IroApplication) context.getApplicationContext();
    }

    public final PrefsWrapper getPrefsWrapper() {
        return this.prefsWrapper;
    }

    @Deprecated
    public final RestClient getRestClient() {
        return this.restClient;
    }

    @Deprecated
    public final Database getDatabase() {
        return this.database;
    }

    private static String getGitHash() {
        try {
            return (String) BuildConfig.class.getField("GITHASH").get(null);
        } catch (Exception e) {
            return "";
        }
    }
}
