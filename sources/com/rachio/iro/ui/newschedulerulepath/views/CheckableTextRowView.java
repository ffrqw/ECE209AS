package com.rachio.iro.ui.newschedulerulepath.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.rachio.iro.R;

public class CheckableTextRowView extends FrameLayout implements Checkable {
    private static int[] CHECKED_STATE_SET = new int[]{16842912};
    private static int[] MASKED_STATE_SET = new int[]{R.attr.state_masked};
    private static int[] SINGLE_STATE_SET = new int[]{R.attr.state_choicemode_single};
    private ImageView checkBox;
    private boolean checked;
    private int choiceMode;
    private boolean masked;
    protected TextView text;

    public CheckableTextRowView(Context context) {
        this(context, null);
    }

    private CheckableTextRowView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    private CheckableTextRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        View.inflate(context, R.layout.view_checkabletextrow, this);
        this.text = (TextView) findViewById(R.id.text);
        this.checkBox = (ImageView) findViewById(R.id.checkbox);
        this.checkBox.setImageResource(R.drawable.settings_checkbox);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        refreshDrawableState();
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void toggle() {
        setChecked(!this.checked);
    }

    public final void setText(String text) {
        this.text.setText(text);
    }

    public final void setChoiceMode(int choiceMode) {
        this.choiceMode = choiceMode;
        refreshDrawableState();
    }

    public final void setMasked(boolean masked) {
        this.masked = masked;
        refreshDrawableState();
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 3);
        if (this.checked) {
            drawableState = mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        if (this.choiceMode == 1) {
            drawableState = mergeDrawableStates(drawableState, SINGLE_STATE_SET);
        }
        if (this.masked) {
            return mergeDrawableStates(drawableState, MASKED_STATE_SET);
        }
        return drawableState;
    }
}
