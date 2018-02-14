package android.support.v7.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.NestedScrollView.OnScrollChangeListener;
import android.support.v7.appcompat.R;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.lang.ref.WeakReference;

final class AlertController {
    ListAdapter mAdapter;
    private int mAlertDialogLayout;
    private final OnClickListener mButtonHandler = new OnClickListener() {
        public final void onClick(View v) {
            Message m;
            if (v == AlertController.this.mButtonPositive && AlertController.this.mButtonPositiveMessage != null) {
                m = Message.obtain(AlertController.this.mButtonPositiveMessage);
            } else if (v == AlertController.this.mButtonNegative && AlertController.this.mButtonNegativeMessage != null) {
                m = Message.obtain(AlertController.this.mButtonNegativeMessage);
            } else if (v != AlertController.this.mButtonNeutral || AlertController.this.mButtonNeutralMessage == null) {
                m = null;
            } else {
                m = Message.obtain(AlertController.this.mButtonNeutralMessage);
            }
            if (m != null) {
                m.sendToTarget();
            }
            AlertController.this.mHandler.obtainMessage(1, AlertController.this.mDialog).sendToTarget();
        }
    };
    Button mButtonNegative;
    Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    Button mButtonNeutral;
    Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    private int mButtonPanelLayoutHint = 0;
    private int mButtonPanelSideLayout;
    Button mButtonPositive;
    Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    int mCheckedItem = -1;
    private final Context mContext;
    private View mCustomTitleView;
    final AppCompatDialog mDialog;
    Handler mHandler;
    private Drawable mIcon;
    private int mIconId = 0;
    private ImageView mIconView;
    int mListItemLayout;
    int mListLayout;
    ListView mListView;
    private CharSequence mMessage;
    private TextView mMessageView;
    int mMultiChoiceItemLayout;
    NestedScrollView mScrollView;
    private boolean mShowTitle;
    int mSingleChoiceItemLayout;
    private CharSequence mTitle;
    private TextView mTitleView;
    private View mView;
    private int mViewLayoutResId;
    private int mViewSpacingBottom;
    private int mViewSpacingLeft;
    private int mViewSpacingRight;
    private boolean mViewSpacingSpecified = false;
    private int mViewSpacingTop;
    private final Window mWindow;

    public static class AlertParams {
        public ListAdapter mAdapter;
        public boolean mCancelable;
        public int mCheckedItem = -1;
        public final Context mContext;
        public View mCustomTitleView;
        public Drawable mIcon;
        public int mIconAttrId = 0;
        public int mIconId = 0;
        public final LayoutInflater mInflater;
        public CharSequence[] mItems;
        public CharSequence mMessage;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNeutralButtonListener;
        public CharSequence mNeutralButtonText;
        public DialogInterface.OnClickListener mOnClickListener;
        public OnKeyListener mOnKeyListener;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public boolean mRecycleOnMeasure = true;
        public CharSequence mTitle;
        public View mView;
        public int mViewLayoutResId;
        public boolean mViewSpacingSpecified = false;

        public AlertParams(Context context) {
            this.mContext = context;
            this.mCancelable = true;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public final void apply(final AlertController dialog) {
            if (this.mCustomTitleView != null) {
                dialog.setCustomTitle(this.mCustomTitleView);
            } else {
                if (this.mTitle != null) {
                    dialog.setTitle(this.mTitle);
                }
                if (this.mIcon != null) {
                    dialog.setIcon(this.mIcon);
                }
            }
            if (this.mMessage != null) {
                dialog.setMessage(this.mMessage);
            }
            if (this.mPositiveButtonText != null) {
                dialog.setButton(-1, this.mPositiveButtonText, this.mPositiveButtonListener, null);
            }
            if (this.mNegativeButtonText != null) {
                dialog.setButton(-2, this.mNegativeButtonText, this.mNegativeButtonListener, null);
            }
            if (this.mNeutralButtonText != null) {
                dialog.setButton(-3, this.mNeutralButtonText, this.mNeutralButtonListener, null);
            }
            if (!(this.mItems == null && this.mAdapter == null)) {
                ListAdapter listAdapter;
                RecycleListView recycleListView = (RecycleListView) this.mInflater.inflate(dialog.mListLayout, null);
                int i = dialog.mListItemLayout;
                if (this.mAdapter != null) {
                    listAdapter = this.mAdapter;
                } else {
                    listAdapter = new CheckedItemAdapter(this.mContext, i, 16908308, this.mItems);
                }
                dialog.mAdapter = listAdapter;
                dialog.mCheckedItem = this.mCheckedItem;
                if (this.mOnClickListener != null) {
                    recycleListView.setOnItemClickListener(new OnItemClickListener() {
                        public final void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                            AlertParams.this.mOnClickListener.onClick(dialog.mDialog, position);
                            dialog.mDialog.dismiss();
                        }
                    });
                }
                dialog.mListView = recycleListView;
            }
            if (this.mView != null) {
                dialog.setView(this.mView);
            }
        }
    }

    private static final class ButtonHandler extends Handler {
        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            this.mDialog = new WeakReference(dialog);
        }

        public final void handleMessage(Message msg) {
            switch (msg.what) {
                case -3:
                case -2:
                case -1:
                    ((DialogInterface.OnClickListener) msg.obj).onClick((DialogInterface) this.mDialog.get(), msg.what);
                    return;
                case 1:
                    ((DialogInterface) msg.obj).dismiss();
                    return;
                default:
                    return;
            }
        }
    }

    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public CheckedItemAdapter(Context context, int resource, int textViewResourceId, CharSequence[] objects) {
            super(context, resource, 16908308, objects);
        }

        public final boolean hasStableIds() {
            return true;
        }

        public final long getItemId(int position) {
            return (long) position;
        }
    }

    public static class RecycleListView extends ListView {
        private final int mPaddingBottomNoButtons;
        private final int mPaddingTopNoTitle;

        public RecycleListView(Context context) {
            this(context, null);
        }

        public RecycleListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RecycleListView);
            this.mPaddingBottomNoButtons = ta.getDimensionPixelOffset(R.styleable.RecycleListView_paddingBottomNoButtons, -1);
            this.mPaddingTopNoTitle = ta.getDimensionPixelOffset(R.styleable.RecycleListView_paddingTopNoTitle, -1);
        }

        public final void setHasDecor(boolean hasTitle, boolean hasButtons) {
            if (!hasButtons || !hasTitle) {
                setPadding(getPaddingLeft(), hasTitle ? getPaddingTop() : this.mPaddingTopNoTitle, getPaddingRight(), hasButtons ? getPaddingBottom() : this.mPaddingBottomNoButtons);
            }
        }
    }

    public AlertController(Context context, AppCompatDialog di, Window window) {
        this.mContext = context;
        this.mDialog = di;
        this.mWindow = window;
        this.mHandler = new ButtonHandler(di);
        TypedArray a = context.obtainStyledAttributes(null, R.styleable.AlertDialog, com.rachio.iro.R.attr.alertDialogStyle, 0);
        this.mAlertDialogLayout = a.getResourceId(R.styleable.AlertDialog_android_layout, 0);
        this.mButtonPanelSideLayout = a.getResourceId(R.styleable.AlertDialog_buttonPanelSideLayout, 0);
        this.mListLayout = a.getResourceId(R.styleable.AlertDialog_listLayout, 0);
        this.mMultiChoiceItemLayout = a.getResourceId(R.styleable.AlertDialog_multiChoiceItemLayout, 0);
        this.mSingleChoiceItemLayout = a.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 0);
        this.mListItemLayout = a.getResourceId(R.styleable.AlertDialog_listItemLayout, 0);
        this.mShowTitle = a.getBoolean(R.styleable.AlertDialog_showTitle, true);
        a.recycle();
        di.getDelegate().requestWindowFeature(1);
    }

    private static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            if (canTextInput(vg.getChildAt(i))) {
                return true;
            }
        }
        return false;
    }

    public final void installContent() {
        View view;
        boolean z;
        boolean z2;
        int i = 0;
        int i2 = this.mButtonPanelSideLayout;
        this.mDialog.setContentView(this.mAlertDialogLayout);
        View findViewById = this.mWindow.findViewById(com.rachio.iro.R.id.parentPanel);
        View findViewById2 = findViewById.findViewById(com.rachio.iro.R.id.topPanel);
        View findViewById3 = findViewById.findViewById(com.rachio.iro.R.id.contentPanel);
        View findViewById4 = findViewById.findViewById(com.rachio.iro.R.id.buttonPanel);
        ViewGroup viewGroup = (ViewGroup) findViewById.findViewById(com.rachio.iro.R.id.customPanel);
        if (this.mView != null) {
            view = this.mView;
        } else if (this.mViewLayoutResId != 0) {
            view = LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, viewGroup, false);
        } else {
            view = null;
        }
        if (view != null) {
            z = true;
        } else {
            z = false;
        }
        if (!(z && canTextInput(view))) {
            this.mWindow.setFlags(131072, 131072);
        }
        if (z) {
            FrameLayout frameLayout = (FrameLayout) this.mWindow.findViewById(com.rachio.iro.R.id.custom);
            frameLayout.addView(view, new LayoutParams(-1, -1));
            if (this.mViewSpacingSpecified) {
                frameLayout.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
            }
            if (this.mListView != null) {
                ((LinearLayout.LayoutParams) viewGroup.getLayoutParams()).weight = 0.0f;
            }
        } else {
            viewGroup.setVisibility(8);
        }
        View findViewById5 = viewGroup.findViewById(com.rachio.iro.R.id.topPanel);
        view = viewGroup.findViewById(com.rachio.iro.R.id.contentPanel);
        View findViewById6 = viewGroup.findViewById(com.rachio.iro.R.id.buttonPanel);
        ViewGroup resolvePanel = resolvePanel(findViewById5, findViewById2);
        ViewGroup resolvePanel2 = resolvePanel(view, findViewById3);
        ViewGroup resolvePanel3 = resolvePanel(findViewById6, findViewById4);
        this.mScrollView = (NestedScrollView) this.mWindow.findViewById(com.rachio.iro.R.id.scrollView);
        this.mScrollView.setFocusable(false);
        this.mScrollView.setNestedScrollingEnabled(false);
        this.mMessageView = (TextView) resolvePanel2.findViewById(16908299);
        if (this.mMessageView != null) {
            if (this.mMessage != null) {
                this.mMessageView.setText(this.mMessage);
            } else {
                this.mMessageView.setVisibility(8);
                this.mScrollView.removeView(this.mMessageView);
                if (this.mListView != null) {
                    ViewGroup viewGroup2 = (ViewGroup) this.mScrollView.getParent();
                    int indexOfChild = viewGroup2.indexOfChild(this.mScrollView);
                    viewGroup2.removeViewAt(indexOfChild);
                    viewGroup2.addView(this.mListView, indexOfChild, new LayoutParams(-1, -1));
                } else {
                    resolvePanel2.setVisibility(8);
                }
            }
        }
        this.mButtonPositive = (Button) resolvePanel3.findViewById(16908313);
        this.mButtonPositive.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(8);
            indexOfChild = 0;
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            this.mButtonPositive.setVisibility(0);
            indexOfChild = 1;
        }
        this.mButtonNegative = (Button) resolvePanel3.findViewById(16908314);
        this.mButtonNegative.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(8);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            this.mButtonNegative.setVisibility(0);
            indexOfChild |= 2;
        }
        this.mButtonNeutral = (Button) resolvePanel3.findViewById(16908315);
        this.mButtonNeutral.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(8);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            this.mButtonNeutral.setVisibility(0);
            indexOfChild |= 4;
        }
        Context context = this.mContext;
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(com.rachio.iro.R.attr.alertDialogCenterButtons, typedValue, true);
        if (typedValue.data != 0) {
            z = true;
        } else {
            z = false;
        }
        if (z) {
            if (indexOfChild == 1) {
                centerButton(this.mButtonPositive);
            } else if (indexOfChild == 2) {
                centerButton(this.mButtonNegative);
            } else if (indexOfChild == 4) {
                centerButton(this.mButtonNeutral);
            }
        }
        if (indexOfChild != 0) {
            z = true;
        } else {
            z = false;
        }
        if (!z) {
            resolvePanel3.setVisibility(8);
        }
        if (this.mCustomTitleView != null) {
            resolvePanel.addView(this.mCustomTitleView, 0, new LayoutParams(-1, -2));
            this.mWindow.findViewById(com.rachio.iro.R.id.title_template).setVisibility(8);
        } else {
            this.mIconView = (ImageView) this.mWindow.findViewById(16908294);
            if ((!TextUtils.isEmpty(this.mTitle)) && this.mShowTitle) {
                this.mTitleView = (TextView) this.mWindow.findViewById(com.rachio.iro.R.id.alertTitle);
                this.mTitleView.setText(this.mTitle);
                if (this.mIconId != 0) {
                    this.mIconView.setImageResource(this.mIconId);
                } else if (this.mIcon != null) {
                    this.mIconView.setImageDrawable(this.mIcon);
                } else {
                    this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
                    this.mIconView.setVisibility(8);
                }
            } else {
                this.mWindow.findViewById(com.rachio.iro.R.id.title_template).setVisibility(8);
                this.mIconView.setVisibility(8);
                resolvePanel.setVisibility(8);
            }
        }
        boolean z3 = (viewGroup == null || viewGroup.getVisibility() == 8) ? false : true;
        if (resolvePanel == null || resolvePanel.getVisibility() == 8) {
            z2 = false;
        } else {
            z2 = true;
        }
        if (resolvePanel3 == null || resolvePanel3.getVisibility() == 8) {
            z = false;
        } else {
            z = true;
        }
        if (!(z || resolvePanel2 == null)) {
            findViewById = resolvePanel2.findViewById(com.rachio.iro.R.id.textSpacerNoButtons);
            if (findViewById != null) {
                findViewById.setVisibility(0);
            }
        }
        if (z2) {
            if (this.mScrollView != null) {
                this.mScrollView.setClipToPadding(true);
            }
            if ((this.mMessage == null && this.mListView == null && !z3) || z3) {
                findViewById = null;
            } else {
                findViewById = resolvePanel.findViewById(com.rachio.iro.R.id.titleDividerNoCustom);
            }
            if (findViewById != null) {
                findViewById.setVisibility(0);
            }
        } else if (resolvePanel2 != null) {
            findViewById = resolvePanel2.findViewById(com.rachio.iro.R.id.textSpacerNoTitle);
            if (findViewById != null) {
                findViewById.setVisibility(0);
            }
        }
        if (this.mListView instanceof RecycleListView) {
            ((RecycleListView) this.mListView).setHasDecor(z2, z);
        }
        if (!z3) {
            view = this.mListView != null ? this.mListView : this.mScrollView;
            if (view != null) {
                if (z2) {
                    i2 = 1;
                } else {
                    i2 = 0;
                }
                if (z) {
                    i = 2;
                }
                i |= i2;
                findViewById5 = this.mWindow.findViewById(com.rachio.iro.R.id.scrollIndicatorUp);
                findViewById = this.mWindow.findViewById(com.rachio.iro.R.id.scrollIndicatorDown);
                if (VERSION.SDK_INT >= 23) {
                    ViewCompat.setScrollIndicators(view, i, 3);
                    if (findViewById5 != null) {
                        resolvePanel2.removeView(findViewById5);
                    }
                    if (findViewById != null) {
                        resolvePanel2.removeView(findViewById);
                    }
                } else {
                    if (findViewById5 != null && (i & 1) == 0) {
                        resolvePanel2.removeView(findViewById5);
                        findViewById5 = null;
                    }
                    if (findViewById != null && (i & 2) == 0) {
                        resolvePanel2.removeView(findViewById);
                        findViewById = null;
                    }
                    if (!(findViewById5 == null && findViewById == null)) {
                        if (this.mMessage != null) {
                            this.mScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
                                public final void onScrollChange$227623bf(NestedScrollView v) {
                                    AlertController.manageScrollIndicators(v, findViewById5, findViewById);
                                }
                            });
                            this.mScrollView.post(new Runnable() {
                                public final void run() {
                                    AlertController.manageScrollIndicators(AlertController.this.mScrollView, findViewById5, findViewById);
                                }
                            });
                        } else if (this.mListView != null) {
                            this.mListView.setOnScrollListener(new OnScrollListener() {
                                public final void onScrollStateChanged(AbsListView view, int scrollState) {
                                }

                                public final void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                    AlertController.manageScrollIndicators(v, findViewById5, findViewById);
                                }
                            });
                            this.mListView.post(new Runnable() {
                                public final void run() {
                                    AlertController.manageScrollIndicators(AlertController.this.mListView, findViewById5, findViewById);
                                }
                            });
                        } else {
                            if (findViewById5 != null) {
                                resolvePanel2.removeView(findViewById5);
                            }
                            if (findViewById != null) {
                                resolvePanel2.removeView(findViewById);
                            }
                        }
                    }
                }
            }
        }
        ListView listView = this.mListView;
        if (listView != null && this.mAdapter != null) {
            listView.setAdapter(this.mAdapter);
            int i3 = this.mCheckedItem;
            if (i3 >= 0) {
                listView.setItemChecked(i3, true);
                listView.setSelection(i3);
            }
        }
    }

    public final void setTitle(CharSequence title) {
        this.mTitle = title;
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
        }
    }

    public final void setCustomTitle(View customTitleView) {
        this.mCustomTitleView = customTitleView;
    }

    public final void setMessage(CharSequence message) {
        this.mMessage = message;
        if (this.mMessageView != null) {
            this.mMessageView.setText(message);
        }
    }

    public final void setView(View view) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = false;
    }

    public final void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener, Message msg) {
        if (listener != null) {
            msg = this.mHandler.obtainMessage(whichButton, listener);
        }
        switch (whichButton) {
            case -3:
                this.mButtonNeutralText = text;
                this.mButtonNeutralMessage = msg;
                return;
            case -2:
                this.mButtonNegativeText = text;
                this.mButtonNegativeMessage = msg;
                return;
            case -1:
                this.mButtonPositiveText = text;
                this.mButtonPositiveMessage = msg;
                return;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public final void setIcon(Drawable icon) {
        this.mIcon = icon;
        this.mIconId = 0;
        if (this.mIconView == null) {
            return;
        }
        if (icon != null) {
            this.mIconView.setVisibility(0);
            this.mIconView.setImageDrawable(icon);
            return;
        }
        this.mIconView.setVisibility(8);
    }

    private static ViewGroup resolvePanel(View customPanel, View defaultPanel) {
        if (customPanel == null) {
            if (defaultPanel instanceof ViewStub) {
                defaultPanel = ((ViewStub) defaultPanel).inflate();
            }
            return (ViewGroup) defaultPanel;
        }
        if (defaultPanel != null) {
            ViewParent parent = defaultPanel.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(defaultPanel);
            }
        }
        if (customPanel instanceof ViewStub) {
            customPanel = ((ViewStub) customPanel).inflate();
        }
        return (ViewGroup) customPanel;
    }

    static void manageScrollIndicators(View v, View upIndicator, View downIndicator) {
        int i = 0;
        if (upIndicator != null) {
            upIndicator.setVisibility(ViewCompat.canScrollVertically(v, -1) ? 0 : 4);
        }
        if (downIndicator != null) {
            if (!ViewCompat.canScrollVertically(v, 1)) {
                i = 4;
            }
            downIndicator.setVisibility(i);
        }
    }

    private static void centerButton(Button button) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.gravity = 1;
        params.weight = 0.5f;
        button.setLayoutParams(params);
    }
}
