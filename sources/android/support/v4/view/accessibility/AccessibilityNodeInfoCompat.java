package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;

public final class AccessibilityNodeInfoCompat {
    static final AccessibilityNodeInfoImpl IMPL;
    private final Object mInfo;
    public int mParentVirtualDescendantId = -1;

    public static class AccessibilityActionCompat {
        public static final AccessibilityActionCompat ACTION_ACCESSIBILITY_FOCUS = new AccessibilityActionCompat(64, null);
        public static final AccessibilityActionCompat ACTION_CLEAR_ACCESSIBILITY_FOCUS = new AccessibilityActionCompat(128, null);
        public static final AccessibilityActionCompat ACTION_CLEAR_FOCUS = new AccessibilityActionCompat(2, null);
        public static final AccessibilityActionCompat ACTION_CLEAR_SELECTION = new AccessibilityActionCompat(8, null);
        public static final AccessibilityActionCompat ACTION_CLICK = new AccessibilityActionCompat(16, null);
        public static final AccessibilityActionCompat ACTION_COLLAPSE = new AccessibilityActionCompat(524288, null);
        public static final AccessibilityActionCompat ACTION_CONTEXT_CLICK = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionContextClick());
        public static final AccessibilityActionCompat ACTION_COPY = new AccessibilityActionCompat(16384, null);
        public static final AccessibilityActionCompat ACTION_CUT = new AccessibilityActionCompat(65536, null);
        public static final AccessibilityActionCompat ACTION_DISMISS = new AccessibilityActionCompat(1048576, null);
        public static final AccessibilityActionCompat ACTION_EXPAND = new AccessibilityActionCompat(262144, null);
        public static final AccessibilityActionCompat ACTION_FOCUS = new AccessibilityActionCompat(1, null);
        public static final AccessibilityActionCompat ACTION_LONG_CLICK = new AccessibilityActionCompat(32, null);
        public static final AccessibilityActionCompat ACTION_NEXT_AT_MOVEMENT_GRANULARITY = new AccessibilityActionCompat(256, null);
        public static final AccessibilityActionCompat ACTION_NEXT_HTML_ELEMENT = new AccessibilityActionCompat(1024, null);
        public static final AccessibilityActionCompat ACTION_PASTE = new AccessibilityActionCompat(32768, null);
        public static final AccessibilityActionCompat ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = new AccessibilityActionCompat(512, null);
        public static final AccessibilityActionCompat ACTION_PREVIOUS_HTML_ELEMENT = new AccessibilityActionCompat(ItemAnimator.FLAG_MOVED, null);
        public static final AccessibilityActionCompat ACTION_SCROLL_BACKWARD = new AccessibilityActionCompat(8192, null);
        public static final AccessibilityActionCompat ACTION_SCROLL_DOWN = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollDown());
        public static final AccessibilityActionCompat ACTION_SCROLL_FORWARD = new AccessibilityActionCompat(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, null);
        public static final AccessibilityActionCompat ACTION_SCROLL_LEFT = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollLeft());
        public static final AccessibilityActionCompat ACTION_SCROLL_RIGHT = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollRight());
        public static final AccessibilityActionCompat ACTION_SCROLL_TO_POSITION = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollToPosition());
        public static final AccessibilityActionCompat ACTION_SCROLL_UP = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollUp());
        public static final AccessibilityActionCompat ACTION_SELECT = new AccessibilityActionCompat(4, null);
        public static final AccessibilityActionCompat ACTION_SET_PROGRESS = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionSetProgress());
        public static final AccessibilityActionCompat ACTION_SET_SELECTION = new AccessibilityActionCompat(131072, null);
        public static final AccessibilityActionCompat ACTION_SET_TEXT = new AccessibilityActionCompat(2097152, null);
        public static final AccessibilityActionCompat ACTION_SHOW_ON_SCREEN = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionShowOnScreen());
        final Object mAction;

        private AccessibilityActionCompat(int actionId, CharSequence label) {
            this(AccessibilityNodeInfoCompat.IMPL.newAccessibilityAction(actionId, null));
        }

        private AccessibilityActionCompat(Object action) {
            this.mAction = action;
        }
    }

    interface AccessibilityNodeInfoImpl {
        void addAction(Object obj, int i);

        void addChild(Object obj, View view);

        Object getActionContextClick();

        Object getActionScrollDown();

        Object getActionScrollLeft();

        Object getActionScrollRight();

        Object getActionScrollToPosition();

        Object getActionScrollUp();

        Object getActionSetProgress();

        Object getActionShowOnScreen();

        int getActions(Object obj);

        void getBoundsInParent(Object obj, Rect rect);

        void getBoundsInScreen(Object obj, Rect rect);

        CharSequence getClassName(Object obj);

        CharSequence getContentDescription(Object obj);

        CharSequence getPackageName(Object obj);

        CharSequence getText(Object obj);

        String getViewIdResourceName(Object obj);

        boolean isAccessibilityFocused(Object obj);

        boolean isCheckable(Object obj);

        boolean isChecked(Object obj);

        boolean isClickable(Object obj);

        boolean isEnabled(Object obj);

        boolean isFocusable(Object obj);

        boolean isFocused(Object obj);

        boolean isLongClickable(Object obj);

        boolean isPassword(Object obj);

        boolean isScrollable(Object obj);

        boolean isSelected(Object obj);

        boolean isVisibleToUser(Object obj);

        Object newAccessibilityAction(int i, CharSequence charSequence);

        Object obtain(Object obj);

        Object obtainCollectionInfo(int i, int i2, boolean z, int i3);

        Object obtainCollectionItemInfo(int i, int i2, int i3, int i4, boolean z, boolean z2);

        void recycle(Object obj);

        boolean removeAction(Object obj, Object obj2);

        void setAccessibilityFocused(Object obj, boolean z);

        void setBoundsInParent(Object obj, Rect rect);

        void setBoundsInScreen(Object obj, Rect rect);

        void setCheckable(Object obj, boolean z);

        void setChecked(Object obj, boolean z);

        void setClassName(Object obj, CharSequence charSequence);

        void setClickable(Object obj, boolean z);

        void setCollectionInfo(Object obj, Object obj2);

        void setCollectionItemInfo(Object obj, Object obj2);

        void setContentDescription(Object obj, CharSequence charSequence);

        void setEnabled(Object obj, boolean z);

        void setFocusable(Object obj, boolean z);

        void setFocused(Object obj, boolean z);

        void setLongClickable(Object obj, boolean z);

        void setPackageName(Object obj, CharSequence charSequence);

        void setParent(Object obj, View view);

        void setScrollable(Object obj, boolean z);

        void setSelected(Object obj, boolean z);

        void setSource(Object obj, View view);

        void setVisibleToUser(Object obj, boolean z);
    }

    static class AccessibilityNodeInfoStubImpl implements AccessibilityNodeInfoImpl {
        AccessibilityNodeInfoStubImpl() {
        }

        public Object newAccessibilityAction(int actionId, CharSequence label) {
            return null;
        }

        public Object obtain(Object info) {
            return null;
        }

        public void addAction(Object info, int action) {
        }

        public boolean removeAction(Object info, Object action) {
            return false;
        }

        public void addChild(Object info, View child) {
        }

        public int getActions(Object info) {
            return 0;
        }

        public void getBoundsInParent(Object info, Rect outBounds) {
        }

        public void getBoundsInScreen(Object info, Rect outBounds) {
        }

        public CharSequence getClassName(Object info) {
            return null;
        }

        public CharSequence getContentDescription(Object info) {
            return null;
        }

        public CharSequence getPackageName(Object info) {
            return null;
        }

        public CharSequence getText(Object info) {
            return null;
        }

        public boolean isCheckable(Object info) {
            return false;
        }

        public boolean isChecked(Object info) {
            return false;
        }

        public boolean isClickable(Object info) {
            return false;
        }

        public boolean isEnabled(Object info) {
            return false;
        }

        public boolean isFocusable(Object info) {
            return false;
        }

        public boolean isFocused(Object info) {
            return false;
        }

        public boolean isVisibleToUser(Object info) {
            return false;
        }

        public boolean isAccessibilityFocused(Object info) {
            return false;
        }

        public boolean isLongClickable(Object info) {
            return false;
        }

        public boolean isPassword(Object info) {
            return false;
        }

        public boolean isScrollable(Object info) {
            return false;
        }

        public boolean isSelected(Object info) {
            return false;
        }

        public void setBoundsInParent(Object info, Rect bounds) {
        }

        public void setBoundsInScreen(Object info, Rect bounds) {
        }

        public void setCheckable(Object info, boolean checkable) {
        }

        public void setChecked(Object info, boolean checked) {
        }

        public void setClassName(Object info, CharSequence className) {
        }

        public void setClickable(Object info, boolean clickable) {
        }

        public void setContentDescription(Object info, CharSequence contentDescription) {
        }

        public void setEnabled(Object info, boolean enabled) {
        }

        public void setFocusable(Object info, boolean focusable) {
        }

        public void setFocused(Object info, boolean focused) {
        }

        public void setVisibleToUser(Object info, boolean visibleToUser) {
        }

        public void setAccessibilityFocused(Object info, boolean focused) {
        }

        public void setLongClickable(Object info, boolean longClickable) {
        }

        public void setPackageName(Object info, CharSequence packageName) {
        }

        public void setParent(Object info, View parent) {
        }

        public void setScrollable(Object info, boolean scrollable) {
        }

        public void setSelected(Object info, boolean selected) {
        }

        public void setSource(Object info, View source) {
        }

        public void recycle(Object info) {
        }

        public String getViewIdResourceName(Object info) {
            return null;
        }

        public void setCollectionInfo(Object info, Object collectionInfo) {
        }

        public void setCollectionItemInfo(Object info, Object collectionItemInfo) {
        }

        public Object obtainCollectionInfo(int rowCount, int columnCount, boolean hierarchical, int selectionMode) {
            return null;
        }

        public Object obtainCollectionItemInfo(int rowIndex, int rowSpan, int columnIndex, int columnSpan, boolean heading, boolean selected) {
            return null;
        }

        public Object getActionScrollToPosition() {
            return null;
        }

        public Object getActionSetProgress() {
            return null;
        }

        public Object getActionShowOnScreen() {
            return null;
        }

        public Object getActionScrollUp() {
            return null;
        }

        public Object getActionScrollDown() {
            return null;
        }

        public Object getActionScrollLeft() {
            return null;
        }

        public Object getActionScrollRight() {
            return null;
        }

        public Object getActionContextClick() {
            return null;
        }
    }

    static class AccessibilityNodeInfoIcsImpl extends AccessibilityNodeInfoStubImpl {
        AccessibilityNodeInfoIcsImpl() {
        }

        public final Object obtain(Object info) {
            return AccessibilityNodeInfo.obtain((AccessibilityNodeInfo) info);
        }

        public final void addAction(Object info, int action) {
            ((AccessibilityNodeInfo) info).addAction(action);
        }

        public final void addChild(Object info, View child) {
            ((AccessibilityNodeInfo) info).addChild(child);
        }

        public final int getActions(Object info) {
            return ((AccessibilityNodeInfo) info).getActions();
        }

        public final void getBoundsInParent(Object info, Rect outBounds) {
            ((AccessibilityNodeInfo) info).getBoundsInParent(outBounds);
        }

        public final void getBoundsInScreen(Object info, Rect outBounds) {
            ((AccessibilityNodeInfo) info).getBoundsInScreen(outBounds);
        }

        public final CharSequence getClassName(Object info) {
            return ((AccessibilityNodeInfo) info).getClassName();
        }

        public final CharSequence getContentDescription(Object info) {
            return ((AccessibilityNodeInfo) info).getContentDescription();
        }

        public final CharSequence getPackageName(Object info) {
            return ((AccessibilityNodeInfo) info).getPackageName();
        }

        public final CharSequence getText(Object info) {
            return ((AccessibilityNodeInfo) info).getText();
        }

        public final boolean isCheckable(Object info) {
            return ((AccessibilityNodeInfo) info).isCheckable();
        }

        public final boolean isChecked(Object info) {
            return ((AccessibilityNodeInfo) info).isChecked();
        }

        public final boolean isClickable(Object info) {
            return ((AccessibilityNodeInfo) info).isClickable();
        }

        public final boolean isEnabled(Object info) {
            return ((AccessibilityNodeInfo) info).isEnabled();
        }

        public final boolean isFocusable(Object info) {
            return ((AccessibilityNodeInfo) info).isFocusable();
        }

        public final boolean isFocused(Object info) {
            return ((AccessibilityNodeInfo) info).isFocused();
        }

        public final boolean isLongClickable(Object info) {
            return ((AccessibilityNodeInfo) info).isLongClickable();
        }

        public final boolean isPassword(Object info) {
            return ((AccessibilityNodeInfo) info).isPassword();
        }

        public final boolean isScrollable(Object info) {
            return ((AccessibilityNodeInfo) info).isScrollable();
        }

        public final boolean isSelected(Object info) {
            return ((AccessibilityNodeInfo) info).isSelected();
        }

        public final void setBoundsInParent(Object info, Rect bounds) {
            ((AccessibilityNodeInfo) info).setBoundsInParent(bounds);
        }

        public final void setBoundsInScreen(Object info, Rect bounds) {
            ((AccessibilityNodeInfo) info).setBoundsInScreen(bounds);
        }

        public final void setCheckable(Object info, boolean checkable) {
            ((AccessibilityNodeInfo) info).setCheckable(checkable);
        }

        public final void setChecked(Object info, boolean checked) {
            ((AccessibilityNodeInfo) info).setChecked(checked);
        }

        public final void setClassName(Object info, CharSequence className) {
            ((AccessibilityNodeInfo) info).setClassName(className);
        }

        public final void setClickable(Object info, boolean clickable) {
            ((AccessibilityNodeInfo) info).setClickable(clickable);
        }

        public final void setContentDescription(Object info, CharSequence contentDescription) {
            ((AccessibilityNodeInfo) info).setContentDescription(contentDescription);
        }

        public final void setEnabled(Object info, boolean enabled) {
            ((AccessibilityNodeInfo) info).setEnabled(enabled);
        }

        public final void setFocusable(Object info, boolean focusable) {
            ((AccessibilityNodeInfo) info).setFocusable(focusable);
        }

        public final void setFocused(Object info, boolean focused) {
            ((AccessibilityNodeInfo) info).setFocused(focused);
        }

        public final void setLongClickable(Object info, boolean longClickable) {
            ((AccessibilityNodeInfo) info).setLongClickable(longClickable);
        }

        public final void setPackageName(Object info, CharSequence packageName) {
            ((AccessibilityNodeInfo) info).setPackageName(packageName);
        }

        public final void setParent(Object info, View parent) {
            ((AccessibilityNodeInfo) info).setParent(parent);
        }

        public final void setScrollable(Object info, boolean scrollable) {
            ((AccessibilityNodeInfo) info).setScrollable(scrollable);
        }

        public final void setSelected(Object info, boolean selected) {
            ((AccessibilityNodeInfo) info).setSelected(selected);
        }

        public final void setSource(Object info, View source) {
            ((AccessibilityNodeInfo) info).setSource(source);
        }

        public final void recycle(Object info) {
            ((AccessibilityNodeInfo) info).recycle();
        }
    }

    static class AccessibilityNodeInfoJellybeanImpl extends AccessibilityNodeInfoIcsImpl {
        AccessibilityNodeInfoJellybeanImpl() {
        }

        public final boolean isVisibleToUser(Object info) {
            return ((AccessibilityNodeInfo) info).isVisibleToUser();
        }

        public final void setVisibleToUser(Object info, boolean visibleToUser) {
            ((AccessibilityNodeInfo) info).setVisibleToUser(visibleToUser);
        }

        public final boolean isAccessibilityFocused(Object info) {
            return ((AccessibilityNodeInfo) info).isAccessibilityFocused();
        }

        public final void setAccessibilityFocused(Object info, boolean focused) {
            ((AccessibilityNodeInfo) info).setAccessibilityFocused(focused);
        }
    }

    static class AccessibilityNodeInfoJellybeanMr1Impl extends AccessibilityNodeInfoJellybeanImpl {
        AccessibilityNodeInfoJellybeanMr1Impl() {
        }
    }

    static class AccessibilityNodeInfoJellybeanMr2Impl extends AccessibilityNodeInfoJellybeanMr1Impl {
        AccessibilityNodeInfoJellybeanMr2Impl() {
        }

        public final String getViewIdResourceName(Object info) {
            return ((AccessibilityNodeInfo) info).getViewIdResourceName();
        }
    }

    static class AccessibilityNodeInfoKitKatImpl extends AccessibilityNodeInfoJellybeanMr2Impl {
        AccessibilityNodeInfoKitKatImpl() {
        }

        public final void setCollectionInfo(Object info, Object collectionInfo) {
            ((AccessibilityNodeInfo) info).setCollectionInfo((CollectionInfo) collectionInfo);
        }

        public Object obtainCollectionInfo(int rowCount, int columnCount, boolean hierarchical, int selectionMode) {
            return CollectionInfo.obtain(rowCount, columnCount, hierarchical);
        }

        public Object obtainCollectionItemInfo(int rowIndex, int rowSpan, int columnIndex, int columnSpan, boolean heading, boolean selected) {
            return CollectionItemInfo.obtain(rowIndex, rowSpan, columnIndex, columnSpan, heading);
        }

        public final void setCollectionItemInfo(Object info, Object collectionItemInfo) {
            ((AccessibilityNodeInfo) info).setCollectionItemInfo((CollectionItemInfo) collectionItemInfo);
        }
    }

    static class AccessibilityNodeInfoApi21Impl extends AccessibilityNodeInfoKitKatImpl {
        AccessibilityNodeInfoApi21Impl() {
        }

        public final Object newAccessibilityAction(int actionId, CharSequence label) {
            return new AccessibilityAction(actionId, label);
        }

        public final Object obtainCollectionInfo(int rowCount, int columnCount, boolean hierarchical, int selectionMode) {
            return CollectionInfo.obtain(rowCount, columnCount, hierarchical, selectionMode);
        }

        public final boolean removeAction(Object info, Object action) {
            return ((AccessibilityNodeInfo) info).removeAction((AccessibilityAction) action);
        }

        public final Object obtainCollectionItemInfo(int rowIndex, int rowSpan, int columnIndex, int columnSpan, boolean heading, boolean selected) {
            return CollectionItemInfo.obtain(rowIndex, rowSpan, columnIndex, columnSpan, heading, selected);
        }
    }

    static class AccessibilityNodeInfoApi22Impl extends AccessibilityNodeInfoApi21Impl {
        AccessibilityNodeInfoApi22Impl() {
        }
    }

    static class AccessibilityNodeInfoApi23Impl extends AccessibilityNodeInfoApi22Impl {
        AccessibilityNodeInfoApi23Impl() {
        }

        public final Object getActionScrollToPosition() {
            return AccessibilityAction.ACTION_SCROLL_TO_POSITION;
        }

        public final Object getActionShowOnScreen() {
            return AccessibilityAction.ACTION_SHOW_ON_SCREEN;
        }

        public final Object getActionScrollUp() {
            return AccessibilityAction.ACTION_SCROLL_UP;
        }

        public final Object getActionScrollDown() {
            return AccessibilityAction.ACTION_SCROLL_DOWN;
        }

        public final Object getActionScrollLeft() {
            return AccessibilityAction.ACTION_SCROLL_LEFT;
        }

        public final Object getActionScrollRight() {
            return AccessibilityAction.ACTION_SCROLL_RIGHT;
        }

        public final Object getActionContextClick() {
            return AccessibilityAction.ACTION_CONTEXT_CLICK;
        }
    }

    static class AccessibilityNodeInfoApi24Impl extends AccessibilityNodeInfoApi23Impl {
        AccessibilityNodeInfoApi24Impl() {
        }

        public final Object getActionSetProgress() {
            return AccessibilityAction.ACTION_SET_PROGRESS;
        }
    }

    public static class CollectionInfoCompat {
        final Object mInfo;

        public static CollectionInfoCompat obtain(int rowCount, int columnCount, boolean hierarchical, int selectionMode) {
            return new CollectionInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainCollectionInfo(rowCount, columnCount, hierarchical, selectionMode));
        }

        private CollectionInfoCompat(Object info) {
            this.mInfo = info;
        }
    }

    public static class CollectionItemInfoCompat {
        final Object mInfo;

        public static CollectionItemInfoCompat obtain(int rowIndex, int rowSpan, int columnIndex, int columnSpan, boolean heading, boolean selected) {
            return new CollectionItemInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainCollectionItemInfo(rowIndex, rowSpan, columnIndex, columnSpan, heading, false));
        }

        private CollectionItemInfoCompat(Object info) {
            this.mInfo = info;
        }
    }

    static {
        if (VERSION.SDK_INT >= 24) {
            IMPL = new AccessibilityNodeInfoApi24Impl();
        } else if (VERSION.SDK_INT >= 23) {
            IMPL = new AccessibilityNodeInfoApi23Impl();
        } else if (VERSION.SDK_INT >= 22) {
            IMPL = new AccessibilityNodeInfoApi22Impl();
        } else if (VERSION.SDK_INT >= 21) {
            IMPL = new AccessibilityNodeInfoApi21Impl();
        } else if (VERSION.SDK_INT >= 19) {
            IMPL = new AccessibilityNodeInfoKitKatImpl();
        } else if (VERSION.SDK_INT >= 18) {
            IMPL = new AccessibilityNodeInfoJellybeanMr2Impl();
        } else if (VERSION.SDK_INT >= 17) {
            IMPL = new AccessibilityNodeInfoJellybeanMr1Impl();
        } else if (VERSION.SDK_INT >= 16) {
            IMPL = new AccessibilityNodeInfoJellybeanImpl();
        } else if (VERSION.SDK_INT >= 14) {
            IMPL = new AccessibilityNodeInfoIcsImpl();
        } else {
            IMPL = new AccessibilityNodeInfoStubImpl();
        }
    }

    public AccessibilityNodeInfoCompat(Object info) {
        this.mInfo = info;
    }

    public final Object getInfo() {
        return this.mInfo;
    }

    public static AccessibilityNodeInfoCompat obtain(AccessibilityNodeInfoCompat info) {
        Object obtain = IMPL.obtain(info.mInfo);
        if (obtain != null) {
            return new AccessibilityNodeInfoCompat(obtain);
        }
        return null;
    }

    public final void setSource(View source) {
        IMPL.setSource(this.mInfo, source);
    }

    public final void addChild(View child) {
        IMPL.addChild(this.mInfo, child);
    }

    public final int getActions() {
        return IMPL.getActions(this.mInfo);
    }

    public final void addAction(int action) {
        IMPL.addAction(this.mInfo, action);
    }

    public final boolean removeAction(AccessibilityActionCompat action) {
        return IMPL.removeAction(this.mInfo, action.mAction);
    }

    public final void setParent(View parent) {
        IMPL.setParent(this.mInfo, parent);
    }

    public final void getBoundsInParent(Rect outBounds) {
        IMPL.getBoundsInParent(this.mInfo, outBounds);
    }

    public final void setBoundsInParent(Rect bounds) {
        IMPL.setBoundsInParent(this.mInfo, bounds);
    }

    public final void getBoundsInScreen(Rect outBounds) {
        IMPL.getBoundsInScreen(this.mInfo, outBounds);
    }

    public final void setBoundsInScreen(Rect bounds) {
        IMPL.setBoundsInScreen(this.mInfo, bounds);
    }

    public final void setCheckable(boolean checkable) {
        IMPL.setCheckable(this.mInfo, checkable);
    }

    public final void setChecked(boolean checked) {
        IMPL.setChecked(this.mInfo, checked);
    }

    public final boolean isFocusable() {
        return IMPL.isFocusable(this.mInfo);
    }

    public final void setFocusable(boolean focusable) {
        IMPL.setFocusable(this.mInfo, focusable);
    }

    public final boolean isFocused() {
        return IMPL.isFocused(this.mInfo);
    }

    public final void setFocused(boolean focused) {
        IMPL.setFocused(this.mInfo, focused);
    }

    public final boolean isVisibleToUser() {
        return IMPL.isVisibleToUser(this.mInfo);
    }

    public final void setVisibleToUser(boolean visibleToUser) {
        IMPL.setVisibleToUser(this.mInfo, visibleToUser);
    }

    public final boolean isAccessibilityFocused() {
        return IMPL.isAccessibilityFocused(this.mInfo);
    }

    public final void setAccessibilityFocused(boolean focused) {
        IMPL.setAccessibilityFocused(this.mInfo, focused);
    }

    public final boolean isSelected() {
        return IMPL.isSelected(this.mInfo);
    }

    public final void setSelected(boolean selected) {
        IMPL.setSelected(this.mInfo, selected);
    }

    public final boolean isClickable() {
        return IMPL.isClickable(this.mInfo);
    }

    public final void setClickable(boolean clickable) {
        IMPL.setClickable(this.mInfo, clickable);
    }

    public final boolean isLongClickable() {
        return IMPL.isLongClickable(this.mInfo);
    }

    public final void setLongClickable(boolean longClickable) {
        IMPL.setLongClickable(this.mInfo, longClickable);
    }

    public final boolean isEnabled() {
        return IMPL.isEnabled(this.mInfo);
    }

    public final void setEnabled(boolean enabled) {
        IMPL.setEnabled(this.mInfo, enabled);
    }

    public final void setScrollable(boolean scrollable) {
        IMPL.setScrollable(this.mInfo, scrollable);
    }

    public final CharSequence getPackageName() {
        return IMPL.getPackageName(this.mInfo);
    }

    public final void setPackageName(CharSequence packageName) {
        IMPL.setPackageName(this.mInfo, packageName);
    }

    public final CharSequence getClassName() {
        return IMPL.getClassName(this.mInfo);
    }

    public final void setClassName(CharSequence className) {
        IMPL.setClassName(this.mInfo, className);
    }

    public final CharSequence getContentDescription() {
        return IMPL.getContentDescription(this.mInfo);
    }

    public final void setContentDescription(CharSequence contentDescription) {
        IMPL.setContentDescription(this.mInfo, contentDescription);
    }

    public final void recycle() {
        IMPL.recycle(this.mInfo);
    }

    public final void setCollectionInfo(Object collectionInfo) {
        IMPL.setCollectionInfo(this.mInfo, ((CollectionInfoCompat) collectionInfo).mInfo);
    }

    public final void setCollectionItemInfo(Object collectionItemInfo) {
        IMPL.setCollectionItemInfo(this.mInfo, ((CollectionItemInfoCompat) collectionItemInfo).mInfo);
    }

    public final int hashCode() {
        return this.mInfo == null ? 0 : this.mInfo.hashCode();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccessibilityNodeInfoCompat other = (AccessibilityNodeInfoCompat) obj;
        if (this.mInfo == null) {
            if (other.mInfo != null) {
                return false;
            }
            return true;
        } else if (this.mInfo.equals(other.mInfo)) {
            return true;
        } else {
            return false;
        }
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        Rect bounds = new Rect();
        getBoundsInParent(bounds);
        builder.append("; boundsInParent: " + bounds);
        getBoundsInScreen(bounds);
        builder.append("; boundsInScreen: " + bounds);
        builder.append("; packageName: ").append(getPackageName());
        builder.append("; className: ").append(getClassName());
        builder.append("; text: ").append(IMPL.getText(this.mInfo));
        builder.append("; contentDescription: ").append(getContentDescription());
        builder.append("; viewId: ").append(IMPL.getViewIdResourceName(this.mInfo));
        builder.append("; checkable: ").append(IMPL.isCheckable(this.mInfo));
        builder.append("; checked: ").append(IMPL.isChecked(this.mInfo));
        builder.append("; focusable: ").append(isFocusable());
        builder.append("; focused: ").append(isFocused());
        builder.append("; selected: ").append(isSelected());
        builder.append("; clickable: ").append(isClickable());
        builder.append("; longClickable: ").append(isLongClickable());
        builder.append("; enabled: ").append(isEnabled());
        builder.append("; password: ").append(IMPL.isPassword(this.mInfo));
        builder.append("; scrollable: " + IMPL.isScrollable(this.mInfo));
        builder.append("; [");
        int actionBits = getActions();
        while (actionBits != 0) {
            String str;
            int action = 1 << Integer.numberOfTrailingZeros(actionBits);
            actionBits &= action ^ -1;
            switch (action) {
                case 1:
                    str = "ACTION_FOCUS";
                    break;
                case 2:
                    str = "ACTION_CLEAR_FOCUS";
                    break;
                case 4:
                    str = "ACTION_SELECT";
                    break;
                case 8:
                    str = "ACTION_CLEAR_SELECTION";
                    break;
                case 16:
                    str = "ACTION_CLICK";
                    break;
                case 32:
                    str = "ACTION_LONG_CLICK";
                    break;
                case 64:
                    str = "ACTION_ACCESSIBILITY_FOCUS";
                    break;
                case 128:
                    str = "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
                    break;
                case 256:
                    str = "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
                    break;
                case 512:
                    str = "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
                    break;
                case 1024:
                    str = "ACTION_NEXT_HTML_ELEMENT";
                    break;
                case ItemAnimator.FLAG_MOVED /*2048*/:
                    str = "ACTION_PREVIOUS_HTML_ELEMENT";
                    break;
                case ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT /*4096*/:
                    str = "ACTION_SCROLL_FORWARD";
                    break;
                case 8192:
                    str = "ACTION_SCROLL_BACKWARD";
                    break;
                case 16384:
                    str = "ACTION_COPY";
                    break;
                case 32768:
                    str = "ACTION_PASTE";
                    break;
                case 65536:
                    str = "ACTION_CUT";
                    break;
                case 131072:
                    str = "ACTION_SET_SELECTION";
                    break;
                default:
                    str = "ACTION_UNKNOWN";
                    break;
            }
            builder.append(str);
            if (actionBits != 0) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
