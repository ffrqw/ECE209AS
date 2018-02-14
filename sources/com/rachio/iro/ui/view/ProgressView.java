package com.rachio.iro.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.rachio.iro.R;

public class ProgressView extends RelativeLayout {
    private TextView mProgresstext;
    private final boolean mShowProgressBar;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_progress_overlay, this);
        setBackgroundColor(context.getResources().getColor(R.color.rachio_white_66_percent));
        setGravity(17);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressView);
        this.mShowProgressBar = a.getBoolean(0, true);
        a.recycle();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mProgresstext = (TextView) findViewById(R.id.progress_text);
        if (!this.mShowProgressBar) {
            findViewById(R.id.progress_bar).setVisibility(8);
        }
    }

    public final void show(String text) {
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        if (TextUtils.isEmpty(text)) {
            this.mProgresstext.setVisibility(8);
            return;
        }
        this.mProgresstext.setVisibility(0);
        this.mProgresstext.setText(text);
    }
}
