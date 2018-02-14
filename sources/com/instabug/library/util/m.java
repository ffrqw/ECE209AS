package com.instabug.library.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import com.instabug.library.internal.layer.c;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public final class m {
    private a a;
    private Activity b;
    private Dialog c;
    private int[] d;
    private View e;
    private View f;
    private Bitmap g;
    private GLSurfaceView h;
    private TextureView i;
    private List<c> j;
    private Exception k;

    public interface a {
        void a(Bitmap bitmap);

        void a(String str, Exception exception);
    }

    public m(Activity activity, Dialog dialog, GLSurfaceView gLSurfaceView, TextureView textureView, List<c> list) {
        this.b = activity;
        this.c = dialog;
        this.h = gLSurfaceView;
        this.i = textureView;
        this.j = list;
    }

    public final void a(a aVar) {
        this.a = aVar;
    }

    public final void b() {
        if (this.a == null) {
            throw new IllegalStateException("OnScreenshotReady Listener cannot be null. Make sure you call setListener or pass a listener through the constructor");
        } else if (this.b.isFinishing()) {
            InstabugSDKLogger.d(this, "ScreenshotProcessor: Activity.isFinishing()");
            this.a.a("Top most activity changed before capturing screenshot", this.k);
        } else {
            c();
        }
    }

    private void c() {
        InstabugSDKLogger.d(this, "ScreenshotProcessor: start capturing " + Thread.currentThread().getName());
        try {
            Bitmap createBitmap;
            Bitmap drawingCache;
            this.e = this.b.getWindow().getDecorView();
            if (this.c != null && this.c.isShowing()) {
                this.f = this.c.getWindow().getDecorView();
                this.d = new int[2];
                this.f.getLocationOnScreen(this.d);
            }
            this.g = Bitmap.createBitmap(this.e.getWidth(), this.e.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas();
            canvas.setBitmap(this.g);
            List<View> arrayList = new ArrayList(3);
            if (this.b.getWindow().getDecorView().getRootView() instanceof ViewGroup) {
                a((ViewGroup) this.b.getWindow().getDecorView().getRootView(), arrayList);
            }
            Drawable background = this.b.getWindow().getDecorView().getBackground();
            if (background != null) {
                this.b.getWindow().setBackgroundDrawable(null);
                background.draw(canvas);
            }
            Object obj = null;
            if (this.j != null) {
                for (c a : this.j) {
                    try {
                        a.a(canvas);
                        obj = 1;
                    } catch (Throwable e) {
                        InstabugSDKLogger.e(this, "drawing screenshot error", e);
                        obj = 1;
                    }
                }
            }
            if (!(this.h == null || this.h.getVisibility() != 0 || this.h.getWindowToken() == null)) {
                int[] iArr = new int[2];
                this.h.getLocationOnScreen(iArr);
                final int width = this.h.getWidth();
                final int height = this.h.getHeight();
                int[] iArr2 = new int[((height + 0) * width)];
                final IntBuffer wrap = IntBuffer.wrap(iArr2);
                wrap.position(0);
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                this.h.queueEvent(new Runnable(this) {
                    final /* synthetic */ m e;

                    public final void run() {
                        EGL10 egl10 = (EGL10) EGLContext.getEGL();
                        egl10.eglWaitGL();
                        GL10 gl10 = (GL10) egl10.eglGetCurrentContext().getGL();
                        gl10.glFinish();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        gl10.glReadPixels(0, 0, width, height + 0, 6408, 5121, wrap);
                        countDownLatch.countDown();
                    }
                });
                countDownLatch.await();
                int[] iArr3 = new int[(width * height)];
                int i = 0;
                int i2 = 0;
                while (i < height) {
                    for (int i3 = 0; i3 < width; i3++) {
                        int i4 = iArr2[(i * width) + i3];
                        iArr3[(((height - i2) - 1) * width) + i3] = ((i4 & -16711936) | ((i4 << 16) & 16711680)) | ((i4 >> 16) & 255);
                    }
                    i++;
                    i2++;
                }
                createBitmap = Bitmap.createBitmap(iArr3, width, height, Config.ARGB_8888);
                canvas.drawBitmap(createBitmap, (float) iArr[0], (float) iArr[1], null);
                createBitmap.recycle();
                obj = 1;
            }
            if (obj != null) {
                boolean isDrawingCacheEnabled = this.e.isDrawingCacheEnabled();
                if (!isDrawingCacheEnabled) {
                    this.e.setDrawingCacheEnabled(true);
                }
                this.e.setDrawingCacheBackgroundColor(0);
                drawingCache = this.e.getDrawingCache(true);
                canvas.drawBitmap(drawingCache, 0.0f, 0.0f, null);
                drawingCache.recycle();
                this.e.destroyDrawingCache();
                this.e.setDrawingCacheEnabled(isDrawingCacheEnabled);
            } else {
                this.e.draw(canvas);
            }
            this.b.getWindow().setBackgroundDrawable(background);
            for (View visibility : arrayList) {
                visibility.setVisibility(0);
            }
            if (this.i != null && VERSION.SDK_INT > 14) {
                int[] iArr4 = new int[2];
                this.i.getLocationOnScreen(iArr4);
                drawingCache = this.i.getBitmap();
                if (drawingCache != null) {
                    Paint paint = new Paint();
                    paint.setXfermode(new PorterDuffXfermode(Mode.DST_ATOP));
                    canvas.drawBitmap(drawingCache, (float) iArr4[0], (float) iArr4[1], paint);
                    drawingCache.recycle();
                }
            }
            if (this.f != null) {
                createBitmap = Bitmap.createBitmap(this.f.getWidth(), this.f.getHeight(), Config.ARGB_8888);
                Canvas canvas2 = new Canvas(createBitmap);
                this.f.draw(canvas2);
                canvas.drawBitmap(createBitmap, (float) this.d[0], (float) this.d[1], null);
                canvas2.setBitmap(null);
                this.f = null;
                createBitmap.recycle();
            }
        } catch (Exception e2) {
            this.k = e2;
            this.a.a(Log.getStackTraceString(e2.getCause()), this.k);
        }
        this.e.setDrawingCacheEnabled(false);
        if (this.g == null) {
            InstabugSDKLogger.d(this, "ScreenShot Error equal null");
            this.a.a("Could not capture screenshot", this.k);
        } else if (this.b.isFinishing()) {
            InstabugSDKLogger.d(this, "ScreenShot Error");
            this.a.a("Top most activity changed while capturing screenshot. Aborting feedback process.", this.k);
        } else {
            InstabugSDKLogger.d(this, "ScreenShot ready");
            this.a.a(this.g);
        }
        this.b = null;
        this.a = null;
        this.c = null;
        this.e = null;
        this.f = null;
    }

    private void a(ViewGroup viewGroup, List<View> list) {
        List<ViewGroup> linkedList = new LinkedList();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            Object obj;
            View childAt = viewGroup.getChildAt(i);
            if ((childAt.getClass().getCanonicalName() == null || !childAt.getClass().getCanonicalName().startsWith("com.instabug.library")) && (childAt.getClass().getName() == null || !childAt.getClass().getName().startsWith("com.instabug.library"))) {
                obj = null;
            } else {
                obj = 1;
            }
            if (obj != null) {
                childAt.setVisibility(8);
                list.add(childAt);
            } else if (childAt instanceof ViewGroup) {
                linkedList.add((ViewGroup) childAt);
            }
        }
        for (ViewGroup a : linkedList) {
            a(a, list);
        }
    }
}
