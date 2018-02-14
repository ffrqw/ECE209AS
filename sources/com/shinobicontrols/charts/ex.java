package com.shinobicontrols.charts;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class ex {
    private final af J;
    private final Handler handler;
    private final OnGestureListener he;
    private final ai hf;
    private boolean hh = false;
    private c pN;
    private a pO;
    private b pP;
    private final Queue<d> pQ = new ConcurrentLinkedQueue();

    private static abstract class d implements Runnable {
        protected final af J;
        protected final PointF ls;
        protected final Queue<d> pQ;
        protected final OnGestureListener pR;

        protected abstract void eF();

        protected abstract void onStart();

        d(af afVar, PointF pointF, Queue<d> queue, OnGestureListener onGestureListener) {
            this.J = afVar;
            this.ls = pointF;
            this.pQ = queue;
            this.pR = onGestureListener;
        }

        public void run() {
            onStart();
            this.pQ.add(this);
        }
    }

    private static class a extends d {
        a(af afVar, PointF pointF, Queue<d> queue, OnGestureListener onGestureListener) {
            super(afVar, pointF, queue, onGestureListener);
        }

        protected void onStart() {
            this.J.b(this.ls);
            this.pR.onDoubleTapDown(this.J, this.ls);
        }

        protected void eF() {
            this.J.c(this.ls);
            this.pR.onDoubleTapUp(this.J, this.ls);
        }
    }

    private static class b extends d {
        b(af afVar, PointF pointF, Queue<d> queue, OnGestureListener onGestureListener) {
            super(afVar, pointF, queue, onGestureListener);
        }

        public void run() {
            super.run();
        }

        protected void onStart() {
            this.J.d(this.ls);
            this.pR.onLongTouchDown(this.J, this.ls);
            this.pQ.clear();
        }

        protected void eF() {
            this.J.e(this.ls);
            this.pR.onLongTouchUp(this.J, this.ls);
        }
    }

    private static class c extends d {
        c(af afVar, PointF pointF, Queue<d> queue, OnGestureListener onGestureListener) {
            super(afVar, pointF, queue, onGestureListener);
        }

        protected void onStart() {
            this.J.f(this.ls);
            this.pR.onSingleTouchDown(this.J, this.ls);
        }

        protected void eF() {
            this.J.g(this.ls);
            this.pR.onSingleTouchUp(this.J, this.ls);
        }
    }

    ex(af afVar, OnGestureListener onGestureListener, ai aiVar) {
        this.J = afVar;
        this.he = onGestureListener;
        this.hf = aiVar;
        this.handler = new Handler(Looper.getMainLooper());
    }

    boolean a(int i, int i2, dw dwVar) {
        if (i == 0 && i2 == 1) {
            a(dwVar);
            return true;
        } else if (i == 1 && i2 == 1) {
            b(dwVar);
            return true;
        } else if (i == 1 && i2 == 0) {
            c(dwVar);
            return true;
        } else {
            if (i == 1 && i2 == 2) {
                eD();
            }
            return false;
        }
    }

    private void a(dw dwVar) {
        long dB = dwVar.dB();
        if (dB <= 0 || dB >= ((long) this.hf.fz)) {
            this.pN = new c(this.J, dwVar.dA().ls, this.pQ, this.he);
            this.pN.run();
            this.pP = new b(this.J, dwVar.dA().ls, this.pQ, this.he);
            this.handler.postDelayed(this.pP, (long) this.hf.fA);
            return;
        }
        this.pO = new a(this.J, dwVar.dA().ls, this.pQ, this.he);
        this.pO.run();
    }

    private void b(dw dwVar) {
        boolean z = this.hh;
        this.hh = p(dwVar.dz().fd());
        if (this.hh) {
            this.handler.removeCallbacks(this.pP);
            this.pQ.clear();
            PointF pointF = z ? dwVar.dy().ls : dwVar.dA().ls;
            this.J.c(pointF, dwVar.dD().ls);
            this.he.onSwipe(this.J, pointF, dwVar.dD().ls);
            dwVar.dE();
        }
    }

    private boolean p(float f) {
        return this.hh || f > ((float) this.hf.bL());
    }

    private void c(dw dwVar) {
        boolean z = false;
        this.handler.removeCallbacks(this.pP);
        this.hh = false;
        if (dwVar.n((float) this.hf.bL())) {
            PointF dC = dwVar.dC();
            if (dC.fd() > ((float) this.hf.fy)) {
                z = true;
            }
            this.J.b(dwVar.dD().ls, z, dC);
            this.he.onSwipeEnd(this.J, dwVar.dD().ls, z, dC);
        }
        eE();
        dwVar.clear();
    }

    private void eD() {
        this.handler.removeCallbacks(this.pP);
        this.pQ.clear();
    }

    private void eE() {
        while (!this.pQ.isEmpty()) {
            ((d) this.pQ.poll()).eF();
        }
    }

    void az() {
        this.handler.removeCallbacks(this.pP);
    }
}
