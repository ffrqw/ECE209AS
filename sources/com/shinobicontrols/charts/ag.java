package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import com.shinobicontrols.charts.Axis.Position;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;

@SuppressLint({"ViewConstructor"})
class ag extends ViewGroup {
    final af J;
    final Rect aX = new Rect();
    private dr eU;
    private w eV;
    private x eW;
    int eX;
    int eY;
    int eZ;
    int fa;
    int fb;
    int fc;

    ag(Context context, af afVar) {
        super(context);
        this.J = afVar;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        e(measuredWidth, measuredHeight);
        this.fb = (measuredWidth - this.eZ) - this.fa;
        this.fc = (measuredHeight - this.eY) - this.eX;
        measuredWidth = MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
        measuredHeight = MeasureSpec.makeMeasureSpec(measuredHeight, 1073741824);
        this.eV.measure(measuredWidth, measuredHeight);
        this.eW.measure(measuredWidth, measuredHeight);
    }

    private void e(int i, int i2) {
        int i3 = 0;
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
        int makeMeasureSpec2 = MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE);
        if (i > 0) {
            for (Axis axis : this.J.eq.bK) {
                a(axis, makeMeasureSpec, makeMeasureSpec2);
                axis.e(i);
                axis.b(true);
            }
        }
        if (i2 > 0) {
            while (i3 < this.J.er.bK.length) {
                Axis axis2 = this.J.er.bK[i3];
                a(axis2, makeMeasureSpec, makeMeasureSpec2);
                axis2.e(i2);
                axis2.b(true);
                i3++;
            }
        }
        c(this.J.eq);
        c(this.J.er);
        bF();
    }

    private void a(Axis<?, ?> axis, int i, int i2) {
        View K = axis.K();
        if (K != null && K.getVisibility() != 8) {
            measureChildWithMargins(K, i, 0, i2, 0);
        }
    }

    private static void c(i iVar) {
        int i = 0;
        if (iVar.af()) {
            int i2;
            Axis ae = iVar.ae();
            if (ae.Q == Position.REVERSE) {
                i2 = 0 - ae.as;
            } else {
                i = ae.as + 0;
                i2 = 0;
            }
            int i3 = i;
            i = i2;
            for (i2 = 1; i2 < iVar.bK.length; i2++) {
                Axis axis = iVar.bK[i2];
                if (axis.Q == Position.REVERSE) {
                    axis.ah = i;
                    i -= axis.as;
                } else {
                    axis.ah = i3;
                    i3 += axis.as;
                }
            }
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (l <= r && t <= b) {
            this.aX.set(0, 0, r - l, b - t);
            Rect rect = this.aX;
            rect.left += this.eZ;
            rect = this.aX;
            rect.top += this.eY;
            rect = this.aX;
            rect.right -= this.fa;
            rect = this.aX;
            rect.bottom -= this.eX;
            if (!this.aX.isEmpty()) {
                bw();
                this.eW.layout(0, 0, r - l, b - t);
                ca.b(this.eU.getView(), this.aX);
                this.eV.f((float) (-this.eZ));
                this.eV.g((float) (-this.eY));
                if (this.J.aW()) {
                    a(l, t, r, b);
                }
                bC();
                this.eV.layout(0, 0, r - l, b - t);
            }
        }
    }

    private void bw() {
        Crosshair crosshair = this.J.ev;
        if (crosshair != null && crosshair.isActive()) {
            crosshair.bX();
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        if (!this.aX.isEmpty()) {
            super.dispatchDraw(canvas);
        }
    }

    void a(ChartStyle chartStyle) {
        this.eW.setBackgroundColor(chartStyle.getCanvasBackgroundColor());
        a.a((View) this, null);
        a.a(this.eV, null);
        this.eU.setBackgroundColor(chartStyle.getPlotAreaBackgroundColor());
        this.eU.setBorderColor(chartStyle.bJ());
        this.eU.l(chartStyle.bK());
        for (Axis M : this.J.getAllXAxes()) {
            M.M();
        }
        for (Axis M2 : this.J.getAllYAxes()) {
            M2.M();
        }
    }

    void bx() {
        this.eW = new x(getContext(), this);
        this.eW.setLayoutParams(new LayoutParams(-1, -1));
        addView(this.eW);
        this.eU = by();
        this.eU.setLayoutParams(new MarginLayoutParams(-1, -1));
        addView(this.eU.getView());
        this.eV = new w(getContext(), this);
        this.eV.setLayoutParams(new LayoutParams(-1, -1));
        addView(this.eV);
    }

    private dr by() {
        return a.a(getContext(), this.J);
    }

    void bz() {
        if (this.J.bn() || this.J.aW()) {
            invalidate();
            requestLayout();
            Crosshair crosshair = this.J.ev;
            if (crosshair != null && crosshair.isActive()) {
                crosshair.forceLayout();
            }
            this.J.eN.forceLayout();
        }
    }

    public void invalidate() {
        super.invalidate();
        bA();
        bB();
    }

    void bA() {
        if (this.eW != null) {
            this.eW.invalidate();
        }
    }

    void bB() {
        if (this.eV != null) {
            this.eV.invalidate();
        }
    }

    public void av() {
        this.eU.av();
    }

    void bC() {
        this.eU.dv();
    }

    void bD() {
        if (this.eV != null) {
            this.eV.forceLayout();
        }
    }

    void bE() {
        if (this.eW != null) {
            this.eW.forceLayout();
        }
    }

    void onResume() {
        this.eU.onResume();
    }

    void onPause() {
        this.eU.onPause();
    }

    void bF() {
        this.eX = j.a(this.J.eq, Position.NORMAL);
        this.eY = j.a(this.J.eq, Position.REVERSE);
        this.eZ = j.a(this.J.er, Position.NORMAL);
        this.fa = j.a(this.J.er, Position.REVERSE);
    }

    void b(Canvas canvas) {
        int i = 0;
        for (Axis draw : this.J.eq.bK) {
            draw.draw(canvas, this.aX);
        }
        while (i < this.J.er.bK.length) {
            this.J.er.bK[i].draw(canvas, this.aX);
            i++;
        }
    }

    void c(Canvas canvas) {
        for (Series series : this.J.en) {
            if (!series.oC) {
                series.b(canvas, this.aX);
            }
        }
    }

    void d(Canvas canvas) {
        Crosshair crosshair = this.J.ev;
        if (crosshair != null && !this.J.bn() && crosshair.fH == a.SHOWN) {
            crosshair.draw(canvas, this.aX);
        }
    }

    void a(OnGestureListener onGestureListener) {
        this.eV.a(onGestureListener);
    }

    void b(OnGestureListener onGestureListener) {
        this.eV.b(onGestureListener);
    }

    void c(OnGestureListener onGestureListener) {
        this.eV.c(onGestureListener);
    }

    void d(OnGestureListener onGestureListener) {
        this.eV.d(onGestureListener);
    }

    private void a(int i, int i2, int i3, int i4) {
        int i5 = 0;
        int i6 = i3 - i;
        int i7 = i4 - i2;
        for (Axis a : this.J.eq.bK) {
            a.a(this.aX, i6, i7);
        }
        while (i5 < this.J.er.bK.length) {
            this.J.er.bK[i5].a(this.aX, i6, i7);
            i5++;
        }
    }

    void a(Tooltip tooltip) {
        this.eV.addView(tooltip);
    }

    void b(Tooltip tooltip) {
        this.eV.removeView(tooltip);
    }

    void a(View view, Annotation.Position position) {
        if (position == Annotation.Position.BEHIND_DATA) {
            this.eW.addView(view);
        } else {
            this.eV.addView(view);
        }
    }

    void b(View view, Annotation.Position position) {
        if (position == Annotation.Position.BEHIND_DATA) {
            this.eW.removeView(view);
        } else {
            this.eV.removeView(view);
        }
    }

    void bG() {
        this.eU.bG();
    }

    public void d(Bitmap bitmap) {
        this.eW.a(bitmap);
    }

    void az() {
        this.eV.az();
    }
}
