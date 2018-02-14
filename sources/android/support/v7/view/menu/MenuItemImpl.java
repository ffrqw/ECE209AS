package android.support.v7.view.menu;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.ActionProvider.VisibilityListener;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.menu.MenuView.ItemView;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewDebug.CapturedViewProperty;

public final class MenuItemImpl implements SupportMenuItem {
    private ActionProvider mActionProvider;
    private View mActionView;
    private final int mCategoryOrder;
    private OnMenuItemClickListener mClickListener;
    private int mFlags = 16;
    private final int mGroup;
    private Drawable mIconDrawable;
    private int mIconResId = 0;
    private final int mId;
    private Intent mIntent;
    private boolean mIsActionViewExpanded = false;
    MenuBuilder mMenu;
    private ContextMenuInfo mMenuInfo;
    private OnActionExpandListener mOnActionExpandListener;
    private final int mOrdering;
    private char mShortcutAlphabeticChar;
    private char mShortcutNumericChar;
    private int mShowAsAction = 0;
    private SubMenuBuilder mSubMenu;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;

    MenuItemImpl(MenuBuilder menu, int group, int id, int categoryOrder, int ordering, CharSequence title, int showAsAction) {
        this.mMenu = menu;
        this.mId = id;
        this.mGroup = group;
        this.mCategoryOrder = categoryOrder;
        this.mOrdering = ordering;
        this.mTitle = title;
        this.mShowAsAction = showAsAction;
    }

    public final boolean invoke() {
        if ((this.mClickListener != null && this.mClickListener.onMenuItemClick(this)) || this.mMenu.dispatchMenuItemSelected(this.mMenu.getRootMenu(), this)) {
            return true;
        }
        if (this.mIntent != null) {
            try {
                this.mMenu.getContext().startActivity(this.mIntent);
                return true;
            } catch (ActivityNotFoundException e) {
                Log.e("MenuItemImpl", "Can't find activity to handle intent; ignoring", e);
            }
        }
        if (this.mActionProvider == null || !this.mActionProvider.onPerformDefaultAction()) {
            return false;
        }
        return true;
    }

    public final boolean isEnabled() {
        return (this.mFlags & 16) != 0;
    }

    public final MenuItem setEnabled(boolean enabled) {
        if (enabled) {
            this.mFlags |= 16;
        } else {
            this.mFlags &= -17;
        }
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public final int getGroupId() {
        return this.mGroup;
    }

    @CapturedViewProperty
    public final int getItemId() {
        return this.mId;
    }

    public final int getOrder() {
        return this.mCategoryOrder;
    }

    public final int getOrdering() {
        return this.mOrdering;
    }

    public final Intent getIntent() {
        return this.mIntent;
    }

    public final MenuItem setIntent(Intent intent) {
        this.mIntent = intent;
        return this;
    }

    public final char getAlphabeticShortcut() {
        return this.mShortcutAlphabeticChar;
    }

    public final MenuItem setAlphabeticShortcut(char alphaChar) {
        if (this.mShortcutAlphabeticChar != alphaChar) {
            this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);
            this.mMenu.onItemsChanged(false);
        }
        return this;
    }

    public final char getNumericShortcut() {
        return this.mShortcutNumericChar;
    }

    public final MenuItem setNumericShortcut(char numericChar) {
        if (this.mShortcutNumericChar != numericChar) {
            this.mShortcutNumericChar = numericChar;
            this.mMenu.onItemsChanged(false);
        }
        return this;
    }

    public final MenuItem setShortcut(char numericChar, char alphaChar) {
        this.mShortcutNumericChar = numericChar;
        this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);
        this.mMenu.onItemsChanged(false);
        return this;
    }

    final char getShortcut() {
        return this.mMenu.isQwertyMode() ? this.mShortcutAlphabeticChar : this.mShortcutNumericChar;
    }

    final boolean shouldShowShortcut() {
        return this.mMenu.isShortcutsVisible() && getShortcut() != '\u0000';
    }

    public final SubMenu getSubMenu() {
        return this.mSubMenu;
    }

    public final boolean hasSubMenu() {
        return this.mSubMenu != null;
    }

    public final void setSubMenu(SubMenuBuilder subMenu) {
        this.mSubMenu = subMenu;
        subMenu.setHeaderTitle(getTitle());
    }

    @CapturedViewProperty
    public final CharSequence getTitle() {
        return this.mTitle;
    }

    final CharSequence getTitleForItemView(ItemView itemView) {
        if (itemView == null || !itemView.prefersCondensedTitle()) {
            return getTitle();
        }
        return getTitleCondensed();
    }

    public final MenuItem setTitle(CharSequence title) {
        this.mTitle = title;
        this.mMenu.onItemsChanged(false);
        if (this.mSubMenu != null) {
            this.mSubMenu.setHeaderTitle(title);
        }
        return this;
    }

    public final MenuItem setTitle(int title) {
        return setTitle(this.mMenu.getContext().getString(title));
    }

    public final CharSequence getTitleCondensed() {
        CharSequence ctitle = this.mTitleCondensed != null ? this.mTitleCondensed : this.mTitle;
        if (VERSION.SDK_INT >= 18 || ctitle == null || (ctitle instanceof String)) {
            return ctitle;
        }
        return ctitle.toString();
    }

    public final MenuItem setTitleCondensed(CharSequence title) {
        this.mTitleCondensed = title;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public final Drawable getIcon() {
        if (this.mIconDrawable != null) {
            return this.mIconDrawable;
        }
        if (this.mIconResId == 0) {
            return null;
        }
        Drawable icon = AppCompatResources.getDrawable(this.mMenu.getContext(), this.mIconResId);
        this.mIconResId = 0;
        this.mIconDrawable = icon;
        return icon;
    }

    public final MenuItem setIcon(Drawable icon) {
        this.mIconResId = 0;
        this.mIconDrawable = icon;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public final MenuItem setIcon(int iconResId) {
        this.mIconDrawable = null;
        this.mIconResId = iconResId;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public final boolean isCheckable() {
        return (this.mFlags & 1) == 1;
    }

    public final MenuItem setCheckable(boolean checkable) {
        int oldFlags = this.mFlags;
        this.mFlags = (checkable ? 1 : 0) | (this.mFlags & -2);
        if (oldFlags != this.mFlags) {
            this.mMenu.onItemsChanged(false);
        }
        return this;
    }

    public final void setExclusiveCheckable(boolean exclusive) {
        this.mFlags = (exclusive ? 4 : 0) | (this.mFlags & -5);
    }

    public final boolean isExclusiveCheckable() {
        return (this.mFlags & 4) != 0;
    }

    public final boolean isChecked() {
        return (this.mFlags & 2) == 2;
    }

    public final MenuItem setChecked(boolean checked) {
        if ((this.mFlags & 4) != 0) {
            this.mMenu.setExclusiveItemChecked(this);
        } else {
            setCheckedInt(checked);
        }
        return this;
    }

    final void setCheckedInt(boolean checked) {
        int i;
        int oldFlags = this.mFlags;
        int i2 = this.mFlags & -3;
        if (checked) {
            i = 2;
        } else {
            i = 0;
        }
        this.mFlags = i | i2;
        if (oldFlags != this.mFlags) {
            this.mMenu.onItemsChanged(false);
        }
    }

    public final boolean isVisible() {
        if (this.mActionProvider == null || !this.mActionProvider.overridesItemVisibility()) {
            if ((this.mFlags & 8) != 0) {
                return false;
            }
            return true;
        } else if ((this.mFlags & 8) == 0 && this.mActionProvider.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    final boolean setVisibleInt(boolean shown) {
        int oldFlags = this.mFlags;
        this.mFlags = (shown ? 0 : 8) | (this.mFlags & -9);
        if (oldFlags != this.mFlags) {
            return true;
        }
        return false;
    }

    public final MenuItem setVisible(boolean shown) {
        if (setVisibleInt(shown)) {
            this.mMenu.onItemVisibleChanged$4da0fe86();
        }
        return this;
    }

    public final MenuItem setOnMenuItemClickListener(OnMenuItemClickListener clickListener) {
        this.mClickListener = clickListener;
        return this;
    }

    public final String toString() {
        return this.mTitle != null ? this.mTitle.toString() : null;
    }

    public final ContextMenuInfo getMenuInfo() {
        return this.mMenuInfo;
    }

    public final boolean isActionButton() {
        return (this.mFlags & 32) == 32;
    }

    public final boolean requestsActionButton() {
        return (this.mShowAsAction & 1) == 1;
    }

    public final boolean requiresActionButton() {
        return (this.mShowAsAction & 2) == 2;
    }

    public final void setIsActionButton(boolean isActionButton) {
        if (isActionButton) {
            this.mFlags |= 32;
        } else {
            this.mFlags &= -33;
        }
    }

    public final boolean showsTextAsAction() {
        return (this.mShowAsAction & 4) == 4;
    }

    public final void setShowAsAction(int actionEnum) {
        switch (actionEnum & 3) {
            case 0:
            case 1:
            case 2:
                this.mShowAsAction = actionEnum;
                this.mMenu.onItemActionRequestChanged$4da0fe86();
                return;
            default:
                throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM, and SHOW_AS_ACTION_NEVER are mutually exclusive.");
        }
    }

    private SupportMenuItem setActionView(View view) {
        this.mActionView = view;
        this.mActionProvider = null;
        if (view != null && view.getId() == -1 && this.mId > 0) {
            view.setId(this.mId);
        }
        this.mMenu.onItemActionRequestChanged$4da0fe86();
        return this;
    }

    public final View getActionView() {
        if (this.mActionView != null) {
            return this.mActionView;
        }
        if (this.mActionProvider == null) {
            return null;
        }
        this.mActionView = this.mActionProvider.onCreateActionView(this);
        return this.mActionView;
    }

    public final MenuItem setActionProvider(android.view.ActionProvider actionProvider) {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.setActionProvider()");
    }

    public final android.view.ActionProvider getActionProvider() {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.getActionProvider()");
    }

    public final ActionProvider getSupportActionProvider() {
        return this.mActionProvider;
    }

    public final SupportMenuItem setSupportActionProvider(ActionProvider actionProvider) {
        if (this.mActionProvider != null) {
            this.mActionProvider.reset();
        }
        this.mActionView = null;
        this.mActionProvider = actionProvider;
        this.mMenu.onItemsChanged(true);
        if (this.mActionProvider != null) {
            this.mActionProvider.setVisibilityListener(new VisibilityListener() {
                public final void onActionProviderVisibilityChanged$1385ff() {
                    MenuItemImpl.this.mMenu.onItemVisibleChanged$4da0fe86();
                }
            });
        }
        return this;
    }

    public final boolean expandActionView() {
        if (!hasCollapsibleActionView()) {
            return false;
        }
        if (this.mOnActionExpandListener == null || this.mOnActionExpandListener.onMenuItemActionExpand(this)) {
            return this.mMenu.expandItemActionView(this);
        }
        return false;
    }

    public final boolean collapseActionView() {
        if ((this.mShowAsAction & 8) == 0) {
            return false;
        }
        if (this.mActionView == null) {
            return true;
        }
        if (this.mOnActionExpandListener == null || this.mOnActionExpandListener.onMenuItemActionCollapse(this)) {
            return this.mMenu.collapseItemActionView(this);
        }
        return false;
    }

    public final SupportMenuItem setSupportOnActionExpandListener(OnActionExpandListener listener) {
        this.mOnActionExpandListener = listener;
        return this;
    }

    public final boolean hasCollapsibleActionView() {
        if ((this.mShowAsAction & 8) == 0) {
            return false;
        }
        if (this.mActionView == null && this.mActionProvider != null) {
            this.mActionView = this.mActionProvider.onCreateActionView(this);
        }
        if (this.mActionView != null) {
            return true;
        }
        return false;
    }

    public final void setActionViewExpanded(boolean isExpanded) {
        this.mIsActionViewExpanded = isExpanded;
        this.mMenu.onItemsChanged(false);
    }

    public final boolean isActionViewExpanded() {
        return this.mIsActionViewExpanded;
    }

    public final MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener listener) {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.setOnActionExpandListener()");
    }

    public final /* bridge */ /* synthetic */ MenuItem setShowAsActionFlags(int i) {
        setShowAsAction(i);
        return this;
    }
}
