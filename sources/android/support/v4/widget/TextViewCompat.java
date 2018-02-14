package android.support.v4.widget;

import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.widget.TextView;

public final class TextViewCompat {
    static final TextViewCompatImpl IMPL;

    interface TextViewCompatImpl {
        int getMaxLines(TextView textView);

        void setCompoundDrawablesRelative(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4);

        void setTextAppearance(TextView textView, int i);
    }

    static class BaseTextViewCompatImpl implements TextViewCompatImpl {
        BaseTextViewCompatImpl() {
        }

        public void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            textView.setCompoundDrawables(start, top, end, bottom);
        }

        public int getMaxLines(TextView textView) {
            return TextViewCompatGingerbread.getMaxLines(textView);
        }

        public void setTextAppearance(TextView textView, int resId) {
            textView.setTextAppearance(textView.getContext(), resId);
        }
    }

    static class JbTextViewCompatImpl extends BaseTextViewCompatImpl {
        JbTextViewCompatImpl() {
        }

        public final int getMaxLines(TextView textView) {
            return textView.getMaxLines();
        }
    }

    static class JbMr1TextViewCompatImpl extends JbTextViewCompatImpl {
        JbMr1TextViewCompatImpl() {
        }

        public void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            Drawable drawable;
            Object obj = textView.getLayoutDirection() == 1 ? 1 : null;
            if (obj != null) {
                drawable = end;
            } else {
                drawable = start;
            }
            if (obj == null) {
                start = end;
            }
            textView.setCompoundDrawables(drawable, top, start, bottom);
        }
    }

    static class JbMr2TextViewCompatImpl extends JbMr1TextViewCompatImpl {
        JbMr2TextViewCompatImpl() {
        }

        public final void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            textView.setCompoundDrawablesRelative(start, top, end, bottom);
        }
    }

    static class Api23TextViewCompatImpl extends JbMr2TextViewCompatImpl {
        Api23TextViewCompatImpl() {
        }

        public final void setTextAppearance(TextView textView, int resId) {
            textView.setTextAppearance(resId);
        }
    }

    static {
        int version = VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new Api23TextViewCompatImpl();
        } else if (version >= 18) {
            IMPL = new JbMr2TextViewCompatImpl();
        } else if (version >= 17) {
            IMPL = new JbMr1TextViewCompatImpl();
        } else if (version >= 16) {
            IMPL = new JbTextViewCompatImpl();
        } else {
            IMPL = new BaseTextViewCompatImpl();
        }
    }

    public static void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        IMPL.setCompoundDrawablesRelative(textView, start, null, null, null);
    }

    public static int getMaxLines(TextView textView) {
        return IMPL.getMaxLines(textView);
    }

    public static void setTextAppearance(TextView textView, int resId) {
        IMPL.setTextAppearance(textView, resId);
    }
}
