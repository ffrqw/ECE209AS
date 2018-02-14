package com.instabug.library;

import android.content.Context;
import com.instabug.library.Feature.State;
import com.instabug.library.internal.a.a;
import com.instabug.library.internal.d.a.h;
import com.instabug.library.model.IssueType;
import com.instabug.library.model.e;
import com.instabug.library.model.f;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.d;
import java.lang.Thread.UncaughtExceptionHandler;
import org.json.JSONException;
import org.json.JSONObject;

public final class c implements UncaughtExceptionHandler {
    private final a a;
    private d b;
    private f c;
    private u d;

    public c(f fVar, u uVar, a aVar, d dVar) {
        this.b = dVar;
        this.a = aVar;
        this.c = fVar;
        this.d = uVar;
    }

    public final void uncaughtException(Thread thread, Throwable th) {
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.CRASH_REPORTING) == State.DISABLED) {
            this.b.a(thread, th);
            return;
        }
        InstabugSDKLogger.e(Instabug.class, "Instabug Caught an Unhandled Exception: " + th.getClass().getCanonicalName(), th);
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("threadName", thread.getName());
            jSONObject2.put("threadId", thread.getId());
            jSONObject2.put("threadPriority", thread.getPriority());
            jSONObject2.put("threadState", thread.getState().toString());
            ThreadGroup threadGroup = thread.getThreadGroup();
            if (threadGroup != null) {
                JSONObject jSONObject3 = new JSONObject();
                jSONObject3.put("name", threadGroup.getName());
                jSONObject3.put("maxPriority", threadGroup.getMaxPriority());
                jSONObject3.put("activeCount", threadGroup.activeCount());
                jSONObject2.put("threadGroup", jSONObject3);
            }
            jSONObject.put("thread", jSONObject2);
            jSONObject.put("error", com.instabug.library.util.a.a.a(th, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (q.a().b() != null) {
            try {
                q.a().b().run();
            } catch (Throwable e2) {
                InstabugSDKLogger.e(Instabug.class, "Pre sending runnable failed to run.", e2);
            }
        }
        com.instabug.library.model.d dVar = new com.instabug.library.model.d(System.currentTimeMillis());
        dVar.b(s.c());
        dVar.c(jSONObject.toString());
        dVar.a(IssueType.CRASH);
        Context application = Instabug.getApplication();
        if (q.a().h() != null) {
            dVar.a(com.instabug.library.internal.d.a.a(application, q.a().h(), q.a().i()), e.a.ATTACHMENT_FILE);
        }
        f fVar = this.c;
        q.a();
        fVar.a(dVar, m.a(), a.a(application));
        h.b(dVar);
        Instabug.onSessionFinished();
        InstabugSDKLogger.i(Instabug.class, "Crash persisted for upload at next startup");
        this.b.a(thread, th);
    }
}
