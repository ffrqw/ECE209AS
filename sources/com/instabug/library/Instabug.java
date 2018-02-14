package com.instabug.library;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Patterns;
import android.view.TextureView;
import android.view.View;
import com.google.android.gms.maps.GoogleMap;
import com.instabug.library.Feature.State;
import com.instabug.library.internal.d.a.f;
import com.instabug.library.internal.layer.CapturableView;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.n;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class Instabug {
    private static boolean DEBUG = false;
    private static Instabug INSTANCE = null;
    public static final String SDK_LEVEL = "SDK Level ";
    public static final String SDK_VERSION = "2.6.4-SNA";
    public static final boolean VERBOSE = false;
    private i delegate;
    private a internalDelegate;

    public static class Builder {
        private Application application;
        private String applicationToken;
        private boolean commentFieldRequired;
        private com.instabug.library.internal.module.a component;
        private State consoleLogState;
        private State crashReportingState;
        private boolean emailFieldRequired;
        private IBGFloatingButtonEdge floatingButtonEdge;
        private int floatingButtonOffsetFromTop;
        private State inAppMessagingState;
        private Locale instabugLocale;
        private State instabugLogState;
        private IBGInvocationEvent invocationEvent;
        private IBGInvocationMode invocationMode;
        private int notificationIcon;
        private State pushNotificationState;
        private float shakingThreshold;
        private boolean shouldPlaySounds;
        private boolean shouldShowIntroDialog;
        private IBGColorTheme theme;
        private State trackingUserStepsState;
        private State userDataState;
        private boolean willTakeScreenshot;

        public Builder(Application application, String str) {
            this(application, str, IBGInvocationEvent.IBGInvocationEventShake);
        }

        public Builder(Application application, String str, IBGInvocationEvent iBGInvocationEvent) {
            this.theme = IBGColorTheme.IBGColorThemeLight;
            this.invocationEvent = IBGInvocationEvent.IBGInvocationEventShake;
            this.userDataState = InstabugFeaturesManager.DEFAULT_FEATURE_STATE;
            this.consoleLogState = InstabugFeaturesManager.DEFAULT_FEATURE_STATE;
            this.instabugLogState = InstabugFeaturesManager.DEFAULT_FEATURE_STATE;
            this.inAppMessagingState = InstabugFeaturesManager.DEFAULT_FEATURE_STATE;
            this.crashReportingState = InstabugFeaturesManager.DEFAULT_FEATURE_STATE;
            this.pushNotificationState = InstabugFeaturesManager.DEFAULT_FEATURE_STATE;
            this.trackingUserStepsState = InstabugFeaturesManager.DEFAULT_FEATURE_STATE;
            this.emailFieldRequired = true;
            this.willTakeScreenshot = true;
            this.commentFieldRequired = false;
            this.shouldShowIntroDialog = true;
            this.shouldPlaySounds = false;
            this.floatingButtonEdge = IBGFloatingButtonEdge.Right;
            this.invocationMode = IBGInvocationMode.IBGInvocationModeNA;
            this.shakingThreshold = -1.0f;
            this.floatingButtonOffsetFromTop = -1;
            this.instabugLocale = Locale.getDefault();
            this.application = application;
            this.invocationEvent = iBGInvocationEvent;
            this.applicationToken = str;
            this.component = new com.instabug.library.internal.module.a();
        }

        public Builder setDebugEnabled(boolean z) {
            Instabug.DEBUG = z;
            return this;
        }

        public Builder setInvocationEvent(IBGInvocationEvent iBGInvocationEvent) {
            this.invocationEvent = iBGInvocationEvent;
            return this;
        }

        public Builder setShouldPlayConversationSounds(boolean z) {
            this.shouldPlaySounds = z;
            return this;
        }

        public Builder setDefaultInvocationMode(IBGInvocationMode iBGInvocationMode) {
            this.invocationMode = iBGInvocationMode;
            return this;
        }

        public Builder setShakingThreshold(float f) {
            this.shakingThreshold = f;
            return this;
        }

        public Builder setShouldShowIntroDialog(boolean z) {
            this.shouldShowIntroDialog = z;
            return this;
        }

        public Builder setTrackingUserStepsState(State state) {
            this.trackingUserStepsState = state;
            return this;
        }

        public Builder setPushNotificationState(State state) {
            this.pushNotificationState = state;
            return this;
        }

        public Builder setConsoleLogState(State state) {
            this.consoleLogState = state;
            return this;
        }

        public Builder setCrashReportingState(State state) {
            this.crashReportingState = state;
            return this;
        }

        public Builder setInstabugLogState(State state) {
            this.instabugLogState = state;
            return this;
        }

        public Builder setUserDataState(State state) {
            this.userDataState = state;
            return this;
        }

        public Builder setInAppMessagingState(State state) {
            this.inAppMessagingState = state;
            return this;
        }

        public Builder setCommentFieldRequired(boolean z) {
            this.commentFieldRequired = z;
            return this;
        }

        public Builder setEmailFieldRequired(boolean z) {
            this.emailFieldRequired = z;
            return this;
        }

        public Builder setWillTakeScreenshot(boolean z) throws IllegalStateException {
            this.willTakeScreenshot = z;
            return this;
        }

        public Builder setColorTheme(IBGColorTheme iBGColorTheme) {
            this.theme = iBGColorTheme;
            return this;
        }

        public Builder setFloatingButtonEdge(IBGFloatingButtonEdge iBGFloatingButtonEdge) {
            this.floatingButtonEdge = iBGFloatingButtonEdge;
            return this;
        }

        public Builder setFloatingButtonOffsetFromTop(int i) {
            this.floatingButtonOffsetFromTop = i;
            return this;
        }

        public Builder setLocale(Locale locale) {
            this.instabugLocale = locale;
            return this;
        }

        public Builder setNotificationIcon(int i) {
            this.notificationIcon = i;
            return this;
        }

        public Instabug build(State state) {
            boolean z = state == State.ENABLED;
            InstabugSDKLogger.i(this, "Initializing Instabug v2.6.4-SNA");
            InstabugSDKLogger.v(this, "Setting user data feature state " + this.userDataState);
            InstabugFeaturesManager.getInstance().setFeatureState(Feature.USER_DATA, this.userDataState);
            InstabugSDKLogger.v(this, "Setting console log feature state " + this.consoleLogState);
            InstabugFeaturesManager.getInstance().setFeatureState(Feature.CONSOLE_LOGS, this.consoleLogState);
            InstabugSDKLogger.v(this, "Setting Instabug logs feature state " + this.instabugLogState);
            InstabugFeaturesManager.getInstance().setFeatureState(Feature.INSTABUG_LOGS, this.instabugLogState);
            InstabugSDKLogger.v(this, "Setting crash reporting feature state " + this.crashReportingState);
            InstabugFeaturesManager.getInstance().setFeatureState(Feature.CRASH_REPORTING, this.crashReportingState);
            InstabugSDKLogger.v(this, "Setting in-app messaging feature state " + this.inAppMessagingState);
            InstabugFeaturesManager.getInstance().setFeatureState(Feature.IN_APP_MESSAGING, this.inAppMessagingState);
            InstabugSDKLogger.v(this, "Setting push notification feature state " + this.pushNotificationState);
            InstabugFeaturesManager.getInstance().setFeatureState(Feature.PUSH_NOTIFICATION, this.pushNotificationState);
            InstabugSDKLogger.v(this, "Setting tracking user steps feature state " + this.trackingUserStepsState);
            InstabugFeaturesManager.getInstance().setFeatureState(Feature.TRACK_USER_STEPS, this.trackingUserStepsState);
            InstabugSDKLogger.v(this, "Setting instabug overall state " + z);
            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.INSTABUG, z);
            i iVar = new i(this.application, this.component, this.applicationToken);
            Instabug.INSTANCE = new Instabug(iVar);
            s.h(this.shouldShowIntroDialog);
            q.a().j().append("\nsetShouldShowIntroDialog(").append(this.shouldShowIntroDialog).append(");");
            InstabugSDKLogger.v(this, "Setting show intro dialog " + this.shouldShowIntroDialog);
            if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
                iVar.e();
            } else {
                iVar.a(b.DISABLED);
            }
            q.a().j().append("\nsetUserDataEnabled(").append(this.userDataState).append(");");
            q.a().j().append("\nisInstabugEnabled(").append(z).append(");");
            q.a().j().append("\nsetConsoleLogEnabled(").append(this.consoleLogState).append(");");
            q.a().j().append("\nsetInstabugLogEnabled(").append(this.instabugLogState).append(");");
            q.a().j().append("\nsetCrashReportingState(").append(this.crashReportingState).append(");");
            q.a().j().append("\nsetInAppMessagingState(").append(this.inAppMessagingState).append(");");
            q.a().j().append("\nsetTrackingUserStepsState(").append(this.trackingUserStepsState).append(");");
            q.a().j().append("\nsetPushNotificationsEnabled(").append(this.pushNotificationState).append(");");
            i.a(this.instabugLocale);
            q.a().j().append("\nsetLocale(").append(this.instabugLocale).append(");");
            InstabugSDKLogger.v(this, "Setting Instabug locale to " + this.instabugLocale);
            s.f(this.shouldPlaySounds);
            q.a().j().append("\nsetShouldPlayConversationSounds(").append(this.shouldPlaySounds).append(");");
            InstabugSDKLogger.v(this, "Setting conversation sounds should play to " + this.shouldPlaySounds);
            i.a(this.invocationEvent);
            q.a().j().append("\nsetInvocationEvent(").append(this.invocationEvent.toString()).append(");");
            InstabugSDKLogger.v(this, "Setting invocation event " + this.invocationEvent);
            iVar.a(this.invocationMode);
            q.a().j().append("\nsetDefaultInvocationMode(").append(this.invocationMode.toString()).append(");");
            InstabugSDKLogger.v(this, "Setting invocation mode " + this.invocationMode);
            if (this.shakingThreshold != -1.0f) {
                iVar.a(this.shakingThreshold);
                q.a().j().append("\nsetShakingThreshold(").append(this.shakingThreshold).append(");");
                InstabugSDKLogger.v(this, "Setting shaking threshold " + this.shakingThreshold);
            }
            if (this.theme == IBGColorTheme.IBGColorThemeDark) {
                iVar.b(-14474200);
                iVar.c(-2039325);
                s.a().a(IBGColorTheme.IBGColorThemeDark);
            } else {
                iVar.b(-2039325);
                iVar.c(-14474200);
                s.a().a(IBGColorTheme.IBGColorThemeLight);
            }
            q.a().j().append("\nsetColorTheme(").append(this.theme).append(");");
            InstabugSDKLogger.v(this, "Setting color theme " + this.theme);
            s.i(this.emailFieldRequired);
            q.a().j().append("\nsetEmailFieldRequired(").append(this.emailFieldRequired).append(");");
            InstabugSDKLogger.v(this, "Setting email field required " + this.emailFieldRequired);
            s.j(this.willTakeScreenshot);
            q.a().j().append("\nsetWillTakeScreenshot(").append(this.emailFieldRequired).append(");");
            InstabugSDKLogger.v(this, "Setting will take screenshot " + this.willTakeScreenshot);
            iVar.g(this.commentFieldRequired);
            q.a().j().append("\nsetCommentFieldRequired(").append(this.commentFieldRequired).append(");");
            InstabugSDKLogger.v(this, "Setting comment field required " + this.commentFieldRequired);
            iVar.a(this.floatingButtonEdge);
            q.a().j().append("\nsetFloatingButtonEdge(").append(this.floatingButtonEdge).append(");");
            InstabugSDKLogger.v(this, "Setting floating button edge " + this.floatingButtonEdge);
            if (this.floatingButtonOffsetFromTop != -1) {
                iVar.a(this.floatingButtonOffsetFromTop);
                q.a().j().append("\nsetFloatingButtonOffsetFromTop(").append(this.floatingButtonOffsetFromTop).append(");");
                InstabugSDKLogger.v(this, "Setting floating button offset from top " + this.floatingButtonOffsetFromTop);
            }
            s.c(this.notificationIcon);
            q.a().j().append("\nsetNotificationIcon(").append(this.notificationIcon).append(");");
            InstabugSDKLogger.v(this, "notification icon: " + this.notificationIcon);
            return Instabug.INSTANCE;
        }

        public Instabug build() {
            return build(State.ENABLED);
        }
    }

    public class a {
        final /* synthetic */ Instabug a;

        public a(Instabug instabug) {
            this.a = instabug;
        }

        final w b() {
            return this.a.delegate.v();
        }
    }

    private Instabug(i iVar) {
        this.delegate = iVar;
        this.internalDelegate = new a(this);
    }

    static Instabug getInstance() throws IllegalStateException {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        throw new IllegalStateException("Instabug getInstance called before Instabug.Builder().build() was called");
    }

    static u getSettingsBundle() throws IllegalStateException {
        getInstance();
        return u.a();
    }

    static a iG() throws IllegalStateException {
        return getInstance().internalDelegate;
    }

    static Application getApplication() throws IllegalStateException {
        return getInstance().delegate.m();
    }

    static boolean isCommentFieldRequired() throws IllegalStateException {
        return getInstance().delegate.y();
    }

    static void onSessionFinished() throws IllegalStateException {
        getInstance().delegate.p();
    }

    static void notifyDelegateActivityStarted(Activity activity) throws IllegalStateException {
        getInstance().delegate.a(activity);
    }

    static void notifyDelegateActivityResumed(Activity activity) throws IllegalStateException {
        getInstance().delegate.b(activity);
    }

    static void notifyDelegateActivityPaused(Activity activity) throws IllegalStateException {
        getInstance().delegate.c(activity);
    }

    static void notifyDelegateActivityStopped(Activity activity) throws IllegalStateException {
        getInstance().delegate.d(activity);
    }

    static void notifyDelegateActivityDestroyed(Activity activity) throws IllegalStateException {
        getInstance().delegate.e(activity);
    }

    static void setSDKState(b bVar) throws IllegalStateException {
        getInstance().delegate.a(bVar);
    }

    static void setShouldAudioRecordingOptionAppear(boolean z) throws IllegalStateException {
        getInstance();
        s.e(z);
    }

    static boolean shouldAudioRecordingOptionAppear() throws IllegalStateException {
        getInstance();
        return s.n();
    }

    public static void notifyActivityStarted(Activity activity) throws IllegalStateException {
        if (VERSION.SDK_INT < 14) {
            notifyDelegateActivityStarted(activity);
        }
    }

    public static void notifyActivityResumed(Activity activity) throws IllegalStateException {
        if (VERSION.SDK_INT < 14) {
            notifyDelegateActivityResumed(activity);
        }
    }

    public static void notifyActivityPaused(Activity activity) throws IllegalStateException {
        if (VERSION.SDK_INT < 14) {
            notifyDelegateActivityResumed(activity);
        }
    }

    public static void notifyActivityStopped(Activity activity) throws IllegalStateException {
        if (VERSION.SDK_INT < 14) {
            notifyDelegateActivityStopped(activity);
        }
    }

    public static void notifyActivityDestroyed(Activity activity) throws IllegalStateException {
        if (VERSION.SDK_INT < 14) {
            notifyDelegateActivityStarted(activity);
        }
    }

    public static void changeInvocationEvent(IBGInvocationEvent iBGInvocationEvent) throws IllegalStateException {
        getInstance();
        i.a(iBGInvocationEvent);
        getInstance();
        q.a().j().append("\nsetInvocationEvent(").append(iBGInvocationEvent.toString()).append(");");
    }

    public static void setDialog(Dialog dialog) throws IllegalStateException {
        getInstance().delegate.a(dialog);
        getInstance();
        q.a().j().append("\nsetDialog();");
    }

    public static void addMapView(View view, GoogleMap googleMap) throws IllegalStateException {
        n.a().a(view, googleMap);
        getInstance();
        q.a().j().append("\naddMapView();");
    }

    public static void addCapturableView(CapturableView capturableView) throws IllegalStateException {
        n.a().a(capturableView);
        getInstance();
        q.a().j().append("\naddCapturableView();");
    }

    public static void setGLSurfaceView(GLSurfaceView gLSurfaceView) throws IllegalStateException {
        getInstance().delegate.a(gLSurfaceView);
        getInstance();
        q.a().j().append("\nsetGLSurfaceView();");
    }

    public static void setTextureView(TextureView textureView) throws IllegalStateException {
        getInstance().delegate.a(textureView);
        getInstance();
        q.a().j().append("\nsetTextureView();");
    }

    public static void invoke() throws IllegalStateException {
        getInstance().delegate.q();
        getInstance();
        q.a().j().append("\ninvoke();");
    }

    public static void invoke(IBGInvocationMode iBGInvocationMode) throws IllegalStateException {
        if (iBGInvocationMode == IBGInvocationMode.IBGInvocationModeFeedbackSender) {
            getInstance().delegate.s();
        } else if (iBGInvocationMode == IBGInvocationMode.IBGInvocationModeBugReporter) {
            getInstance().delegate.r();
        } else {
            getInstance().delegate.q();
        }
        getInstance();
        q.a().j().append("\ninvoke(IBGInvocationMode);");
    }

    public static void invokeConversations() throws IllegalStateException {
        getInstance();
        q.a().j().append("\ninvokeConversations();");
        getInstance().delegate.t();
    }

    public static void log(String str) throws IllegalStateException {
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.INSTABUG_LOGS) == State.ENABLED) {
            m.a(str);
            getInstance();
            q.a().j().append("\nlog();");
        }
    }

    public static void clearLog() throws IllegalStateException {
        m.b();
        getInstance();
        q.a().j().append("\nclearLog();");
    }

    public static void showIntroMessage() throws IllegalStateException {
        getInstance().delegate.g();
        getInstance();
        q.a().j().append("\nshowIntroMessage();");
    }

    public static void reportException(Throwable th) throws IllegalStateException {
        reportException(th, null);
    }

    public static void reportException(Throwable th, String str) throws IllegalStateException {
        getInstance();
        q.a().j().append("\nreportException(...);");
        getInstance().delegate.a(th, str);
    }

    public static void setPrimaryColor(int i) throws IllegalStateException {
        getInstance();
        s.b(i);
    }

    public static int getPrimaryColor() throws IllegalStateException {
        getInstance();
        return s.a().j();
    }

    public static IBGColorTheme getColorTheme() throws IllegalStateException {
        getInstance();
        return s.k();
    }

    public static void setUserData(String str) throws IllegalStateException {
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.USER_DATA) == State.ENABLED) {
            getInstance();
            s.e(str);
            getInstance();
            q.a().j().append("\nsetUserData(...);");
        }
    }

    public static void setFileAttachment(Uri uri, String str) throws IllegalStateException {
        getInstance();
        q.a().a(uri);
        q.a().a(str);
        getInstance();
        q.a().j().append("\nsetFileAttachment();");
    }

    public static void setPreSendingRunnable(Runnable runnable) throws IllegalStateException {
        getInstance();
        q.a().a(runnable);
        getInstance();
        q.a().j().append("\nsetPreSendingRunnable();");
    }

    public static void setOnSdkInvokedCallback(OnSdkInvokedCallback onSdkInvokedCallback) throws IllegalStateException {
        getInstance();
        q.a().a(onSdkInvokedCallback);
        getInstance();
        q.a().j().append("\nsetOnSdkInvokedCallback();");
    }

    public static void setOnSdkDismissedCallback(OnSdkDismissedCallback onSdkDismissedCallback) throws IllegalStateException {
        getInstance();
        q.a().a(onSdkDismissedCallback);
        getInstance();
        q.a().j().append("\nsetOnSdkDismissedCallback();");
    }

    public static String getUserData() throws IllegalStateException {
        getInstance();
        return s.q();
    }

    public static String getUsername() throws IllegalStateException {
        getSettingsBundle();
        return s.m();
    }

    public static String getUserEmail() throws IllegalStateException {
        getSettingsBundle();
        return s.c();
    }

    public static String getAppToken() throws IllegalStateException {
        getSettingsBundle();
        return s.b();
    }

    public static void setUserEmail(String str) {
        i iVar = getInstance().delegate;
        if (str == null || str.equals("")) {
            InstabugSDKLogger.d(iVar, "Email set to empty string, enabling user input of email");
            s.g(true);
            s.b("");
        } else if (!s.t() || Patterns.EMAIL_ADDRESS.matcher(str).matches()) {
            s.g(false);
            s.b(str);
        } else {
            InstabugSDKLogger.w(iVar, "Invalid email " + str + " passed to setUserEmail, ignoring.");
        }
        getSettingsBundle();
        q.a().j().append("\nsetUserEmail(").append(str).append(");");
        InstabugSDKLogger.v(Instabug.class, "Setting user email " + str);
    }

    public static void setUsername(String str) {
        getInstance();
        s.c(str);
        getSettingsBundle();
        q.a().j().append("\nsetUsername(").append(str).append(");");
        InstabugSDKLogger.v(Instabug.class, "Setting username " + str);
    }

    public static boolean isSDKInvoked() throws IllegalStateException {
        return getInstance().delegate.x().equals(b.INVOKED);
    }

    public static boolean isDebugEnabled() {
        return DEBUG;
    }

    public static boolean isEnabled() {
        return InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG);
    }

    public static void enable() throws IllegalStateException {
        InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.INSTABUG, true);
        getInstance().delegate.e();
    }

    public static void disable() throws IllegalStateException {
        InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.INSTABUG, false);
        getInstance().delegate.h();
    }

    public static void changeLocale(Locale locale) throws IllegalStateException {
        getInstance();
        i.a(locale);
    }

    public static Locale getLocale() throws IllegalStateException {
        getInstance();
        return q.a().f();
    }

    public static int getUnreadMessagesCount() throws IllegalStateException {
        return f.f();
    }

    public static void dismiss() {
        getInstance().delegate.j();
    }

    public static void addTags(String... strArr) {
        getInstance();
        q.a().a(strArr);
        getSettingsBundle();
        q.a().j().append("\naddTags(").append(Arrays.toString(strArr)).append(");");
    }

    public static ArrayList<String> getTags() {
        getSettingsBundle();
        q.a().j().append("\ngetTags();");
        getInstance();
        return q.a().l();
    }

    public static void resetTags() {
        getInstance();
        q.a().m();
        getSettingsBundle();
        q.a().j().append("\nresetTags();");
    }

    public static boolean isInstabugNotification(Bundle bundle) {
        return getInstance().delegate.b(bundle);
    }

    public static boolean isInstabugNotification(Map<String, String> map) {
        return getInstance().delegate.b((Map) map);
    }

    public static void showNotification(Bundle bundle) {
        getInstance().delegate.a(bundle);
    }

    public static void showNotification(Map<String, String> map) {
        getInstance().delegate.a((Map) map);
    }

    public static void setPushNotificationRegistrationToken(String str) {
        s.f(str);
    }

    public static void setCustomTextPlaceHolders(IBGCustomTextPlaceHolder iBGCustomTextPlaceHolder) {
        q.a().a(iBGCustomTextPlaceHolder);
        getSettingsBundle();
        q.a().j().append("\nsetCustomTextPlaceHolders();");
    }
}
