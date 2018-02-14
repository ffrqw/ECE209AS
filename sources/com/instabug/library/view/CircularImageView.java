package com.instabug.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.instabug.library.R;

public class CircularImageView extends ImageView {
    private static final ScaleType a = ScaleType.CENTER_CROP;
    private static final Config b = Config.ARGB_8888;
    private final RectF c;
    private final RectF d;
    private final Matrix e;
    private final Paint f;
    private final Paint g;
    private final Paint h;
    private int i;
    private int j;
    private int k;
    private Bitmap l;
    private BitmapShader m;
    private int n;
    private int o;
    private float p;
    private float q;
    private ColorFilter r;
    private boolean s;
    private boolean t;
    private boolean u;

    public CircularImageView(Context context) {
        super(context);
        this.c = new RectF();
        this.d = new RectF();
        this.e = new Matrix();
        this.f = new Paint();
        this.g = new Paint();
        this.h = new Paint();
        this.i = -16777216;
        this.j = 0;
        this.k = 0;
        a();
    }

    public CircularImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CircularImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.c = new RectF();
        this.d = new RectF();
        this.e = new Matrix();
        this.f = new Paint();
        this.g = new Paint();
        this.h = new Paint();
        this.i = -16777216;
        this.j = 0;
        this.k = 0;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CircleImageView, i, 0);
        this.j = obtainStyledAttributes.getDimensionPixelSize(R.styleable.CircleImageView_ibg_civ_border_width, 0);
        this.i = obtainStyledAttributes.getColor(R.styleable.CircleImageView_ibg_civ_border_color, -16777216);
        this.u = obtainStyledAttributes.getBoolean(R.styleable.CircleImageView_ibg_civ_border_overlay, false);
        this.k = obtainStyledAttributes.getColor(R.styleable.CircleImageView_ibg_civ_fill_color, 0);
        obtainStyledAttributes.recycle();
        a();
    }

    private void a() {
        super.setScaleType(a);
        this.s = true;
        if (this.t) {
            b();
            this.t = false;
        }
    }

    public ScaleType getScaleType() {
        return a;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType != a) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", new Object[]{scaleType}));
        }
    }

    public void setAdjustViewBounds(boolean z) {
        if (z) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.l != null) {
            if (this.k != 0) {
                canvas.drawCircle(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, this.p, this.h);
            }
            canvas.drawCircle(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, this.p, this.f);
            if (this.j != 0) {
                canvas.drawCircle(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f, this.q, this.g);
            }
        }
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        b();
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        this.l = bitmap;
        b();
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        this.l = a(drawable);
        b();
    }

    public void setImageResource(int i) {
        super.setImageResource(i);
        this.l = a(getDrawable());
        b();
    }

    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        this.l = uri != null ? a(getDrawable()) : null;
        b();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter != this.r) {
            this.r = colorFilter;
            this.f.setColorFilter(this.r);
            invalidate();
        }
    }

    private static Bitmap a(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap createBitmap;
            if (drawable instanceof ColorDrawable) {
                createBitmap = Bitmap.createBitmap(2, 2, b);
            } else {
                createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), b);
            }
            Canvas canvas = new Canvas(createBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return createBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void b() {
        float f = 0.0f;
        if (!this.s) {
            this.t = true;
        } else if (getWidth() != 0 || getHeight() != 0) {
            if (this.l == null) {
                invalidate();
                return;
            }
            float height;
            float width;
            this.m = new BitmapShader(this.l, TileMode.CLAMP, TileMode.CLAMP);
            this.f.setAntiAlias(true);
            this.f.setShader(this.m);
            this.g.setStyle(Style.STROKE);
            this.g.setAntiAlias(true);
            this.g.setColor(this.i);
            this.g.setStrokeWidth((float) this.j);
            this.h.setStyle(Style.FILL);
            this.h.setAntiAlias(true);
            this.h.setColor(this.k);
            this.o = this.l.getHeight();
            this.n = this.l.getWidth();
            this.d.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
            this.q = Math.min((this.d.height() - ((float) this.j)) / 2.0f, (this.d.width() - ((float) this.j)) / 2.0f);
            this.c.set(this.d);
            if (!this.u) {
                this.c.inset((float) this.j, (float) this.j);
            }
            this.p = Math.min(this.c.height() / 2.0f, this.c.width() / 2.0f);
            this.e.set(null);
            if (((float) this.n) * this.c.height() > this.c.width() * ((float) this.o)) {
                height = this.c.height() / ((float) this.o);
                width = (this.c.width() - (((float) this.n) * height)) * 0.5f;
            } else {
                height = this.c.width() / ((float) this.n);
                width = 0.0f;
                f = (this.c.height() - (((float) this.o) * height)) * 0.5f;
            }
            this.e.setScale(height, height);
            this.e.postTranslate(((float) ((int) (width + 0.5f))) + this.c.left, ((float) ((int) (f + 0.5f))) + this.c.top);
            this.m.setLocalMatrix(this.e);
            invalidate();
        }
    }
}
