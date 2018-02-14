package com.shinobicontrols.charts;

import com.shinobicontrols.charts.GLSurfaceView.EGLConfigChooser;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

class aj implements EGLConfigChooser, GLTextureView.EGLConfigChooser {
    private final int[][] fC = new int[][]{new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 16, 12326, 8, 12352, 4, 12338, 1, 12337, 2, 12344}, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 16, 12326, 8, 12352, 4, 12512, 1, 12513, 2, 12344}, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 16, 12326, 8, 12352, 4, 12344}};

    aj() {
    }

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        int i = 0;
        while (true) {
            int i2 = i + 1;
            EGLConfig[] a = a(egl, display, this.fC[i]);
            if (a.length == 0 && i2 < this.fC.length) {
                i = i2;
            }
        }
        if (a.length > 0) {
            return a[0];
        }
        return null;
    }

    private EGLConfig[] a(EGL10 egl10, EGLDisplay eGLDisplay, int[] iArr) {
        int[] iArr2 = new int[1];
        egl10.eglChooseConfig(eGLDisplay, iArr, null, 0, iArr2);
        EGLConfig[] eGLConfigArr = new EGLConfig[iArr2[0]];
        if (iArr2[0] > 0) {
            egl10.eglChooseConfig(eGLDisplay, iArr, eGLConfigArr, iArr2[0], iArr2);
        }
        return eGLConfigArr;
    }
}
