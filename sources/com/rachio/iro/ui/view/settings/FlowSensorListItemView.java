package com.rachio.iro.ui.view.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.rachio.iro.R;

public class FlowSensorListItemView extends FrameLayout implements Checkable {
    private CheckBox checkBox;
    private TextView text;

    public FlowSensorListItemView(Context context) {
        this(context, null);
    }

    private FlowSensorListItemView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    private FlowSensorListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        inflate(context, R.layout.view_flowsensorlistitem, this);
        this.text = (TextView) findViewById(R.id.text);
        this.checkBox = (CheckBox) findViewById(R.id.checkbox);
    }

    public void setChecked(boolean checked) {
        this.checkBox.setChecked(checked);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public void toggle() {
        this.checkBox.toggle();
    }

    public final void setText(String text) {
        this.text.setText(text);
    }
}
