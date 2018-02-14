package com.instabug.library;

import com.instabug.library.e.c.a;
import com.instabug.library.internal.d.a.h;
import com.instabug.library.model.d;
import com.instabug.library.model.e;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.FileNotFoundException;
import org.json.JSONException;

public class InstabugIssueUploaderService extends n {
    private void a(final d dVar) throws JSONException, FileNotFoundException {
        InstabugSDKLogger.d(this, "Found " + dVar.b().size() + " attachments related to issue: " + dVar.g());
        com.instabug.library.e.a.d.a().b(this, dVar, new a<Boolean, d>(this) {
            final /* synthetic */ InstabugIssueUploaderService b;

            public final /* bridge */ /* synthetic */ void a(Object obj) {
                d dVar = (d) obj;
                InstabugSDKLogger.d(this.b, "Something went wrong while uploading issue attachments");
                h.b(dVar);
                h.b();
            }

            public final /* synthetic */ void b(Object obj) {
                InstabugSDKLogger.d(this.b, "Issue attachments uploaded successfully, deleting issue");
                h.a(dVar.d());
                h.b();
            }
        });
    }

    protected final void b() throws Exception {
        InstabugSDKLogger.d(this, "Found " + h.e().size() + " issues in cache");
        for (final d dVar : h.e()) {
            if (dVar.e().equals("offline_issue_occurrence_id")) {
                InstabugSDKLogger.d(this, "Uploading issue: " + dVar);
                com.instabug.library.e.a.d.a().a(this, dVar, new a<String, Throwable>(this) {
                    final /* synthetic */ InstabugIssueUploaderService b;

                    public final /* synthetic */ void b(Object obj) {
                        Exception e;
                        String str = (String) obj;
                        InstabugSDKLogger.d(this.b, "Issue uploaded successfully, setting issue ID to " + str);
                        dVar.a(str);
                        for (e a : dVar.b()) {
                            a.a(str);
                        }
                        h.b();
                        try {
                            this.b.a(dVar);
                            return;
                        } catch (JSONException e2) {
                            e = e2;
                        } catch (FileNotFoundException e3) {
                            e = e3;
                        }
                        InstabugSDKLogger.d(this.b, "Something went wrong while uploading issue attachments " + e.getMessage());
                    }

                    public final /* bridge */ /* synthetic */ void a(Object obj) {
                        InstabugSDKLogger.d(this.b, "Something went wrong while uploading issue");
                    }
                });
            } else if (!dVar.e().equals("in_progress_issue_occurrence_id")) {
                InstabugSDKLogger.d(this, "Issue: " + dVar + " already uploaded but has unsent attachments, uploading now");
                a(dVar);
            }
        }
    }
}
