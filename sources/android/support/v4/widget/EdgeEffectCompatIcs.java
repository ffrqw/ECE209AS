package android.support.v4.widget;

import android.annotation.TargetApi;
import android.widget.EdgeEffect;

@TargetApi(14)
final class EdgeEffectCompatIcs {
    public static boolean onPull(Object edgeEffect, float deltaDistance) {
        ((EdgeEffect) edgeEffect).onPull(deltaDistance);
        return true;
    }
}
