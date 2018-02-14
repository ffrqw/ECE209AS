package com.instabug.library.e.a;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.webkit.MimeTypeMap;
import com.instabug.library.e.a;
import com.instabug.library.e.c;
import com.instabug.library.e.c.b;
import com.instabug.library.internal.d.a.h;
import com.instabug.library.model.e;
import com.instabug.library.s;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;

public final class d {
    private static d a;
    private a b = new a();

    public static d a() {
        if (a == null) {
            a = new d();
        }
        return a;
    }

    private d() {
    }

    public final void a(final Context context, com.instabug.library.model.d dVar, final c.a<String, Throwable> aVar) throws JSONException, IOException {
        InstabugSDKLogger.d(this, "Reporting issue");
        c a = this.b.a(context, b.ReportIssue, com.instabug.library.e.c.d.Post);
        a.a(dVar.c());
        this.b.a(a).subscribe(new Subscriber<com.instabug.library.e.d>(this) {
            final /* synthetic */ d c;

            public final /* synthetic */ void onNext(Object obj) {
                com.instabug.library.e.d dVar = (com.instabug.library.e.d) obj;
                InstabugSDKLogger.v(this, "reportIssue request onNext, Response code: " + dVar.a() + "Response body: " + dVar.b());
                try {
                    aVar.b(new JSONObject((String) dVar.b()).getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (dVar.a() == Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    Calendar instance = Calendar.getInstance(Locale.ENGLISH);
                    InstabugSDKLogger.d(this, "Updating last_contacted_at to " + instance);
                    s.a(instance.getTime());
                    Intent intent = new Intent();
                    intent.setAction("User last contact at changed");
                    intent.putExtra("last_contacted_at", instance.getTime().getTime());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }

            public final void onStart() {
                InstabugSDKLogger.d(this, "reportIssue request started");
            }

            public final void onCompleted() {
                InstabugSDKLogger.d(this, "reportIssue request completed");
            }

            public final void onError(Throwable th) {
                InstabugSDKLogger.d(this, "reportIssue request got error: " + th.getMessage());
                aVar.a(th);
            }
        });
    }

    public final void b(Context context, final com.instabug.library.model.d dVar, final c.a<Boolean, com.instabug.library.model.d> aVar) throws JSONException, FileNotFoundException {
        InstabugSDKLogger.d(this, "Uploading issue attachments");
        Observable[] observableArr = new Observable[dVar.b().size()];
        for (int i = 0; i < observableArr.length; i++) {
            String aVar2;
            e eVar = (e) dVar.b().get(i);
            c a$2a0f3fa8 = this.b.a$2a0f3fa8(context, b.UploadFile, com.instabug.library.e.c.d.Post, a.a.b$656280e9);
            a$2a0f3fa8.a("issue_occurrence_id", eVar.c());
            a$2a0f3fa8.a("file_type", eVar.b().toString());
            String str = "file";
            String d = eVar.d();
            String e = eVar.e();
            String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(eVar.d());
            if (fileExtensionFromUrl == null || fileExtensionFromUrl.equals("")) {
                aVar2 = eVar.b().toString();
            } else {
                fileExtensionFromUrl = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
                if (fileExtensionFromUrl == null || fileExtensionFromUrl.equals("")) {
                    aVar2 = eVar.b().toString();
                } else {
                    aVar2 = fileExtensionFromUrl;
                }
            }
            a$2a0f3fa8.a(new c.c(str, d, e, aVar2));
            observableArr[i] = this.b.a(a$2a0f3fa8);
        }
        Observable.merge(observableArr, 1).subscribe(new Subscriber<com.instabug.library.e.d>(this) {
            final /* synthetic */ d c;

            public final /* synthetic */ void onNext(Object obj) {
                com.instabug.library.e.d dVar = (com.instabug.library.e.d) obj;
                InstabugSDKLogger.v(this, "uploadIssueAttachment request onNext, Response code: " + dVar.a() + ", Response body: " + dVar.b());
                new File(((e) dVar.b().get(0)).e()).delete();
                InstabugSDKLogger.d(this, "Attachment: " + ((e) dVar.b().remove(0)) + " is removed");
                h.b(dVar);
                h.b();
            }

            public final void onStart() {
                InstabugSDKLogger.d(this, "uploadIssueAttachment request started");
            }

            public final void onCompleted() {
                InstabugSDKLogger.d(this, "uploadIssueAttachment request completed");
                if (dVar.b().size() == 0) {
                    aVar.b(Boolean.valueOf(true));
                }
            }

            public final void onError(Throwable th) {
                InstabugSDKLogger.d(this, "uploadIssueAttachment request got error: " + th.getMessage());
                aVar.a(dVar);
            }
        });
    }
}
