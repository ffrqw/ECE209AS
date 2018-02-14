package com.shinobicontrols.charts;

class SChartGLDrawer {
    private final long nativeHandle = 0;

    private native void alloc(boolean z, SChartGLErrorHandler sChartGLErrorHandler);

    private native void delete();

    native void beginRender(boolean z);

    native void drawBandSeriesFill(int i, float[] fArr, Series<?> series, float[] fArr2, int[] iArr, int i2, int i3, int i4, int i5, int i6, float[] fArr3);

    native void drawBarColumnFill(int i, float[] fArr, Series<?> series, float[] fArr2, int[] iArr, int i2, int i3, int i4, int i5, int i6, float f, float f2, int i7, boolean z, float[] fArr3);

    native void drawBarColumnLine(int i, float[] fArr, Series<?> series, float[] fArr2, int[] iArr, int i2, int i3, int i4, float f, float f2, float f3, int i5, float[] fArr3);

    native void drawCandlesticks(int i, float[] fArr, Series<?> series, int[] iArr, int i2, int[] iArr2, int[] iArr3, int i3, int i4, boolean z, boolean z2, boolean z3, float f, float f2, float f3, int i5, float f4, float[] fArr2);

    native void drawDataPoints(int i, float[] fArr, Series<?> series, int[] iArr, int[] iArr2, int i2, int i3, int i4, float f, float f2, int i5, float[] fArr2);

    native void drawHorizontalFill(int i, float[] fArr, Series<?> series, int[] iArr, int i2, int i3, int i4, int i5, int i6, boolean z, boolean z2, float f, float f2, float f3, int i7, boolean z3, float[] fArr2);

    native void drawLineStrip(int i, float[] fArr, Series<?> series, int[] iArr, int i2, int i3, int i4, float f, float f2, int i5, float[] fArr2);

    native void drawOHLCPoints(int i, float[] fArr, Series<?> series, int[] iArr, int i2, int[] iArr2, int[] iArr3, int i3, float f, float f2, float f3, float[] fArr2);

    native void drawRadialSlice(int i, int i2, Series<?> series, float f, float f2, float f3, float f4, float f5, int i3, int i4, float f6, int i5, float f7);

    native boolean endRender(AnimationManager animationManager);

    native void setFrameBufferSize(int i, int i2);

    native void updateRenderQueues(int i, Series<?> series, float[] fArr);

    static {
        System.loadLibrary("shinobicharts-android");
    }

    SChartGLDrawer(boolean opaqueBackground, SChartGLErrorHandler errorHandler) {
        alloc(opaqueBackground, errorHandler);
    }

    void ef() {
        delete();
    }
}
