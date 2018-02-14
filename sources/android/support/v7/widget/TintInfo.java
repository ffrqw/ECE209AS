package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;

final class TintInfo {
    public boolean mHasTintList;
    public boolean mHasTintMode;
    public ColorStateList mTintList;
    public Mode mTintMode;

    TintInfo() {
    }

    final void clear() {
        this.mTintList = null;
        this.mHasTintList = false;
        this.mTintMode = null;
        this.mHasTintMode = false;
    }
}
