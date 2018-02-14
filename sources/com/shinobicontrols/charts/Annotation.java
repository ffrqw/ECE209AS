package com.shinobicontrols.charts;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class Annotation {
    private Object m;
    private Object n;
    private final Axis<?, ?> o;
    private final Axis<?, ?> p;
    private Range<?> q;
    private Range<?> r;
    private Position s = Position.IN_FRONT_OF_DATA;
    private AnnotationStyle t;
    private final bg u = new bg();
    private final e v;
    private final View view;

    public enum Position {
        IN_FRONT_OF_DATA,
        BEHIND_DATA
    }

    Annotation(View view, Axis<?, ?> xAxis, Axis<?, ?> yAxis, e styleApplier) {
        if (view == null) {
            throw new IllegalArgumentException("Annotation cannot have a null View.");
        } else if (xAxis == null) {
            throw new IllegalArgumentException(view.getContext().getString(R.string.AnnotationCannotHaveNullX));
        } else if (yAxis == null) {
            throw new IllegalArgumentException(view.getContext().getString(R.string.AnnotationCannotHaveNullY));
        } else if (xAxis == yAxis) {
            throw new IllegalArgumentException(view.getContext().getString(R.string.AnnotationCannotHaveSameXY));
        } else {
            this.view = view;
            this.o = xAxis;
            this.p = yAxis;
            this.v = styleApplier;
        }
    }

    public Object getXValue() {
        return this.m;
    }

    public void setXValue(Object value) {
        this.m = value;
    }

    public Object getYValue() {
        return this.n;
    }

    public void setYValue(Object value) {
        this.n = value;
    }

    public Axis<?, ?> getXAxis() {
        return this.o;
    }

    public Axis<?, ?> getYAxis() {
        return this.p;
    }

    public Range<?> getXRange() {
        return this.q;
    }

    public void setXRange(Range<?> range) {
        this.q = range;
    }

    public Range<?> getYRange() {
        return this.r;
    }

    public void setYRange(Range<?> range) {
        this.r = range;
    }

    public View getView() {
        return this.view;
    }

    public Position getPosition() {
        return this.s;
    }

    public void setPosition(Position position) {
        this.s = position;
        d();
    }

    public AnnotationStyle getStyle() {
        return this.t;
    }

    public void setStyle(AnnotationStyle style) {
        this.t = style;
    }

    final void d() {
        this.u.a(new d(this));
    }

    bh a(a aVar) {
        return this.u.a(d.A, (a) aVar);
    }

    void measure(int widthMeasureSpec, int heightMeasureSpec) {
        this.view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    void layout(int left, int top, int right, int bottom) {
        LayoutParams layoutParams = this.view.getLayoutParams();
        if (layoutParams.width != -1) {
            if (layoutParams.width == 0 && this.q != null) {
                left = ((int) this.o.f(this.o.translatePoint(this.q.getMinimum()))) - this.o.J.eg.left;
                right = ((int) this.o.f(this.o.translatePoint(this.q.getMaximum()))) - this.o.J.eg.left;
            } else if (this.m == null) {
                throw new NullPointerException(this.view.getContext().getString(R.string.AnnotationCannotConvertNullX));
            } else {
                double f = this.o.f(this.o.translatePoint(this.m)) - ((double) this.o.J.eg.left);
                int measuredWidth = this.view.getMeasuredWidth();
                left = (int) (f - (((double) measuredWidth) / 2.0d));
                right = left + measuredWidth;
            }
        }
        if (layoutParams.height != -1) {
            if (layoutParams.height == 0 && this.r != null) {
                top = ((int) this.p.f(this.p.translatePoint(this.r.getMaximum()))) - this.p.J.eg.top;
                bottom = ((int) this.p.f(this.p.translatePoint(this.r.getMinimum()))) - this.p.J.eg.top;
            } else if (this.n == null) {
                throw new NullPointerException(this.view.getContext().getString(R.string.AnnotationCannotConvertNullY));
            } else {
                double f2 = this.p.f(this.p.translatePoint(this.n)) - ((double) this.p.J.eg.top);
                int measuredHeight = this.view.getMeasuredHeight();
                top = (int) (f2 - (((double) measuredHeight) / 2.0d));
                bottom = top + measuredHeight;
            }
        }
        this.view.layout(left, top, right, bottom);
    }

    void e() {
        this.v.b(this);
    }
}
