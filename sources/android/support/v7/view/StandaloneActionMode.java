package android.support.v7.view;

import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.widget.ActionBarContextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.lang.ref.WeakReference;

public final class StandaloneActionMode extends ActionMode implements Callback {
    private ActionMode.Callback mCallback;
    private Context mContext;
    private ActionBarContextView mContextView;
    private WeakReference<View> mCustomView;
    private boolean mFinished;
    private boolean mFocusable;
    private MenuBuilder mMenu;

    public StandaloneActionMode(Context context, ActionBarContextView view, ActionMode.Callback callback, boolean isFocusable) {
        this.mContext = context;
        this.mContextView = view;
        this.mCallback = callback;
        this.mMenu = new MenuBuilder(view.getContext()).setDefaultShowAsAction(1);
        this.mMenu.setCallback(this);
        this.mFocusable = isFocusable;
    }

    public final void setTitle(CharSequence title) {
        this.mContextView.setTitle(title);
    }

    public final void setSubtitle(CharSequence subtitle) {
        this.mContextView.setSubtitle(subtitle);
    }

    public final void setTitle(int resId) {
        setTitle(this.mContext.getString(resId));
    }

    public final void setSubtitle(int resId) {
        setSubtitle(this.mContext.getString(resId));
    }

    public final void setTitleOptionalHint(boolean titleOptional) {
        super.setTitleOptionalHint(titleOptional);
        this.mContextView.setTitleOptional(titleOptional);
    }

    public final boolean isTitleOptional() {
        return this.mContextView.isTitleOptional();
    }

    public final void setCustomView(View view) {
        this.mContextView.setCustomView(view);
        this.mCustomView = view != null ? new WeakReference(view) : null;
    }

    public final void invalidate() {
        this.mCallback.onPrepareActionMode(this, this.mMenu);
    }

    public final void finish() {
        if (!this.mFinished) {
            this.mFinished = true;
            this.mContextView.sendAccessibilityEvent(32);
            this.mCallback.onDestroyActionMode(this);
        }
    }

    public final Menu getMenu() {
        return this.mMenu;
    }

    public final CharSequence getTitle() {
        return this.mContextView.getTitle();
    }

    public final CharSequence getSubtitle() {
        return this.mContextView.getSubtitle();
    }

    public final View getCustomView() {
        return this.mCustomView != null ? (View) this.mCustomView.get() : null;
    }

    public final MenuInflater getMenuInflater() {
        return new SupportMenuInflater(this.mContextView.getContext());
    }

    public final boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        return this.mCallback.onActionItemClicked(this, item);
    }

    public final void onMenuModeChange(MenuBuilder menu) {
        invalidate();
        this.mContextView.showOverflowMenu();
    }
}
