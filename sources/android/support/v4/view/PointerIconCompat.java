package android.support.v4.view;

import android.content.Context;
import android.support.v4.os.BuildCompat;
import android.view.PointerIcon;

public final class PointerIconCompat {
    static final PointerIconCompatImpl IMPL;
    private Object mPointerIcon;

    interface PointerIconCompatImpl {
        Object getSystemIcon(Context context, int i);
    }

    static class BasePointerIconCompatImpl implements PointerIconCompatImpl {
        BasePointerIconCompatImpl() {
        }

        public Object getSystemIcon(Context context, int style) {
            return null;
        }
    }

    static class Api24PointerIconCompatImpl extends BasePointerIconCompatImpl {
        Api24PointerIconCompatImpl() {
        }

        public final Object getSystemIcon(Context context, int style) {
            return PointerIcon.getSystemIcon(context, style);
        }
    }

    private PointerIconCompat(Object pointerIcon) {
        this.mPointerIcon = pointerIcon;
    }

    public final Object getPointerIcon() {
        return this.mPointerIcon;
    }

    static {
        if (BuildCompat.isAtLeastN()) {
            IMPL = new Api24PointerIconCompatImpl();
        } else {
            IMPL = new BasePointerIconCompatImpl();
        }
    }

    public static PointerIconCompat getSystemIcon(Context context, int style) {
        return new PointerIconCompat(IMPL.getSystemIcon(context, 1002));
    }
}
