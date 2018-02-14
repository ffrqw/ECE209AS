package com.instabug.library;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import com.instabug.library.Feature.State;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.c.a.e;
import com.instabug.library.c.c;
import com.instabug.library.c.d;
import com.instabug.library.gcm.InstabugGcmRegistrationIntentService;
import com.instabug.library.internal.d.a.h;
import com.instabug.library.model.IssueType;
import com.instabug.library.model.f;
import com.instabug.library.model.g;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.j;
import com.instabug.library.util.k;
import com.instabug.library.util.l;
import com.instabug.library.util.n;
import com.rachio.iro.R;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

final class i implements com.instabug.library.a.a.a, com.instabug.library.a.b.a, e, com.instabug.library.c.b.a, com.instabug.library.c.c.a, com.instabug.library.c.d.a, com.instabug.library.d.e {
    private b a;
    private Dialog b;
    private Application c;
    private Activity d;
    private GLSurfaceView e;
    private TextureView f;
    private int g;
    private WeakReference<Dialog> h;
    private d j;
    private c k;
    private boolean l = false;
    private w m;
    private f n;
    private com.instabug.library.internal.a.a o;
    private com.instabug.library.c.a.d p;
    private IBGInvocationMode q = IBGInvocationMode.IBGInvocationModeNA;
    private com.instabug.library.c.a r = new com.instabug.library.c.a(this);
    private final com.instabug.library.a.b s = new com.instabug.library.a.b(this);
    private final com.instabug.library.a.a t = new com.instabug.library.a.a(this);

    class a {
        final /* synthetic */ i a;
        private boolean b;
        private Uri c;

        a(i iVar) {
            this.a = iVar;
        }

        public final a a(boolean z) {
            this.b = z;
            return this;
        }

        public final Uri a() {
            return this.c;
        }

        public final a a(Uri uri) {
            this.c = uri;
            return this;
        }
    }

    enum b {
        ENABLED,
        INVOKED,
        TAKING_SCREENSHOT,
        DISABLED
    }

    public i(Application application, com.instabug.library.internal.module.a aVar, String str) {
        this.c = application;
        InstabugFeaturesManager.getInstance().restoreFeaturesFromSharedPreferences(application);
        u.a(application);
        s.a(str);
        this.o = new com.instabug.library.internal.a.a();
        this.m = aVar.b();
        this.n = f.a(u.a(), this.m);
        Thread.setDefaultUncaughtExceptionHandler(aVar.a(this.n, this.o, u.a()));
        this.j = new d(new com.instabug.library.internal.c.a(), this, (SensorManager) this.c.getSystemService("sensor"));
        this.k = new c(this);
        InstabugSDKLogger.v(this, "Creating conversations disk cache");
        com.instabug.library.internal.d.a.e.a().a(new com.instabug.library.internal.d.a.i(this.c, "CONVERSATIONS_DISK_CACHE", "/conversations.cache"));
        InstabugSDKLogger.v(this, "Creating read queue disk cache");
        com.instabug.library.internal.d.a.e.a().a(new com.instabug.library.internal.d.a.i(this.c, "read_queue_disk_cache_key", "/read_queue.cache"));
        InstabugSDKLogger.v(this, "Creating issues disk cache");
        com.instabug.library.internal.d.a.e.a().a(new com.instabug.library.internal.d.a.i(this.c, "issues_disk_cache", "/issues.cache"));
        InstabugSDKLogger.v(this, "Creating sessions disk cache");
        com.instabug.library.internal.d.a.e.a().a(new com.instabug.library.internal.d.a.i(this.c, "sessions_disk_cache", "/sessions.cache"));
        com.instabug.library.c.b.a((com.instabug.library.c.b.a) this);
        h.c();
        InstabugSDKLogger.v(this, "Registering activity lifecycle listener");
        if (VERSION.SDK_INT >= 14) {
            this.c.registerActivityLifecycleCallbacks(new d());
        }
    }

    public final void a() {
        if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
            InstabugSDKLogger.d(this, "Shake detected, invoking SDK");
            M();
        }
    }

    public final void b() {
        if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
            InstabugSDKLogger.d(this, "Floating button clicked, invoking SDK");
            M();
        }
    }

    public final void a(Uri uri) {
        if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
            InstabugSDKLogger.d(this, "Screenshot taken: " + uri.getPath() + ", invoking SDK");
            if (s.u()) {
                a aVar = new a(this);
                aVar.a(true);
                aVar.a(com.instabug.library.internal.d.a.a(this.c, uri, null));
                a(aVar);
                return;
            }
            O();
        }
    }

    public final void a(boolean z) {
        InstabugSDKLogger.d(this, "SDK Invoked: " + z);
        if (this.a == b.TAKING_SCREENSHOT) {
            return;
        }
        if (z) {
            this.a = b.INVOKED;
        } else {
            this.a = b.ENABLED;
        }
    }

    public final void c() {
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.PUSH_NOTIFICATION) == State.ENABLED) {
            InstabugSDKLogger.d(this, "Last contacted at changed - GCM is enabled");
            if (!TextUtils.isEmpty(s.v())) {
                this.c.startService(new Intent(this.c, InstabugGcmRegistrationIntentService.class));
            }
        }
    }

    public final List<g> a(final List<g> list) {
        if (this.d != null) {
            InstabugSDKLogger.d(this, list.size() + " New messages received to be notified while application is active");
            this.d.runOnUiThread(new Runnable(this) {
                final /* synthetic */ i b;

                public final void run() {
                    b.a().a(this.b.o(), list);
                }
            });
        } else {
            InstabugSDKLogger.d(this, list.size() + " New messages received to be notified while application is inactive");
            b.a().a(this.c, (List) list);
        }
        return null;
    }

    final void e() {
        InstabugSDKLogger.d(this, "Starting Instabug SDK functionality");
        this.a = b.ENABLED;
        InstabugSDKLogger.v(this, "Waking up migration manager");
        com.instabug.library.migration.b.a(this.c);
        InstabugSDKLogger.v(this, "clean hanging issue if exist");
        InstabugSDKLogger.v(this, "Registering broadcasts");
        LocalBroadcastManager.getInstance(this.c).registerReceiver(this.s, new IntentFilter("SDK invoked"));
        LocalBroadcastManager.getInstance(this.c).registerReceiver(this.t, new IntentFilter("User last contact at changed"));
        InstabugSDKLogger.v(this, "Preparing In-app messaging");
        com.instabug.library.internal.d.a.e.a();
        InstabugSDKLogger.v(this, "Creating conversations disk cache");
        com.instabug.library.internal.d.a.e.a().a(new com.instabug.library.internal.d.a.i(this.c, "CONVERSATIONS_DISK_CACHE", "/conversations.cache"));
        InstabugSDKLogger.v(this, "Creating read queue disk cache");
        com.instabug.library.internal.d.a.e.a().a(new com.instabug.library.internal.d.a.i(this.c, "read_queue_disk_cache_key", "/read_queue.cache"));
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.IN_APP_MESSAGING) == State.ENABLED) {
            InstabugSDKLogger.v(this, "Starting synchronization manager");
            com.instabug.library.d.a.a a = com.instabug.library.d.a.a.a(this.c);
            InstabugSDKLogger.v(this, "Initializing synchronization manager");
            a.a();
            InstabugSDKLogger.v(this, "Checking synchronization manager state, last contact time " + s.f());
            if (s.f() != 0) {
                InstabugSDKLogger.v(this, "User has issues, starting sync right away");
                a.b();
            }
            InstabugSDKLogger.v(this, "Adding this as listener on NewMessagesHandler");
            com.instabug.library.d.d.a().a((com.instabug.library.d.e) this);
            Context context = this.c;
            if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.PUSH_NOTIFICATION) == State.ENABLED) {
                InstabugSDKLogger.d(this, "GCM is enabled");
                if (!(s.f() == 0 || TextUtils.isEmpty(s.v()))) {
                    context.startService(new Intent(context, InstabugGcmRegistrationIntentService.class));
                }
            }
        }
        InstabugSDKLogger.v(this, "Preparing Invocation listeners");
        H();
        InstabugSDKLogger.v(this, "show intro dialog if valid");
        StringBuilder append = new StringBuilder("Checking if should show intro dialog, firstRun ").append(s.e()).append(", Settings.getInstance().isShowIntroDialog() ").append(s.s()).append(" Settings.getInstance().getInitialIntroActivity() ");
        q.a();
        InstabugSDKLogger.v(this, append.append(null).toString());
        if (s.e() && s.s()) {
            q.a();
            InstabugSDKLogger.v(this, "Showing Intro dialog");
            new Handler().postDelayed(new Runnable(this) {
                final /* synthetic */ i a;

                {
                    this.a = r1;
                }

                public final void run() {
                    this.a.g();
                }
            }, 10000);
        }
        InstabugSDKLogger.v(this, "Initializing Session manager");
        this.b = null;
    }

    private void H() {
        InstabugSDKLogger.d(this, "Starting Instabug SDK invocation listeners");
        if (q.a().e() == IBGInvocationEvent.IBGInvocationEventShake) {
            InstabugSDKLogger.d(this, "Starting shake detection");
            this.j.a(true);
        } else {
            this.j.a(false);
        }
        if (q.a().e() == IBGInvocationEvent.IBGInvocationEventFloatingButton) {
            InstabugSDKLogger.d(this, "Restoring floating button");
            this.r.a(this.d);
        }
        if (q.a().e() == IBGInvocationEvent.IBGInvocationScreenshotGesture) {
            InstabugSDKLogger.d(this, "Start watching Screenshots directory");
            this.k.a();
        }
    }

    public final void g() {
        if (o() != null && !o().isFinishing()) {
            Dialog cVar;
            if (q.a().e() == IBGInvocationEvent.IBGInvocationEventTwoFingersSwipeLeft) {
                cVar = new com.instabug.library.b.c(o(), l.a(Key.SWIPE_HINT, o().getResources().getString(R.string.instabug_str_swipe_hint)));
                cVar.setCanceledOnTouchOutside(true);
                this.b = cVar;
                cVar.show();
            } else if (q.a().e() == IBGInvocationEvent.IBGInvocationEventShake) {
                cVar = new com.instabug.library.b.b(o(), l.a(Key.SHAKE_HINT, o().getResources().getString(R.string.instabug_str_shake_hint)));
                cVar.setCanceledOnTouchOutside(true);
                this.b = cVar;
                cVar.show();
            }
        }
    }

    final void h() {
        InstabugSDKLogger.d(this, "Stopping Instabug SDK functionality");
        this.a = b.DISABLED;
        InstabugSDKLogger.v(this, "Un-registering broadcasts");
        LocalBroadcastManager.getInstance(this.c).unregisterReceiver(this.s);
        LocalBroadcastManager.getInstance(this.c).unregisterReceiver(this.t);
        InstabugSDKLogger.v(this, "Stopping In-app messaging");
        InstabugSDKLogger.v(this, "Stopping synchronization manager");
        com.instabug.library.d.a.a.a(this.c).d();
        InstabugSDKLogger.v(this, "Removing this as listener from NewMessageReceiver");
        com.instabug.library.d.d.a().b(this);
        Context context = this.c;
        context.stopService(new Intent(context, InstabugGcmRegistrationIntentService.class));
        K();
        try {
            if (this.b != null && this.b.isShowing()) {
                InstabugSDKLogger.v(this, "Dismissing instabug dialog");
                this.b.dismiss();
                this.b = null;
            }
        } catch (Exception e) {
            InstabugSDKLogger.d(this, Log.getStackTraceString(e.getCause()));
        }
    }

    private void K() {
        InstabugSDKLogger.d(this, "Stopping Instabug SDK invocation listeners");
        if (q.a().e() == IBGInvocationEvent.IBGInvocationEventShake) {
            InstabugSDKLogger.d(this, "Stopping shake detection");
            this.j.a(false);
        }
        if (q.a().e() == IBGInvocationEvent.IBGInvocationEventFloatingButton) {
            InstabugSDKLogger.d(this, "Hiding floating button");
            this.r.a();
        }
        if (q.a().e() == IBGInvocationEvent.IBGInvocationScreenshotGesture) {
            InstabugSDKLogger.d(this, "Stop watching Screenshots directory");
            this.k.b();
        }
    }

    public final void j() {
        Activity o = o();
        Intent intent = new Intent(this.d, InstabugFeedbackActivity.class);
        intent.putExtra("com.instabug.library.process", 166);
        intent.addFlags(65536);
        o.startActivity(intent);
    }

    public final Application m() {
        return this.c;
    }

    final Activity o() {
        if (this.d == null || this.d.getParent() == null) {
            return this.d;
        }
        Activity parent = this.d.getParent();
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        return parent;
    }

    public final void a(Activity activity) {
        InstabugSDKLogger.d(this, activity.getClass().getName() + " onNewActivityStarted, runningActivitiesNumber:" + this.g);
        if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG) && this.g == 0) {
            if (q.a().e() == IBGInvocationEvent.IBGInvocationScreenshotGesture) {
                k.a(activity, "android.permission.WRITE_EXTERNAL_STORAGE", 1, null, null);
            }
            t.a().a(activity);
        }
        this.g++;
    }

    public final void d(Activity activity) {
        InstabugSDKLogger.d(this, activity.getClass().getName() + " onCurrentActivityStopped, runningActivitiesNumber:" + this.g);
        InstabugFeaturesManager.getInstance().saveFeaturesToSharedPreferences(activity);
        if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG) && this.g == 1) {
            t.a().b(activity);
        }
        this.g--;
    }

    public final void e(Activity activity) {
        InstabugSDKLogger.d(this, "onCurrentActivityDestroyed: " + activity.getClass().getName());
        if (activity.equals(this.d)) {
            this.d = null;
            this.b = null;
            n.a().b();
        }
    }

    final void p() {
        if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
            t.a().b(this.c);
        }
    }

    public final void q() {
        InstabugSDKLogger.d(this, "Instabug.invoke() called, invoking SDK");
        M();
    }

    public final void r() {
        InstabugSDKLogger.d(this, "Instabug.invokeBugReporter() called, invoking SDK with bug reporter mode");
        final a aVar = new a(this);
        if (s.u()) {
            aVar.a(true);
            n.a().a(o(), this.h, this.e, this.f, new n.c(this) {
                final /* synthetic */ i b;

                public final void a(Uri uri) {
                    InstabugSDKLogger.d(this, "Captured screenShot Uri: " + uri);
                    aVar.a(uri);
                    this.b.c(aVar);
                }
            });
            return;
        }
        aVar.a(false);
        c(aVar);
    }

    private void O() {
        a aVar = new a(this);
        aVar.a(false);
        a(aVar);
    }

    private void a(a aVar) {
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.TRACK_USER_STEPS) == State.ENABLED) {
            this.m.a(this.d != null ? this.d.getClass().getName() : "Unknown Activity", 2563);
        }
        if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
            switch (this.q) {
                case IBGInvocationModeNA:
                    InstabugSDKLogger.d(this, "IBGInvocationModeNA: true");
                    j.a(o());
                    if (aVar.b) {
                        o().startActivity(j.a(this.d, aVar.a(), q.a().h()));
                        return;
                    } else {
                        o().startActivity(j.a(this.d, null, q.a().h()));
                        return;
                    }
                case IBGInvocationModeBugReporter:
                    InstabugSDKLogger.d(this, "IBGInvocationModeBugReporter: true");
                    c(aVar);
                    return;
                case IBGInvocationModeFeedbackSender:
                    InstabugSDKLogger.d(this, "IBGInvocationModeFeedbackSender: true");
                    s();
                    return;
                default:
                    return;
            }
        }
    }

    private void c(a aVar) {
        j.a(o());
        if (aVar.b) {
            o().startActivity(j.a(this.d, aVar.a(), q.a().h()));
        } else {
            o().startActivity(j.a(this.d, null, q.a().h()));
        }
    }

    public final void s() {
        Activity o = o();
        Context context = this.d;
        Parcelable h = q.a().h();
        Intent intent = new Intent(context, InstabugFeedbackActivity.class);
        intent.putExtra("com.instabug.library.process", 161);
        intent.putExtra("com.instabug.library.file", h);
        intent.addFlags(65536);
        o.startActivity(intent);
    }

    final void t() {
        j.a(o());
        o().startActivity(j.a(this.d));
    }

    public final void a(Throwable th, String str) {
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.CRASH_REPORTING) != State.DISABLED) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("error", com.instabug.library.util.a.a.a(th, str));
                com.instabug.library.model.d dVar = new com.instabug.library.model.d(System.currentTimeMillis());
                dVar.b(s.c());
                dVar.c(jSONObject.toString());
                dVar.a(IssueType.CRASH);
                if (q.a().h() != null) {
                    dVar.a(com.instabug.library.internal.d.a.a(this.c, q.a().h(), q.a().i()), com.instabug.library.model.e.a.ATTACHMENT_FILE);
                    q.a().a(null);
                    q.a().a(null);
                }
                com.instabug.library.internal.a.b a = com.instabug.library.internal.a.a.a(this.c);
                if (q.a().b() != null) {
                    try {
                        q.a().b().run();
                    } catch (Throwable e) {
                        InstabugSDKLogger.e(this, "Pre sending runnable failed to run.", e);
                    }
                }
                this.n.a(dVar, m.a(), a);
                h.b(dVar);
                InstabugSDKLogger.i(this, "ReportCaughtException: Your exception has been reported");
                this.c.startService(new Intent(this.c, InstabugIssueUploaderService.class));
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
    }

    public final void a(Dialog dialog) {
        this.h = new WeakReference(dialog);
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.TRACK_USER_STEPS) == State.ENABLED) {
            this.m.a(this.h.getClass().getName(), 2564);
        }
    }

    public final void a(GLSurfaceView gLSurfaceView) {
        this.e = gLSurfaceView;
    }

    public final void a(TextureView textureView) {
        this.f = textureView;
    }

    public final w v() {
        return this.m;
    }

    public final void a(float f) {
        this.j.a(f);
    }

    public final void a(IBGInvocationMode iBGInvocationMode) {
        this.q = iBGInvocationMode;
    }

    public final void a(IBGFloatingButtonEdge iBGFloatingButtonEdge) {
        if (this.p == null) {
            this.p = new com.instabug.library.c.a.d();
            this.r.a(this.p);
        }
        this.p.c = iBGFloatingButtonEdge;
    }

    public final void a(int i) {
        if (this.p == null) {
            this.p = new com.instabug.library.c.a.d();
            this.r.a(this.p);
        }
        this.p.d = i;
    }

    public final void b(int i) {
        if (this.p == null) {
            this.p = new com.instabug.library.c.a.d();
            this.r.a(this.p);
        }
        this.p.b = i;
    }

    public final void c(int i) {
        if (this.p == null) {
            this.p = new com.instabug.library.c.a.d();
            this.r.a(this.p);
        }
        this.p.a = i;
    }

    final void a(b bVar) {
        this.a = bVar;
    }

    final b x() {
        return this.a;
    }

    public final void g(boolean z) {
        this.l = z;
    }

    public final boolean y() {
        return this.l;
    }

    public final void a(Bundle bundle) {
        InstabugSDKLogger.d(this, "Message is related to Instabug, checking Instabug GCM state");
        InstabugSDKLogger.d(this, "GCM state is " + InstabugFeaturesManager.getInstance().getFeatureState(Feature.PUSH_NOTIFICATION));
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.PUSH_NOTIFICATION) == State.ENABLED) {
            InstabugSDKLogger.d(this, "Parsing GCM response");
            try {
                b.a().a(this.c, new JSONObject(new JSONObject(bundle.getString("message")).getString("aps")).getString("alert"));
                com.instabug.library.d.a.a.a(this.c).b();
            } catch (Throwable e) {
                InstabugSDKLogger.e(this, "Parsing GCM response failed", e);
            } catch (Throwable e2) {
                InstabugSDKLogger.e(this, "Something went wrong while showing notification", e2);
            }
        }
    }

    public final void a(Map<String, String> map) {
        InstabugSDKLogger.d(this, "Message is related to Instabug, checking Instabug GCM state");
        InstabugSDKLogger.d(this, "GCM state is " + InstabugFeaturesManager.getInstance().getFeatureState(Feature.PUSH_NOTIFICATION));
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.PUSH_NOTIFICATION) == State.ENABLED) {
            InstabugSDKLogger.d(this, "Parsing GCM response");
            try {
                b.a().a(this.c, new JSONObject(new JSONObject((String) map.get("message")).getString("aps")).getString("alert"));
                com.instabug.library.d.a.a.a(this.c).b();
            } catch (Throwable e) {
                InstabugSDKLogger.e(this, "Parsing GCM response failed", e);
            } catch (Throwable e2) {
                InstabugSDKLogger.e(this, "Something went wrong while showing notification", e2);
            }
        }
    }

    public final boolean b(Bundle bundle) {
        try {
            String string = new JSONObject(bundle.getString("message")).getString("IBGHost");
            InstabugSDKLogger.d(this, "IBGHost: " + string);
            if (string != null && Boolean.parseBoolean(string)) {
                return true;
            }
        } catch (Throwable e) {
            InstabugSDKLogger.e(this, "Parsing GCM response failed", e);
        } catch (Throwable e2) {
            InstabugSDKLogger.e(this, "Something went wrong while showing notification", e2);
        }
        return false;
    }

    public final boolean b(Map<String, String> map) {
        if (map.containsKey("message")) {
            try {
                String string = new JSONObject((String) map.get("message")).getString("IBGHost");
                if (string != null && Boolean.parseBoolean(string)) {
                    return true;
                }
            } catch (Throwable e) {
                InstabugSDKLogger.e(this, "Parsing GCM response failed", e);
            } catch (Throwable e2) {
                InstabugSDKLogger.e(this, "Something went wrong while showing notification", e2);
            }
        }
        return false;
    }

    public final void a(final com.instabug.library.model.d dVar) {
        this.a = b.ENABLED;
        n.a().a(o(), this.h, this.e, this.f, new n.c(this) {
            final /* synthetic */ i b;

            public final void a(Uri uri) {
                InstabugSDKLogger.d(this, "screenShotUri: " + uri);
                dVar.a(uri, com.instabug.library.model.e.a.IMAGE);
                dVar.a("offline_issue_occurrence_id");
                h.a(dVar.d());
                i.a(this.b, dVar);
            }
        });
    }

    public final void b(Activity activity) {
        InstabugSDKLogger.d(this, this.a.toString());
        this.d = activity;
        if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
            if (this.a == b.TAKING_SCREENSHOT) {
                K();
                com.instabug.library.c.b.a((com.instabug.library.c.b.a) this).a(activity, h.d());
            } else {
                H();
            }
        }
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.TRACK_USER_STEPS) == State.ENABLED) {
            this.m.a(this.d.getClass().getName(), 2565);
        }
    }

    public final void c(Activity activity) {
        InstabugSDKLogger.d(this, this.a.toString());
        if (this.d == null) {
            InstabugSDKLogger.w(this, "No activity was set earlier than this call. Doing nothing");
        } else if (activity.equals(this.d)) {
            if (InstabugFeaturesManager.getInstance().isFeatureAvailable(Feature.INSTABUG)) {
                if (this.a == b.TAKING_SCREENSHOT) {
                    com.instabug.library.c.b.a((com.instabug.library.c.b.a) this).a();
                } else {
                    K();
                }
            }
            if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.TRACK_USER_STEPS) == State.ENABLED) {
                this.m.a(activity.getClass().getName(), 2566);
            }
        } else {
            InstabugSDKLogger.w(this, "You're trying to pause an activity that is not the current activity! Please make sure you're calling onCurrentActivityPaused and onCurrentActivityResumed on every activity");
        }
    }

    private void M() {
        if (s.u()) {
            final a aVar = new a(this);
            aVar.a(true);
            n.a().a(o(), this.h, this.e, this.f, new n.c(this) {
                final /* synthetic */ i b;

                public final void a(Uri uri) {
                    InstabugSDKLogger.d(this, "Captured screenShot Uri: " + uri);
                    aVar.a(uri);
                    this.b.a(aVar);
                }
            });
            return;
        }
        O();
    }

    public static void a(IBGInvocationEvent iBGInvocationEvent) {
        q.a().a(iBGInvocationEvent);
    }

    public static void a(Locale locale) {
        q.a().a(locale);
    }

    static /* synthetic */ void a(i iVar, com.instabug.library.model.d dVar) {
        Activity o = iVar.o();
        Context context = iVar.d;
        b.a();
        b.a(context);
        Intent intent = new Intent(context, InstabugFeedbackActivity.class);
        intent.putExtra("com.instabug.library.process", 165);
        intent.putExtra("com.instabug.library.hanging.issue", dVar);
        intent.addFlags(65536);
        o.startActivity(intent);
    }
}
