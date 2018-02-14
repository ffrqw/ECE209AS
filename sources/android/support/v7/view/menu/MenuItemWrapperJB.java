package android.support.v7.view.menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.ActionProvider;
import android.view.ActionProvider.VisibilityListener;
import android.view.MenuItem;
import android.view.View;

@TargetApi(16)
final class MenuItemWrapperJB extends MenuItemWrapperICS {

    class ActionProviderWrapperJB extends ActionProviderWrapper implements VisibilityListener {
        ActionProvider.VisibilityListener mListener;

        public ActionProviderWrapperJB(Context context, android.view.ActionProvider inner) {
            super(context, inner);
        }

        public final View onCreateActionView(MenuItem forItem) {
            return this.mInner.onCreateActionView(forItem);
        }

        public final boolean overridesItemVisibility() {
            return this.mInner.overridesItemVisibility();
        }

        public final boolean isVisible() {
            return this.mInner.isVisible();
        }

        public final void setVisibilityListener(ActionProvider.VisibilityListener listener) {
            this.mListener = listener;
            this.mInner.setVisibilityListener(this);
        }

        public final void onActionProviderVisibilityChanged(boolean isVisible) {
            if (this.mListener != null) {
                this.mListener.onActionProviderVisibilityChanged$1385ff();
            }
        }
    }

    MenuItemWrapperJB(Context context, SupportMenuItem object) {
        super(context, object);
    }

    final ActionProviderWrapper createActionProviderWrapper(android.view.ActionProvider provider) {
        return new ActionProviderWrapperJB(this.mContext, provider);
    }
}
