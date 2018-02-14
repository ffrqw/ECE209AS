package com.instabug.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.rachio.iro.R;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class AnnotationView extends ImageView {
    private Canvas a;
    private Path b;
    private Paint c;
    private int d;
    private boolean e = false;
    private LinkedHashMap<Path, Integer> f = new LinkedHashMap();
    private float g;
    private float h;

    public AnnotationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
        setFocusableInTouchMode(true);
        b();
    }

    public AnnotationView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        b();
    }

    public AnnotationView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setFocusable(true);
        setFocusableInTouchMode(true);
        b();
    }

    private void b() {
        this.c = new Paint();
        this.c.setAntiAlias(true);
        this.c.setDither(true);
        if (isInEditMode()) {
            this.d = -16733521;
        } else {
            this.d = getResources().getColor(R.color.instabug_annotation_color_default);
        }
        this.c.setColor(this.d);
        this.c.setStyle(Style.STROKE);
        this.c.setStrokeJoin(Join.ROUND);
        this.c.setStrokeCap(Cap.ROUND);
        this.c.setStrokeWidth(2.0f * getContext().getResources().getDisplayMetrics().density);
        this.a = new Canvas();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.f.isEmpty()) {
            Iterator it = this.f.entrySet().iterator();
            do {
                Entry entry = (Entry) it.next();
                this.c.setColor(((Integer) entry.getValue()).intValue());
                canvas.drawPath((Path) entry.getKey(), this.c);
            } while (it.hasNext());
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case 0:
                this.e = false;
                this.b = new Path();
                this.f.put(this.b, Integer.valueOf(this.d));
                this.b.reset();
                this.b.moveTo(x, y);
                this.g = x;
                this.h = y;
                break;
            case 1:
                this.b.lineTo(this.g, this.h);
                this.a.drawPath(this.b, this.c);
                if (!this.e) {
                    performClick();
                }
                invalidate();
                break;
            case 2:
                this.e = true;
                float abs = Math.abs(x - this.g);
                float abs2 = Math.abs(y - this.h);
                if (abs >= 4.0f || abs2 >= 4.0f) {
                    this.b.quadTo(this.g, this.h, (this.g + x) / 2.0f, (this.h + y) / 2.0f);
                    this.g = x;
                    this.h = y;
                }
                invalidate();
                break;
        }
        return true;
    }

    public final void a() {
        this.f.clear();
        invalidate();
    }

    public final void a(int i) {
        this.d = getResources().getColor(i);
    }
}
