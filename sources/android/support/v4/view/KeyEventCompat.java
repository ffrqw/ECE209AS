package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.KeyEvent;

public final class KeyEventCompat {
    static final KeyEventVersionImpl IMPL;

    interface KeyEventVersionImpl {
        boolean isCtrlPressed(KeyEvent keyEvent);

        boolean metaStateHasModifiers(int i, int i2);

        boolean metaStateHasNoModifiers(int i);
    }

    static class BaseKeyEventVersionImpl implements KeyEventVersionImpl {
        BaseKeyEventVersionImpl() {
        }

        private static int metaStateFilterDirectionalModifiers(int metaState, int modifiers, int basic, int left, int right) {
            boolean wantBasic;
            boolean wantLeftOrRight = true;
            if ((modifiers & basic) != 0) {
                wantBasic = true;
            } else {
                wantBasic = false;
            }
            int directional = left | right;
            if ((modifiers & directional) == 0) {
                wantLeftOrRight = false;
            }
            if (wantBasic) {
                if (!wantLeftOrRight) {
                    return metaState & (directional ^ -1);
                }
                throw new IllegalArgumentException("bad arguments");
            } else if (wantLeftOrRight) {
                return metaState & (basic ^ -1);
            } else {
                return metaState;
            }
        }

        public int normalizeMetaState(int metaState) {
            if ((metaState & 192) != 0) {
                metaState |= 1;
            }
            if ((metaState & 48) != 0) {
                metaState |= 2;
            }
            return metaState & 247;
        }

        public boolean metaStateHasModifiers(int metaState, int modifiers) {
            if (metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(normalizeMetaState(metaState) & 247, modifiers, 1, 64, 128), modifiers, 2, 16, 32) == modifiers) {
                return true;
            }
            return false;
        }

        public boolean metaStateHasNoModifiers(int metaState) {
            return (normalizeMetaState(metaState) & 247) == 0;
        }

        public boolean isCtrlPressed(KeyEvent event) {
            return false;
        }
    }

    static class HoneycombKeyEventVersionImpl extends BaseKeyEventVersionImpl {
        HoneycombKeyEventVersionImpl() {
        }

        public final int normalizeMetaState(int metaState) {
            return KeyEvent.normalizeMetaState(metaState);
        }

        public final boolean metaStateHasModifiers(int metaState, int modifiers) {
            return KeyEvent.metaStateHasModifiers(metaState, modifiers);
        }

        public final boolean metaStateHasNoModifiers(int metaState) {
            return KeyEvent.metaStateHasNoModifiers(metaState);
        }

        public final boolean isCtrlPressed(KeyEvent event) {
            return event.isCtrlPressed();
        }
    }

    static {
        if (VERSION.SDK_INT >= 11) {
            IMPL = new HoneycombKeyEventVersionImpl();
        } else {
            IMPL = new BaseKeyEventVersionImpl();
        }
    }

    public static boolean hasModifiers(KeyEvent event, int modifiers) {
        return IMPL.metaStateHasModifiers(event.getMetaState(), 1);
    }

    public static boolean hasNoModifiers(KeyEvent event) {
        return IMPL.metaStateHasNoModifiers(event.getMetaState());
    }

    public static boolean isCtrlPressed(KeyEvent event) {
        return IMPL.isCtrlPressed(event);
    }
}
