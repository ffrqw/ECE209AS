package com.electricimp.blinkup;

import android.app.Activity;
import android.opengl.GLSurfaceView;

public final class BlinkupSurfaceView extends GLSurfaceView {
    private float mMaxSize;
    private BlinkupRenderer mRenderer;

    public BlinkupSurfaceView(Activity activity, float maxSize) {
        super(activity);
        this.mMaxSize = maxSize;
        this.mRenderer = new BlinkupRenderer(activity);
        setRenderer(this.mRenderer);
    }

    public final void onMeasure(int widthSpec, int heightSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightSpec);
        float scale = 1.0f;
        if (width > height) {
            if (((float) width) > this.mMaxSize) {
                scale = this.mMaxSize / ((float) width);
            }
        } else if (((float) height) > this.mMaxSize) {
            scale = this.mMaxSize / ((float) height);
        }
        setMeasuredDimension((int) (((float) width) * scale), (int) (((float) height) * scale));
    }

    public final void startTransmitting(BlinkupPacket packet) {
        this.mRenderer.startTransmitting(packet);
    }

    public final float getFrameRate() {
        return this.mRenderer.getFrameRate();
    }
}
