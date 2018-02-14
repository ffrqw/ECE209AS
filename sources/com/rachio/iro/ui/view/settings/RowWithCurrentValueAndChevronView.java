package com.rachio.iro.ui.view.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.rachio.iro.R;

public class RowWithCurrentValueAndChevronView extends RowWithChevronView {
    private final TextView value;

    public RowWithCurrentValueAndChevronView(Context context) {
        this(context, null);
    }

    public RowWithCurrentValueAndChevronView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.rowWithChevronViewStyle);
    }

    public RowWithCurrentValueAndChevronView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.nextToChevron.setVisibility(0);
        inflate(context, R.layout.currentvaluechevrontext, this.nextToChevron);
        this.value = (TextView) findViewById(R.id.value);
    }

    public final void setValue(String value) {
        this.value.setText(value);
    }
}
