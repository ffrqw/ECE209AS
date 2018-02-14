package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.TintableCompoundButton;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.widget.CheckBox;
import com.rachio.iro.R;

public final class AppCompatCheckBox extends CheckBox implements TintableCompoundButton {
    private AppCompatCompoundButtonHelper mCompoundButtonHelper;

    public AppCompatCheckBox(Context context) {
        this(context, null);
    }

    public AppCompatCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.checkboxStyle);
    }

    public AppCompatCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(TintContextWrapper.wrap(context), attrs, defStyleAttr);
        this.mCompoundButtonHelper = new AppCompatCompoundButtonHelper(this);
        this.mCompoundButtonHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    public final void setButtonDrawable(Drawable buttonDrawable) {
        super.setButtonDrawable(buttonDrawable);
        if (this.mCompoundButtonHelper != null) {
            this.mCompoundButtonHelper.onSetButtonDrawable();
        }
    }

    public final void setButtonDrawable(int resId) {
        setButtonDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    public final int getCompoundPaddingLeft() {
        int value = super.getCompoundPaddingLeft();
        return this.mCompoundButtonHelper != null ? this.mCompoundButtonHelper.getCompoundPaddingLeft(value) : value;
    }

    public final void setSupportButtonTintList(ColorStateList tint) {
        if (this.mCompoundButtonHelper != null) {
            this.mCompoundButtonHelper.setSupportButtonTintList(tint);
        }
    }

    public final ColorStateList getSupportButtonTintList() {
        return this.mCompoundButtonHelper != null ? this.mCompoundButtonHelper.getSupportButtonTintList() : null;
    }

    public final void setSupportButtonTintMode(Mode tintMode) {
        if (this.mCompoundButtonHelper != null) {
            this.mCompoundButtonHelper.setSupportButtonTintMode(tintMode);
        }
    }

    public final Mode getSupportButtonTintMode() {
        return this.mCompoundButtonHelper != null ? this.mCompoundButtonHelper.getSupportButtonTintMode() : null;
    }
}
