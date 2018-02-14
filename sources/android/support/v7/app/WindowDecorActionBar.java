package android.support.v7.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.view.ActionBarPolicy;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.ActionBarContainer;
import android.support.v7.widget.ActionBarContextView;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.ActionBarOverlayLayout.ActionBarVisibilityCallback;
import android.support.v7.widget.DecorToolbar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.rachio.iro.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class WindowDecorActionBar extends ActionBar implements ActionBarVisibilityCallback {
    static final /* synthetic */ boolean $assertionsDisabled = (!WindowDecorActionBar.class.desiredAssertionStatus());
    private static final boolean ALLOW_SHOW_HIDE_ANIMATIONS;
    private static final Interpolator sHideInterpolator = new AccelerateInterpolator();
    private static final Interpolator sShowInterpolator = new DecelerateInterpolator();
    ActionModeImpl mActionMode;
    private Activity mActivity;
    ActionBarContainer mContainerView;
    boolean mContentAnimations = true;
    View mContentView;
    Context mContext;
    ActionBarContextView mContextView;
    private int mCurWindowVisibility = 0;
    ViewPropertyAnimatorCompatSet mCurrentShowAnim;
    DecorToolbar mDecorToolbar;
    ActionMode mDeferredDestroyActionMode;
    Callback mDeferredModeDestroyCallback;
    private Dialog mDialog;
    private boolean mDisplayHomeAsUpSet;
    private boolean mHasEmbeddedTabs;
    boolean mHiddenByApp;
    boolean mHiddenBySystem;
    final ViewPropertyAnimatorListener mHideListener = new ViewPropertyAnimatorListenerAdapter() {
        public final void onAnimationEnd(View view) {
            if (WindowDecorActionBar.this.mContentAnimations && WindowDecorActionBar.this.mContentView != null) {
                ViewCompat.setTranslationY(WindowDecorActionBar.this.mContentView, 0.0f);
                ViewCompat.setTranslationY(WindowDecorActionBar.this.mContainerView, 0.0f);
            }
            WindowDecorActionBar.this.mContainerView.setVisibility(8);
            WindowDecorActionBar.this.mContainerView.setTransitioning(false);
            WindowDecorActionBar.this.mCurrentShowAnim = null;
            WindowDecorActionBar windowDecorActionBar = WindowDecorActionBar.this;
            if (windowDecorActionBar.mDeferredModeDestroyCallback != null) {
                windowDecorActionBar.mDeferredModeDestroyCallback.onDestroyActionMode(windowDecorActionBar.mDeferredDestroyActionMode);
                windowDecorActionBar.mDeferredDestroyActionMode = null;
                windowDecorActionBar.mDeferredModeDestroyCallback = null;
            }
            if (WindowDecorActionBar.this.mOverlayLayout != null) {
                ViewCompat.requestApplyInsets(WindowDecorActionBar.this.mOverlayLayout);
            }
        }
    };
    boolean mHideOnContentScroll;
    private boolean mLastMenuVisibility;
    private ArrayList<Object> mMenuVisibilityListeners = new ArrayList();
    private boolean mNowShowing = true;
    ActionBarOverlayLayout mOverlayLayout;
    private int mSavedTabPosition = -1;
    private boolean mShowHideAnimationEnabled;
    final ViewPropertyAnimatorListener mShowListener = new ViewPropertyAnimatorListenerAdapter() {
        public final void onAnimationEnd(View view) {
            WindowDecorActionBar.this.mCurrentShowAnim = null;
            WindowDecorActionBar.this.mContainerView.requestLayout();
        }
    };
    private boolean mShowingForMode;
    private ArrayList<Object> mTabs = new ArrayList();
    private Context mThemedContext;
    final ViewPropertyAnimatorUpdateListener mUpdateListener = new ViewPropertyAnimatorUpdateListener() {
        public final void onAnimationUpdate$3c7ec8c3() {
            ((View) WindowDecorActionBar.this.mContainerView.getParent()).invalidate();
        }
    };

    public class ActionModeImpl extends ActionMode implements MenuBuilder.Callback {
        private final Context mActionModeContext;
        private Callback mCallback;
        private WeakReference<View> mCustomView;
        private final MenuBuilder mMenu;

        public ActionModeImpl(Context context, Callback callback) {
            this.mActionModeContext = context;
            this.mCallback = callback;
            this.mMenu = new MenuBuilder(context).setDefaultShowAsAction(1);
            this.mMenu.setCallback(this);
        }

        public final MenuInflater getMenuInflater() {
            return new SupportMenuInflater(this.mActionModeContext);
        }

        public final Menu getMenu() {
            return this.mMenu;
        }

        public final void finish() {
            if (WindowDecorActionBar.this.mActionMode == this) {
                if (WindowDecorActionBar.checkShowingFlags(WindowDecorActionBar.this.mHiddenByApp, WindowDecorActionBar.this.mHiddenBySystem, false)) {
                    this.mCallback.onDestroyActionMode(this);
                } else {
                    WindowDecorActionBar.this.mDeferredDestroyActionMode = this;
                    WindowDecorActionBar.this.mDeferredModeDestroyCallback = this.mCallback;
                }
                this.mCallback = null;
                WindowDecorActionBar.this.animateToMode(false);
                WindowDecorActionBar.this.mContextView.closeMode();
                WindowDecorActionBar.this.mDecorToolbar.getViewGroup().sendAccessibilityEvent(32);
                WindowDecorActionBar.this.mOverlayLayout.setHideOnContentScrollEnabled(WindowDecorActionBar.this.mHideOnContentScroll);
                WindowDecorActionBar.this.mActionMode = null;
            }
        }

        public final void invalidate() {
            if (WindowDecorActionBar.this.mActionMode == this) {
                this.mMenu.stopDispatchingItemsChanged();
                try {
                    this.mCallback.onPrepareActionMode(this, this.mMenu);
                } finally {
                    this.mMenu.startDispatchingItemsChanged();
                }
            }
        }

        public final boolean dispatchOnCreate() {
            this.mMenu.stopDispatchingItemsChanged();
            try {
                boolean onCreateActionMode = this.mCallback.onCreateActionMode(this, this.mMenu);
                return onCreateActionMode;
            } finally {
                this.mMenu.startDispatchingItemsChanged();
            }
        }

        public final void setCustomView(View view) {
            WindowDecorActionBar.this.mContextView.setCustomView(view);
            this.mCustomView = new WeakReference(view);
        }

        public final void setSubtitle(CharSequence subtitle) {
            WindowDecorActionBar.this.mContextView.setSubtitle(subtitle);
        }

        public final void setTitle(CharSequence title) {
            WindowDecorActionBar.this.mContextView.setTitle(title);
        }

        public final void setTitle(int resId) {
            setTitle(WindowDecorActionBar.this.mContext.getResources().getString(resId));
        }

        public final void setSubtitle(int resId) {
            setSubtitle(WindowDecorActionBar.this.mContext.getResources().getString(resId));
        }

        public final CharSequence getTitle() {
            return WindowDecorActionBar.this.mContextView.getTitle();
        }

        public final CharSequence getSubtitle() {
            return WindowDecorActionBar.this.mContextView.getSubtitle();
        }

        public final void setTitleOptionalHint(boolean titleOptional) {
            super.setTitleOptionalHint(titleOptional);
            WindowDecorActionBar.this.mContextView.setTitleOptional(titleOptional);
        }

        public final boolean isTitleOptional() {
            return WindowDecorActionBar.this.mContextView.isTitleOptional();
        }

        public final View getCustomView() {
            return this.mCustomView != null ? (View) this.mCustomView.get() : null;
        }

        public final boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            if (this.mCallback != null) {
                return this.mCallback.onActionItemClicked(this, item);
            }
            return false;
        }

        public final void onMenuModeChange(MenuBuilder menu) {
            if (this.mCallback != null) {
                invalidate();
                WindowDecorActionBar.this.mContextView.showOverflowMenu();
            }
        }
    }

    static {
        boolean z = true;
        if (VERSION.SDK_INT < 14) {
            z = false;
        }
        ALLOW_SHOW_HIDE_ANIMATIONS = z;
    }

    public WindowDecorActionBar(Activity activity, boolean overlayMode) {
        this.mActivity = activity;
        View decor = activity.getWindow().getDecorView();
        init(decor);
        if (!overlayMode) {
            this.mContentView = decor.findViewById(16908290);
        }
    }

    public WindowDecorActionBar(Dialog dialog) {
        this.mDialog = dialog;
        init(dialog.getWindow().getDecorView());
    }

    private void init(View decor) {
        DecorToolbar decorToolbar;
        this.mOverlayLayout = (ActionBarOverlayLayout) decor.findViewById(R.id.decor_content_parent);
        if (this.mOverlayLayout != null) {
            this.mOverlayLayout.setActionBarVisibilityCallback(this);
        }
        View findViewById = decor.findViewById(R.id.action_bar);
        if (findViewById instanceof DecorToolbar) {
            decorToolbar = (DecorToolbar) findViewById;
        } else if (findViewById instanceof Toolbar) {
            decorToolbar = ((Toolbar) findViewById).getWrapper();
        } else {
            throw new IllegalStateException(new StringBuilder("Can't make a decor toolbar out of ").append(findViewById).toString() != null ? findViewById.getClass().getSimpleName() : "null");
        }
        this.mDecorToolbar = decorToolbar;
        this.mContextView = (ActionBarContextView) decor.findViewById(R.id.action_context_bar);
        this.mContainerView = (ActionBarContainer) decor.findViewById(R.id.action_bar_container);
        if (this.mDecorToolbar == null || this.mContextView == null || this.mContainerView == null) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with a compatible window decor layout");
        }
        boolean homeAsUp;
        boolean z;
        this.mContext = this.mDecorToolbar.getContext();
        if ((this.mDecorToolbar.getDisplayOptions() & 4) != 0) {
            homeAsUp = true;
        } else {
            homeAsUp = false;
        }
        if (homeAsUp) {
            this.mDisplayHomeAsUpSet = true;
        }
        ActionBarPolicy abp = ActionBarPolicy.get(this.mContext);
        if (abp.enableHomeButtonByDefault() || homeAsUp) {
            z = true;
        } else {
            z = false;
        }
        this.mDecorToolbar.setHomeButtonEnabled(z);
        setHasEmbeddedTabs(abp.hasEmbeddedTabs());
        TypedArray a = this.mContext.obtainStyledAttributes(null, android.support.v7.appcompat.R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        if (a.getBoolean(android.support.v7.appcompat.R.styleable.ActionBar_hideOnContentScroll, false)) {
            if (this.mOverlayLayout.isInOverlayMode()) {
                this.mHideOnContentScroll = true;
                this.mOverlayLayout.setHideOnContentScrollEnabled(true);
            } else {
                throw new IllegalStateException("Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to enable hide on content scroll");
            }
        }
        int elevation = a.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.ActionBar_elevation, 0);
        if (elevation != 0) {
            ViewCompat.setElevation(this.mContainerView, (float) elevation);
        }
        a.recycle();
    }

    public final void onConfigurationChanged(Configuration newConfig) {
        setHasEmbeddedTabs(ActionBarPolicy.get(this.mContext).hasEmbeddedTabs());
    }

    private void setHasEmbeddedTabs(boolean hasEmbeddedTabs) {
        boolean z;
        boolean z2 = true;
        this.mHasEmbeddedTabs = hasEmbeddedTabs;
        if (this.mHasEmbeddedTabs) {
            this.mContainerView.setTabContainer(null);
            this.mDecorToolbar.setEmbeddedTabView(null);
        } else {
            this.mDecorToolbar.setEmbeddedTabView(null);
            this.mContainerView.setTabContainer(null);
        }
        boolean isInTabMode;
        if (this.mDecorToolbar.getNavigationMode() == 2) {
            isInTabMode = true;
        } else {
            isInTabMode = false;
        }
        DecorToolbar decorToolbar = this.mDecorToolbar;
        if (this.mHasEmbeddedTabs || !isInTabMode) {
            z = false;
        } else {
            z = true;
        }
        decorToolbar.setCollapsible(z);
        ActionBarOverlayLayout actionBarOverlayLayout = this.mOverlayLayout;
        if (this.mHasEmbeddedTabs || !isInTabMode) {
            z2 = false;
        }
        actionBarOverlayLayout.setHasNonEmbeddedTabs(z2);
    }

    public void onWindowVisibilityChanged(int visibility) {
        this.mCurWindowVisibility = visibility;
    }

    public final void setShowHideAnimationEnabled(boolean enabled) {
        this.mShowHideAnimationEnabled = enabled;
        if (!enabled && this.mCurrentShowAnim != null) {
            this.mCurrentShowAnim.cancel();
        }
    }

    public final void dispatchMenuVisibilityChanged(boolean isVisible) {
        if (isVisible != this.mLastMenuVisibility) {
            this.mLastMenuVisibility = isVisible;
            int count = this.mMenuVisibilityListeners.size();
            for (int i = 0; i < count; i++) {
                this.mMenuVisibilityListeners.get(i);
            }
        }
    }

    public final void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
        setDisplayOptions(showHomeAsUp ? 4 : 0, 4);
    }

    public final void setDisplayShowTitleEnabled(boolean showTitle) {
        setDisplayOptions(showTitle ? 8 : 0, 8);
    }

    public final void setDisplayShowCustomEnabled(boolean showCustom) {
        setDisplayOptions(showCustom ? 16 : 0, 16);
    }

    public final void setTitle(int resId) {
        setTitle(this.mContext.getString(resId));
    }

    public final void setTitle(CharSequence title) {
        this.mDecorToolbar.setTitle(title);
    }

    public final void setWindowTitle(CharSequence title) {
        this.mDecorToolbar.setWindowTitle(title);
    }

    public final boolean requestFocus() {
        ViewGroup viewGroup = this.mDecorToolbar.getViewGroup();
        if (viewGroup == null || viewGroup.hasFocus()) {
            return false;
        }
        viewGroup.requestFocus();
        return true;
    }

    public final void setSubtitle(CharSequence subtitle) {
        this.mDecorToolbar.setSubtitle(subtitle);
    }

    private void setDisplayOptions(int options, int mask) {
        int current = this.mDecorToolbar.getDisplayOptions();
        if ((mask & 4) != 0) {
            this.mDisplayHomeAsUpSet = true;
        }
        this.mDecorToolbar.setDisplayOptions((options & mask) | ((mask ^ -1) & current));
    }

    public final int getDisplayOptions() {
        return this.mDecorToolbar.getDisplayOptions();
    }

    public final ActionMode startActionMode(Callback callback) {
        if (this.mActionMode != null) {
            this.mActionMode.finish();
        }
        this.mOverlayLayout.setHideOnContentScrollEnabled(false);
        this.mContextView.killMode();
        ActionModeImpl mode = new ActionModeImpl(this.mContextView.getContext(), callback);
        if (!mode.dispatchOnCreate()) {
            return null;
        }
        this.mActionMode = mode;
        mode.invalidate();
        this.mContextView.initForMode(mode);
        animateToMode(true);
        this.mContextView.sendAccessibilityEvent(32);
        return mode;
    }

    public void enableContentAnimations(boolean enabled) {
        this.mContentAnimations = enabled;
    }

    public void showForSystem() {
        if (this.mHiddenBySystem) {
            this.mHiddenBySystem = false;
            updateVisibility(true);
        }
    }

    public void hideForSystem() {
        if (!this.mHiddenBySystem) {
            this.mHiddenBySystem = true;
            updateVisibility(true);
        }
    }

    static boolean checkShowingFlags(boolean hiddenByApp, boolean hiddenBySystem, boolean showingForMode) {
        if (showingForMode) {
            return true;
        }
        if (hiddenByApp || hiddenBySystem) {
            return false;
        }
        return true;
    }

    private void updateVisibility(boolean fromSystem) {
        float f;
        ViewPropertyAnimatorCompatSet viewPropertyAnimatorCompatSet;
        ViewPropertyAnimatorCompat translationY;
        if (checkShowingFlags(false, this.mHiddenBySystem, this.mShowingForMode)) {
            if (!this.mNowShowing) {
                this.mNowShowing = true;
                if (this.mCurrentShowAnim != null) {
                    this.mCurrentShowAnim.cancel();
                }
                this.mContainerView.setVisibility(0);
                if (this.mCurWindowVisibility == 0 && ALLOW_SHOW_HIDE_ANIMATIONS && (this.mShowHideAnimationEnabled || fromSystem)) {
                    ViewCompat.setTranslationY(this.mContainerView, 0.0f);
                    f = (float) (-this.mContainerView.getHeight());
                    if (fromSystem) {
                        int[] iArr = new int[]{0, 0};
                        this.mContainerView.getLocationInWindow(iArr);
                        f -= (float) iArr[1];
                    }
                    ViewCompat.setTranslationY(this.mContainerView, f);
                    viewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
                    translationY = ViewCompat.animate(this.mContainerView).translationY(0.0f);
                    translationY.setUpdateListener(this.mUpdateListener);
                    viewPropertyAnimatorCompatSet.play(translationY);
                    if (this.mContentAnimations && this.mContentView != null) {
                        ViewCompat.setTranslationY(this.mContentView, f);
                        viewPropertyAnimatorCompatSet.play(ViewCompat.animate(this.mContentView).translationY(0.0f));
                    }
                    viewPropertyAnimatorCompatSet.setInterpolator(sShowInterpolator);
                    viewPropertyAnimatorCompatSet.setDuration(250);
                    viewPropertyAnimatorCompatSet.setListener(this.mShowListener);
                    this.mCurrentShowAnim = viewPropertyAnimatorCompatSet;
                    viewPropertyAnimatorCompatSet.start();
                } else {
                    ViewCompat.setAlpha(this.mContainerView, 1.0f);
                    ViewCompat.setTranslationY(this.mContainerView, 0.0f);
                    if (this.mContentAnimations && this.mContentView != null) {
                        ViewCompat.setTranslationY(this.mContentView, 0.0f);
                    }
                    this.mShowListener.onAnimationEnd(null);
                }
                if (this.mOverlayLayout != null) {
                    ViewCompat.requestApplyInsets(this.mOverlayLayout);
                }
            }
        } else if (this.mNowShowing) {
            this.mNowShowing = false;
            if (this.mCurrentShowAnim != null) {
                this.mCurrentShowAnim.cancel();
            }
            if (this.mCurWindowVisibility == 0 && ALLOW_SHOW_HIDE_ANIMATIONS && (this.mShowHideAnimationEnabled || fromSystem)) {
                ViewCompat.setAlpha(this.mContainerView, 1.0f);
                this.mContainerView.setTransitioning(true);
                viewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
                f = (float) (-this.mContainerView.getHeight());
                if (fromSystem) {
                    int[] iArr2 = new int[]{0, 0};
                    this.mContainerView.getLocationInWindow(iArr2);
                    f -= (float) iArr2[1];
                }
                translationY = ViewCompat.animate(this.mContainerView).translationY(f);
                translationY.setUpdateListener(this.mUpdateListener);
                viewPropertyAnimatorCompatSet.play(translationY);
                if (this.mContentAnimations && this.mContentView != null) {
                    viewPropertyAnimatorCompatSet.play(ViewCompat.animate(this.mContentView).translationY(f));
                }
                viewPropertyAnimatorCompatSet.setInterpolator(sHideInterpolator);
                viewPropertyAnimatorCompatSet.setDuration(250);
                viewPropertyAnimatorCompatSet.setListener(this.mHideListener);
                this.mCurrentShowAnim = viewPropertyAnimatorCompatSet;
                viewPropertyAnimatorCompatSet.start();
                return;
            }
            this.mHideListener.onAnimationEnd(null);
        }
    }

    public final void animateToMode(boolean toActionMode) {
        if (toActionMode) {
            if (!this.mShowingForMode) {
                this.mShowingForMode = true;
                if (this.mOverlayLayout != null) {
                    this.mOverlayLayout.setShowingForActionMode(true);
                }
                updateVisibility(false);
            }
        } else if (this.mShowingForMode) {
            this.mShowingForMode = false;
            if (this.mOverlayLayout != null) {
                this.mOverlayLayout.setShowingForActionMode(false);
            }
            updateVisibility(false);
        }
        if (ViewCompat.isLaidOut(this.mContainerView)) {
            ViewPropertyAnimatorCompat fadeOut;
            ViewPropertyAnimatorCompat fadeIn;
            if (toActionMode) {
                fadeOut = this.mDecorToolbar.setupAnimatorToVisibility(4, 100);
                fadeIn = this.mContextView.setupAnimatorToVisibility(0, 200);
            } else {
                fadeIn = this.mDecorToolbar.setupAnimatorToVisibility(0, 200);
                fadeOut = this.mContextView.setupAnimatorToVisibility(8, 100);
            }
            ViewPropertyAnimatorCompatSet set = new ViewPropertyAnimatorCompatSet();
            set.playSequentially(fadeOut, fadeIn);
            set.start();
        } else if (toActionMode) {
            this.mDecorToolbar.setVisibility(4);
            this.mContextView.setVisibility(0);
        } else {
            this.mDecorToolbar.setVisibility(0);
            this.mContextView.setVisibility(8);
        }
    }

    public final Context getThemedContext() {
        if (this.mThemedContext == null) {
            TypedValue outValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(R.attr.actionBarWidgetTheme, outValue, true);
            int targetThemeRes = outValue.resourceId;
            if (targetThemeRes != 0) {
                this.mThemedContext = new ContextThemeWrapper(this.mContext, targetThemeRes);
            } else {
                this.mThemedContext = this.mContext;
            }
        }
        return this.mThemedContext;
    }

    public final void setHomeAsUpIndicator(Drawable indicator) {
        this.mDecorToolbar.setNavigationIcon(indicator);
    }

    public final void setHomeAsUpIndicator(int resId) {
        this.mDecorToolbar.setNavigationIcon((int) R.drawable.back_arrow);
    }

    public final void setHomeActionContentDescription(int resId) {
        this.mDecorToolbar.setNavigationContentDescription(resId);
    }

    public void onContentScrollStarted() {
        if (this.mCurrentShowAnim != null) {
            this.mCurrentShowAnim.cancel();
            this.mCurrentShowAnim = null;
        }
    }

    public void onContentScrollStopped() {
    }

    public final boolean collapseActionView() {
        if (this.mDecorToolbar == null || !this.mDecorToolbar.hasExpandedActionView()) {
            return false;
        }
        this.mDecorToolbar.collapseActionView();
        return true;
    }

    public final void setCustomView(View view, LayoutParams layoutParams) {
        view.setLayoutParams(layoutParams);
        this.mDecorToolbar.setCustomView(view);
    }

    public final void setDefaultDisplayHomeAsUpEnabled(boolean enable) {
        if (!this.mDisplayHomeAsUpSet) {
            setDisplayHomeAsUpEnabled(enable);
        }
    }

    public final boolean isShowing() {
        int height = this.mContainerView.getHeight();
        return this.mNowShowing && (height == 0 || this.mOverlayLayout.getActionBarHideOffset() < height);
    }
}
