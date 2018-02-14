package com.crashlytics.android;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.beta.Beta;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.KitGroup;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Crashlytics extends Kit<Void> implements KitGroup {
    public final Answers answers;
    public final Beta beta;
    public final CrashlyticsCore core;
    public final Collection<? extends Kit> kits;

    protected final /* bridge */ /* synthetic */ Object doInBackground() {
        return null;
    }

    public Crashlytics() {
        this(new Answers(), new Beta(), new CrashlyticsCore());
    }

    private Crashlytics(Answers answers, Beta beta, CrashlyticsCore core) {
        this.answers = answers;
        this.beta = beta;
        this.core = core;
        this.kits = Collections.unmodifiableCollection(Arrays.asList(new Kit[]{answers, beta, core}));
    }

    public final String getVersion() {
        return "2.6.8.dev";
    }

    public final String getIdentifier() {
        return "com.crashlytics.sdk.android:crashlytics";
    }

    public final Collection<? extends Kit> getKits() {
        return this.kits;
    }

    private static Crashlytics getInstance() {
        return (Crashlytics) Fabric.getKit(Crashlytics.class);
    }

    public static void logException(Throwable throwable) {
        checkInitialized();
        getInstance().core.logException(throwable);
    }

    public static void log(int priority, String tag, String msg) {
        checkInitialized();
        getInstance().core.log(3, tag, msg);
    }

    public static void setUserIdentifier(String identifier) {
        checkInitialized();
        getInstance().core.setUserIdentifier(identifier);
    }

    public static void setUserName(String name) {
        checkInitialized();
        getInstance().core.setUserName(name);
    }

    public static void setString(String key, String value) {
        checkInitialized();
        getInstance().core.setString(key, value);
    }

    public static void setBool(String key, boolean value) {
        checkInitialized();
        getInstance().core.setString(key, Boolean.toString(value));
    }

    public static void setLong(String key, long value) {
        checkInitialized();
        getInstance().core.setString(key, Long.toString(value));
    }

    private static void checkInitialized() {
        if (getInstance() == null) {
            throw new IllegalStateException("Crashlytics must be initialized by calling Fabric.with(Context) prior to calling Crashlytics.getInstance()");
        }
    }
}
