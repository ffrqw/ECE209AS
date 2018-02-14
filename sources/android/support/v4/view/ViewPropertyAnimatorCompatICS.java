package android.support.v4.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.view.View;

@TargetApi(14)
final class ViewPropertyAnimatorCompatICS {

    /* renamed from: android.support.v4.view.ViewPropertyAnimatorCompatICS$1 */
    static class AnonymousClass1 extends AnimatorListenerAdapter {
        final /* synthetic */ ViewPropertyAnimatorListener val$listener;
        final /* synthetic */ View val$view;

        AnonymousClass1(ViewPropertyAnimatorListener viewPropertyAnimatorListener, View view) {
            this.val$listener = viewPropertyAnimatorListener;
            this.val$view = view;
        }

        public final void onAnimationCancel(Animator animation) {
            this.val$listener.onAnimationCancel(this.val$view);
        }

        public final void onAnimationEnd(Animator animation) {
            this.val$listener.onAnimationEnd(this.val$view);
        }

        public final void onAnimationStart(Animator animation) {
            this.val$listener.onAnimationStart(this.val$view);
        }
    }
}
