package com.shinobicontrols.charts;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.shinobicontrols.charts.Series.SelectionMode;

public abstract class PieDonutSeries<T extends PieDonutSeriesStyle> extends Series<T> {
    private NumberRange dW;
    private DrawDirection lR;
    private float lS;
    final fh<Float> lZ;
    final fh<Float> ma;
    private final ChartUtils mb;
    private boolean mc;
    private final BounceAnimationCurve md;
    private final BounceAnimationCurve me;
    private final EaseInOutAnimationCurve mf;
    private Float mg;
    private final b mh;
    private final a mi;

    public enum DrawDirection {
        CLOCKWISE,
        ANTICLOCKWISE
    }

    public enum RadialEffect {
        FLAT(0),
        BEVELLED(1),
        BEVELLED_LIGHT(2),
        ROUNDED(3),
        ROUNDED_LIGHT(4),
        DEFAULT(5);
        
        private final int mp;

        private RadialEffect(int xmlValue) {
            this.mp = xmlValue;
        }

        public final int getXmlValue() {
            return this.mp;
        }
    }

    private static class a implements a {
        private final PieDonutSeries<?> mk;
        float ml;
        float mm;
        float mn;
        float mo;

        public a(PieDonutSeries<?> pieDonutSeries) {
            this.mk = pieDonutSeries;
        }

        public void onAnimationStart() {
            for (InternalDataPoint internalDataPoint : this.mk.db.je) {
                PieDonutSlice pieDonutSlice = (PieDonutSlice) internalDataPoint;
                pieDonutSlice.mH = pieDonutSlice.mI;
            }
        }

        public void b(Animation animation) {
            dp dpVar = (dp) animation;
            b((float) dpVar.ds(), dpVar.dq(), dpVar.dr());
        }

        public void onAnimationEnd() {
        }

        public void c() {
        }

        public void execute() {
            b(1.0f, 1.0f, 1.0f);
        }

        private void b(float f, float f2, float f3) {
            if (this.mk.J == null) {
                this.mk.mh.cancel();
                return;
            }
            synchronized (ah.lock) {
                this.mk.lS = cv.j(this.mn + (this.mo * f));
                for (InternalDataPoint internalDataPoint : this.mk.db.je) {
                    PieDonutSlice pieDonutSlice = (PieDonutSlice) internalDataPoint;
                    pieDonutSlice.mI = pieDonutSlice.iU ? pieDonutSlice.mH + ((this.ml - pieDonutSlice.mH) * f2) : pieDonutSlice.mH + ((this.mm - pieDonutSlice.mH) * f3);
                }
            }
            this.mk.J.em.invalidate();
            this.mk.ot.av();
            this.mk.J.em.bC();
        }
    }

    abstract float h(float f);

    PieDonutSeries(ao dataLoadHelperFactory) {
        super(dataLoadHelperFactory);
        this.lR = DrawDirection.ANTICLOCKWISE;
        this.lZ = new fh(Float.valueOf(0.0f));
        this.ma = new fh(Float.valueOf(0.0f));
        this.mb = new ChartUtils();
        this.mc = true;
        this.dW = new NumberRange();
        this.md = new BounceAnimationCurve();
        this.me = new BounceAnimationCurve();
        this.mf = new EaseInOutAnimationCurve();
        this.mg = null;
        this.mh = new b();
        this.mi = new a(this);
        this.oA = SeriesAnimation.createGrowAnimation();
        this.oB = SeriesAnimation.createGrowAnimation();
    }

    float dm() {
        return Math.max(((PieDonutSeriesStyle) this.ou).getProtrusion(), ((PieDonutSeriesStyle) this.ov).getProtrusion());
    }

    public DrawDirection getDrawDirection() {
        return this.lR;
    }

    public void setDrawDirection(DrawDirection drawDirection) {
        synchronized (ah.lock) {
            this.lR = drawDirection;
        }
    }

    float getInnerRadius() {
        return ((Float) this.lZ.sU).floatValue();
    }

    public float getOuterRadius() {
        return ((Float) this.ma.sU).floatValue();
    }

    public void setOuterRadius(float outerRadius) {
        synchronized (ah.lock) {
            this.ma.b(Float.valueOf(outerRadius));
            ac();
        }
    }

    public float getRotation() {
        if (this.mc) {
            this.lS = ((PieDonutSeriesStyle) this.ou).getInitialRotation();
            this.mc = false;
        }
        return this.lS;
    }

    public void setRotation(float rotation) {
        synchronized (ah.lock) {
            this.lS = rotation;
        }
    }

    public Float getSelectedPosition() {
        return this.mg;
    }

    public void setSelectedPosition(Float selectedPosition) {
        synchronized (ah.lock) {
            this.mg = selectedPosition;
        }
    }

    public Point getCenter() {
        if (this.J == null) {
            return null;
        }
        Point point = new Point();
        point.set((int) ((((float) this.J.getPlotAreaRect().width()) / 2.0f) + 0.5f), (int) ((((float) this.J.getPlotAreaRect().height()) / 2.0f) + 0.5f));
        return point;
    }

    Point a(Rect rect, int i) {
        float f;
        PieDonutSlice pieDonutSlice = (PieDonutSlice) this.db.je[i];
        float floatValue = ((((Float) this.lZ.sU).floatValue() + ((Float) this.ma.sU).floatValue()) / 2.0f) + pieDonutSlice.mI;
        if (this.lR == DrawDirection.ANTICLOCKWISE) {
            f = (-(pieDonutSlice.mG + pieDonutSlice.mF)) / 2.0f;
        } else {
            f = (pieDonutSlice.mG + pieDonutSlice.mF) / 2.0f;
        }
        float cos = ((float) Math.cos(((double) ((-getRotation()) + f)) - 1.5707963267948966d)) * floatValue;
        f = ((float) Math.sin(((double) (f + (-getRotation()))) - 1.5707963267948966d)) * floatValue;
        int min = Math.min(rect.width(), rect.height()) / 2;
        return new Point(((int) (cos * ((float) min))) + rect.centerX(), ((int) (f * ((float) min))) + rect.centerY());
    }

    void b(boolean z, int i) {
        this.J.eu.a(z, this, (PieDonutSlice) this.db.je[i], SelectionMode.POINT_MULTIPLE, false);
    }

    void b(Canvas canvas, Rect rect) {
        if (!((PieDonutSeriesStyle) this.ou).areLabelsShown()) {
            return;
        }
        if (this.oz == null || this.oz.isFinished()) {
            int length = this.db.je.length;
            for (int i = 0; i < length; i++) {
                PieDonutSeriesStyle pieDonutSeriesStyle;
                PieDonutSlice pieDonutSlice = (PieDonutSlice) this.db.je[i];
                if (pieDonutSlice.iU) {
                    pieDonutSeriesStyle = (PieDonutSeriesStyle) this.ov;
                } else {
                    pieDonutSeriesStyle = (PieDonutSeriesStyle) this.ou;
                }
                pieDonutSlice.mJ = a(rect, i);
                pieDonutSlice.mK = new Point(pieDonutSlice.mJ.x, pieDonutSlice.mJ.y);
                pieDonutSlice.mM.setColor(pieDonutSeriesStyle.getLabelBackgroundColor());
                pieDonutSlice.mL.setTextAlign(Align.CENTER);
                pieDonutSlice.mL.setAntiAlias(true);
                pieDonutSlice.mL.setColor(pieDonutSeriesStyle.getLabelTextColor());
                pieDonutSlice.mL.setTypeface(pieDonutSeriesStyle.getLabelTypeface());
                pieDonutSlice.mL.setTextSize(pieDonutSeriesStyle.getLabelTextSize() * this.J.getResources().getDisplayMetrics().scaledDensity);
                this.J.a(pieDonutSlice, this);
                Rect a = this.mb.a(pieDonutSlice.mK.x, pieDonutSlice.mK.y, pieDonutSlice.mE, pieDonutSeriesStyle.getLabelTextSize(), pieDonutSeriesStyle.getLabelTypeface(), this.J);
                if (!this.J.a(canvas, pieDonutSlice, a, this)) {
                    if (pieDonutSlice.mM.getColor() != 0) {
                        ChartUtils.drawTextBackground(canvas, a, pieDonutSlice.mM);
                    }
                    ChartUtils.drawText(canvas, pieDonutSlice.mE, pieDonutSlice.mK.x, pieDonutSlice.mK.y, pieDonutSlice.mL);
                }
            }
        }
    }

    String C(int i) {
        return ((PieDonutSlice) this.db.je[i]).mE;
    }

    Drawable c(float f) {
        return null;
    }

    Drawable b(int i, float f) {
        int i2 = 0;
        PieDonutSeriesStyle pieDonutSeriesStyle = (!this.db.je[i].iU || this.ov == null) ? (PieDonutSeriesStyle) this.ou : (PieDonutSeriesStyle) this.ov;
        if (pieDonutSeriesStyle.eC()) {
            return new cl();
        }
        int flavorColorAtIndex = pieDonutSeriesStyle.isFlavorShown() ? pieDonutSeriesStyle.flavorColorAtIndex(i) : 0;
        if (pieDonutSeriesStyle.isCrustShown()) {
            i2 = pieDonutSeriesStyle.crustColorAtIndex(i);
        }
        return new ch(flavorColorAtIndex, i2, f);
    }

    void a(PieDonutSlice pieDonutSlice, boolean z) {
        a(pieDonutSlice);
        if (z) {
            a(1.5f, this.md, this.me, this.mf, this.mi);
        } else {
            this.mi.execute();
        }
    }

    private void a(PieDonutSlice pieDonutSlice) {
        this.mi.mn = this.lS;
        this.mi.ml = ((PieDonutSeriesStyle) this.ov).getProtrusion();
        this.mi.mm = ((PieDonutSeriesStyle) this.ou).getProtrusion();
        if (this.mg == null || !pieDonutSlice.iU) {
            this.mi.mo = 0.0f;
            return;
        }
        float o = this.lS + dn().o(pieDonutSlice.getCenterAngle());
        this.mi.mo = this.mg.floatValue() - o;
        this.mi.mo = cv.k(this.mi.mo);
    }

    private void a(float f, AnimationCurve animationCurve, AnimationCurve animationCurve2, AnimationCurve animationCurve3, a aVar) {
        Animation dpVar = new dp();
        dpVar.setDuration(f);
        dpVar.b(animationCurve);
        dpVar.c(animationCurve2);
        dpVar.d(animationCurve3);
        this.mh.a(dpVar);
        this.mh.a((a) aVar);
        this.mh.start();
    }

    void ac() {
        for (InternalDataPoint internalDataPoint : this.db.je) {
            PieDonutSlice pieDonutSlice = (PieDonutSlice) internalDataPoint;
            pieDonutSlice.mI = pieDonutSlice.iU ? ((PieDonutSeriesStyle) this.ov).getProtrusion() : ((PieDonutSeriesStyle) this.ou).getProtrusion();
        }
        super.ac();
    }

    eb dn() {
        return eb.a(this.lR);
    }

    double as() {
        return 0.0d;
    }

    double al() {
        return 0.0d;
    }

    final NumberRange aN() {
        return this.dW;
    }
}
