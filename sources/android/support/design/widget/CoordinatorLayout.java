package android.support.design.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.design.R;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SynchronizedPool;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoordinatorLayout extends ViewGroup implements NestedScrollingParent {
    static final Class<?>[] CONSTRUCTOR_PARAMS = new Class[]{Context.class, AttributeSet.class};
    static final Comparator<View> TOP_SORTED_CHILDREN_COMPARATOR;
    static final String WIDGET_PACKAGE_NAME;
    static final ThreadLocal<Map<String, Constructor<Behavior>>> sConstructors = new ThreadLocal();
    private static final Pool<Rect> sRectPool = new SynchronizedPool(12);
    private OnApplyWindowInsetsListener mApplyWindowInsetsListener;
    private View mBehaviorTouchView;
    private final DirectedAcyclicGraph<View> mChildDag;
    private final List<View> mDependencySortedChildren;
    private boolean mDisallowInterceptReset;
    private boolean mDrawStatusBarBackground;
    private boolean mIsAttachedToWindow;
    private int[] mKeylines;
    private WindowInsetsCompat mLastInsets;
    private boolean mNeedsPreDrawListener;
    private View mNestedScrollingDirectChild;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private View mNestedScrollingTarget;
    OnHierarchyChangeListener mOnHierarchyChangeListener;
    private OnPreDrawListener mOnPreDrawListener;
    private Drawable mStatusBarBackground;
    private final List<View> mTempDependenciesList;
    private final int[] mTempIntPair;
    private final List<View> mTempList1;

    public static abstract class Behavior<V extends View> {
        public Behavior(Context context, AttributeSet attrs) {
        }

        public void onAttachedToLayoutParams(LayoutParams params) {
        }

        public boolean onInterceptTouchEvent(CoordinatorLayout parent, V v, MotionEvent ev) {
            return false;
        }

        public boolean onTouchEvent(CoordinatorLayout parent, V v, MotionEvent ev) {
            return false;
        }

        public boolean layoutDependsOn$3747c3f0(View dependency) {
            return false;
        }

        public boolean onDependentViewChanged(CoordinatorLayout parent, V v, View dependency) {
            return false;
        }

        public boolean onMeasureChild(CoordinatorLayout parent, V v, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            return false;
        }

        public boolean onLayoutChild(CoordinatorLayout parent, V v, int layoutDirection) {
            return false;
        }

        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V v, View directTargetChild, View target, int nestedScrollAxes) {
            return false;
        }

        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V v, View target) {
        }

        public void onNestedScroll(CoordinatorLayout coordinatorLayout, V v, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        }

        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V v, View target, int dx, int dy, int[] consumed) {
        }

        public boolean onNestedFling(CoordinatorLayout coordinatorLayout, V v, View target, float velocityX, float velocityY, boolean consumed) {
            return false;
        }

        public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V v, View target, float velocityX, float velocityY) {
            return false;
        }

        public boolean onRequestChildRectangleOnScreen(CoordinatorLayout coordinatorLayout, V v, Rect rectangle, boolean immediate) {
            return false;
        }

        public void onRestoreInstanceState(CoordinatorLayout parent, V v, Parcelable state) {
        }

        public Parcelable onSaveInstanceState(CoordinatorLayout parent, V v) {
            return BaseSavedState.EMPTY_STATE;
        }

        public boolean getInsetDodgeRect(CoordinatorLayout parent, V v, Rect rect) {
            return false;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface DefaultBehavior {
        Class<? extends Behavior> value();
    }

    private class HierarchyChangeListener implements OnHierarchyChangeListener {
        HierarchyChangeListener() {
        }

        public final void onChildViewAdded(View parent, View child) {
            if (CoordinatorLayout.this.mOnHierarchyChangeListener != null) {
                CoordinatorLayout.this.mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        public final void onChildViewRemoved(View parent, View child) {
            CoordinatorLayout.this.onChildViewsChanged(2);
            if (CoordinatorLayout.this.mOnHierarchyChangeListener != null) {
                CoordinatorLayout.this.mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int anchorGravity = 0;
        public int dodgeInsetEdges = 0;
        public int gravity = 0;
        public int insetEdge = 0;
        public int keyline = -1;
        View mAnchorDirectChild;
        int mAnchorId = -1;
        View mAnchorView;
        Behavior mBehavior;
        boolean mBehaviorResolved = false;
        Object mBehaviorTag;
        private boolean mDidAcceptNestedScroll;
        private boolean mDidBlockInteraction;
        private boolean mDidChangeAfterNestedScroll;
        int mInsetOffsetX;
        int mInsetOffsetY;
        final Rect mLastChildRect = new Rect();

        public LayoutParams(int width, int height) {
            super(-2, -2);
        }

        LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoordinatorLayout_Layout);
            this.gravity = a.getInteger(R.styleable.CoordinatorLayout_Layout_android_layout_gravity, 0);
            this.mAnchorId = a.getResourceId(R.styleable.CoordinatorLayout_Layout_layout_anchor, -1);
            this.anchorGravity = a.getInteger(R.styleable.CoordinatorLayout_Layout_layout_anchorGravity, 0);
            this.keyline = a.getInteger(R.styleable.CoordinatorLayout_Layout_layout_keyline, -1);
            this.insetEdge = a.getInt(R.styleable.CoordinatorLayout_Layout_layout_insetEdge, 0);
            this.dodgeInsetEdges = a.getInt(R.styleable.CoordinatorLayout_Layout_layout_dodgeInsetEdges, 0);
            this.mBehaviorResolved = a.hasValue(R.styleable.CoordinatorLayout_Layout_layout_behavior);
            if (this.mBehaviorResolved) {
                this.mBehavior = CoordinatorLayout.parseBehavior(context, attrs, a.getString(R.styleable.CoordinatorLayout_Layout_layout_behavior));
            }
            a.recycle();
            if (this.mBehavior != null) {
                this.mBehavior.onAttachedToLayoutParams(this);
            }
        }

        public LayoutParams(LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams p) {
            super(p);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams p) {
            super(p);
        }

        public final void setBehavior(Behavior behavior) {
            if (this.mBehavior != behavior) {
                this.mBehavior = behavior;
                this.mBehaviorTag = null;
                this.mBehaviorResolved = true;
                if (behavior != null) {
                    behavior.onAttachedToLayoutParams(this);
                }
            }
        }

        final boolean didBlockInteraction() {
            if (this.mBehavior == null) {
                this.mDidBlockInteraction = false;
            }
            return this.mDidBlockInteraction;
        }

        final boolean isBlockingInteractionBelow(CoordinatorLayout parent, View child) {
            if (this.mDidBlockInteraction) {
                return true;
            }
            boolean z = this.mDidBlockInteraction;
            if (this.mBehavior != null) {
                Behavior behavior = this.mBehavior;
            }
            this.mDidBlockInteraction = z;
            return z;
        }

        final void resetTouchBehaviorTracking() {
            this.mDidBlockInteraction = false;
        }

        final void resetNestedScroll() {
            this.mDidAcceptNestedScroll = false;
        }

        final void acceptNestedScroll(boolean accept) {
            this.mDidAcceptNestedScroll = accept;
        }

        final boolean isNestedScrollAccepted() {
            return this.mDidAcceptNestedScroll;
        }

        final boolean getChangedAfterNestedScroll() {
            return this.mDidChangeAfterNestedScroll;
        }

        final void setChangedAfterNestedScroll(boolean changed) {
            this.mDidChangeAfterNestedScroll = changed;
        }

        final void resetChangedAfterNestedScroll() {
            this.mDidChangeAfterNestedScroll = false;
        }

        final boolean dependsOn(CoordinatorLayout parent, View child, View dependency) {
            if (dependency != this.mAnchorDirectChild) {
                Object obj;
                int layoutDirection = ViewCompat.getLayoutDirection(parent);
                int absoluteGravity = GravityCompat.getAbsoluteGravity(((LayoutParams) dependency.getLayoutParams()).insetEdge, layoutDirection);
                if (absoluteGravity == 0 || (GravityCompat.getAbsoluteGravity(this.dodgeInsetEdges, layoutDirection) & absoluteGravity) != absoluteGravity) {
                    obj = null;
                } else {
                    obj = 1;
                }
                if (obj == null && (this.mBehavior == null || !this.mBehavior.layoutDependsOn$3747c3f0(dependency))) {
                    return false;
                }
            }
            return true;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        final android.view.View findAnchorView(android.support.design.widget.CoordinatorLayout r6, android.view.View r7) {
            /*
            r5 = this;
            r3 = 0;
            r2 = 0;
            r0 = r5.mAnchorId;
            r1 = -1;
            if (r0 != r1) goto L_0x000d;
        L_0x0007:
            r5.mAnchorDirectChild = r2;
            r5.mAnchorView = r2;
            r0 = r2;
        L_0x000c:
            return r0;
        L_0x000d:
            r0 = r5.mAnchorView;
            if (r0 == 0) goto L_0x001e;
        L_0x0011:
            r0 = r5.mAnchorView;
            r0 = r0.getId();
            r1 = r5.mAnchorId;
            if (r0 == r1) goto L_0x003b;
        L_0x001b:
            r0 = r3;
        L_0x001c:
            if (r0 != 0) goto L_0x0038;
        L_0x001e:
            r0 = r5.mAnchorId;
            r0 = r6.findViewById(r0);
            r5.mAnchorView = r0;
            r0 = r5.mAnchorView;
            if (r0 == 0) goto L_0x0097;
        L_0x002a:
            r0 = r5.mAnchorView;
            if (r0 != r6) goto L_0x0067;
        L_0x002e:
            r0 = r6.isInEditMode();
            if (r0 == 0) goto L_0x005f;
        L_0x0034:
            r5.mAnchorDirectChild = r2;
            r5.mAnchorView = r2;
        L_0x0038:
            r0 = r5.mAnchorView;
            goto L_0x000c;
        L_0x003b:
            r0 = r5.mAnchorView;
            r1 = r5.mAnchorView;
            r1 = r1.getParent();
        L_0x0043:
            if (r1 == r6) goto L_0x005b;
        L_0x0045:
            if (r1 == 0) goto L_0x0049;
        L_0x0047:
            if (r1 != r7) goto L_0x004f;
        L_0x0049:
            r5.mAnchorDirectChild = r2;
            r5.mAnchorView = r2;
            r0 = r3;
            goto L_0x001c;
        L_0x004f:
            r4 = r1 instanceof android.view.View;
            if (r4 == 0) goto L_0x0056;
        L_0x0053:
            r0 = r1;
            r0 = (android.view.View) r0;
        L_0x0056:
            r1 = r1.getParent();
            goto L_0x0043;
        L_0x005b:
            r5.mAnchorDirectChild = r0;
            r0 = 1;
            goto L_0x001c;
        L_0x005f:
            r0 = new java.lang.IllegalStateException;
            r1 = "View can not be anchored to the the parent CoordinatorLayout";
            r0.<init>(r1);
            throw r0;
        L_0x0067:
            r0 = r5.mAnchorView;
            r1 = r5.mAnchorView;
            r1 = r1.getParent();
        L_0x006f:
            if (r1 == r6) goto L_0x0094;
        L_0x0071:
            if (r1 == 0) goto L_0x0094;
        L_0x0073:
            if (r1 != r7) goto L_0x0088;
        L_0x0075:
            r0 = r6.isInEditMode();
            if (r0 == 0) goto L_0x0080;
        L_0x007b:
            r5.mAnchorDirectChild = r2;
            r5.mAnchorView = r2;
            goto L_0x0038;
        L_0x0080:
            r0 = new java.lang.IllegalStateException;
            r1 = "Anchor must not be a descendant of the anchored view";
            r0.<init>(r1);
            throw r0;
        L_0x0088:
            r3 = r1 instanceof android.view.View;
            if (r3 == 0) goto L_0x008f;
        L_0x008c:
            r0 = r1;
            r0 = (android.view.View) r0;
        L_0x008f:
            r1 = r1.getParent();
            goto L_0x006f;
        L_0x0094:
            r5.mAnchorDirectChild = r0;
            goto L_0x0038;
        L_0x0097:
            r0 = r6.isInEditMode();
            if (r0 == 0) goto L_0x00a2;
        L_0x009d:
            r5.mAnchorDirectChild = r2;
            r5.mAnchorView = r2;
            goto L_0x0038;
        L_0x00a2:
            r0 = new java.lang.IllegalStateException;
            r1 = new java.lang.StringBuilder;
            r2 = "Could not find CoordinatorLayout descendant view with id ";
            r1.<init>(r2);
            r2 = r6.getResources();
            r3 = r5.mAnchorId;
            r2 = r2.getResourceName(r3);
            r1 = r1.append(r2);
            r2 = " to anchor view ";
            r1 = r1.append(r2);
            r1 = r1.append(r7);
            r1 = r1.toString();
            r0.<init>(r1);
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.CoordinatorLayout.LayoutParams.findAnchorView(android.support.design.widget.CoordinatorLayout, android.view.View):android.view.View");
        }
    }

    class OnPreDrawListener implements android.view.ViewTreeObserver.OnPreDrawListener {
        OnPreDrawListener() {
        }

        public final boolean onPreDraw() {
            CoordinatorLayout.this.onChildViewsChanged(0);
            return true;
        }
    }

    protected static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
            public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
                return new SavedState[i];
            }

            public final /* bridge */ /* synthetic */ Object createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }
        });
        SparseArray<Parcelable> behaviorStates;

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            int size = source.readInt();
            int[] ids = new int[size];
            source.readIntArray(ids);
            Parcelable[] states = source.readParcelableArray(loader);
            this.behaviorStates = new SparseArray(size);
            for (int i = 0; i < size; i++) {
                this.behaviorStates.append(ids[i], states[i]);
            }
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public final void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            int size = this.behaviorStates != null ? this.behaviorStates.size() : 0;
            dest.writeInt(size);
            int[] ids = new int[size];
            Parcelable[] states = new Parcelable[size];
            for (int i = 0; i < size; i++) {
                ids[i] = this.behaviorStates.keyAt(i);
                states[i] = (Parcelable) this.behaviorStates.valueAt(i);
            }
            dest.writeIntArray(ids);
            dest.writeParcelableArray(states, flags);
        }
    }

    static class ViewElevationComparator implements Comparator<View> {
        ViewElevationComparator() {
        }

        public final /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
            View view = (View) obj2;
            float z = ViewCompat.getZ((View) obj);
            float z2 = ViewCompat.getZ(view);
            if (z > z2) {
                return -1;
            }
            if (z < z2) {
                return 1;
            }
            return 0;
        }
    }

    static {
        Package pkg = CoordinatorLayout.class.getPackage();
        WIDGET_PACKAGE_NAME = pkg != null ? pkg.getName() : null;
        if (VERSION.SDK_INT >= 21) {
            TOP_SORTED_CHILDREN_COMPARATOR = new ViewElevationComparator();
        } else {
            TOP_SORTED_CHILDREN_COMPARATOR = null;
        }
    }

    private static Rect acquireTempRect() {
        Rect rect = (Rect) sRectPool.acquire();
        if (rect == null) {
            return new Rect();
        }
        return rect;
    }

    private static void releaseTempRect(Rect rect) {
        rect.setEmpty();
        sRectPool.release(rect);
    }

    public CoordinatorLayout(Context context) {
        this(context, null);
    }

    public CoordinatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDependencySortedChildren = new ArrayList();
        this.mChildDag = new DirectedAcyclicGraph();
        this.mTempList1 = new ArrayList();
        this.mTempDependenciesList = new ArrayList();
        this.mTempIntPair = new int[2];
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        ThemeUtils.checkAppCompatTheme(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoordinatorLayout, defStyleAttr, com.rachio.iro.R.style.Widget.Design.CoordinatorLayout);
        int keylineArrayRes = a.getResourceId(R.styleable.CoordinatorLayout_keylines, 0);
        if (keylineArrayRes != 0) {
            Resources res = context.getResources();
            this.mKeylines = res.getIntArray(keylineArrayRes);
            float density = res.getDisplayMetrics().density;
            int count = this.mKeylines.length;
            for (int i = 0; i < count; i++) {
                int[] iArr = this.mKeylines;
                iArr[i] = (int) (((float) iArr[i]) * density);
            }
        }
        this.mStatusBarBackground = a.getDrawable(R.styleable.CoordinatorLayout_statusBarBackground);
        a.recycle();
        setupForInsets();
        super.setOnHierarchyChangeListener(new HierarchyChangeListener());
    }

    public void setOnHierarchyChangeListener(OnHierarchyChangeListener onHierarchyChangeListener) {
        this.mOnHierarchyChangeListener = onHierarchyChangeListener;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        resetTouchBehaviors();
        if (this.mNeedsPreDrawListener) {
            if (this.mOnPreDrawListener == null) {
                this.mOnPreDrawListener = new OnPreDrawListener();
            }
            getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
        }
        if (this.mLastInsets == null && ViewCompat.getFitsSystemWindows(this)) {
            ViewCompat.requestApplyInsets(this);
        }
        this.mIsAttachedToWindow = true;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resetTouchBehaviors();
        if (this.mNeedsPreDrawListener && this.mOnPreDrawListener != null) {
            getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
        }
        if (this.mNestedScrollingTarget != null) {
            onStopNestedScroll(this.mNestedScrollingTarget);
        }
        this.mIsAttachedToWindow = false;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] state = getDrawableState();
        boolean changed = false;
        Drawable d = this.mStatusBarBackground;
        if (d != null && d.isStateful()) {
            changed = d.setState(state) | 0;
        }
        if (changed) {
            invalidate();
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mStatusBarBackground;
    }

    public void setVisibility(int visibility) {
        boolean visible;
        super.setVisibility(visibility);
        if (visibility == 0) {
            visible = true;
        } else {
            visible = false;
        }
        if (this.mStatusBarBackground != null && this.mStatusBarBackground.isVisible() != visible) {
            this.mStatusBarBackground.setVisible(visible, false);
        }
    }

    final WindowInsetsCompat setWindowInsets(WindowInsetsCompat insets) {
        boolean z = true;
        int i = 0;
        if (!ViewUtils.objectEquals(this.mLastInsets, insets)) {
            this.mLastInsets = insets;
            boolean z2 = insets != null && insets.getSystemWindowInsetTop() > 0;
            this.mDrawStatusBarBackground = z2;
            if (this.mDrawStatusBarBackground || getBackground() != null) {
                z = false;
            }
            setWillNotDraw(z);
            if (!insets.isConsumed()) {
                int childCount = getChildCount();
                WindowInsetsCompat windowInsetsCompat = insets;
                while (i < childCount) {
                    View childAt = getChildAt(i);
                    if (ViewCompat.getFitsSystemWindows(childAt) && ((LayoutParams) childAt.getLayoutParams()).mBehavior != null && windowInsetsCompat.isConsumed()) {
                        break;
                    }
                    i++;
                    windowInsetsCompat = windowInsetsCompat;
                }
                insets = windowInsetsCompat;
            }
            requestLayout();
        }
        return insets;
    }

    final WindowInsetsCompat getLastWindowInsets() {
        return this.mLastInsets;
    }

    private void resetTouchBehaviors() {
        if (this.mBehaviorTouchView != null) {
            Behavior b = ((LayoutParams) this.mBehaviorTouchView.getLayoutParams()).mBehavior;
            if (b != null) {
                long now = SystemClock.uptimeMillis();
                MotionEvent cancelEvent = MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0);
                b.onTouchEvent(this, this.mBehaviorTouchView, cancelEvent);
                cancelEvent.recycle();
            }
            this.mBehaviorTouchView = null;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((LayoutParams) getChildAt(i).getLayoutParams()).resetTouchBehaviorTracking();
        }
        this.mDisallowInterceptReset = false;
    }

    private boolean performIntercept(MotionEvent ev, int type) {
        boolean intercepted = false;
        boolean newBlock = false;
        MotionEvent cancelEvent = null;
        int action = MotionEventCompat.getActionMasked(ev);
        List<View> topmostChildList = this.mTempList1;
        topmostChildList.clear();
        boolean isChildrenDrawingOrderEnabled = isChildrenDrawingOrderEnabled();
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            int childDrawingOrder;
            if (isChildrenDrawingOrderEnabled) {
                childDrawingOrder = getChildDrawingOrder(childCount, i);
            } else {
                childDrawingOrder = i;
            }
            topmostChildList.add(getChildAt(childDrawingOrder));
        }
        if (TOP_SORTED_CHILDREN_COMPARATOR != null) {
            Collections.sort(topmostChildList, TOP_SORTED_CHILDREN_COMPARATOR);
        }
        int childCount2 = topmostChildList.size();
        for (int i2 = 0; i2 < childCount2; i2++) {
            View child = (View) topmostChildList.get(i2);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Behavior b = lp.mBehavior;
            if ((intercepted || newBlock) && action != 0) {
                if (b != null) {
                    if (cancelEvent == null) {
                        long now = SystemClock.uptimeMillis();
                        cancelEvent = MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0);
                    }
                    switch (type) {
                        case 0:
                            b.onInterceptTouchEvent(this, child, cancelEvent);
                            break;
                        case 1:
                            b.onTouchEvent(this, child, cancelEvent);
                            break;
                        default:
                            break;
                    }
                }
            } else {
                if (!(intercepted || b == null)) {
                    switch (type) {
                        case 0:
                            intercepted = b.onInterceptTouchEvent(this, child, ev);
                            break;
                        case 1:
                            intercepted = b.onTouchEvent(this, child, ev);
                            break;
                    }
                    if (intercepted) {
                        this.mBehaviorTouchView = child;
                    }
                }
                boolean wasBlocking = lp.didBlockInteraction();
                boolean isBlocking = lp.isBlockingInteractionBelow(this, child);
                newBlock = isBlocking && !wasBlocking;
                if (isBlocking && !newBlock) {
                    topmostChildList.clear();
                    return intercepted;
                }
            }
        }
        topmostChildList.clear();
        return intercepted;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == 0) {
            resetTouchBehaviors();
        }
        boolean intercepted = performIntercept(ev, 0);
        if (action == 1 || action == 3) {
            resetTouchBehaviors();
        }
        return intercepted;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
        r14 = this;
        r12 = 0;
        r11 = 0;
        r10 = 0;
        r8 = android.support.v4.view.MotionEventCompat.getActionMasked(r15);
        r2 = r14.mBehaviorTouchView;
        if (r2 != 0) goto L_0x0012;
    L_0x000b:
        r2 = 1;
        r11 = r14.performIntercept(r15, r2);
        if (r11 == 0) goto L_0x0024;
    L_0x0012:
        r2 = r14.mBehaviorTouchView;
        r13 = r2.getLayoutParams();
        r13 = (android.support.design.widget.CoordinatorLayout.LayoutParams) r13;
        r9 = r13.mBehavior;
        if (r9 == 0) goto L_0x0024;
    L_0x001e:
        r2 = r14.mBehaviorTouchView;
        r12 = r9.onTouchEvent(r14, r2, r15);
    L_0x0024:
        r2 = r14.mBehaviorTouchView;
        if (r2 != 0) goto L_0x003c;
    L_0x0028:
        r2 = super.onTouchEvent(r15);
        r12 = r12 | r2;
    L_0x002d:
        if (r10 == 0) goto L_0x0032;
    L_0x002f:
        r10.recycle();
    L_0x0032:
        r2 = 1;
        if (r8 == r2) goto L_0x0038;
    L_0x0035:
        r2 = 3;
        if (r8 != r2) goto L_0x003b;
    L_0x0038:
        r14.resetTouchBehaviors();
    L_0x003b:
        return r12;
    L_0x003c:
        if (r11 == 0) goto L_0x002d;
    L_0x003e:
        r0 = android.os.SystemClock.uptimeMillis();
        r4 = 3;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r2 = r0;
        r10 = android.view.MotionEvent.obtain(r0, r2, r4, r5, r6, r7);
        super.onTouchEvent(r10);
        goto L_0x002d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.CoordinatorLayout.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        if (disallowIntercept && !this.mDisallowInterceptReset) {
            resetTouchBehaviors();
            this.mDisallowInterceptReset = true;
        }
    }

    private int getKeyline(int index) {
        if (this.mKeylines == null) {
            Log.e("CoordinatorLayout", "No keylines defined for " + this + " - attempted index lookup " + index);
            return 0;
        } else if (index >= 0 && index < this.mKeylines.length) {
            return this.mKeylines[index];
        } else {
            Log.e("CoordinatorLayout", "Keyline index " + index + " out of range for " + this);
            return 0;
        }
    }

    static Behavior parseBehavior(Context context, AttributeSet attrs, String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        String fullName;
        if (name.startsWith(".")) {
            fullName = context.getPackageName() + name;
        } else if (name.indexOf(46) >= 0) {
            fullName = name;
        } else {
            fullName = !TextUtils.isEmpty(WIDGET_PACKAGE_NAME) ? WIDGET_PACKAGE_NAME + '.' + name : name;
        }
        try {
            Map<String, Constructor<Behavior>> constructors = (Map) sConstructors.get();
            if (constructors == null) {
                constructors = new HashMap();
                sConstructors.set(constructors);
            }
            Constructor<Behavior> c = (Constructor) constructors.get(fullName);
            if (c == null) {
                c = Class.forName(fullName, true, context.getClassLoader()).getConstructor(CONSTRUCTOR_PARAMS);
                c.setAccessible(true);
                constructors.put(fullName, c);
            }
            return (Behavior) c.newInstance(new Object[]{context, attrs});
        } catch (Exception e) {
            throw new RuntimeException("Could not inflate Behavior subclass " + fullName, e);
        }
    }

    private static LayoutParams getResolvedLayoutParams(View child) {
        LayoutParams result = (LayoutParams) child.getLayoutParams();
        if (!result.mBehaviorResolved) {
            DefaultBehavior defaultBehavior = null;
            for (Class<?> childClass = child.getClass(); childClass != null; childClass = childClass.getSuperclass()) {
                defaultBehavior = (DefaultBehavior) childClass.getAnnotation(DefaultBehavior.class);
                if (defaultBehavior != null) {
                    break;
                }
            }
            if (defaultBehavior != null) {
                try {
                    result.setBehavior((Behavior) defaultBehavior.value().newInstance());
                } catch (Exception e) {
                    Log.e("CoordinatorLayout", "Default behavior class " + defaultBehavior.value().getName() + " could not be instantiated. Did you forget a default constructor?", e);
                }
            }
            result.mBehaviorResolved = true;
        }
        return result;
    }

    protected int getSuggestedMinimumWidth() {
        return Math.max(super.getSuggestedMinimumWidth(), getPaddingLeft() + getPaddingRight());
    }

    protected int getSuggestedMinimumHeight() {
        return Math.max(super.getSuggestedMinimumHeight(), getPaddingTop() + getPaddingBottom());
    }

    public final void onMeasureChild(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    public final void onLayoutChild(View child, int layoutDirection) {
        Object obj;
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp.mAnchorView != null || lp.mAnchorId == -1) {
            obj = null;
        } else {
            obj = 1;
        }
        if (obj != null) {
            throw new IllegalStateException("An anchor may not be changed after CoordinatorLayout measurement begins before layout is complete.");
        } else if (lp.mAnchorView != null) {
            View view = lp.mAnchorView;
            child.getLayoutParams();
            Rect acquireTempRect = acquireTempRect();
            Rect acquireTempRect2 = acquireTempRect();
            try {
                ViewGroupUtils.getDescendantRect(this, view, acquireTempRect);
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                r4 = child.getMeasuredWidth();
                r5 = child.getMeasuredHeight();
                getDesiredAnchoredChildRectWithoutConstraints$50164761(layoutDirection, acquireTempRect, acquireTempRect2, layoutParams, r4, r5);
                constrainChildRect(layoutParams, acquireTempRect2, r4, r5);
                child.layout(acquireTempRect2.left, acquireTempRect2.top, acquireTempRect2.right, acquireTempRect2.bottom);
            } finally {
                releaseTempRect(acquireTempRect);
                releaseTempRect(acquireTempRect2);
            }
        } else if (lp.keyline >= 0) {
            int i;
            int i2 = lp.keyline;
            r0 = (LayoutParams) child.getLayoutParams();
            r4 = GravityCompat.getAbsoluteGravity(resolveKeylineGravity(r0.gravity), layoutDirection);
            r5 = r4 & 7;
            r4 &= 112;
            int width = getWidth();
            int height = getHeight();
            int measuredWidth = child.getMeasuredWidth();
            int measuredHeight = child.getMeasuredHeight();
            if (layoutDirection == 1) {
                i2 = width - i2;
            }
            i2 = getKeyline(i2) - measuredWidth;
            switch (r5) {
                case 1:
                    i = i2 + (measuredWidth / 2);
                    break;
                case 5:
                    i = i2 + measuredWidth;
                    break;
                default:
                    i = i2;
                    break;
            }
            switch (r4) {
                case 16:
                    i2 = (measuredHeight / 2) + 0;
                    break;
                case 80:
                    i2 = measuredHeight + 0;
                    break;
                default:
                    i2 = 0;
                    break;
            }
            int max = Math.max(getPaddingLeft() + r0.leftMargin, Math.min(i, ((width - getPaddingRight()) - measuredWidth) - r0.rightMargin));
            int max2 = Math.max(getPaddingTop() + r0.topMargin, Math.min(i2, ((height - getPaddingBottom()) - measuredHeight) - r0.bottomMargin));
            child.layout(max, max2, max + measuredWidth, max2 + measuredHeight);
        } else {
            r0 = (LayoutParams) child.getLayoutParams();
            Rect acquireTempRect3 = acquireTempRect();
            acquireTempRect3.set(getPaddingLeft() + r0.leftMargin, getPaddingTop() + r0.topMargin, (getWidth() - getPaddingRight()) - r0.rightMargin, (getHeight() - getPaddingBottom()) - r0.bottomMargin);
            if (!(this.mLastInsets == null || !ViewCompat.getFitsSystemWindows(this) || ViewCompat.getFitsSystemWindows(child))) {
                acquireTempRect3.left += this.mLastInsets.getSystemWindowInsetLeft();
                acquireTempRect3.top += this.mLastInsets.getSystemWindowInsetTop();
                acquireTempRect3.right -= this.mLastInsets.getSystemWindowInsetRight();
                acquireTempRect3.bottom -= this.mLastInsets.getSystemWindowInsetBottom();
            }
            Rect acquireTempRect4 = acquireTempRect();
            GravityCompat.apply(resolveGravity(r0.gravity), child.getMeasuredWidth(), child.getMeasuredHeight(), acquireTempRect3, acquireTempRect4, layoutDirection);
            child.layout(acquireTempRect4.left, acquireTempRect4.top, acquireTempRect4.right, acquireTempRect4.bottom);
            releaseTempRect(acquireTempRect3);
            releaseTempRect(acquireTempRect4);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        int childCount = this.mDependencySortedChildren.size();
        for (int i = 0; i < childCount; i++) {
            View child = (View) this.mDependencySortedChildren.get(i);
            if (child.getVisibility() != 8) {
                Behavior behavior = ((LayoutParams) child.getLayoutParams()).mBehavior;
                if (behavior == null || !behavior.onLayoutChild(this, child, layoutDirection)) {
                    onLayoutChild(child, layoutDirection);
                }
            }
        }
    }

    public void onDraw(Canvas c) {
        super.onDraw(c);
        if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
            int inset = this.mLastInsets != null ? this.mLastInsets.getSystemWindowInsetTop() : 0;
            if (inset > 0) {
                this.mStatusBarBackground.setBounds(0, 0, getWidth(), inset);
                this.mStatusBarBackground.draw(c);
            }
        }
    }

    public void setFitsSystemWindows(boolean fitSystemWindows) {
        super.setFitsSystemWindows(fitSystemWindows);
        setupForInsets();
    }

    private void getChildRect(View child, boolean transform, Rect out) {
        if (child.isLayoutRequested() || child.getVisibility() == 8) {
            out.setEmpty();
        } else if (transform) {
            ViewGroupUtils.getDescendantRect(this, child, out);
        } else {
            out.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }
    }

    private static void getDesiredAnchoredChildRectWithoutConstraints$50164761(int layoutDirection, Rect anchorRect, Rect out, LayoutParams lp, int childWidth, int childHeight) {
        int left;
        int top;
        int i = lp.gravity;
        if (i == 0) {
            i = 17;
        }
        int absGravity = GravityCompat.getAbsoluteGravity(i, layoutDirection);
        int absAnchorGravity = GravityCompat.getAbsoluteGravity(resolveGravity(lp.anchorGravity), layoutDirection);
        int hgrav = absGravity & 7;
        int vgrav = absGravity & 112;
        int anchorVgrav = absAnchorGravity & 112;
        switch (absAnchorGravity & 7) {
            case 1:
                left = anchorRect.left + (anchorRect.width() / 2);
                break;
            case 5:
                left = anchorRect.right;
                break;
            default:
                left = anchorRect.left;
                break;
        }
        switch (anchorVgrav) {
            case 16:
                top = anchorRect.top + (anchorRect.height() / 2);
                break;
            case 80:
                top = anchorRect.bottom;
                break;
            default:
                top = anchorRect.top;
                break;
        }
        switch (hgrav) {
            case 1:
                left -= childWidth / 2;
                break;
            case 5:
                break;
            default:
                left -= childWidth;
                break;
        }
        switch (vgrav) {
            case 16:
                top -= childHeight / 2;
                break;
            case 80:
                break;
            default:
                top -= childHeight;
                break;
        }
        out.set(left, top, left + childWidth, top + childHeight);
    }

    private void constrainChildRect(LayoutParams lp, Rect out, int childWidth, int childHeight) {
        int width = getWidth();
        int height = getHeight();
        int left = Math.max(getPaddingLeft() + lp.leftMargin, Math.min(out.left, ((width - getPaddingRight()) - childWidth) - lp.rightMargin));
        int top = Math.max(getPaddingTop() + lp.topMargin, Math.min(out.top, ((height - getPaddingBottom()) - childHeight) - lp.bottomMargin));
        out.set(left, top, left + childWidth, top + childHeight);
    }

    private static int resolveGravity(int gravity) {
        if ((gravity & 7) == 0) {
            gravity |= 8388611;
        }
        if ((gravity & 112) == 0) {
            return gravity | 48;
        }
        return gravity;
    }

    private static int resolveKeylineGravity(int gravity) {
        return gravity == 0 ? 8388661 : gravity;
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        child.getLayoutParams();
        return super.drawChild(canvas, child, drawingTime);
    }

    final void onChildViewsChanged(int type) {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        int childCount = this.mDependencySortedChildren.size();
        Rect inset = acquireTempRect();
        Rect drawRect = acquireTempRect();
        Rect lastDrawRect = acquireTempRect();
        for (int i = 0; i < childCount; i++) {
            View child = (View) this.mDependencySortedChildren.get(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (type != 0 || child.getVisibility() != 8) {
                int j;
                int measuredWidth;
                int measuredHeight;
                for (j = 0; j < i; j++) {
                    if (lp.mAnchorDirectChild == ((View) this.mDependencySortedChildren.get(j))) {
                        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                        if (layoutParams.mAnchorView != null) {
                            Rect acquireTempRect = acquireTempRect();
                            Rect acquireTempRect2 = acquireTempRect();
                            Rect acquireTempRect3 = acquireTempRect();
                            ViewGroupUtils.getDescendantRect(this, layoutParams.mAnchorView, acquireTempRect);
                            getChildRect(child, false, acquireTempRect2);
                            measuredWidth = child.getMeasuredWidth();
                            measuredHeight = child.getMeasuredHeight();
                            getDesiredAnchoredChildRectWithoutConstraints$50164761(layoutDirection, acquireTempRect, acquireTempRect3, layoutParams, measuredWidth, measuredHeight);
                            Object obj = (acquireTempRect3.left == acquireTempRect2.left && acquireTempRect3.top == acquireTempRect2.top) ? null : 1;
                            constrainChildRect(layoutParams, acquireTempRect3, measuredWidth, measuredHeight);
                            measuredWidth = acquireTempRect3.left - acquireTempRect2.left;
                            measuredHeight = acquireTempRect3.top - acquireTempRect2.top;
                            if (measuredWidth != 0) {
                                ViewCompat.offsetLeftAndRight(child, measuredWidth);
                            }
                            if (measuredHeight != 0) {
                                ViewCompat.offsetTopAndBottom(child, measuredHeight);
                            }
                            if (obj != null) {
                                Behavior behavior = layoutParams.mBehavior;
                                if (behavior != null) {
                                    behavior.onDependentViewChanged(this, child, layoutParams.mAnchorView);
                                }
                            }
                            releaseTempRect(acquireTempRect);
                            releaseTempRect(acquireTempRect2);
                            releaseTempRect(acquireTempRect3);
                        }
                    }
                }
                getChildRect(child, true, drawRect);
                if (!(lp.insetEdge == 0 || drawRect.isEmpty())) {
                    int absInsetEdge = GravityCompat.getAbsoluteGravity(lp.insetEdge, layoutDirection);
                    switch (absInsetEdge & 112) {
                        case com.shinobicontrols.charts.R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor4 /*48*/:
                            inset.top = Math.max(inset.top, drawRect.bottom);
                            break;
                        case 80:
                            inset.bottom = Math.max(inset.bottom, getHeight() - drawRect.top);
                            break;
                    }
                    switch (absInsetEdge & 7) {
                        case 3:
                            inset.left = Math.max(inset.left, drawRect.right);
                            break;
                        case 5:
                            inset.right = Math.max(inset.right, getWidth() - drawRect.left);
                            break;
                    }
                }
                if (lp.dodgeInsetEdges != 0 && child.getVisibility() == 0 && ViewCompat.isLaidOut(child) && child.getWidth() > 0 && child.getHeight() > 0) {
                    LayoutParams layoutParams2 = (LayoutParams) child.getLayoutParams();
                    Behavior behavior2 = layoutParams2.mBehavior;
                    Rect acquireTempRect4 = acquireTempRect();
                    Rect acquireTempRect5 = acquireTempRect();
                    acquireTempRect5.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                    if (behavior2 == null || !behavior2.getInsetDodgeRect(this, child, acquireTempRect4)) {
                        acquireTempRect4.set(acquireTempRect5);
                    } else if (!acquireTempRect5.contains(acquireTempRect4)) {
                        throw new IllegalArgumentException("Rect should be within the child's bounds. Rect:" + acquireTempRect4.toShortString() + " | Bounds:" + acquireTempRect5.toShortString());
                    }
                    releaseTempRect(acquireTempRect5);
                    if (!acquireTempRect4.isEmpty()) {
                        Object obj2;
                        measuredWidth = GravityCompat.getAbsoluteGravity(layoutParams2.dodgeInsetEdges, layoutDirection);
                        Object obj3 = null;
                        if ((measuredWidth & 48) == 48) {
                            measuredHeight = (acquireTempRect4.top - layoutParams2.topMargin) - layoutParams2.mInsetOffsetY;
                            if (measuredHeight < inset.top) {
                                setInsetOffsetY(child, inset.top - measuredHeight);
                                obj3 = 1;
                            }
                        }
                        if ((measuredWidth & 80) == 80) {
                            measuredHeight = ((getHeight() - acquireTempRect4.bottom) - layoutParams2.bottomMargin) + layoutParams2.mInsetOffsetY;
                            if (measuredHeight < inset.bottom) {
                                setInsetOffsetY(child, measuredHeight - inset.bottom);
                                obj3 = 1;
                            }
                        }
                        if (obj3 == null) {
                            setInsetOffsetY(child, 0);
                        }
                        obj3 = null;
                        if ((measuredWidth & 3) == 3) {
                            measuredHeight = (acquireTempRect4.left - layoutParams2.leftMargin) - layoutParams2.mInsetOffsetX;
                            if (measuredHeight < inset.left) {
                                setInsetOffsetX(child, inset.left - measuredHeight);
                                obj3 = 1;
                            }
                        }
                        if ((measuredWidth & 5) == 5) {
                            int width = layoutParams2.mInsetOffsetX + ((getWidth() - acquireTempRect4.right) - layoutParams2.rightMargin);
                            if (width < inset.right) {
                                setInsetOffsetX(child, width - inset.right);
                                obj2 = 1;
                                if (obj2 == null) {
                                    setInsetOffsetX(child, 0);
                                }
                            }
                        }
                        obj2 = obj3;
                        if (obj2 == null) {
                            setInsetOffsetX(child, 0);
                        }
                    }
                    releaseTempRect(acquireTempRect4);
                }
                if (type != 2) {
                    lastDrawRect.set(((LayoutParams) child.getLayoutParams()).mLastChildRect);
                    if (!lastDrawRect.equals(drawRect)) {
                        ((LayoutParams) child.getLayoutParams()).mLastChildRect.set(drawRect);
                    }
                }
                for (j = i + 1; j < childCount; j++) {
                    View checkChild = (View) this.mDependencySortedChildren.get(j);
                    LayoutParams checkLp = (LayoutParams) checkChild.getLayoutParams();
                    Behavior b = checkLp.mBehavior;
                    if (b != null && b.layoutDependsOn$3747c3f0(child)) {
                        if (type == 0 && checkLp.getChangedAfterNestedScroll()) {
                            checkLp.resetChangedAfterNestedScroll();
                        } else {
                            boolean handled;
                            switch (type) {
                                case 2:
                                    handled = true;
                                    break;
                                default:
                                    handled = b.onDependentViewChanged(this, checkChild, child);
                                    break;
                            }
                            if (type == 1) {
                                checkLp.setChangedAfterNestedScroll(handled);
                            }
                        }
                    }
                }
            }
        }
        releaseTempRect(inset);
        releaseTempRect(drawRect);
        releaseTempRect(lastDrawRect);
    }

    private static void setInsetOffsetX(View child, int offsetX) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp.mInsetOffsetX != offsetX) {
            ViewCompat.offsetLeftAndRight(child, offsetX - lp.mInsetOffsetX);
            lp.mInsetOffsetX = offsetX;
        }
    }

    private static void setInsetOffsetY(View child, int offsetY) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp.mInsetOffsetY != offsetY) {
            ViewCompat.offsetTopAndBottom(child, offsetY - lp.mInsetOffsetY);
            lp.mInsetOffsetY = offsetY;
        }
    }

    public final void dispatchDependentViewsChanged(View view) {
        List<View> dependents = this.mChildDag.getIncomingEdges(view);
        if (dependents != null && !dependents.isEmpty()) {
            for (int i = 0; i < dependents.size(); i++) {
                View child = (View) dependents.get(i);
                Behavior b = ((LayoutParams) child.getLayoutParams()).mBehavior;
                if (b != null) {
                    b.onDependentViewChanged(this, child, view);
                }
            }
        }
    }

    public final List<View> getDependencies(View child) {
        List<View> dependencies = this.mChildDag.getOutgoingEdges(child);
        this.mTempDependenciesList.clear();
        if (dependencies != null) {
            this.mTempDependenciesList.addAll(dependencies);
        }
        return this.mTempDependenciesList;
    }

    public final List<View> getDependents(View child) {
        List<View> edges = this.mChildDag.getIncomingEdges(child);
        this.mTempDependenciesList.clear();
        if (edges != null) {
            this.mTempDependenciesList.addAll(edges);
        }
        return this.mTempDependenciesList;
    }

    public final boolean isPointInChildBounds(View child, int x, int y) {
        Rect r = acquireTempRect();
        ViewGroupUtils.getDescendantRect(this, child, r);
        try {
            boolean contains = r.contains(x, y);
            return contains;
        } finally {
            releaseTempRect(r);
        }
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return (p instanceof LayoutParams) && super.checkLayoutParams(p);
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        boolean handled = false;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                Behavior viewBehavior = lp.mBehavior;
                if (viewBehavior != null) {
                    boolean accepted = viewBehavior.onStartNestedScroll(this, view, child, target, nestedScrollAxes);
                    handled |= accepted;
                    lp.acceptNestedScroll(accepted);
                } else {
                    lp.acceptNestedScroll(false);
                }
            }
        }
        return handled;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted$244b0b2e(nestedScrollAxes);
        this.mNestedScrollingDirectChild = child;
        this.mNestedScrollingTarget = target;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).getLayoutParams();
        }
    }

    public void onStopNestedScroll(View target) {
        this.mNestedScrollingParentHelper.onStopNestedScroll$3c7ec8c3();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            if (lp.isNestedScrollAccepted()) {
                Behavior viewBehavior = lp.mBehavior;
                if (viewBehavior != null) {
                    viewBehavior.onStopNestedScroll(this, view, target);
                }
                lp.resetNestedScroll();
                lp.resetChangedAfterNestedScroll();
            }
        }
        this.mNestedScrollingDirectChild = null;
        this.mNestedScrollingTarget = null;
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        int childCount = getChildCount();
        boolean accepted = false;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                if (lp.isNestedScrollAccepted()) {
                    Behavior viewBehavior = lp.mBehavior;
                    if (viewBehavior != null) {
                        viewBehavior.onNestedScroll(this, view, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
                        accepted = true;
                    }
                }
            }
        }
        if (accepted) {
            onChildViewsChanged(1);
        }
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        int xConsumed = 0;
        int yConsumed = 0;
        boolean accepted = false;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                if (lp.isNestedScrollAccepted()) {
                    Behavior viewBehavior = lp.mBehavior;
                    if (viewBehavior != null) {
                        int[] iArr = this.mTempIntPair;
                        this.mTempIntPair[1] = 0;
                        iArr[0] = 0;
                        viewBehavior.onNestedPreScroll(this, view, target, dx, dy, this.mTempIntPair);
                        if (dx > 0) {
                            xConsumed = Math.max(xConsumed, this.mTempIntPair[0]);
                        } else {
                            xConsumed = Math.min(xConsumed, this.mTempIntPair[0]);
                        }
                        if (dy > 0) {
                            yConsumed = Math.max(yConsumed, this.mTempIntPair[1]);
                        } else {
                            yConsumed = Math.min(yConsumed, this.mTempIntPair[1]);
                        }
                        accepted = true;
                    }
                }
            }
        }
        consumed[0] = xConsumed;
        consumed[1] = yConsumed;
        if (accepted) {
            onChildViewsChanged(1);
        }
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        boolean handled = false;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                if (lp.isNestedScrollAccepted()) {
                    Behavior viewBehavior = lp.mBehavior;
                    if (viewBehavior != null) {
                        handled |= viewBehavior.onNestedFling(this, view, target, velocityX, velocityY, consumed);
                    }
                }
            }
        }
        if (handled) {
            onChildViewsChanged(1);
        }
        return handled;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        boolean handled = false;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                if (lp.isNestedScrollAccepted()) {
                    Behavior viewBehavior = lp.mBehavior;
                    if (viewBehavior != null) {
                        handled |= viewBehavior.onNestedPreFling(this, view, target, velocityX, velocityY);
                    }
                }
            }
        }
        return handled;
    }

    public int getNestedScrollAxes() {
        return this.mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            SparseArray<Parcelable> behaviorStates = ss.behaviorStates;
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                int childId = child.getId();
                Behavior b = getResolvedLayoutParams(child).mBehavior;
                if (!(childId == -1 || b == null)) {
                    Parcelable savedState = (Parcelable) behaviorStates.get(childId);
                    if (savedState != null) {
                        b.onRestoreInstanceState(this, child, savedState);
                    }
                }
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }

    protected Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        SparseArray<Parcelable> behaviorStates = new SparseArray();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int childId = child.getId();
            Behavior b = ((LayoutParams) child.getLayoutParams()).mBehavior;
            if (!(childId == -1 || b == null)) {
                Parcelable state = b.onSaveInstanceState(this, child);
                if (state != null) {
                    behaviorStates.append(childId, state);
                }
            }
        }
        ss.behaviorStates = behaviorStates;
        return ss;
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        Behavior behavior = ((LayoutParams) child.getLayoutParams()).mBehavior;
        if (behavior == null || !behavior.onRequestChildRectangleOnScreen(this, child, rectangle, immediate)) {
            return super.requestChildRectangleOnScreen(child, rectangle, immediate);
        }
        return true;
    }

    private void setupForInsets() {
        if (VERSION.SDK_INT >= 21) {
            if (ViewCompat.getFitsSystemWindows(this)) {
                if (this.mApplyWindowInsetsListener == null) {
                    this.mApplyWindowInsetsListener = new OnApplyWindowInsetsListener() {
                        public final WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                            return CoordinatorLayout.this.setWindowInsets(insets);
                        }
                    };
                }
                ViewCompat.setOnApplyWindowInsetsListener(this, this.mApplyWindowInsetsListener);
                setSystemUiVisibility(1280);
                return;
            }
            ViewCompat.setOnApplyWindowInsetsListener(this, null);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        this.mDependencySortedChildren.clear();
        this.mChildDag.clear();
        int childCount = getChildCount();
        for (i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            getResolvedLayoutParams(childAt).findAnchorView(this, childAt);
            this.mChildDag.addNode(childAt);
            for (int i2 = 0; i2 < childCount; i2++) {
                if (i2 != i) {
                    View childAt2 = getChildAt(i2);
                    if (getResolvedLayoutParams(childAt2).dependsOn(this, childAt2, childAt)) {
                        if (!this.mChildDag.contains(childAt2)) {
                            this.mChildDag.addNode(childAt2);
                        }
                        this.mChildDag.addEdge(childAt, childAt2);
                    }
                }
            }
        }
        this.mDependencySortedChildren.addAll(this.mChildDag.getSortedList());
        Collections.reverse(this.mDependencySortedChildren);
        boolean z = false;
        childCount = getChildCount();
        for (i = 0; i < childCount; i++) {
            if (this.mChildDag.hasOutgoingEdges(getChildAt(i))) {
                z = true;
                break;
            }
        }
        if (z != this.mNeedsPreDrawListener) {
            if (z) {
                if (this.mIsAttachedToWindow) {
                    if (this.mOnPreDrawListener == null) {
                        this.mOnPreDrawListener = new OnPreDrawListener();
                    }
                    getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
                }
                this.mNeedsPreDrawListener = true;
            } else {
                if (this.mIsAttachedToWindow && this.mOnPreDrawListener != null) {
                    getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
                }
                this.mNeedsPreDrawListener = false;
            }
        }
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        boolean isRtl = layoutDirection == 1;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthPadding = paddingLeft + paddingRight;
        int heightPadding = paddingTop + paddingBottom;
        int widthUsed = getSuggestedMinimumWidth();
        int heightUsed = getSuggestedMinimumHeight();
        int childState = 0;
        boolean applyInsets = this.mLastInsets != null && ViewCompat.getFitsSystemWindows(this);
        int childCount2 = this.mDependencySortedChildren.size();
        for (int i3 = 0; i3 < childCount2; i3++) {
            View child = (View) this.mDependencySortedChildren.get(i3);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int keylineWidthUsed = 0;
                if (lp.keyline >= 0 && widthMode != 0) {
                    int keylinePos = getKeyline(lp.keyline);
                    int keylineGravity = GravityCompat.getAbsoluteGravity(resolveKeylineGravity(lp.gravity), layoutDirection) & 7;
                    if ((keylineGravity == 3 && !isRtl) || (keylineGravity == 5 && isRtl)) {
                        keylineWidthUsed = Math.max(0, (widthSize - paddingRight) - keylinePos);
                    } else if ((keylineGravity == 5 && !isRtl) || (keylineGravity == 3 && isRtl)) {
                        keylineWidthUsed = Math.max(0, keylinePos - paddingLeft);
                    }
                }
                int childWidthMeasureSpec = widthMeasureSpec;
                int childHeightMeasureSpec = heightMeasureSpec;
                if (applyInsets && !ViewCompat.getFitsSystemWindows(child)) {
                    int vertInsets = this.mLastInsets.getSystemWindowInsetTop() + this.mLastInsets.getSystemWindowInsetBottom();
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize - (this.mLastInsets.getSystemWindowInsetLeft() + this.mLastInsets.getSystemWindowInsetRight()), widthMode);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize - vertInsets, heightMode);
                }
                Behavior b = lp.mBehavior;
                if (b == null || !b.onMeasureChild(this, child, childWidthMeasureSpec, keylineWidthUsed, childHeightMeasureSpec, 0)) {
                    onMeasureChild(child, childWidthMeasureSpec, keylineWidthUsed, childHeightMeasureSpec, 0);
                }
                widthUsed = Math.max(widthUsed, ((child.getMeasuredWidth() + widthPadding) + lp.leftMargin) + lp.rightMargin);
                heightUsed = Math.max(heightUsed, ((child.getMeasuredHeight() + heightPadding) + lp.topMargin) + lp.bottomMargin);
                childState = ViewCompat.combineMeasuredStates(childState, ViewCompat.getMeasuredState(child));
            }
        }
        setMeasuredDimension(ViewCompat.resolveSizeAndState(widthUsed, widthMeasureSpec, -16777216 & childState), ViewCompat.resolveSizeAndState(heightUsed, heightMeasureSpec, childState << 16));
    }

    protected /* bridge */ /* synthetic */ android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }
}
