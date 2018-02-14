package com.shinobicontrols.charts;

import android.content.Context;
import android.opengl.GLDebugHelper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

class GLSurfaceView extends SurfaceView implements Callback {
    private static final g hq = new g();
    private int hA;
    private boolean hB;
    private final WeakReference<GLSurfaceView> hr = new WeakReference(this);
    private f hs;
    private Renderer ht;
    private boolean hu;
    private EGLConfigChooser hv;
    private EGLContextFactory hw;
    private EGLWindowSurfaceFactory hx;
    private GLWrapper hy;
    private int hz;

    public interface EGLConfigChooser {
        EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay);
    }

    public interface EGLContextFactory {
        EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig);

        void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext);
    }

    public interface EGLWindowSurfaceFactory {
        EGLSurface createWindowSurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj);

        void destroySurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface);
    }

    public interface GLWrapper {
        GL wrap(GL gl);
    }

    public interface Renderer {
        void onDrawFrame(GL10 gl10);

        void onSurfaceChanged(GL10 gl10, int i, int i2);

        void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig);
    }

    private abstract class a implements EGLConfigChooser {
        protected int[] hC;
        final /* synthetic */ GLSurfaceView hD;

        abstract EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

        public a(GLSurfaceView gLSurfaceView, int[] iArr) {
            this.hD = gLSurfaceView;
            this.hC = a(iArr);
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] iArr = new int[1];
            if (egl.eglChooseConfig(display, this.hC, null, 0, iArr)) {
                int i = iArr[0];
                if (i <= 0) {
                    throw new IllegalArgumentException("No configs match configSpec");
                }
                EGLConfig[] eGLConfigArr = new EGLConfig[i];
                if (egl.eglChooseConfig(display, this.hC, eGLConfigArr, i, iArr)) {
                    EGLConfig a = a(egl, display, eGLConfigArr);
                    if (a != null) {
                        return a;
                    }
                    throw new IllegalArgumentException("No config chosen");
                }
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            throw new IllegalArgumentException("eglChooseConfig failed");
        }

        private int[] a(int[] iArr) {
            if (this.hD.hA != 2 && this.hD.hA != 3) {
                return iArr;
            }
            int length = iArr.length;
            Object obj = new int[(length + 2)];
            System.arraycopy(iArr, 0, obj, 0, length - 1);
            obj[length - 1] = 12352;
            if (this.hD.hA == 2) {
                obj[length] = 4;
            } else {
                obj[length] = 64;
            }
            obj[length + 1] = 12344;
            return obj;
        }
    }

    private class b extends a {
        final /* synthetic */ GLSurfaceView hD;
        private final int[] hE = new int[1];
        protected int hF;
        protected int hG;
        protected int hH;
        protected int hI;
        protected int hJ;
        protected int hK;

        public b(GLSurfaceView gLSurfaceView, int i, int i2, int i3, int i4, int i5, int i6) {
            this.hD = gLSurfaceView;
            super(gLSurfaceView, new int[]{12324, i, 12323, i2, 12322, i3, 12321, i4, 12325, i5, 12326, i6, 12344});
            this.hF = i;
            this.hG = i2;
            this.hH = i3;
            this.hI = i4;
            this.hJ = i5;
            this.hK = i6;
        }

        public EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
            for (EGLConfig eGLConfig : eGLConfigArr) {
                int a = a(egl10, eGLDisplay, eGLConfig, 12325, 0);
                int a2 = a(egl10, eGLDisplay, eGLConfig, 12326, 0);
                if (a >= this.hJ && a2 >= this.hK) {
                    a = a(egl10, eGLDisplay, eGLConfig, 12324, 0);
                    int a3 = a(egl10, eGLDisplay, eGLConfig, 12323, 0);
                    int a4 = a(egl10, eGLDisplay, eGLConfig, 12322, 0);
                    a2 = a(egl10, eGLDisplay, eGLConfig, 12321, 0);
                    if (a == this.hF && a3 == this.hG && a4 == this.hH && a2 == this.hI) {
                        return eGLConfig;
                    }
                }
            }
            return null;
        }

        private int a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2) {
            if (egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.hE)) {
                return this.hE[0];
            }
            return i2;
        }
    }

    private class c implements EGLContextFactory {
        private final int EGL_CONTEXT_CLIENT_VERSION;
        final /* synthetic */ GLSurfaceView hD;

        private c(GLSurfaceView gLSurfaceView) {
            this.hD = gLSurfaceView;
            this.EGL_CONTEXT_CLIENT_VERSION = 12440;
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int[] iArr = new int[]{12440, this.hD.hA, 12344};
            EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
            if (this.hD.hA == 0) {
                iArr = null;
            }
            return egl.eglCreateContext(display, config, eGLContext, iArr);
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                Log.e("DefaultContextFactory", "display:" + display + " context: " + context);
                e.a("eglDestroyContex", egl.eglGetError());
            }
        }
    }

    private static class d implements EGLWindowSurfaceFactory {
        private d() {
        }

        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
            EGLSurface eGLSurface = null;
            try {
                eGLSurface = egl.eglCreateWindowSurface(display, config, nativeWindow, null);
            } catch (Throwable e) {
                Log.e("GLSurfaceView", "eglCreateWindowSurface", e);
            }
            return eGLSurface;
        }

        public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
            egl.eglDestroySurface(display, surface);
        }
    }

    private static class e {
        private final WeakReference<GLSurfaceView> hL;
        EGL10 hM;
        EGLDisplay hN;
        EGLSurface hO;
        EGLConfig hP;
        EGLContext hQ;

        public e(WeakReference<GLSurfaceView> weakReference) {
            this.hL = weakReference;
        }

        public void start() {
            this.hM = (EGL10) EGLContext.getEGL();
            this.hN = this.hM.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.hN == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }
            if (this.hM.eglInitialize(this.hN, new int[2])) {
                GLSurfaceView gLSurfaceView = (GLSurfaceView) this.hL.get();
                if (gLSurfaceView == null) {
                    this.hP = null;
                    this.hQ = null;
                } else {
                    this.hP = gLSurfaceView.hv.chooseConfig(this.hM, this.hN);
                    this.hQ = gLSurfaceView.hw.createContext(this.hM, this.hN, this.hP);
                }
                if (this.hQ == null || this.hQ == EGL10.EGL_NO_CONTEXT) {
                    this.hQ = null;
                    d("createContext");
                }
                this.hO = null;
                return;
            }
            throw new RuntimeException("eglInitialize failed");
        }

        public boolean cw() {
            if (this.hM == null) {
                throw new RuntimeException("egl not initialized");
            } else if (this.hN == null) {
                throw new RuntimeException("eglDisplay not initialized");
            } else if (this.hP == null) {
                throw new RuntimeException("mEglConfig not initialized");
            } else {
                cA();
                GLSurfaceView gLSurfaceView = (GLSurfaceView) this.hL.get();
                if (gLSurfaceView != null) {
                    this.hO = gLSurfaceView.hx.createWindowSurface(this.hM, this.hN, this.hP, gLSurfaceView.getHolder());
                } else {
                    this.hO = null;
                }
                if (this.hO == null || this.hO == EGL10.EGL_NO_SURFACE) {
                    if (this.hM.eglGetError() == 12299) {
                        Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                    }
                    return false;
                } else if (this.hM.eglMakeCurrent(this.hN, this.hO, this.hO, this.hQ)) {
                    return true;
                } else {
                    a("EGLHelper", "eglMakeCurrent", this.hM.eglGetError());
                    return false;
                }
            }
        }

        GL cx() {
            GL gl = this.hQ.getGL();
            GLSurfaceView gLSurfaceView = (GLSurfaceView) this.hL.get();
            if (gLSurfaceView == null) {
                return gl;
            }
            if (gLSurfaceView.hy != null) {
                gl = gLSurfaceView.hy.wrap(gl);
            }
            if ((gLSurfaceView.hz & 3) == 0) {
                return gl;
            }
            Writer hVar;
            int i = 0;
            if ((gLSurfaceView.hz & 1) != 0) {
                i = 1;
            }
            if ((gLSurfaceView.hz & 2) != 0) {
                hVar = new h();
            } else {
                hVar = null;
            }
            return GLDebugHelper.wrap(gl, i, hVar);
        }

        public int cy() {
            if (this.hM.eglSwapBuffers(this.hN, this.hO)) {
                return 12288;
            }
            return this.hM.eglGetError();
        }

        public void cz() {
            cA();
        }

        private void cA() {
            if (this.hO != null && this.hO != EGL10.EGL_NO_SURFACE) {
                this.hM.eglMakeCurrent(this.hN, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GLSurfaceView gLSurfaceView = (GLSurfaceView) this.hL.get();
                if (gLSurfaceView != null) {
                    gLSurfaceView.hx.destroySurface(this.hM, this.hN, this.hO);
                }
                this.hO = null;
            }
        }

        public void finish() {
            if (this.hQ != null) {
                GLSurfaceView gLSurfaceView = (GLSurfaceView) this.hL.get();
                if (gLSurfaceView != null) {
                    gLSurfaceView.hw.destroyContext(this.hM, this.hN, this.hQ);
                }
                this.hQ = null;
            }
            if (this.hN != null) {
                this.hM.eglTerminate(this.hN);
                this.hN = null;
            }
        }

        private void d(String str) {
            a(str, this.hM.eglGetError());
        }

        public static void a(String str, int i) {
            throw new RuntimeException(b(str, i));
        }

        public static void a(String str, String str2, int i) {
            Log.w(str, b(str2, i));
        }

        public static String b(String str, int i) {
            return str + " failed: ";
        }
    }

    static class f extends Thread {
        private final WeakReference<GLSurfaceView> hL;
        private boolean hR;
        private boolean hS;
        private boolean hT;
        private boolean hU;
        private boolean hV;
        private boolean hW;
        private boolean hX;
        private boolean hY;
        private boolean hZ;
        private boolean ia;
        private boolean ib;
        private int ic = 0;
        private int ie = 0;
        private int if = 1;
        private boolean ig = true;
        private boolean ih;
        private final ArrayList<Runnable> ii = new ArrayList();
        private boolean ij = true;
        private e ik;

        f(WeakReference<GLSurfaceView> weakReference) {
            this.hL = weakReference;
        }

        public void run() {
            setName("GLThread " + getId());
            try {
                cD();
            } catch (InterruptedException e) {
            } finally {
                GLSurfaceView.hq.a(this);
            }
        }

        private void cB() {
            if (this.hZ) {
                this.hZ = false;
                this.ik.cz();
            }
        }

        private void cC() {
            if (this.hY) {
                this.ik.finish();
                this.hY = false;
                GLSurfaceView.hq.c(this);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void cD() throws java.lang.InterruptedException {
            /*
            r18 = this;
            r1 = new com.shinobicontrols.charts.GLSurfaceView$e;
            r0 = r18;
            r2 = r0.hL;
            r1.<init>(r2);
            r0 = r18;
            r0.ik = r1;
            r1 = 0;
            r0 = r18;
            r0.hY = r1;
            r1 = 0;
            r0 = r18;
            r0.hZ = r1;
            r3 = 0;
            r12 = 0;
            r1 = 0;
            r11 = 0;
            r10 = 0;
            r9 = 0;
            r8 = 0;
            r2 = 0;
            r7 = 0;
            r6 = 0;
            r5 = 0;
            r4 = 0;
            r14 = r3;
            r3 = r5;
            r5 = r7;
            r7 = r8;
            r8 = r9;
            r9 = r10;
            r10 = r11;
            r11 = r1;
            r17 = r2;
            r2 = r4;
            r4 = r6;
            r6 = r17;
        L_0x0031:
            r15 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d5 }
            monitor-enter(r15);	 Catch:{ all -> 0x01d5 }
        L_0x0036:
            r0 = r18;
            r1 = r0.hR;	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x004d;
        L_0x003c:
            monitor-exit(r15);	 Catch:{ all -> 0x01d2 }
            r2 = com.shinobicontrols.charts.GLSurfaceView.hq;
            monitor-enter(r2);
            r18.cB();	 Catch:{ all -> 0x004a }
            r18.cC();	 Catch:{ all -> 0x004a }
            monitor-exit(r2);	 Catch:{ all -> 0x004a }
            return;
        L_0x004a:
            r1 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x004a }
            throw r1;
        L_0x004d:
            r0 = r18;
            r1 = r0.ii;	 Catch:{ all -> 0x01d2 }
            r1 = r1.isEmpty();	 Catch:{ all -> 0x01d2 }
            if (r1 != 0) goto L_0x0081;
        L_0x0057:
            r0 = r18;
            r1 = r0.ii;	 Catch:{ all -> 0x01d2 }
            r2 = 0;
            r1 = r1.remove(r2);	 Catch:{ all -> 0x01d2 }
            r1 = (java.lang.Runnable) r1;	 Catch:{ all -> 0x01d2 }
            r2 = r6;
            r6 = r4;
            r4 = r1;
            r1 = r11;
            r11 = r10;
            r10 = r9;
            r9 = r8;
            r8 = r7;
            r7 = r5;
            r5 = r3;
        L_0x006c:
            monitor-exit(r15);	 Catch:{ all -> 0x01d2 }
            if (r4 == 0) goto L_0x01f9;
        L_0x006f:
            r4.run();	 Catch:{ all -> 0x01d5 }
            r4 = 0;
            r3 = r5;
            r5 = r7;
            r7 = r8;
            r8 = r9;
            r9 = r10;
            r10 = r11;
            r11 = r1;
            r17 = r2;
            r2 = r4;
            r4 = r6;
            r6 = r17;
            goto L_0x0031;
        L_0x0081:
            r1 = 0;
            r0 = r18;
            r13 = r0.hU;	 Catch:{ all -> 0x01d2 }
            r0 = r18;
            r0 = r0.hT;	 Catch:{ all -> 0x01d2 }
            r16 = r0;
            r0 = r16;
            if (r13 == r0) goto L_0x02f2;
        L_0x0090:
            r0 = r18;
            r1 = r0.hT;	 Catch:{ all -> 0x01d2 }
            r0 = r18;
            r13 = r0.hT;	 Catch:{ all -> 0x01d2 }
            r0 = r18;
            r0.hU = r13;	 Catch:{ all -> 0x01d2 }
            r13 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r13.notifyAll();	 Catch:{ all -> 0x01d2 }
            r13 = r1;
        L_0x00a4:
            r0 = r18;
            r1 = r0.ib;	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x00b6;
        L_0x00aa:
            r18.cB();	 Catch:{ all -> 0x01d2 }
            r18.cC();	 Catch:{ all -> 0x01d2 }
            r1 = 0;
            r0 = r18;
            r0.ib = r1;	 Catch:{ all -> 0x01d2 }
            r5 = 1;
        L_0x00b6:
            if (r9 == 0) goto L_0x00bf;
        L_0x00b8:
            r18.cB();	 Catch:{ all -> 0x01d2 }
            r18.cC();	 Catch:{ all -> 0x01d2 }
            r9 = 0;
        L_0x00bf:
            if (r13 == 0) goto L_0x00ca;
        L_0x00c1:
            r0 = r18;
            r1 = r0.hZ;	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x00ca;
        L_0x00c7:
            r18.cB();	 Catch:{ all -> 0x01d2 }
        L_0x00ca:
            if (r13 == 0) goto L_0x00ee;
        L_0x00cc:
            r0 = r18;
            r1 = r0.hY;	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x00ee;
        L_0x00d2:
            r0 = r18;
            r1 = r0.hL;	 Catch:{ all -> 0x01d2 }
            r1 = r1.get();	 Catch:{ all -> 0x01d2 }
            r1 = (com.shinobicontrols.charts.GLSurfaceView) r1;	 Catch:{ all -> 0x01d2 }
            if (r1 != 0) goto L_0x01ab;
        L_0x00de:
            r1 = 0;
        L_0x00df:
            if (r1 == 0) goto L_0x00eb;
        L_0x00e1:
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r1 = r1.cK();	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x00ee;
        L_0x00eb:
            r18.cC();	 Catch:{ all -> 0x01d2 }
        L_0x00ee:
            if (r13 == 0) goto L_0x0101;
        L_0x00f0:
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r1 = r1.cL();	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x0101;
        L_0x00fa:
            r0 = r18;
            r1 = r0.ik;	 Catch:{ all -> 0x01d2 }
            r1.finish();	 Catch:{ all -> 0x01d2 }
        L_0x0101:
            r0 = r18;
            r1 = r0.hV;	 Catch:{ all -> 0x01d2 }
            if (r1 != 0) goto L_0x0127;
        L_0x0107:
            r0 = r18;
            r1 = r0.hX;	 Catch:{ all -> 0x01d2 }
            if (r1 != 0) goto L_0x0127;
        L_0x010d:
            r0 = r18;
            r1 = r0.hZ;	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x0116;
        L_0x0113:
            r18.cB();	 Catch:{ all -> 0x01d2 }
        L_0x0116:
            r1 = 1;
            r0 = r18;
            r0.hX = r1;	 Catch:{ all -> 0x01d2 }
            r1 = 0;
            r0 = r18;
            r0.hW = r1;	 Catch:{ all -> 0x01d2 }
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r1.notifyAll();	 Catch:{ all -> 0x01d2 }
        L_0x0127:
            r0 = r18;
            r1 = r0.hV;	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x013f;
        L_0x012d:
            r0 = r18;
            r1 = r0.hX;	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x013f;
        L_0x0133:
            r1 = 0;
            r0 = r18;
            r0.hX = r1;	 Catch:{ all -> 0x01d2 }
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r1.notifyAll();	 Catch:{ all -> 0x01d2 }
        L_0x013f:
            if (r6 == 0) goto L_0x014f;
        L_0x0141:
            r7 = 0;
            r6 = 0;
            r1 = 1;
            r0 = r18;
            r0.ih = r1;	 Catch:{ all -> 0x01d2 }
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r1.notifyAll();	 Catch:{ all -> 0x01d2 }
        L_0x014f:
            r1 = r18.cF();	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x01f0;
        L_0x0155:
            r0 = r18;
            r1 = r0.hY;	 Catch:{ all -> 0x01d2 }
            if (r1 != 0) goto L_0x015e;
        L_0x015b:
            if (r5 == 0) goto L_0x01b1;
        L_0x015d:
            r5 = 0;
        L_0x015e:
            r0 = r18;
            r1 = r0.hY;	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x02ee;
        L_0x0164:
            r0 = r18;
            r1 = r0.hZ;	 Catch:{ all -> 0x01d2 }
            if (r1 != 0) goto L_0x02ee;
        L_0x016a:
            r1 = 1;
            r0 = r18;
            r0.hZ = r1;	 Catch:{ all -> 0x01d2 }
            r11 = 1;
            r10 = 1;
            r8 = 1;
            r1 = r8;
            r8 = r10;
        L_0x0174:
            r0 = r18;
            r10 = r0.hZ;	 Catch:{ all -> 0x01d2 }
            if (r10 == 0) goto L_0x01ee;
        L_0x017a:
            r0 = r18;
            r10 = r0.ij;	 Catch:{ all -> 0x01d2 }
            if (r10 == 0) goto L_0x02e4;
        L_0x0180:
            r7 = 1;
            r0 = r18;
            r3 = r0.ic;	 Catch:{ all -> 0x01d2 }
            r0 = r18;
            r1 = r0.ie;	 Catch:{ all -> 0x01d2 }
            r4 = 1;
            r10 = 1;
            r11 = 0;
            r0 = r18;
            r0.ij = r11;	 Catch:{ all -> 0x01d2 }
        L_0x0190:
            r11 = 0;
            r0 = r18;
            r0.ig = r11;	 Catch:{ all -> 0x01d2 }
            r11 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r11.notifyAll();	 Catch:{ all -> 0x01d2 }
            r11 = r8;
            r8 = r4;
            r4 = r2;
            r2 = r6;
            r6 = r3;
            r17 = r1;
            r1 = r10;
            r10 = r9;
            r9 = r7;
            r7 = r5;
            r5 = r17;
            goto L_0x006c;
        L_0x01ab:
            r1 = r1.hB;	 Catch:{ all -> 0x01d2 }
            goto L_0x00df;
        L_0x01b1:
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r0 = r18;
            r1 = r1.b(r0);	 Catch:{ all -> 0x01d2 }
            if (r1 == 0) goto L_0x015e;
        L_0x01bd:
            r0 = r18;
            r1 = r0.ik;	 Catch:{ RuntimeException -> 0x01e3 }
            r1.start();	 Catch:{ RuntimeException -> 0x01e3 }
            r1 = 1;
            r0 = r18;
            r0.hY = r1;	 Catch:{ all -> 0x01d2 }
            r12 = 1;
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r1.notifyAll();	 Catch:{ all -> 0x01d2 }
            goto L_0x015e;
        L_0x01d2:
            r1 = move-exception;
            monitor-exit(r15);	 Catch:{ all -> 0x01d2 }
            throw r1;	 Catch:{ all -> 0x01d5 }
        L_0x01d5:
            r1 = move-exception;
            r2 = com.shinobicontrols.charts.GLSurfaceView.hq;
            monitor-enter(r2);
            r18.cB();	 Catch:{ all -> 0x02db }
            r18.cC();	 Catch:{ all -> 0x02db }
            monitor-exit(r2);	 Catch:{ all -> 0x02db }
            throw r1;
        L_0x01e3:
            r1 = move-exception;
            r2 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r0 = r18;
            r2.c(r0);	 Catch:{ all -> 0x01d2 }
            throw r1;	 Catch:{ all -> 0x01d2 }
        L_0x01ee:
            r10 = r8;
            r8 = r1;
        L_0x01f0:
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d2 }
            r1.wait();	 Catch:{ all -> 0x01d2 }
            goto L_0x0036;
        L_0x01f9:
            if (r1 == 0) goto L_0x02e1;
        L_0x01fb:
            r0 = r18;
            r3 = r0.ik;	 Catch:{ all -> 0x01d5 }
            r3 = r3.cw();	 Catch:{ all -> 0x01d5 }
            if (r3 == 0) goto L_0x02ad;
        L_0x0205:
            r3 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d5 }
            monitor-enter(r3);	 Catch:{ all -> 0x01d5 }
            r1 = 1;
            r0 = r18;
            r0.ia = r1;	 Catch:{ all -> 0x02aa }
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x02aa }
            r1.notifyAll();	 Catch:{ all -> 0x02aa }
            monitor-exit(r3);	 Catch:{ all -> 0x02aa }
            r1 = 0;
            r3 = r1;
        L_0x0219:
            if (r11 == 0) goto L_0x02de;
        L_0x021b:
            r0 = r18;
            r1 = r0.ik;	 Catch:{ all -> 0x01d5 }
            r1 = r1.cx();	 Catch:{ all -> 0x01d5 }
            r1 = (javax.microedition.khronos.opengles.GL10) r1;	 Catch:{ all -> 0x01d5 }
            r11 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d5 }
            r11.a(r1);	 Catch:{ all -> 0x01d5 }
            r11 = 0;
            r13 = r1;
        L_0x022e:
            if (r12 == 0) goto L_0x024a;
        L_0x0230:
            r0 = r18;
            r1 = r0.hL;	 Catch:{ all -> 0x01d5 }
            r1 = r1.get();	 Catch:{ all -> 0x01d5 }
            r1 = (com.shinobicontrols.charts.GLSurfaceView) r1;	 Catch:{ all -> 0x01d5 }
            if (r1 == 0) goto L_0x0249;
        L_0x023c:
            r1 = r1.ht;	 Catch:{ all -> 0x01d5 }
            r0 = r18;
            r12 = r0.ik;	 Catch:{ all -> 0x01d5 }
            r12 = r12.hP;	 Catch:{ all -> 0x01d5 }
            r1.onSurfaceCreated(r13, r12);	 Catch:{ all -> 0x01d5 }
        L_0x0249:
            r12 = 0;
        L_0x024a:
            if (r9 == 0) goto L_0x0260;
        L_0x024c:
            r0 = r18;
            r1 = r0.hL;	 Catch:{ all -> 0x01d5 }
            r1 = r1.get();	 Catch:{ all -> 0x01d5 }
            r1 = (com.shinobicontrols.charts.GLSurfaceView) r1;	 Catch:{ all -> 0x01d5 }
            if (r1 == 0) goto L_0x025f;
        L_0x0258:
            r1 = r1.ht;	 Catch:{ all -> 0x01d5 }
            r1.onSurfaceChanged(r13, r6, r5);	 Catch:{ all -> 0x01d5 }
        L_0x025f:
            r9 = 0;
        L_0x0260:
            r0 = r18;
            r1 = r0.hL;	 Catch:{ all -> 0x01d5 }
            r1 = r1.get();	 Catch:{ all -> 0x01d5 }
            r1 = (com.shinobicontrols.charts.GLSurfaceView) r1;	 Catch:{ all -> 0x01d5 }
            if (r1 == 0) goto L_0x0273;
        L_0x026c:
            r1 = r1.ht;	 Catch:{ all -> 0x01d5 }
            r1.onDrawFrame(r13);	 Catch:{ all -> 0x01d5 }
        L_0x0273:
            r0 = r18;
            r1 = r0.ik;	 Catch:{ all -> 0x01d5 }
            r1 = r1.cy();	 Catch:{ all -> 0x01d5 }
            switch(r1) {
                case 12288: goto L_0x0297;
                case 12302: goto L_0x02d6;
                default: goto L_0x027e;
            };	 Catch:{ all -> 0x01d5 }
        L_0x027e:
            r14 = "GLThread";
            r15 = "eglSwapBuffers";
            com.shinobicontrols.charts.GLSurfaceView.e.a(r14, r15, r1);	 Catch:{ all -> 0x01d5 }
            r14 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d5 }
            monitor-enter(r14);	 Catch:{ all -> 0x01d5 }
            r1 = 1;
            r0 = r18;
            r0.hW = r1;	 Catch:{ all -> 0x02d8 }
            r1 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x02d8 }
            r1.notifyAll();	 Catch:{ all -> 0x02d8 }
            monitor-exit(r14);	 Catch:{ all -> 0x02d8 }
        L_0x0297:
            if (r8 == 0) goto L_0x02f5;
        L_0x0299:
            r1 = 1;
        L_0x029a:
            r2 = r4;
            r14 = r13;
            r4 = r6;
            r6 = r1;
            r17 = r7;
            r7 = r8;
            r8 = r9;
            r9 = r10;
            r10 = r11;
            r11 = r3;
            r3 = r5;
            r5 = r17;
            goto L_0x0031;
        L_0x02aa:
            r1 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x02aa }
            throw r1;	 Catch:{ all -> 0x01d5 }
        L_0x02ad:
            r3 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x01d5 }
            monitor-enter(r3);	 Catch:{ all -> 0x01d5 }
            r13 = 1;
            r0 = r18;
            r0.ia = r13;	 Catch:{ all -> 0x02d3 }
            r13 = 1;
            r0 = r18;
            r0.hW = r13;	 Catch:{ all -> 0x02d3 }
            r13 = com.shinobicontrols.charts.GLSurfaceView.hq;	 Catch:{ all -> 0x02d3 }
            r13.notifyAll();	 Catch:{ all -> 0x02d3 }
            monitor-exit(r3);	 Catch:{ all -> 0x02d3 }
            r3 = r5;
            r5 = r7;
            r7 = r8;
            r8 = r9;
            r9 = r10;
            r10 = r11;
            r11 = r1;
            r17 = r2;
            r2 = r4;
            r4 = r6;
            r6 = r17;
            goto L_0x0031;
        L_0x02d3:
            r1 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x02d3 }
            throw r1;	 Catch:{ all -> 0x01d5 }
        L_0x02d6:
            r10 = 1;
            goto L_0x0297;
        L_0x02d8:
            r1 = move-exception;
            monitor-exit(r14);	 Catch:{ all -> 0x02d8 }
            throw r1;	 Catch:{ all -> 0x01d5 }
        L_0x02db:
            r1 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x02db }
            throw r1;
        L_0x02de:
            r13 = r14;
            goto L_0x022e;
        L_0x02e1:
            r3 = r1;
            goto L_0x0219;
        L_0x02e4:
            r10 = r11;
            r17 = r4;
            r4 = r7;
            r7 = r1;
            r1 = r3;
            r3 = r17;
            goto L_0x0190;
        L_0x02ee:
            r1 = r8;
            r8 = r10;
            goto L_0x0174;
        L_0x02f2:
            r13 = r1;
            goto L_0x00a4;
        L_0x02f5:
            r1 = r2;
            goto L_0x029a;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.shinobicontrols.charts.GLSurfaceView.f.cD():void");
        }

        public boolean cE() {
            return this.hY && this.hZ && cF();
        }

        private boolean cF() {
            return !this.hU && this.hV && !this.hW && this.ic > 0 && this.ie > 0 && (this.ig || this.if == 1);
        }

        public void setRenderMode(int renderMode) {
            if (renderMode < 0 || renderMode > 1) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (GLSurfaceView.hq) {
                this.if = renderMode;
                GLSurfaceView.hq.notifyAll();
            }
        }

        public int getRenderMode() {
            int i;
            synchronized (GLSurfaceView.hq) {
                i = this.if;
            }
            return i;
        }

        public void requestRender() {
            synchronized (GLSurfaceView.hq) {
                this.ig = true;
                GLSurfaceView.hq.notifyAll();
            }
        }

        public void cG() {
            synchronized (GLSurfaceView.hq) {
                this.hV = true;
                this.ia = false;
                GLSurfaceView.hq.notifyAll();
                while (this.hX && !this.ia && !this.hS) {
                    try {
                        GLSurfaceView.hq.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void cH() {
            synchronized (GLSurfaceView.hq) {
                this.hV = false;
                GLSurfaceView.hq.notifyAll();
                while (!this.hX && !this.hS) {
                    try {
                        GLSurfaceView.hq.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onPause() {
            synchronized (GLSurfaceView.hq) {
                this.hT = true;
                GLSurfaceView.hq.notifyAll();
                while (!this.hS && !this.hU) {
                    try {
                        GLSurfaceView.hq.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onResume() {
            synchronized (GLSurfaceView.hq) {
                this.hT = false;
                this.ig = true;
                this.ih = false;
                GLSurfaceView.hq.notifyAll();
                while (!this.hS && this.hU && !this.ih) {
                    try {
                        GLSurfaceView.hq.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void f(int i, int i2) {
            synchronized (GLSurfaceView.hq) {
                this.ic = i;
                this.ie = i2;
                this.ij = true;
                this.ig = true;
                this.ih = false;
                GLSurfaceView.hq.notifyAll();
                while (!this.hS && !this.hU && !this.ih && cE()) {
                    try {
                        GLSurfaceView.hq.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void cI() {
            synchronized (GLSurfaceView.hq) {
                this.hR = true;
                GLSurfaceView.hq.notifyAll();
                while (!this.hS) {
                    try {
                        GLSurfaceView.hq.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void cJ() {
            this.ib = true;
            GLSurfaceView.hq.notifyAll();
        }
    }

    private static class g {
        private static String TAG = "GLThreadManager";
        private boolean il;
        private int im;
        private boolean in;
        private boolean io;
        private boolean ip;
        private f iq;

        private g() {
        }

        public synchronized void a(f fVar) {
            fVar.hS = true;
            if (this.iq == fVar) {
                this.iq = null;
            }
            notifyAll();
        }

        public boolean b(f fVar) {
            if (this.iq == fVar || this.iq == null) {
                this.iq = fVar;
                notifyAll();
                return true;
            }
            cM();
            if (this.io) {
                return true;
            }
            if (this.iq != null) {
                this.iq.cJ();
            }
            return false;
        }

        public void c(f fVar) {
            if (this.iq == fVar) {
                this.iq = null;
            }
            notifyAll();
        }

        public synchronized boolean cK() {
            return this.ip;
        }

        public synchronized boolean cL() {
            cM();
            return !this.io;
        }

        public synchronized void a(GL10 gl10) {
            boolean z = true;
            synchronized (this) {
                if (!this.in) {
                    cM();
                    String glGetString = gl10.glGetString(7937);
                    if (this.im < 131072) {
                        this.io = !glGetString.startsWith("Q3Dimension MSM7500 ");
                        notifyAll();
                    }
                    if (this.io) {
                        z = false;
                    }
                    this.ip = z;
                    this.in = true;
                }
            }
        }

        private void cM() {
            if (!this.il) {
                this.il = true;
            }
        }
    }

    static class h extends Writer {
        private final StringBuilder ir = new StringBuilder();

        h() {
        }

        public void close() {
            cN();
        }

        public void flush() {
            cN();
        }

        public void write(char[] buf, int offset, int count) {
            for (int i = 0; i < count; i++) {
                char c = buf[offset + i];
                if (c == '\n') {
                    cN();
                } else {
                    this.ir.append(c);
                }
            }
        }

        private void cN() {
            if (this.ir.length() > 0) {
                Log.v("GLSurfaceView", this.ir.toString());
                this.ir.delete(0, this.ir.length());
            }
        }
    }

    private class i extends b {
        final /* synthetic */ GLSurfaceView hD;

        public i(GLSurfaceView gLSurfaceView, boolean z) {
            int i;
            this.hD = gLSurfaceView;
            if (z) {
                i = 16;
            } else {
                i = 0;
            }
            super(gLSurfaceView, 8, 8, 8, 0, i, 0);
        }
    }

    public GLSurfaceView(Context context) {
        super(context);
        ct();
    }

    protected void finalize() throws Throwable {
        try {
            if (this.hs != null) {
                this.hs.cI();
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    private void ct() {
        getHolder().addCallback(this);
    }

    public void a(Renderer renderer) {
        cu();
        if (this.hv == null) {
            this.hv = new i(this, true);
        }
        if (this.hw == null) {
            this.hw = new c();
        }
        if (this.hx == null) {
            this.hx = new d();
        }
        this.ht = renderer;
        this.hs = new f(this.hr);
        this.hs.start();
    }

    public void a(EGLConfigChooser eGLConfigChooser) {
        cu();
        this.hv = eGLConfigChooser;
    }

    public void setEGLContextClientVersion(int version) {
        cu();
        this.hA = version;
    }

    public void setRenderMode(int renderMode) {
        this.hs.setRenderMode(renderMode);
    }

    public void requestRender() {
        this.hs.requestRender();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.hs.cG();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.hs.cH();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        this.hs.f(w, h);
    }

    public void onPause() {
        this.hs.onPause();
    }

    public void onResume() {
        this.hs.onResume();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.hu && this.ht != null) {
            int renderMode;
            if (this.hs != null) {
                renderMode = this.hs.getRenderMode();
            } else {
                renderMode = 1;
            }
            this.hs = new f(this.hr);
            if (renderMode != 1) {
                this.hs.setRenderMode(renderMode);
            }
            this.hs.start();
        }
        this.hu = false;
    }

    protected void onDetachedFromWindow() {
        if (this.hs != null) {
            this.hs.cI();
        }
        this.hu = true;
        super.onDetachedFromWindow();
    }

    private void cu() {
        if (this.hs != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }
}
