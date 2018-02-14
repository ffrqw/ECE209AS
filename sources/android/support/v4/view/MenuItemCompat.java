package android.support.v4.view;

import android.os.Build.VERSION;
import android.support.v4.internal.view.SupportMenuItem;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public final class MenuItemCompat {
    static final MenuVersionImpl IMPL;

    interface MenuVersionImpl {
        boolean expandActionView(MenuItem menuItem);

        View getActionView(MenuItem menuItem);

        boolean isActionViewExpanded(MenuItem menuItem);

        MenuItem setActionView(MenuItem menuItem, int i);

        MenuItem setActionView(MenuItem menuItem, View view);

        void setShowAsAction(MenuItem menuItem, int i);
    }

    static class BaseMenuVersionImpl implements MenuVersionImpl {
        BaseMenuVersionImpl() {
        }

        public final void setShowAsAction(MenuItem item, int actionEnum) {
        }

        public final MenuItem setActionView(MenuItem item, View view) {
            return item;
        }

        public final MenuItem setActionView(MenuItem item, int resId) {
            return item;
        }

        public final View getActionView(MenuItem item) {
            return null;
        }

        public final boolean expandActionView(MenuItem item) {
            return false;
        }

        public final boolean isActionViewExpanded(MenuItem item) {
            return false;
        }
    }

    static class HoneycombMenuVersionImpl implements MenuVersionImpl {
        HoneycombMenuVersionImpl() {
        }

        public boolean expandActionView(MenuItem item) {
            return false;
        }

        public boolean isActionViewExpanded(MenuItem item) {
            return false;
        }

        public final void setShowAsAction(MenuItem item, int actionEnum) {
            item.setShowAsAction(actionEnum);
        }

        public final MenuItem setActionView(MenuItem item, View view) {
            return item.setActionView(view);
        }

        public final MenuItem setActionView(MenuItem item, int resId) {
            return item.setActionView(resId);
        }

        public final View getActionView(MenuItem item) {
            return item.getActionView();
        }
    }

    static class IcsMenuVersionImpl extends HoneycombMenuVersionImpl {
        IcsMenuVersionImpl() {
        }

        public final boolean expandActionView(MenuItem item) {
            return item.expandActionView();
        }

        public final boolean isActionViewExpanded(MenuItem item) {
            return item.isActionViewExpanded();
        }
    }

    public interface OnActionExpandListener {
        boolean onMenuItemActionCollapse(MenuItem menuItem);

        boolean onMenuItemActionExpand(MenuItem menuItem);
    }

    static {
        if (VERSION.SDK_INT >= 14) {
            IMPL = new IcsMenuVersionImpl();
        } else if (VERSION.SDK_INT >= 11) {
            IMPL = new HoneycombMenuVersionImpl();
        } else {
            IMPL = new BaseMenuVersionImpl();
        }
    }

    public static void setShowAsAction(MenuItem item, int actionEnum) {
        if (item instanceof SupportMenuItem) {
            ((SupportMenuItem) item).setShowAsAction(actionEnum);
        } else {
            IMPL.setShowAsAction(item, actionEnum);
        }
    }

    public static MenuItem setActionView(MenuItem item, View view) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).setActionView(view);
        }
        return IMPL.setActionView(item, view);
    }

    public static MenuItem setActionView(MenuItem item, int resId) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).setActionView(resId);
        }
        return IMPL.setActionView(item, resId);
    }

    public static View getActionView(MenuItem item) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).getActionView();
        }
        return IMPL.getActionView(item);
    }

    public static MenuItem setActionProvider(MenuItem item, ActionProvider provider) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).setSupportActionProvider(provider);
        }
        Log.w("MenuItemCompat", "setActionProvider: item does not implement SupportMenuItem; ignoring");
        return item;
    }

    public static boolean expandActionView(MenuItem item) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).expandActionView();
        }
        return IMPL.expandActionView(item);
    }

    public static boolean isActionViewExpanded(MenuItem item) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).isActionViewExpanded();
        }
        return IMPL.isActionViewExpanded(item);
    }
}
