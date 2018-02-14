package com.shinobicontrols.charts;

import com.shinobicontrols.charts.TickMark.ClippingMode;

class fd {
    fd() {
    }

    boolean a(TickMark tickMark, c cVar, Axis<?, ?> axis, double d) {
        switch (cVar.bB) {
            case TICKS_AND_LABELS_PERSIST:
                if (d <= ((double) cVar.by)) {
                    return false;
                }
                return true;
            case NEITHER_PERSIST:
            case TICKS_PERSIST:
                return (0.5d * ((double) cVar.bA)) + d > ((double) cVar.by);
            default:
                return false;
        }
    }

    boolean b(TickMark tickMark, c cVar, Axis<?, ?> axis, double d) {
        switch (cVar.bC) {
            case TICKS_AND_LABELS_PERSIST:
                if (d >= ((double) cVar.bz)) {
                    return false;
                }
                return true;
            case NEITHER_PERSIST:
            case TICKS_PERSIST:
                return d - (0.5d * ((double) cVar.bA)) < ((double) cVar.bz);
            default:
                return false;
        }
    }

    boolean a(TickMark tickMark, c cVar, Axis<?, ?> axis, boolean z, boolean z2) {
        if (!z2 || z) {
            if (!z || z2) {
                return false;
            }
            if (cVar.bB == ClippingMode.TICKS_PERSIST) {
                return false;
            }
            return true;
        } else if (cVar.bC != ClippingMode.TICKS_PERSIST) {
            return true;
        } else {
            return false;
        }
    }
}
