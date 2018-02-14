package com.instabug.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ScaleImageView extends ImageView implements OnTouchListener {
    String a;
    private Context b;
    private float c;
    private Matrix d;
    private final float[] e;
    private int f;
    private int g;
    private int h;
    private int i;
    private float j;
    private float k;
    private float l;
    private boolean m;
    private int n;
    private int o;
    private GestureDetector p;

    public ScaleImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.c = 5.0f;
        this.e = new float[9];
        this.a = "ScaleImageView";
        this.b = context;
        b();
    }

    public ScaleImageView(Context context) {
        super(context);
        this.c = 5.0f;
        this.e = new float[9];
        this.a = "ScaleImageView";
        this.b = context;
        b();
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        b();
    }

    public void setImageResource(int i) {
        super.setImageResource(i);
        b();
    }

    private void b() {
        setScaleType(ScaleType.MATRIX);
        this.d = new Matrix();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            this.h = drawable.getIntrinsicWidth();
            this.i = drawable.getIntrinsicHeight();
            setOnTouchListener(this);
        }
        this.p = new GestureDetector(this.b, new SimpleOnGestureListener(this) {
            final /* synthetic */ ScaleImageView a;

            {
                this.a = r1;
            }

            public final boolean onDoubleTap(MotionEvent motionEvent) {
                this.a.a((int) motionEvent.getX(), (int) motionEvent.getY());
                this.a.a();
                return super.onDoubleTap(motionEvent);
            }
        });
    }

    protected boolean setFrame(int i, int i2, int i3, int i4) {
        int i5;
        int i6 = 0;
        this.f = i3 - i;
        this.g = i4 - i2;
        this.d.reset();
        this.j = ((float) (i3 - i)) / ((float) this.h);
        if (this.j * ((float) this.i) > ((float) this.g)) {
            this.j = ((float) this.g) / ((float) this.i);
            this.d.postScale(this.j, this.j);
            i5 = (i3 - this.f) / 2;
        } else {
            this.d.postScale(this.j, this.j);
            i6 = (i4 - this.g) / 2;
            i5 = 0;
        }
        this.d.postTranslate((float) i5, (float) i6);
        setImageMatrix(this.d);
        this.k = this.j;
        a(this.j, this.f / 2, this.g / 2);
        a();
        return super.setFrame(i, i2, i3, i4);
    }

    private float a(Matrix matrix, int i) {
        matrix.getValues(this.e);
        return this.e[i];
    }

    private float getScale() {
        return a(this.d, 0);
    }

    private float getTranslateX() {
        return a(this.d, 2);
    }

    private float getTranslateY() {
        return a(this.d, 5);
    }

    protected final void a(int i, int i2) {
        if (this.k == getScale() || getScale() - this.k <= 0.1f) {
            a(this.c / getScale(), i, i2);
        } else {
            a(this.k / getScale(), i, i2);
        }
    }

    private void a(float f, int i, int i2) {
        if (getScale() * f >= this.k) {
            if (f < 1.0f || getScale() * f <= this.c) {
                this.d.postScale(f, f);
                this.d.postTranslate((-((((float) this.f) * f) - ((float) this.f))) / 2.0f, (-((((float) this.g) * f) - ((float) this.g))) / 2.0f);
                this.d.postTranslate(((float) (-(i - (this.f / 2)))) * f, 0.0f);
                this.d.postTranslate(0.0f, ((float) (-(i2 - (this.g / 2)))) * f);
                setImageMatrix(this.d);
            }
        }
    }

    public final void a() {
        int scale = (int) (((float) this.h) * getScale());
        int scale2 = (int) (((float) this.i) * getScale());
        if (getTranslateX() < ((float) (-(scale - this.f)))) {
            this.d.postTranslate(-((getTranslateX() + ((float) scale)) - ((float) this.f)), 0.0f);
        }
        if (getTranslateX() > 0.0f) {
            this.d.postTranslate(-getTranslateX(), 0.0f);
        }
        if (getTranslateY() < ((float) (-(scale2 - this.g)))) {
            this.d.postTranslate(0.0f, -((getTranslateY() + ((float) scale2)) - ((float) this.g)));
        }
        if (getTranslateY() > 0.0f) {
            this.d.postTranslate(0.0f, -getTranslateY());
        }
        if (scale < this.f) {
            this.d.postTranslate((float) ((this.f - scale) / 2), 0.0f);
        }
        if (scale2 < this.g) {
            this.d.postTranslate(0.0f, (float) ((this.g - scale2) / 2));
        }
        setImageMatrix(this.d);
    }

    private static float a(float f, float f2, float f3, float f4) {
        float f5 = f - f2;
        float f6 = f3 - f4;
        return (float) Math.sqrt((double) ((f5 * f5) + (f6 * f6)));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.p.onTouchEvent(motionEvent)) {
            int pointerCount = motionEvent.getPointerCount();
            switch (motionEvent.getAction()) {
                case 0:
                case 5:
                case 261:
                    if (pointerCount < 2) {
                        this.n = (int) motionEvent.getX();
                        this.o = (int) motionEvent.getY();
                        break;
                    }
                    this.l = a(motionEvent.getX(0), motionEvent.getX(1), motionEvent.getY(0), motionEvent.getY(1));
                    this.m = true;
                    break;
                case 1:
                case 6:
                case 262:
                    if (motionEvent.getPointerCount() <= 1) {
                        this.m = false;
                        break;
                    }
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            if (pointerCount >= 2 && this.m) {
                float a = a(motionEvent.getX(0), motionEvent.getX(1), motionEvent.getY(0), motionEvent.getY(1));
                float sqrt = (a - this.l) / ((float) Math.sqrt((double) ((this.f * this.f) + (this.g * this.g))));
                this.l = a;
                a = 1.0f + sqrt;
                a(a * a, this.f / 2, this.g / 2);
                a();
            } else if (!this.m) {
                pointerCount = this.n - ((int) motionEvent.getX());
                int y = this.o - ((int) motionEvent.getY());
                this.n = (int) motionEvent.getX();
                this.o = (int) motionEvent.getY();
                this.d.postTranslate((float) (-pointerCount), (float) (-y));
                a();
            }
        }
        return true;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent);
    }
}
