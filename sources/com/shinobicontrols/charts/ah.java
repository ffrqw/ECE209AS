package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLES20;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class ah implements bf {
    static final Object lock = new Object();
    private final af J;
    private boolean da = false;
    private bc fd = new bc(0);
    private final AnimationManager fe = new AnimationManager();
    private SChartGLDrawer ff = null;
    private final SChartGLErrorHandler fg = new SChartGLErrorHandler();
    private final boolean fh;
    private final Resources fi;
    private boolean fj;
    private final bd fk = new bd();
    private final es fl;
    private int height;
    private int width;

    ah(af afVar, boolean z, Resources resources) {
        this.J = afVar;
        this.fh = z;
        this.fi = resources;
        this.fl = new es(ba.cp());
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        synchronized (lock) {
            if (this.ff != null) {
                this.ff.ef();
            }
            this.ff = new SChartGLDrawer(this.fh, this.fg);
            this.da = true;
        }
    }

    void bH() {
        synchronized (lock) {
            if (this.ff != null) {
                this.ff.ef();
                this.ff = null;
            }
            this.fk.clear();
        }
    }

    @SuppressLint({"WrongCall"})
    public void onDrawFrame(GL10 gl) {
        synchronized (lock) {
            try {
                this.da |= h(this.J.en);
                this.ff.beginRender(this.da);
                if (this.da) {
                    i(this.J.eo.aR());
                }
                this.fl.a(this.J.en, this.da, this.fk, this.ff);
                this.da = false;
                a(this.fd);
                this.ff.endRender(this.fe);
                if (this.fj) {
                    this.J.b(a(this.width, this.height, gl));
                    this.fj = false;
                }
            } catch (RuntimeException e) {
                ev.h(this.J.getContext().getString(R.string.ChartRendererExceptionInGL));
                throw e;
            }
        }
    }

    private boolean h(List<Series<?>> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (((Series) list.get(i)).ot.aw()) {
                return true;
            }
        }
        return false;
    }

    private void i(List<en> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ((en) list.get(i)).ah();
        }
    }

    private void a(bc bcVar) {
        GLES20.glClearColor(bcVar.hj, bcVar.hk, bcVar.hl, bcVar.alpha);
        GLES20.glClear(17664);
    }

    private static Bitmap a(int i, int i2, GL10 gl10) {
        int[] iArr = new int[(i * i2)];
        Buffer wrap = IntBuffer.wrap(iArr);
        wrap.position(0);
        gl10.glReadPixels(0, 0, i, i2, 6408, 5121, wrap);
        int[] iArr2 = new int[(i * i2)];
        for (int i3 = 0; i3 < i2; i3++) {
            for (int i4 = 0; i4 < i; i4++) {
                int i5 = iArr[(i3 * i) + i4];
                iArr2[(((i2 - i3) - 1) * i) + i4] = ((i5 & -16711936) | ((i5 << 16) & 16711680)) | ((i5 >> 16) & 255);
            }
        }
        return Bitmap.createBitmap(iArr2, i, i2, Config.ARGB_8888);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        synchronized (lock) {
            this.width = width;
            this.height = height;
            GLES20.glViewport(0, 0, width, height);
            this.fk.a(2.0f / ((float) width), 2.0f / ((float) height), this.fi.getDisplayMetrics());
            this.ff.setFrameBufferSize(width, height);
            this.da = true;
        }
    }

    void setBackgroundColor(int backgroundColor) {
        synchronized (lock) {
            this.fd = new bc(backgroundColor);
        }
    }

    void av() {
        this.da = true;
    }

    public void requestSnapshot() {
        this.fj = true;
    }
}
