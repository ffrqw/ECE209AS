package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.widget.CompoundButton;

final class AppCompatCompoundButtonHelper {
    private ColorStateList mButtonTintList = null;
    private Mode mButtonTintMode = null;
    private boolean mHasButtonTint = false;
    private boolean mHasButtonTintMode = false;
    private boolean mSkipNextApply;
    private final CompoundButton mView;

    AppCompatCompoundButtonHelper(CompoundButton view) {
        this.mView = view;
    }

    final void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = this.mView.getContext().obtainStyledAttributes(attrs, R.styleable.CompoundButton, defStyleAttr, 0);
        try {
            if (a.hasValue(R.styleable.CompoundButton_android_button)) {
                int resourceId = a.getResourceId(R.styleable.CompoundButton_android_button, 0);
                if (resourceId != 0) {
                    this.mView.setButtonDrawable(AppCompatResources.getDrawable(this.mView.getContext(), resourceId));
                }
            }
            if (a.hasValue(R.styleable.CompoundButton_buttonTint)) {
                CompoundButtonCompat.setButtonTintList(this.mView, a.getColorStateList(R.styleable.CompoundButton_buttonTint));
            }
            if (a.hasValue(R.styleable.CompoundButton_buttonTintMode)) {
                CompoundButtonCompat.setButtonTintMode(this.mView, DrawableUtils.parseTintMode(a.getInt(R.styleable.CompoundButton_buttonTintMode, -1), null));
            }
            a.recycle();
        } catch (Throwable th) {
            a.recycle();
        }
    }

    final void setSupportButtonTintList(ColorStateList tint) {
        this.mButtonTintList = tint;
        this.mHasButtonTint = true;
        applyButtonTint();
    }

    final ColorStateList getSupportButtonTintList() {
        return this.mButtonTintList;
    }

    final void setSupportButtonTintMode(Mode tintMode) {
        this.mButtonTintMode = tintMode;
        this.mHasButtonTintMode = true;
        applyButtonTint();
    }

    final Mode getSupportButtonTintMode() {
        return this.mButtonTintMode;
    }

    final void onSetButtonDrawable() {
        if (this.mSkipNextApply) {
            this.mSkipNextApply = false;
            return;
        }
        this.mSkipNextApply = true;
        applyButtonTint();
    }

    final void applyButtonTint() {
        Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(this.mView);
        if (buttonDrawable == null) {
            return;
        }
        if (this.mHasButtonTint || this.mHasButtonTintMode) {
            buttonDrawable = DrawableCompat.wrap(buttonDrawable).mutate();
            if (this.mHasButtonTint) {
                DrawableCompat.setTintList(buttonDrawable, this.mButtonTintList);
            }
            if (this.mHasButtonTintMode) {
                DrawableCompat.setTintMode(buttonDrawable, this.mButtonTintMode);
            }
            if (buttonDrawable.isStateful()) {
                buttonDrawable.setState(this.mView.getDrawableState());
            }
            this.mView.setButtonDrawable(buttonDrawable);
        }
    }

    final int getCompoundPaddingLeft(int superValue) {
        if (VERSION.SDK_INT >= 17) {
            return superValue;
        }
        Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(this.mView);
        if (buttonDrawable != null) {
            return superValue + buttonDrawable.getIntrinsicWidth();
        }
        return superValue;
    }
}
