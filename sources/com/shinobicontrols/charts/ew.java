package com.shinobicontrols.charts;

import android.view.MotionEvent;
import com.shinobicontrols.charts.ShinobiChart.OnGestureListener;

class ew {
    private final ai hf;
    private final fi nn = new fi();
    private final dy pK = new dy(2, this.nn);
    private final ex pL;
    private final az pM;

    ew(af afVar, OnGestureListener onGestureListener) {
        this.hf = afVar.eO;
        this.pL = new ex(afVar, onGestureListener, this.hf);
        this.pM = new az(afVar, onGestureListener, this.hf);
    }

    boolean onTouchEvent(MotionEvent event) {
        boolean z;
        if (event.getActionMasked() == 3) {
            this.pK.reset();
            z = true;
        } else {
            this.nn.ff().addMovement(event);
            dw F = this.pK.F(0);
            dw F2 = this.pK.F(1);
            int size = this.pK.size();
            this.pK.b(event);
            if (F == null) {
                F = this.pK.F(0);
            }
            if (F2 == null) {
                F2 = this.pK.F(1);
            }
            int size2 = this.pK.size();
            z = this.pL.a(size, size2, F) || this.pM.a(size, size2, F, F2);
        }
        if (this.pK.size() == 0) {
            this.nn.recycle();
        }
        return z;
    }

    void az() {
        this.pL.az();
    }
}
