package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.View;

class ds extends GLTextureView implements dr, eg {
    private final ah mR;
    private int mS;
    private float mT;

    @SuppressLint({"NewApi"})
    ds(Context context, af afVar) {
        super(context);
        this.mR = new ah(afVar, false, getResources());
        setEGLContextClientVersion(2);
        a(new aj());
        a(this.mR);
        setRenderMode(0);
        setOpaque(false);
        this.mR.setBackgroundColor(0);
    }

    public void b(SurfaceTexture surfaceTexture) {
        this.mR.bH();
        super.b(surfaceTexture);
    }

    public void setBackgroundColor(int backgroundColor) {
    }

    public void setBorderColor(int borderColor) {
        this.mS = borderColor;
    }

    public void l(float f) {
        this.mT = f;
    }

    public View getView() {
        return this;
    }

    public void dv() {
        requestRender();
    }

    public void av() {
        this.mR.av();
    }

    public void bG() {
        this.mR.requestSnapshot();
    }
}
