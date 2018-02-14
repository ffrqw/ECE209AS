package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuView.ItemView;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.rachio.iro.R;

public class ListMenuItemView extends LinearLayout implements ItemView {
    private Drawable mBackground;
    private CheckBox mCheckBox;
    private boolean mForceShowIcon;
    private ImageView mIconView;
    private LayoutInflater mInflater;
    private MenuItemImpl mItemData;
    private int mMenuType;
    private boolean mPreserveIconSpacing;
    private RadioButton mRadioButton;
    private TextView mShortcutView;
    private Drawable mSubMenuArrow;
    private ImageView mSubMenuArrowView;
    private int mTextAppearance;
    private Context mTextAppearanceContext;
    private TextView mTitleView;

    public ListMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.listMenuViewStyle);
    }

    public ListMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, android.support.v7.appcompat.R.styleable.MenuView, defStyleAttr, 0);
        this.mBackground = a.getDrawable(android.support.v7.appcompat.R.styleable.MenuView_android_itemBackground);
        this.mTextAppearance = a.getResourceId(android.support.v7.appcompat.R.styleable.MenuView_android_itemTextAppearance, -1);
        this.mPreserveIconSpacing = a.getBoolean(android.support.v7.appcompat.R.styleable.MenuView_preserveIconSpacing, false);
        this.mTextAppearanceContext = context;
        this.mSubMenuArrow = a.getDrawable(android.support.v7.appcompat.R.styleable.MenuView_subMenuArrow);
        a.recycle();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewCompat.setBackground(this, this.mBackground);
        this.mTitleView = (TextView) findViewById(R.id.title);
        if (this.mTextAppearance != -1) {
            this.mTitleView.setTextAppearance(this.mTextAppearanceContext, this.mTextAppearance);
        }
        this.mShortcutView = (TextView) findViewById(R.id.shortcut);
        this.mSubMenuArrowView = (ImageView) findViewById(R.id.submenuarrow);
        if (this.mSubMenuArrowView != null) {
            this.mSubMenuArrowView.setImageDrawable(this.mSubMenuArrow);
        }
    }

    public final void initialize(MenuItemImpl itemData, int menuType) {
        int i = 0;
        this.mItemData = itemData;
        this.mMenuType = 0;
        setVisibility(itemData.isVisible() ? 0 : 8);
        CharSequence titleForItemView = itemData.getTitleForItemView(this);
        if (titleForItemView != null) {
            this.mTitleView.setText(titleForItemView);
            if (this.mTitleView.getVisibility() != 0) {
                this.mTitleView.setVisibility(0);
            }
        } else if (this.mTitleView.getVisibility() != 8) {
            this.mTitleView.setVisibility(8);
        }
        boolean isCheckable = itemData.isCheckable();
        if (!(!isCheckable && this.mRadioButton == null && this.mCheckBox == null)) {
            CompoundButton compoundButton;
            CompoundButton compoundButton2;
            if (this.mItemData.isExclusiveCheckable()) {
                if (this.mRadioButton == null) {
                    this.mRadioButton = (RadioButton) getInflater().inflate(R.layout.abc_list_menu_item_radio, this, false);
                    addView(this.mRadioButton);
                }
                compoundButton = this.mRadioButton;
                compoundButton2 = this.mCheckBox;
            } else {
                if (this.mCheckBox == null) {
                    this.mCheckBox = (CheckBox) getInflater().inflate(R.layout.abc_list_menu_item_checkbox, this, false);
                    addView(this.mCheckBox);
                }
                compoundButton = this.mCheckBox;
                compoundButton2 = this.mRadioButton;
            }
            if (isCheckable) {
                int i2;
                compoundButton.setChecked(this.mItemData.isChecked());
                if (isCheckable) {
                    i2 = 0;
                } else {
                    i2 = 8;
                }
                if (compoundButton.getVisibility() != i2) {
                    compoundButton.setVisibility(i2);
                }
                if (!(compoundButton2 == null || compoundButton2.getVisibility() == 8)) {
                    compoundButton2.setVisibility(8);
                }
            } else {
                if (this.mCheckBox != null) {
                    this.mCheckBox.setVisibility(8);
                }
                if (this.mRadioButton != null) {
                    this.mRadioButton.setVisibility(8);
                }
            }
        }
        boolean shouldShowShortcut = itemData.shouldShowShortcut();
        itemData.getShortcut();
        int i3 = (shouldShowShortcut && this.mItemData.shouldShowShortcut()) ? 0 : 8;
        if (i3 == 0) {
            TextView textView = this.mShortcutView;
            char shortcut = this.mItemData.getShortcut();
            if (shortcut == '\u0000') {
                titleForItemView = "";
            } else {
                StringBuilder stringBuilder = new StringBuilder(null);
                switch (shortcut) {
                    case '\b':
                        stringBuilder.append(null);
                        break;
                    case '\n':
                        stringBuilder.append(null);
                        break;
                    case ' ':
                        stringBuilder.append(null);
                        break;
                    default:
                        stringBuilder.append(shortcut);
                        break;
                }
                titleForItemView = stringBuilder.toString();
            }
            textView.setText(titleForItemView);
        }
        if (this.mShortcutView.getVisibility() != i3) {
            this.mShortcutView.setVisibility(i3);
        }
        Drawable icon = itemData.getIcon();
        MenuBuilder menuBuilder = this.mItemData.mMenu;
        int i4 = this.mForceShowIcon ? 1 : 0;
        if ((i4 != 0 || this.mPreserveIconSpacing) && !(this.mIconView == null && icon == null && !this.mPreserveIconSpacing)) {
            if (this.mIconView == null) {
                this.mIconView = (ImageView) getInflater().inflate(R.layout.abc_list_menu_item_icon, this, false);
                addView(this.mIconView, 0);
            }
            if (icon != null || this.mPreserveIconSpacing) {
                this.mIconView.setImageDrawable(i4 != 0 ? icon : null);
                if (this.mIconView.getVisibility() != 0) {
                    this.mIconView.setVisibility(0);
                }
            } else {
                this.mIconView.setVisibility(8);
            }
        }
        setEnabled(itemData.isEnabled());
        shouldShowShortcut = itemData.hasSubMenu();
        if (this.mSubMenuArrowView != null) {
            ImageView imageView = this.mSubMenuArrowView;
            if (!shouldShowShortcut) {
                i = 8;
            }
            imageView.setVisibility(i);
        }
    }

    public final void setForceShowIcon(boolean forceShow) {
        this.mForceShowIcon = true;
        this.mPreserveIconSpacing = true;
    }

    public final MenuItemImpl getItemData() {
        return this.mItemData;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mIconView != null && this.mPreserveIconSpacing) {
            LayoutParams lp = getLayoutParams();
            LinearLayout.LayoutParams iconLp = (LinearLayout.LayoutParams) this.mIconView.getLayoutParams();
            if (lp.height > 0 && iconLp.width <= 0) {
                iconLp.width = lp.height;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public final boolean prefersCondensedTitle() {
        return false;
    }

    private LayoutInflater getInflater() {
        if (this.mInflater == null) {
            this.mInflater = LayoutInflater.from(getContext());
        }
        return this.mInflater;
    }
}
