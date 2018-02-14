package android.support.v7.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;

@TargetApi(11)
public final class SupportActionModeWrapper extends ActionMode {
    final Context mContext;
    final ActionMode mWrappedObject;

    public static class CallbackWrapper implements Callback {
        final ArrayList<SupportActionModeWrapper> mActionModes = new ArrayList();
        final Context mContext;
        final SimpleArrayMap<Menu, Menu> mMenus = new SimpleArrayMap();
        final ActionMode.Callback mWrappedCallback;

        public CallbackWrapper(Context context, ActionMode.Callback supportCallback) {
            this.mContext = context;
            this.mWrappedCallback = supportCallback;
        }

        public final boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return this.mWrappedCallback.onCreateActionMode(getActionModeWrapper(mode), getMenuWrapper(menu));
        }

        public final boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return this.mWrappedCallback.onPrepareActionMode(getActionModeWrapper(mode), getMenuWrapper(menu));
        }

        public final boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mWrappedCallback.onActionItemClicked(getActionModeWrapper(mode), MenuWrapperFactory.wrapSupportMenuItem(this.mContext, (SupportMenuItem) item));
        }

        public final void onDestroyActionMode(ActionMode mode) {
            this.mWrappedCallback.onDestroyActionMode(getActionModeWrapper(mode));
        }

        private Menu getMenuWrapper(Menu menu) {
            Menu wrapper = (Menu) this.mMenus.get(menu);
            if (wrapper != null) {
                return wrapper;
            }
            wrapper = MenuWrapperFactory.wrapSupportMenu(this.mContext, (SupportMenu) menu);
            this.mMenus.put(menu, wrapper);
            return wrapper;
        }

        public final ActionMode getActionModeWrapper(ActionMode mode) {
            SupportActionModeWrapper wrapper;
            int count = this.mActionModes.size();
            for (int i = 0; i < count; i++) {
                wrapper = (SupportActionModeWrapper) this.mActionModes.get(i);
                if (wrapper != null && wrapper.mWrappedObject == mode) {
                    return wrapper;
                }
            }
            wrapper = new SupportActionModeWrapper(this.mContext, mode);
            this.mActionModes.add(wrapper);
            return wrapper;
        }
    }

    public SupportActionModeWrapper(Context context, ActionMode supportActionMode) {
        this.mContext = context;
        this.mWrappedObject = supportActionMode;
    }

    public final Object getTag() {
        return this.mWrappedObject.getTag();
    }

    public final void setTag(Object tag) {
        this.mWrappedObject.setTag(tag);
    }

    public final void setTitle(CharSequence title) {
        this.mWrappedObject.setTitle(title);
    }

    public final void setSubtitle(CharSequence subtitle) {
        this.mWrappedObject.setSubtitle(subtitle);
    }

    public final void invalidate() {
        this.mWrappedObject.invalidate();
    }

    public final void finish() {
        this.mWrappedObject.finish();
    }

    public final Menu getMenu() {
        return MenuWrapperFactory.wrapSupportMenu(this.mContext, (SupportMenu) this.mWrappedObject.getMenu());
    }

    public final CharSequence getTitle() {
        return this.mWrappedObject.getTitle();
    }

    public final void setTitle(int resId) {
        this.mWrappedObject.setTitle(resId);
    }

    public final CharSequence getSubtitle() {
        return this.mWrappedObject.getSubtitle();
    }

    public final void setSubtitle(int resId) {
        this.mWrappedObject.setSubtitle(resId);
    }

    public final View getCustomView() {
        return this.mWrappedObject.getCustomView();
    }

    public final void setCustomView(View view) {
        this.mWrappedObject.setCustomView(view);
    }

    public final MenuInflater getMenuInflater() {
        return this.mWrappedObject.getMenuInflater();
    }

    public final boolean getTitleOptionalHint() {
        return this.mWrappedObject.getTitleOptionalHint();
    }

    public final void setTitleOptionalHint(boolean titleOptional) {
        this.mWrappedObject.setTitleOptionalHint(titleOptional);
    }

    public final boolean isTitleOptional() {
        return this.mWrappedObject.isTitleOptional();
    }
}
