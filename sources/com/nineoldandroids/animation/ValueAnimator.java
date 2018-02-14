package com.nineoldandroids.animation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AndroidRuntimeException;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.HashMap;

public final class ValueAnimator extends Animator {
    private static ThreadLocal<AnimationHandler> sAnimationHandler = new ThreadLocal();
    private static final ThreadLocal<ArrayList<ValueAnimator>> sAnimations = new ThreadLocal<ArrayList<ValueAnimator>>() {
        protected final /* bridge */ /* synthetic */ Object initialValue() {
            return new ArrayList();
        }
    };
    private static final Interpolator sDefaultInterpolator = new AccelerateDecelerateInterpolator();
    private static final ThreadLocal<ArrayList<ValueAnimator>> sDelayedAnims = new ThreadLocal<ArrayList<ValueAnimator>>() {
        protected final /* bridge */ /* synthetic */ Object initialValue() {
            return new ArrayList();
        }
    };
    private static final ThreadLocal<ArrayList<ValueAnimator>> sEndingAnims = new ThreadLocal<ArrayList<ValueAnimator>>() {
        protected final /* bridge */ /* synthetic */ Object initialValue() {
            return new ArrayList();
        }
    };
    private static final TypeEvaluator sFloatEvaluator = new FloatEvaluator();
    private static long sFrameDelay = 10;
    private static final TypeEvaluator sIntEvaluator = new IntEvaluator();
    private static final ThreadLocal<ArrayList<ValueAnimator>> sPendingAnimations = new ThreadLocal<ArrayList<ValueAnimator>>() {
        protected final /* bridge */ /* synthetic */ Object initialValue() {
            return new ArrayList();
        }
    };
    private static final ThreadLocal<ArrayList<ValueAnimator>> sReadyAnims = new ThreadLocal<ArrayList<ValueAnimator>>() {
        protected final /* bridge */ /* synthetic */ Object initialValue() {
            return new ArrayList();
        }
    };
    private float mCurrentFraction = 0.0f;
    private int mCurrentIteration = 0;
    private long mDelayStartTime;
    private long mDuration = 300;
    boolean mInitialized = false;
    private Interpolator mInterpolator = sDefaultInterpolator;
    private boolean mPlayingBackwards = false;
    int mPlayingState = 0;
    private int mRepeatCount = 0;
    private int mRepeatMode = 1;
    private boolean mRunning = false;
    long mSeekTime = -1;
    private long mStartDelay = 0;
    long mStartTime;
    private boolean mStarted = false;
    private boolean mStartedDelay = false;
    private ArrayList<AnimatorUpdateListener> mUpdateListeners = null;
    PropertyValuesHolder[] mValues;
    HashMap<String, PropertyValuesHolder> mValuesMap;

    private static class AnimationHandler extends Handler {
        private AnimationHandler() {
        }

        public final void handleMessage(Message msg) {
            int i;
            ValueAnimator anim;
            boolean callAgain = true;
            ArrayList<ValueAnimator> animations = (ArrayList) ValueAnimator.sAnimations.get();
            ArrayList<ValueAnimator> delayedAnims = (ArrayList) ValueAnimator.sDelayedAnims.get();
            switch (msg.what) {
                case 0:
                    ArrayList<ValueAnimator> pendingAnimations = (ArrayList) ValueAnimator.sPendingAnimations.get();
                    if (animations.size() > 0 || delayedAnims.size() > 0) {
                        callAgain = false;
                    }
                    while (pendingAnimations.size() > 0) {
                        ArrayList<ValueAnimator> pendingCopy = (ArrayList) pendingAnimations.clone();
                        pendingAnimations.clear();
                        int count = pendingCopy.size();
                        for (i = 0; i < count; i++) {
                            anim = (ValueAnimator) pendingCopy.get(i);
                            if (0 == 0) {
                                ValueAnimator.access$400(anim);
                            } else {
                                delayedAnims.add(anim);
                            }
                        }
                    }
                    break;
                case 1:
                    break;
                default:
                    return;
            }
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            ArrayList<ValueAnimator> readyAnims = (ArrayList) ValueAnimator.sReadyAnims.get();
            ArrayList<ValueAnimator> endingAnims = (ArrayList) ValueAnimator.sEndingAnims.get();
            int numDelayedAnims = delayedAnims.size();
            for (i = 0; i < numDelayedAnims; i++) {
                anim = (ValueAnimator) delayedAnims.get(i);
                if (ValueAnimator.access$700(anim, currentTime)) {
                    readyAnims.add(anim);
                }
            }
            int numReadyAnims = readyAnims.size();
            if (numReadyAnims > 0) {
                for (i = 0; i < numReadyAnims; i++) {
                    anim = (ValueAnimator) readyAnims.get(i);
                    ValueAnimator.access$400(anim);
                    anim.mRunning = true;
                    delayedAnims.remove(anim);
                }
                readyAnims.clear();
            }
            int numAnims = animations.size();
            i = 0;
            while (i < numAnims) {
                anim = (ValueAnimator) animations.get(i);
                if (anim.animationFrame(currentTime)) {
                    endingAnims.add(anim);
                }
                if (animations.size() == numAnims) {
                    i++;
                } else {
                    numAnims--;
                    endingAnims.remove(anim);
                }
            }
            if (endingAnims.size() > 0) {
                for (i = 0; i < endingAnims.size(); i++) {
                    ValueAnimator.access$900((ValueAnimator) endingAnims.get(i));
                }
                endingAnims.clear();
            }
            if (!callAgain) {
                return;
            }
            if (!animations.isEmpty() || !delayedAnims.isEmpty()) {
                sendEmptyMessageDelayed(1, Math.max(0, ValueAnimator.sFrameDelay - (AnimationUtils.currentAnimationTimeMillis() - currentTime)));
            }
        }
    }

    public interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimator valueAnimator);
    }

    static /* synthetic */ void access$400(ValueAnimator x0) {
        x0.initAnimation();
        ((ArrayList) sAnimations.get()).add(x0);
        if (0 > 0 && x0.mListeners != null) {
            ArrayList arrayList = (ArrayList) x0.mListeners.clone();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i);
            }
        }
    }

    static /* synthetic */ boolean access$700(ValueAnimator x0, long x1) {
        if (x0.mStartedDelay) {
            long j = x1 - x0.mDelayStartTime;
            if (j > 0) {
                x0.mStartTime = x1 - j;
                x0.mPlayingState = 1;
                return true;
            }
        }
        x0.mStartedDelay = true;
        x0.mDelayStartTime = x1;
        return false;
    }

    static /* synthetic */ void access$900(ValueAnimator x0) {
        ((ArrayList) sAnimations.get()).remove(x0);
        ((ArrayList) sPendingAnimations.get()).remove(x0);
        ((ArrayList) sDelayedAnims.get()).remove(x0);
        x0.mPlayingState = 0;
        if (x0.mRunning && x0.mListeners != null) {
            ArrayList arrayList = (ArrayList) x0.mListeners.clone();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i);
            }
        }
        x0.mRunning = false;
        x0.mStarted = false;
    }

    public static ValueAnimator ofFloat(float... values) {
        ValueAnimator anim = new ValueAnimator();
        if (!(values == null || values.length == 0)) {
            if (anim.mValues == null || anim.mValues.length == 0) {
                PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("", values)};
                anim.mValues = propertyValuesHolderArr;
                anim.mValuesMap = new HashMap(1);
                for (int i = 0; i <= 0; i++) {
                    PropertyValuesHolder propertyValuesHolder = propertyValuesHolderArr[0];
                    anim.mValuesMap.put(propertyValuesHolder.mPropertyName, propertyValuesHolder);
                }
                anim.mInitialized = false;
            } else {
                anim.mValues[0].setFloatValues(values);
            }
            anim.mInitialized = false;
        }
        return anim;
    }

    private void initAnimation() {
        if (!this.mInitialized) {
            for (PropertyValuesHolder init : this.mValues) {
                init.init();
            }
            this.mInitialized = true;
        }
    }

    public final ValueAnimator setDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + duration);
        }
        this.mDuration = duration;
        return this;
    }

    private void setCurrentPlayTime(long playTime) {
        initAnimation();
        long currentTime = AnimationUtils.currentAnimationTimeMillis();
        if (this.mPlayingState != 1) {
            this.mSeekTime = playTime;
            this.mPlayingState = 2;
        }
        this.mStartTime = currentTime - playTime;
        animationFrame(currentTime);
    }

    public final Object getAnimatedValue() {
        if (this.mValues == null || this.mValues.length <= 0) {
            return null;
        }
        return this.mValues[0].getAnimatedValue();
    }

    public final void addUpdateListener(AnimatorUpdateListener listener) {
        if (this.mUpdateListeners == null) {
            this.mUpdateListeners = new ArrayList();
        }
        this.mUpdateListeners.add(listener);
    }

    public final void setInterpolator(Interpolator value) {
        this.mInterpolator = value;
    }

    public final void start() {
        if (Looper.myLooper() == null) {
            throw new AndroidRuntimeException("Animators may only be run on Looper threads");
        }
        this.mPlayingBackwards = false;
        this.mCurrentIteration = 0;
        this.mPlayingState = 0;
        this.mStarted = true;
        this.mStartedDelay = false;
        ((ArrayList) sPendingAnimations.get()).add(this);
        if (0 == 0) {
            long j;
            if (!this.mInitialized || this.mPlayingState == 0) {
                j = 0;
            } else {
                j = AnimationUtils.currentAnimationTimeMillis() - this.mStartTime;
            }
            setCurrentPlayTime(j);
            this.mPlayingState = 0;
            this.mRunning = true;
            if (this.mListeners != null) {
                ArrayList arrayList = (ArrayList) this.mListeners.clone();
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    arrayList.get(i);
                }
            }
        }
        AnimationHandler animationHandler = (AnimationHandler) sAnimationHandler.get();
        if (animationHandler == null) {
            animationHandler = new AnimationHandler();
            sAnimationHandler.set(animationHandler);
        }
        animationHandler.sendEmptyMessage(0);
    }

    final boolean animationFrame(long currentTime) {
        boolean z = true;
        int i = 0;
        boolean done = false;
        if (this.mPlayingState == 0) {
            this.mPlayingState = 1;
            if (this.mSeekTime < 0) {
                this.mStartTime = currentTime;
            } else {
                this.mStartTime = currentTime - this.mSeekTime;
                this.mSeekTime = -1;
            }
        }
        switch (this.mPlayingState) {
            case 1:
            case 2:
                float fraction;
                if (this.mDuration > 0) {
                    fraction = ((float) (currentTime - this.mStartTime)) / ((float) this.mDuration);
                } else {
                    fraction = 1.0f;
                }
                if (fraction >= 1.0f) {
                    if (this.mCurrentIteration >= 0) {
                        done = true;
                        fraction = Math.min(fraction, 1.0f);
                    } else {
                        if (this.mListeners != null) {
                            int numListeners = this.mListeners.size();
                            for (int i2 = 0; i2 < numListeners; i2++) {
                                this.mListeners.get(i2);
                            }
                        }
                        if (this.mRepeatMode == 2) {
                            if (this.mPlayingBackwards) {
                                z = false;
                            }
                            this.mPlayingBackwards = z;
                        }
                        this.mCurrentIteration += (int) fraction;
                        fraction %= 1.0f;
                        this.mStartTime += this.mDuration;
                    }
                }
                if (this.mPlayingBackwards) {
                    fraction = 1.0f - fraction;
                }
                float interpolation = this.mInterpolator.getInterpolation(fraction);
                this.mCurrentFraction = interpolation;
                for (PropertyValuesHolder calculateValue : this.mValues) {
                    calculateValue.calculateValue(interpolation);
                }
                if (this.mUpdateListeners != null) {
                    int size = this.mUpdateListeners.size();
                    while (i < size) {
                        ((AnimatorUpdateListener) this.mUpdateListeners.get(i)).onAnimationUpdate(this);
                        i++;
                    }
                    break;
                }
                break;
        }
        return done;
    }

    private ValueAnimator clone() {
        int i;
        ValueAnimator anim = (ValueAnimator) super.clone();
        if (this.mUpdateListeners != null) {
            ArrayList<AnimatorUpdateListener> oldListeners = this.mUpdateListeners;
            anim.mUpdateListeners = new ArrayList();
            int numListeners = oldListeners.size();
            for (i = 0; i < numListeners; i++) {
                anim.mUpdateListeners.add(oldListeners.get(i));
            }
        }
        anim.mSeekTime = -1;
        anim.mPlayingBackwards = false;
        anim.mCurrentIteration = 0;
        anim.mInitialized = false;
        anim.mPlayingState = 0;
        anim.mStartedDelay = false;
        PropertyValuesHolder[] oldValues = this.mValues;
        if (oldValues != null) {
            int numValues = oldValues.length;
            anim.mValues = new PropertyValuesHolder[numValues];
            anim.mValuesMap = new HashMap(numValues);
            for (i = 0; i < numValues; i++) {
                PropertyValuesHolder newValuesHolder = oldValues[i].clone();
                anim.mValues[i] = newValuesHolder;
                anim.mValuesMap.put(newValuesHolder.mPropertyName, newValuesHolder);
            }
        }
        return anim;
    }

    public final String toString() {
        String returnVal = "ValueAnimator@" + Integer.toHexString(hashCode());
        if (this.mValues != null) {
            for (PropertyValuesHolder propertyValuesHolder : this.mValues) {
                returnVal = returnVal + "\n    " + propertyValuesHolder.toString();
            }
        }
        return returnVal;
    }
}
