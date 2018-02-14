package com.shinobicontrols.charts;

abstract class Animation {
    private float a = 0.0f;
    private float b = 0.016666668f;
    private boolean c;

    Animation() {
    }

    float a() {
        return this.a;
    }

    float b() {
        return this.a / this.b;
    }

    public float getDuration() {
        return this.b;
    }

    public void setDuration(float duration) {
        this.b = duration;
    }

    void a(float f) {
        if (this.c) {
            this.a -= f;
        } else {
            this.a += f;
        }
    }

    void a(boolean z) {
        this.c = z;
        if (z) {
            this.a = this.b;
        } else {
            this.a = 0.0f;
        }
    }

    boolean isFinished() {
        return this.c ? this.a <= 0.0f : this.a >= this.b;
    }
}
