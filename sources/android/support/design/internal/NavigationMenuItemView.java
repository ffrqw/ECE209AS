package android.support.design.internal;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView.ItemView;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import com.rachio.iro.R;

public class NavigationMenuItemView extends ForegroundLinearLayout implements ItemView {
    private static final int[] CHECKED_STATE_SET = new int[]{16842912};
    private final AccessibilityDelegateCompat mAccessibilityDelegate;
    private FrameLayout mActionArea;
    boolean mCheckable;
    private final int mIconSize;
    private MenuItemImpl mItemData;
    private final CheckedTextView mTextView;

    public NavigationMenuItemView(Context context) {
        this(context, null);
    }

    public NavigationMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mAccessibilityDelegate = new AccessibilityDelegateCompat() {
            public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setCheckable(NavigationMenuItemView.this.mCheckable);
            }
        };
        setOrientation(0);
        LayoutInflater.from(context).inflate(R.layout.design_navigation_menu_item, this, true);
        this.mIconSize = context.getResources().getDimensionPixelSize(R.dimen.design_navigation_icon_size);
        this.mTextView = (CheckedTextView) findViewById(R.id.design_menu_item_text);
        this.mTextView.setDuplicateParentStateEnabled(true);
        ViewCompat.setAccessibilityDelegate(this.mTextView, this.mAccessibilityDelegate);
    }

    public final void initialize(MenuItemImpl itemData, int menuType) {
        Drawable stateListDrawable;
        this.mItemData = itemData;
        setVisibility(itemData.isVisible() ? 0 : 8);
        if (getBackground() == null) {
            TypedValue typedValue = new TypedValue();
            if (getContext().getTheme().resolveAttribute(R.attr.colorControlHighlight, typedValue, true)) {
                stateListDrawable = new StateListDrawable();
                stateListDrawable.addState(CHECKED_STATE_SET, new ColorDrawable(typedValue.data));
                stateListDrawable.addState(EMPTY_STATE_SET, new ColorDrawable(0));
            } else {
                stateListDrawable = null;
            }
            ViewCompat.setBackground(this, stateListDrawable);
        }
        boolean isCheckable = itemData.isCheckable();
        refreshDrawableState();
        if (this.mCheckable != isCheckable) {
            this.mCheckable = isCheckable;
            this.mAccessibilityDelegate.sendAccessibilityEvent(this.mTextView, ItemAnimator.FLAG_MOVED);
        }
        isCheckable = itemData.isChecked();
        refreshDrawableState();
        this.mTextView.setChecked(isCheckable);
        setEnabled(itemData.isEnabled());
        this.mTextView.setText(itemData.getTitle());
        stateListDrawable = itemData.getIcon();
        if (stateListDrawable != null) {
            stateListDrawable.setBounds(0, 0, this.mIconSize, this.mIconSize);
        }
        TextViewCompat.setCompoundDrawablesRelative(this.mTextView, stateListDrawable, null, null, null);
        View actionView = itemData.getActionView();
        if (actionView != null) {
            if (this.mActionArea == null) {
                this.mActionArea = (FrameLayout) ((ViewStub) findViewById(R.id.design_menu_item_action_area_stub)).inflate();
            }
            this.mActionArea.removeAllViews();
            this.mActionArea.addView(actionView);
        }
        if (this.mItemData.getTitle() == null && this.mItemData.getIcon() == null && this.mItemData.getActionView() != null) {
            isCheckable = true;
        } else {
            isCheckable = false;
        }
        if (isCheckable) {
            this.mTextView.setVisibility(8);
            if (this.mActionArea != null) {
                LayoutParams layoutParams = (LayoutParams) this.mActionArea.getLayoutParams();
                layoutParams.width = -1;
                this.mActionArea.setLayoutParams(layoutParams);
                return;
            }
            return;
        }
        this.mTextView.setVisibility(0);
        if (this.mActionArea != null) {
            layoutParams = (LayoutParams) this.mActionArea.getLayoutParams();
            layoutParams.width = -2;
            this.mActionArea.setLayoutParams(layoutParams);
        }
    }

    public final MenuItemImpl getItemData() {
        return this.mItemData;
    }

    public final boolean prefersCondensedTitle() {
        return false;
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.mItemData != null && this.mItemData.isCheckable() && this.mItemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}
