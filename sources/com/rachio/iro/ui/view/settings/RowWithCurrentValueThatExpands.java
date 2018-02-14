package com.rachio.iro.ui.view.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import com.rachio.iro.R;
import com.rachio.iro.ui.newschedulerulepath.views.Expandable;
import com.rachio.iro.ui.newschedulerulepath.views.Expandable.Listener;

public class RowWithCurrentValueThatExpands extends RowWithCurrentValueAndChevronView implements Expandable {
    private boolean expanded;
    private Listener listener;
    private int viewId;
    private View viewUnderControl;

    public RowWithCurrentValueThatExpands(Context context) {
        this(context, null);
    }

    public RowWithCurrentValueThatExpands(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RowWithCurrentValueThatExpands(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.expanded = false;
        this.drawable.setImageResource(R.drawable.expander);
        this.drawable.setVisibility(0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Expandable);
        this.viewId = a.getResourceId(0, -1);
        if (this.viewId != -1 || isInEditMode()) {
            a.recycle();
            super.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (RowWithCurrentValueThatExpands.this.expanded) {
                        RowWithCurrentValueThatExpands.this.collapse();
                    } else {
                        RowWithCurrentValueThatExpands.this.expand();
                    }
                }
            });
            return;
        }
        throw new IllegalStateException();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.viewUnderControl = getRootView().findViewById(this.viewId);
    }

    public final void expand() {
        if (!this.expanded) {
            setSelected(true);
            this.expanded = true;
            this.viewUnderControl.setVisibility(0);
            if (this.listener != null) {
                this.listener.onExpanded(this);
            }
        }
    }

    public final void collapse() {
        if (this.expanded) {
            setSelected(false);
            this.expanded = false;
            this.viewUnderControl.setVisibility(8);
        }
    }

    public void setOnClickListener(OnClickListener l) {
        throw new UnsupportedOperationException();
    }

    public final void setExpandListener(Listener listener) {
        this.listener = listener;
    }
}
