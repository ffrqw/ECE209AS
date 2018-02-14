package com.shinobicontrols.charts;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DefaultTooltipView extends RelativeLayout {
    TextView gV;
    TextView gW;
    TextView gX;
    TextView gY;
    TextView gZ;
    TextView ha;
    TextView hb;
    TextView hc;
    TextView hd;

    public DefaultTooltipView(Context context) {
        super(context);
        d(context);
        cf();
    }

    public final void setText(CharSequence text) {
        this.gV.setText(text);
        ce();
    }

    public final void setText(int resid) {
        this.gV.setText(resid);
        ce();
    }

    private void ce() {
        for (int i = 1; i < 9; i++) {
            getChildAt(i).setVisibility(8);
        }
    }

    private void d(Context context) {
        int i = 5;
        this.gV = new TextView(context);
        this.gV.setId(1);
        this.gW = new TextView(context);
        this.gW.setId(2);
        this.gX = new TextView(context);
        this.gX.setId(3);
        this.gY = new TextView(context);
        this.gY.setId(4);
        this.gZ = new TextView(context);
        this.gZ.setId(5);
        this.ha = new TextView(context);
        this.ha.setId(6);
        this.hb = new TextView(context);
        this.hb.setId(7);
        this.hc = new TextView(context);
        this.hc.setId(8);
        this.hd = new TextView(context);
        this.hd.setId(9);
        addView(this.gV);
        addView(this.gW);
        addView(this.gX);
        addView(this.gY);
        addView(this.gZ);
        addView(this.ha);
        addView(this.hb);
        addView(this.hc);
        addView(this.hd);
        for (int i2 = 0; i2 < 5; i2++) {
            ((TextView) getChildAt(i2)).setGravity(5);
        }
        while (i < 9) {
            ((TextView) getChildAt(i)).setGravity(3);
            i++;
        }
        ((TextView) getChildAt(0)).setGravity(1);
    }

    private void cf() {
        cg();
        ch();
        ci();
        cj();
        ck();
    }

    private void cg() {
        LayoutParams layoutParams = (LayoutParams) this.gV.getLayoutParams();
        layoutParams.addRule(10);
        layoutParams.addRule(14);
    }

    private void ch() {
        ((LayoutParams) this.gW.getLayoutParams()).addRule(3, 1);
        LayoutParams layoutParams = (LayoutParams) this.ha.getLayoutParams();
        layoutParams.addRule(3, 1);
        layoutParams.addRule(1, 2);
    }

    private void ci() {
        LayoutParams layoutParams = (LayoutParams) this.gX.getLayoutParams();
        layoutParams.addRule(3, 1);
        layoutParams.addRule(1, 6);
        layoutParams = (LayoutParams) this.hb.getLayoutParams();
        layoutParams.addRule(3, 1);
        layoutParams.addRule(1, 3);
    }

    private void cj() {
        ((LayoutParams) this.gY.getLayoutParams()).addRule(3, 2);
        LayoutParams layoutParams = (LayoutParams) this.hc.getLayoutParams();
        layoutParams.addRule(3, 6);
        layoutParams.addRule(1, 4);
    }

    private void ck() {
        LayoutParams layoutParams = (LayoutParams) this.gZ.getLayoutParams();
        layoutParams.addRule(3, 3);
        layoutParams.addRule(1, 8);
        layoutParams = (LayoutParams) this.hd.getLayoutParams();
        layoutParams.addRule(3, 7);
        layoutParams.addRule(1, 5);
    }
}
