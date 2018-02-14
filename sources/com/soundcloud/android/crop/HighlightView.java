package com.soundcloud.android.crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.os.Build.VERSION;
import android.util.TypedValue;
import android.view.View;
import com.rachio.iro.R;

final class HighlightView {
    RectF cropRect;
    Rect drawRect;
    private int handleMode$5d571a8b = HandleMode.Changing$5d571a8b;
    private final Paint handlePaint = new Paint();
    private float handleRadius;
    private int highlightColor;
    private RectF imageRect;
    private float initialAspectRatio;
    private boolean isFocused;
    private boolean maintainAspectRatio;
    Matrix matrix;
    private int modifyMode$899a623 = ModifyMode.None$899a623;
    private final Paint outlinePaint = new Paint();
    private float outlineWidth;
    private final Paint outsidePaint = new Paint();
    private boolean showCircle;
    private boolean showThirds;
    private View viewContext;

    enum HandleMode {
        ;

        static {
            Changing$5d571a8b = 1;
            Always$5d571a8b = 2;
            Never$5d571a8b = 3;
            $VALUES$53a33390 = new int[]{1, 2, 3};
        }

        public static int[] values$6477e16f() {
            return (int[]) $VALUES$53a33390.clone();
        }
    }

    enum ModifyMode {
        ;

        static {
            None$899a623 = 1;
            Move$899a623 = 2;
            Grow$899a623 = 3;
            $VALUES$466c0bc2 = new int[]{1, 2, 3};
        }
    }

    public HighlightView(View context) {
        this.viewContext = context;
        Context context2 = context.getContext();
        TypedValue typedValue = new TypedValue();
        context2.getTheme().resolveAttribute(R.attr.cropImageStyle, typedValue, true);
        TypedArray obtainStyledAttributes = context2.obtainStyledAttributes(typedValue.resourceId, R.styleable.CropImageView);
        try {
            this.showThirds = obtainStyledAttributes.getBoolean(R.styleable.CropImageView_showThirds, false);
            this.showCircle = obtainStyledAttributes.getBoolean(R.styleable.CropImageView_showCircle, false);
            this.highlightColor = obtainStyledAttributes.getColor(R.styleable.CropImageView_highlightColor, -13388315);
            this.handleMode$5d571a8b = HandleMode.values$6477e16f()[obtainStyledAttributes.getInt(R.styleable.CropImageView_showHandles, 0)];
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public final void setup(Matrix m, Rect imageRect, RectF cropRect, boolean maintainAspectRatio) {
        this.matrix = new Matrix(m);
        this.cropRect = cropRect;
        this.imageRect = new RectF(imageRect);
        this.maintainAspectRatio = maintainAspectRatio;
        this.initialAspectRatio = this.cropRect.width() / this.cropRect.height();
        this.drawRect = computeLayout();
        this.outsidePaint.setARGB(125, 50, 50, 50);
        this.outlinePaint.setStyle(Style.STROKE);
        this.outlinePaint.setAntiAlias(true);
        this.outlineWidth = dpToPx(2.0f);
        this.handlePaint.setColor(this.highlightColor);
        this.handlePaint.setStyle(Style.FILL);
        this.handlePaint.setAntiAlias(true);
        this.handleRadius = dpToPx(12.0f);
        this.modifyMode$899a623 = ModifyMode.None$899a623;
    }

    private float dpToPx(float dp) {
        return this.viewContext.getResources().getDisplayMetrics().density * dp;
    }

    protected final void draw(Canvas canvas) {
        Object obj = 1;
        canvas.save();
        Path path = new Path();
        this.outlinePaint.setStrokeWidth(this.outlineWidth);
        if (this.isFocused) {
            Rect viewDrawingRect = new Rect();
            this.viewContext.getDrawingRect(viewDrawingRect);
            path.addRect(new RectF(this.drawRect), Direction.CW);
            this.outlinePaint.setColor(this.highlightColor);
            if (VERSION.SDK_INT == 17 || (VERSION.SDK_INT >= 14 && VERSION.SDK_INT <= 15 && canvas.isHardwareAccelerated())) {
                obj = null;
            }
            if (obj != null) {
                canvas.clipPath(path, Op.DIFFERENCE);
                canvas.drawRect(viewDrawingRect, this.outsidePaint);
            } else {
                canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) this.drawRect.top, this.outsidePaint);
                canvas.drawRect(0.0f, (float) this.drawRect.bottom, (float) canvas.getWidth(), (float) canvas.getHeight(), this.outsidePaint);
                canvas.drawRect(0.0f, (float) this.drawRect.top, (float) this.drawRect.left, (float) this.drawRect.bottom, this.outsidePaint);
                canvas.drawRect((float) this.drawRect.right, (float) this.drawRect.top, (float) canvas.getWidth(), (float) this.drawRect.bottom, this.outsidePaint);
            }
            canvas.restore();
            canvas.drawPath(path, this.outlinePaint);
            if (this.showThirds) {
                this.outlinePaint.setStrokeWidth(1.0f);
                float f = (float) ((this.drawRect.right - this.drawRect.left) / 3);
                float f2 = (float) ((this.drawRect.bottom - this.drawRect.top) / 3);
                canvas.drawLine(((float) this.drawRect.left) + f, (float) this.drawRect.top, ((float) this.drawRect.left) + f, (float) this.drawRect.bottom, this.outlinePaint);
                Canvas canvas2 = canvas;
                canvas2.drawLine((f * 2.0f) + ((float) this.drawRect.left), (float) this.drawRect.top, (f * 2.0f) + ((float) this.drawRect.left), (float) this.drawRect.bottom, this.outlinePaint);
                canvas.drawLine((float) this.drawRect.left, ((float) this.drawRect.top) + f2, (float) this.drawRect.right, ((float) this.drawRect.top) + f2, this.outlinePaint);
                canvas2 = canvas;
                canvas2.drawLine((float) this.drawRect.left, (f2 * 2.0f) + ((float) this.drawRect.top), (float) this.drawRect.right, (f2 * 2.0f) + ((float) this.drawRect.top), this.outlinePaint);
            }
            if (this.showCircle) {
                this.outlinePaint.setStrokeWidth(1.0f);
                canvas.drawOval(new RectF(this.drawRect), this.outlinePaint);
            }
            if (this.handleMode$5d571a8b == HandleMode.Always$5d571a8b || (this.handleMode$5d571a8b == HandleMode.Changing$5d571a8b && this.modifyMode$899a623 == ModifyMode.Grow$899a623)) {
                int i = this.drawRect.left + ((this.drawRect.right - this.drawRect.left) / 2);
                int i2 = this.drawRect.top + ((this.drawRect.bottom - this.drawRect.top) / 2);
                canvas.drawCircle((float) this.drawRect.left, (float) i2, this.handleRadius, this.handlePaint);
                canvas.drawCircle((float) i, (float) this.drawRect.top, this.handleRadius, this.handlePaint);
                canvas.drawCircle((float) this.drawRect.right, (float) i2, this.handleRadius, this.handlePaint);
                canvas.drawCircle((float) i, (float) this.drawRect.bottom, this.handleRadius, this.handlePaint);
                return;
            }
            return;
        }
        this.outlinePaint.setColor(-16777216);
        canvas.drawRect(this.drawRect, this.outlinePaint);
    }

    public final void setMode$27ea16d8(int mode) {
        if (mode != this.modifyMode$899a623) {
            this.modifyMode$899a623 = mode;
            this.viewContext.invalidate();
        }
    }

    public final int getHit(float x, float y) {
        Rect r = computeLayout();
        int retval = 1;
        boolean verticalCheck;
        if (y < ((float) r.top) - 20.0f || y >= ((float) r.bottom) + 20.0f) {
            verticalCheck = false;
        } else {
            verticalCheck = true;
        }
        boolean horizCheck;
        if (x < ((float) r.left) - 20.0f || x >= ((float) r.right) + 20.0f) {
            horizCheck = false;
        } else {
            horizCheck = true;
        }
        if (Math.abs(((float) r.left) - x) < 20.0f && verticalCheck) {
            retval = 3;
        }
        if (Math.abs(((float) r.right) - x) < 20.0f && verticalCheck) {
            retval |= 4;
        }
        if (Math.abs(((float) r.top) - y) < 20.0f && horizCheck) {
            retval |= 8;
        }
        if (Math.abs(((float) r.bottom) - y) < 20.0f && horizCheck) {
            retval |= 16;
        }
        if (retval == 1 && r.contains((int) x, (int) y)) {
            return 32;
        }
        return retval;
    }

    final void handleMotion(int edge, float dx, float dy) {
        int i = -1;
        Rect r = computeLayout();
        if (edge == 32) {
            float width = (this.cropRect.width() / ((float) r.width())) * dx;
            float height = (this.cropRect.height() / ((float) r.height())) * dy;
            Rect rect = new Rect(this.drawRect);
            this.cropRect.offset(width, height);
            this.cropRect.offset(Math.max(0.0f, this.imageRect.left - this.cropRect.left), Math.max(0.0f, this.imageRect.top - this.cropRect.top));
            this.cropRect.offset(Math.min(0.0f, this.imageRect.right - this.cropRect.right), Math.min(0.0f, this.imageRect.bottom - this.cropRect.bottom));
            this.drawRect = computeLayout();
            rect.union(this.drawRect);
            rect.inset(-((int) this.handleRadius), -((int) this.handleRadius));
            this.viewContext.invalidate(rect);
            return;
        }
        int i2;
        RectF rectF;
        if ((edge & 6) == 0) {
            dx = 0.0f;
        }
        if ((edge & 24) == 0) {
            dy = 0.0f;
        }
        float xDelta = dx * (this.cropRect.width() / ((float) r.width()));
        float yDelta = dy * (this.cropRect.height() / ((float) r.height()));
        if ((edge & 2) != 0) {
            i2 = -1;
        } else {
            i2 = 1;
        }
        float f = ((float) i2) * xDelta;
        if ((edge & 8) == 0) {
            i = 1;
        }
        width = ((float) i) * yDelta;
        if (this.maintainAspectRatio) {
            if (f != 0.0f) {
                width = f / this.initialAspectRatio;
                height = f;
            } else if (width != 0.0f) {
                height = this.initialAspectRatio * width;
            }
            rectF = new RectF(this.cropRect);
            if (height > 0.0f && rectF.width() + (height * 2.0f) > this.imageRect.width()) {
                height = (this.imageRect.width() - rectF.width()) / 2.0f;
                if (this.maintainAspectRatio) {
                    width = height / this.initialAspectRatio;
                }
            }
            if (width > 0.0f && rectF.height() + (width * 2.0f) > this.imageRect.height()) {
                width = (this.imageRect.height() - rectF.height()) / 2.0f;
                if (this.maintainAspectRatio) {
                    height = this.initialAspectRatio * width;
                }
            }
            rectF.inset(-height, -width);
            if (rectF.width() < 25.0f) {
                rectF.inset((-(25.0f - rectF.width())) / 2.0f, 0.0f);
            }
            width = this.maintainAspectRatio ? 25.0f / this.initialAspectRatio : 25.0f;
            if (rectF.height() < width) {
                rectF.inset(0.0f, (-(width - rectF.height())) / 2.0f);
            }
            if (rectF.left < this.imageRect.left) {
                rectF.offset(this.imageRect.left - rectF.left, 0.0f);
            } else if (rectF.right > this.imageRect.right) {
                rectF.offset(-(rectF.right - this.imageRect.right), 0.0f);
            }
            if (rectF.top < this.imageRect.top) {
                rectF.offset(0.0f, this.imageRect.top - rectF.top);
            } else if (rectF.bottom > this.imageRect.bottom) {
                rectF.offset(0.0f, -(rectF.bottom - this.imageRect.bottom));
            }
            this.cropRect.set(rectF);
            this.drawRect = computeLayout();
            this.viewContext.invalidate();
        }
        height = f;
        rectF = new RectF(this.cropRect);
        height = (this.imageRect.width() - rectF.width()) / 2.0f;
        if (this.maintainAspectRatio) {
            width = height / this.initialAspectRatio;
        }
        width = (this.imageRect.height() - rectF.height()) / 2.0f;
        if (this.maintainAspectRatio) {
            height = this.initialAspectRatio * width;
        }
        rectF.inset(-height, -width);
        if (rectF.width() < 25.0f) {
            rectF.inset((-(25.0f - rectF.width())) / 2.0f, 0.0f);
        }
        if (this.maintainAspectRatio) {
        }
        if (rectF.height() < width) {
            rectF.inset(0.0f, (-(width - rectF.height())) / 2.0f);
        }
        if (rectF.left < this.imageRect.left) {
            rectF.offset(this.imageRect.left - rectF.left, 0.0f);
        } else if (rectF.right > this.imageRect.right) {
            rectF.offset(-(rectF.right - this.imageRect.right), 0.0f);
        }
        if (rectF.top < this.imageRect.top) {
            rectF.offset(0.0f, this.imageRect.top - rectF.top);
        } else if (rectF.bottom > this.imageRect.bottom) {
            rectF.offset(0.0f, -(rectF.bottom - this.imageRect.bottom));
        }
        this.cropRect.set(rectF);
        this.drawRect = computeLayout();
        this.viewContext.invalidate();
    }

    private Rect computeLayout() {
        RectF r = new RectF(this.cropRect.left, this.cropRect.top, this.cropRect.right, this.cropRect.bottom);
        this.matrix.mapRect(r);
        return new Rect(Math.round(r.left), Math.round(r.top), Math.round(r.right), Math.round(r.bottom));
    }

    public final void invalidate() {
        this.drawRect = computeLayout();
    }

    public final boolean hasFocus() {
        return this.isFocused;
    }

    public final void setFocus(boolean isFocused) {
        this.isFocused = true;
    }
}
