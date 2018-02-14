package android.support.v7.widget;

import android.os.Bundle;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

public final class RecyclerViewAccessibilityDelegate extends AccessibilityDelegateCompat {
    final AccessibilityDelegateCompat mItemDelegate = new AccessibilityDelegateCompat() {
        public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            if (!RecyclerViewAccessibilityDelegate.this.shouldIgnore() && RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager() != null) {
                RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfoForItem(host, info);
            }
        }

        public final boolean performAccessibilityAction(View host, int action, Bundle args) {
            if (super.performAccessibilityAction(host, action, args)) {
                return true;
            }
            if (RecyclerViewAccessibilityDelegate.this.shouldIgnore() || RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager() == null) {
                return false;
            }
            return RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager().performAccessibilityActionForItem(host, action, args);
        }
    };
    final RecyclerView mRecyclerView;

    public RecyclerViewAccessibilityDelegate(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    final boolean shouldIgnore() {
        return this.mRecyclerView.hasPendingAdapterUpdates();
    }

    public final boolean performAccessibilityAction(View host, int action, Bundle args) {
        if (super.performAccessibilityAction(host, action, args)) {
            return true;
        }
        if (shouldIgnore() || this.mRecyclerView.getLayoutManager() == null) {
            return false;
        }
        return this.mRecyclerView.getLayoutManager().performAccessibilityAction(action, args);
    }

    public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        info.setClassName(RecyclerView.class.getName());
        if (!shouldIgnore() && this.mRecyclerView.getLayoutManager() != null) {
            this.mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfo(info);
        }
    }

    public final void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(host, event);
        event.setClassName(RecyclerView.class.getName());
        if ((host instanceof RecyclerView) && !shouldIgnore()) {
            RecyclerView rv = (RecyclerView) host;
            if (rv.getLayoutManager() != null) {
                rv.getLayoutManager().onInitializeAccessibilityEvent(event);
            }
        }
    }

    public final AccessibilityDelegateCompat getItemDelegate() {
        return this.mItemDelegate;
    }
}
