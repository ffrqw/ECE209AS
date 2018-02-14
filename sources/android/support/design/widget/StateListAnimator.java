package android.support.design.widget;

import android.util.StateSet;
import java.util.ArrayList;

final class StateListAnimator {
    private final AnimatorListener mAnimationListener = new AnimatorListenerAdapter() {
        public final void onAnimationEnd(ValueAnimatorCompat animator) {
            if (StateListAnimator.this.mRunningAnimator == animator) {
                StateListAnimator.this.mRunningAnimator = null;
            }
        }
    };
    private Tuple mLastMatch = null;
    ValueAnimatorCompat mRunningAnimator = null;
    private final ArrayList<Tuple> mTuples = new ArrayList();

    static class Tuple {
        final ValueAnimatorCompat mAnimator;
        final int[] mSpecs;

        Tuple(int[] specs, ValueAnimatorCompat animator) {
            this.mSpecs = specs;
            this.mAnimator = animator;
        }
    }

    StateListAnimator() {
    }

    public final void addState(int[] specs, ValueAnimatorCompat animator) {
        Tuple tuple = new Tuple(specs, animator);
        animator.addListener(this.mAnimationListener);
        this.mTuples.add(tuple);
    }

    final void setState(int[] state) {
        Tuple match = null;
        int count = this.mTuples.size();
        for (int i = 0; i < count; i++) {
            Tuple tuple = (Tuple) this.mTuples.get(i);
            if (StateSet.stateSetMatches(tuple.mSpecs, state)) {
                match = tuple;
                break;
            }
        }
        if (match != this.mLastMatch) {
            if (!(this.mLastMatch == null || this.mRunningAnimator == null)) {
                this.mRunningAnimator.cancel();
                this.mRunningAnimator = null;
            }
            this.mLastMatch = match;
            if (match != null) {
                this.mRunningAnimator = match.mAnimator;
                this.mRunningAnimator.start();
            }
        }
    }
}
