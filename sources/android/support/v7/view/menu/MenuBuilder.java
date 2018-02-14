package android.support.v7.view.menu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.util.SparseArray;
import android.view.KeyCharacterMap.KeyData;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import com.rachio.iro.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MenuBuilder implements SupportMenu {
    private static final int[] sCategoryToOrder = new int[]{1, 4, 5, 3, 2, 0};
    private ArrayList<MenuItemImpl> mActionItems;
    private Callback mCallback;
    private final Context mContext;
    private int mDefaultShowAsAction = 0;
    private MenuItemImpl mExpandedItem;
    Drawable mHeaderIcon;
    CharSequence mHeaderTitle;
    View mHeaderView;
    private boolean mIsActionItemsStale;
    private boolean mIsClosing = false;
    private boolean mIsVisibleItemsStale;
    private ArrayList<MenuItemImpl> mItems;
    private boolean mItemsChangedWhileDispatchPrevented = false;
    private ArrayList<MenuItemImpl> mNonActionItems;
    private boolean mOptionalIconsVisible = false;
    private boolean mOverrideVisibleItems;
    private CopyOnWriteArrayList<WeakReference<MenuPresenter>> mPresenters = new CopyOnWriteArrayList();
    private boolean mPreventDispatchingItemsChanged = false;
    private boolean mQwertyMode;
    private final Resources mResources;
    private boolean mShortcutsVisible;
    private boolean mStructureChangedWhileDispatchPrevented = false;
    private ArrayList<MenuItemImpl> mTempShortcutItemList = new ArrayList();
    private ArrayList<MenuItemImpl> mVisibleItems;

    public interface Callback {
        boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem);

        void onMenuModeChange(MenuBuilder menuBuilder);
    }

    public interface ItemInvoker {
        boolean invokeItem(MenuItemImpl menuItemImpl);
    }

    public MenuBuilder(Context context) {
        boolean z = true;
        this.mContext = context;
        this.mResources = context.getResources();
        this.mItems = new ArrayList();
        this.mVisibleItems = new ArrayList();
        this.mIsVisibleItemsStale = true;
        this.mActionItems = new ArrayList();
        this.mNonActionItems = new ArrayList();
        this.mIsActionItemsStale = true;
        if (this.mResources.getConfiguration().keyboard == 1 || !this.mResources.getBoolean(R.bool.abc_config_showMenuShortcutsWhenKeyboardPresent)) {
            z = false;
        }
        this.mShortcutsVisible = z;
    }

    public final MenuBuilder setDefaultShowAsAction(int defaultShowAsAction) {
        this.mDefaultShowAsAction = 1;
        return this;
    }

    public final void addMenuPresenter(MenuPresenter presenter) {
        addMenuPresenter(presenter, this.mContext);
    }

    public final void addMenuPresenter(MenuPresenter presenter, Context menuContext) {
        this.mPresenters.add(new WeakReference(presenter));
        presenter.initForMenu(menuContext, this);
        this.mIsActionItemsStale = true;
    }

    public final void removeMenuPresenter(MenuPresenter presenter) {
        Iterator it = this.mPresenters.iterator();
        while (it.hasNext()) {
            WeakReference<MenuPresenter> ref = (WeakReference) it.next();
            MenuPresenter item = (MenuPresenter) ref.get();
            if (item == null || item == presenter) {
                this.mPresenters.remove(ref);
            }
        }
    }

    public final void saveActionViewStates(Bundle outStates) {
        SparseArray<Parcelable> viewStates = null;
        int itemCount = size();
        for (int i = 0; i < itemCount; i++) {
            MenuItem item = getItem(i);
            View v = MenuItemCompat.getActionView(item);
            if (!(v == null || v.getId() == -1)) {
                if (viewStates == null) {
                    viewStates = new SparseArray();
                }
                v.saveHierarchyState(viewStates);
                if (MenuItemCompat.isActionViewExpanded(item)) {
                    outStates.putInt("android:menu:expandedactionview", item.getItemId());
                }
            }
            if (item.hasSubMenu()) {
                ((SubMenuBuilder) item.getSubMenu()).saveActionViewStates(outStates);
            }
        }
        if (viewStates != null) {
            outStates.putSparseParcelableArray(getActionViewStatesKey(), viewStates);
        }
    }

    public final void restoreActionViewStates(Bundle states) {
        if (states != null) {
            SparseArray<Parcelable> viewStates = states.getSparseParcelableArray(getActionViewStatesKey());
            int itemCount = size();
            for (int i = 0; i < itemCount; i++) {
                MenuItem item = getItem(i);
                View v = MenuItemCompat.getActionView(item);
                if (!(v == null || v.getId() == -1)) {
                    v.restoreHierarchyState(viewStates);
                }
                if (item.hasSubMenu()) {
                    ((SubMenuBuilder) item.getSubMenu()).restoreActionViewStates(states);
                }
            }
            int expandedId = states.getInt("android:menu:expandedactionview");
            if (expandedId > 0) {
                MenuItem itemToExpand = findItem(expandedId);
                if (itemToExpand != null) {
                    MenuItemCompat.expandActionView(itemToExpand);
                }
            }
        }
    }

    protected String getActionViewStatesKey() {
        return "android:menu:actionviewstates";
    }

    public void setCallback(Callback cb) {
        this.mCallback = cb;
    }

    public MenuItem add(CharSequence title) {
        return addInternal(0, 0, 0, title);
    }

    public MenuItem add(int titleRes) {
        return addInternal(0, 0, 0, this.mResources.getString(titleRes));
    }

    public MenuItem add(int group, int id, int categoryOrder, CharSequence title) {
        return addInternal(group, id, categoryOrder, title);
    }

    public MenuItem add(int group, int id, int categoryOrder, int title) {
        return addInternal(group, id, categoryOrder, this.mResources.getString(title));
    }

    public SubMenu addSubMenu(CharSequence title) {
        return addSubMenu(0, 0, 0, title);
    }

    public SubMenu addSubMenu(int titleRes) {
        return addSubMenu(0, 0, 0, this.mResources.getString(titleRes));
    }

    public SubMenu addSubMenu(int group, int id, int categoryOrder, CharSequence title) {
        MenuItemImpl item = (MenuItemImpl) addInternal(group, id, categoryOrder, title);
        SubMenuBuilder subMenu = new SubMenuBuilder(this.mContext, this, item);
        item.setSubMenu(subMenu);
        return subMenu;
    }

    public SubMenu addSubMenu(int group, int id, int categoryOrder, int title) {
        return addSubMenu(group, id, categoryOrder, this.mResources.getString(title));
    }

    public int addIntentOptions(int group, int id, int categoryOrder, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        PackageManager pm = this.mContext.getPackageManager();
        List<ResolveInfo> lri = pm.queryIntentActivityOptions(caller, specifics, intent, 0);
        int N = lri != null ? lri.size() : 0;
        if ((flags & 1) == 0) {
            removeGroup(group);
        }
        for (int i = 0; i < N; i++) {
            Intent intent2;
            ResolveInfo ri = (ResolveInfo) lri.get(i);
            if (ri.specificIndex < 0) {
                intent2 = intent;
            } else {
                intent2 = specifics[ri.specificIndex];
            }
            Intent rintent = new Intent(intent2);
            rintent.setComponent(new ComponentName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name));
            MenuItem item = add(group, id, categoryOrder, ri.loadLabel(pm)).setIcon(ri.loadIcon(pm)).setIntent(rintent);
            if (outSpecificItems != null && ri.specificIndex >= 0) {
                outSpecificItems[ri.specificIndex] = item;
            }
        }
        return N;
    }

    public void removeGroup(int group) {
        int size = size();
        int i = 0;
        while (i < size) {
            if (((MenuItemImpl) this.mItems.get(i)).getGroupId() == group) {
                break;
            }
            i++;
        }
        i = -1;
        if (i >= 0) {
            int maxRemovable = this.mItems.size() - i;
            int numRemoved = 0;
            while (true) {
                int numRemoved2 = numRemoved + 1;
                if (numRemoved >= maxRemovable || ((MenuItemImpl) this.mItems.get(i)).getGroupId() != group) {
                    onItemsChanged(true);
                } else {
                    removeItemAtInt(i, false);
                    numRemoved = numRemoved2;
                }
            }
            onItemsChanged(true);
        }
    }

    private void removeItemAtInt(int index, boolean updateChildrenOnMenuViews) {
        if (index >= 0 && index < this.mItems.size()) {
            this.mItems.remove(index);
            if (updateChildrenOnMenuViews) {
                onItemsChanged(true);
            }
        }
    }

    public void clear() {
        if (this.mExpandedItem != null) {
            collapseItemActionView(this.mExpandedItem);
        }
        this.mItems.clear();
        onItemsChanged(true);
    }

    final void setExclusiveItemChecked(MenuItem item) {
        int group = item.getGroupId();
        int N = this.mItems.size();
        stopDispatchingItemsChanged();
        for (int i = 0; i < N; i++) {
            MenuItem curItem = (MenuItemImpl) this.mItems.get(i);
            if (curItem.getGroupId() == group && curItem.isExclusiveCheckable() && curItem.isCheckable()) {
                boolean z;
                if (curItem == item) {
                    z = true;
                } else {
                    z = false;
                }
                curItem.setCheckedInt(z);
            }
        }
        startDispatchingItemsChanged();
    }

    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
        int N = this.mItems.size();
        for (int i = 0; i < N; i++) {
            MenuItemImpl item = (MenuItemImpl) this.mItems.get(i);
            if (item.getGroupId() == group) {
                item.setExclusiveCheckable(exclusive);
                item.setCheckable(checkable);
            }
        }
    }

    public void setGroupVisible(int group, boolean visible) {
        int N = this.mItems.size();
        boolean changedAtLeastOneItem = false;
        for (int i = 0; i < N; i++) {
            MenuItemImpl item = (MenuItemImpl) this.mItems.get(i);
            if (item.getGroupId() == group && item.setVisibleInt(visible)) {
                changedAtLeastOneItem = true;
            }
        }
        if (changedAtLeastOneItem) {
            onItemsChanged(true);
        }
    }

    public void setGroupEnabled(int group, boolean enabled) {
        int N = this.mItems.size();
        for (int i = 0; i < N; i++) {
            MenuItemImpl item = (MenuItemImpl) this.mItems.get(i);
            if (item.getGroupId() == group) {
                item.setEnabled(enabled);
            }
        }
    }

    public boolean hasVisibleItems() {
        if (this.mOverrideVisibleItems) {
            return true;
        }
        int size = size();
        for (int i = 0; i < size; i++) {
            if (((MenuItemImpl) this.mItems.get(i)).isVisible()) {
                return true;
            }
        }
        return false;
    }

    public MenuItem findItem(int id) {
        int size = size();
        for (int i = 0; i < size; i++) {
            MenuItemImpl item = (MenuItemImpl) this.mItems.get(i);
            if (item.getItemId() == id) {
                return item;
            }
            if (item.hasSubMenu()) {
                MenuItem possibleItem = item.getSubMenu().findItem(id);
                if (possibleItem != null) {
                    return possibleItem;
                }
            }
        }
        return null;
    }

    public int size() {
        return this.mItems.size();
    }

    public MenuItem getItem(int index) {
        return (MenuItem) this.mItems.get(index);
    }

    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return findItemWithShortcutForKey(keyCode, event) != null;
    }

    public void setQwertyMode(boolean isQwerty) {
        this.mQwertyMode = isQwerty;
        onItemsChanged(false);
    }

    boolean isQwertyMode() {
        return this.mQwertyMode;
    }

    public boolean isShortcutsVisible() {
        return this.mShortcutsVisible;
    }

    public final Context getContext() {
        return this.mContext;
    }

    boolean dispatchMenuItemSelected(MenuBuilder menu, MenuItem item) {
        return this.mCallback != null && this.mCallback.onMenuItemSelected(menu, item);
    }

    public final void changeMenuMode() {
        if (this.mCallback != null) {
            this.mCallback.onMenuModeChange(this);
        }
    }

    private static int findInsertIndex(ArrayList<MenuItemImpl> items, int ordering) {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (((MenuItemImpl) items.get(i)).getOrdering() <= ordering) {
                return i + 1;
            }
        }
        return 0;
    }

    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        MenuItemImpl item = findItemWithShortcutForKey(keyCode, event);
        boolean handled = false;
        if (item != null) {
            handled = performItemAction(item, null, flags);
        }
        if ((flags & 2) != 0) {
            close(true);
        }
        return handled;
    }

    private void findItemsWithShortcutForKey(List<MenuItemImpl> items, int keyCode, KeyEvent event) {
        boolean qwerty = isQwertyMode();
        int metaState = event.getMetaState();
        KeyData possibleChars = new KeyData();
        if (event.getKeyData(possibleChars) || keyCode == 67) {
            int N = this.mItems.size();
            for (int i = 0; i < N; i++) {
                MenuItemImpl item = (MenuItemImpl) this.mItems.get(i);
                if (item.hasSubMenu()) {
                    ((MenuBuilder) item.getSubMenu()).findItemsWithShortcutForKey(items, keyCode, event);
                }
                char shortcutChar = qwerty ? item.getAlphabeticShortcut() : item.getNumericShortcut();
                if ((metaState & 5) == 0 && shortcutChar != '\u0000' && ((shortcutChar == possibleChars.meta[0] || shortcutChar == possibleChars.meta[2] || (qwerty && shortcutChar == '\b' && keyCode == 67)) && item.isEnabled())) {
                    items.add(item);
                }
            }
        }
    }

    private MenuItemImpl findItemWithShortcutForKey(int keyCode, KeyEvent event) {
        ArrayList<MenuItemImpl> items = this.mTempShortcutItemList;
        items.clear();
        findItemsWithShortcutForKey(items, keyCode, event);
        if (items.isEmpty()) {
            return null;
        }
        int metaState = event.getMetaState();
        KeyData possibleChars = new KeyData();
        event.getKeyData(possibleChars);
        int size = items.size();
        if (size == 1) {
            return (MenuItemImpl) items.get(0);
        }
        boolean qwerty = isQwertyMode();
        for (int i = 0; i < size; i++) {
            char shortcutChar;
            MenuItemImpl item = (MenuItemImpl) items.get(i);
            if (qwerty) {
                shortcutChar = item.getAlphabeticShortcut();
            } else {
                shortcutChar = item.getNumericShortcut();
            }
            if ((shortcutChar == possibleChars.meta[0] && (metaState & 2) == 0) || ((shortcutChar == possibleChars.meta[2] && (metaState & 2) != 0) || (qwerty && shortcutChar == '\b' && keyCode == 67))) {
                return item;
            }
        }
        return null;
    }

    public boolean performIdentifierAction(int id, int flags) {
        return performItemAction(findItem(id), null, flags);
    }

    public final boolean performItemAction(MenuItem item, int flags) {
        return performItemAction(item, null, flags);
    }

    public final boolean performItemAction(MenuItem item, MenuPresenter preferredPresenter, int flags) {
        boolean z = false;
        MenuItemImpl itemImpl = (MenuItemImpl) item;
        if (itemImpl == null || !itemImpl.isEnabled()) {
            return false;
        }
        boolean providerHasSubMenu;
        boolean invoked = itemImpl.invoke();
        ActionProvider provider = itemImpl.getSupportActionProvider();
        if (provider == null || !provider.hasSubMenu()) {
            providerHasSubMenu = false;
        } else {
            providerHasSubMenu = true;
        }
        if (itemImpl.hasCollapsibleActionView()) {
            invoked |= itemImpl.expandActionView();
            if (!invoked) {
                return invoked;
            }
            close(true);
            return invoked;
        } else if (itemImpl.hasSubMenu() || providerHasSubMenu) {
            if ((flags & 4) == 0) {
                close(false);
            }
            if (!itemImpl.hasSubMenu()) {
                itemImpl.setSubMenu(new SubMenuBuilder(this.mContext, this, itemImpl));
            }
            SubMenuBuilder subMenu = (SubMenuBuilder) itemImpl.getSubMenu();
            if (providerHasSubMenu) {
                provider.onPrepareSubMenu(subMenu);
            }
            if (!this.mPresenters.isEmpty()) {
                if (preferredPresenter != null) {
                    z = preferredPresenter.onSubMenuSelected(subMenu);
                }
                Iterator it = this.mPresenters.iterator();
                boolean z2 = z;
                while (it.hasNext()) {
                    WeakReference weakReference = (WeakReference) it.next();
                    MenuPresenter menuPresenter = (MenuPresenter) weakReference.get();
                    if (menuPresenter == null) {
                        this.mPresenters.remove(weakReference);
                    } else {
                        if (z2) {
                            z = z2;
                        } else {
                            z = menuPresenter.onSubMenuSelected(subMenu);
                        }
                        z2 = z;
                    }
                }
                z = z2;
            }
            invoked |= r5;
            if (invoked) {
                return invoked;
            }
            close(true);
            return invoked;
        } else if ((flags & 1) != 0) {
            return invoked;
        } else {
            close(true);
            return invoked;
        }
    }

    public final void close(boolean closeAllMenus) {
        if (!this.mIsClosing) {
            this.mIsClosing = true;
            Iterator it = this.mPresenters.iterator();
            while (it.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference) it.next();
                MenuPresenter presenter = (MenuPresenter) ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else {
                    presenter.onCloseMenu(this, closeAllMenus);
                }
            }
            this.mIsClosing = false;
        }
    }

    public void close() {
        close(true);
    }

    public final void onItemsChanged(boolean structureChanged) {
        if (this.mPreventDispatchingItemsChanged) {
            this.mItemsChangedWhileDispatchPrevented = true;
            if (structureChanged) {
                this.mStructureChangedWhileDispatchPrevented = true;
                return;
            }
            return;
        }
        if (structureChanged) {
            this.mIsVisibleItemsStale = true;
            this.mIsActionItemsStale = true;
        }
        if (!this.mPresenters.isEmpty()) {
            stopDispatchingItemsChanged();
            Iterator it = this.mPresenters.iterator();
            while (it.hasNext()) {
                WeakReference weakReference = (WeakReference) it.next();
                MenuPresenter menuPresenter = (MenuPresenter) weakReference.get();
                if (menuPresenter == null) {
                    this.mPresenters.remove(weakReference);
                } else {
                    menuPresenter.updateMenuView(structureChanged);
                }
            }
            startDispatchingItemsChanged();
        }
    }

    public final void stopDispatchingItemsChanged() {
        if (!this.mPreventDispatchingItemsChanged) {
            this.mPreventDispatchingItemsChanged = true;
            this.mItemsChangedWhileDispatchPrevented = false;
            this.mStructureChangedWhileDispatchPrevented = false;
        }
    }

    public final void startDispatchingItemsChanged() {
        this.mPreventDispatchingItemsChanged = false;
        if (this.mItemsChangedWhileDispatchPrevented) {
            this.mItemsChangedWhileDispatchPrevented = false;
            onItemsChanged(this.mStructureChangedWhileDispatchPrevented);
        }
    }

    final void onItemVisibleChanged$4da0fe86() {
        this.mIsVisibleItemsStale = true;
        onItemsChanged(true);
    }

    final void onItemActionRequestChanged$4da0fe86() {
        this.mIsActionItemsStale = true;
        onItemsChanged(true);
    }

    public final ArrayList<MenuItemImpl> getVisibleItems() {
        if (!this.mIsVisibleItemsStale) {
            return this.mVisibleItems;
        }
        this.mVisibleItems.clear();
        int itemsSize = this.mItems.size();
        for (int i = 0; i < itemsSize; i++) {
            MenuItemImpl item = (MenuItemImpl) this.mItems.get(i);
            if (item.isVisible()) {
                this.mVisibleItems.add(item);
            }
        }
        this.mIsVisibleItemsStale = false;
        this.mIsActionItemsStale = true;
        return this.mVisibleItems;
    }

    public final void flagActionItems() {
        ArrayList<MenuItemImpl> visibleItems = getVisibleItems();
        if (this.mIsActionItemsStale) {
            boolean flagged = false;
            Iterator it = this.mPresenters.iterator();
            while (it.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference) it.next();
                MenuPresenter presenter = (MenuPresenter) ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else {
                    flagged |= presenter.flagActionItems();
                }
            }
            if (flagged) {
                this.mActionItems.clear();
                this.mNonActionItems.clear();
                int itemsSize = visibleItems.size();
                for (int i = 0; i < itemsSize; i++) {
                    MenuItemImpl item = (MenuItemImpl) visibleItems.get(i);
                    if (item.isActionButton()) {
                        this.mActionItems.add(item);
                    } else {
                        this.mNonActionItems.add(item);
                    }
                }
            } else {
                this.mActionItems.clear();
                this.mNonActionItems.clear();
                this.mNonActionItems.addAll(getVisibleItems());
            }
            this.mIsActionItemsStale = false;
        }
    }

    public final ArrayList<MenuItemImpl> getActionItems() {
        flagActionItems();
        return this.mActionItems;
    }

    public final ArrayList<MenuItemImpl> getNonActionItems() {
        flagActionItems();
        return this.mNonActionItems;
    }

    public void clearHeader() {
        this.mHeaderIcon = null;
        this.mHeaderTitle = null;
        this.mHeaderView = null;
        onItemsChanged(false);
    }

    private void setHeaderInternal(int titleRes, CharSequence title, int iconRes, Drawable icon, View view) {
        Resources r = this.mResources;
        if (view != null) {
            this.mHeaderView = view;
            this.mHeaderTitle = null;
            this.mHeaderIcon = null;
        } else {
            if (titleRes > 0) {
                this.mHeaderTitle = r.getText(titleRes);
            } else if (title != null) {
                this.mHeaderTitle = title;
            }
            if (iconRes > 0) {
                this.mHeaderIcon = ContextCompat.getDrawable(this.mContext, iconRes);
            } else if (icon != null) {
                this.mHeaderIcon = icon;
            }
            this.mHeaderView = null;
        }
        onItemsChanged(false);
    }

    protected final MenuBuilder setHeaderTitleInt(CharSequence title) {
        setHeaderInternal(0, title, 0, null, null);
        return this;
    }

    protected final MenuBuilder setHeaderTitleInt(int titleRes) {
        setHeaderInternal(titleRes, null, 0, null, null);
        return this;
    }

    protected final MenuBuilder setHeaderIconInt(Drawable icon) {
        setHeaderInternal(0, null, 0, icon, null);
        return this;
    }

    protected final MenuBuilder setHeaderIconInt(int iconRes) {
        setHeaderInternal(0, null, iconRes, null, null);
        return this;
    }

    protected final MenuBuilder setHeaderViewInt(View view) {
        setHeaderInternal(0, null, 0, null, view);
        return this;
    }

    public MenuBuilder getRootMenu() {
        return this;
    }

    public boolean expandItemActionView(MenuItemImpl item) {
        if (this.mPresenters.isEmpty()) {
            return false;
        }
        boolean expanded = false;
        stopDispatchingItemsChanged();
        Iterator it = this.mPresenters.iterator();
        while (it.hasNext()) {
            WeakReference<MenuPresenter> ref = (WeakReference) it.next();
            MenuPresenter presenter = (MenuPresenter) ref.get();
            if (presenter == null) {
                this.mPresenters.remove(ref);
            } else {
                expanded = presenter.expandItemActionView(this, item);
                if (expanded) {
                    break;
                }
            }
        }
        startDispatchingItemsChanged();
        if (!expanded) {
            return expanded;
        }
        this.mExpandedItem = item;
        return expanded;
    }

    public boolean collapseItemActionView(MenuItemImpl item) {
        if (this.mPresenters.isEmpty() || this.mExpandedItem != item) {
            return false;
        }
        boolean collapsed = false;
        stopDispatchingItemsChanged();
        Iterator it = this.mPresenters.iterator();
        while (it.hasNext()) {
            WeakReference<MenuPresenter> ref = (WeakReference) it.next();
            MenuPresenter presenter = (MenuPresenter) ref.get();
            if (presenter == null) {
                this.mPresenters.remove(ref);
            } else {
                collapsed = presenter.collapseItemActionView(this, item);
                if (collapsed) {
                    break;
                }
            }
        }
        startDispatchingItemsChanged();
        if (!collapsed) {
            return collapsed;
        }
        this.mExpandedItem = null;
        return collapsed;
    }

    public final MenuItemImpl getExpandedItem() {
        return this.mExpandedItem;
    }

    public final void setOverrideVisibleItems(boolean override) {
        this.mOverrideVisibleItems = override;
    }

    private MenuItem addInternal(int group, int id, int categoryOrder, CharSequence title) {
        int i = categoryOrder >> 16;
        if (i < 0 || i >= 6) {
            throw new IllegalArgumentException("order does not contain a valid category.");
        }
        int ordering = (sCategoryToOrder[i] << 16) | (65535 & categoryOrder);
        MenuItemImpl item = new MenuItemImpl(this, group, id, categoryOrder, ordering, title, this.mDefaultShowAsAction);
        this.mItems.add(findInsertIndex(this.mItems, ordering), item);
        onItemsChanged(true);
        return item;
    }

    public void removeItem(int id) {
        int i;
        int size = size();
        for (int i2 = 0; i2 < size; i2++) {
            if (((MenuItemImpl) this.mItems.get(i2)).getItemId() == id) {
                i = i2;
                break;
            }
        }
        i = -1;
        removeItemAtInt(i, true);
    }
}
