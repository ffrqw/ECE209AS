package com.instabug.library.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.view.TextureView;
import android.view.View;
import com.google.android.gms.maps.GoogleMap;
import com.instabug.library.internal.layer.CapturableView;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public final class n {
    private static n a;
    private List<com.instabug.library.internal.layer.c> b = new LinkedList();

    public interface c {
        void a(Uri uri);
    }

    public interface b {
        void a(Uri uri);

        void a(String str);
    }

    class a implements com.instabug.library.internal.layer.c.a {
        int a = 1;
        Activity b;
        WeakReference<Dialog> c;
        GLSurfaceView d;
        TextureView e;
        b f;
        final /* synthetic */ n g;

        public a(n nVar, Activity activity, WeakReference<Dialog> weakReference, GLSurfaceView gLSurfaceView, TextureView textureView, b bVar) {
            this.g = nVar;
            this.b = activity;
            this.c = weakReference;
            this.d = gLSurfaceView;
            this.e = textureView;
            this.f = bVar;
        }

        public final void a() {
            if (this.g.b.size() >= this.a) {
                this.g.a(this.b, this.c, this.d, this.e, this.f);
                return;
            }
            this.a++;
            ((com.instabug.library.internal.layer.c) this.g.b.get(this.a)).a((com.instabug.library.internal.layer.c.a) this);
        }
    }

    public static n a() {
        if (a == null) {
            a = new n();
        }
        return a;
    }

    private n() {
    }

    public final void a(final Activity activity, WeakReference<Dialog> weakReference, GLSurfaceView gLSurfaceView, TextureView textureView, c cVar) {
        InstabugSDKLogger.d(this, "Starting Instabug process");
        b();
        if (activity != null && !activity.isFinishing()) {
            j.b(activity);
            final c cVar2 = cVar;
            b anonymousClass1 = new b(this) {
                final /* synthetic */ n c;

                public final void a(Uri uri) {
                    InstabugSDKLogger.d(this, "Screenshot capturing completed");
                    cVar2.a(uri);
                }

                public final void a(String str) {
                    j.a(activity);
                    InstabugSDKLogger.e(this, "Screenshot capturing failed: " + str);
                }
            };
            if (this.b.isEmpty()) {
                InstabugSDKLogger.d(this, "ScreenshotProcessor: Starting Screenshot Capture");
                a(activity, (WeakReference) weakReference, gLSurfaceView, textureView, anonymousClass1);
                return;
            }
            InstabugSDKLogger.d(this, "Invoking advanced screenshot capturing");
            ((com.instabug.library.internal.layer.c) this.b.get(0)).a(new a(this, activity, weakReference, gLSurfaceView, textureView, anonymousClass1));
        }
    }

    private void a(Activity activity, WeakReference<Dialog> weakReference, GLSurfaceView gLSurfaceView, TextureView textureView, final b bVar) {
        Dialog dialog;
        final com.instabug.library.internal.d.a aVar = new com.instabug.library.internal.d.a(activity);
        com.instabug.library.internal.module.a aVar2 = new com.instabug.library.internal.module.a();
        com.instabug.library.internal.a.a aVar3 = new com.instabug.library.internal.a.a();
        com.instabug.library.internal.a.a.a(activity);
        if (weakReference == null) {
            dialog = null;
        } else {
            dialog = (Dialog) weakReference.get();
        }
        m mVar = new m(activity, dialog, gLSurfaceView, textureView, this.b);
        mVar.a(new com.instabug.library.util.m.a(this) {
            final /* synthetic */ n c;

            public final void a(Bitmap bitmap) {
                InstabugSDKLogger.d(this, "Screenshot captured. Saving to file..");
                aVar.a(bitmap, new com.instabug.library.internal.d.a.a(this) {
                    final /* synthetic */ AnonymousClass2 a;

                    {
                        this.a = r1;
                    }

                    public final void a$300829f2(Uri uri) {
                        bVar.a(uri);
                    }

                    public final void a(String str) {
                        bVar.a(str);
                    }
                });
            }

            public final void a(String str, Exception exception) {
                if (exception != null) {
                    InstabugSDKLogger.e(this, "Screenshot capture failed: " + str, exception);
                } else {
                    InstabugSDKLogger.e(this, "Screenshot capture failed: " + str);
                }
                bVar.a(str);
            }
        });
        mVar.b();
    }

    public final void a(View view, GoogleMap googleMap) {
        a((Object) view);
        this.b.add(new com.instabug.library.internal.layer.b(view, googleMap));
    }

    public final void a(CapturableView capturableView) {
        a((Object) capturableView);
        this.b.add(new com.instabug.library.internal.layer.a(capturableView));
    }

    private void a(Object obj) {
        int i = 0;
        while (i < this.b.size()) {
            if (((com.instabug.library.internal.layer.c) this.b.get(i)).b() == null || ((com.instabug.library.internal.layer.c) this.b.get(i)).b() != obj) {
                i++;
            } else {
                this.b.remove(i);
                return;
            }
        }
    }

    public final void b() {
        int i = 0;
        while (i < this.b.size()) {
            if (((com.instabug.library.internal.layer.c) this.b.get(i)).a()) {
                i++;
            } else {
                this.b.remove(i);
            }
        }
    }
}
