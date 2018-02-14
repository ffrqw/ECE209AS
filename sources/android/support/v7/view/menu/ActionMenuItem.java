package android.support.v7.view.menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

public final class ActionMenuItem implements SupportMenuItem {
    private final int mCategoryOrder;
    private OnMenuItemClickListener mClickListener;
    private Context mContext;
    private int mFlags = 16;
    private final int mGroup;
    private Drawable mIconDrawable;
    private int mIconResId = 0;
    private final int mId;
    private Intent mIntent;
    private final int mOrdering;
    private char mShortcutAlphabeticChar;
    private char mShortcutNumericChar;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;

    public ActionMenuItem(Context context, int group, int id, int categoryOrder, int ordering, CharSequence title) {
        this.mContext = context;
        this.mId = 16908332;
        this.mGroup = 0;
        this.mCategoryOrder = 0;
        this.mOrdering = 0;
        this.mTitle = title;
    }

    public final char getAlphabeticShortcut() {
        return this.mShortcutAlphabeticChar;
    }

    public final int getGroupId() {
        return this.mGroup;
    }

    public final Drawable getIcon() {
        return this.mIconDrawable;
    }

    public final Intent getIntent() {
        return this.mIntent;
    }

    public final int getItemId() {
        return this.mId;
    }

    public final ContextMenuInfo getMenuInfo() {
        return null;
    }

    public final char getNumericShortcut() {
        return this.mShortcutNumericChar;
    }

    public final int getOrder() {
        return this.mOrdering;
    }

    public final SubMenu getSubMenu() {
        return null;
    }

    public final CharSequence getTitle() {
        return this.mTitle;
    }

    public final CharSequence getTitleCondensed() {
        return this.mTitleCondensed != null ? this.mTitleCondensed : this.mTitle;
    }

    public final boolean hasSubMenu() {
        return false;
    }

    public final boolean isCheckable() {
        return (this.mFlags & 1) != 0;
    }

    public final boolean isChecked() {
        return (this.mFlags & 2) != 0;
    }

    public final boolean isEnabled() {
        return (this.mFlags & 16) != 0;
    }

    public final boolean isVisible() {
        return (this.mFlags & 8) == 0;
    }

    public final MenuItem setAlphabeticShortcut(char alphaChar) {
        this.mShortcutAlphabeticChar = alphaChar;
        return this;
    }

    public final MenuItem setCheckable(boolean checkable) {
        this.mFlags = (checkable ? 1 : 0) | (this.mFlags & -2);
        return this;
    }

    public final MenuItem setChecked(boolean checked) {
        this.mFlags = (checked ? 2 : 0) | (this.mFlags & -3);
        return this;
    }

    public final MenuItem setEnabled(boolean enabled) {
        this.mFlags = (enabled ? 16 : 0) | (this.mFlags & -17);
        return this;
    }

    public final MenuItem setIcon(Drawable icon) {
        this.mIconDrawable = icon;
        this.mIconResId = 0;
        return this;
    }

    public final MenuItem setIcon(int iconRes) {
        this.mIconResId = iconRes;
        this.mIconDrawable = ContextCompat.getDrawable(this.mContext, iconRes);
        return this;
    }

    public final MenuItem setIntent(Intent intent) {
        this.mIntent = intent;
        return this;
    }

    public final MenuItem setNumericShortcut(char numericChar) {
        this.mShortcutNumericChar = numericChar;
        return this;
    }

    public final MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        this.mClickListener = menuItemClickListener;
        return this;
    }

    public final MenuItem setShortcut(char numericChar, char alphaChar) {
        this.mShortcutNumericChar = numericChar;
        this.mShortcutAlphabeticChar = alphaChar;
        return this;
    }

    public final MenuItem setTitle(CharSequence title) {
        this.mTitle = title;
        return this;
    }

    public final MenuItem setTitle(int title) {
        this.mTitle = this.mContext.getResources().getString(title);
        return this;
    }

    public final MenuItem setTitleCondensed(CharSequence title) {
        this.mTitleCondensed = title;
        return this;
    }

    public final MenuItem setVisible(boolean visible) {
        this.mFlags = (visible ? 0 : 8) | (this.mFlags & 8);
        return this;
    }

    public final void setShowAsAction(int show) {
    }

    public final View getActionView() {
        return null;
    }

    public final MenuItem setActionProvider(ActionProvider actionProvider) {
        throw new UnsupportedOperationException();
    }

    public final ActionProvider getActionProvider() {
        throw new UnsupportedOperationException();
    }

    public final android.support.v4.view.ActionProvider getSupportActionProvider() {
        return null;
    }

    public final SupportMenuItem setSupportActionProvider(android.support.v4.view.ActionProvider actionProvider) {
        throw new UnsupportedOperationException();
    }

    public final boolean expandActionView() {
        return false;
    }

    public final boolean collapseActionView() {
        return false;
    }

    public final boolean isActionViewExpanded() {
        return false;
    }

    public final MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
        throw new UnsupportedOperationException();
    }

    public final SupportMenuItem setSupportOnActionExpandListener(MenuItemCompat.OnActionExpandListener listener) {
        return this;
    }

    public final /* bridge */ /* synthetic */ MenuItem setShowAsActionFlags(int i) {
        setShowAsAction(i);
        return this;
    }
}
