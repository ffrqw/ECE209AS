package android.support.v4.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.widget.CompoundButton;

public final class CompoundButtonCompat {
    private static final CompoundButtonCompatImpl IMPL;

    interface CompoundButtonCompatImpl {
        Drawable getButtonDrawable(CompoundButton compoundButton);

        void setButtonTintList(CompoundButton compoundButton, ColorStateList colorStateList);

        void setButtonTintMode(CompoundButton compoundButton, Mode mode);
    }

    static class BaseCompoundButtonCompat implements CompoundButtonCompatImpl {
        BaseCompoundButtonCompat() {
        }

        public Drawable getButtonDrawable(CompoundButton button) {
            return CompoundButtonCompatGingerbread.getButtonDrawable(button);
        }

        public void setButtonTintList(CompoundButton button, ColorStateList tint) {
            if (button instanceof TintableCompoundButton) {
                ((TintableCompoundButton) button).setSupportButtonTintList(tint);
            }
        }

        public void setButtonTintMode(CompoundButton button, Mode tintMode) {
            if (button instanceof TintableCompoundButton) {
                ((TintableCompoundButton) button).setSupportButtonTintMode(tintMode);
            }
        }
    }

    static class LollipopCompoundButtonImpl extends BaseCompoundButtonCompat {
        LollipopCompoundButtonImpl() {
        }

        public final void setButtonTintList(CompoundButton button, ColorStateList tint) {
            button.setButtonTintList(tint);
        }

        public final void setButtonTintMode(CompoundButton button, Mode tintMode) {
            button.setButtonTintMode(tintMode);
        }
    }

    static class Api23CompoundButtonImpl extends LollipopCompoundButtonImpl {
        Api23CompoundButtonImpl() {
        }

        public final Drawable getButtonDrawable(CompoundButton button) {
            return button.getButtonDrawable();
        }
    }

    static {
        int sdk = VERSION.SDK_INT;
        if (sdk >= 23) {
            IMPL = new Api23CompoundButtonImpl();
        } else if (sdk >= 21) {
            IMPL = new LollipopCompoundButtonImpl();
        } else {
            IMPL = new BaseCompoundButtonCompat();
        }
    }

    public static void setButtonTintList(CompoundButton button, ColorStateList tint) {
        IMPL.setButtonTintList(button, tint);
    }

    public static void setButtonTintMode(CompoundButton button, Mode tintMode) {
        IMPL.setButtonTintMode(button, tintMode);
    }

    public static Drawable getButtonDrawable(CompoundButton button) {
        return IMPL.getButtonDrawable(button);
    }
}
