package com.shinobicontrols.charts;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.View;

class dt extends GLSurfaceView implements dr, eg {
    private final ah mR;
    private int mS;
    private float mT;

    dt(Context context, af afVar) {
        super(context);
        this.mR = new ah(afVar, false, getResources());
        setEGLContextClientVersion(2);
        a(new aj());
        setZOrderOnTop(false);
        a(this.mR);
        setRenderMode(0);
        getHolder().setFormat(-3);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mR.bH();
        super.surfaceDestroyed(holder);
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mR.setBackgroundColor(backgroundColor);
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
