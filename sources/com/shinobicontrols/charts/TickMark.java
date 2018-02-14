package com.shinobicontrols.charts;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

public final class TickMark {
    private final Axis<?, ?> aS;
    private int bk;
    private int bm;
    private int bn;
    private Typeface bo;
    private int bt;
    private int bu;
    String mE;
    private final Point mK = new Point();
    private final ChartUtils mb = new ChartUtils();
    private int qB;
    boolean rR = false;
    boolean rS = false;
    boolean rT;
    boolean rU;
    private float rV;
    private double rW = Double.NEGATIVE_INFINITY;
    private final Paint rX = new Paint();
    private final TextPaint rY;
    private final Paint rZ = new Paint();
    private final Paint sa = new Paint();
    final Rect sb = new Rect();
    private final Rect sc = new Rect();
    private final Path sd = new Path();
    private final PointF se = new PointF();
    private final PointF sf = new PointF();
    double value;

    public enum ClippingMode {
        NEITHER_PERSIST,
        TICKS_AND_LABELS_PERSIST,
        TICKS_PERSIST
    }

    public enum Orientation {
        HORIZONTAL(0),
        VERTICAL(1),
        DIAGONAL(2);
        
        private final int mp;

        private Orientation(int xmlValue) {
            this.mp = xmlValue;
        }

        public final int getXmlValue() {
            return this.mp;
        }
    }

    TickMark(Axis<?, ?> axis) {
        this.aS = axis;
        this.rY = new TextPaint();
        this.rY.setTextAlign(Align.CENTER);
        this.rY.setAntiAlias(true);
    }

    final void a(Canvas canvas, fe feVar, int i, c cVar) {
        int b = (int) (this.aS.b(this.value, (double) cVar.bw, cVar.bx) + 0.5d);
        boolean z = this.value <= this.aS.ai.nw;
        a(canvas, cVar, b, z);
        if (z) {
            a(canvas, cVar, b);
        }
        if (z && this.rR && (!(this.aS instanceof CategoryAxis) || this.value <= this.aS.aj.nw)) {
            b(canvas, cVar, b);
        }
        if (this.rR && cVar.bc && (!(this.aS instanceof CategoryAxis) || (this.value > this.aS.aj.nv && this.value <= this.aS.aj.nw))) {
            feVar.a(i, this.se, this, this.sf, cVar);
            if ((this.aS.h() && this.sf.x > this.se.x) || (!this.aS.h() && this.sf.y < this.se.y)) {
                a(canvas, cVar);
            }
        }
        this.aS.J.a(this, this.aS);
        a(canvas, z, cVar);
    }

    private void a(Canvas canvas, c cVar, int i, boolean z) {
        this.aS.ao.a(this.sb, cVar, i, this.rR);
        if (z && this.bk != cVar.bk) {
            this.bk = cVar.bk;
            this.rX.setColor(this.bk);
        }
    }

    private void a(Canvas canvas, c cVar, int i) {
        this.aS.ao.a(this.mK, cVar, i);
        if (this.bm != cVar.bm) {
            this.rY.setColor(cVar.bm);
            this.bm = cVar.bm;
        }
        if (this.bn != cVar.bn) {
            this.rY.setShadowLayer(1.0f, 1.0f, 1.0f, cVar.bn);
            this.bn = cVar.bn;
        }
        if (this.bo != cVar.bo) {
            this.rY.setTypeface(cVar.bo);
            this.bo = cVar.bo;
        }
        if (this.rV != cVar.bp) {
            this.rY.setTextSize(cVar.bp);
            this.rV = cVar.bp;
        }
        if (this.value != this.rW) {
            this.mE = this.aS.a(this.value);
            this.mE = Axis.a(this.mE) ? " " : this.mE.trim();
            this.rW = this.value;
        }
    }

    private void b(Canvas canvas, c cVar, int i) {
        this.rZ.setStrokeWidth((float) ca.c(this.aS.density, cVar.bf));
        this.aS.ao.c(this.sd, cVar, i, this.rZ);
        this.rZ.setStyle(Style.STROKE);
        if (this.qB != cVar.bl) {
            this.qB = cVar.bl;
            this.rZ.setColor(this.qB);
        }
        if (cVar.br) {
            this.rZ.setPathEffect(cVar.bs);
        }
    }

    private void a(Canvas canvas, c cVar) {
        if (this.bt != cVar.bt) {
            this.bt = cVar.bt;
        }
        if (this.bu != cVar.bu) {
            this.bu = cVar.bu;
        }
        if (this.rS) {
            this.sa.setColor(this.bu);
        } else {
            this.sa.setColor(this.bt);
        }
        this.aS.ao.c(this.sc, cVar, this.se, this.sf);
    }

    final void eS() {
        this.rW = Double.NEGATIVE_INFINITY;
    }

    private void a(Canvas canvas, boolean z, c cVar) {
        String str;
        ChartUtils chartUtils = this.mb;
        int i = this.mK.x;
        int i2 = this.mK.y;
        if (this.aS.aI != null) {
            str = this.aS.aI;
        } else {
            str = this.aS.aH;
        }
        Rect a = chartUtils.a(i, i2, str, this.aS.getStyle().getTickStyle().getLabelTextSize(), this.aS.getStyle().getTickStyle().getLabelTypeface(), this.aS.J);
        if (z && !this.aS.J.a(canvas, this, a, this.sb, this.aS)) {
            if (this.rU) {
                canvas.drawRect(this.sb, this.rX);
            }
            if (this.rT) {
                if (cVar.bq != Orientation.HORIZONTAL) {
                    canvas.save();
                    canvas.rotate(cVar.bq == Orientation.DIAGONAL ? -45.0f : -90.0f, (float) this.mK.x, (float) this.mK.y);
                }
                ChartUtils.drawText(canvas, this.mE, this.mK.x, this.mK.y, this.rY);
                if (cVar.bq != Orientation.HORIZONTAL) {
                    canvas.restore();
                }
            }
        }
        if (z && this.rR && cVar.bb && (!(this.aS instanceof CategoryAxis) || this.value <= this.aS.aj.nw)) {
            canvas.drawPath(this.sd, this.rZ);
        }
        if (!this.rR || !cVar.bc) {
            return;
        }
        if ((this.aS instanceof CategoryAxis) && (this.value <= this.aS.aj.nv || this.value > this.aS.aj.nw)) {
            return;
        }
        if ((this.aS.h() && this.sf.x > this.se.x) || (!this.aS.h() && this.sf.y < this.se.y)) {
            canvas.drawRect(this.sc, this.sa);
        }
    }

    public final boolean isMajor() {
        return this.rR;
    }

    public final Object getValue() {
        return this.aS.transformInternalValueToUser(this.value);
    }

    public final boolean isLabelShown() {
        return this.rT;
    }

    public final void setLabelShown(boolean showLabel) {
        this.rT = showLabel;
    }

    public final boolean isLineShown() {
        return this.rU;
    }

    public final void setLineShown(boolean showLine) {
        this.rU = showLine;
    }

    public final String getLabelText() {
        return this.mE;
    }

    public final void setLabelText(String labelText) {
        this.mE = labelText;
    }

    public final Point getLabelCenter() {
        return this.mK;
    }

    public final Paint getLinePaint() {
        return this.rX;
    }

    public final TextPaint getLabelPaint() {
        return this.rY;
    }
}
