package android.support.design.widget;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.graphics.drawable.InsetDrawable;

@TargetApi(21)
final class FloatingActionButtonLollipop extends FloatingActionButtonIcs {
    private InsetDrawable mInsetDrawable;

    FloatingActionButtonLollipop(VisibilityAwareImageButton view, ShadowViewDelegate shadowViewDelegate, Creator animatorCreator) {
        super(view, shadowViewDelegate, animatorCreator);
    }

    final void onPaddingUpdated(Rect padding) {
        if (this.mShadowViewDelegate.isCompatPaddingEnabled()) {
            this.mInsetDrawable = new InsetDrawable(null, padding.left, padding.top, padding.right, padding.bottom);
            this.mShadowViewDelegate.setBackgroundDrawable(this.mInsetDrawable);
            return;
        }
        this.mShadowViewDelegate.setBackgroundDrawable(null);
    }

    final void onDrawableStateChanged(int[] state) {
    }

    final void jumpDrawableToCurrentState() {
    }

    final boolean requirePreDrawListener() {
        return false;
    }

    final void getPadding(Rect rect) {
        if (this.mShadowViewDelegate.isCompatPaddingEnabled()) {
            float radius = this.mShadowViewDelegate.getRadius();
            float maxShadowSize = this.mView.getElevation() + 0.0f;
            int hPadding = (int) Math.ceil((double) ShadowDrawableWrapper.calculateHorizontalPadding(maxShadowSize, radius, false));
            int vPadding = (int) Math.ceil((double) ShadowDrawableWrapper.calculateVerticalPadding(maxShadowSize, radius, false));
            rect.set(hPadding, vPadding, hPadding, vPadding);
            return;
        }
        rect.set(0, 0, 0, 0);
    }
}
