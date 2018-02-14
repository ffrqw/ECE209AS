package com.shinobicontrols.charts;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ChartView extends FrameLayout {
    private final af J;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.J = new af(getContext(), attrs, defStyle);
        addView(this.J);
    }

    public final ShinobiChart getShinobiChart() {
        return this.J;
    }

    public final void onCreate(Bundle savedInstanceState) {
        this.J.onCreate(savedInstanceState);
    }

    public void onResume() {
        this.J.onResume();
    }

    public void onPause() {
        this.J.onPause();
    }

    public final void onDestroy() {
        this.J.onDestroy();
    }
}
