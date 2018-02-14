package android.support.v7.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.widget.MenuItemHoverListener;
import android.support.v7.widget.MenuPopupWindow;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import com.rachio.iro.R;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

final class CascadingMenuPopup extends MenuPopup implements MenuPresenter, OnKeyListener, OnDismissListener {
    private View mAnchorView;
    private final Context mContext;
    private int mDropDownGravity = 0;
    private boolean mForceShowIcon;
    private final OnGlobalLayoutListener mGlobalLayoutListener = new OnGlobalLayoutListener() {
        public final void onGlobalLayout() {
            if (CascadingMenuPopup.this.isShowing() && CascadingMenuPopup.this.mShowingMenus.size() > 0 && !((CascadingMenuInfo) CascadingMenuPopup.this.mShowingMenus.get(0)).window.isModal()) {
                View anchor = CascadingMenuPopup.this.mShownAnchorView;
                if (anchor == null || !anchor.isShown()) {
                    CascadingMenuPopup.this.dismiss();
                    return;
                }
                for (CascadingMenuInfo info : CascadingMenuPopup.this.mShowingMenus) {
                    info.window.show();
                }
            }
        }
    };
    private boolean mHasXOffset;
    private boolean mHasYOffset;
    private int mLastPosition;
    private final MenuItemHoverListener mMenuItemHoverListener = new MenuItemHoverListener() {
        public final void onItemHoverExit(MenuBuilder menu, MenuItem item) {
            CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages(menu);
        }

        public final void onItemHoverEnter(final MenuBuilder menu, final MenuItem item) {
            CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages(null);
            int menuIndex = -1;
            int count = CascadingMenuPopup.this.mShowingMenus.size();
            for (int i = 0; i < count; i++) {
                if (menu == ((CascadingMenuInfo) CascadingMenuPopup.this.mShowingMenus.get(i)).menu) {
                    menuIndex = i;
                    break;
                }
            }
            if (menuIndex != -1) {
                CascadingMenuInfo nextInfo;
                int nextIndex = menuIndex + 1;
                if (nextIndex < CascadingMenuPopup.this.mShowingMenus.size()) {
                    nextInfo = (CascadingMenuInfo) CascadingMenuPopup.this.mShowingMenus.get(nextIndex);
                } else {
                    nextInfo = null;
                }
                CascadingMenuPopup.this.mSubMenuHoverHandler.postAtTime(new Runnable() {
                    public final void run() {
                        if (nextInfo != null) {
                            CascadingMenuPopup.this.mShouldCloseImmediately = true;
                            nextInfo.menu.close(false);
                            CascadingMenuPopup.this.mShouldCloseImmediately = false;
                        }
                        if (item.isEnabled() && item.hasSubMenu()) {
                            menu.performItemAction(item, 4);
                        }
                    }
                }, menu, SystemClock.uptimeMillis() + 200);
            }
        }
    };
    private final int mMenuMaxWidth;
    private OnDismissListener mOnDismissListener;
    private final boolean mOverflowOnly;
    private final List<MenuBuilder> mPendingMenus = new LinkedList();
    private final int mPopupStyleAttr;
    private final int mPopupStyleRes;
    private Callback mPresenterCallback;
    private int mRawDropDownGravity = 0;
    boolean mShouldCloseImmediately;
    private boolean mShowTitle;
    final List<CascadingMenuInfo> mShowingMenus = new ArrayList();
    View mShownAnchorView;
    final Handler mSubMenuHoverHandler;
    private ViewTreeObserver mTreeObserver;
    private int mXOffset;
    private int mYOffset;

    private static class CascadingMenuInfo {
        public final MenuBuilder menu;
        public final int position;
        public final MenuPopupWindow window;

        public CascadingMenuInfo(MenuPopupWindow window, MenuBuilder menu, int position) {
            this.window = window;
            this.menu = menu;
            this.position = position;
        }
    }

    public CascadingMenuPopup(Context context, View anchor, int popupStyleAttr, int popupStyleRes, boolean overflowOnly) {
        this.mContext = context;
        this.mAnchorView = anchor;
        this.mPopupStyleAttr = popupStyleAttr;
        this.mPopupStyleRes = popupStyleRes;
        this.mOverflowOnly = overflowOnly;
        this.mForceShowIcon = false;
        this.mLastPosition = getInitialMenuPosition();
        Resources res = context.getResources();
        this.mMenuMaxWidth = Math.max(res.getDisplayMetrics().widthPixels / 2, res.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
        this.mSubMenuHoverHandler = new Handler();
    }

    public final void setForceShowIcon(boolean forceShow) {
        this.mForceShowIcon = forceShow;
    }

    public final void show() {
        if (!isShowing()) {
            for (MenuBuilder menu : this.mPendingMenus) {
                showMenu(menu);
            }
            this.mPendingMenus.clear();
            this.mShownAnchorView = this.mAnchorView;
            if (this.mShownAnchorView != null) {
                boolean addGlobalListener = this.mTreeObserver == null;
                this.mTreeObserver = this.mShownAnchorView.getViewTreeObserver();
                if (addGlobalListener) {
                    this.mTreeObserver.addOnGlobalLayoutListener(this.mGlobalLayoutListener);
                }
            }
        }
    }

    public final void dismiss() {
        int length = this.mShowingMenus.size();
        if (length > 0) {
            CascadingMenuInfo[] addedMenus = (CascadingMenuInfo[]) this.mShowingMenus.toArray(new CascadingMenuInfo[length]);
            for (int i = length - 1; i >= 0; i--) {
                CascadingMenuInfo info = addedMenus[i];
                if (info.window.isShowing()) {
                    info.window.dismiss();
                }
            }
        }
    }

    public final boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != 1 || keyCode != 82) {
            return false;
        }
        dismiss();
        return true;
    }

    private int getInitialMenuPosition() {
        if (ViewCompat.getLayoutDirection(this.mAnchorView) == 1) {
            return 0;
        }
        return 1;
    }

    public final void addMenu(MenuBuilder menu) {
        menu.addMenuPresenter(this, this.mContext);
        if (isShowing()) {
            showMenu(menu);
        } else {
            this.mPendingMenus.add(menu);
        }
    }

    private void showMenu(MenuBuilder menu) {
        CascadingMenuInfo parentInfo;
        View parentView;
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        MenuAdapter adapter = new MenuAdapter(menu, inflater, this.mOverflowOnly);
        if (!isShowing() && this.mForceShowIcon) {
            adapter.setForceShowIcon(true);
        } else if (isShowing()) {
            adapter.setForceShowIcon(MenuPopup.shouldPreserveIconSpacing(menu));
        }
        int menuWidth = MenuPopup.measureIndividualMenuWidth(adapter, null, this.mContext, this.mMenuMaxWidth);
        MenuPopupWindow popupWindow = new MenuPopupWindow(this.mContext, null, this.mPopupStyleAttr, this.mPopupStyleRes);
        popupWindow.setHoverListener(this.mMenuItemHoverListener);
        popupWindow.setOnItemClickListener(this);
        popupWindow.setOnDismissListener(this);
        popupWindow.setAnchorView(this.mAnchorView);
        popupWindow.setDropDownGravity(this.mDropDownGravity);
        popupWindow.setModal(true);
        popupWindow.setAdapter(adapter);
        popupWindow.setContentWidth(menuWidth);
        popupWindow.setDropDownGravity(this.mDropDownGravity);
        if (this.mShowingMenus.size() > 0) {
            int i;
            MenuItem menuItem;
            parentInfo = (CascadingMenuInfo) this.mShowingMenus.get(this.mShowingMenus.size() - 1);
            MenuBuilder menuBuilder = parentInfo.menu;
            int size = menuBuilder.size();
            for (i = 0; i < size; i++) {
                MenuItem item = menuBuilder.getItem(i);
                if (item.hasSubMenu() && menu == item.getSubMenu()) {
                    menuItem = item;
                    break;
                }
            }
            menuItem = null;
            if (menuItem == null) {
                parentView = null;
            } else {
                MenuAdapter menuAdapter;
                int i2;
                ListView listView = parentInfo.window.getListView();
                ListAdapter adapter2 = listView.getAdapter();
                if (adapter2 instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) adapter2;
                    i = headerViewListAdapter.getHeadersCount();
                    menuAdapter = (MenuAdapter) headerViewListAdapter.getWrappedAdapter();
                } else {
                    i = 0;
                    menuAdapter = (MenuAdapter) adapter2;
                }
                int count = menuAdapter.getCount();
                for (int i3 = 0; i3 < count; i3++) {
                    if (menuItem == menuAdapter.getItem(i3)) {
                        i2 = i3;
                        break;
                    }
                }
                i2 = -1;
                if (i2 == -1) {
                    parentView = null;
                } else {
                    i2 = (i2 + i) - listView.getFirstVisiblePosition();
                    if (i2 < 0 || i2 >= listView.getChildCount()) {
                        parentView = null;
                    } else {
                        parentView = listView.getChildAt(i2);
                    }
                }
            }
        } else {
            parentInfo = null;
            parentView = null;
        }
        if (parentView != null) {
            int nextMenuPosition;
            int x;
            popupWindow.setTouchModal(false);
            popupWindow.setEnterTransition(null);
            ListView listView2 = ((CascadingMenuInfo) this.mShowingMenus.get(this.mShowingMenus.size() - 1)).window.getListView();
            int[] iArr = new int[2];
            listView2.getLocationOnScreen(iArr);
            Rect rect = new Rect();
            this.mShownAnchorView.getWindowVisibleDisplayFrame(rect);
            if (this.mLastPosition == 1) {
                if ((listView2.getWidth() + iArr[0]) + menuWidth <= rect.right) {
                    nextMenuPosition = 1;
                }
                nextMenuPosition = 0;
            } else {
                if (iArr[0] - menuWidth < 0) {
                    nextMenuPosition = 1;
                }
                nextMenuPosition = 0;
            }
            boolean showOnRight = nextMenuPosition == 1;
            this.mLastPosition = nextMenuPosition;
            int[] tempLocation = new int[2];
            parentView.getLocationInWindow(tempLocation);
            int parentOffsetLeft = parentInfo.window.getHorizontalOffset() + tempLocation[0];
            int parentOffsetTop = parentInfo.window.getVerticalOffset() + tempLocation[1];
            if ((this.mDropDownGravity & 5) == 5) {
                if (showOnRight) {
                    x = parentOffsetLeft + menuWidth;
                } else {
                    x = parentOffsetLeft - parentView.getWidth();
                }
            } else if (showOnRight) {
                x = parentOffsetLeft + parentView.getWidth();
            } else {
                x = parentOffsetLeft - menuWidth;
            }
            popupWindow.setHorizontalOffset(x);
            popupWindow.setVerticalOffset(parentOffsetTop);
        } else {
            if (this.mHasXOffset) {
                popupWindow.setHorizontalOffset(this.mXOffset);
            }
            if (this.mHasYOffset) {
                popupWindow.setVerticalOffset(this.mYOffset);
            }
            popupWindow.setEpicenterBounds(getEpicenterBounds());
        }
        this.mShowingMenus.add(new CascadingMenuInfo(popupWindow, menu, this.mLastPosition));
        popupWindow.show();
        if (parentInfo == null && this.mShowTitle && menu.mHeaderTitle != null) {
            ListView listView3 = popupWindow.getListView();
            View titleItemView = (FrameLayout) inflater.inflate(R.layout.abc_popup_menu_header_item_layout, listView3, false);
            TextView titleView = (TextView) titleItemView.findViewById(16908310);
            titleItemView.setEnabled(false);
            titleView.setText(menu.mHeaderTitle);
            listView3.addHeaderView(titleItemView, null, false);
            popupWindow.show();
        }
    }

    public final boolean isShowing() {
        return this.mShowingMenus.size() > 0 && ((CascadingMenuInfo) this.mShowingMenus.get(0)).window.isShowing();
    }

    public final void onDismiss() {
        CascadingMenuInfo dismissedInfo = null;
        int count = this.mShowingMenus.size();
        for (int i = 0; i < count; i++) {
            CascadingMenuInfo info = (CascadingMenuInfo) this.mShowingMenus.get(i);
            if (!info.window.isShowing()) {
                dismissedInfo = info;
                break;
            }
        }
        if (dismissedInfo != null) {
            dismissedInfo.menu.close(false);
        }
    }

    public final void updateMenuView(boolean cleared) {
        for (CascadingMenuInfo info : this.mShowingMenus) {
            MenuPopup.toMenuAdapter(info.window.getListView().getAdapter()).notifyDataSetChanged();
        }
    }

    public final void setCallback(Callback cb) {
        this.mPresenterCallback = cb;
    }

    public final boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        for (CascadingMenuInfo info : this.mShowingMenus) {
            if (subMenu == info.menu) {
                info.window.getListView().requestFocus();
                return true;
            }
        }
        if (!subMenu.hasVisibleItems()) {
            return false;
        }
        addMenu(subMenu);
        if (this.mPresenterCallback == null) {
            return true;
        }
        this.mPresenterCallback.onOpenSubMenu(subMenu);
        return true;
    }

    public final void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        int size = this.mShowingMenus.size();
        int menuIndex = 0;
        while (menuIndex < size) {
            if (menu == ((CascadingMenuInfo) this.mShowingMenus.get(menuIndex)).menu) {
                break;
            }
            menuIndex++;
        }
        menuIndex = -1;
        if (menuIndex >= 0) {
            int nextMenuIndex = menuIndex + 1;
            if (nextMenuIndex < this.mShowingMenus.size()) {
                ((CascadingMenuInfo) this.mShowingMenus.get(nextMenuIndex)).menu.close(false);
            }
            CascadingMenuInfo info = (CascadingMenuInfo) this.mShowingMenus.remove(menuIndex);
            info.menu.removeMenuPresenter(this);
            if (this.mShouldCloseImmediately) {
                info.window.setExitTransition(null);
                info.window.setAnimationStyle(0);
            }
            info.window.dismiss();
            int count = this.mShowingMenus.size();
            if (count > 0) {
                this.mLastPosition = ((CascadingMenuInfo) this.mShowingMenus.get(count - 1)).position;
            } else {
                this.mLastPosition = getInitialMenuPosition();
            }
            if (count == 0) {
                dismiss();
                if (this.mPresenterCallback != null) {
                    this.mPresenterCallback.onCloseMenu(menu, true);
                }
                if (this.mTreeObserver != null) {
                    if (this.mTreeObserver.isAlive()) {
                        this.mTreeObserver.removeGlobalOnLayoutListener(this.mGlobalLayoutListener);
                    }
                    this.mTreeObserver = null;
                }
                this.mOnDismissListener.onDismiss();
            } else if (allMenusAreClosing) {
                ((CascadingMenuInfo) this.mShowingMenus.get(0)).menu.close(false);
            }
        }
    }

    public final boolean flagActionItems() {
        return false;
    }

    public final void setGravity(int dropDownGravity) {
        if (this.mRawDropDownGravity != dropDownGravity) {
            this.mRawDropDownGravity = dropDownGravity;
            this.mDropDownGravity = GravityCompat.getAbsoluteGravity(dropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView));
        }
    }

    public final void setAnchorView(View anchor) {
        if (this.mAnchorView != anchor) {
            this.mAnchorView = anchor;
            this.mDropDownGravity = GravityCompat.getAbsoluteGravity(this.mRawDropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView));
        }
    }

    public final void setOnDismissListener(OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    public final ListView getListView() {
        if (this.mShowingMenus.isEmpty()) {
            return null;
        }
        return ((CascadingMenuInfo) this.mShowingMenus.get(this.mShowingMenus.size() - 1)).window.getListView();
    }

    public final void setHorizontalOffset(int x) {
        this.mHasXOffset = true;
        this.mXOffset = x;
    }

    public final void setVerticalOffset(int y) {
        this.mHasYOffset = true;
        this.mYOffset = y;
    }

    public final void setShowTitle(boolean showTitle) {
        this.mShowTitle = showTitle;
    }

    protected final boolean closeMenuOnSubMenuOpened() {
        return false;
    }
}
