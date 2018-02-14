package android.support.v7.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertController.AlertParams;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import com.rachio.iro.R;

public final class AlertDialog extends AppCompatDialog implements DialogInterface {
    final AlertController mAlert = new AlertController(getContext(), this, getWindow());

    public static class Builder {
        private final AlertParams P;
        private final int mTheme;

        public Builder(Context context) {
            this(context, AlertDialog.resolveDialogTheme(context, 0));
        }

        private Builder(Context context, int themeResId) {
            this.P = new AlertParams(new ContextThemeWrapper(context, AlertDialog.resolveDialogTheme(context, themeResId)));
            this.mTheme = themeResId;
        }

        public final Context getContext() {
            return this.P.mContext;
        }

        public final Builder setTitle(CharSequence title) {
            this.P.mTitle = title;
            return this;
        }

        public final Builder setCustomTitle(View customTitleView) {
            this.P.mCustomTitleView = customTitleView;
            return this;
        }

        public final Builder setMessage(int messageId) {
            this.P.mMessage = this.P.mContext.getText(R.string.cancelwizardblurb);
            return this;
        }

        public final Builder setMessage(CharSequence message) {
            this.P.mMessage = message;
            return this;
        }

        public final Builder setIcon(Drawable icon) {
            this.P.mIcon = icon;
            return this;
        }

        public final Builder setPositiveButton(int textId, OnClickListener listener) {
            this.P.mPositiveButtonText = this.P.mContext.getText(textId);
            this.P.mPositiveButtonListener = listener;
            return this;
        }

        public final Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            this.P.mPositiveButtonText = text;
            this.P.mPositiveButtonListener = listener;
            return this;
        }

        public final Builder setNegativeButton(int textId, OnClickListener listener) {
            this.P.mNegativeButtonText = this.P.mContext.getText(R.string.no);
            this.P.mNegativeButtonListener = listener;
            return this;
        }

        public final Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            this.P.mNegativeButtonText = text;
            this.P.mNegativeButtonListener = listener;
            return this;
        }

        public final Builder setNeutralButton(int textId, OnClickListener listener) {
            this.P.mNeutralButtonText = this.P.mContext.getText(17039370);
            this.P.mNeutralButtonListener = null;
            return this;
        }

        public final Builder setCancelable(boolean cancelable) {
            this.P.mCancelable = false;
            return this;
        }

        public final Builder setOnKeyListener(OnKeyListener onKeyListener) {
            this.P.mOnKeyListener = onKeyListener;
            return this;
        }

        public final Builder setItems(CharSequence[] items, OnClickListener listener) {
            this.P.mItems = items;
            this.P.mOnClickListener = listener;
            return this;
        }

        public final Builder setAdapter(ListAdapter adapter, OnClickListener listener) {
            this.P.mAdapter = adapter;
            this.P.mOnClickListener = listener;
            return this;
        }

        public final Builder setView(View view) {
            this.P.mView = view;
            this.P.mViewLayoutResId = 0;
            this.P.mViewSpacingSpecified = false;
            return this;
        }

        public final AlertDialog create() {
            AlertDialog dialog = new AlertDialog(this.P.mContext, this.mTheme);
            this.P.apply(dialog.mAlert);
            dialog.setCancelable(this.P.mCancelable);
            if (this.P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(null);
            dialog.setOnDismissListener(null);
            if (this.P.mOnKeyListener != null) {
                dialog.setOnKeyListener(this.P.mOnKeyListener);
            }
            return dialog;
        }

        public final AlertDialog show() {
            AlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    protected AlertDialog(Context context, int themeResId) {
        super(context, resolveDialogTheme(context, themeResId));
    }

    static int resolveDialogTheme(Context context, int resid) {
        if (resid >= 16777216) {
            return resid;
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.alertDialogTheme, outValue, true);
        return outValue.resourceId;
    }

    public final Button getButton(int whichButton) {
        AlertController alertController = this.mAlert;
        switch (-1) {
            case -3:
                return alertController.mButtonNeutral;
            case -2:
                return alertController.mButtonNegative;
            case -1:
                return alertController.mButtonPositive;
            default:
                return null;
        }
    }

    public final void setTitle(CharSequence title) {
        super.setTitle(title);
        this.mAlert.setTitle(title);
    }

    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAlert.installContent();
    }

    public final boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean z;
        AlertController alertController = this.mAlert;
        if (alertController.mScrollView == null || !alertController.mScrollView.executeKeyEvent(event)) {
            z = false;
        } else {
            z = true;
        }
        if (z) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public final boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean z;
        AlertController alertController = this.mAlert;
        if (alertController.mScrollView == null || !alertController.mScrollView.executeKeyEvent(event)) {
            z = false;
        } else {
            z = true;
        }
        if (z) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
