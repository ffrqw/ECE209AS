package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.design.R;
import android.support.design.widget.CoordinatorLayout.DefaultBehavior;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import java.lang.ref.WeakReference;
import java.util.List;

@DefaultBehavior(Behavior.class)
public final class AppBarLayout extends LinearLayout {
    private boolean mCollapsed;
    private boolean mCollapsible;
    private int mDownPreScrollRange;
    private int mDownScrollRange;
    private boolean mHaveChildWithInterpolator;
    private int mPendingAction;
    private final int[] mTmpStatesArray;
    private int mTotalScrollRange;

    public static class Behavior extends HeaderBehavior<AppBarLayout> {
        private WeakReference<View> mLastNestedScrollingChildRef;
        private ValueAnimatorCompat mOffsetAnimator;
        private int mOffsetDelta;
        private int mOffsetToChildIndexOnLayout = -1;
        private boolean mOffsetToChildIndexOnLayoutIsMinHeight;
        private float mOffsetToChildIndexOnLayoutPerc;
        private boolean mSkipNestedPreScroll;
        private boolean mWasNestedFlung;

        protected static class SavedState extends AbsSavedState {
            public static final Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
                public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
                    return new SavedState[i];
                }

                public final /* bridge */ /* synthetic */ Object createFromParcel(Parcel parcel, ClassLoader classLoader) {
                    return new SavedState(parcel, classLoader);
                }
            });
            boolean firstVisibleChildAtMinimumHeight;
            int firstVisibleChildIndex;
            float firstVisibleChildPercentageShown;

            public SavedState(Parcel source, ClassLoader loader) {
                super(source, loader);
                this.firstVisibleChildIndex = source.readInt();
                this.firstVisibleChildPercentageShown = source.readFloat();
                this.firstVisibleChildAtMinimumHeight = source.readByte() != (byte) 0;
            }

            public SavedState(Parcelable superState) {
                super(superState);
            }

            public final void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeInt(this.firstVisibleChildIndex);
                dest.writeFloat(this.firstVisibleChildPercentageShown);
                dest.writeByte((byte) (this.firstVisibleChildAtMinimumHeight ? 1 : 0));
            }
        }

        final /* bridge */ /* synthetic */ boolean canDragView(View view) {
            AppBarLayout appBarLayout = (AppBarLayout) view;
            if (this.mLastNestedScrollingChildRef != null) {
                View view2 = (View) this.mLastNestedScrollingChildRef.get();
                if (view2 == null || !view2.isShown() || ViewCompat.canScrollVertically(view2, -1)) {
                    return false;
                }
            }
            return true;
        }

        final /* bridge */ /* synthetic */ int getMaxDragOffset(View view) {
            return -((AppBarLayout) view).getDownNestedScrollRange();
        }

        final /* bridge */ /* synthetic */ int getScrollRangeForDragFling(View view) {
            return ((AppBarLayout) view).getTotalScrollRange();
        }

        public final /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        final /* bridge */ /* synthetic */ void onFlingFinished(CoordinatorLayout coordinatorLayout, View view) {
            snapToChildIfNeeded(coordinatorLayout, (AppBarLayout) view);
        }

        public final /* bridge */ /* synthetic */ boolean onLayoutChild(CoordinatorLayout coordinatorLayout, View view, int i) {
            AppBarLayout appBarLayout = (AppBarLayout) view;
            boolean onLayoutChild = super.onLayoutChild(coordinatorLayout, appBarLayout, i);
            int pendingAction = appBarLayout.getPendingAction();
            if (this.mOffsetToChildIndexOnLayout >= 0 && (pendingAction & 8) == 0) {
                int minimumHeight;
                View childAt = appBarLayout.getChildAt(this.mOffsetToChildIndexOnLayout);
                pendingAction = -childAt.getBottom();
                if (this.mOffsetToChildIndexOnLayoutIsMinHeight) {
                    minimumHeight = ViewCompat.getMinimumHeight(childAt) + pendingAction;
                } else {
                    minimumHeight = Math.round(((float) childAt.getHeight()) * this.mOffsetToChildIndexOnLayoutPerc) + pendingAction;
                }
                setHeaderTopBottomOffset(coordinatorLayout, appBarLayout, minimumHeight);
            } else if (pendingAction != 0) {
                boolean z;
                if ((pendingAction & 4) != 0) {
                    z = true;
                } else {
                    z = false;
                }
                if ((pendingAction & 2) != 0) {
                    pendingAction = -appBarLayout.getUpNestedPreScrollRange();
                    if (z) {
                        animateOffsetTo(coordinatorLayout, appBarLayout, pendingAction, 0.0f);
                    } else {
                        setHeaderTopBottomOffset(coordinatorLayout, appBarLayout, pendingAction);
                    }
                } else if ((pendingAction & 1) != 0) {
                    if (z) {
                        animateOffsetTo(coordinatorLayout, appBarLayout, 0, 0.0f);
                    } else {
                        setHeaderTopBottomOffset(coordinatorLayout, appBarLayout, 0);
                    }
                }
            }
            appBarLayout.resetPendingAction();
            this.mOffsetToChildIndexOnLayout = -1;
            super.setTopAndBottomOffset(MathUtils.constrain(super.getTopAndBottomOffset(), -appBarLayout.getTotalScrollRange(), 0));
            updateAppBarLayoutDrawableState(coordinatorLayout, appBarLayout, super.getTopAndBottomOffset(), 0, true);
            super.getTopAndBottomOffset();
            return onLayoutChild;
        }

        public final /* bridge */ /* synthetic */ boolean onMeasureChild(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3, int i4) {
            View view2 = (AppBarLayout) view;
            if (((android.support.design.widget.CoordinatorLayout.LayoutParams) view2.getLayoutParams()).height != -2) {
                return super.onMeasureChild(coordinatorLayout, view2, i, i2, i3, i4);
            }
            coordinatorLayout.onMeasureChild(view2, i, i2, MeasureSpec.makeMeasureSpec(0, 0), i4);
            return true;
        }

        public final /* bridge */ /* synthetic */ boolean onNestedFling(CoordinatorLayout coordinatorLayout, View view, View view2, float f, float f2, boolean z) {
            boolean z2 = false;
            AppBarLayout appBarLayout = (AppBarLayout) view;
            if (!z) {
                z2 = fling(coordinatorLayout, appBarLayout, -appBarLayout.getTotalScrollRange(), 0, -f2);
            } else if (f2 < 0.0f) {
                r1 = (-appBarLayout.getTotalScrollRange()) + appBarLayout.getDownNestedPreScrollRange();
                if (getTopBottomOffsetForScrollingSibling() < r1) {
                    animateOffsetTo(coordinatorLayout, appBarLayout, r1, f2);
                    z2 = true;
                }
            } else {
                r1 = -appBarLayout.getUpNestedPreScrollRange();
                if (getTopBottomOffsetForScrollingSibling() > r1) {
                    animateOffsetTo(coordinatorLayout, appBarLayout, r1, f2);
                    z2 = true;
                }
            }
            this.mWasNestedFlung = z2;
            return z2;
        }

        public final /* bridge */ /* synthetic */ void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View view, View view2, int i, int i2, int[] iArr) {
            AppBarLayout appBarLayout = (AppBarLayout) view;
            if (i2 != 0 && !this.mSkipNestedPreScroll) {
                int i3;
                int downNestedPreScrollRange;
                if (i2 < 0) {
                    i3 = -appBarLayout.getTotalScrollRange();
                    downNestedPreScrollRange = i3 + appBarLayout.getDownNestedPreScrollRange();
                } else {
                    i3 = -appBarLayout.getUpNestedPreScrollRange();
                    downNestedPreScrollRange = 0;
                }
                iArr[1] = scroll(coordinatorLayout, appBarLayout, i2, i3, downNestedPreScrollRange);
            }
        }

        public final /* bridge */ /* synthetic */ void onNestedScroll(CoordinatorLayout coordinatorLayout, View view, View view2, int i, int i2, int i3, int i4) {
            AppBarLayout appBarLayout = (AppBarLayout) view;
            if (i4 < 0) {
                scroll(coordinatorLayout, appBarLayout, i4, -appBarLayout.getDownNestedScrollRange(), 0);
                this.mSkipNestedPreScroll = true;
                return;
            }
            this.mSkipNestedPreScroll = false;
        }

        public final /* bridge */ /* synthetic */ void onRestoreInstanceState(CoordinatorLayout coordinatorLayout, View view, Parcelable parcelable) {
            AppBarLayout appBarLayout = (AppBarLayout) view;
            if (parcelable instanceof SavedState) {
                SavedState savedState = (SavedState) parcelable;
                super.onRestoreInstanceState(coordinatorLayout, appBarLayout, savedState.getSuperState());
                this.mOffsetToChildIndexOnLayout = savedState.firstVisibleChildIndex;
                this.mOffsetToChildIndexOnLayoutPerc = savedState.firstVisibleChildPercentageShown;
                this.mOffsetToChildIndexOnLayoutIsMinHeight = savedState.firstVisibleChildAtMinimumHeight;
                return;
            }
            super.onRestoreInstanceState(coordinatorLayout, appBarLayout, parcelable);
            this.mOffsetToChildIndexOnLayout = -1;
        }

        public final /* bridge */ /* synthetic */ Parcelable onSaveInstanceState(CoordinatorLayout coordinatorLayout, View view) {
            boolean z = false;
            AppBarLayout appBarLayout = (AppBarLayout) view;
            Parcelable onSaveInstanceState = super.onSaveInstanceState(coordinatorLayout, appBarLayout);
            int topAndBottomOffset = super.getTopAndBottomOffset();
            int childCount = appBarLayout.getChildCount();
            int i = 0;
            while (i < childCount) {
                View childAt = appBarLayout.getChildAt(i);
                int bottom = childAt.getBottom() + topAndBottomOffset;
                if (childAt.getTop() + topAndBottomOffset > 0 || bottom < 0) {
                    i++;
                } else {
                    SavedState savedState = new SavedState(onSaveInstanceState);
                    savedState.firstVisibleChildIndex = i;
                    if (bottom == ViewCompat.getMinimumHeight(childAt)) {
                        z = true;
                    }
                    savedState.firstVisibleChildAtMinimumHeight = z;
                    savedState.firstVisibleChildPercentageShown = ((float) bottom) / ((float) childAt.getHeight());
                    return savedState;
                }
            }
            return onSaveInstanceState;
        }

        public final /* bridge */ /* synthetic */ boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View view, View view2, View view3, int i) {
            AppBarLayout appBarLayout = (AppBarLayout) view;
            boolean z = (i & 2) != 0 && appBarLayout.hasScrollableChildren() && coordinatorLayout.getHeight() - view2.getHeight() <= appBarLayout.getHeight();
            if (z && this.mOffsetAnimator != null) {
                this.mOffsetAnimator.cancel();
            }
            this.mLastNestedScrollingChildRef = null;
            return z;
        }

        public final /* bridge */ /* synthetic */ void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View view, View view2) {
            AppBarLayout appBarLayout = (AppBarLayout) view;
            if (!this.mWasNestedFlung) {
                snapToChildIfNeeded(coordinatorLayout, appBarLayout);
            }
            this.mSkipNestedPreScroll = false;
            this.mWasNestedFlung = false;
            this.mLastNestedScrollingChildRef = new WeakReference(view2);
        }

        final /* bridge */ /* synthetic */ int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3) {
            AppBarLayout appBarLayout = (AppBarLayout) view;
            int topBottomOffsetForScrollingSibling = getTopBottomOffsetForScrollingSibling();
            if (i2 == 0 || topBottomOffsetForScrollingSibling < i2 || topBottomOffsetForScrollingSibling > i3) {
                this.mOffsetDelta = 0;
                return 0;
            }
            int constrain = MathUtils.constrain(i, i2, i3);
            if (topBottomOffsetForScrollingSibling == constrain) {
                return 0;
            }
            int childCount;
            int height;
            if (appBarLayout.hasChildWithInterpolator()) {
                int abs = Math.abs(constrain);
                childCount = appBarLayout.getChildCount();
                int i4 = 0;
                while (i4 < childCount) {
                    View childAt = appBarLayout.getChildAt(i4);
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    Interpolator interpolator = layoutParams.mScrollInterpolator;
                    if (abs < childAt.getTop() || abs > childAt.getBottom()) {
                        i4++;
                    } else {
                        if (interpolator != null) {
                            childCount = layoutParams.mScrollFlags;
                            if ((childCount & 1) != 0) {
                                height = (layoutParams.bottomMargin + (childAt.getHeight() + layoutParams.topMargin)) + 0;
                                if ((childCount & 2) != 0) {
                                    height -= ViewCompat.getMinimumHeight(childAt);
                                }
                            } else {
                                height = 0;
                            }
                            if (ViewCompat.getFitsSystemWindows(childAt)) {
                                if (height > 0) {
                                    i4 = abs - childAt.getTop();
                                    height = Math.round(interpolator.getInterpolation(((float) i4) / ((float) height)) * ((float) height));
                                    height = (height + childAt.getTop()) * Integer.signum(constrain);
                                }
                            } else if (height > 0) {
                                i4 = abs - childAt.getTop();
                                height = Math.round(interpolator.getInterpolation(((float) i4) / ((float) height)) * ((float) height));
                                height = (height + childAt.getTop()) * Integer.signum(constrain);
                            }
                        }
                        height = constrain;
                    }
                }
                height = constrain;
            } else {
                height = constrain;
            }
            boolean topAndBottomOffset = super.setTopAndBottomOffset(height);
            childCount = topBottomOffsetForScrollingSibling - constrain;
            this.mOffsetDelta = constrain - height;
            if (!topAndBottomOffset && appBarLayout.hasChildWithInterpolator()) {
                coordinatorLayout.dispatchDependentViewsChanged(appBarLayout);
            }
            super.getTopAndBottomOffset();
            if (constrain < topBottomOffsetForScrollingSibling) {
                topBottomOffsetForScrollingSibling = -1;
            } else {
                topBottomOffsetForScrollingSibling = 1;
            }
            updateAppBarLayoutDrawableState(coordinatorLayout, appBarLayout, constrain, topBottomOffsetForScrollingSibling, false);
            return childCount;
        }

        public final /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        private void animateOffsetTo(final CoordinatorLayout coordinatorLayout, final AppBarLayout child, int offset, float velocity) {
            int duration;
            int distance = Math.abs(getTopBottomOffsetForScrollingSibling() - offset);
            velocity = Math.abs(velocity);
            if (velocity > 0.0f) {
                duration = Math.round(1000.0f * (((float) distance) / velocity)) * 3;
            } else {
                duration = (int) ((1.0f + (((float) distance) / ((float) child.getHeight()))) * 150.0f);
            }
            int topBottomOffsetForScrollingSibling = getTopBottomOffsetForScrollingSibling();
            if (topBottomOffsetForScrollingSibling != offset) {
                if (this.mOffsetAnimator == null) {
                    this.mOffsetAnimator = ViewUtils.createAnimator();
                    this.mOffsetAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
                    this.mOffsetAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        public final void onAnimationUpdate(ValueAnimatorCompat animator) {
                            Behavior.this.setHeaderTopBottomOffset(coordinatorLayout, child, animator.getAnimatedIntValue());
                        }
                    });
                } else {
                    this.mOffsetAnimator.cancel();
                }
                this.mOffsetAnimator.setDuration((long) Math.min(duration, 600));
                this.mOffsetAnimator.setIntValues(topBottomOffsetForScrollingSibling, offset);
                this.mOffsetAnimator.start();
            } else if (this.mOffsetAnimator != null && this.mOffsetAnimator.isRunning()) {
                this.mOffsetAnimator.cancel();
            }
        }

        private void snapToChildIfNeeded(CoordinatorLayout coordinatorLayout, AppBarLayout abl) {
            int offset = getTopBottomOffsetForScrollingSibling();
            int offsetChildIndex = 0;
            int childCount = abl.getChildCount();
            while (offsetChildIndex < childCount) {
                View childAt = abl.getChildAt(offsetChildIndex);
                if (childAt.getTop() <= (-offset) && childAt.getBottom() >= (-offset)) {
                    break;
                }
                offsetChildIndex++;
            }
            offsetChildIndex = -1;
            if (offsetChildIndex >= 0) {
                View offsetChild = abl.getChildAt(offsetChildIndex);
                int flags = ((LayoutParams) offsetChild.getLayoutParams()).mScrollFlags;
                if ((flags & 17) == 17) {
                    int newOffset;
                    int snapTop = -offsetChild.getTop();
                    int snapBottom = -offsetChild.getBottom();
                    if (offsetChildIndex == abl.getChildCount() - 1) {
                    }
                    if (checkFlag(flags, 2)) {
                        snapBottom += ViewCompat.getMinimumHeight(offsetChild);
                    } else if (checkFlag(flags, 5)) {
                        int seam = snapBottom + ViewCompat.getMinimumHeight(offsetChild);
                        if (offset < seam) {
                            snapTop = seam;
                        } else {
                            snapBottom = seam;
                        }
                    }
                    if (offset < (snapBottom + snapTop) / 2) {
                        newOffset = snapBottom;
                    } else {
                        newOffset = snapTop;
                    }
                    animateOffsetTo(coordinatorLayout, abl, MathUtils.constrain(newOffset, -abl.getTotalScrollRange(), 0), 0.0f);
                }
            }
        }

        private static boolean checkFlag(int flags, int check) {
            return (flags & check) == check;
        }

        private void updateAppBarLayoutDrawableState(CoordinatorLayout parent, AppBarLayout layout, int offset, int direction, boolean forceJump) {
            View child;
            int abs = Math.abs(offset);
            int childCount = layout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                child = layout.getChildAt(i);
                if (abs >= child.getTop() && abs <= child.getBottom()) {
                    break;
                }
            }
            child = null;
            if (child != null) {
                int flags = ((LayoutParams) child.getLayoutParams()).mScrollFlags;
                boolean collapsed = false;
                if ((flags & 1) != 0) {
                    int minHeight = ViewCompat.getMinimumHeight(child);
                    if (direction > 0 && (flags & 12) != 0) {
                        collapsed = (-offset) >= child.getBottom() - minHeight;
                    } else if ((flags & 2) != 0) {
                        collapsed = (-offset) >= child.getBottom() - minHeight;
                    }
                }
                boolean changed = layout.setCollapsedState(collapsed);
                if (VERSION.SDK_INT >= 11) {
                    if (!forceJump) {
                        if (changed) {
                            Object obj;
                            List dependents = parent.getDependents(layout);
                            int size = dependents.size();
                            for (abs = 0; abs < size; abs++) {
                                android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) ((View) dependents.get(abs)).getLayoutParams()).mBehavior;
                                if (behavior instanceof ScrollingViewBehavior) {
                                    if (((ScrollingViewBehavior) behavior).getOverlayTop() != 0) {
                                        obj = 1;
                                    } else {
                                        obj = null;
                                    }
                                    if (obj == null) {
                                        return;
                                    }
                                }
                            }
                            obj = null;
                            if (obj == null) {
                                return;
                            }
                        }
                        return;
                    }
                    layout.jumpDrawablesToCurrentState();
                }
            }
        }

        final int getTopBottomOffsetForScrollingSibling() {
            return super.getTopAndBottomOffset() + this.mOffsetDelta;
        }
    }

    public static class LayoutParams extends android.widget.LinearLayout.LayoutParams {
        int mScrollFlags = 1;
        Interpolator mScrollInterpolator;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.AppBarLayout_Layout);
            this.mScrollFlags = a.getInt(R.styleable.AppBarLayout_Layout_layout_scrollFlags, 0);
            if (a.hasValue(R.styleable.AppBarLayout_Layout_layout_scrollInterpolator)) {
                this.mScrollInterpolator = AnimationUtils.loadInterpolator(c, a.getResourceId(R.styleable.AppBarLayout_Layout_layout_scrollInterpolator, 0));
            }
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(-1, -2);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(19)
        public LayoutParams(android.widget.LinearLayout.LayoutParams source) {
            super(source);
        }
    }

    public static class ScrollingViewBehavior extends HeaderScrollingViewBehavior {
        public final /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        public final /* bridge */ /* synthetic */ boolean onLayoutChild(CoordinatorLayout coordinatorLayout, View view, int i) {
            return super.onLayoutChild(coordinatorLayout, view, i);
        }

        public final /* bridge */ /* synthetic */ boolean onMeasureChild(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3, int i4) {
            return super.onMeasureChild(coordinatorLayout, view, i, i2, i3, i4);
        }

        public final /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public ScrollingViewBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollingViewBehavior_Layout);
            setOverlayTop(a.getDimensionPixelSize(R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
            a.recycle();
        }

        public final boolean layoutDependsOn$3747c3f0(View dependency) {
            return dependency instanceof AppBarLayout;
        }

        public final boolean onRequestChildRectangleOnScreen(CoordinatorLayout parent, View child, Rect rectangle, boolean immediate) {
            AppBarLayout header = findFirstDependency(parent.getDependencies(child));
            if (header != null) {
                rectangle.offset(child.getLeft(), child.getTop());
                Rect parentRect = this.mTempRect1;
                parentRect.set(0, 0, parent.getWidth(), parent.getHeight());
                if (!parentRect.contains(rectangle)) {
                    boolean z;
                    if (immediate) {
                        z = false;
                    } else {
                        z = true;
                    }
                    header.setExpanded(false, z);
                    return true;
                }
            }
            return false;
        }

        final float getOverlapRatioForOffset(View header) {
            if (header instanceof AppBarLayout) {
                int offset;
                AppBarLayout abl = (AppBarLayout) header;
                int totalScrollRange = abl.getTotalScrollRange();
                int preScrollDown = abl.getDownNestedPreScrollRange();
                android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) abl.getLayoutParams()).mBehavior;
                if (behavior instanceof Behavior) {
                    offset = ((Behavior) behavior).getTopBottomOffsetForScrollingSibling();
                } else {
                    offset = 0;
                }
                if (preScrollDown != 0 && totalScrollRange + offset <= preScrollDown) {
                    return 0.0f;
                }
                int availScrollRange = totalScrollRange - preScrollDown;
                if (availScrollRange != 0) {
                    return 1.0f + (((float) offset) / ((float) availScrollRange));
                }
            }
            return 0.0f;
        }

        private static AppBarLayout findFirstDependency(List<View> views) {
            int z = views.size();
            for (int i = 0; i < z; i++) {
                View view = (View) views.get(i);
                if (view instanceof AppBarLayout) {
                    return (AppBarLayout) view;
                }
            }
            return null;
        }

        final int getScrollRange(View v) {
            if (v instanceof AppBarLayout) {
                return ((AppBarLayout) v).getTotalScrollRange();
            }
            return super.getScrollRange(v);
        }

        public final boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).mBehavior;
            if (behavior instanceof Behavior) {
                int bottom = dependency.getBottom() - child.getTop();
                ViewCompat.offsetTopAndBottom(child, ((((Behavior) behavior).mOffsetDelta + bottom) + getVerticalLayoutGap()) - getOverlapPixelsForOffset(dependency));
            }
            return false;
        }
    }

    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidateScrollRanges();
    }

    protected final void onLayout(boolean changed, int l, int t, int r, int b) {
        boolean z;
        super.onLayout(changed, l, t, r, b);
        invalidateScrollRanges();
        this.mHaveChildWithInterpolator = false;
        int z2 = getChildCount();
        for (int i = 0; i < z2; i++) {
            if (((LayoutParams) getChildAt(i).getLayoutParams()).mScrollInterpolator != null) {
                this.mHaveChildWithInterpolator = true;
                break;
            }
        }
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            Object obj;
            LayoutParams layoutParams = (LayoutParams) getChildAt(i2).getLayoutParams();
            if ((layoutParams.mScrollFlags & 1) != 1 || (layoutParams.mScrollFlags & 10) == 0) {
                obj = null;
            } else {
                obj = 1;
            }
            if (obj != null) {
                z = true;
                break;
            }
        }
        z = false;
        if (this.mCollapsible != z) {
            this.mCollapsible = z;
            refreshDrawableState();
        }
    }

    private void invalidateScrollRanges() {
        this.mTotalScrollRange = -1;
        this.mDownPreScrollRange = -1;
        this.mDownScrollRange = -1;
    }

    public final void setOrientation(int orientation) {
        if (orientation != 1) {
            throw new IllegalArgumentException("AppBarLayout is always vertical and does not support horizontal orientation");
        }
        super.setOrientation(orientation);
    }

    protected final boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    private static LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2);
    }

    private LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private static LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        if (VERSION.SDK_INT >= 19 && (p instanceof android.widget.LinearLayout.LayoutParams)) {
            return new LayoutParams((android.widget.LinearLayout.LayoutParams) p);
        }
        if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    final boolean hasChildWithInterpolator() {
        return this.mHaveChildWithInterpolator;
    }

    public final int getTotalScrollRange() {
        if (this.mTotalScrollRange != -1) {
            return this.mTotalScrollRange;
        }
        int range = 0;
        int z = getChildCount();
        for (int i = 0; i < z; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int flags = lp.mScrollFlags;
            if ((flags & 1) == 0) {
                break;
            }
            range += (lp.topMargin + childHeight) + lp.bottomMargin;
            if ((flags & 2) != 0) {
                range -= ViewCompat.getMinimumHeight(child);
                break;
            }
        }
        int max = Math.max(0, range);
        this.mTotalScrollRange = max;
        return max;
    }

    final boolean hasScrollableChildren() {
        return getTotalScrollRange() != 0;
    }

    final int getUpNestedPreScrollRange() {
        return getTotalScrollRange();
    }

    final int getDownNestedPreScrollRange() {
        if (this.mDownPreScrollRange != -1) {
            return this.mDownPreScrollRange;
        }
        int range = 0;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int flags = lp.mScrollFlags;
            if ((flags & 5) == 5) {
                range += lp.topMargin + lp.bottomMargin;
                if ((flags & 8) != 0) {
                    range += ViewCompat.getMinimumHeight(child);
                } else if ((flags & 2) != 0) {
                    range += childHeight - ViewCompat.getMinimumHeight(child);
                } else {
                    range += childHeight;
                }
            } else if (range > 0) {
                break;
            }
        }
        int max = Math.max(0, range);
        this.mDownPreScrollRange = max;
        return max;
    }

    final int getDownNestedScrollRange() {
        if (this.mDownScrollRange != -1) {
            return this.mDownScrollRange;
        }
        int range = 0;
        int z = getChildCount();
        for (int i = 0; i < z; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight() + (lp.topMargin + lp.bottomMargin);
            int flags = lp.mScrollFlags;
            if ((flags & 1) == 0) {
                break;
            }
            range += childHeight;
            if ((flags & 2) != 0) {
                range -= ViewCompat.getMinimumHeight(child);
                break;
            }
        }
        int max = Math.max(0, range);
        this.mDownScrollRange = max;
        return max;
    }

    protected final int[] onCreateDrawableState(int extraSpace) {
        int[] extraStates = this.mTmpStatesArray;
        int[] states = super.onCreateDrawableState(extraStates.length + extraSpace);
        extraStates[0] = this.mCollapsible ? com.rachio.iro.R.attr.state_collapsible : -2130772035;
        int i = (this.mCollapsible && this.mCollapsed) ? com.rachio.iro.R.attr.state_collapsed : -2130772034;
        extraStates[1] = i;
        return mergeDrawableStates(states, extraStates);
    }

    final boolean setCollapsedState(boolean collapsed) {
        if (this.mCollapsed == collapsed) {
            return false;
        }
        this.mCollapsed = collapsed;
        refreshDrawableState();
        return true;
    }

    final int getPendingAction() {
        return this.mPendingAction;
    }

    final void resetPendingAction() {
        this.mPendingAction = 0;
    }

    public final void setExpanded(boolean expanded, boolean animate) {
        this.mPendingAction = ((animate ? 4 : 0) | 2) | 8;
        requestLayout();
    }
}
