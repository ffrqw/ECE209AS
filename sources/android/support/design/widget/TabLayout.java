package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import android.support.v4.util.Pools.SynchronizedPool;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.DecorView;
import android.support.v4.view.ViewPager.OnAdapterChangeListener;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.rachio.iro.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

@DecorView
public class TabLayout extends HorizontalScrollView {
    private static final Pool<Tab> sTabPool = new SynchronizedPool(16);
    private AdapterChangeListener mAdapterChangeListener;
    private int mContentInsetStart;
    private OnTabSelectedListener mCurrentVpSelectedListener;
    int mMode;
    private TabLayoutOnPageChangeListener mPageChangeListener;
    private PagerAdapter mPagerAdapter;
    private DataSetObserver mPagerAdapterObserver;
    private final int mRequestedTabMaxWidth;
    private final int mRequestedTabMinWidth;
    private ValueAnimatorCompat mScrollAnimator;
    private final int mScrollableTabMinWidth;
    private final ArrayList<OnTabSelectedListener> mSelectedListeners;
    private Tab mSelectedTab;
    private boolean mSetupViewPagerImplicitly;
    final int mTabBackgroundResId;
    int mTabGravity;
    int mTabMaxWidth;
    int mTabPaddingBottom;
    int mTabPaddingEnd;
    int mTabPaddingStart;
    int mTabPaddingTop;
    private final SlidingTabStrip mTabStrip;
    int mTabTextAppearance;
    ColorStateList mTabTextColors;
    float mTabTextMultiLineSize;
    float mTabTextSize;
    private final Pool<TabView> mTabViewPool;
    private final ArrayList<Tab> mTabs;
    ViewPager mViewPager;

    private class AdapterChangeListener implements OnAdapterChangeListener {
        private boolean mAutoRefresh;

        AdapterChangeListener() {
        }

        public final void onAdapterChanged$49113dbb(ViewPager viewPager, PagerAdapter newAdapter) {
            if (TabLayout.this.mViewPager == viewPager) {
                TabLayout.this.setPagerAdapter(newAdapter, this.mAutoRefresh);
            }
        }

        final void setAutoRefresh(boolean autoRefresh) {
            this.mAutoRefresh = autoRefresh;
        }
    }

    public interface OnTabSelectedListener {
        void onTabSelected(Tab tab);
    }

    private class PagerAdapterObserver extends DataSetObserver {
        PagerAdapterObserver() {
        }

        public final void onChanged() {
            TabLayout.this.populateFromPagerAdapter();
        }

        public final void onInvalidated() {
            TabLayout.this.populateFromPagerAdapter();
        }
    }

    private class SlidingTabStrip extends LinearLayout {
        private ValueAnimatorCompat mIndicatorAnimator;
        private int mIndicatorLeft = -1;
        private int mIndicatorRight = -1;
        private int mSelectedIndicatorHeight;
        private final Paint mSelectedIndicatorPaint;
        int mSelectedPosition = -1;
        float mSelectionOffset;

        SlidingTabStrip(Context context) {
            super(context);
            setWillNotDraw(false);
            this.mSelectedIndicatorPaint = new Paint();
        }

        final void setSelectedIndicatorColor(int color) {
            if (this.mSelectedIndicatorPaint.getColor() != color) {
                this.mSelectedIndicatorPaint.setColor(color);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        final void setSelectedIndicatorHeight(int height) {
            if (this.mSelectedIndicatorHeight != height) {
                this.mSelectedIndicatorHeight = height;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        final void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
            if (this.mIndicatorAnimator != null && this.mIndicatorAnimator.isRunning()) {
                this.mIndicatorAnimator.cancel();
            }
            this.mSelectedPosition = position;
            this.mSelectionOffset = positionOffset;
            updateIndicatorPosition();
        }

        protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (MeasureSpec.getMode(widthMeasureSpec) == 1073741824 && TabLayout.this.mMode == 1 && TabLayout.this.mTabGravity == 1) {
                int i;
                int count = getChildCount();
                int largestTabWidth = 0;
                for (i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() == 0) {
                        largestTabWidth = Math.max(largestTabWidth, child.getMeasuredWidth());
                    }
                }
                if (largestTabWidth > 0) {
                    boolean remeasure = false;
                    if (largestTabWidth * count <= getMeasuredWidth() - (TabLayout.this.dpToPx(16) << 1)) {
                        for (i = 0; i < count; i++) {
                            LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
                            if (lp.width != largestTabWidth || lp.weight != 0.0f) {
                                lp.width = largestTabWidth;
                                lp.weight = 0.0f;
                                remeasure = true;
                            }
                        }
                    } else {
                        TabLayout.this.mTabGravity = 0;
                        TabLayout.this.updateTabViews(false);
                        remeasure = true;
                    }
                    if (remeasure) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }
                }
            }
        }

        protected final void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.mIndicatorAnimator == null || !this.mIndicatorAnimator.isRunning()) {
                updateIndicatorPosition();
                return;
            }
            this.mIndicatorAnimator.cancel();
            animateIndicatorToPosition(this.mSelectedPosition, Math.round((1.0f - this.mIndicatorAnimator.getAnimatedFraction()) * ((float) this.mIndicatorAnimator.getDuration())));
        }

        private void updateIndicatorPosition() {
            int right;
            int left;
            View selectedTitle = getChildAt(this.mSelectedPosition);
            if (selectedTitle == null || selectedTitle.getWidth() <= 0) {
                right = -1;
                left = -1;
            } else {
                left = selectedTitle.getLeft();
                right = selectedTitle.getRight();
                if (this.mSelectionOffset > 0.0f && this.mSelectedPosition < getChildCount() - 1) {
                    View nextTitle = getChildAt(this.mSelectedPosition + 1);
                    left = (int) ((this.mSelectionOffset * ((float) nextTitle.getLeft())) + ((1.0f - this.mSelectionOffset) * ((float) left)));
                    right = (int) ((this.mSelectionOffset * ((float) nextTitle.getRight())) + ((1.0f - this.mSelectionOffset) * ((float) right)));
                }
            }
            setIndicatorPosition(left, right);
        }

        final void setIndicatorPosition(int left, int right) {
            if (left != this.mIndicatorLeft || right != this.mIndicatorRight) {
                this.mIndicatorLeft = left;
                this.mIndicatorRight = right;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        final void animateIndicatorToPosition(final int position, int duration) {
            if (this.mIndicatorAnimator != null && this.mIndicatorAnimator.isRunning()) {
                this.mIndicatorAnimator.cancel();
            }
            boolean isRtl = ViewCompat.getLayoutDirection(this) == 1;
            View targetView = getChildAt(position);
            if (targetView == null) {
                updateIndicatorPosition();
                return;
            }
            int startLeft;
            int startRight;
            final int targetLeft = targetView.getLeft();
            final int targetRight = targetView.getRight();
            if (Math.abs(position - this.mSelectedPosition) <= 1) {
                startLeft = this.mIndicatorLeft;
                startRight = this.mIndicatorRight;
            } else {
                int offset = TabLayout.this.dpToPx(24);
                if (position < this.mSelectedPosition) {
                    if (!isRtl) {
                        startRight = targetRight + offset;
                        startLeft = startRight;
                    }
                } else if (isRtl) {
                    startRight = targetRight + offset;
                    startLeft = startRight;
                }
                startRight = targetLeft - offset;
                startLeft = startRight;
            }
            if (startLeft != targetLeft || startRight != targetRight) {
                ValueAnimatorCompat animator = ViewUtils.createAnimator();
                this.mIndicatorAnimator = animator;
                animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                animator.setDuration((long) duration);
                animator.setFloatValues(0.0f, 1.0f);
                animator.addUpdateListener(new AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimatorCompat animator) {
                        float fraction = animator.getAnimatedFraction();
                        SlidingTabStrip.this.setIndicatorPosition(AnimationUtils.lerp(startLeft, targetLeft, fraction), AnimationUtils.lerp(startRight, targetRight, fraction));
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    public final void onAnimationEnd(ValueAnimatorCompat animator) {
                        SlidingTabStrip.this.mSelectedPosition = position;
                        SlidingTabStrip.this.mSelectionOffset = 0.0f;
                    }
                });
                animator.start();
            }
        }

        public final void draw(Canvas canvas) {
            super.draw(canvas);
            if (this.mIndicatorLeft >= 0 && this.mIndicatorRight > this.mIndicatorLeft) {
                canvas.drawRect((float) this.mIndicatorLeft, (float) (getHeight() - this.mSelectedIndicatorHeight), (float) this.mIndicatorRight, (float) getHeight(), this.mSelectedIndicatorPaint);
            }
        }
    }

    public static final class Tab {
        private CharSequence mContentDesc;
        private View mCustomView;
        private Drawable mIcon;
        TabLayout mParent;
        private int mPosition = -1;
        private Object mTag;
        private CharSequence mText;
        TabView mView;

        Tab() {
        }

        public final View getCustomView() {
            return this.mCustomView;
        }

        public final Tab setCustomView(View view) {
            this.mCustomView = view;
            updateView();
            return this;
        }

        public final Drawable getIcon() {
            return this.mIcon;
        }

        public final int getPosition() {
            return this.mPosition;
        }

        final void setPosition(int position) {
            this.mPosition = position;
        }

        public final CharSequence getText() {
            return this.mText;
        }

        public final Tab setIcon(Drawable icon) {
            this.mIcon = icon;
            updateView();
            return this;
        }

        public final Tab setText(CharSequence text) {
            this.mText = text;
            updateView();
            return this;
        }

        public final void select() {
            if (this.mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            this.mParent.selectTab(this);
        }

        public final boolean isSelected() {
            if (this.mParent != null) {
                return this.mParent.getSelectedTabPosition() == this.mPosition;
            } else {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
        }

        public final Tab setContentDescription(CharSequence contentDesc) {
            this.mContentDesc = contentDesc;
            updateView();
            return this;
        }

        public final CharSequence getContentDescription() {
            return this.mContentDesc;
        }

        private void updateView() {
            if (this.mView != null) {
                this.mView.update();
            }
        }

        final void reset() {
            this.mParent = null;
            this.mView = null;
            this.mTag = null;
            this.mIcon = null;
            this.mText = null;
            this.mContentDesc = null;
            this.mPosition = -1;
            this.mCustomView = null;
        }
    }

    public static class TabLayoutOnPageChangeListener implements OnPageChangeListener {
        private int mPreviousScrollState;
        private int mScrollState;
        private final WeakReference<TabLayout> mTabLayoutRef;

        public TabLayoutOnPageChangeListener(TabLayout tabLayout) {
            this.mTabLayoutRef = new WeakReference(tabLayout);
        }

        public final void onPageScrollStateChanged(int state) {
            this.mPreviousScrollState = this.mScrollState;
            this.mScrollState = state;
        }

        public final void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            TabLayout tabLayout = (TabLayout) this.mTabLayoutRef.get();
            if (tabLayout != null) {
                boolean updateIndicator;
                boolean updateText = this.mScrollState != 2 || this.mPreviousScrollState == 1;
                if (this.mScrollState == 2 && this.mPreviousScrollState == 0) {
                    updateIndicator = false;
                } else {
                    updateIndicator = true;
                }
                tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator);
            }
        }

        public final void onPageSelected(int position) {
            TabLayout tabLayout = (TabLayout) this.mTabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position && position < tabLayout.getTabCount()) {
                boolean updateIndicator = this.mScrollState == 0 || (this.mScrollState == 2 && this.mPreviousScrollState == 0);
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator);
            }
        }

        final void reset() {
            this.mScrollState = 0;
            this.mPreviousScrollState = 0;
        }
    }

    class TabView extends LinearLayout implements OnLongClickListener {
        private ImageView mCustomIconView;
        private TextView mCustomTextView;
        private View mCustomView;
        private int mDefaultMaxLines = 2;
        private ImageView mIconView;
        private Tab mTab;
        private TextView mTextView;

        public TabView(Context context) {
            super(context);
            if (TabLayout.this.mTabBackgroundResId != 0) {
                ViewCompat.setBackground(this, AppCompatResources.getDrawable(context, TabLayout.this.mTabBackgroundResId));
            }
            ViewCompat.setPaddingRelative(this, TabLayout.this.mTabPaddingStart, TabLayout.this.mTabPaddingTop, TabLayout.this.mTabPaddingEnd, TabLayout.this.mTabPaddingBottom);
            setGravity(17);
            setOrientation(1);
            setClickable(true);
            ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), 1002));
        }

        public final boolean performClick() {
            boolean handled = super.performClick();
            if (this.mTab == null) {
                return handled;
            }
            if (!handled) {
                playSoundEffect(0);
            }
            this.mTab.select();
            return true;
        }

        public final void setSelected(boolean selected) {
            boolean changed = isSelected() != selected;
            super.setSelected(selected);
            if (changed && selected && VERSION.SDK_INT < 16) {
                sendAccessibilityEvent(4);
            }
            if (this.mTextView != null) {
                this.mTextView.setSelected(selected);
            }
            if (this.mIconView != null) {
                this.mIconView.setSelected(selected);
            }
            if (this.mCustomView != null) {
                this.mCustomView.setSelected(selected);
            }
        }

        @TargetApi(14)
        public final void onInitializeAccessibilityEvent(AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(event);
            event.setClassName(android.support.v7.app.ActionBar.Tab.class.getName());
        }

        @TargetApi(14)
        public final void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(android.support.v7.app.ActionBar.Tab.class.getName());
        }

        public final void onMeasure(int origWidthMeasureSpec, int origHeightMeasureSpec) {
            int widthMeasureSpec;
            int specWidthSize = MeasureSpec.getSize(origWidthMeasureSpec);
            int specWidthMode = MeasureSpec.getMode(origWidthMeasureSpec);
            int maxWidth = TabLayout.this.mTabMaxWidth;
            if (maxWidth <= 0 || (specWidthMode != 0 && specWidthSize <= maxWidth)) {
                widthMeasureSpec = origWidthMeasureSpec;
            } else {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(TabLayout.this.mTabMaxWidth, Integer.MIN_VALUE);
            }
            super.onMeasure(widthMeasureSpec, origHeightMeasureSpec);
            if (this.mTextView != null) {
                getResources();
                float textSize = TabLayout.this.mTabTextSize;
                int maxLines = this.mDefaultMaxLines;
                if (this.mIconView != null && this.mIconView.getVisibility() == 0) {
                    maxLines = 1;
                } else if (this.mTextView != null && this.mTextView.getLineCount() > 1) {
                    textSize = TabLayout.this.mTabTextMultiLineSize;
                }
                float curTextSize = this.mTextView.getTextSize();
                int curLineCount = this.mTextView.getLineCount();
                int curMaxLines = TextViewCompat.getMaxLines(this.mTextView);
                if (textSize != curTextSize || (curMaxLines >= 0 && maxLines != curMaxLines)) {
                    boolean updateTextView = true;
                    if (TabLayout.this.mMode == 1 && textSize > curTextSize && curLineCount == 1) {
                        Layout layout = this.mTextView.getLayout();
                        if (layout == null || layout.getLineWidth(0) * (textSize / layout.getPaint().getTextSize()) > ((float) ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()))) {
                            updateTextView = false;
                        }
                    }
                    if (updateTextView) {
                        this.mTextView.setTextSize(0, textSize);
                        this.mTextView.setMaxLines(maxLines);
                        super.onMeasure(widthMeasureSpec, origHeightMeasureSpec);
                    }
                }
            }
        }

        final void setTab(Tab tab) {
            if (tab != this.mTab) {
                this.mTab = tab;
                update();
            }
        }

        final void update() {
            View custom;
            boolean z;
            Tab tab = this.mTab;
            if (tab != null) {
                custom = tab.getCustomView();
            } else {
                custom = null;
            }
            if (custom != null) {
                TabView customParent = custom.getParent();
                if (customParent != this) {
                    if (customParent != null) {
                        customParent.removeView(custom);
                    }
                    addView(custom);
                }
                this.mCustomView = custom;
                if (this.mTextView != null) {
                    this.mTextView.setVisibility(8);
                }
                if (this.mIconView != null) {
                    this.mIconView.setVisibility(8);
                    this.mIconView.setImageDrawable(null);
                }
                this.mCustomTextView = (TextView) custom.findViewById(16908308);
                if (this.mCustomTextView != null) {
                    this.mDefaultMaxLines = TextViewCompat.getMaxLines(this.mCustomTextView);
                }
                this.mCustomIconView = (ImageView) custom.findViewById(16908294);
            } else {
                if (this.mCustomView != null) {
                    removeView(this.mCustomView);
                    this.mCustomView = null;
                }
                this.mCustomTextView = null;
                this.mCustomIconView = null;
            }
            if (this.mCustomView == null) {
                if (this.mIconView == null) {
                    ImageView iconView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.design_layout_tab_icon, this, false);
                    addView(iconView, 0);
                    this.mIconView = iconView;
                }
                if (this.mTextView == null) {
                    TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.design_layout_tab_text, this, false);
                    addView(textView);
                    this.mTextView = textView;
                    this.mDefaultMaxLines = TextViewCompat.getMaxLines(this.mTextView);
                }
                TextViewCompat.setTextAppearance(this.mTextView, TabLayout.this.mTabTextAppearance);
                if (TabLayout.this.mTabTextColors != null) {
                    this.mTextView.setTextColor(TabLayout.this.mTabTextColors);
                }
                updateTextAndIcon(this.mTextView, this.mIconView);
            } else if (!(this.mCustomTextView == null && this.mCustomIconView == null)) {
                updateTextAndIcon(this.mCustomTextView, this.mCustomIconView);
            }
            if (tab == null || !tab.isSelected()) {
                z = false;
            } else {
                z = true;
            }
            setSelected(z);
        }

        private void updateTextAndIcon(TextView textView, ImageView iconView) {
            Drawable icon;
            CharSequence text;
            CharSequence contentDesc;
            boolean hasText;
            if (this.mTab != null) {
                icon = this.mTab.getIcon();
            } else {
                icon = null;
            }
            if (this.mTab != null) {
                text = this.mTab.getText();
            } else {
                text = null;
            }
            if (this.mTab != null) {
                contentDesc = this.mTab.getContentDescription();
            } else {
                contentDesc = null;
            }
            if (iconView != null) {
                if (icon != null) {
                    iconView.setImageDrawable(icon);
                    iconView.setVisibility(0);
                    setVisibility(0);
                } else {
                    iconView.setVisibility(8);
                    iconView.setImageDrawable(null);
                }
                iconView.setContentDescription(contentDesc);
            }
            if (TextUtils.isEmpty(text)) {
                hasText = false;
            } else {
                hasText = true;
            }
            if (textView != null) {
                if (hasText) {
                    textView.setText(text);
                    textView.setVisibility(0);
                    setVisibility(0);
                } else {
                    textView.setVisibility(8);
                    textView.setText(null);
                }
                textView.setContentDescription(contentDesc);
            }
            if (iconView != null) {
                MarginLayoutParams lp = (MarginLayoutParams) iconView.getLayoutParams();
                int bottomMargin = 0;
                if (hasText && iconView.getVisibility() == 0) {
                    bottomMargin = TabLayout.this.dpToPx(8);
                }
                if (bottomMargin != lp.bottomMargin) {
                    lp.bottomMargin = bottomMargin;
                    iconView.requestLayout();
                }
            }
            if (hasText || TextUtils.isEmpty(contentDesc)) {
                setOnLongClickListener(null);
                setLongClickable(false);
                return;
            }
            setOnLongClickListener(this);
        }

        public final boolean onLongClick(View v) {
            int[] screenPos = new int[2];
            Rect displayFrame = new Rect();
            getLocationOnScreen(screenPos);
            getWindowVisibleDisplayFrame(displayFrame);
            Context context = getContext();
            int width = getWidth();
            int height = getHeight();
            int midy = screenPos[1] + (height / 2);
            int referenceX = screenPos[0] + (width / 2);
            if (ViewCompat.getLayoutDirection(v) == 0) {
                referenceX = context.getResources().getDisplayMetrics().widthPixels - referenceX;
            }
            Toast cheatSheet = Toast.makeText(context, this.mTab.getContentDescription(), 0);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(8388661, referenceX, (screenPos[1] + height) - displayFrame.top);
            } else {
                cheatSheet.setGravity(81, 0, height);
            }
            cheatSheet.show();
            return true;
        }
    }

    public static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
        private final ViewPager mViewPager;

        public ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            this.mViewPager = viewPager;
        }

        public final void onTabSelected(Tab tab) {
            this.mViewPager.setCurrentItem(tab.getPosition());
        }
    }

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTabs = new ArrayList();
        this.mTabMaxWidth = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mSelectedListeners = new ArrayList();
        this.mTabViewPool = new SimplePool(12);
        ThemeUtils.checkAppCompatTheme(context);
        setHorizontalScrollBarEnabled(false);
        this.mTabStrip = new SlidingTabStrip(context);
        super.addView(this.mTabStrip, 0, new FrameLayout.LayoutParams(-2, -1));
        TypedArray a = context.obtainStyledAttributes(attrs, android.support.design.R.styleable.TabLayout, defStyleAttr, R.style.Widget.Design.TabLayout);
        this.mTabStrip.setSelectedIndicatorHeight(a.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabIndicatorHeight, 0));
        this.mTabStrip.setSelectedIndicatorColor(a.getColor(android.support.design.R.styleable.TabLayout_tabIndicatorColor, 0));
        int dimensionPixelSize = a.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPadding, 0);
        this.mTabPaddingBottom = dimensionPixelSize;
        this.mTabPaddingEnd = dimensionPixelSize;
        this.mTabPaddingTop = dimensionPixelSize;
        this.mTabPaddingStart = dimensionPixelSize;
        this.mTabPaddingStart = a.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPaddingStart, this.mTabPaddingStart);
        this.mTabPaddingTop = a.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPaddingTop, this.mTabPaddingTop);
        this.mTabPaddingEnd = a.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPaddingEnd, this.mTabPaddingEnd);
        this.mTabPaddingBottom = a.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPaddingBottom, this.mTabPaddingBottom);
        this.mTabTextAppearance = a.getResourceId(android.support.design.R.styleable.TabLayout_tabTextAppearance, R.style.TextAppearance.Design.Tab);
        TypedArray ta = context.obtainStyledAttributes(this.mTabTextAppearance, android.support.v7.appcompat.R.styleable.TextAppearance);
        try {
            this.mTabTextSize = (float) ta.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.TextAppearance_android_textSize, 0);
            this.mTabTextColors = ta.getColorStateList(android.support.v7.appcompat.R.styleable.TextAppearance_android_textColor);
            if (a.hasValue(android.support.design.R.styleable.TabLayout_tabTextColor)) {
                this.mTabTextColors = a.getColorStateList(android.support.design.R.styleable.TabLayout_tabTextColor);
            }
            if (a.hasValue(android.support.design.R.styleable.TabLayout_tabSelectedTextColor)) {
                int selected = a.getColor(android.support.design.R.styleable.TabLayout_tabSelectedTextColor, 0);
                dimensionPixelSize = this.mTabTextColors.getDefaultColor();
                r6 = new int[2][];
                int[] iArr = new int[]{SELECTED_STATE_SET, selected};
                r6[1] = EMPTY_STATE_SET;
                iArr[1] = dimensionPixelSize;
                this.mTabTextColors = new ColorStateList(r6, iArr);
            }
            this.mRequestedTabMinWidth = a.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabMinWidth, -1);
            this.mRequestedTabMaxWidth = a.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabMaxWidth, -1);
            this.mTabBackgroundResId = a.getResourceId(android.support.design.R.styleable.TabLayout_tabBackground, 0);
            this.mContentInsetStart = a.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabContentStart, 0);
            this.mMode = a.getInt(android.support.design.R.styleable.TabLayout_tabMode, 1);
            this.mTabGravity = a.getInt(android.support.design.R.styleable.TabLayout_tabGravity, 0);
            a.recycle();
            Resources res = getResources();
            this.mTabTextMultiLineSize = (float) res.getDimensionPixelSize(R.dimen.design_tab_text_size_2line);
            this.mScrollableTabMinWidth = res.getDimensionPixelSize(R.dimen.design_tab_scrollable_min_width);
            if (this.mMode == 0) {
                dimensionPixelSize = Math.max(0, this.mContentInsetStart - this.mTabPaddingStart);
            } else {
                dimensionPixelSize = 0;
            }
            ViewCompat.setPaddingRelative(this.mTabStrip, dimensionPixelSize, 0, 0, 0);
            switch (this.mMode) {
                case 0:
                    this.mTabStrip.setGravity(8388611);
                    break;
                case 1:
                    this.mTabStrip.setGravity(1);
                    break;
            }
            updateTabViews(true);
        } finally {
            ta.recycle();
        }
    }

    private void setScrollPosition(int position, float positionOffset, boolean updateSelectedText) {
        setScrollPosition(position, 0.0f, true, true);
    }

    final void setScrollPosition(int position, float positionOffset, boolean updateSelectedText, boolean updateIndicatorPosition) {
        int roundedPosition = Math.round(((float) position) + positionOffset);
        if (roundedPosition >= 0 && roundedPosition < this.mTabStrip.getChildCount()) {
            if (updateIndicatorPosition) {
                this.mTabStrip.setIndicatorPositionFromTabPosition(position, positionOffset);
            }
            if (this.mScrollAnimator != null && this.mScrollAnimator.isRunning()) {
                this.mScrollAnimator.cancel();
            }
            scrollTo(calculateScrollXForTab(position, positionOffset), 0);
            if (updateSelectedText) {
                setSelectedTabView(roundedPosition);
            }
        }
    }

    private void addTab(Tab tab, boolean setSelected) {
        int size = this.mTabs.size();
        if (tab.mParent != this) {
            throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
        }
        configureTab(tab, size);
        View view = tab.mView;
        SlidingTabStrip slidingTabStrip = this.mTabStrip;
        int position = tab.getPosition();
        ViewGroup.LayoutParams layoutParams = new LayoutParams(-2, -1);
        updateTabViewLayoutParams(layoutParams);
        slidingTabStrip.addView(view, position, layoutParams);
        if (setSelected) {
            tab.select();
        }
    }

    private Tab newTab() {
        Tab tab = (Tab) sTabPool.acquire();
        if (tab == null) {
            tab = new Tab();
        }
        tab.mParent = this;
        TabView tabView = (TabView) this.mTabViewPool.acquire();
        if (tabView == null) {
            tabView = new TabView(getContext());
        }
        tabView.setTab(tab);
        tabView.setFocusable(true);
        tabView.setMinimumWidth(getTabMinWidth());
        tab.mView = tabView;
        return tab;
    }

    public final int getTabCount() {
        return this.mTabs.size();
    }

    public final Tab getTabAt(int index) {
        return (index < 0 || index >= this.mTabs.size()) ? null : (Tab) this.mTabs.get(index);
    }

    public final int getSelectedTabPosition() {
        return this.mSelectedTab != null ? this.mSelectedTab.getPosition() : -1;
    }

    public final void removeAllTabs() {
        for (int i = this.mTabStrip.getChildCount() - 1; i >= 0; i--) {
            TabView tabView = (TabView) this.mTabStrip.getChildAt(i);
            this.mTabStrip.removeViewAt(i);
            if (tabView != null) {
                tabView.setTab(null);
                tabView.setSelected(false);
                this.mTabViewPool.release(tabView);
            }
            requestLayout();
        }
        Iterator<Tab> i2 = this.mTabs.iterator();
        while (i2.hasNext()) {
            Tab tab = (Tab) i2.next();
            i2.remove();
            tab.reset();
            sTabPool.release(tab);
        }
        this.mSelectedTab = null;
    }

    private void setupWithViewPager(ViewPager viewPager, boolean autoRefresh, boolean implicitSetup) {
        if (this.mViewPager != null) {
            if (this.mPageChangeListener != null) {
                this.mViewPager.removeOnPageChangeListener(this.mPageChangeListener);
            }
            if (this.mAdapterChangeListener != null) {
                this.mViewPager.removeOnAdapterChangeListener(this.mAdapterChangeListener);
            }
        }
        if (this.mCurrentVpSelectedListener != null) {
            this.mSelectedListeners.remove(this.mCurrentVpSelectedListener);
            this.mCurrentVpSelectedListener = null;
        }
        if (viewPager != null) {
            this.mViewPager = viewPager;
            if (this.mPageChangeListener == null) {
                this.mPageChangeListener = new TabLayoutOnPageChangeListener(this);
            }
            this.mPageChangeListener.reset();
            viewPager.addOnPageChangeListener(this.mPageChangeListener);
            this.mCurrentVpSelectedListener = new ViewPagerOnTabSelectedListener(viewPager);
            OnTabSelectedListener onTabSelectedListener = this.mCurrentVpSelectedListener;
            if (!this.mSelectedListeners.contains(onTabSelectedListener)) {
                this.mSelectedListeners.add(onTabSelectedListener);
            }
            PagerAdapter adapter = viewPager.getAdapter();
            if (adapter != null) {
                setPagerAdapter(adapter, autoRefresh);
            }
            if (this.mAdapterChangeListener == null) {
                this.mAdapterChangeListener = new AdapterChangeListener();
            }
            this.mAdapterChangeListener.setAutoRefresh(autoRefresh);
            viewPager.addOnAdapterChangeListener(this.mAdapterChangeListener);
            setScrollPosition(viewPager.getCurrentItem(), 0.0f, true);
        } else {
            this.mViewPager = null;
            setPagerAdapter(null, false);
        }
        this.mSetupViewPagerImplicitly = implicitSetup;
    }

    public boolean shouldDelayChildPressedState() {
        return Math.max(0, ((this.mTabStrip.getWidth() - getWidth()) - getPaddingLeft()) - getPaddingRight()) > 0;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mViewPager == null) {
            ViewParent vp = getParent();
            if (vp instanceof ViewPager) {
                setupWithViewPager((ViewPager) vp, true, true);
            }
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mSetupViewPagerImplicitly) {
            setupWithViewPager(null);
            this.mSetupViewPagerImplicitly = false;
        }
    }

    final void setPagerAdapter(PagerAdapter adapter, boolean addObserver) {
        if (!(this.mPagerAdapter == null || this.mPagerAdapterObserver == null)) {
            this.mPagerAdapter.unregisterDataSetObserver(this.mPagerAdapterObserver);
        }
        this.mPagerAdapter = adapter;
        if (addObserver && adapter != null) {
            if (this.mPagerAdapterObserver == null) {
                this.mPagerAdapterObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(this.mPagerAdapterObserver);
        }
        populateFromPagerAdapter();
    }

    final void populateFromPagerAdapter() {
        removeAllTabs();
        if (this.mPagerAdapter != null) {
            int adapterCount = this.mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                addTab(newTab().setText(this.mPagerAdapter.getPageTitle(i)), false);
            }
            if (this.mViewPager != null && adapterCount > 0) {
                int curItem = this.mViewPager.getCurrentItem();
                if (curItem != getSelectedTabPosition() && curItem < this.mTabs.size()) {
                    selectTab(getTabAt(curItem), true);
                }
            }
        }
    }

    private void configureTab(Tab tab, int position) {
        tab.setPosition(position);
        this.mTabs.add(position, tab);
        int count = this.mTabs.size();
        for (int i = position + 1; i < count; i++) {
            ((Tab) this.mTabs.get(i)).setPosition(i);
        }
    }

    public void addView(View child) {
        addViewInternal(child);
    }

    public void addView(View child, int index) {
        addViewInternal(child);
    }

    public void addView(View child, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    private void addViewInternal(View child) {
        if (child instanceof TabItem) {
            TabItem tabItem = (TabItem) child;
            Tab newTab = newTab();
            if (tabItem.mText != null) {
                newTab.setText(tabItem.mText);
            }
            if (tabItem.mIcon != null) {
                newTab.setIcon(tabItem.mIcon);
            }
            if (tabItem.mCustomLayout != 0) {
                newTab.setCustomView(LayoutInflater.from(newTab.mView.getContext()).inflate(tabItem.mCustomLayout, newTab.mView, false));
            }
            if (!TextUtils.isEmpty(tabItem.getContentDescription())) {
                newTab.setContentDescription(tabItem.getContentDescription());
            }
            addTab(newTab, this.mTabs.isEmpty());
            return;
        }
        throw new IllegalArgumentException("Only TabItem instances can be added to TabLayout");
    }

    private void updateTabViewLayoutParams(LayoutParams lp) {
        if (this.mMode == 1 && this.mTabGravity == 0) {
            lp.width = 0;
            lp.weight = 1.0f;
            return;
        }
        lp.width = -2;
        lp.weight = 0.0f;
    }

    final int dpToPx(int dps) {
        return Math.round(getResources().getDisplayMetrics().density * ((float) dps));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int size = this.mTabs.size();
        for (int i2 = 0; i2 < size; i2++) {
            Tab tab = (Tab) this.mTabs.get(i2);
            if (tab != null && tab.getIcon() != null && !TextUtils.isEmpty(tab.getText())) {
                i = 1;
                break;
            }
        }
        i = 0;
        if (i != 0) {
            i = 72;
        } else {
            i = 48;
        }
        int idealHeight = (dpToPx(i) + getPaddingTop()) + getPaddingBottom();
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case Integer.MIN_VALUE:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(idealHeight, MeasureSpec.getSize(heightMeasureSpec)), 1073741824);
                break;
            case 0:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(idealHeight, 1073741824);
                break;
        }
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) != 0) {
            if (this.mRequestedTabMaxWidth > 0) {
                i = this.mRequestedTabMaxWidth;
            } else {
                i = specWidth - dpToPx(56);
            }
            this.mTabMaxWidth = i;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 1) {
            View child = getChildAt(0);
            boolean remeasure = false;
            switch (this.mMode) {
                case 0:
                    if (child.getMeasuredWidth() < getMeasuredWidth()) {
                        remeasure = true;
                    } else {
                        remeasure = false;
                    }
                    break;
                case 1:
                    remeasure = child.getMeasuredWidth() != getMeasuredWidth();
                    break;
            }
            if (remeasure) {
                child.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), child.getLayoutParams().height));
            }
        }
    }

    private void animateToTab(int newPosition) {
        boolean z = false;
        if (newPosition != -1) {
            if (getWindowToken() != null && ViewCompat.isLaidOut(this)) {
                SlidingTabStrip slidingTabStrip = this.mTabStrip;
                int childCount = slidingTabStrip.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (slidingTabStrip.getChildAt(i).getWidth() <= 0) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    int startScrollX = getScrollX();
                    int targetScrollX = calculateScrollXForTab(newPosition, 0.0f);
                    if (startScrollX != targetScrollX) {
                        if (this.mScrollAnimator == null) {
                            this.mScrollAnimator = ViewUtils.createAnimator();
                            this.mScrollAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                            this.mScrollAnimator.setDuration(300);
                            this.mScrollAnimator.addUpdateListener(new AnimatorUpdateListener() {
                                public final void onAnimationUpdate(ValueAnimatorCompat animator) {
                                    TabLayout.this.scrollTo(animator.getAnimatedIntValue(), 0);
                                }
                            });
                        }
                        this.mScrollAnimator.setIntValues(startScrollX, targetScrollX);
                        this.mScrollAnimator.start();
                    }
                    this.mTabStrip.animateIndicatorToPosition(newPosition, 300);
                    return;
                }
            }
            setScrollPosition(newPosition, 0.0f, true);
        }
    }

    private void setSelectedTabView(int position) {
        int tabCount = this.mTabStrip.getChildCount();
        if (position < tabCount) {
            int i = 0;
            while (i < tabCount) {
                this.mTabStrip.getChildAt(i).setSelected(i == position);
                i++;
            }
        }
    }

    final void selectTab(Tab tab) {
        selectTab(tab, true);
    }

    final void selectTab(Tab tab, boolean updateIndicator) {
        Tab currentTab = this.mSelectedTab;
        int size;
        if (currentTab != tab) {
            int newPosition;
            if (tab != null) {
                newPosition = tab.getPosition();
            } else {
                newPosition = -1;
            }
            if (updateIndicator) {
                if ((currentTab == null || currentTab.getPosition() == -1) && newPosition != -1) {
                    setScrollPosition(newPosition, 0.0f, true);
                } else {
                    animateToTab(newPosition);
                }
                if (newPosition != -1) {
                    setSelectedTabView(newPosition);
                }
            }
            if (currentTab != null) {
                for (size = this.mSelectedListeners.size() - 1; size >= 0; size--) {
                    this.mSelectedListeners.get(size);
                }
            }
            this.mSelectedTab = tab;
            if (tab != null) {
                for (int size2 = this.mSelectedListeners.size() - 1; size2 >= 0; size2--) {
                    ((OnTabSelectedListener) this.mSelectedListeners.get(size2)).onTabSelected(tab);
                }
            }
        } else if (currentTab != null) {
            for (size = this.mSelectedListeners.size() - 1; size >= 0; size--) {
                this.mSelectedListeners.get(size);
            }
            animateToTab(tab.getPosition());
        }
    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        int nextWidth = 0;
        if (this.mMode != 0) {
            return 0;
        }
        int selectedWidth;
        View selectedChild = this.mTabStrip.getChildAt(position);
        View nextChild = position + 1 < this.mTabStrip.getChildCount() ? this.mTabStrip.getChildAt(position + 1) : null;
        if (selectedChild != null) {
            selectedWidth = selectedChild.getWidth();
        } else {
            selectedWidth = 0;
        }
        if (nextChild != null) {
            nextWidth = nextChild.getWidth();
        }
        int scrollBase = (selectedChild.getLeft() + (selectedWidth / 2)) - (getWidth() / 2);
        int scrollOffset = (int) ((((float) (selectedWidth + nextWidth)) * 0.5f) * positionOffset);
        return ViewCompat.getLayoutDirection(this) == 0 ? scrollBase + scrollOffset : scrollBase - scrollOffset;
    }

    final void updateTabViews(boolean requestLayout) {
        for (int i = 0; i < this.mTabStrip.getChildCount(); i++) {
            View child = this.mTabStrip.getChildAt(i);
            child.setMinimumWidth(getTabMinWidth());
            updateTabViewLayoutParams((LayoutParams) child.getLayoutParams());
            if (requestLayout) {
                child.requestLayout();
            }
        }
    }

    private int getTabMinWidth() {
        if (this.mRequestedTabMinWidth != -1) {
            return this.mRequestedTabMinWidth;
        }
        return this.mMode == 0 ? this.mScrollableTabMinWidth : 0;
    }

    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return generateDefaultLayoutParams();
    }

    public final void setupWithViewPager(ViewPager viewPager) {
        setupWithViewPager(viewPager, true, false);
    }
}
