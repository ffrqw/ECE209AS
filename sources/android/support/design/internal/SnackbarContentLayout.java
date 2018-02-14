package android.support.design.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.R;
import android.support.design.widget.BaseTransientBottomBar.ContentViewCallback;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SnackbarContentLayout extends LinearLayout implements ContentViewCallback {
    private Button mActionView;
    private int mMaxInlineActionWidth;
    private int mMaxWidth;
    private TextView mMessageView;

    public SnackbarContentLayout(Context context) {
        this(context, null);
    }

    public SnackbarContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout);
        this.mMaxWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1);
        this.mMaxInlineActionWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_maxActionInlineWidth, -1);
        a.recycle();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mMessageView = (TextView) findViewById(com.rachio.iro.R.id.snackbar_text);
        this.mActionView = (Button) findViewById(com.rachio.iro.R.id.snackbar_action);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean isMultiLine;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mMaxWidth > 0 && getMeasuredWidth() > this.mMaxWidth) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(this.mMaxWidth, 1073741824);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        int multiLineVPadding = getResources().getDimensionPixelSize(com.rachio.iro.R.dimen.design_snackbar_padding_vertical_2lines);
        int singleLineVPadding = getResources().getDimensionPixelSize(com.rachio.iro.R.dimen.design_snackbar_padding_vertical);
        if (this.mMessageView.getLayout().getLineCount() > 1) {
            isMultiLine = true;
        } else {
            isMultiLine = false;
        }
        boolean remeasure = false;
        if (!isMultiLine || this.mMaxInlineActionWidth <= 0 || this.mActionView.getMeasuredWidth() <= this.mMaxInlineActionWidth) {
            int messagePadding;
            if (isMultiLine) {
                messagePadding = multiLineVPadding;
            } else {
                messagePadding = singleLineVPadding;
            }
            if (updateViewsWithinLayout(0, messagePadding, messagePadding)) {
                remeasure = true;
            }
        } else if (updateViewsWithinLayout(1, multiLineVPadding, multiLineVPadding - singleLineVPadding)) {
            remeasure = true;
        }
        if (remeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean updateViewsWithinLayout(int orientation, int messagePadTop, int messagePadBottom) {
        boolean changed = false;
        if (orientation != getOrientation()) {
            setOrientation(orientation);
            changed = true;
        }
        if (this.mMessageView.getPaddingTop() == messagePadTop && this.mMessageView.getPaddingBottom() == messagePadBottom) {
            return changed;
        }
        View view = this.mMessageView;
        if (ViewCompat.isPaddingRelative(view)) {
            ViewCompat.setPaddingRelative(view, ViewCompat.getPaddingStart(view), messagePadTop, ViewCompat.getPaddingEnd(view), messagePadBottom);
        } else {
            view.setPadding(view.getPaddingLeft(), messagePadTop, view.getPaddingRight(), messagePadBottom);
        }
        return true;
    }

    public final void animateContentIn(int delay, int duration) {
        ViewCompat.setAlpha(this.mMessageView, 0.0f);
        ViewCompat.animate(this.mMessageView).alpha(1.0f).setDuration(180).setStartDelay(70).start();
        if (this.mActionView.getVisibility() == 0) {
            ViewCompat.setAlpha(this.mActionView, 0.0f);
            ViewCompat.animate(this.mActionView).alpha(1.0f).setDuration(180).setStartDelay(70).start();
        }
    }

    public final void animateContentOut(int delay, int duration) {
        ViewCompat.setAlpha(this.mMessageView, 1.0f);
        ViewCompat.animate(this.mMessageView).alpha(0.0f).setDuration(180).setStartDelay(0).start();
        if (this.mActionView.getVisibility() == 0) {
            ViewCompat.setAlpha(this.mActionView, 1.0f);
            ViewCompat.animate(this.mActionView).alpha(0.0f).setDuration(180).setStartDelay(0).start();
        }
    }
}
