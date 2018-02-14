package com.rachio.iro.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.rachio.iro.R;

@Deprecated
public class ColoredImageView extends ImageView {
    private int color;
    private boolean disabled = false;

    public ColoredImageView(Context context) {
        super(context);
    }

    public ColoredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColoredImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColoredImageView);
        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    this.color = Color.parseColor(a.getString(attr));
                    invalidate();
                    break;
                default:
                    break;
            }
        }
        a.recycle();
    }

    protected void onDraw(Canvas canvas) {
        Drawable d = getDrawable();
        if (d != null) {
            d.setColorFilter(this.color, Mode.MULTIPLY);
            super.onDraw(canvas);
            d.clearColorFilter();
        }
    }
}
