package com.instabug.library.c;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import com.instabug.library.IBGFloatingButtonEdge;
import com.rachio.iro.R;

public final class a implements OnClickListener {
    int a;
    int b = 0;
    private LayoutParams c;
    private int d;
    private int e = 0;
    private e f;
    private c g;
    private int h;
    private d i;

    static class a extends SimpleOnGestureListener {
        a() {
        }

        public final boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return Math.abs(motionEvent2.getX() - motionEvent.getX()) < 90.0f && motionEvent2.getY() - motionEvent.getY() > 90.0f;
        }
    }

    public class b extends ImageButton {
        final /* synthetic */ a a;
        private GestureDetector b;
        private boolean c = true;
        private a d;
        private long e;
        private float f;
        private float g;
        private boolean h = false;

        private class a implements Runnable {
            final /* synthetic */ b a;
            private Handler b;
            private float c;
            private float d;
            private long e;

            private a(b bVar) {
                this.a = bVar;
                this.b = new Handler(Looper.getMainLooper());
            }

            public final void run() {
                if (this.a.getParent() != null) {
                    float min = Math.min(1.0f, ((float) (System.currentTimeMillis() - this.e)) / 400.0f);
                    this.a.a((int) (((this.c - ((float) this.a.a.a)) * min) + ((float) this.a.a.a)), (int) (((this.d - ((float) this.a.a.b)) * min) + ((float) this.a.a.b)));
                    if (min < 1.0f) {
                        this.b.post(this);
                    }
                }
            }

            static /* synthetic */ void a(a aVar, float f, float f2) {
                aVar.c = f;
                aVar.d = f2;
                aVar.e = System.currentTimeMillis();
                aVar.b.post(aVar);
            }
        }

        public b(a aVar, Context context) {
            this.a = aVar;
            super(context);
            this.b = new GestureDetector(context, new a());
            this.d = new a();
        }

        public final void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
            this.a.c = (LayoutParams) layoutParams;
            super.setLayoutParams(layoutParams);
        }

        public final boolean onTouchEvent(MotionEvent motionEvent) {
            boolean onTouchEvent;
            if (this.c) {
                onTouchEvent = this.b.onTouchEvent(motionEvent);
            } else {
                onTouchEvent = false;
            }
            if (onTouchEvent) {
                a();
            } else {
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.e = System.currentTimeMillis();
                    this.d.b.removeCallbacks(this.d);
                    this.h = true;
                } else if (action == 1) {
                    if (System.currentTimeMillis() - this.e < 200) {
                        performClick();
                    }
                    this.h = false;
                    a();
                } else if (action == 2 && this.h) {
                    float f = rawX - this.f;
                    float f2 = rawY - this.g;
                    if (((float) this.a.b) + f2 > 50.0f) {
                        a((int) (f + ((float) this.a.a)), (int) (f2 + ((float) this.a.b)));
                    }
                    if (this.c && !this.h && Math.abs(this.a.c.rightMargin) < 50 && Math.abs(this.a.c.topMargin - (getContext().getResources().getDisplayMetrics().heightPixels / 2)) < Callback.DEFAULT_SWIPE_ANIMATION_DURATION) {
                        a();
                    }
                }
                this.f = rawX;
                this.g = rawY;
            }
            return true;
        }

        private void a() {
            if (this.a.i.c == IBGFloatingButtonEdge.Left) {
                a.a(this.d, ((float) this.a.a) >= ((float) this.a.d) / 2.0f ? (float) ((this.a.d - this.a.h) + 10) : -10.0f, this.a.b > this.a.e - this.a.h ? (float) (this.a.e - (this.a.h << 1)) : (float) this.a.b);
                return;
            }
            a.a(this.d, ((float) this.a.a) >= ((float) this.a.d) / 2.0f ? (float) (this.a.d + 10) : (float) (this.a.h - 10), this.a.b > this.a.e - this.a.h ? (float) (this.a.e - (this.a.h << 1)) : (float) this.a.b);
        }

        final void a(int i, int i2) {
            this.a.a = i;
            this.a.b = i2;
            this.a.c.leftMargin = this.a.a + 0;
            this.a.c.rightMargin = this.a.d - this.a.a;
            this.a.c.topMargin = this.a.b + 0;
            this.a.c.bottomMargin = this.a.e - this.a.b;
            setLayoutParams(this.a.c);
        }
    }

    public static class c extends FrameLayout {
        public c(Context context) {
            super(context);
        }
    }

    public static class d {
        public int a = -16776961;
        public int b = -1;
        public IBGFloatingButtonEdge c = IBGFloatingButtonEdge.Right;
        public int d = Callback.DEFAULT_SWIPE_ANIMATION_DURATION;
    }

    public interface e {
        void b();
    }

    public a(e eVar) {
        this.f = eVar;
    }

    public final void a(d dVar) {
        this.i = dVar;
    }

    public final void a(Activity activity) {
        this.g = new c(activity);
        if (this.i == null) {
            this.i = new d();
        }
        float f = activity.getResources().getDisplayMetrics().density;
        int i = this.d;
        int i2 = this.e;
        this.e = activity.getResources().getDisplayMetrics().heightPixels;
        this.d = activity.getResources().getDisplayMetrics().widthPixels;
        this.h = (int) (f * 56.0f);
        View bVar = new b(this, activity);
        new ShapeDrawable(new OvalShape()).getPaint().setColor(this.i.b);
        new ShapeDrawable(new OvalShape()).getPaint().setColor(-1);
        Drawable layerDrawable = new LayerDrawable(new Drawable[]{r2, r0});
        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        layerDrawable.setLayerInset(1, 2, 2, 2, 2);
        bVar.setBackgroundDrawable(layerDrawable);
        layerDrawable = activity.getResources().getDrawable(R.drawable.instabug_ic_ibg_logo_dark);
        layerDrawable.setColorFilter(new PorterDuffColorFilter(this.i.a, Mode.SRC_IN));
        bVar.setImageDrawable(layerDrawable);
        bVar.setScaleType(ScaleType.CENTER);
        if (this.c != null) {
            float f2 = ((float) (this.b * this.e)) / ((float) i2);
            this.a = Math.round(((float) (this.a * this.d)) / ((float) i));
            this.b = Math.round(f2);
            this.c.leftMargin = this.a;
            this.c.rightMargin = this.d - this.a;
            this.c.topMargin = this.b;
            this.c.bottomMargin = this.e - this.b;
            bVar.setLayoutParams(this.c);
            bVar.a();
        } else if (this.i.c == IBGFloatingButtonEdge.Left) {
            this.c = new LayoutParams(this.h, this.h, 51);
            bVar.setLayoutParams(this.c);
            bVar.a(-10, this.i.d);
        } else {
            this.c = new LayoutParams(this.h, this.h, 53);
            bVar.setLayoutParams(this.c);
            bVar.a(this.d + 10, this.i.d);
        }
        bVar.setOnClickListener(this);
        bVar.setScaleType(ScaleType.CENTER_INSIDE);
        this.g.addView(bVar);
        ((FrameLayout) activity.getWindow().getDecorView()).addView(this.g, new ViewGroup.LayoutParams(-1, -1));
    }

    public final void a() {
        this.g.setOnClickListener(null);
        if (this.g != null && this.g.getParent() != null && (this.g.getParent() instanceof ViewGroup)) {
            ((ViewGroup) this.g.getParent()).removeView(this.g);
        }
    }

    public final void onClick(View view) {
        if (this.f != null) {
            this.f.b();
        }
    }
}
