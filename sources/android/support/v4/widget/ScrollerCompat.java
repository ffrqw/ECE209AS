package android.support.v4.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

public final class ScrollerCompat {
    private final boolean mIsIcsOrNewer;
    OverScroller mScroller;

    public static ScrollerCompat create(Context context) {
        return create(context, null);
    }

    public static ScrollerCompat create(Context context, Interpolator interpolator) {
        return new ScrollerCompat(VERSION.SDK_INT >= 14, context, interpolator);
    }

    private ScrollerCompat(boolean isIcsOrNewer, Context context, Interpolator interpolator) {
        this.mIsIcsOrNewer = isIcsOrNewer;
        this.mScroller = interpolator != null ? new OverScroller(context, interpolator) : new OverScroller(context);
    }

    public final boolean isFinished() {
        return this.mScroller.isFinished();
    }

    public final int getCurrX() {
        return this.mScroller.getCurrX();
    }

    public final int getCurrY() {
        return this.mScroller.getCurrY();
    }

    public final int getFinalX() {
        return this.mScroller.getFinalX();
    }

    public final int getFinalY() {
        return this.mScroller.getFinalY();
    }

    public final float getCurrVelocity() {
        return this.mIsIcsOrNewer ? this.mScroller.getCurrVelocity() : 0.0f;
    }

    public final boolean computeScrollOffset() {
        return this.mScroller.computeScrollOffset();
    }

    public final void startScroll(int startX, int startY, int dx, int dy, int duration) {
        this.mScroller.startScroll(startX, startY, dx, dy, duration);
    }

    public final void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        this.mScroller.fling(0, startY, velocityX, velocityY, minX, maxX, minY, maxY);
    }

    public final boolean springBack(int startX, int startY, int minX, int maxX, int minY, int maxY) {
        return this.mScroller.springBack(startX, startY, 0, 0, 0, maxY);
    }

    public final void abortAnimation() {
        this.mScroller.abortAnimation();
    }
}
