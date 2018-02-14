package com.instabug.library.internal.b;

import android.media.MediaRecorder;
import com.instabug.library.util.InstabugSDKLogger;

public final class b {
    private final String a;
    private MediaRecorder b = null;

    public b(String str) {
        this.a = str;
    }

    public final void a() {
        this.b = new MediaRecorder();
        this.b.setAudioSource(1);
        this.b.setOutputFormat(2);
        this.b.setOutputFile(this.a);
        this.b.setAudioEncoder(3);
        try {
            this.b.prepare();
            this.b.start();
        } catch (Throwable e) {
            InstabugSDKLogger.e(this, "Recording audio failed", e);
        }
    }

    public final void b() {
        this.b.stop();
        this.b.reset();
        this.b.release();
        this.b = null;
    }
}
