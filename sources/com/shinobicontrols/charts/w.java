package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import com.shinobicontrols.charts.Annotation.Position;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;

@SuppressLint({"ViewConstructor"})
class w extends ViewGroup {
    private final cy dB;
    private final ew dC;
    private float dD = 0.0f;
    private float dE = 0.0f;
    final ag dF;
    private final fn dG;

    w(Context context, ag agVar) {
        super(context);
        setWillNotDraw(false);
        this.dF = agVar;
        this.dB = new cy(agVar.J);
        this.dC = new ew(agVar.J, this.dB);
        this.dG = ba.cn() ? new fn(context) : null;
    }

    public boolean onTouchEvent(MotionEvent event) {
        event.offsetLocation(this.dD, this.dE);
        return this.dC.onTouchEvent(event) || super.onTouchEvent(event);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int size2 = MeasureSpec.getSize(heightMeasureSpec);
        size = MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
        size2 = MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE);
        if (this.dF.J.ev != null) {
            this.dF.J.ev.measure(size, size2);
        }
        this.dF.J.eN.a(size, size2, Position.IN_FRONT_OF_DATA);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.dF.J.ev != null) {
            this.dF.J.ev.layout(this.dF.aX.left, this.dF.aX.top, this.dF.aX.right, this.dF.aX.bottom);
        }
        this.dF.J.eN.a(this.dF.aX.left, this.dF.aX.top, this.dF.aX.right, this.dF.aX.bottom, Position.IN_FRONT_OF_DATA);
        if (ba.cn()) {
            this.dG.c(this.dF.aX.left, this.dF.aX.top, this.dF.aX.right, this.dF.aX.bottom);
        }
    }

    protected void onDraw(Canvas canvas) {
        canvas.clipRect(this.dF.aX);
        this.dF.c(canvas);
        this.dF.d(canvas);
    }

    void a(OnGestureListener onGestureListener) {
        this.dB.a(onGestureListener);
    }

    void b(OnGestureListener onGestureListener) {
        this.dB.b(onGestureListener);
    }

    void c(OnGestureListener onGestureListener) {
        this.dB.c(onGestureListener);
    }

    void d(OnGestureListener onGestureListener) {
        this.dB.d(onGestureListener);
    }

    void f(float f) {
        this.dD = f;
    }

    void g(float f) {
        this.dE = f;
    }

    void az() {
        this.dC.az();
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (ba.cn()) {
            a(canvas);
        }
    }

    private void a(Canvas canvas) {
        Rect rect = this.dF.aX;
        int width = rect.left + (rect.width() / 2);
        width -= this.dG.getWidth() / 2;
        int height = ((rect.height() / 2) + rect.top) - (this.dG.getHeight() / 2);
        canvas.save();
        canvas.translate((float) width, (float) height);
        this.dG.draw(canvas);
        canvas.restore();
    }
}
