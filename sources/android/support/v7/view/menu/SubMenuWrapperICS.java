package android.support.v7.view.menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.internal.view.SupportSubMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

@TargetApi(14)
final class SubMenuWrapperICS extends MenuWrapperICS implements SubMenu {
    SubMenuWrapperICS(Context context, SupportSubMenu subMenu) {
        super(context, subMenu);
    }

    public final SubMenu setHeaderTitle(int titleRes) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderTitle(titleRes);
        return this;
    }

    public final SubMenu setHeaderTitle(CharSequence title) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderTitle(title);
        return this;
    }

    public final SubMenu setHeaderIcon(int iconRes) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderIcon(iconRes);
        return this;
    }

    public final SubMenu setHeaderIcon(Drawable icon) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderIcon(icon);
        return this;
    }

    public final SubMenu setHeaderView(View view) {
        ((SupportSubMenu) this.mWrappedObject).setHeaderView(view);
        return this;
    }

    public final void clearHeader() {
        ((SupportSubMenu) this.mWrappedObject).clearHeader();
    }

    public final SubMenu setIcon(int iconRes) {
        ((SupportSubMenu) this.mWrappedObject).setIcon(iconRes);
        return this;
    }

    public final SubMenu setIcon(Drawable icon) {
        ((SupportSubMenu) this.mWrappedObject).setIcon(icon);
        return this;
    }

    public final MenuItem getItem() {
        return getMenuItemWrapper(((SupportSubMenu) this.mWrappedObject).getItem());
    }
}
