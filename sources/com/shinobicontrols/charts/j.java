package com.shinobicontrols.charts;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.ViewGroup.MarginLayoutParams;
import com.shinobicontrols.charts.Axis.Orientation;
import com.shinobicontrols.charts.Title.Position;

abstract class j {
    private static j bM = new j(null) {
        final int a(Position position) {
            return 0;
        }

        final void a(c cVar) {
        }

        final void a(Rect rect, Rect rect2, float f, int i, float f2) {
        }

        final void a(Rect rect, c cVar, int i, boolean z) {
        }

        final void a(Point point, c cVar, int i) {
        }

        final double a(double d, int i, int i2) {
            return 0.0d;
        }

        final double a(double d, Rect rect) {
            return 0.0d;
        }

        final void c(Path path, c cVar, int i, Paint paint) {
        }

        final void c(Rect rect, c cVar, PointF pointF, PointF pointF2) {
        }

        final void a(Rect rect, int i, int i2, Title title, Rect rect2) {
        }
    };
    protected Axis<?, ?> aS;

    private static class a extends j {
        protected a(Axis<?, ?> axis) {
            super(axis);
        }

        int a(Position position) {
            return position.eX();
        }

        void a(c cVar) {
            float f = (cVar.bi + cVar.bD) + cVar.bv;
            cVar.bg = (float) a(0, cVar.bf / 2.0f);
            cVar.bG.left = a(cVar.aX.left, (-cVar.be) / 2.0f);
            cVar.bG.right = a(cVar.aX.left, cVar.be / 2.0f);
            cVar.bG.top = (int) (((float) a(cVar.aX.bottom, (f - cVar.bj) - cVar.bd)) + cVar.bE);
            cVar.bG.bottom = (int) (((float) a(cVar.aX.bottom, f - cVar.bj)) + cVar.bE);
            cVar.bH.bottom = (int) (((float) a(cVar.aX.bottom, (f - cVar.bj) - (cVar.bd * 0.5f))) + cVar.bE);
            float a = cVar.bE + ((float) a(0, (cVar.bi + cVar.bD) + cVar.bv));
            float f2 = (-((float) cVar.ay.x)) / 2.0f;
            float f3 = f2 + ((float) cVar.ay.x);
            f = ((float) cVar.ay.y) + a;
            if (cVar.bF) {
                a -= cVar.bv;
                f -= cVar.bv;
            }
            cVar.bI.x = (int) (((f2 + f3) / 2.0f) + ((float) cVar.aX.left));
            cVar.bI.y = (int) (((f + a) / 2.0f) + ((float) cVar.aX.bottom));
        }

        void a(Rect rect, Rect rect2, float f, int i, float f2) {
            rect.left = rect2.left;
            rect.right = rect2.right;
            rect.top = rect2.bottom + i;
            rect.bottom = a(rect2.bottom + i, f);
        }

        void a(Rect rect, c cVar, int i, boolean z) {
            rect.left = cVar.bG.left + i;
            rect.right = cVar.bG.right + i;
            rect.top = cVar.bG.top;
            if (z) {
                rect.bottom = cVar.bG.bottom;
            } else {
                rect.bottom = cVar.bH.bottom;
            }
        }

        void a(Point point, c cVar, int i) {
            point.x = cVar.bI.x + i;
            point.y = cVar.bI.y;
        }

        double a(double d, int i, int i2) {
            return j.a(d, i, this.aS.ai.nv, this.aS.ai.dF());
        }

        double a(double d, Rect rect) {
            return j.a(d, rect, this.aS.ai.nv, this.aS.ai.dF());
        }

        void c(Path path, c cVar, int i, Paint paint) {
            a(path, cVar, i, paint);
        }

        void c(Rect rect, c cVar, PointF pointF, PointF pointF2) {
            a(rect, cVar, pointF, pointF2);
        }

        void a(Rect rect, int i, int i2, Title title, Rect rect2) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) title.getLayoutParams();
            int measuredHeight = marginLayoutParams.bottomMargin + (marginLayoutParams.topMargin + title.getMeasuredHeight());
            rect2.left = rect.left;
            rect2.top = ((rect.bottom + i2) + i) - measuredHeight;
            rect2.right = rect.right;
            rect2.bottom = (rect.bottom + i2) + i;
        }
    }

    private static class b extends j {
        protected b(Axis<?, ?> axis) {
            super(axis);
        }

        int a(Position position) {
            return position.eY();
        }

        void a(c cVar) {
            float f = -((cVar.bi + cVar.bD) - cVar.bv);
            cVar.bg = (float) a(0, cVar.bf / 2.0f);
            cVar.bG.left = (int) (((float) a(cVar.aX.left, cVar.bj + f)) - cVar.bE);
            cVar.bH.left = (int) (((float) a(cVar.aX.left, (cVar.bj + f) + (cVar.bd * 0.5f))) - cVar.bE);
            cVar.bG.right = (int) (((float) a(cVar.aX.left, (f + cVar.bj) + cVar.bd)) - cVar.bE);
            cVar.bG.top = a(cVar.aX.top, (-cVar.be) / 2.0f);
            cVar.bG.bottom = a(cVar.aX.top, cVar.be / 2.0f);
            float a = (((float) a(0, -((cVar.bi + cVar.bD) - cVar.bv))) - cVar.bE) - ((float) cVar.ay.x);
            float f2 = (-((float) cVar.ay.y)) / 2.0f;
            f = ((float) cVar.ay.x) + a;
            float f3 = ((float) cVar.ay.y) + f2;
            if (cVar.bF) {
                a -= cVar.bv;
                f -= cVar.bv;
            }
            cVar.bI.x = (int) (((f + a) / 2.0f) + ((float) cVar.aX.left));
            cVar.bI.y = (int) (((float) cVar.aX.top) + ((f2 + f3) / 2.0f));
        }

        void a(Rect rect, Rect rect2, float f, int i, float f2) {
            rect.left = a(rect2.left - i, -f);
            rect.right = rect2.left - i;
            rect.top = a(rect2.top, -f2);
            rect.bottom = a(rect2.bottom, f);
        }

        void a(Rect rect, c cVar, int i, boolean z) {
            if (z) {
                rect.left = cVar.bG.left;
            } else {
                rect.left = cVar.bH.left;
            }
            rect.right = cVar.bG.right;
            rect.top = cVar.bG.top + i;
            rect.bottom = cVar.bG.bottom + i;
        }

        void a(Point point, c cVar, int i) {
            point.x = cVar.bI.x;
            point.y = cVar.bI.y + i;
        }

        double a(double d, int i, int i2) {
            return j.b(d, i2, this.aS.ai.nv, this.aS.ai.dF());
        }

        double a(double d, Rect rect) {
            return j.b(d, rect, this.aS.ai.nv, this.aS.ai.dF());
        }

        void c(Path path, c cVar, int i, Paint paint) {
            b(path, cVar, i, paint);
        }

        void c(Rect rect, c cVar, PointF pointF, PointF pointF2) {
            b(rect, cVar, pointF, pointF2);
        }

        void a(Rect rect, int i, int i2, Title title, Rect rect2) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) title.getLayoutParams();
            int measuredWidth = marginLayoutParams.rightMargin + (marginLayoutParams.leftMargin + title.getMeasuredWidth());
            rect2.left = (rect.left - i2) - i;
            rect2.top = rect.top;
            rect2.right = measuredWidth + ((rect.left - i2) - i);
            rect2.bottom = rect.bottom;
        }
    }

    private static class c extends j {
        protected c(Axis<?, ?> axis) {
            super(axis);
        }

        int a(Position position) {
            return position.eX();
        }

        void a(c cVar) {
            float f = ((-cVar.bi) - cVar.bD) + cVar.bv;
            cVar.bg = (float) a(0, cVar.bf / 2.0f);
            cVar.bG.left = a(cVar.aX.left, (-cVar.be) / 2.0f);
            cVar.bG.right = a(cVar.aX.left, cVar.be / 2.0f);
            cVar.bG.top = (int) (((float) a(cVar.aX.top, cVar.bj + f)) + cVar.bE);
            cVar.bH.top = (int) (((float) a(cVar.aX.top, (cVar.bj + f) + (cVar.bd * 0.5f))) + cVar.bE);
            cVar.bG.bottom = (int) (((float) a(cVar.aX.top, (f + cVar.bj) + cVar.bd)) + cVar.bE);
            float f2 = (-((float) cVar.ay.x)) / 2.0f;
            float a = (((float) a(0, ((-cVar.bi) - cVar.bD) + cVar.bv)) + cVar.bE) - ((float) cVar.ay.y);
            float f3 = f2 + ((float) cVar.ay.x);
            f = ((float) cVar.ay.y) + a;
            if (cVar.bF) {
                a -= cVar.bv;
                f -= cVar.bv;
            }
            cVar.bI.x = (int) (((f2 + f3) / 2.0f) + ((float) cVar.aX.left));
            cVar.bI.y = (int) (((f + a) / 2.0f) + ((float) cVar.aX.top));
        }

        void a(Rect rect, Rect rect2, float f, int i, float f2) {
            rect.left = rect2.left;
            rect.right = rect2.right;
            rect.top = a(rect2.top + i, -f);
            rect.bottom = rect2.top + i;
        }

        void a(Rect rect, c cVar, int i, boolean z) {
            rect.left = cVar.bG.left + i;
            rect.right = cVar.bG.right + i;
            if (z) {
                rect.top = cVar.bG.top;
            } else {
                rect.top = cVar.bH.top;
            }
            rect.bottom = cVar.bG.bottom;
        }

        void a(Point point, c cVar, int i) {
            point.x = cVar.bI.x + i;
            point.y = cVar.bI.y;
        }

        double a(double d, int i, int i2) {
            return j.a(d, i, this.aS.ai.nv, this.aS.ai.dF());
        }

        double a(double d, Rect rect) {
            return j.a(d, rect, this.aS.ai.nv, this.aS.ai.dF());
        }

        void c(Path path, c cVar, int i, Paint paint) {
            a(path, cVar, i, paint);
        }

        void c(Rect rect, c cVar, PointF pointF, PointF pointF2) {
            a(rect, cVar, pointF, pointF2);
        }

        void a(Rect rect, int i, int i2, Title title, Rect rect2) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) title.getLayoutParams();
            int measuredHeight = marginLayoutParams.bottomMargin + (marginLayoutParams.topMargin + title.getMeasuredHeight());
            rect2.left = rect.left;
            rect2.top = (rect.top + i2) - i;
            rect2.right = rect.right;
            rect2.bottom = measuredHeight + ((rect.top + i2) - i);
        }
    }

    private static class d extends j {
        protected d(Axis<?, ?> axis) {
            super(axis);
        }

        int a(Position position) {
            return position.eY();
        }

        void a(c cVar) {
            float f = (cVar.bi + cVar.bD) - cVar.bv;
            cVar.bg = (float) a(0, cVar.bf / 2.0f);
            cVar.bG.left = (int) (((float) a(cVar.aX.right, (f - cVar.bj) - cVar.bd)) - cVar.bE);
            cVar.bG.right = (int) (((float) a(cVar.aX.right, f - cVar.bj)) - cVar.bE);
            cVar.bH.right = (int) (((float) a(cVar.aX.right, (f - cVar.bj) - (cVar.bd * 0.5f))) - cVar.bE);
            cVar.bG.top = a(cVar.aX.top, (-cVar.be) / 2.0f);
            cVar.bG.bottom = a(cVar.aX.top, cVar.be / 2.0f);
            float a = ((float) a(0, (cVar.bi + cVar.bD) - cVar.bv)) - cVar.bE;
            float f2 = (-((float) cVar.ay.y)) / 2.0f;
            f = ((float) cVar.ay.x) + a;
            float f3 = ((float) cVar.ay.y) + f2;
            if (cVar.bF) {
                a -= cVar.bv;
                f -= cVar.bv;
            }
            cVar.bI.x = (int) (((f + a) / 2.0f) + ((float) cVar.aX.right));
            cVar.bI.y = (int) (((float) cVar.aX.top) + ((f2 + f3) / 2.0f));
        }

        void a(Rect rect, Rect rect2, float f, int i, float f2) {
            rect.left = rect2.right - i;
            rect.right = a(rect2.right - i, f);
            rect.top = a(rect2.top, -f2);
            rect.bottom = a(rect2.bottom, f);
        }

        void a(Rect rect, c cVar, int i, boolean z) {
            rect.left = cVar.bG.left;
            if (z) {
                rect.right = cVar.bG.right;
            } else {
                rect.right = cVar.bH.right;
            }
            rect.top = cVar.bG.top + i;
            rect.bottom = cVar.bG.bottom + i;
        }

        void a(Point point, c cVar, int i) {
            point.x = cVar.bI.x;
            point.y = cVar.bI.y + i;
        }

        double a(double d, int i, int i2) {
            return j.b(d, i2, this.aS.ai.nv, this.aS.ai.dF());
        }

        double a(double d, Rect rect) {
            return j.b(d, rect, this.aS.ai.nv, this.aS.ai.dF());
        }

        void c(Path path, c cVar, int i, Paint paint) {
            b(path, cVar, i, paint);
        }

        void c(Rect rect, c cVar, PointF pointF, PointF pointF2) {
            b(rect, cVar, pointF, pointF2);
        }

        void a(Rect rect, int i, int i2, Title title, Rect rect2) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) title.getLayoutParams();
            rect2.left = ((rect.right - i2) + i) - (marginLayoutParams.rightMargin + (marginLayoutParams.leftMargin + title.getMeasuredWidth()));
            rect2.top = rect.top;
            rect2.right = (rect.right - i2) + i;
            rect2.bottom = rect.bottom;
        }
    }

    abstract double a(double d, int i, int i2);

    abstract double a(double d, Rect rect);

    abstract int a(Position position);

    abstract void a(Point point, c cVar, int i);

    abstract void a(Rect rect, int i, int i2, Title title, Rect rect2);

    abstract void a(Rect rect, Rect rect2, float f, int i, float f2);

    abstract void a(Rect rect, c cVar, int i, boolean z);

    abstract void a(c cVar);

    abstract void c(Path path, c cVar, int i, Paint paint);

    abstract void c(Rect rect, c cVar, PointF pointF, PointF pointF2);

    protected j(Axis<?, ?> axis) {
        this.aS = axis;
    }

    static j e(Axis<?, ?> axis) {
        if (axis == null || axis.P == null || axis.Q == null) {
            return bM;
        }
        if (axis.P == Orientation.HORIZONTAL) {
            if (axis.Q == Axis.Position.NORMAL) {
                return new a(axis);
            }
            if (axis.Q == Axis.Position.REVERSE) {
                return new c(axis);
            }
            throw new AssertionError("Axis Position invalid:" + axis.Q);
        } else if (axis.P != Orientation.VERTICAL) {
            throw new AssertionError("Axis Orientation invalid:" + axis.P);
        } else if (axis.Q == Axis.Position.NORMAL) {
            return new b(axis);
        } else {
            if (axis.Q == Axis.Position.REVERSE) {
                return new d(axis);
            }
            throw new AssertionError("Axis Position invalid:" + axis.Q);
        }
    }

    int a(int i, float f) {
        return ca.a(this.aS.density, i, f);
    }

    void a(Path path, c cVar, int i, Paint paint) {
        path.reset();
        int i2 = cVar.aX.left + i;
        path.moveTo((float) i2, (float) cVar.aX.top);
        path.lineTo((float) i2, (float) cVar.aX.bottom);
        int i3 = i2 - cVar.aX.left;
        if (((float) i3) < cVar.bg) {
            float f = cVar.bg - ((float) i3);
            paint.setStrokeWidth(paint.getStrokeWidth() - f);
            path.offset(f / 2.0f, 0.0f);
        }
        i2 = cVar.aX.right - i2;
        if (((float) i2) < cVar.bg) {
            float f2 = cVar.bg - ((float) i2);
            paint.setStrokeWidth(paint.getStrokeWidth() - f2);
            path.offset((-f2) / 2.0f, 0.0f);
        }
    }

    void b(Path path, c cVar, int i, Paint paint) {
        path.reset();
        int i2 = cVar.aX.top + i;
        path.moveTo((float) cVar.aX.left, (float) i2);
        path.lineTo((float) cVar.aX.right, (float) i2);
        int i3 = cVar.aX.bottom - i2;
        if (((float) i3) < cVar.bg) {
            float f = cVar.bg - ((float) i3);
            paint.setStrokeWidth(paint.getStrokeWidth() - f);
            path.offset(0.0f, (-f) / 2.0f);
        }
        i2 -= cVar.aX.top;
        if (((float) i2) < cVar.bg) {
            float f2 = cVar.bg - ((float) i2);
            paint.setStrokeWidth(paint.getStrokeWidth() - f2);
            path.offset(0.0f, f2 / 2.0f);
        }
    }

    void a(Rect rect, c cVar, PointF pointF, PointF pointF2) {
        rect.left = (int) pointF.x;
        rect.right = (int) pointF2.x;
        rect.top = cVar.aX.top;
        rect.bottom = a(cVar.aX.bottom, cVar.bE);
    }

    void b(Rect rect, c cVar, PointF pointF, PointF pointF2) {
        rect.left = cVar.aX.left;
        rect.right = cVar.aX.right;
        rect.top = (int) pointF2.y;
        rect.bottom = (int) pointF.y;
    }

    static double a(double d, int i, double d2, double d3) {
        return ((d - d2) * ((double) i)) / d3;
    }

    static double a(double d, Rect rect, double d2, double d3) {
        return ((d * d3) / ((double) (rect.right - rect.left))) + d2;
    }

    static double b(double d, int i, double d2, double d3) {
        return ((double) i) - (((d - d2) * ((double) i)) / d3);
    }

    static double b(double d, Rect rect, double d2, double d3) {
        return ((1.0d - (d / ((double) (rect.bottom - rect.top)))) * d3) + d2;
    }

    static int a(i iVar, Axis.Position position) {
        int i = 0;
        int i2 = 0;
        while (i < iVar.bK.length) {
            Axis axis = iVar.bK[i];
            if (position == axis.Q) {
                i2 += axis.as;
            }
            i++;
        }
        return i2;
    }
}
