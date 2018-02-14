package android.support.v4.view;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.AccessibilityDelegateCompatIcs.AccessibilityDelegateBridge;
import android.support.v4.view.AccessibilityDelegateCompatJellyBean.AccessibilityDelegateBridgeJellyBean;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;

public class AccessibilityDelegateCompat {
    private static final Object DEFAULT_DELEGATE = IMPL.newAccessiblityDelegateDefaultImpl();
    private static final AccessibilityDelegateImpl IMPL;
    final Object mBridge = IMPL.newAccessiblityDelegateBridge(this);

    interface AccessibilityDelegateImpl {
        boolean dispatchPopulateAccessibilityEvent(Object obj, View view, AccessibilityEvent accessibilityEvent);

        AccessibilityNodeProviderCompat getAccessibilityNodeProvider(Object obj, View view);

        Object newAccessiblityDelegateBridge(AccessibilityDelegateCompat accessibilityDelegateCompat);

        Object newAccessiblityDelegateDefaultImpl();

        void onInitializeAccessibilityEvent(Object obj, View view, AccessibilityEvent accessibilityEvent);

        void onInitializeAccessibilityNodeInfo(Object obj, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat);

        void onPopulateAccessibilityEvent(Object obj, View view, AccessibilityEvent accessibilityEvent);

        boolean onRequestSendAccessibilityEvent(Object obj, ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent);

        boolean performAccessibilityAction(Object obj, View view, int i, Bundle bundle);

        void sendAccessibilityEvent(Object obj, View view, int i);

        void sendAccessibilityEventUnchecked(Object obj, View view, AccessibilityEvent accessibilityEvent);
    }

    static class AccessibilityDelegateStubImpl implements AccessibilityDelegateImpl {
        AccessibilityDelegateStubImpl() {
        }

        public Object newAccessiblityDelegateDefaultImpl() {
            return null;
        }

        public Object newAccessiblityDelegateBridge(AccessibilityDelegateCompat listener) {
            return null;
        }

        public boolean dispatchPopulateAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
            return false;
        }

        public void onInitializeAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
        }

        public void onInitializeAccessibilityNodeInfo(Object delegate, View host, AccessibilityNodeInfoCompat info) {
        }

        public void onPopulateAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
        }

        public boolean onRequestSendAccessibilityEvent(Object delegate, ViewGroup host, View child, AccessibilityEvent event) {
            return true;
        }

        public void sendAccessibilityEvent(Object delegate, View host, int eventType) {
        }

        public void sendAccessibilityEventUnchecked(Object delegate, View host, AccessibilityEvent event) {
        }

        public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(Object delegate, View host) {
            return null;
        }

        public boolean performAccessibilityAction(Object delegate, View host, int action, Bundle args) {
            return false;
        }
    }

    static class AccessibilityDelegateIcsImpl extends AccessibilityDelegateStubImpl {
        AccessibilityDelegateIcsImpl() {
        }

        public Object newAccessiblityDelegateBridge(final AccessibilityDelegateCompat compat) {
            return new android.support.v4.view.AccessibilityDelegateCompatIcs.AnonymousClass1(new AccessibilityDelegateBridge() {
                public final boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    return compat.dispatchPopulateAccessibilityEvent(host, event);
                }

                public final void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                    compat.onInitializeAccessibilityEvent(host, event);
                }

                public final void onInitializeAccessibilityNodeInfo(View host, Object info) {
                    compat.onInitializeAccessibilityNodeInfo(host, new AccessibilityNodeInfoCompat(info));
                }

                public final void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    compat.onPopulateAccessibilityEvent(host, event);
                }

                public final boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                    return compat.onRequestSendAccessibilityEvent(host, child, event);
                }

                public final void sendAccessibilityEvent(View host, int eventType) {
                    compat.sendAccessibilityEvent(host, eventType);
                }

                public final void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
                    compat.sendAccessibilityEventUnchecked(host, event);
                }
            });
        }

        public final void onInitializeAccessibilityNodeInfo(Object delegate, View host, AccessibilityNodeInfoCompat info) {
            ((AccessibilityDelegate) delegate).onInitializeAccessibilityNodeInfo(host, (AccessibilityNodeInfo) info.getInfo());
        }

        public final Object newAccessiblityDelegateDefaultImpl() {
            return new AccessibilityDelegate();
        }

        public final boolean dispatchPopulateAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
            return ((AccessibilityDelegate) delegate).dispatchPopulateAccessibilityEvent(host, event);
        }

        public final void onInitializeAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
            ((AccessibilityDelegate) delegate).onInitializeAccessibilityEvent(host, event);
        }

        public final void onPopulateAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
            ((AccessibilityDelegate) delegate).onPopulateAccessibilityEvent(host, event);
        }

        public final boolean onRequestSendAccessibilityEvent(Object delegate, ViewGroup host, View child, AccessibilityEvent event) {
            return ((AccessibilityDelegate) delegate).onRequestSendAccessibilityEvent(host, child, event);
        }

        public final void sendAccessibilityEvent(Object delegate, View host, int eventType) {
            ((AccessibilityDelegate) delegate).sendAccessibilityEvent(host, eventType);
        }

        public final void sendAccessibilityEventUnchecked(Object delegate, View host, AccessibilityEvent event) {
            ((AccessibilityDelegate) delegate).sendAccessibilityEventUnchecked(host, event);
        }
    }

    static class AccessibilityDelegateJellyBeanImpl extends AccessibilityDelegateIcsImpl {
        AccessibilityDelegateJellyBeanImpl() {
        }

        public final Object newAccessiblityDelegateBridge(final AccessibilityDelegateCompat compat) {
            return new android.support.v4.view.AccessibilityDelegateCompatJellyBean.AnonymousClass1(new AccessibilityDelegateBridgeJellyBean() {
                public final boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    return compat.dispatchPopulateAccessibilityEvent(host, event);
                }

                public final void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                    compat.onInitializeAccessibilityEvent(host, event);
                }

                public final void onInitializeAccessibilityNodeInfo(View host, Object info) {
                    compat.onInitializeAccessibilityNodeInfo(host, new AccessibilityNodeInfoCompat(info));
                }

                public final void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    compat.onPopulateAccessibilityEvent(host, event);
                }

                public final boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                    return compat.onRequestSendAccessibilityEvent(host, child, event);
                }

                public final void sendAccessibilityEvent(View host, int eventType) {
                    compat.sendAccessibilityEvent(host, eventType);
                }

                public final void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
                    compat.sendAccessibilityEventUnchecked(host, event);
                }

                public final Object getAccessibilityNodeProvider(View host) {
                    AccessibilityNodeProviderCompat provider = compat.getAccessibilityNodeProvider(host);
                    return provider != null ? provider.getProvider() : null;
                }

                public final boolean performAccessibilityAction(View host, int action, Bundle args) {
                    return compat.performAccessibilityAction(host, action, args);
                }
            });
        }

        public final AccessibilityNodeProviderCompat getAccessibilityNodeProvider(Object delegate, View host) {
            AccessibilityNodeProvider provider = ((AccessibilityDelegate) delegate).getAccessibilityNodeProvider(host);
            if (provider != null) {
                return new AccessibilityNodeProviderCompat(provider);
            }
            return null;
        }

        public final boolean performAccessibilityAction(Object delegate, View host, int action, Bundle args) {
            return ((AccessibilityDelegate) delegate).performAccessibilityAction(host, action, args);
        }
    }

    static {
        if (VERSION.SDK_INT >= 16) {
            IMPL = new AccessibilityDelegateJellyBeanImpl();
        } else if (VERSION.SDK_INT >= 14) {
            IMPL = new AccessibilityDelegateIcsImpl();
        } else {
            IMPL = new AccessibilityDelegateStubImpl();
        }
    }

    Object getBridge() {
        return this.mBridge;
    }

    public void sendAccessibilityEvent(View host, int eventType) {
        IMPL.sendAccessibilityEvent(DEFAULT_DELEGATE, host, eventType);
    }

    public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
        IMPL.sendAccessibilityEventUnchecked(DEFAULT_DELEGATE, host, event);
    }

    public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        return IMPL.dispatchPopulateAccessibilityEvent(DEFAULT_DELEGATE, host, event);
    }

    public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        IMPL.onPopulateAccessibilityEvent(DEFAULT_DELEGATE, host, event);
    }

    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        IMPL.onInitializeAccessibilityEvent(DEFAULT_DELEGATE, host, event);
    }

    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        IMPL.onInitializeAccessibilityNodeInfo(DEFAULT_DELEGATE, host, info);
    }

    public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
        return IMPL.onRequestSendAccessibilityEvent(DEFAULT_DELEGATE, host, child, event);
    }

    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View host) {
        return IMPL.getAccessibilityNodeProvider(DEFAULT_DELEGATE, host);
    }

    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        return IMPL.performAccessibilityAction(DEFAULT_DELEGATE, host, action, args);
    }
}
