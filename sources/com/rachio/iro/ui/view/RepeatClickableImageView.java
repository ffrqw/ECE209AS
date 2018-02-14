package com.rachio.iro.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class RepeatClickableImageView extends AppCompatImageView {
    private static final String TAG = RepeatClickableImageView.class.getSimpleName();
    private Runnable doClick;
    private boolean down;

    public RepeatClickableImageView(Context context) {
        this(context, null);
    }

    public RepeatClickableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RepeatClickableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.doClick = new Runnable() {
            public void run() {
                if (RepeatClickableImageView.this.down) {
                    RepeatClickableImageView.this.performClick();
                    RepeatClickableImageView.this.postDelayed(RepeatClickableImageView.this.doClick, 300);
                    return;
                }
                Log.d(RepeatClickableImageView.TAG, "ignoring repeat");
            }
        };
        setFocusable(true);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case 0:
                Log.d(TAG, "down");
                getParent().requestDisallowInterceptTouchEvent(true);
                this.down = true;
                performClick();
                postDelayed(this.doClick, 300);
                break;
            case 1:
                break;
            case 2:
                if (!new Rect(0, 0, getWidth(), getHeight()).contains((int) event.getX(), (int) event.getY())) {
                    Log.d(TAG, "moved outside of rect");
                    this.down = false;
                    break;
                }
                break;
            case 3:
                Log.d(TAG, "cancel");
                break;
        }
        getParent().requestDisallowInterceptTouchEvent(false);
        Log.d(TAG, "up");
        this.down = false;
        if (!this.down) {
            removeCallbacks(this.doClick);
        }
        return true;
    }
}
