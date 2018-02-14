package android.support.v4.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.view.View;

@TargetApi(19)
final class ViewPropertyAnimatorCompatKK {

    /* renamed from: android.support.v4.view.ViewPropertyAnimatorCompatKK$1 */
    static class AnonymousClass1 implements AnimatorUpdateListener {
        final /* synthetic */ ViewPropertyAnimatorUpdateListener val$listener;
        final /* synthetic */ View val$view;

        AnonymousClass1(ViewPropertyAnimatorUpdateListener viewPropertyAnimatorUpdateListener, View view) {
            this.val$listener = viewPropertyAnimatorUpdateListener;
            this.val$view = view;
        }

        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            this.val$listener.onAnimationUpdate$3c7ec8c3();
        }
    }
}
