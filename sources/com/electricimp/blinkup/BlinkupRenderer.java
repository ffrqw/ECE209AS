package com.electricimp.blinkup;

import android.app.Activity;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Process;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public final class BlinkupRenderer implements Renderer {
    private Activity activity = null;
    private int currentBit = 0;
    private int delayCounter = 0;
    private int idleCounter = 0;
    private BlinkupPacket packet = null;
    private long startTime;
    private int state = 0;

    public BlinkupRenderer(Activity activity) {
        this.activity = activity;
    }

    public final void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Process.setThreadPriority(-19);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(16384);
    }

    public final void onDrawFrame(GL10 gl) {
        switch (this.state) {
            case 0:
                if (this.idleCounter == 0) {
                    this.startTime = System.currentTimeMillis();
                }
                this.idleCounter++;
                gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                gl.glClear(16384);
                return;
            case 1:
                if (this.packet == null) {
                    return;
                }
                if (this.currentBit == this.packet.numBits()) {
                    this.state = 2;
                    this.delayCounter = 60;
                    this.packet = null;
                    return;
                }
                if (this.currentBit == 0 && this.idleCounter > 0) {
                    float framerate = getFrameRate();
                    if (framerate >= 78.0f && framerate <= 102.0f) {
                        this.packet.twoThirdSpeed();
                    }
                }
                switch (this.packet.bitAtIndex(this.currentBit)) {
                    case 0:
                        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                        break;
                    case 1:
                        gl.glClearColor(0.75f, 0.75f, 0.75f, 1.0f);
                        break;
                    case 2:
                        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                        break;
                }
                gl.glClear(16384);
                this.currentBit++;
                return;
            case 2:
                this.delayCounter--;
                gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                gl.glClear(16384);
                if (this.delayCounter == 0) {
                    this.state = 0;
                    this.activity.setResult(-1);
                    this.activity.finish();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public final void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }

    public final void startTransmitting(BlinkupPacket packet) {
        this.packet = packet;
        this.state = 1;
    }

    public final float getFrameRate() {
        return 1000.0f / (((float) (System.currentTimeMillis() - this.startTime)) / ((float) this.idleCounter));
    }
}
