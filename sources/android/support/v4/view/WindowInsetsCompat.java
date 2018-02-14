package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.WindowInsets;

public final class WindowInsetsCompat {
    private static final WindowInsetsCompatImpl IMPL;
    private final Object mInsets;

    private interface WindowInsetsCompatImpl {
        int getSystemWindowInsetBottom(Object obj);

        int getSystemWindowInsetLeft(Object obj);

        int getSystemWindowInsetRight(Object obj);

        int getSystemWindowInsetTop(Object obj);

        boolean isConsumed(Object obj);

        WindowInsetsCompat replaceSystemWindowInsets(Object obj, int i, int i2, int i3, int i4);
    }

    private static class WindowInsetsCompatBaseImpl implements WindowInsetsCompatImpl {
        WindowInsetsCompatBaseImpl() {
        }

        public int getSystemWindowInsetLeft(Object insets) {
            return 0;
        }

        public int getSystemWindowInsetTop(Object insets) {
            return 0;
        }

        public int getSystemWindowInsetRight(Object insets) {
            return 0;
        }

        public int getSystemWindowInsetBottom(Object insets) {
            return 0;
        }

        public boolean isConsumed(Object insets) {
            return false;
        }

        public WindowInsetsCompat replaceSystemWindowInsets(Object insets, int left, int top, int right, int bottom) {
            return null;
        }
    }

    private static class WindowInsetsCompatApi20Impl extends WindowInsetsCompatBaseImpl {
        WindowInsetsCompatApi20Impl() {
        }

        public final WindowInsetsCompat replaceSystemWindowInsets(Object insets, int left, int top, int right, int bottom) {
            return new WindowInsetsCompat(((WindowInsets) insets).replaceSystemWindowInsets(left, top, right, bottom));
        }

        public final int getSystemWindowInsetBottom(Object insets) {
            return ((WindowInsets) insets).getSystemWindowInsetBottom();
        }

        public final int getSystemWindowInsetLeft(Object insets) {
            return ((WindowInsets) insets).getSystemWindowInsetLeft();
        }

        public final int getSystemWindowInsetRight(Object insets) {
            return ((WindowInsets) insets).getSystemWindowInsetRight();
        }

        public final int getSystemWindowInsetTop(Object insets) {
            return ((WindowInsets) insets).getSystemWindowInsetTop();
        }
    }

    private static class WindowInsetsCompatApi21Impl extends WindowInsetsCompatApi20Impl {
        WindowInsetsCompatApi21Impl() {
        }

        public final boolean isConsumed(Object insets) {
            return ((WindowInsets) insets).isConsumed();
        }
    }

    static {
        int version = VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new WindowInsetsCompatApi21Impl();
        } else if (version >= 20) {
            IMPL = new WindowInsetsCompatApi20Impl();
        } else {
            IMPL = new WindowInsetsCompatBaseImpl();
        }
    }

    WindowInsetsCompat(Object insets) {
        this.mInsets = insets;
    }

    public final int getSystemWindowInsetLeft() {
        return IMPL.getSystemWindowInsetLeft(this.mInsets);
    }

    public final int getSystemWindowInsetTop() {
        return IMPL.getSystemWindowInsetTop(this.mInsets);
    }

    public final int getSystemWindowInsetRight() {
        return IMPL.getSystemWindowInsetRight(this.mInsets);
    }

    public final int getSystemWindowInsetBottom() {
        return IMPL.getSystemWindowInsetBottom(this.mInsets);
    }

    public final boolean isConsumed() {
        return IMPL.isConsumed(this.mInsets);
    }

    public final WindowInsetsCompat replaceSystemWindowInsets(int left, int top, int right, int bottom) {
        return IMPL.replaceSystemWindowInsets(this.mInsets, left, top, right, bottom);
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WindowInsetsCompat other = (WindowInsetsCompat) o;
        if (this.mInsets != null) {
            return this.mInsets.equals(other.mInsets);
        }
        if (other.mInsets != null) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        return this.mInsets == null ? 0 : this.mInsets.hashCode();
    }

    static WindowInsetsCompat wrap(Object insets) {
        return insets == null ? null : new WindowInsetsCompat(insets);
    }

    static Object unwrap(WindowInsetsCompat insets) {
        return insets == null ? null : insets.mInsets;
    }
}
