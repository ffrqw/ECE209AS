package android.support.v4.view.accessibility;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.util.List;

@TargetApi(19)
final class AccessibilityNodeProviderCompatKitKat {

    interface AccessibilityNodeInfoBridge {
        Object createAccessibilityNodeInfo(int i);

        List<Object> findAccessibilityNodeInfosByText(String str, int i);

        Object findFocus(int i);

        boolean performAction(int i, int i2, Bundle bundle);
    }

    /* renamed from: android.support.v4.view.accessibility.AccessibilityNodeProviderCompatKitKat$1 */
    static class AnonymousClass1 extends AccessibilityNodeProvider {
        final /* synthetic */ AccessibilityNodeInfoBridge val$bridge;

        AnonymousClass1(AccessibilityNodeInfoBridge accessibilityNodeInfoBridge) {
            this.val$bridge = accessibilityNodeInfoBridge;
        }

        public final AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
            return (AccessibilityNodeInfo) this.val$bridge.createAccessibilityNodeInfo(virtualViewId);
        }

        public final List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String text, int virtualViewId) {
            return this.val$bridge.findAccessibilityNodeInfosByText(text, virtualViewId);
        }

        public final boolean performAction(int virtualViewId, int action, Bundle arguments) {
            return this.val$bridge.performAction(virtualViewId, action, arguments);
        }

        public final AccessibilityNodeInfo findFocus(int focus) {
            return (AccessibilityNodeInfo) this.val$bridge.findFocus(focus);
        }
    }
}
