package com.instabug.library.c;

import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;

public final class c extends FileObserver {
    private static final File a = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    private static final File b;
    private static final String c;
    private final Handler d = new Handler();
    private Runnable e;
    private a f;
    private String g;
    private String h;

    public interface a {
        void a(Uri uri);
    }

    static {
        File file = new File(a, "Screenshots");
        b = file;
        c = file.getAbsolutePath();
    }

    public c(a aVar) {
        super(c);
        InstabugSDKLogger.d(this, "PATH: " + c);
        this.f = aVar;
    }

    public final void onEvent(int i, String str) {
        InstabugSDKLogger.i(this, "Event:" + i + "\t" + str);
        if (str == null) {
            InstabugSDKLogger.i(this, "Ignore this event.");
            return;
        }
        if (i == 32) {
            this.g = str;
        }
        if (i == 2 && this.g.equals(str)) {
            this.h = str;
        }
        if (i == 8 && this.h.equals(this.g) && this.h.equals(str) && a(str)) {
            this.f.a(Uri.fromFile(new File(c + "/" + str)));
        }
    }

    public final void a() {
        if (VERSION.SDK_INT >= 23) {
            startWatching();
        } else {
            super.startWatching();
        }
    }

    public final void b() {
        if (VERSION.SDK_INT >= 23) {
            stopWatching();
        } else {
            super.stopWatching();
        }
    }

    public final void startWatching() {
        this.e = new Runnable(this) {
            final /* synthetic */ c a;

            {
                this.a = r1;
            }

            public final void run() {
                File file = new File(c.c);
                if (file.listFiles() != null) {
                    InstabugSDKLogger.d(this, "The path is directory: " + file.isDirectory());
                    if (file.isDirectory()) {
                        for (File file2 : file.listFiles()) {
                            InstabugSDKLogger.d(this, "File name: " + file2.getName() + "File last modified: " + file2.lastModified());
                            if (this.a.a(file2.getName())) {
                                this.a.f.a(Uri.fromFile(file2));
                            }
                        }
                    }
                }
                this.a.d.postDelayed(this.a.e, 1500);
            }
        };
        this.d.post(this.e);
    }

    public final void stopWatching() {
        this.d.removeCallbacks(this.e);
    }

    private boolean a(String str) {
        if (str.toLowerCase().contains("screenshot")) {
            long currentTimeMillis = System.currentTimeMillis() - new File(c + "/" + str).lastModified();
            InstabugSDKLogger.d(this, "Difference time between file lastUpdate and currentTime: " + currentTimeMillis);
            if (currentTimeMillis < 1000) {
                InstabugSDKLogger.d(this, "Send event to listener.");
                return true;
            }
        }
        return false;
    }
}
