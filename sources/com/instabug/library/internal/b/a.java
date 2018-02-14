package com.instabug.library.internal.b;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import com.instabug.library.util.InstabugSDKLogger;

public final class a {
    private final String a;
    private MediaPlayer b = null;

    public a(String str) {
        this.a = str;
    }

    private void a() {
        this.b = new MediaPlayer();
        try {
            this.b.setDataSource(this.a);
            this.b.prepare();
        } catch (Throwable e) {
            InstabugSDKLogger.e(this, "Playing audio file failed", e);
        }
    }

    public final void a(OnCompletionListener onCompletionListener) {
        this.b.setOnCompletionListener(onCompletionListener);
    }

    public final int b() {
        if (this.b == null) {
            a();
        }
        return this.b.getDuration();
    }

    public final void c() {
        if (this.b == null) {
            a();
        }
        this.b.start();
    }

    public final void d() {
        this.b.release();
        this.b = null;
    }
}
