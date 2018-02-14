package com.rachio.iro.ui.newschedulerulepath.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import com.rachio.iro.R;

public class RingsView extends BaseCanvasView {
    private float centerX;
    private float centerY;
    private Drawable check;
    private double checkCircleDelta;
    private double checkDelta;
    private boolean complete;
    private Handler handler;
    private AccelerateInterpolator interpolator;
    private double last;
    private Listener listener;
    private double[] ringDeltas;
    private Paint ringPaint;
    private float ringRadius;
    private int ringStrokeWidth;
    private Paint solidPaint;

    public interface Listener {
        void onCompleteAnimationComplete();
    }

    public RingsView(Context context) {
        this(context, null);
    }

    public RingsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ringPaint = new Paint();
        this.solidPaint = new Paint();
        this.interpolator = new AccelerateInterpolator();
        this.handler = new Handler();
        this.last = -1.0d;
        this.complete = false;
        this.checkCircleDelta = 0.0d;
        this.checkDelta = 0.0d;
        this.ringDeltas = new double[]{0.0d, -0.20000000298023224d, -0.4000000059604645d};
        int dotColour = context.getResources().getColor(R.color.rachio_blue);
        this.solidPaint.setColor(dotColour);
        this.solidPaint.setAntiAlias(true);
        this.ringStrokeWidth = (int) TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics());
        this.ringPaint.setColor(dotColour);
        this.ringPaint.setStrokeWidth((float) this.ringStrokeWidth);
        this.ringPaint.setStyle(Style.STROKE);
        this.ringPaint.setAntiAlias(true);
        this.check = getResources().getDrawable(R.drawable.tinycheck);
    }

    protected final int getDefaultSize() {
        return 60;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        double now = (double) System.currentTimeMillis();
        if (this.last == -1.0d) {
            this.last = now;
        }
        double diff = ((now - this.last) / 1000.0d) / 2.0d;
        this.last = now;
        boolean invalidate = true;
        canvas.save();
        float scale = 1.0f - ((float) this.checkCircleDelta);
        canvas.translate(this.centerX, this.centerY);
        canvas.scale(scale, scale);
        int r = 0;
        while (r < this.ringDeltas.length) {
            double[] dArr = this.ringDeltas;
            dArr[r] = dArr[r] + diff;
            if (this.ringDeltas[r] >= 0.0d && this.ringDeltas[r] < 1.0d) {
                canvas.drawCircle(0.0f, 0.0f, this.ringRadius * this.interpolator.getInterpolation((float) this.ringDeltas[r]), this.ringPaint);
            } else if (this.ringDeltas[r] >= 1.5d) {
                this.ringDeltas[r] = 0.0d;
            }
            r++;
        }
        canvas.drawCircle(0.0f, 0.0f, this.ringRadius, this.ringPaint);
        canvas.restore();
        if (this.complete) {
            double multipliedDiff = diff * 4.0d;
            if (this.checkCircleDelta < 1.0d) {
                this.checkCircleDelta = Math.min(this.checkCircleDelta + multipliedDiff, 1.0d);
            } else {
                this.checkDelta = Math.min(this.checkDelta + multipliedDiff, 1.0d);
                if (this.checkDelta == 1.0d) {
                    invalidate = false;
                    if (this.listener != null) {
                        this.handler.postDelayed(new Runnable() {
                            public void run() {
                                RingsView.this.listener.onCompleteAnimationComplete();
                            }
                        }, 2000);
                    }
                }
            }
            canvas.drawCircle(this.centerY, this.centerX, this.ringRadius * this.interpolator.getInterpolation((float) this.checkCircleDelta), this.solidPaint);
            if (this.checkCircleDelta == 1.0d) {
                this.check.draw(canvas);
            }
        }
        if (invalidate) {
            invalidate();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float ringDiameter = (float) Math.min(width, height);
        this.ringRadius = (ringDiameter / 2.0f) - ((float) this.ringStrokeWidth);
        this.centerY = ((float) width) / 2.0f;
        this.centerX = ((float) height) / 2.0f;
        int checkSize = (int) ringDiameter;
        int padX = (width - checkSize) / 2;
        int padY = (height - checkSize) / 2;
        this.check.setBounds(padX, padY, padX + checkSize, padY + checkSize);
    }

    public final void setComplete(boolean complete) {
        if (this.complete) {
            throw new IllegalStateException();
        }
        this.complete = true;
    }

    public final void setListener(Listener listener) {
        this.listener = listener;
    }
}
