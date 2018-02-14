package android.support.v7.view.menu;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.rachio.iro.R;

final class MenuDialogHelper implements OnClickListener, OnDismissListener, OnKeyListener, Callback {
    private AlertDialog mDialog;
    private MenuBuilder mMenu;
    ListMenuPresenter mPresenter;

    public MenuDialogHelper(MenuBuilder menu) {
        this.mMenu = menu;
    }

    public final void show(IBinder windowToken) {
        MenuBuilder menu = this.mMenu;
        Builder builder = new Builder(menu.getContext());
        this.mPresenter = new ListMenuPresenter(builder.getContext(), (int) R.layout.abc_list_menu_item_layout);
        this.mPresenter.setCallback(this);
        this.mMenu.addMenuPresenter(this.mPresenter);
        builder.setAdapter(this.mPresenter.getAdapter(), this);
        View headerView = menu.mHeaderView;
        if (headerView != null) {
            builder.setCustomTitle(headerView);
        } else {
            builder.setIcon(menu.mHeaderIcon).setTitle(menu.mHeaderTitle);
        }
        builder.setOnKeyListener(this);
        this.mDialog = builder.create();
        this.mDialog.setOnDismissListener(this);
        LayoutParams lp = this.mDialog.getWindow().getAttributes();
        lp.type = 1003;
        lp.flags |= 131072;
        this.mDialog.show();
    }

    public final boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == 82 || keyCode == 4) {
            Window win;
            View decor;
            DispatcherState ds;
            if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                win = this.mDialog.getWindow();
                if (win != null) {
                    decor = win.getDecorView();
                    if (decor != null) {
                        ds = decor.getKeyDispatcherState();
                        if (ds != null) {
                            ds.startTracking(event, this);
                            return true;
                        }
                    }
                }
            } else if (event.getAction() == 1 && !event.isCanceled()) {
                win = this.mDialog.getWindow();
                if (win != null) {
                    decor = win.getDecorView();
                    if (decor != null) {
                        ds = decor.getKeyDispatcherState();
                        if (ds != null && ds.isTracking(event)) {
                            this.mMenu.close(true);
                            dialog.dismiss();
                            return true;
                        }
                    }
                }
            }
        }
        return this.mMenu.performShortcut(keyCode, event, 0);
    }

    public final void onDismiss(DialogInterface dialog) {
        this.mPresenter.onCloseMenu(this.mMenu, true);
    }

    public final void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        if ((allMenusAreClosing || menu == this.mMenu) && this.mDialog != null) {
            this.mDialog.dismiss();
        }
    }

    public final boolean onOpenSubMenu(MenuBuilder subMenu) {
        return false;
    }

    public final void onClick(DialogInterface dialog, int which) {
        this.mMenu.performItemAction((MenuItemImpl) this.mPresenter.getAdapter().getItem(which), 0);
    }
}
