package com.shinobicontrols.charts;

import java.text.DecimalFormat;

class dd extends DecimalFormat {
    private static final long serialVersionUID = 5745069603170376668L;
    private int lB;
    private String lF;
    private int lG = -1;
    private int lH;
    private double lI = -1.0d;
    private double lJ;

    dd() {
    }

    String dk() {
        if (!(this.lI == this.lJ && this.lG == this.lH)) {
            this.lF = super.format(this.lI);
            this.lJ = this.lI;
            this.lH = this.lG;
        }
        return this.lF;
    }

    void A(int i) {
        if (i != this.lB) {
            this.lB = i;
            this.lI = -Math.pow(10.0d, (double) this.lB);
        }
    }

    boolean B(int i) {
        if (i == this.lG) {
            return false;
        }
        this.lG = i;
        super.setMinimumFractionDigits(this.lG);
        super.setMaximumFractionDigits(this.lG + 2);
        return true;
    }
}
