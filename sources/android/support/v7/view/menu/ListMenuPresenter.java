package android.support.v7.view.menu;

import android.content.Context;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView.ItemView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import com.rachio.iro.R;
import java.util.ArrayList;

public final class ListMenuPresenter implements MenuPresenter, OnItemClickListener {
    MenuAdapter mAdapter;
    private Callback mCallback;
    Context mContext;
    LayoutInflater mInflater;
    int mItemLayoutRes;
    MenuBuilder mMenu;
    ExpandedMenuView mMenuView;
    int mThemeRes;

    private class MenuAdapter extends BaseAdapter {
        private int mExpandedIndex = -1;

        public MenuAdapter() {
            findExpandedIndex();
        }

        public final int getCount() {
            int count = ListMenuPresenter.this.mMenu.getNonActionItems().size();
            return this.mExpandedIndex < 0 ? count : count - 1;
        }

        public final MenuItemImpl getItem(int position) {
            ArrayList<MenuItemImpl> items = ListMenuPresenter.this.mMenu.getNonActionItems();
            if (this.mExpandedIndex >= 0 && position >= this.mExpandedIndex) {
                position++;
            }
            return (MenuItemImpl) items.get(position);
        }

        public final long getItemId(int position) {
            return (long) position;
        }

        public final View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ListMenuPresenter.this.mInflater.inflate(ListMenuPresenter.this.mItemLayoutRes, parent, false);
            }
            ((ItemView) convertView).initialize(getItem(position), 0);
            return convertView;
        }

        private void findExpandedIndex() {
            MenuItemImpl expandedItem = ListMenuPresenter.this.mMenu.getExpandedItem();
            if (expandedItem != null) {
                ArrayList<MenuItemImpl> items = ListMenuPresenter.this.mMenu.getNonActionItems();
                int count = items.size();
                for (int i = 0; i < count; i++) {
                    if (((MenuItemImpl) items.get(i)) == expandedItem) {
                        this.mExpandedIndex = i;
                        return;
                    }
                }
            }
            this.mExpandedIndex = -1;
        }

        public final void notifyDataSetChanged() {
            findExpandedIndex();
            super.notifyDataSetChanged();
        }
    }

    public ListMenuPresenter(Context context, int itemLayoutRes) {
        this(itemLayoutRes, 0);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    private ListMenuPresenter(int itemLayoutRes, int themeRes) {
        this.mItemLayoutRes = itemLayoutRes;
        this.mThemeRes = 0;
    }

    public final void initForMenu(Context context, MenuBuilder menu) {
        if (this.mThemeRes != 0) {
            this.mContext = new ContextThemeWrapper(context, this.mThemeRes);
            this.mInflater = LayoutInflater.from(this.mContext);
        } else if (this.mContext != null) {
            this.mContext = context;
            if (this.mInflater == null) {
                this.mInflater = LayoutInflater.from(this.mContext);
            }
        }
        this.mMenu = menu;
        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public final MenuView getMenuView(ViewGroup root) {
        if (this.mMenuView == null) {
            this.mMenuView = (ExpandedMenuView) this.mInflater.inflate(R.layout.abc_expanded_menu_layout, root, false);
            if (this.mAdapter == null) {
                this.mAdapter = new MenuAdapter();
            }
            this.mMenuView.setAdapter(this.mAdapter);
            this.mMenuView.setOnItemClickListener(this);
        }
        return this.mMenuView;
    }

    public final ListAdapter getAdapter() {
        if (this.mAdapter == null) {
            this.mAdapter = new MenuAdapter();
        }
        return this.mAdapter;
    }

    public final void updateMenuView(boolean cleared) {
        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public final void setCallback(Callback cb) {
        this.mCallback = cb;
    }

    public final boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        if (!subMenu.hasVisibleItems()) {
            return false;
        }
        new MenuDialogHelper(subMenu).show(null);
        if (this.mCallback != null) {
            this.mCallback.onOpenSubMenu(subMenu);
        }
        return true;
    }

    public final void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        if (this.mCallback != null) {
            this.mCallback.onCloseMenu(menu, allMenusAreClosing);
        }
    }

    public final void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        this.mMenu.performItemAction(this.mAdapter.getItem(position), this, 0);
    }

    public final boolean flagActionItems() {
        return false;
    }

    public final boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }

    public final boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }
}
