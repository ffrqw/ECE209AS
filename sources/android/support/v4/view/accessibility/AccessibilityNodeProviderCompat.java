package android.support.v4.view.accessibility;

import android.os.Build.VERSION;
import android.os.Bundle;
import java.util.List;

public final class AccessibilityNodeProviderCompat {
    private static final AccessibilityNodeProviderImpl IMPL;
    private final Object mProvider;

    interface AccessibilityNodeProviderImpl {
        Object newAccessibilityNodeProviderBridge(AccessibilityNodeProviderCompat accessibilityNodeProviderCompat);
    }

    static class AccessibilityNodeProviderStubImpl implements AccessibilityNodeProviderImpl {
        AccessibilityNodeProviderStubImpl() {
        }

        public Object newAccessibilityNodeProviderBridge(AccessibilityNodeProviderCompat compat) {
            return null;
        }
    }

    private static class AccessibilityNodeProviderJellyBeanImpl extends AccessibilityNodeProviderStubImpl {
        AccessibilityNodeProviderJellyBeanImpl() {
        }

        public final Object newAccessibilityNodeProviderBridge(final AccessibilityNodeProviderCompat compat) {
            return new android.support.v4.view.accessibility.AccessibilityNodeProviderCompatJellyBean.AnonymousClass1(new AccessibilityNodeInfoBridge() {
                public final boolean performAction(int virtualViewId, int action, Bundle arguments) {
                    return AccessibilityNodeProviderCompat.performAction$5985f823();
                }

                public final List<Object> findAccessibilityNodeInfosByText(String text, int virtualViewId) {
                    AccessibilityNodeProviderCompat.findAccessibilityNodeInfosByText$2393931d();
                    return null;
                }

                public final Object createAccessibilityNodeInfo(int virtualViewId) {
                    AccessibilityNodeProviderCompat.createAccessibilityNodeInfo$f3a5639();
                    return null;
                }
            });
        }
    }

    private static class AccessibilityNodeProviderKitKatImpl extends AccessibilityNodeProviderStubImpl {
        AccessibilityNodeProviderKitKatImpl() {
        }

        public final Object newAccessibilityNodeProviderBridge(final AccessibilityNodeProviderCompat compat) {
            return new android.support.v4.view.accessibility.AccessibilityNodeProviderCompatKitKat.AnonymousClass1(new AccessibilityNodeInfoBridge() {
                public final boolean performAction(int virtualViewId, int action, Bundle arguments) {
                    return AccessibilityNodeProviderCompat.performAction$5985f823();
                }

                public final List<Object> findAccessibilityNodeInfosByText(String text, int virtualViewId) {
                    AccessibilityNodeProviderCompat.findAccessibilityNodeInfosByText$2393931d();
                    return null;
                }

                public final Object createAccessibilityNodeInfo(int virtualViewId) {
                    AccessibilityNodeProviderCompat.createAccessibilityNodeInfo$f3a5639();
                    return null;
                }

                public final Object findFocus(int focus) {
                    AccessibilityNodeProviderCompat.findFocus$f3a5639();
                    return null;
                }
            });
        }
    }

    static {
        if (VERSION.SDK_INT >= 19) {
            IMPL = new AccessibilityNodeProviderKitKatImpl();
        } else if (VERSION.SDK_INT >= 16) {
            IMPL = new AccessibilityNodeProviderJellyBeanImpl();
        } else {
            IMPL = new AccessibilityNodeProviderStubImpl();
        }
    }

    public AccessibilityNodeProviderCompat() {
        this.mProvider = IMPL.newAccessibilityNodeProviderBridge(this);
    }

    public AccessibilityNodeProviderCompat(Object provider) {
        this.mProvider = provider;
    }

    public final Object getProvider() {
        return this.mProvider;
    }

    public static AccessibilityNodeInfoCompat createAccessibilityNodeInfo$f3a5639() {
        return null;
    }

    public static boolean performAction$5985f823() {
        return false;
    }

    public static List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText$2393931d() {
        return null;
    }

    public static AccessibilityNodeInfoCompat findFocus$f3a5639() {
        return null;
    }
}
