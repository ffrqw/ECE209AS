package com.rachio.iro.ui.view.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.rachio.iro.R;

public class RowWithChevronView extends FrameLayout {
    protected final ImageView drawable;
    protected final FrameLayout nextToChevron;
    private final TextView text;

    public RowWithChevronView(Context context) {
        this(context, null);
    }

    public RowWithChevronView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.rowWithChevronViewStyle);
    }

    public RowWithChevronView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_rowwithchevron, this);
        this.text = (TextView) findViewById(R.id.text);
        this.nextToChevron = (FrameLayout) findViewById(R.id.nexttochevron);
        this.drawable = (ImageView) findViewById(R.id.drawable);
        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{16843087, 16843161}, defStyleAttr, 0);
        CharSequence t = a.getText(0);
        a.recycle();
        a = context.obtainStyledAttributes(attrs, R.styleable.SettingsRow, defStyleAttr, 0);
        getChildAt(0).getBackground().setLevel(a.getInt(0, 0));
        a.recycle();
        this.text.setText(t);
    }

    public final void setText(String text) {
        this.text.setText(text);
    }
}
