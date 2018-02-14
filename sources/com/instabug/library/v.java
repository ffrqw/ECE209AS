package com.instabug.library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import com.instabug.library.OnSdkDismissedCallback.IssueState;
import com.instabug.library.model.d;
import com.instabug.library.model.e;
import com.instabug.library.model.e.a;
import java.io.File;

public final class v {
    private static v a = new v();
    private d b;
    private boolean c;
    private IssueState d = null;

    private v() {
    }

    protected static v a() {
        return a;
    }

    public final void a(d dVar) {
        this.b = dVar;
        this.c = false;
        this.d = IssueState.IN_PROGRESS;
    }

    protected final d b() {
        return this.b;
    }

    protected final void a(Context context, Uri uri) {
        a(context, uri, a.IMAGE, null);
    }

    protected final void a(Context context, Uri uri, a aVar, String str) {
        this.b.a(com.instabug.library.internal.d.a.a(context, uri, str), aVar);
        a(context);
    }

    protected static void a(e eVar) {
        new File(eVar.e()).delete();
    }

    protected static void a(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("refresh.attachments"));
    }

    public final void a(IssueState issueState) {
        this.d = issueState;
    }

    public final IssueState d() {
        return this.d;
    }

    public final boolean e() {
        return this.c;
    }

    public final void a(boolean z) {
        this.c = z;
    }

    protected final void b(Context context, Uri uri) {
        a.b.a(uri, a.AUDIO);
        a(context);
    }
}
