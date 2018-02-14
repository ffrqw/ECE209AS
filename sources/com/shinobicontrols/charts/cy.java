package com.shinobicontrols.charts;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;
import java.util.ArrayList;
import java.util.List;

class cy implements OnGestureListener {
    private final af J;
    private final Handler handler;
    private final ai hf;
    private final a ll = new a(this);
    private final List<OnGestureListener> lm = new ArrayList();
    private OnGestureListener ln;
    private OnGestureListener lo;
    private b lp = b.NO_GESTURE;

    private static class a implements Runnable {
        final cy lq;
        ShinobiChart lr;
        PointF ls;

        public a(cy cyVar) {
            this.lq = cyVar;
        }

        public void run() {
            this.lq.a(this.lr, this.ls);
        }
    }

    private enum b {
        NO_GESTURE,
        PANNING,
        ZOOMING
    }

    public cy(af afVar) {
        this.J = afVar;
        this.hf = afVar.eO;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void onDoubleTapDown(ShinobiChart sender, PointF position) {
        this.handler.removeCallbacks(this.ll);
    }

    public void onDoubleTapUp(ShinobiChart sender, PointF position) {
        for (OnGestureListener onDoubleTapUp : this.lm) {
            onDoubleTapUp.onDoubleTapUp(sender, position);
        }
    }

    public void onLongTouchDown(ShinobiChart sender, PointF position) {
        this.ln.onLongTouchDown(this.J, position);
    }

    public void onLongTouchUp(ShinobiChart sender, PointF position) {
    }

    public void onSingleTouchDown(ShinobiChart sender, PointF position) {
        if (this.J.ev == null || !this.J.ev.isActive()) {
            for (OnGestureListener onSingleTouchDown : this.lm) {
                onSingleTouchDown.onSingleTouchDown(sender, position);
            }
        }
    }

    public void onSingleTouchUp(ShinobiChart sender, PointF position) {
        if (this.J.bt()) {
            this.ll.lr = sender;
            this.ll.ls = position;
            this.handler.postDelayed(this.ll, (long) this.hf.fz);
            return;
        }
        a(sender, position);
    }

    private void a(ShinobiChart shinobiChart, PointF pointF) {
        if (this.J.ev == null || !this.J.ev.isActive()) {
            this.lo.onSingleTouchUp(shinobiChart, pointF);
        } else {
            this.ln.onSingleTouchUp(shinobiChart, pointF);
        }
    }

    public void onSwipe(ShinobiChart sender, PointF startPosition, PointF endPosition) {
        if (this.J.ev == null || !this.J.ev.isActive()) {
            if (this.lp != b.PANNING) {
                this.lp = b.PANNING;
            }
            for (OnGestureListener onSwipe : this.lm) {
                onSwipe.onSwipe(sender, startPosition, endPosition);
            }
            return;
        }
        this.ln.onSwipe(sender, startPosition, endPosition);
    }

    public void onSwipeEnd(ShinobiChart sender, PointF position, boolean flinging, PointF velocity) {
        if (this.J.ev == null || !this.J.ev.isActive()) {
            this.lp = b.NO_GESTURE;
            for (OnGestureListener onSwipeEnd : this.lm) {
                onSwipeEnd.onSwipeEnd(sender, position, flinging, velocity);
            }
        }
    }

    public void onSecondTouchDown(ShinobiChart sender, PointF position, PointF position2) {
    }

    public void onSecondTouchUp(ShinobiChart sender, PointF position, PointF position2) {
    }

    public void onPinch(ShinobiChart sender, PointF startFocus, PointF endFocus, PointF scaleFactor) {
        if (this.lp != b.ZOOMING) {
            this.lp = b.ZOOMING;
        }
        for (OnGestureListener onPinch : this.lm) {
            onPinch.onPinch(sender, startFocus, endFocus, scaleFactor);
        }
    }

    public void onPinchEnd(ShinobiChart sender, PointF focus, boolean flinging, PointF scaleFactor) {
        this.lp = b.NO_GESTURE;
        for (OnGestureListener onPinchEnd : this.lm) {
            onPinchEnd.onPinchEnd(sender, focus, flinging, scaleFactor);
        }
    }

    void a(OnGestureListener onGestureListener) {
        this.lm.add(onGestureListener);
    }

    void b(OnGestureListener onGestureListener) {
        this.lm.remove(onGestureListener);
    }

    void c(OnGestureListener onGestureListener) {
        this.ln = onGestureListener;
    }

    void d(OnGestureListener onGestureListener) {
        this.lo = onGestureListener;
    }
}
