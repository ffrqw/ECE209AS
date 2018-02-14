package com.shinobicontrols.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import com.shinobicontrols.charts.Annotation.Position;

@SuppressLint({"ViewConstructor"})
class x extends ViewGroup {
    final ag dF;
    private final Paint dH = new Paint();
    private Bitmap dI;

    x(Context context, ag agVar) {
        super(context);
        setWillNotDraw(false);
        this.dF = agVar;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int size2 = MeasureSpec.getSize(heightMeasureSpec);
        this.dF.J.eN.a(MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE), Position.BEHIND_DATA);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.dF.J.eN.a(this.dF.aX.left, this.dF.aX.top, this.dF.aX.right, this.dF.aX.bottom, Position.BEHIND_DATA);
    }

    protected void onDraw(Canvas canvas) {
        this.dH.setColor(this.dF.J.getStyle().getPlotAreaBackgroundColor());
        canvas.drawRect(this.dF.aX, this.dH);
        this.dF.b(canvas);
        canvas.clipRect(this.dF.aX);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.dI != null) {
            canvas.drawBitmap(this.dI, null, this.dF.aX, this.dH);
            this.dI = null;
        }
    }

    void a(Bitmap bitmap) {
        this.dI = bitmap;
    }
}
