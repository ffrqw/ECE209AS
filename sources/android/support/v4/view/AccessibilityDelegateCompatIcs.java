package android.support.v4.view;

import android.annotation.TargetApi;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

@TargetApi(14)
final class AccessibilityDelegateCompatIcs {

    public interface AccessibilityDelegateBridge {
        boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

        void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

        void onInitializeAccessibilityNodeInfo(View view, Object obj);

        void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

        boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent);

        void sendAccessibilityEvent(View view, int i);

        void sendAccessibilityEventUnchecked(View view, AccessibilityEvent accessibilityEvent);
    }

    /* renamed from: android.support.v4.view.AccessibilityDelegateCompatIcs$1 */
    static class AnonymousClass1 extends AccessibilityDelegate {
        final /* synthetic */ AccessibilityDelegateBridge val$bridge;

        AnonymousClass1(AccessibilityDelegateBridge accessibilityDelegateBridge) {
            this.val$bridge = accessibilityDelegateBridge;
        }

        public final boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            return this.val$bridge.dispatchPopulateAccessibilityEvent(host, event);
        }

        public final void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            this.val$bridge.onInitializeAccessibilityEvent(host, event);
        }

        public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            this.val$bridge.onInitializeAccessibilityNodeInfo(host, info);
        }

        public final void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            this.val$bridge.onPopulateAccessibilityEvent(host, event);
        }

        public final boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            return this.val$bridge.onRequestSendAccessibilityEvent(host, child, event);
        }

        public final void sendAccessibilityEvent(View host, int eventType) {
            this.val$bridge.sendAccessibilityEvent(host, eventType);
        }

        public final void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
            this.val$bridge.sendAccessibilityEventUnchecked(host, event);
        }
    }
}
