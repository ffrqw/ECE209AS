package android.support.v4.view.accessibility;

import android.os.Build.VERSION;
import android.view.accessibility.AccessibilityRecord;

public final class AccessibilityRecordCompat {
    private static final AccessibilityRecordImpl IMPL;
    private final Object mRecord;

    interface AccessibilityRecordImpl {
        void setFromIndex(Object obj, int i);

        void setItemCount(Object obj, int i);

        void setMaxScrollX(Object obj, int i);

        void setMaxScrollY(Object obj, int i);

        void setScrollX(Object obj, int i);

        void setScrollY(Object obj, int i);

        void setScrollable(Object obj, boolean z);

        void setToIndex(Object obj, int i);
    }

    static class AccessibilityRecordStubImpl implements AccessibilityRecordImpl {
        AccessibilityRecordStubImpl() {
        }

        public void setFromIndex(Object record, int fromIndex) {
        }

        public void setItemCount(Object record, int itemCount) {
        }

        public void setMaxScrollX(Object record, int maxScrollX) {
        }

        public void setMaxScrollY(Object record, int maxScrollY) {
        }

        public void setScrollX(Object record, int scrollX) {
        }

        public void setScrollY(Object record, int scrollY) {
        }

        public void setScrollable(Object record, boolean scrollable) {
        }

        public void setToIndex(Object record, int toIndex) {
        }
    }

    static class AccessibilityRecordIcsImpl extends AccessibilityRecordStubImpl {
        AccessibilityRecordIcsImpl() {
        }

        public final void setFromIndex(Object record, int fromIndex) {
            ((AccessibilityRecord) record).setFromIndex(fromIndex);
        }

        public final void setItemCount(Object record, int itemCount) {
            ((AccessibilityRecord) record).setItemCount(itemCount);
        }

        public final void setScrollX(Object record, int scrollX) {
            ((AccessibilityRecord) record).setScrollX(scrollX);
        }

        public final void setScrollY(Object record, int scrollY) {
            ((AccessibilityRecord) record).setScrollY(scrollY);
        }

        public final void setScrollable(Object record, boolean scrollable) {
            ((AccessibilityRecord) record).setScrollable(scrollable);
        }

        public final void setToIndex(Object record, int toIndex) {
            ((AccessibilityRecord) record).setToIndex(toIndex);
        }
    }

    static class AccessibilityRecordIcsMr1Impl extends AccessibilityRecordIcsImpl {
        AccessibilityRecordIcsMr1Impl() {
        }

        public final void setMaxScrollX(Object record, int maxScrollX) {
            ((AccessibilityRecord) record).setMaxScrollX(maxScrollX);
        }

        public final void setMaxScrollY(Object record, int maxScrollY) {
            ((AccessibilityRecord) record).setMaxScrollY(maxScrollY);
        }
    }

    static class AccessibilityRecordJellyBeanImpl extends AccessibilityRecordIcsMr1Impl {
        AccessibilityRecordJellyBeanImpl() {
        }
    }

    static {
        if (VERSION.SDK_INT >= 16) {
            IMPL = new AccessibilityRecordJellyBeanImpl();
        } else if (VERSION.SDK_INT >= 15) {
            IMPL = new AccessibilityRecordIcsMr1Impl();
        } else if (VERSION.SDK_INT >= 14) {
            IMPL = new AccessibilityRecordIcsImpl();
        } else {
            IMPL = new AccessibilityRecordStubImpl();
        }
    }

    @Deprecated
    public AccessibilityRecordCompat(Object record) {
        this.mRecord = record;
    }

    public final void setScrollable(boolean scrollable) {
        IMPL.setScrollable(this.mRecord, scrollable);
    }

    public final void setItemCount(int itemCount) {
        IMPL.setItemCount(this.mRecord, itemCount);
    }

    public final void setFromIndex(int fromIndex) {
        IMPL.setFromIndex(this.mRecord, fromIndex);
    }

    public final void setToIndex(int toIndex) {
        IMPL.setToIndex(this.mRecord, toIndex);
    }

    public final void setScrollX(int scrollX) {
        IMPL.setScrollX(this.mRecord, scrollX);
    }

    public final void setScrollY(int scrollY) {
        IMPL.setScrollY(this.mRecord, scrollY);
    }

    public final void setMaxScrollX(int maxScrollX) {
        IMPL.setMaxScrollX(this.mRecord, maxScrollX);
    }

    public final void setMaxScrollY(int maxScrollY) {
        IMPL.setMaxScrollY(this.mRecord, maxScrollY);
    }

    public final int hashCode() {
        return this.mRecord == null ? 0 : this.mRecord.hashCode();
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
        AccessibilityRecordCompat other = (AccessibilityRecordCompat) obj;
        if (this.mRecord == null) {
            if (other.mRecord != null) {
                return false;
            }
            return true;
        } else if (this.mRecord.equals(other.mRecord)) {
            return true;
        } else {
            return false;
        }
    }
}
