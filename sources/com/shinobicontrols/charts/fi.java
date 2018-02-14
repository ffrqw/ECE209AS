package com.shinobicontrols.charts;

import android.view.VelocityTracker;

class fi {
    private VelocityTracker sW;

    fi() {
    }

    VelocityTracker ff() {
        if (this.sW == null) {
            this.sW = VelocityTracker.obtain();
        }
        return this.sW;
    }

    VelocityTracker fg() {
        return this.sW;
    }

    void recycle() {
        if (this.sW != null) {
            this.sW.recycle();
            this.sW = null;
        }
    }
}
