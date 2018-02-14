package android.support.v4.view;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Build.VERSION;
import android.support.v4.view.ViewPropertyAnimatorCompatICS.AnonymousClass1;
import android.view.View;
import android.view.animation.Interpolator;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public final class ViewPropertyAnimatorCompat {
    static final ViewPropertyAnimatorCompatImpl IMPL;
    Runnable mEndAction = null;
    int mOldLayerType = -1;
    Runnable mStartAction = null;
    private WeakReference<View> mView;

    interface ViewPropertyAnimatorCompatImpl {
        void alpha(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view, float f);

        void cancel(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view);

        long getDuration$66604b42(View view);

        void setDuration$65a8a4c6(View view, long j);

        void setInterpolator$4b3df29b(View view, Interpolator interpolator);

        void setListener(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view, ViewPropertyAnimatorListener viewPropertyAnimatorListener);

        void setStartDelay$65a8a4c6(View view, long j);

        void setUpdateListener$587f161e(View view, ViewPropertyAnimatorUpdateListener viewPropertyAnimatorUpdateListener);

        void start(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view);

        void translationX(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view, float f);

        void translationY(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat, View view, float f);
    }

    static class BaseViewPropertyAnimatorCompatImpl implements ViewPropertyAnimatorCompatImpl {
        WeakHashMap<View, Runnable> mStarterMap = null;

        class Starter implements Runnable {
            WeakReference<View> mViewRef;
            ViewPropertyAnimatorCompat mVpa;

            Starter(ViewPropertyAnimatorCompat vpa, View view) {
                this.mViewRef = new WeakReference(view);
                this.mVpa = vpa;
            }

            public final void run() {
                View view = (View) this.mViewRef.get();
                if (view != null) {
                    BaseViewPropertyAnimatorCompatImpl.this.startAnimation(this.mVpa, view);
                }
            }
        }

        BaseViewPropertyAnimatorCompatImpl() {
        }

        public void setDuration$65a8a4c6(View view, long value) {
        }

        public void alpha(ViewPropertyAnimatorCompat vpa, View view, float value) {
            postStartMessage(vpa, view);
        }

        public void translationX(ViewPropertyAnimatorCompat vpa, View view, float value) {
            postStartMessage(vpa, view);
        }

        public void translationY(ViewPropertyAnimatorCompat vpa, View view, float value) {
            postStartMessage(vpa, view);
        }

        public long getDuration$66604b42(View view) {
            return 0;
        }

        public void setInterpolator$4b3df29b(View view, Interpolator value) {
        }

        public void setStartDelay$65a8a4c6(View view, long value) {
        }

        public void cancel(ViewPropertyAnimatorCompat vpa, View view) {
            postStartMessage(vpa, view);
        }

        public void setListener(ViewPropertyAnimatorCompat vpa, View view, ViewPropertyAnimatorListener listener) {
            view.setTag(2113929216, listener);
        }

        public void setUpdateListener$587f161e(View view, ViewPropertyAnimatorUpdateListener listener) {
        }

        final void startAnimation(ViewPropertyAnimatorCompat vpa, View view) {
            ViewPropertyAnimatorListener listenerTag = view.getTag(2113929216);
            ViewPropertyAnimatorListener listener = null;
            if (listenerTag instanceof ViewPropertyAnimatorListener) {
                listener = listenerTag;
            }
            Runnable runnable = vpa.mStartAction;
            runnable = vpa.mEndAction;
            vpa.mStartAction = null;
            vpa.mEndAction = null;
            if (listener != null) {
                listener.onAnimationStart(view);
                listener.onAnimationEnd(view);
            }
            if (this.mStarterMap != null) {
                this.mStarterMap.remove(view);
            }
        }

        private void postStartMessage(ViewPropertyAnimatorCompat vpa, View view) {
            Runnable runnable = null;
            if (this.mStarterMap != null) {
                runnable = (Runnable) this.mStarterMap.get(view);
            }
            if (runnable == null) {
                runnable = new Starter(vpa, view);
                if (this.mStarterMap == null) {
                    this.mStarterMap = new WeakHashMap();
                }
                this.mStarterMap.put(view, runnable);
            }
            view.removeCallbacks(runnable);
            view.post(runnable);
        }

        public void start(ViewPropertyAnimatorCompat vpa, View view) {
            if (this.mStarterMap != null) {
                Runnable runnable = (Runnable) this.mStarterMap.get(view);
                if (runnable != null) {
                    view.removeCallbacks(runnable);
                }
            }
            startAnimation(vpa, view);
        }
    }

    static class ICSViewPropertyAnimatorCompatImpl extends BaseViewPropertyAnimatorCompatImpl {
        WeakHashMap<View, Integer> mLayerMap = null;

        static class MyVpaListener implements ViewPropertyAnimatorListener {
            boolean mAnimEndCalled;
            ViewPropertyAnimatorCompat mVpa;

            MyVpaListener(ViewPropertyAnimatorCompat vpa) {
                this.mVpa = vpa;
            }

            public final void onAnimationStart(View view) {
                this.mAnimEndCalled = false;
                if (this.mVpa.mOldLayerType >= 0) {
                    ViewCompat.setLayerType(view, 2, null);
                }
                Runnable runnable = this.mVpa.mStartAction;
                ViewPropertyAnimatorListener listenerTag = view.getTag(2113929216);
                ViewPropertyAnimatorListener listener = null;
                if (listenerTag instanceof ViewPropertyAnimatorListener) {
                    listener = listenerTag;
                }
                if (listener != null) {
                    listener.onAnimationStart(view);
                }
            }

            public final void onAnimationEnd(View view) {
                if (this.mVpa.mOldLayerType >= 0) {
                    ViewCompat.setLayerType(view, this.mVpa.mOldLayerType, null);
                    this.mVpa.mOldLayerType = -1;
                }
                if (VERSION.SDK_INT >= 16 || !this.mAnimEndCalled) {
                    Runnable runnable = this.mVpa.mEndAction;
                    ViewPropertyAnimatorListener listenerTag = view.getTag(2113929216);
                    ViewPropertyAnimatorListener listener = null;
                    if (listenerTag instanceof ViewPropertyAnimatorListener) {
                        listener = listenerTag;
                    }
                    if (listener != null) {
                        listener.onAnimationEnd(view);
                    }
                    this.mAnimEndCalled = true;
                }
            }

            public final void onAnimationCancel(View view) {
                ViewPropertyAnimatorListener listenerTag = view.getTag(2113929216);
                ViewPropertyAnimatorListener listener = null;
                if (listenerTag instanceof ViewPropertyAnimatorListener) {
                    listener = listenerTag;
                }
                if (listener != null) {
                    listener.onAnimationCancel(view);
                }
            }
        }

        ICSViewPropertyAnimatorCompatImpl() {
        }

        public void setListener(ViewPropertyAnimatorCompat vpa, View view, ViewPropertyAnimatorListener listener) {
            view.setTag(2113929216, listener);
            view.animate().setListener(new AnonymousClass1(new MyVpaListener(vpa), view));
        }

        public final void setDuration$65a8a4c6(View view, long value) {
            view.animate().setDuration(value);
        }

        public final void alpha(ViewPropertyAnimatorCompat vpa, View view, float value) {
            view.animate().alpha(value);
        }

        public final void translationX(ViewPropertyAnimatorCompat vpa, View view, float value) {
            view.animate().translationX(value);
        }

        public final void translationY(ViewPropertyAnimatorCompat vpa, View view, float value) {
            view.animate().translationY(value);
        }

        public final long getDuration$66604b42(View view) {
            return view.animate().getDuration();
        }

        public final void setInterpolator$4b3df29b(View view, Interpolator value) {
            view.animate().setInterpolator(value);
        }

        public final void setStartDelay$65a8a4c6(View view, long value) {
            view.animate().setStartDelay(value);
        }

        public final void cancel(ViewPropertyAnimatorCompat vpa, View view) {
            view.animate().cancel();
        }

        public final void start(ViewPropertyAnimatorCompat vpa, View view) {
            view.animate().start();
        }
    }

    static class JBViewPropertyAnimatorCompatImpl extends ICSViewPropertyAnimatorCompatImpl {
        JBViewPropertyAnimatorCompatImpl() {
        }

        public final void setListener(ViewPropertyAnimatorCompat vpa, View view, ViewPropertyAnimatorListener listener) {
            if (listener != null) {
                view.animate().setListener(new ViewPropertyAnimatorCompatJB.AnonymousClass1(listener, view));
            } else {
                view.animate().setListener(null);
            }
        }
    }

    static class JBMr2ViewPropertyAnimatorCompatImpl extends JBViewPropertyAnimatorCompatImpl {
        JBMr2ViewPropertyAnimatorCompatImpl() {
        }
    }

    static class KitKatViewPropertyAnimatorCompatImpl extends JBMr2ViewPropertyAnimatorCompatImpl {
        KitKatViewPropertyAnimatorCompatImpl() {
        }

        public final void setUpdateListener$587f161e(View view, ViewPropertyAnimatorUpdateListener listener) {
            AnimatorUpdateListener animatorUpdateListener = null;
            if (listener != null) {
                animatorUpdateListener = new ViewPropertyAnimatorCompatKK.AnonymousClass1(listener, view);
            }
            view.animate().setUpdateListener(animatorUpdateListener);
        }
    }

    static class LollipopViewPropertyAnimatorCompatImpl extends KitKatViewPropertyAnimatorCompatImpl {
        LollipopViewPropertyAnimatorCompatImpl() {
        }
    }

    ViewPropertyAnimatorCompat(View view) {
        this.mView = new WeakReference(view);
    }

    static {
        int version = VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LollipopViewPropertyAnimatorCompatImpl();
        } else if (version >= 19) {
            IMPL = new KitKatViewPropertyAnimatorCompatImpl();
        } else if (version >= 18) {
            IMPL = new JBMr2ViewPropertyAnimatorCompatImpl();
        } else if (version >= 16) {
            IMPL = new JBViewPropertyAnimatorCompatImpl();
        } else if (version >= 14) {
            IMPL = new ICSViewPropertyAnimatorCompatImpl();
        } else {
            IMPL = new BaseViewPropertyAnimatorCompatImpl();
        }
    }

    public final ViewPropertyAnimatorCompat setDuration(long value) {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.setDuration$65a8a4c6(view, value);
        }
        return this;
    }

    public final ViewPropertyAnimatorCompat alpha(float value) {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.alpha(this, view, value);
        }
        return this;
    }

    public final ViewPropertyAnimatorCompat translationX(float value) {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.translationX(this, view, value);
        }
        return this;
    }

    public final ViewPropertyAnimatorCompat translationY(float value) {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.translationY(this, view, value);
        }
        return this;
    }

    public final long getDuration() {
        View view = (View) this.mView.get();
        if (view != null) {
            return IMPL.getDuration$66604b42(view);
        }
        return 0;
    }

    public final ViewPropertyAnimatorCompat setInterpolator(Interpolator value) {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.setInterpolator$4b3df29b(view, value);
        }
        return this;
    }

    public final ViewPropertyAnimatorCompat setStartDelay(long value) {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.setStartDelay$65a8a4c6(view, value);
        }
        return this;
    }

    public final void cancel() {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.cancel(this, view);
        }
    }

    public final void start() {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.start(this, view);
        }
    }

    public final ViewPropertyAnimatorCompat setListener(ViewPropertyAnimatorListener listener) {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.setListener(this, view, listener);
        }
        return this;
    }

    public final ViewPropertyAnimatorCompat setUpdateListener(ViewPropertyAnimatorUpdateListener listener) {
        View view = (View) this.mView.get();
        if (view != null) {
            IMPL.setUpdateListener$587f161e(view, listener);
        }
        return this;
    }
}
