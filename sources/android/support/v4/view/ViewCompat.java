package android.support.v4.view;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.os.BuildCompat;
import android.support.v4.view.ViewCompatLollipop.OnApplyWindowInsetsListenerBridge;
import android.util.Log;
import android.view.Display;
import android.view.PointerIcon;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.WindowManager;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public final class ViewCompat {
    static final ViewCompatImpl IMPL;

    interface ViewCompatImpl {
        ViewPropertyAnimatorCompat animate(View view);

        boolean canScrollHorizontally(View view, int i);

        boolean canScrollVertically(View view, int i);

        int combineMeasuredStates(int i, int i2);

        WindowInsetsCompat dispatchApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat);

        float getAlpha(View view);

        ColorStateList getBackgroundTintList(View view);

        Mode getBackgroundTintMode(View view);

        Display getDisplay(View view);

        float getElevation(View view);

        boolean getFitsSystemWindows(View view);

        int getImportantForAccessibility(View view);

        int getLayerType(View view);

        int getLayoutDirection(View view);

        Matrix getMatrix(View view);

        int getMeasuredState(View view);

        int getMeasuredWidthAndState(View view);

        int getMinimumHeight(View view);

        int getMinimumWidth(View view);

        int getPaddingEnd(View view);

        int getPaddingStart(View view);

        ViewParent getParentForAccessibility(View view);

        String getTransitionName(View view);

        float getTranslationX(View view);

        float getTranslationY(View view);

        int getWindowSystemUiVisibility(View view);

        float getZ(View view);

        boolean hasAccessibilityDelegate(View view);

        boolean hasOnClickListeners(View view);

        boolean hasOverlappingRendering(View view);

        boolean hasTransientState(View view);

        boolean isAttachedToWindow(View view);

        boolean isLaidOut(View view);

        boolean isNestedScrollingEnabled(View view);

        boolean isPaddingRelative(View view);

        void jumpDrawablesToCurrentState(View view);

        void offsetLeftAndRight(View view, int i);

        void offsetTopAndBottom(View view, int i);

        WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat);

        void postInvalidateOnAnimation(View view);

        void postOnAnimation(View view, Runnable runnable);

        void postOnAnimationDelayed(View view, Runnable runnable, long j);

        void requestApplyInsets(View view);

        int resolveSizeAndState(int i, int i2, int i3);

        void setAccessibilityDelegate(View view, AccessibilityDelegateCompat accessibilityDelegateCompat);

        void setActivated(View view, boolean z);

        void setAlpha(View view, float f);

        void setBackground(View view, Drawable drawable);

        void setBackgroundTintList(View view, ColorStateList colorStateList);

        void setBackgroundTintMode(View view, Mode mode);

        void setChildrenDrawingOrderEnabled(ViewGroup viewGroup, boolean z);

        void setElevation(View view, float f);

        void setFitsSystemWindows(View view, boolean z);

        void setImportantForAccessibility(View view, int i);

        void setLayerType(View view, int i, Paint paint);

        void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener);

        void setPaddingRelative(View view, int i, int i2, int i3, int i4);

        void setPointerIcon(View view, PointerIconCompat pointerIconCompat);

        void setSaveFromParentEnabled(View view, boolean z);

        void setScaleX(View view, float f);

        void setScaleY(View view, float f);

        void setScrollIndicators(View view, int i, int i2);

        void setTranslationX(View view, float f);

        void setTranslationY(View view, float f);

        void stopNestedScroll(View view);
    }

    static class BaseViewCompatImpl implements ViewCompatImpl {
        private static Method sChildrenDrawingOrderMethod;
        WeakHashMap<View, ViewPropertyAnimatorCompat> mViewPropertyAnimatorCompatMap = null;

        BaseViewCompatImpl() {
        }

        public boolean canScrollHorizontally(View v, int direction) {
            if (v instanceof ScrollingView) {
                boolean z;
                ScrollingView scrollingView = (ScrollingView) v;
                int computeHorizontalScrollOffset = scrollingView.computeHorizontalScrollOffset();
                int computeHorizontalScrollRange = scrollingView.computeHorizontalScrollRange() - scrollingView.computeHorizontalScrollExtent();
                if (computeHorizontalScrollRange != 0) {
                    if (direction < 0) {
                        z = computeHorizontalScrollOffset > 0;
                    } else if (computeHorizontalScrollOffset < computeHorizontalScrollRange - 1) {
                        z = true;
                    }
                    if (z) {
                        return true;
                    }
                }
                z = false;
                if (z) {
                    return true;
                }
            }
            return false;
        }

        public boolean canScrollVertically(View v, int direction) {
            if (v instanceof ScrollingView) {
                boolean z;
                ScrollingView scrollingView = (ScrollingView) v;
                int computeVerticalScrollOffset = scrollingView.computeVerticalScrollOffset();
                int computeVerticalScrollRange = scrollingView.computeVerticalScrollRange() - scrollingView.computeVerticalScrollExtent();
                if (computeVerticalScrollRange != 0) {
                    if (direction < 0) {
                        z = computeVerticalScrollOffset > 0;
                    } else if (computeVerticalScrollOffset < computeVerticalScrollRange - 1) {
                        z = true;
                    }
                    if (z) {
                        return true;
                    }
                }
                z = false;
                if (z) {
                    return true;
                }
            }
            return false;
        }

        public void setAccessibilityDelegate(View v, AccessibilityDelegateCompat delegate) {
        }

        public boolean hasAccessibilityDelegate(View v) {
            return false;
        }

        public boolean hasTransientState(View view) {
            return false;
        }

        public void postInvalidateOnAnimation(View view) {
            view.invalidate();
        }

        public void postOnAnimation(View view, Runnable action) {
            view.postDelayed(action, getFrameTime());
        }

        public void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
            view.postDelayed(action, getFrameTime() + delayMillis);
        }

        long getFrameTime() {
            return 10;
        }

        public int getImportantForAccessibility(View view) {
            return 0;
        }

        public void setImportantForAccessibility(View view, int mode) {
        }

        public float getAlpha(View view) {
            return 1.0f;
        }

        public void setLayerType(View view, int layerType, Paint paint) {
        }

        public int getLayerType(View view) {
            return 0;
        }

        public int getLayoutDirection(View view) {
            return 0;
        }

        public ViewParent getParentForAccessibility(View view) {
            return view.getParent();
        }

        public int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
            return View.resolveSize(size, measureSpec);
        }

        public int getMeasuredWidthAndState(View view) {
            return view.getMeasuredWidth();
        }

        public int getMeasuredState(View view) {
            return 0;
        }

        public int getPaddingStart(View view) {
            return view.getPaddingLeft();
        }

        public int getPaddingEnd(View view) {
            return view.getPaddingRight();
        }

        public void setPaddingRelative(View view, int start, int top, int end, int bottom) {
            view.setPadding(start, top, end, bottom);
        }

        public boolean hasOverlappingRendering(View view) {
            return true;
        }

        public float getTranslationX(View view) {
            return 0.0f;
        }

        public float getTranslationY(View view) {
            return 0.0f;
        }

        public Matrix getMatrix(View view) {
            return null;
        }

        public int getMinimumWidth(View view) {
            return ViewCompatBase.getMinimumWidth(view);
        }

        public int getMinimumHeight(View view) {
            return ViewCompatBase.getMinimumHeight(view);
        }

        public ViewPropertyAnimatorCompat animate(View view) {
            return new ViewPropertyAnimatorCompat(view);
        }

        public void setTranslationX(View view, float value) {
        }

        public void setTranslationY(View view, float value) {
        }

        public void setAlpha(View view, float value) {
        }

        public void setScaleX(View view, float value) {
        }

        public void setScaleY(View view, float value) {
        }

        public String getTransitionName(View view) {
            return null;
        }

        public int getWindowSystemUiVisibility(View view) {
            return 0;
        }

        public void requestApplyInsets(View view) {
        }

        public void setElevation(View view, float elevation) {
        }

        public float getElevation(View view) {
            return 0.0f;
        }

        public float getTranslationZ(View view) {
            return 0.0f;
        }

        public final void setChildrenDrawingOrderEnabled(ViewGroup viewGroup, boolean enabled) {
            if (sChildrenDrawingOrderMethod == null) {
                try {
                    sChildrenDrawingOrderMethod = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", new Class[]{Boolean.TYPE});
                } catch (NoSuchMethodException e) {
                    Log.e("ViewCompat", "Unable to find childrenDrawingOrderEnabled", e);
                }
                sChildrenDrawingOrderMethod.setAccessible(true);
            }
            try {
                sChildrenDrawingOrderMethod.invoke(viewGroup, new Object[]{Boolean.valueOf(enabled)});
            } catch (IllegalAccessException e2) {
                Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", e2);
            } catch (IllegalArgumentException e3) {
                Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", e3);
            } catch (InvocationTargetException e4) {
                Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", e4);
            }
        }

        public boolean getFitsSystemWindows(View view) {
            return false;
        }

        public void setFitsSystemWindows(View view, boolean fitSystemWindows) {
        }

        public void jumpDrawablesToCurrentState(View view) {
        }

        public void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener listener) {
        }

        public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            return insets;
        }

        public WindowInsetsCompat dispatchApplyWindowInsets(View v, WindowInsetsCompat insets) {
            return insets;
        }

        public void setSaveFromParentEnabled(View v, boolean enabled) {
        }

        public void setActivated(View view, boolean activated) {
        }

        public boolean isPaddingRelative(View view) {
            return false;
        }

        public boolean isNestedScrollingEnabled(View view) {
            if (view instanceof NestedScrollingChild) {
                return ((NestedScrollingChild) view).isNestedScrollingEnabled();
            }
            return false;
        }

        public void setBackground(View view, Drawable background) {
            view.setBackgroundDrawable(background);
        }

        public void stopNestedScroll(View view) {
            if (view instanceof NestedScrollingChild) {
                ((NestedScrollingChild) view).stopNestedScroll();
            }
        }

        public int combineMeasuredStates(int curState, int newState) {
            return curState | newState;
        }

        public float getZ(View view) {
            return getTranslationZ(view) + getElevation(view);
        }

        public boolean isAttachedToWindow(View view) {
            return ViewCompatBase.isAttachedToWindow(view);
        }

        public boolean hasOnClickListeners(View view) {
            return false;
        }

        public void setScrollIndicators(View view, int indicators, int mask) {
        }

        public void setPointerIcon(View view, PointerIconCompat pointerIcon) {
        }

        public ColorStateList getBackgroundTintList(View view) {
            return view instanceof TintableBackgroundView ? ((TintableBackgroundView) view).getSupportBackgroundTintList() : null;
        }

        public void setBackgroundTintList(View view, ColorStateList tintList) {
            if (view instanceof TintableBackgroundView) {
                ((TintableBackgroundView) view).setSupportBackgroundTintList(tintList);
            }
        }

        public void setBackgroundTintMode(View view, Mode mode) {
            if (view instanceof TintableBackgroundView) {
                ((TintableBackgroundView) view).setSupportBackgroundTintMode(mode);
            }
        }

        public Mode getBackgroundTintMode(View view) {
            return view instanceof TintableBackgroundView ? ((TintableBackgroundView) view).getSupportBackgroundTintMode() : null;
        }

        public boolean isLaidOut(View view) {
            return view.getWidth() > 0 && view.getHeight() > 0;
        }

        public void offsetLeftAndRight(View view, int offset) {
            int left = view.getLeft();
            view.offsetLeftAndRight(offset);
            if (offset != 0) {
                ViewParent parent = view.getParent();
                if (parent instanceof View) {
                    int abs = Math.abs(offset);
                    ((View) parent).invalidate(left - abs, view.getTop(), (left + view.getWidth()) + abs, view.getBottom());
                    return;
                }
                view.invalidate();
            }
        }

        public void offsetTopAndBottom(View view, int offset) {
            int top = view.getTop();
            view.offsetTopAndBottom(offset);
            if (offset != 0) {
                ViewParent parent = view.getParent();
                if (parent instanceof View) {
                    int abs = Math.abs(offset);
                    ((View) parent).invalidate(view.getLeft(), top - abs, view.getRight(), (top + view.getHeight()) + abs);
                    return;
                }
                view.invalidate();
            }
        }

        public Display getDisplay(View view) {
            if (ViewCompatBase.isAttachedToWindow(view)) {
                return ((WindowManager) view.getContext().getSystemService("window")).getDefaultDisplay();
            }
            return null;
        }
    }

    static class HCViewCompatImpl extends BaseViewCompatImpl {
        HCViewCompatImpl() {
        }

        public void offsetLeftAndRight(View view, int offset) {
            ViewCompatHC.offsetLeftAndRight(view, offset);
        }

        public void offsetTopAndBottom(View view, int offset) {
            ViewCompatHC.offsetTopAndBottom(view, offset);
        }

        final long getFrameTime() {
            return ValueAnimator.getFrameDelay();
        }

        public final float getAlpha(View view) {
            return view.getAlpha();
        }

        public final void setLayerType(View view, int layerType, Paint paint) {
            view.setLayerType(layerType, paint);
        }

        public final int getLayerType(View view) {
            return view.getLayerType();
        }

        public final int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
            return View.resolveSizeAndState(size, measureSpec, childMeasuredState);
        }

        public final int getMeasuredWidthAndState(View view) {
            return view.getMeasuredWidthAndState();
        }

        public final int getMeasuredState(View view) {
            return view.getMeasuredState();
        }

        public final float getTranslationX(View view) {
            return view.getTranslationX();
        }

        public final float getTranslationY(View view) {
            return view.getTranslationY();
        }

        public final Matrix getMatrix(View view) {
            return view.getMatrix();
        }

        public final void setTranslationX(View view, float value) {
            view.setTranslationX(value);
        }

        public final void setTranslationY(View view, float value) {
            view.setTranslationY(value);
        }

        public final void setAlpha(View view, float value) {
            view.setAlpha(value);
        }

        public final void setScaleX(View view, float value) {
            view.setScaleX(value);
        }

        public final void setScaleY(View view, float value) {
            view.setScaleY(value);
        }

        public final void jumpDrawablesToCurrentState(View view) {
            view.jumpDrawablesToCurrentState();
        }

        public final void setSaveFromParentEnabled(View view, boolean enabled) {
            view.setSaveFromParentEnabled(enabled);
        }

        public final void setActivated(View view, boolean activated) {
            view.setActivated(activated);
        }

        public final int combineMeasuredStates(int curState, int newState) {
            return View.combineMeasuredStates(curState, newState);
        }
    }

    static class ICSViewCompatImpl extends HCViewCompatImpl {
        static boolean accessibilityDelegateCheckFailed = false;
        static Field mAccessibilityDelegateField;

        ICSViewCompatImpl() {
        }

        public final void setAccessibilityDelegate(View v, AccessibilityDelegateCompat delegate) {
            Object obj;
            if (delegate == null) {
                obj = null;
            } else {
                obj = delegate.getBridge();
            }
            v.setAccessibilityDelegate((AccessibilityDelegate) obj);
        }

        public final boolean hasAccessibilityDelegate(View v) {
            if (accessibilityDelegateCheckFailed) {
                return false;
            }
            if (mAccessibilityDelegateField == null) {
                try {
                    Field declaredField = View.class.getDeclaredField("mAccessibilityDelegate");
                    mAccessibilityDelegateField = declaredField;
                    declaredField.setAccessible(true);
                } catch (Throwable th) {
                    accessibilityDelegateCheckFailed = true;
                    return false;
                }
            }
            try {
                if (mAccessibilityDelegateField.get(v) != null) {
                    return true;
                }
                return false;
            } catch (Throwable th2) {
                accessibilityDelegateCheckFailed = true;
                return false;
            }
        }

        public final ViewPropertyAnimatorCompat animate(View view) {
            if (this.mViewPropertyAnimatorCompatMap == null) {
                this.mViewPropertyAnimatorCompatMap = new WeakHashMap();
            }
            ViewPropertyAnimatorCompat vpa = (ViewPropertyAnimatorCompat) this.mViewPropertyAnimatorCompatMap.get(view);
            if (vpa != null) {
                return vpa;
            }
            vpa = new ViewPropertyAnimatorCompat(view);
            this.mViewPropertyAnimatorCompatMap.put(view, vpa);
            return vpa;
        }

        public final boolean canScrollHorizontally(View v, int direction) {
            return v.canScrollHorizontally(direction);
        }

        public final boolean canScrollVertically(View v, int direction) {
            return v.canScrollVertically(direction);
        }

        public final void setFitsSystemWindows(View view, boolean fitSystemWindows) {
            view.setFitsSystemWindows(fitSystemWindows);
        }
    }

    static class ICSMr1ViewCompatImpl extends ICSViewCompatImpl {
        ICSMr1ViewCompatImpl() {
        }

        public final boolean hasOnClickListeners(View view) {
            return view.hasOnClickListeners();
        }
    }

    static class JBViewCompatImpl extends ICSMr1ViewCompatImpl {
        JBViewCompatImpl() {
        }

        public void setImportantForAccessibility(View view, int mode) {
            if (mode == 4) {
                mode = 2;
            }
            view.setImportantForAccessibility(mode);
        }

        public final boolean hasTransientState(View view) {
            return view.hasTransientState();
        }

        public final void postInvalidateOnAnimation(View view) {
            view.postInvalidateOnAnimation();
        }

        public final void postOnAnimation(View view, Runnable action) {
            view.postOnAnimation(action);
        }

        public final void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
            view.postOnAnimationDelayed(action, delayMillis);
        }

        public final int getImportantForAccessibility(View view) {
            return view.getImportantForAccessibility();
        }

        public final ViewParent getParentForAccessibility(View view) {
            return view.getParentForAccessibility();
        }

        public final int getMinimumWidth(View view) {
            return view.getMinimumWidth();
        }

        public final int getMinimumHeight(View view) {
            return view.getMinimumHeight();
        }

        public void requestApplyInsets(View view) {
            view.requestFitSystemWindows();
        }

        public final boolean getFitsSystemWindows(View view) {
            return view.getFitsSystemWindows();
        }

        public final boolean hasOverlappingRendering(View view) {
            return view.hasOverlappingRendering();
        }

        public final void setBackground(View view, Drawable background) {
            view.setBackground(background);
        }
    }

    static class JbMr1ViewCompatImpl extends JBViewCompatImpl {
        JbMr1ViewCompatImpl() {
        }

        public final int getLayoutDirection(View view) {
            return view.getLayoutDirection();
        }

        public final int getPaddingStart(View view) {
            return view.getPaddingStart();
        }

        public final int getPaddingEnd(View view) {
            return view.getPaddingEnd();
        }

        public final void setPaddingRelative(View view, int start, int top, int end, int bottom) {
            view.setPaddingRelative(start, top, end, bottom);
        }

        public final int getWindowSystemUiVisibility(View view) {
            return view.getWindowSystemUiVisibility();
        }

        public final boolean isPaddingRelative(View view) {
            return view.isPaddingRelative();
        }

        public final Display getDisplay(View view) {
            return view.getDisplay();
        }
    }

    static class JbMr2ViewCompatImpl extends JbMr1ViewCompatImpl {
        JbMr2ViewCompatImpl() {
        }
    }

    static class KitKatViewCompatImpl extends JbMr2ViewCompatImpl {
        KitKatViewCompatImpl() {
        }

        public final void setImportantForAccessibility(View view, int mode) {
            view.setImportantForAccessibility(mode);
        }

        public final boolean isLaidOut(View view) {
            return view.isLaidOut();
        }

        public final boolean isAttachedToWindow(View view) {
            return view.isAttachedToWindow();
        }
    }

    static class LollipopViewCompatImpl extends KitKatViewCompatImpl {
        LollipopViewCompatImpl() {
        }

        public final void setOnApplyWindowInsetsListener(View view, final OnApplyWindowInsetsListener listener) {
            if (listener == null) {
                ViewCompatLollipop.setOnApplyWindowInsetsListener(view, null);
            } else {
                ViewCompatLollipop.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListenerBridge() {
                    public final Object onApplyWindowInsets(View v, Object insets) {
                        return WindowInsetsCompat.unwrap(listener.onApplyWindowInsets(v, WindowInsetsCompat.wrap(insets)));
                    }
                });
            }
        }

        public final WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            Object unwrap = WindowInsetsCompat.unwrap(insets);
            WindowInsets windowInsets = (WindowInsets) unwrap;
            WindowInsets onApplyWindowInsets = v.onApplyWindowInsets(windowInsets);
            if (onApplyWindowInsets != windowInsets) {
                unwrap = new WindowInsets(onApplyWindowInsets);
            }
            return WindowInsetsCompat.wrap(unwrap);
        }

        public final WindowInsetsCompat dispatchApplyWindowInsets(View v, WindowInsetsCompat insets) {
            Object unwrap = WindowInsetsCompat.unwrap(insets);
            WindowInsets windowInsets = (WindowInsets) unwrap;
            WindowInsets dispatchApplyWindowInsets = v.dispatchApplyWindowInsets(windowInsets);
            if (dispatchApplyWindowInsets != windowInsets) {
                unwrap = new WindowInsets(dispatchApplyWindowInsets);
            }
            return WindowInsetsCompat.wrap(unwrap);
        }

        public void offsetLeftAndRight(View view, int offset) {
            ViewCompatLollipop.offsetLeftAndRight(view, offset);
        }

        public void offsetTopAndBottom(View view, int offset) {
            ViewCompatLollipop.offsetTopAndBottom(view, offset);
        }

        public final String getTransitionName(View view) {
            return view.getTransitionName();
        }

        public final void requestApplyInsets(View view) {
            view.requestApplyInsets();
        }

        public final void setElevation(View view, float elevation) {
            view.setElevation(elevation);
        }

        public final float getElevation(View view) {
            return view.getElevation();
        }

        public final float getTranslationZ(View view) {
            return view.getTranslationZ();
        }

        public final boolean isNestedScrollingEnabled(View view) {
            return view.isNestedScrollingEnabled();
        }

        public final void stopNestedScroll(View view) {
            view.stopNestedScroll();
        }

        public final ColorStateList getBackgroundTintList(View view) {
            return view.getBackgroundTintList();
        }

        public final void setBackgroundTintList(View view, ColorStateList tintList) {
            view.setBackgroundTintList(tintList);
            if (VERSION.SDK_INT == 21) {
                Drawable background = view.getBackground();
                Object obj = (view.getBackgroundTintList() == null || view.getBackgroundTintMode() == null) ? null : 1;
                if (background != null && obj != null) {
                    if (background.isStateful()) {
                        background.setState(view.getDrawableState());
                    }
                    view.setBackground(background);
                }
            }
        }

        public final void setBackgroundTintMode(View view, Mode mode) {
            view.setBackgroundTintMode(mode);
            if (VERSION.SDK_INT == 21) {
                Drawable background = view.getBackground();
                Object obj = (view.getBackgroundTintList() == null || view.getBackgroundTintMode() == null) ? null : 1;
                if (background != null && obj != null) {
                    if (background.isStateful()) {
                        background.setState(view.getDrawableState());
                    }
                    view.setBackground(background);
                }
            }
        }

        public final Mode getBackgroundTintMode(View view) {
            return view.getBackgroundTintMode();
        }

        public final float getZ(View view) {
            return view.getZ();
        }
    }

    static class MarshmallowViewCompatImpl extends LollipopViewCompatImpl {
        MarshmallowViewCompatImpl() {
        }

        public final void setScrollIndicators(View view, int indicators, int mask) {
            view.setScrollIndicators(indicators, mask);
        }

        public final void offsetLeftAndRight(View view, int offset) {
            view.offsetLeftAndRight(offset);
        }

        public final void offsetTopAndBottom(View view, int offset) {
            view.offsetTopAndBottom(offset);
        }
    }

    static class Api24ViewCompatImpl extends MarshmallowViewCompatImpl {
        Api24ViewCompatImpl() {
        }

        public final void setPointerIcon(View view, PointerIconCompat pointerIconCompat) {
            view.setPointerIcon((PointerIcon) (pointerIconCompat != null ? pointerIconCompat.getPointerIcon() : null));
        }
    }

    static {
        int version = VERSION.SDK_INT;
        if (BuildCompat.isAtLeastN()) {
            IMPL = new Api24ViewCompatImpl();
        } else if (version >= 23) {
            IMPL = new MarshmallowViewCompatImpl();
        } else if (version >= 21) {
            IMPL = new LollipopViewCompatImpl();
        } else if (version >= 19) {
            IMPL = new KitKatViewCompatImpl();
        } else if (version >= 18) {
            IMPL = new JbMr2ViewCompatImpl();
        } else if (version >= 17) {
            IMPL = new JbMr1ViewCompatImpl();
        } else if (version >= 16) {
            IMPL = new JBViewCompatImpl();
        } else if (version >= 15) {
            IMPL = new ICSMr1ViewCompatImpl();
        } else if (version >= 14) {
            IMPL = new ICSViewCompatImpl();
        } else if (version >= 11) {
            IMPL = new HCViewCompatImpl();
        } else {
            IMPL = new BaseViewCompatImpl();
        }
    }

    public static boolean canScrollHorizontally(View v, int direction) {
        return IMPL.canScrollHorizontally(v, direction);
    }

    public static boolean canScrollVertically(View v, int direction) {
        return IMPL.canScrollVertically(v, direction);
    }

    public static void setAccessibilityDelegate(View v, AccessibilityDelegateCompat delegate) {
        IMPL.setAccessibilityDelegate(v, delegate);
    }

    public static boolean hasAccessibilityDelegate(View v) {
        return IMPL.hasAccessibilityDelegate(v);
    }

    public static boolean hasTransientState(View view) {
        return IMPL.hasTransientState(view);
    }

    public static void postInvalidateOnAnimation(View view) {
        IMPL.postInvalidateOnAnimation(view);
    }

    public static void postOnAnimation(View view, Runnable action) {
        IMPL.postOnAnimation(view, action);
    }

    public static void postOnAnimationDelayed(View view, Runnable action, long delayMillis) {
        IMPL.postOnAnimationDelayed(view, action, delayMillis);
    }

    public static int getImportantForAccessibility(View view) {
        return IMPL.getImportantForAccessibility(view);
    }

    public static void setImportantForAccessibility(View view, int mode) {
        IMPL.setImportantForAccessibility(view, mode);
    }

    public static float getAlpha(View view) {
        return IMPL.getAlpha(view);
    }

    public static void setLayerType(View view, int layerType, Paint paint) {
        IMPL.setLayerType(view, layerType, paint);
    }

    public static int getLayerType(View view) {
        return IMPL.getLayerType(view);
    }

    public static int getLayoutDirection(View view) {
        return IMPL.getLayoutDirection(view);
    }

    public static ViewParent getParentForAccessibility(View view) {
        return IMPL.getParentForAccessibility(view);
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        return IMPL.resolveSizeAndState(size, measureSpec, childMeasuredState);
    }

    public static int getMeasuredWidthAndState(View view) {
        return IMPL.getMeasuredWidthAndState(view);
    }

    public static int getMeasuredState(View view) {
        return IMPL.getMeasuredState(view);
    }

    public static int combineMeasuredStates(int curState, int newState) {
        return IMPL.combineMeasuredStates(curState, newState);
    }

    public static int getPaddingStart(View view) {
        return IMPL.getPaddingStart(view);
    }

    public static int getPaddingEnd(View view) {
        return IMPL.getPaddingEnd(view);
    }

    public static void setPaddingRelative(View view, int start, int top, int end, int bottom) {
        IMPL.setPaddingRelative(view, start, top, end, bottom);
    }

    public static float getTranslationX(View view) {
        return IMPL.getTranslationX(view);
    }

    public static float getTranslationY(View view) {
        return IMPL.getTranslationY(view);
    }

    public static Matrix getMatrix(View view) {
        return IMPL.getMatrix(view);
    }

    public static int getMinimumWidth(View view) {
        return IMPL.getMinimumWidth(view);
    }

    public static int getMinimumHeight(View view) {
        return IMPL.getMinimumHeight(view);
    }

    public static ViewPropertyAnimatorCompat animate(View view) {
        return IMPL.animate(view);
    }

    public static void setTranslationX(View view, float value) {
        IMPL.setTranslationX(view, value);
    }

    public static void setTranslationY(View view, float value) {
        IMPL.setTranslationY(view, value);
    }

    public static void setAlpha(View view, float value) {
        IMPL.setAlpha(view, value);
    }

    public static void setScaleX(View view, float value) {
        IMPL.setScaleX(view, value);
    }

    public static void setScaleY(View view, float value) {
        IMPL.setScaleY(view, value);
    }

    public static void setElevation(View view, float elevation) {
        IMPL.setElevation(view, elevation);
    }

    public static float getElevation(View view) {
        return IMPL.getElevation(view);
    }

    public static String getTransitionName(View view) {
        return IMPL.getTransitionName(view);
    }

    public static int getWindowSystemUiVisibility(View view) {
        return IMPL.getWindowSystemUiVisibility(view);
    }

    public static void requestApplyInsets(View view) {
        IMPL.requestApplyInsets(view);
    }

    public static void setChildrenDrawingOrderEnabled(ViewGroup viewGroup, boolean enabled) {
        IMPL.setChildrenDrawingOrderEnabled(viewGroup, true);
    }

    public static boolean getFitsSystemWindows(View v) {
        return IMPL.getFitsSystemWindows(v);
    }

    public static void setFitsSystemWindows(View view, boolean fitSystemWindows) {
        IMPL.setFitsSystemWindows(view, true);
    }

    public static void jumpDrawablesToCurrentState(View v) {
        IMPL.jumpDrawablesToCurrentState(v);
    }

    public static void setOnApplyWindowInsetsListener(View v, OnApplyWindowInsetsListener listener) {
        IMPL.setOnApplyWindowInsetsListener(v, listener);
    }

    public static WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
        return IMPL.onApplyWindowInsets(view, insets);
    }

    public static WindowInsetsCompat dispatchApplyWindowInsets(View view, WindowInsetsCompat insets) {
        return IMPL.dispatchApplyWindowInsets(view, insets);
    }

    public static void setSaveFromParentEnabled(View v, boolean enabled) {
        IMPL.setSaveFromParentEnabled(v, false);
    }

    public static void setActivated(View view, boolean activated) {
        IMPL.setActivated(view, activated);
    }

    public static boolean hasOverlappingRendering(View view) {
        return IMPL.hasOverlappingRendering(view);
    }

    public static boolean isPaddingRelative(View view) {
        return IMPL.isPaddingRelative(view);
    }

    public static void setBackground(View view, Drawable background) {
        IMPL.setBackground(view, background);
    }

    public static ColorStateList getBackgroundTintList(View view) {
        return IMPL.getBackgroundTintList(view);
    }

    public static void setBackgroundTintList(View view, ColorStateList tintList) {
        IMPL.setBackgroundTintList(view, tintList);
    }

    public static Mode getBackgroundTintMode(View view) {
        return IMPL.getBackgroundTintMode(view);
    }

    public static void setBackgroundTintMode(View view, Mode mode) {
        IMPL.setBackgroundTintMode(view, mode);
    }

    public static boolean isNestedScrollingEnabled(View view) {
        return IMPL.isNestedScrollingEnabled(view);
    }

    public static void stopNestedScroll(View view) {
        IMPL.stopNestedScroll(view);
    }

    public static boolean isLaidOut(View view) {
        return IMPL.isLaidOut(view);
    }

    public static float getZ(View view) {
        return IMPL.getZ(view);
    }

    public static void offsetTopAndBottom(View view, int offset) {
        IMPL.offsetTopAndBottom(view, offset);
    }

    public static void offsetLeftAndRight(View view, int offset) {
        IMPL.offsetLeftAndRight(view, offset);
    }

    public static boolean isAttachedToWindow(View view) {
        return IMPL.isAttachedToWindow(view);
    }

    public static boolean hasOnClickListeners(View view) {
        return IMPL.hasOnClickListeners(view);
    }

    public static void setScrollIndicators(View view, int indicators, int mask) {
        IMPL.setScrollIndicators(view, indicators, mask);
    }

    public static void setPointerIcon(View view, PointerIconCompat pointerIcon) {
        IMPL.setPointerIcon(view, pointerIcon);
    }

    public static Display getDisplay(View view) {
        return IMPL.getDisplay(view);
    }
}
