package android.support.v7.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.StandaloneActionMode;
import android.support.v7.view.menu.ListMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.ActionBarContextView;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.ContentFrameLayout.OnAttachListener;
import android.support.v7.widget.DecorContentParent;
import android.support.v7.widget.FitWindowsViewGroup;
import android.support.v7.widget.FitWindowsViewGroup.OnFitSystemWindowsListener;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.VectorEnabledTintResources;
import android.support.v7.widget.ViewStubCompat;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;

@TargetApi(9)
class AppCompatDelegateImplV9 extends AppCompatDelegateImplBase implements LayoutInflaterFactory, Callback {
    private static final boolean IS_PRE_LOLLIPOP = (VERSION.SDK_INT < 21);
    private ActionMenuPresenterCallback mActionMenuPresenterCallback;
    ActionMode mActionMode;
    PopupWindow mActionModePopup;
    ActionBarContextView mActionModeView;
    private AppCompatViewInflater mAppCompatViewInflater;
    private boolean mClosingActionMenu;
    private DecorContentParent mDecorContentParent;
    private boolean mEnableDefaultActionBarUp;
    ViewPropertyAnimatorCompat mFadeAnim = null;
    private boolean mFeatureIndeterminateProgress;
    private boolean mFeatureProgress;
    int mInvalidatePanelMenuFeatures;
    boolean mInvalidatePanelMenuPosted;
    private final Runnable mInvalidatePanelMenuRunnable = new Runnable() {
        public final void run() {
            if ((AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures & 1) != 0) {
                AppCompatDelegateImplV9.this.doInvalidatePanelMenu(0);
            }
            if ((AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures & ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT) != 0) {
                AppCompatDelegateImplV9.this.doInvalidatePanelMenu(108);
            }
            AppCompatDelegateImplV9.this.mInvalidatePanelMenuPosted = false;
            AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures = 0;
        }
    };
    private boolean mLongPressBackDown;
    private PanelMenuPresenterCallback mPanelMenuPresenterCallback;
    private PanelFeatureState[] mPanels;
    private PanelFeatureState mPreparedPanel;
    Runnable mShowActionModePopup;
    private View mStatusGuard;
    private ViewGroup mSubDecor;
    private boolean mSubDecorInstalled;
    private Rect mTempRect1;
    private Rect mTempRect2;
    private TextView mTitleView;

    private final class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        ActionMenuPresenterCallback() {
        }

        public final boolean onOpenSubMenu(MenuBuilder subMenu) {
            Window.Callback cb = AppCompatDelegateImplV9.this.mWindow.getCallback();
            if (cb != null) {
                cb.onMenuOpened(108, subMenu);
            }
            return true;
        }

        public final void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
            AppCompatDelegateImplV9.this.checkCloseActionMenu(menu);
        }
    }

    class ActionModeCallbackWrapperV9 implements ActionMode.Callback {
        private ActionMode.Callback mWrapped;

        public ActionModeCallbackWrapperV9(ActionMode.Callback wrapped) {
            this.mWrapped = wrapped;
        }

        public final boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onCreateActionMode(mode, menu);
        }

        public final boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(mode, menu);
        }

        public final boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mWrapped.onActionItemClicked(mode, item);
        }

        public final void onDestroyActionMode(ActionMode mode) {
            this.mWrapped.onDestroyActionMode(mode);
            if (AppCompatDelegateImplV9.this.mActionModePopup != null) {
                AppCompatDelegateImplV9.this.mWindow.getDecorView().removeCallbacks(AppCompatDelegateImplV9.this.mShowActionModePopup);
            }
            if (AppCompatDelegateImplV9.this.mActionModeView != null) {
                AppCompatDelegateImplV9.this.endOnGoingFadeAnimation();
                AppCompatDelegateImplV9.this.mFadeAnim = ViewCompat.animate(AppCompatDelegateImplV9.this.mActionModeView).alpha(0.0f);
                AppCompatDelegateImplV9.this.mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter() {
                    public final void onAnimationEnd(View view) {
                        AppCompatDelegateImplV9.this.mActionModeView.setVisibility(8);
                        if (AppCompatDelegateImplV9.this.mActionModePopup != null) {
                            AppCompatDelegateImplV9.this.mActionModePopup.dismiss();
                        } else if (AppCompatDelegateImplV9.this.mActionModeView.getParent() instanceof View) {
                            ViewCompat.requestApplyInsets((View) AppCompatDelegateImplV9.this.mActionModeView.getParent());
                        }
                        AppCompatDelegateImplV9.this.mActionModeView.removeAllViews();
                        AppCompatDelegateImplV9.this.mFadeAnim.setListener(null);
                        AppCompatDelegateImplV9.this.mFadeAnim = null;
                    }
                });
            }
            if (AppCompatDelegateImplV9.this.mAppCompatCallback != null) {
                AppCompatCallback appCompatCallback = AppCompatDelegateImplV9.this.mAppCompatCallback;
                ActionMode actionMode = AppCompatDelegateImplV9.this.mActionMode;
            }
            AppCompatDelegateImplV9.this.mActionMode = null;
        }
    }

    private class ListMenuDecorView extends ContentFrameLayout {
        public ListMenuDecorView(Context context) {
            super(context);
        }

        public final boolean dispatchKeyEvent(KeyEvent event) {
            return AppCompatDelegateImplV9.this.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
        }

        public final boolean onInterceptTouchEvent(MotionEvent event) {
            if (event.getAction() == 0) {
                int i;
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (x < -5 || y < -5 || x > getWidth() + 5 || y > getHeight() + 5) {
                    i = true;
                } else {
                    i = 0;
                }
                if (i != 0) {
                    AppCompatDelegateImplV9.this.closePanel(0);
                    return true;
                }
            }
            return super.onInterceptTouchEvent(event);
        }

        public final void setBackgroundResource(int resid) {
            setBackgroundDrawable(AppCompatResources.getDrawable(getContext(), resid));
        }
    }

    protected static final class PanelFeatureState {
        int background;
        View createdPanelView;
        ViewGroup decorView;
        int featureId;
        Bundle frozenActionViewState;
        int gravity;
        boolean isHandled;
        boolean isOpen;
        boolean isPrepared;
        ListMenuPresenter listMenuPresenter;
        Context listPresenterContext;
        MenuBuilder menu;
        public boolean qwertyMode;
        boolean refreshDecorView = false;
        boolean refreshMenuContent;
        View shownPanelView;
        int windowAnimations;

        PanelFeatureState(int featureId) {
            this.featureId = featureId;
        }

        final void setMenu(MenuBuilder menu) {
            if (menu != this.menu) {
                if (this.menu != null) {
                    this.menu.removeMenuPresenter(this.listMenuPresenter);
                }
                this.menu = menu;
                if (menu != null && this.listMenuPresenter != null) {
                    menu.addMenuPresenter(this.listMenuPresenter);
                }
            }
        }
    }

    private final class PanelMenuPresenterCallback implements MenuPresenter.Callback {
        PanelMenuPresenterCallback() {
        }

        public final void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
            MenuBuilder parentMenu = menu.getRootMenu();
            boolean isSubMenu = parentMenu != menu;
            AppCompatDelegateImplV9 appCompatDelegateImplV9 = AppCompatDelegateImplV9.this;
            if (isSubMenu) {
                menu = parentMenu;
            }
            PanelFeatureState panel = appCompatDelegateImplV9.findMenuPanel(menu);
            if (panel == null) {
                return;
            }
            if (isSubMenu) {
                AppCompatDelegateImplV9.this.callOnPanelClosed(panel.featureId, panel, parentMenu);
                AppCompatDelegateImplV9.this.closePanel(panel, true);
                return;
            }
            AppCompatDelegateImplV9.this.closePanel(panel, allMenusAreClosing);
        }

        public final boolean onOpenSubMenu(MenuBuilder subMenu) {
            if (subMenu == null && AppCompatDelegateImplV9.this.mHasActionBar) {
                Window.Callback cb = AppCompatDelegateImplV9.this.mWindow.getCallback();
                if (!(cb == null || AppCompatDelegateImplV9.this.isDestroyed())) {
                    cb.onMenuOpened(108, subMenu);
                }
            }
            return true;
        }
    }

    AppCompatDelegateImplV9(Context context, Window window, AppCompatCallback callback) {
        super(context, window, callback);
    }

    public void onCreate(Bundle savedInstanceState) {
        if ((this.mOriginalWindowCallback instanceof Activity) && NavUtils.getParentActivityName((Activity) this.mOriginalWindowCallback) != null) {
            ActionBar ab = this.mActionBar;
            if (ab == null) {
                this.mEnableDefaultActionBarUp = true;
            } else {
                ab.setDefaultDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public final void onPostCreate$79e5e33f() {
        ensureSubDecor();
    }

    public final void initWindowDecorActionBar() {
        ensureSubDecor();
        if (this.mHasActionBar && this.mActionBar == null) {
            if (this.mOriginalWindowCallback instanceof Activity) {
                this.mActionBar = new WindowDecorActionBar((Activity) this.mOriginalWindowCallback, this.mOverlayActionBar);
            } else if (this.mOriginalWindowCallback instanceof Dialog) {
                this.mActionBar = new WindowDecorActionBar((Dialog) this.mOriginalWindowCallback);
            }
            if (this.mActionBar != null) {
                this.mActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp);
            }
        }
    }

    public final void setSupportActionBar(Toolbar toolbar) {
        if (this.mOriginalWindowCallback instanceof Activity) {
            ActionBar ab = getSupportActionBar();
            if (ab instanceof WindowDecorActionBar) {
                throw new IllegalStateException("This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_SUPPORT_ACTION_BAR and set windowActionBar to false in your theme to use a Toolbar instead.");
            }
            this.mMenuInflater = null;
            if (ab != null) {
                ab.onDestroy();
            }
            if (toolbar != null) {
                ToolbarActionBar tbab = new ToolbarActionBar(toolbar, ((Activity) this.mOriginalWindowCallback).getTitle(), this.mAppCompatWindowCallback);
                this.mActionBar = tbab;
                this.mWindow.setCallback(tbab.mWindowCallback);
            } else {
                this.mActionBar = null;
                this.mWindow.setCallback(this.mAppCompatWindowCallback);
            }
            invalidateOptionsMenu();
        }
    }

    public final View findViewById(int id) {
        ensureSubDecor();
        return this.mWindow.findViewById(id);
    }

    public final void onConfigurationChanged(Configuration newConfig) {
        if (this.mHasActionBar && this.mSubDecorInstalled) {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.onConfigurationChanged(newConfig);
            }
        }
        AppCompatDrawableManager.get().onConfigurationChanged(this.mContext);
        applyDayNight();
    }

    public void onStop() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setShowHideAnimationEnabled(false);
        }
    }

    public final void onPostResume() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setShowHideAnimationEnabled(true);
        }
    }

    public final void setContentView(View v) {
        ensureSubDecor();
        ViewGroup contentParent = (ViewGroup) this.mSubDecor.findViewById(16908290);
        contentParent.removeAllViews();
        contentParent.addView(v);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public final void setContentView(int resId) {
        ensureSubDecor();
        ViewGroup contentParent = (ViewGroup) this.mSubDecor.findViewById(16908290);
        contentParent.removeAllViews();
        LayoutInflater.from(this.mContext).inflate(resId, contentParent);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public final void setContentView(View v, LayoutParams lp) {
        ensureSubDecor();
        ViewGroup contentParent = (ViewGroup) this.mSubDecor.findViewById(16908290);
        contentParent.removeAllViews();
        contentParent.addView(v, lp);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public final void addContentView(View v, LayoutParams lp) {
        ensureSubDecor();
        ((ViewGroup) this.mSubDecor.findViewById(16908290)).addView(v, lp);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public void onDestroy() {
        if (this.mInvalidatePanelMenuPosted) {
            this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable);
        }
        super.onDestroy();
        if (this.mActionBar != null) {
            this.mActionBar.onDestroy();
        }
    }

    private void ensureSubDecor() {
        if (!this.mSubDecorInstalled) {
            TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
            if (obtainStyledAttributes.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
                View view;
                View view2;
                if (obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_windowNoTitle, false)) {
                    requestWindowFeature(1);
                } else if (obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_windowActionBar, false)) {
                    requestWindowFeature(108);
                }
                if (obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_windowActionBarOverlay, false)) {
                    requestWindowFeature(109);
                }
                if (obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_windowActionModeOverlay, false)) {
                    requestWindowFeature(10);
                }
                this.mIsFloating = obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_android_windowIsFloating, false);
                obtainStyledAttributes.recycle();
                this.mWindow.getDecorView();
                LayoutInflater from = LayoutInflater.from(this.mContext);
                if (this.mWindowNoTitle) {
                    if (this.mOverlayActionMode) {
                        view = (ViewGroup) from.inflate(com.rachio.iro.R.layout.abc_screen_simple_overlay_action_mode, null);
                    } else {
                        view = (ViewGroup) from.inflate(com.rachio.iro.R.layout.abc_screen_simple, null);
                    }
                    if (VERSION.SDK_INT >= 21) {
                        ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
                            public final WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                                int top = insets.getSystemWindowInsetTop();
                                int newTop = AppCompatDelegateImplV9.this.updateStatusGuard(top);
                                if (top != newTop) {
                                    insets = insets.replaceSystemWindowInsets(insets.getSystemWindowInsetLeft(), newTop, insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
                                }
                                return ViewCompat.onApplyWindowInsets(v, insets);
                            }
                        });
                        view2 = view;
                    } else {
                        ((FitWindowsViewGroup) view).setOnFitSystemWindowsListener(new OnFitSystemWindowsListener() {
                            public final void onFitSystemWindows(Rect insets) {
                                insets.top = AppCompatDelegateImplV9.this.updateStatusGuard(insets.top);
                            }
                        });
                        view2 = view;
                    }
                } else if (this.mIsFloating) {
                    r2 = (ViewGroup) from.inflate(com.rachio.iro.R.layout.abc_dialog_title_material, null);
                    this.mOverlayActionBar = false;
                    this.mHasActionBar = false;
                    view2 = r2;
                } else if (this.mHasActionBar) {
                    Context contextThemeWrapper;
                    TypedValue typedValue = new TypedValue();
                    this.mContext.getTheme().resolveAttribute(com.rachio.iro.R.attr.actionBarTheme, typedValue, true);
                    if (typedValue.resourceId != 0) {
                        contextThemeWrapper = new ContextThemeWrapper(this.mContext, typedValue.resourceId);
                    } else {
                        contextThemeWrapper = this.mContext;
                    }
                    r2 = (ViewGroup) LayoutInflater.from(contextThemeWrapper).inflate(com.rachio.iro.R.layout.abc_screen_toolbar, null);
                    this.mDecorContentParent = (DecorContentParent) r2.findViewById(com.rachio.iro.R.id.decor_content_parent);
                    this.mDecorContentParent.setWindowCallback(this.mWindow.getCallback());
                    if (this.mOverlayActionBar) {
                        this.mDecorContentParent.initFeature(109);
                    }
                    if (this.mFeatureProgress) {
                        this.mDecorContentParent.initFeature(2);
                    }
                    if (this.mFeatureIndeterminateProgress) {
                        this.mDecorContentParent.initFeature(5);
                    }
                    view2 = r2;
                } else {
                    view2 = null;
                }
                if (view2 == null) {
                    throw new IllegalArgumentException("AppCompat does not support the current theme features: { windowActionBar: " + this.mHasActionBar + ", windowActionBarOverlay: " + this.mOverlayActionBar + ", android:windowIsFloating: " + this.mIsFloating + ", windowActionModeOverlay: " + this.mOverlayActionMode + ", windowNoTitle: " + this.mWindowNoTitle + " }");
                }
                if (this.mDecorContentParent == null) {
                    this.mTitleView = (TextView) view2.findViewById(com.rachio.iro.R.id.title);
                }
                ViewUtils.makeOptionalFitsSystemWindows(view2);
                ContentFrameLayout contentFrameLayout = (ContentFrameLayout) view2.findViewById(com.rachio.iro.R.id.action_bar_activity_content);
                ViewGroup viewGroup = (ViewGroup) this.mWindow.findViewById(16908290);
                if (viewGroup != null) {
                    while (viewGroup.getChildCount() > 0) {
                        View childAt = viewGroup.getChildAt(0);
                        viewGroup.removeViewAt(0);
                        contentFrameLayout.addView(childAt);
                    }
                    viewGroup.setId(-1);
                    contentFrameLayout.setId(16908290);
                    if (viewGroup instanceof FrameLayout) {
                        ((FrameLayout) viewGroup).setForeground(null);
                    }
                }
                this.mWindow.setContentView(view2);
                contentFrameLayout.setAttachListener(new OnAttachListener() {
                    public final void onAttachedFromWindow() {
                    }

                    public final void onDetachedFromWindow() {
                        AppCompatDelegateImplV9.this.dismissPopups();
                    }
                });
                this.mSubDecor = view2;
                CharSequence title = getTitle();
                if (!TextUtils.isEmpty(title)) {
                    onTitleChanged(title);
                }
                contentFrameLayout = (ContentFrameLayout) this.mSubDecor.findViewById(16908290);
                view = this.mWindow.getDecorView();
                contentFrameLayout.setDecorPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                TypedArray obtainStyledAttributes2 = this.mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
                obtainStyledAttributes2.getValue(R.styleable.AppCompatTheme_windowMinWidthMajor, contentFrameLayout.getMinWidthMajor());
                obtainStyledAttributes2.getValue(R.styleable.AppCompatTheme_windowMinWidthMinor, contentFrameLayout.getMinWidthMinor());
                if (obtainStyledAttributes2.hasValue(R.styleable.AppCompatTheme_windowFixedWidthMajor)) {
                    obtainStyledAttributes2.getValue(R.styleable.AppCompatTheme_windowFixedWidthMajor, contentFrameLayout.getFixedWidthMajor());
                }
                if (obtainStyledAttributes2.hasValue(R.styleable.AppCompatTheme_windowFixedWidthMinor)) {
                    obtainStyledAttributes2.getValue(R.styleable.AppCompatTheme_windowFixedWidthMinor, contentFrameLayout.getFixedWidthMinor());
                }
                if (obtainStyledAttributes2.hasValue(R.styleable.AppCompatTheme_windowFixedHeightMajor)) {
                    obtainStyledAttributes2.getValue(R.styleable.AppCompatTheme_windowFixedHeightMajor, contentFrameLayout.getFixedHeightMajor());
                }
                if (obtainStyledAttributes2.hasValue(R.styleable.AppCompatTheme_windowFixedHeightMinor)) {
                    obtainStyledAttributes2.getValue(R.styleable.AppCompatTheme_windowFixedHeightMinor, contentFrameLayout.getFixedHeightMinor());
                }
                obtainStyledAttributes2.recycle();
                contentFrameLayout.requestLayout();
                this.mSubDecorInstalled = true;
                PanelFeatureState st = getPanelState$20476cf5(0);
                if (!isDestroyed()) {
                    if (st == null || st.menu == null) {
                        invalidatePanelMenu(108);
                        return;
                    }
                    return;
                }
                return;
            }
            obtainStyledAttributes.recycle();
            throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
        }
    }

    public final boolean requestWindowFeature(int featureId) {
        if (featureId == 8) {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR id when requesting this feature.");
            featureId = 108;
        } else if (featureId == 9) {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY id when requesting this feature.");
            featureId = 109;
        }
        if (this.mWindowNoTitle && featureId == 108) {
            return false;
        }
        if (this.mHasActionBar && featureId == 1) {
            this.mHasActionBar = false;
        }
        switch (featureId) {
            case 1:
                throwFeatureRequestIfSubDecorInstalled();
                this.mWindowNoTitle = true;
                return true;
            case 2:
                throwFeatureRequestIfSubDecorInstalled();
                this.mFeatureProgress = true;
                return true;
            case 5:
                throwFeatureRequestIfSubDecorInstalled();
                this.mFeatureIndeterminateProgress = true;
                return true;
            case 10:
                throwFeatureRequestIfSubDecorInstalled();
                this.mOverlayActionMode = true;
                return true;
            case 108:
                throwFeatureRequestIfSubDecorInstalled();
                this.mHasActionBar = true;
                return true;
            case 109:
                throwFeatureRequestIfSubDecorInstalled();
                this.mOverlayActionBar = true;
                return true;
            default:
                return this.mWindow.requestFeature(featureId);
        }
    }

    final void onTitleChanged(CharSequence title) {
        if (this.mDecorContentParent != null) {
            this.mDecorContentParent.setWindowTitle(title);
        } else if (this.mActionBar != null) {
            this.mActionBar.setWindowTitle(title);
        } else if (this.mTitleView != null) {
            this.mTitleView.setText(title);
        }
    }

    final void onPanelClosed$2fef5516(int featureId) {
        if (featureId == 108) {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.dispatchMenuVisibilityChanged(false);
            }
        } else if (featureId == 0) {
            PanelFeatureState st = getPanelState$20476cf5(featureId);
            if (st.isOpen) {
                closePanel(st, false);
            }
        }
    }

    final boolean onMenuOpened$2fef5512(int featureId) {
        if (featureId != 108) {
            return false;
        }
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return true;
        }
        ab.dispatchMenuVisibilityChanged(true);
        return true;
    }

    public void onMenuModeChange(MenuBuilder menu) {
        if (this.mDecorContentParent == null || !this.mDecorContentParent.canShowOverflowMenu() || (ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(this.mContext)) && !this.mDecorContentParent.isOverflowMenuShowPending())) {
            PanelFeatureState panelState$20476cf5 = getPanelState$20476cf5(0);
            panelState$20476cf5.refreshDecorView = true;
            closePanel(panelState$20476cf5, false);
            openPanel(panelState$20476cf5, null);
            return;
        }
        Window.Callback callback = this.mWindow.getCallback();
        if (this.mDecorContentParent.isOverflowMenuShowing()) {
            this.mDecorContentParent.hideOverflowMenu();
            if (!isDestroyed()) {
                callback.onPanelClosed(108, getPanelState$20476cf5(0).menu);
            }
        } else if (callback != null && !isDestroyed()) {
            if (this.mInvalidatePanelMenuPosted && (this.mInvalidatePanelMenuFeatures & 1) != 0) {
                this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable);
                this.mInvalidatePanelMenuRunnable.run();
            }
            PanelFeatureState panelState$20476cf52 = getPanelState$20476cf5(0);
            if (panelState$20476cf52.menu != null && !panelState$20476cf52.refreshMenuContent && callback.onPreparePanel(0, panelState$20476cf52.createdPanelView, panelState$20476cf52.menu)) {
                callback.onMenuOpened(108, panelState$20476cf52.menu);
                this.mDecorContentParent.showOverflowMenu();
            }
        }
    }

    public final ActionMode startSupportActionMode(ActionMode.Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("ActionMode callback can not be null.");
        }
        if (this.mActionMode != null) {
            this.mActionMode.finish();
        }
        ActionMode.Callback wrappedCallback = new ActionModeCallbackWrapperV9(callback);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            this.mActionMode = ab.startActionMode(wrappedCallback);
        }
        if (this.mActionMode == null) {
            endOnGoingFadeAnimation();
            if (this.mActionMode != null) {
                this.mActionMode.finish();
            }
            if (this.mActionModeView == null) {
                if (this.mIsFloating) {
                    Context contextThemeWrapper;
                    TypedValue typedValue = new TypedValue();
                    Theme theme = this.mContext.getTheme();
                    theme.resolveAttribute(com.rachio.iro.R.attr.actionBarTheme, typedValue, true);
                    if (typedValue.resourceId != 0) {
                        Theme newTheme = this.mContext.getResources().newTheme();
                        newTheme.setTo(theme);
                        newTheme.applyStyle(typedValue.resourceId, true);
                        contextThemeWrapper = new ContextThemeWrapper(this.mContext, 0);
                        contextThemeWrapper.getTheme().setTo(newTheme);
                    } else {
                        contextThemeWrapper = this.mContext;
                    }
                    this.mActionModeView = new ActionBarContextView(contextThemeWrapper);
                    this.mActionModePopup = new PopupWindow(contextThemeWrapper, null, com.rachio.iro.R.attr.actionModePopupWindowStyle);
                    PopupWindowCompat.setWindowLayoutType(this.mActionModePopup, 2);
                    this.mActionModePopup.setContentView(this.mActionModeView);
                    this.mActionModePopup.setWidth(-1);
                    contextThemeWrapper.getTheme().resolveAttribute(com.rachio.iro.R.attr.actionBarSize, typedValue, true);
                    this.mActionModeView.setContentHeight(TypedValue.complexToDimensionPixelSize(typedValue.data, contextThemeWrapper.getResources().getDisplayMetrics()));
                    this.mActionModePopup.setHeight(-2);
                    this.mShowActionModePopup = new Runnable() {
                        public final void run() {
                            AppCompatDelegateImplV9.this.mActionModePopup.showAtLocation(AppCompatDelegateImplV9.this.mActionModeView, 55, 0, 0);
                            AppCompatDelegateImplV9.this.endOnGoingFadeAnimation();
                            if (AppCompatDelegateImplV9.this.shouldAnimateActionModeView()) {
                                ViewCompat.setAlpha(AppCompatDelegateImplV9.this.mActionModeView, 0.0f);
                                AppCompatDelegateImplV9.this.mFadeAnim = ViewCompat.animate(AppCompatDelegateImplV9.this.mActionModeView).alpha(1.0f);
                                AppCompatDelegateImplV9.this.mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter() {
                                    public final void onAnimationStart(View view) {
                                        AppCompatDelegateImplV9.this.mActionModeView.setVisibility(0);
                                    }

                                    public final void onAnimationEnd(View view) {
                                        ViewCompat.setAlpha(AppCompatDelegateImplV9.this.mActionModeView, 1.0f);
                                        AppCompatDelegateImplV9.this.mFadeAnim.setListener(null);
                                        AppCompatDelegateImplV9.this.mFadeAnim = null;
                                    }
                                });
                                return;
                            }
                            ViewCompat.setAlpha(AppCompatDelegateImplV9.this.mActionModeView, 1.0f);
                            AppCompatDelegateImplV9.this.mActionModeView.setVisibility(0);
                        }
                    };
                } else {
                    ViewStubCompat viewStubCompat = (ViewStubCompat) this.mSubDecor.findViewById(com.rachio.iro.R.id.action_mode_bar_stub);
                    if (viewStubCompat != null) {
                        viewStubCompat.setLayoutInflater(LayoutInflater.from(getActionBarThemedContext()));
                        this.mActionModeView = (ActionBarContextView) viewStubCompat.inflate();
                    }
                }
            }
            if (this.mActionModeView != null) {
                boolean z;
                endOnGoingFadeAnimation();
                this.mActionModeView.killMode();
                Context context = this.mActionModeView.getContext();
                ActionBarContextView actionBarContextView = this.mActionModeView;
                if (this.mActionModePopup == null) {
                    z = true;
                } else {
                    z = false;
                }
                ActionMode standaloneActionMode = new StandaloneActionMode(context, actionBarContextView, wrappedCallback, z);
                if (wrappedCallback.onCreateActionMode(standaloneActionMode, standaloneActionMode.getMenu())) {
                    standaloneActionMode.invalidate();
                    this.mActionModeView.initForMode(standaloneActionMode);
                    this.mActionMode = standaloneActionMode;
                    if (shouldAnimateActionModeView()) {
                        ViewCompat.setAlpha(this.mActionModeView, 0.0f);
                        this.mFadeAnim = ViewCompat.animate(this.mActionModeView).alpha(1.0f);
                        this.mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter() {
                            public final void onAnimationStart(View view) {
                                AppCompatDelegateImplV9.this.mActionModeView.setVisibility(0);
                                AppCompatDelegateImplV9.this.mActionModeView.sendAccessibilityEvent(32);
                                if (AppCompatDelegateImplV9.this.mActionModeView.getParent() instanceof View) {
                                    ViewCompat.requestApplyInsets((View) AppCompatDelegateImplV9.this.mActionModeView.getParent());
                                }
                            }

                            public final void onAnimationEnd(View view) {
                                ViewCompat.setAlpha(AppCompatDelegateImplV9.this.mActionModeView, 1.0f);
                                AppCompatDelegateImplV9.this.mFadeAnim.setListener(null);
                                AppCompatDelegateImplV9.this.mFadeAnim = null;
                            }
                        });
                    } else {
                        ViewCompat.setAlpha(this.mActionModeView, 1.0f);
                        this.mActionModeView.setVisibility(0);
                        this.mActionModeView.sendAccessibilityEvent(32);
                        if (this.mActionModeView.getParent() instanceof View) {
                            ViewCompat.requestApplyInsets((View) this.mActionModeView.getParent());
                        }
                    }
                    if (this.mActionModePopup != null) {
                        this.mWindow.getDecorView().post(this.mShowActionModePopup);
                    }
                } else {
                    this.mActionMode = null;
                }
            }
            this.mActionMode = this.mActionMode;
        }
        return this.mActionMode;
    }

    public final void invalidateOptionsMenu() {
        ActionBar ab = getSupportActionBar();
        if (ab == null || !ab.invalidateOptionsMenu()) {
            invalidatePanelMenu(0);
        }
    }

    final boolean shouldAnimateActionModeView() {
        return this.mSubDecorInstalled && this.mSubDecor != null && ViewCompat.isLaidOut(this.mSubDecor);
    }

    final void endOnGoingFadeAnimation() {
        if (this.mFadeAnim != null) {
            this.mFadeAnim.cancel();
        }
    }

    final boolean onKeyShortcut(int keyCode, KeyEvent ev) {
        ActionBar ab = getSupportActionBar();
        if (ab != null && ab.onKeyShortcut(keyCode, ev)) {
            return true;
        }
        if (this.mPreparedPanel == null || !performPanelShortcut(this.mPreparedPanel, ev.getKeyCode(), ev, 1)) {
            if (this.mPreparedPanel == null) {
                PanelFeatureState st = getPanelState$20476cf5(0);
                preparePanel(st, ev);
                boolean handled = performPanelShortcut(st, ev.getKeyCode(), ev, 1);
                st.isPrepared = false;
                if (handled) {
                    return true;
                }
            }
            return false;
        } else if (this.mPreparedPanel == null) {
            return true;
        } else {
            this.mPreparedPanel.isHandled = true;
            return true;
        }
    }

    final boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 82 && this.mOriginalWindowCallback.dispatchKeyEvent(event)) {
            return true;
        }
        boolean isDown;
        int keyCode = event.getKeyCode();
        if (event.getAction() == 0) {
            isDown = true;
        } else {
            isDown = false;
        }
        if (isDown) {
            switch (keyCode) {
                case 4:
                    this.mLongPressBackDown = (event.getFlags() & 128) != 0;
                    break;
                case 82:
                    if (event.getRepeatCount() != 0) {
                        return true;
                    }
                    PanelFeatureState panelState$20476cf5 = getPanelState$20476cf5(0);
                    if (panelState$20476cf5.isOpen) {
                        return true;
                    }
                    preparePanel(panelState$20476cf5, event);
                    return true;
            }
            if (VERSION.SDK_INT < 11) {
                onKeyShortcut(keyCode, event);
            }
            return false;
        }
        boolean z;
        PanelFeatureState panelState$20476cf52;
        switch (keyCode) {
            case 4:
                z = this.mLongPressBackDown;
                this.mLongPressBackDown = false;
                panelState$20476cf52 = getPanelState$20476cf5(0);
                if (panelState$20476cf52 == null || !panelState$20476cf52.isOpen) {
                    if (this.mActionMode != null) {
                        this.mActionMode.finish();
                        z = true;
                    } else {
                        ActionBar supportActionBar = getSupportActionBar();
                        if (supportActionBar == null || !supportActionBar.collapseActionView()) {
                            z = false;
                        } else {
                            z = true;
                        }
                    }
                    if (z) {
                        return true;
                    }
                } else if (z) {
                    return true;
                } else {
                    closePanel(panelState$20476cf52, true);
                    return true;
                }
                break;
            case 82:
                if (this.mActionMode != null) {
                    return true;
                }
                panelState$20476cf52 = getPanelState$20476cf5(0);
                if (this.mDecorContentParent == null || !this.mDecorContentParent.canShowOverflowMenu() || ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(this.mContext))) {
                    if (panelState$20476cf52.isOpen || panelState$20476cf52.isHandled) {
                        z = panelState$20476cf52.isOpen;
                        closePanel(panelState$20476cf52, true);
                    } else {
                        if (panelState$20476cf52.isPrepared) {
                            if (panelState$20476cf52.refreshMenuContent) {
                                panelState$20476cf52.isPrepared = false;
                                z = preparePanel(panelState$20476cf52, event);
                            } else {
                                z = true;
                            }
                            if (z) {
                                openPanel(panelState$20476cf52, event);
                                z = true;
                            }
                        }
                        z = false;
                    }
                } else if (this.mDecorContentParent.isOverflowMenuShowing()) {
                    z = this.mDecorContentParent.hideOverflowMenu();
                } else {
                    if (!isDestroyed() && preparePanel(panelState$20476cf52, event)) {
                        z = this.mDecorContentParent.showOverflowMenu();
                    }
                    z = false;
                }
                if (!z) {
                    return true;
                }
                AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
                if (audioManager != null) {
                    audioManager.playSoundEffect(0);
                    return true;
                }
                Log.w("AppCompatDelegate", "Couldn't get audio manager");
                return true;
        }
        return false;
    }

    public final void installViewFactory() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.mContext);
        if (layoutInflater.getFactory() == null) {
            LayoutInflaterCompat.setFactory(layoutInflater, this);
        } else if (!(LayoutInflaterCompat.getFactory(layoutInflater) instanceof AppCompatDelegateImplV9)) {
            Log.i("AppCompatDelegate", "The Activity's LayoutInflater already has a Factory installed so we can not install AppCompat's");
        }
    }

    public final View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = callActivityOnCreateView$1fef4371(name, context, attrs);
        if (view != null) {
            return view;
        }
        boolean z;
        if (this.mAppCompatViewInflater == null) {
            this.mAppCompatViewInflater = new AppCompatViewInflater();
        }
        if (IS_PRE_LOLLIPOP) {
            boolean z2;
            if (attrs instanceof XmlPullParser) {
                z2 = ((XmlPullParser) attrs).getDepth() > 1;
            } else {
                ViewParent viewParent = (ViewParent) parent;
                if (viewParent == null) {
                    z2 = false;
                } else {
                    ViewParent decorView = this.mWindow.getDecorView();
                    ViewParent viewParent2 = viewParent;
                    while (viewParent2 != null) {
                        if (viewParent2 == decorView || !(viewParent2 instanceof View) || ViewCompat.isAttachedToWindow((View) viewParent2)) {
                            z2 = false;
                            break;
                        }
                        viewParent2 = viewParent2.getParent();
                    }
                    z2 = true;
                }
            }
            z = z2;
        } else {
            z = false;
        }
        return this.mAppCompatViewInflater.createView(parent, name, context, attrs, z, IS_PRE_LOLLIPOP, true, VectorEnabledTintResources.shouldBeUsed());
    }

    View callActivityOnCreateView$1fef4371(String name, Context context, AttributeSet attrs) {
        if (this.mOriginalWindowCallback instanceof Factory) {
            View result = ((Factory) this.mOriginalWindowCallback).onCreateView(name, context, attrs);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private void openPanel(PanelFeatureState st, KeyEvent event) {
        if (!st.isOpen && !isDestroyed()) {
            if (st.featureId == 0) {
                Context context = this.mContext;
                boolean isXLarge = (context.getResources().getConfiguration().screenLayout & 15) == 4;
                boolean isHoneycombApp = context.getApplicationInfo().targetSdkVersion >= 11;
                if (isXLarge && isHoneycombApp) {
                    return;
                }
            }
            Window.Callback cb = this.mWindow.getCallback();
            if (cb == null || cb.onMenuOpened(st.featureId, st.menu)) {
                WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
                if (wm != null && preparePanel(st, event)) {
                    int width = -2;
                    LayoutParams lp;
                    if (st.decorView == null || st.refreshDecorView) {
                        Object obj;
                        if (st.decorView == null) {
                            Context actionBarThemedContext = getActionBarThemedContext();
                            TypedValue typedValue = new TypedValue();
                            Theme newTheme = actionBarThemedContext.getResources().newTheme();
                            newTheme.setTo(actionBarThemedContext.getTheme());
                            newTheme.resolveAttribute(com.rachio.iro.R.attr.actionBarPopupTheme, typedValue, true);
                            if (typedValue.resourceId != 0) {
                                newTheme.applyStyle(typedValue.resourceId, true);
                            }
                            newTheme.resolveAttribute(com.rachio.iro.R.attr.panelMenuListTheme, typedValue, true);
                            if (typedValue.resourceId != 0) {
                                newTheme.applyStyle(typedValue.resourceId, true);
                            } else {
                                newTheme.applyStyle(com.rachio.iro.R.style.Theme.AppCompat.CompactMenu, true);
                            }
                            Context contextThemeWrapper = new ContextThemeWrapper(actionBarThemedContext, 0);
                            contextThemeWrapper.getTheme().setTo(newTheme);
                            st.listPresenterContext = contextThemeWrapper;
                            TypedArray obtainStyledAttributes = contextThemeWrapper.obtainStyledAttributes(R.styleable.AppCompatTheme);
                            st.background = obtainStyledAttributes.getResourceId(R.styleable.AppCompatTheme_panelBackground, 0);
                            st.windowAnimations = obtainStyledAttributes.getResourceId(R.styleable.AppCompatTheme_android_windowAnimationStyle, 0);
                            obtainStyledAttributes.recycle();
                            st.decorView = new ListMenuDecorView(st.listPresenterContext);
                            st.gravity = 81;
                            if (st.decorView == null) {
                                return;
                            }
                        } else if (st.refreshDecorView && st.decorView.getChildCount() > 0) {
                            st.decorView.removeAllViews();
                        }
                        if (st.createdPanelView != null) {
                            st.shownPanelView = st.createdPanelView;
                            obj = 1;
                        } else {
                            if (st.menu != null) {
                                MenuView menuView;
                                if (this.mPanelMenuPresenterCallback == null) {
                                    this.mPanelMenuPresenterCallback = new PanelMenuPresenterCallback();
                                }
                                MenuPresenter.Callback callback = this.mPanelMenuPresenterCallback;
                                if (st.menu == null) {
                                    menuView = null;
                                } else {
                                    if (st.listMenuPresenter == null) {
                                        st.listMenuPresenter = new ListMenuPresenter(st.listPresenterContext, (int) com.rachio.iro.R.layout.abc_list_menu_item_layout);
                                        st.listMenuPresenter.setCallback(callback);
                                        st.menu.addMenuPresenter(st.listMenuPresenter);
                                    }
                                    menuView = st.listMenuPresenter.getMenuView(st.decorView);
                                }
                                st.shownPanelView = (View) menuView;
                                if (st.shownPanelView != null) {
                                    obj = 1;
                                }
                            }
                            obj = null;
                        }
                        if (obj != null) {
                            ViewParent shownPanelParent;
                            if (st.shownPanelView != null) {
                                if (st.createdPanelView != null) {
                                    obj = 1;
                                } else if (st.listMenuPresenter.getAdapter().getCount() > 0) {
                                    obj = 1;
                                }
                                if (obj != null) {
                                    lp = st.shownPanelView.getLayoutParams();
                                    if (lp == null) {
                                        lp = new LayoutParams(-2, -2);
                                    }
                                    st.decorView.setBackgroundResource(st.background);
                                    shownPanelParent = st.shownPanelView.getParent();
                                    if (shownPanelParent != null && (shownPanelParent instanceof ViewGroup)) {
                                        ((ViewGroup) shownPanelParent).removeView(st.shownPanelView);
                                    }
                                    st.decorView.addView(st.shownPanelView, lp);
                                    if (!st.shownPanelView.hasFocus()) {
                                        st.shownPanelView.requestFocus();
                                    }
                                } else {
                                    return;
                                }
                            }
                            obj = null;
                            if (obj != null) {
                                lp = st.shownPanelView.getLayoutParams();
                                if (lp == null) {
                                    lp = new LayoutParams(-2, -2);
                                }
                                st.decorView.setBackgroundResource(st.background);
                                shownPanelParent = st.shownPanelView.getParent();
                                ((ViewGroup) shownPanelParent).removeView(st.shownPanelView);
                                st.decorView.addView(st.shownPanelView, lp);
                                if (st.shownPanelView.hasFocus()) {
                                    st.shownPanelView.requestFocus();
                                }
                            } else {
                                return;
                            }
                        }
                        return;
                    } else if (st.createdPanelView != null) {
                        lp = st.createdPanelView.getLayoutParams();
                        if (lp != null && lp.width == -1) {
                            width = -1;
                        }
                    }
                    st.isHandled = false;
                    WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams(width, -2, 0, 0, 1002, 8519680, -3);
                    lp2.gravity = st.gravity;
                    lp2.windowAnimations = st.windowAnimations;
                    wm.addView(st.decorView, lp2);
                    st.isOpen = true;
                    return;
                }
                return;
            }
            closePanel(st, true);
        }
    }

    private boolean preparePanel(PanelFeatureState st, KeyEvent event) {
        if (isDestroyed()) {
            return false;
        }
        if (st.isPrepared) {
            return true;
        }
        boolean isActionBarMenu;
        if (!(this.mPreparedPanel == null || this.mPreparedPanel == st)) {
            closePanel(this.mPreparedPanel, false);
        }
        Window.Callback cb = this.mWindow.getCallback();
        if (cb != null) {
            st.createdPanelView = cb.onCreatePanelView(st.featureId);
        }
        if (st.featureId == 0 || st.featureId == 108) {
            isActionBarMenu = true;
        } else {
            isActionBarMenu = false;
        }
        if (isActionBarMenu && this.mDecorContentParent != null) {
            this.mDecorContentParent.setMenuPrepared();
        }
        if (st.createdPanelView == null && !(isActionBarMenu && (this.mActionBar instanceof ToolbarActionBar))) {
            if (st.menu == null || st.refreshMenuContent) {
                if (st.menu == null) {
                    Context contextThemeWrapper;
                    MenuBuilder menuBuilder;
                    Context context = this.mContext;
                    if ((st.featureId == 0 || st.featureId == 108) && this.mDecorContentParent != null) {
                        Theme newTheme;
                        TypedValue typedValue = new TypedValue();
                        Theme theme = context.getTheme();
                        theme.resolveAttribute(com.rachio.iro.R.attr.actionBarTheme, typedValue, true);
                        if (typedValue.resourceId != 0) {
                            newTheme = context.getResources().newTheme();
                            newTheme.setTo(theme);
                            newTheme.applyStyle(typedValue.resourceId, true);
                            newTheme.resolveAttribute(com.rachio.iro.R.attr.actionBarWidgetTheme, typedValue, true);
                        } else {
                            theme.resolveAttribute(com.rachio.iro.R.attr.actionBarWidgetTheme, typedValue, true);
                            newTheme = null;
                        }
                        if (typedValue.resourceId != 0) {
                            if (newTheme == null) {
                                newTheme = context.getResources().newTheme();
                                newTheme.setTo(theme);
                            }
                            newTheme.applyStyle(typedValue.resourceId, true);
                        }
                        Theme theme2 = newTheme;
                        if (theme2 != null) {
                            contextThemeWrapper = new ContextThemeWrapper(context, 0);
                            contextThemeWrapper.getTheme().setTo(theme2);
                            menuBuilder = new MenuBuilder(contextThemeWrapper);
                            menuBuilder.setCallback(this);
                            st.setMenu(menuBuilder);
                            if (st.menu == null) {
                                return false;
                            }
                        }
                    }
                    contextThemeWrapper = context;
                    menuBuilder = new MenuBuilder(contextThemeWrapper);
                    menuBuilder.setCallback(this);
                    st.setMenu(menuBuilder);
                    if (st.menu == null) {
                        return false;
                    }
                }
                if (isActionBarMenu && this.mDecorContentParent != null) {
                    if (this.mActionMenuPresenterCallback == null) {
                        this.mActionMenuPresenterCallback = new ActionMenuPresenterCallback();
                    }
                    this.mDecorContentParent.setMenu(st.menu, this.mActionMenuPresenterCallback);
                }
                st.menu.stopDispatchingItemsChanged();
                if (cb.onCreatePanelMenu(st.featureId, st.menu)) {
                    st.refreshMenuContent = false;
                } else {
                    st.setMenu(null);
                    if (!isActionBarMenu || this.mDecorContentParent == null) {
                        return false;
                    }
                    this.mDecorContentParent.setMenu(null, this.mActionMenuPresenterCallback);
                    return false;
                }
            }
            st.menu.stopDispatchingItemsChanged();
            if (st.frozenActionViewState != null) {
                st.menu.restoreActionViewStates(st.frozenActionViewState);
                st.frozenActionViewState = null;
            }
            if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                boolean z;
                if (KeyCharacterMap.load(event != null ? event.getDeviceId() : -1).getKeyboardType() != 1) {
                    z = true;
                } else {
                    z = false;
                }
                st.qwertyMode = z;
                st.menu.setQwertyMode(st.qwertyMode);
                st.menu.startDispatchingItemsChanged();
            } else {
                if (isActionBarMenu && this.mDecorContentParent != null) {
                    this.mDecorContentParent.setMenu(null, this.mActionMenuPresenterCallback);
                }
                st.menu.startDispatchingItemsChanged();
                return false;
            }
        }
        st.isPrepared = true;
        st.isHandled = false;
        this.mPreparedPanel = st;
        return true;
    }

    final void checkCloseActionMenu(MenuBuilder menu) {
        if (!this.mClosingActionMenu) {
            this.mClosingActionMenu = true;
            this.mDecorContentParent.dismissPopups();
            Window.Callback cb = this.mWindow.getCallback();
            if (!(cb == null || isDestroyed())) {
                cb.onPanelClosed(108, menu);
            }
            this.mClosingActionMenu = false;
        }
    }

    final void closePanel(int featureId) {
        closePanel(getPanelState$20476cf5(0), true);
    }

    final void closePanel(PanelFeatureState st, boolean doCallback) {
        if (doCallback && st.featureId == 0 && this.mDecorContentParent != null && this.mDecorContentParent.isOverflowMenuShowing()) {
            checkCloseActionMenu(st.menu);
            return;
        }
        WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
        if (!(wm == null || !st.isOpen || st.decorView == null)) {
            wm.removeView(st.decorView);
            if (doCallback) {
                callOnPanelClosed(st.featureId, st, null);
            }
        }
        st.isPrepared = false;
        st.isHandled = false;
        st.isOpen = false;
        st.shownPanelView = null;
        st.refreshDecorView = true;
        if (this.mPreparedPanel == st) {
            this.mPreparedPanel = null;
        }
    }

    final void callOnPanelClosed(int featureId, PanelFeatureState panel, Menu menu) {
        if (menu == null) {
            if (panel == null && featureId >= 0 && featureId < this.mPanels.length) {
                panel = this.mPanels[featureId];
            }
            if (panel != null) {
                menu = panel.menu;
            }
        }
        if ((panel == null || panel.isOpen) && !isDestroyed()) {
            this.mOriginalWindowCallback.onPanelClosed(featureId, menu);
        }
    }

    final PanelFeatureState findMenuPanel(Menu menu) {
        PanelFeatureState[] panels = this.mPanels;
        int N = panels != null ? panels.length : 0;
        for (int i = 0; i < N; i++) {
            PanelFeatureState panel = panels[i];
            if (panel != null && panel.menu == menu) {
                return panel;
            }
        }
        return null;
    }

    protected final PanelFeatureState getPanelState$20476cf5(int featureId) {
        PanelFeatureState[] ar = this.mPanels;
        if (ar == null || ar.length <= featureId) {
            PanelFeatureState[] nar = new PanelFeatureState[(featureId + 1)];
            if (ar != null) {
                System.arraycopy(ar, 0, nar, 0, ar.length);
            }
            ar = nar;
            this.mPanels = nar;
        }
        PanelFeatureState st = ar[featureId];
        if (st != null) {
            return st;
        }
        st = new PanelFeatureState(featureId);
        ar[featureId] = st;
        return st;
    }

    private boolean performPanelShortcut(PanelFeatureState st, int keyCode, KeyEvent event, int flags) {
        if (event.isSystem()) {
            return false;
        }
        if ((st.isPrepared || preparePanel(st, event)) && st.menu != null) {
            return st.menu.performShortcut(keyCode, event, 1);
        }
        return false;
    }

    private void invalidatePanelMenu(int featureId) {
        this.mInvalidatePanelMenuFeatures |= 1 << featureId;
        if (!this.mInvalidatePanelMenuPosted) {
            ViewCompat.postOnAnimation(this.mWindow.getDecorView(), this.mInvalidatePanelMenuRunnable);
            this.mInvalidatePanelMenuPosted = true;
        }
    }

    final void doInvalidatePanelMenu(int featureId) {
        PanelFeatureState st = getPanelState$20476cf5(featureId);
        if (st.menu != null) {
            Bundle savedActionViewStates = new Bundle();
            st.menu.saveActionViewStates(savedActionViewStates);
            if (savedActionViewStates.size() > 0) {
                st.frozenActionViewState = savedActionViewStates;
            }
            st.menu.stopDispatchingItemsChanged();
            st.menu.clear();
        }
        st.refreshMenuContent = true;
        st.refreshDecorView = true;
        if ((featureId == 108 || featureId == 0) && this.mDecorContentParent != null) {
            st = getPanelState$20476cf5(0);
            if (st != null) {
                st.isPrepared = false;
                preparePanel(st, null);
            }
        }
    }

    final int updateStatusGuard(int insetTop) {
        int i = 0;
        boolean showStatusGuard = false;
        if (this.mActionModeView != null && (this.mActionModeView.getLayoutParams() instanceof MarginLayoutParams)) {
            MarginLayoutParams mlp = (MarginLayoutParams) this.mActionModeView.getLayoutParams();
            boolean mlpChanged = false;
            if (this.mActionModeView.isShown()) {
                int newMargin;
                if (this.mTempRect1 == null) {
                    this.mTempRect1 = new Rect();
                    this.mTempRect2 = new Rect();
                }
                Rect insets = this.mTempRect1;
                Rect localInsets = this.mTempRect2;
                insets.set(0, insetTop, 0, 0);
                ViewUtils.computeFitSystemWindows(this.mSubDecor, insets, localInsets);
                if (localInsets.top == 0) {
                    newMargin = insetTop;
                } else {
                    newMargin = 0;
                }
                if (mlp.topMargin != newMargin) {
                    mlpChanged = true;
                    mlp.topMargin = insetTop;
                    if (this.mStatusGuard == null) {
                        this.mStatusGuard = new View(this.mContext);
                        this.mStatusGuard.setBackgroundColor(this.mContext.getResources().getColor(com.rachio.iro.R.color.abc_input_method_navigation_guard));
                        this.mSubDecor.addView(this.mStatusGuard, -1, new LayoutParams(-1, insetTop));
                    } else {
                        LayoutParams lp = this.mStatusGuard.getLayoutParams();
                        if (lp.height != insetTop) {
                            lp.height = insetTop;
                            this.mStatusGuard.setLayoutParams(lp);
                        }
                    }
                }
                if (this.mStatusGuard != null) {
                    showStatusGuard = true;
                } else {
                    showStatusGuard = false;
                }
                if (!this.mOverlayActionMode && showStatusGuard) {
                    insetTop = 0;
                }
            } else if (mlp.topMargin != 0) {
                mlpChanged = true;
                mlp.topMargin = 0;
            }
            if (mlpChanged) {
                this.mActionModeView.setLayoutParams(mlp);
            }
        }
        if (this.mStatusGuard != null) {
            View view = this.mStatusGuard;
            if (!showStatusGuard) {
                i = 8;
            }
            view.setVisibility(i);
        }
        return insetTop;
    }

    private void throwFeatureRequestIfSubDecorInstalled() {
        if (this.mSubDecorInstalled) {
            throw new AndroidRuntimeException("Window feature must be requested before adding content");
        }
    }

    final void dismissPopups() {
        if (this.mDecorContentParent != null) {
            this.mDecorContentParent.dismissPopups();
        }
        if (this.mActionModePopup != null) {
            this.mWindow.getDecorView().removeCallbacks(this.mShowActionModePopup);
            if (this.mActionModePopup.isShowing()) {
                try {
                    this.mActionModePopup.dismiss();
                } catch (IllegalArgumentException e) {
                }
            }
            this.mActionModePopup = null;
        }
        endOnGoingFadeAnimation();
        PanelFeatureState st = getPanelState$20476cf5(0);
        if (st != null && st.menu != null) {
            st.menu.close();
        }
    }

    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        Window.Callback cb = this.mWindow.getCallback();
        if (!(cb == null || isDestroyed())) {
            PanelFeatureState panel = findMenuPanel(menu.getRootMenu());
            if (panel != null) {
                return cb.onMenuItemSelected(panel.featureId, item);
            }
        }
        return false;
    }
}
