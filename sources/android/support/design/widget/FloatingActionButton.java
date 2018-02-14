package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.design.R;
import android.support.design.widget.CoordinatorLayout.DefaultBehavior;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import java.util.List;

@DefaultBehavior(Behavior.class)
public final class FloatingActionButton extends VisibilityAwareImageButton {
    private ColorStateList mBackgroundTint;
    private Mode mBackgroundTintMode;
    boolean mCompatPadding;
    int mImagePadding;
    private FloatingActionButtonImpl mImpl;
    final Rect mShadowPadding;
    private final Rect mTouchArea;

    public static class Behavior extends android.support.design.widget.CoordinatorLayout.Behavior<FloatingActionButton> {
        private boolean mAutoHideEnabled;
        private Rect mTmpRect;

        public final /* bridge */ /* synthetic */ boolean getInsetDodgeRect(CoordinatorLayout coordinatorLayout, View view, Rect rect) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) view;
            Rect rect2 = floatingActionButton.mShadowPadding;
            rect.set(floatingActionButton.getLeft() + rect2.left, floatingActionButton.getTop() + rect2.top, floatingActionButton.getRight() - rect2.right, floatingActionButton.getBottom() - rect2.bottom);
            return true;
        }

        public final /* bridge */ /* synthetic */ boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, View view, View view2) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) view;
            if (view2 instanceof AppBarLayout) {
                updateFabVisibilityForAppBarLayout(coordinatorLayout, (AppBarLayout) view2, floatingActionButton);
            } else if (isBottomSheet(view2)) {
                updateFabVisibilityForBottomSheet(view2, floatingActionButton);
            }
            return false;
        }

        public final /* bridge */ /* synthetic */ boolean onLayoutChild(CoordinatorLayout coordinatorLayout, View view, int i) {
            int i2;
            int i3 = 0;
            FloatingActionButton floatingActionButton = (FloatingActionButton) view;
            List dependencies = coordinatorLayout.getDependencies(floatingActionButton);
            int size = dependencies.size();
            for (i2 = 0; i2 < size; i2++) {
                View view2 = (View) dependencies.get(i2);
                if (!(view2 instanceof AppBarLayout)) {
                    if (isBottomSheet(view2) && updateFabVisibilityForBottomSheet(view2, floatingActionButton)) {
                        break;
                    }
                } else if (updateFabVisibilityForAppBarLayout(coordinatorLayout, (AppBarLayout) view2, floatingActionButton)) {
                    break;
                }
            }
            coordinatorLayout.onLayoutChild(floatingActionButton, i);
            Rect rect = floatingActionButton.mShadowPadding;
            if (rect != null && rect.centerX() > 0 && rect.centerY() > 0) {
                LayoutParams layoutParams = (LayoutParams) floatingActionButton.getLayoutParams();
                if (floatingActionButton.getRight() >= coordinatorLayout.getWidth() - layoutParams.rightMargin) {
                    i2 = rect.right;
                } else if (floatingActionButton.getLeft() <= layoutParams.leftMargin) {
                    i2 = -rect.left;
                } else {
                    i2 = 0;
                }
                if (floatingActionButton.getBottom() >= coordinatorLayout.getHeight() - layoutParams.bottomMargin) {
                    i3 = rect.bottom;
                } else if (floatingActionButton.getTop() <= layoutParams.topMargin) {
                    i3 = -rect.top;
                }
                if (i3 != 0) {
                    ViewCompat.offsetTopAndBottom(floatingActionButton, i3);
                }
                if (i2 != 0) {
                    ViewCompat.offsetLeftAndRight(floatingActionButton, i2);
                }
            }
            return true;
        }

        public Behavior() {
            this.mAutoHideEnabled = true;
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton_Behavior_Layout);
            this.mAutoHideEnabled = a.getBoolean(R.styleable.FloatingActionButton_Behavior_Layout_behavior_autoHide, true);
            a.recycle();
        }

        public final void onAttachedToLayoutParams(LayoutParams lp) {
            if (lp.dodgeInsetEdges == 0) {
                lp.dodgeInsetEdges = 80;
            }
        }

        private static boolean isBottomSheet(View view) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof LayoutParams) {
                return ((LayoutParams) lp).mBehavior instanceof BottomSheetBehavior;
            }
            return false;
        }

        private boolean shouldUpdateVisibility(View dependency, FloatingActionButton child) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (this.mAutoHideEnabled && lp.mAnchorId == dependency.getId() && child.getUserSetVisibility() == 0) {
                return true;
            }
            return false;
        }

        private boolean updateFabVisibilityForAppBarLayout(CoordinatorLayout parent, AppBarLayout appBarLayout, FloatingActionButton child) {
            if (!shouldUpdateVisibility(appBarLayout, child)) {
                return false;
            }
            if (this.mTmpRect == null) {
                this.mTmpRect = new Rect();
            }
            Rect rect = this.mTmpRect;
            ViewGroupUtils.getDescendantRect(parent, appBarLayout, rect);
            int i = rect.bottom;
            int minimumHeight = ViewCompat.getMinimumHeight(appBarLayout);
            if (minimumHeight != 0) {
                minimumHeight = (minimumHeight << 1) + 0;
            } else {
                minimumHeight = appBarLayout.getChildCount();
                if (minimumHeight > 0) {
                    minimumHeight = ViewCompat.getMinimumHeight(appBarLayout.getChildAt(minimumHeight - 1));
                } else {
                    minimumHeight = 0;
                }
                if (minimumHeight != 0) {
                    minimumHeight = (minimumHeight << 1) + 0;
                } else {
                    minimumHeight = appBarLayout.getHeight() / 3;
                }
            }
            if (i <= minimumHeight) {
                child.hide(null, false);
            } else {
                child.show(null, false);
            }
            return true;
        }

        private boolean updateFabVisibilityForBottomSheet(View bottomSheet, FloatingActionButton child) {
            if (!shouldUpdateVisibility(bottomSheet, child)) {
                return false;
            }
            if (bottomSheet.getTop() < (child.getHeight() / 2) + ((LayoutParams) child.getLayoutParams()).topMargin) {
                child.hide(null, false);
            } else {
                child.show(null, false);
            }
            return true;
        }
    }

    public static abstract class OnVisibilityChangedListener {
    }

    private class ShadowDelegateImpl implements ShadowViewDelegate {
        ShadowDelegateImpl() {
        }

        public final float getRadius() {
            return ((float) FloatingActionButton.this.getSizeDimension()) / 2.0f;
        }

        public final void setShadowPadding(int left, int top, int right, int bottom) {
            FloatingActionButton.this.mShadowPadding.set(left, top, right, bottom);
            FloatingActionButton.this.setPadding(FloatingActionButton.this.mImagePadding + left, FloatingActionButton.this.mImagePadding + top, FloatingActionButton.this.mImagePadding + right, FloatingActionButton.this.mImagePadding + bottom);
        }

        public final void setBackgroundDrawable(Drawable background) {
            super.setBackgroundDrawable(background);
        }

        public final boolean isCompatPaddingEnabled() {
            return FloatingActionButton.this.mCompatPadding;
        }
    }

    public final /* bridge */ /* synthetic */ void setVisibility(int i) {
        super.setVisibility(i);
    }

    public final ColorStateList getBackgroundTintList() {
        return this.mBackgroundTint;
    }

    public final void setBackgroundTintList(ColorStateList tint) {
        if (this.mBackgroundTint != tint) {
            this.mBackgroundTint = tint;
            getImpl().setBackgroundTintList(tint);
        }
    }

    public final Mode getBackgroundTintMode() {
        return this.mBackgroundTintMode;
    }

    public final void setBackgroundTintMode(Mode tintMode) {
        if (this.mBackgroundTintMode != tintMode) {
            this.mBackgroundTintMode = tintMode;
            getImpl().setBackgroundTintMode(tintMode);
        }
    }

    public final void setBackgroundDrawable(Drawable background) {
        Log.i("FloatingActionButton", "Setting a custom background is not supported.");
    }

    public final void setBackgroundResource(int resid) {
        Log.i("FloatingActionButton", "Setting a custom background is not supported.");
    }

    public final void setBackgroundColor(int color) {
        Log.i("FloatingActionButton", "Setting a custom background is not supported.");
    }

    public final void setImageResource(int resId) {
        AppCompatImageHelper appCompatImageHelper = null;
        appCompatImageHelper.setImageResource(resId);
    }

    final void show(OnVisibilityChangedListener listener, boolean fromUser) {
        getImpl().show(wrapOnVisibilityChangedListener(listener), false);
    }

    final void hide(OnVisibilityChangedListener listener, boolean fromUser) {
        getImpl().hide(wrapOnVisibilityChangedListener(listener), false);
    }

    private InternalVisibilityChangedListener wrapOnVisibilityChangedListener(final OnVisibilityChangedListener listener) {
        if (listener == null) {
            return null;
        }
        return new InternalVisibilityChangedListener() {
        };
    }

    final int getSizeDimension() {
        return getSizeDimension(0);
    }

    private int getSizeDimension(int size) {
        while (true) {
            Resources res = getResources();
            switch (size) {
                case -1:
                    if (Math.max(ConfigurationHelper.getScreenWidthDp(res), ConfigurationHelper.getScreenHeightDp(res)) < 470) {
                        return getSizeDimension(1);
                    }
                    size = 0;
                case 1:
                    return res.getDimensionPixelSize(com.rachio.iro.R.dimen.design_fab_size_mini);
                default:
                    return res.getDimensionPixelSize(com.rachio.iro.R.dimen.design_fab_size_normal);
            }
        }
    }

    protected final void onAttachedToWindow() {
        super.onAttachedToWindow();
        getImpl().onAttachedToWindow();
    }

    protected final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getImpl().onDetachedFromWindow();
    }

    protected final void drawableStateChanged() {
        super.drawableStateChanged();
        getImpl().onDrawableStateChanged(getDrawableState());
    }

    @TargetApi(11)
    public final void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        getImpl().jumpDrawableToCurrentState();
    }

    private static int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
                return Math.min(desiredSize, specSize);
            case 0:
                return desiredSize;
            case 1073741824:
                return specSize;
            default:
                return result;
        }
    }

    public final boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                boolean z;
                Rect rect = this.mTouchArea;
                if (ViewCompat.isLaidOut(this)) {
                    rect.set(0, 0, getWidth(), getHeight());
                    rect.left += this.mShadowPadding.left;
                    rect.top += this.mShadowPadding.top;
                    rect.right -= this.mShadowPadding.right;
                    rect.bottom -= this.mShadowPadding.bottom;
                    z = true;
                } else {
                    z = false;
                }
                if (z && !this.mTouchArea.contains((int) ev.getX(), (int) ev.getY())) {
                    return false;
                }
        }
        return super.onTouchEvent(ev);
    }

    private FloatingActionButtonImpl getImpl() {
        if (this.mImpl == null) {
            FloatingActionButtonImpl floatingActionButtonLollipop;
            int i = VERSION.SDK_INT;
            if (i >= 21) {
                floatingActionButtonLollipop = new FloatingActionButtonLollipop(this, new ShadowDelegateImpl(), ViewUtils.DEFAULT_ANIMATOR_CREATOR);
            } else if (i >= 14) {
                floatingActionButtonLollipop = new FloatingActionButtonIcs(this, new ShadowDelegateImpl(), ViewUtils.DEFAULT_ANIMATOR_CREATOR);
            } else {
                floatingActionButtonLollipop = new FloatingActionButtonGingerbread(this, new ShadowDelegateImpl(), ViewUtils.DEFAULT_ANIMATOR_CREATOR);
            }
            this.mImpl = floatingActionButtonLollipop;
        }
        return this.mImpl;
    }

    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int preferredSize = getSizeDimension(0);
        this.mImagePadding = preferredSize / 2;
        getImpl().updatePadding();
        int d = Math.min(resolveAdjustedSize(preferredSize, widthMeasureSpec), resolveAdjustedSize(preferredSize, heightMeasureSpec));
        setMeasuredDimension((this.mShadowPadding.left + d) + this.mShadowPadding.right, (this.mShadowPadding.top + d) + this.mShadowPadding.bottom);
    }
}
