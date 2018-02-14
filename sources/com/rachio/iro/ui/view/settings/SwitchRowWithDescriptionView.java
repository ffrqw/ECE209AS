package com.rachio.iro.ui.view.settings;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.ui.newschedulerulepath.views.Expandable;
import com.rachio.iro.ui.newschedulerulepath.views.Expandable.Listener;

@Deprecated
public class SwitchRowWithDescriptionView extends LinearLayout implements Checkable, Expandable {
    private final TextView description;
    private boolean expanded;
    private Listener listener;
    private final SwitchCompat theSwitch;
    private final TextView title;

    static /* synthetic */ void access$000(SwitchRowWithDescriptionView x0) {
        x0.expanded = !x0.expanded;
        if (x0.expanded) {
            x0.expanded = true;
            x0.description.animate().alpha(1.0f).setListener(new BaseAnimationListener() {
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    SwitchRowWithDescriptionView.this.description.setVisibility(0);
                }
            });
            if (x0.listener != null) {
                x0.listener.onExpanded(x0);
                return;
            }
            return;
        }
        x0.collapse();
    }

    public SwitchRowWithDescriptionView(Context context) {
        this(context, null);
    }

    public SwitchRowWithDescriptionView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SwitchRowWithDescriptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.expanded = false;
        inflate(context, R.layout.view_switchrowwithdescription, this);
        this.title = (TextView) findViewById(R.id.switchwithdescription_title);
        this.description = (TextView) findViewById(R.id.switchwithdescription_description);
        this.theSwitch = (SwitchCompat) findViewById(R.id.switchwithdescription_switch);
        ImageView expand = (ImageView) findViewById(R.id.switchwithdescription_expand);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwitchRowWithDescriptionView, 0, 0);
            try {
                boolean collapsable = a.getBoolean(2, false);
                if (collapsable) {
                    expand.setVisibility(0);
                    expand.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            SwitchRowWithDescriptionView.access$000(SwitchRowWithDescriptionView.this);
                        }
                    });
                }
                this.title.setText(a.getText(0));
                CharSequence descriptionText = a.getText(1);
                if (descriptionText != null) {
                    this.description.setText(descriptionText);
                    if (!collapsable) {
                        this.description.setVisibility(0);
                    }
                }
                a.recycle();
            } catch (Throwable th) {
                a.recycle();
            }
        }
    }

    public void setChecked(boolean checked) {
        this.theSwitch.setChecked(checked);
    }

    public boolean isChecked() {
        return this.theSwitch.isChecked();
    }

    public void toggle() {
        this.theSwitch.toggle();
    }

    public final void setOnCheckedChangedListener(OnCheckedChangeListener listener) {
        this.theSwitch.setOnCheckedChangeListener(listener);
    }

    public final void collapse() {
        this.expanded = false;
        this.description.animate().alpha(0.0f).setListener(new BaseAnimationListener() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                SwitchRowWithDescriptionView.this.description.setVisibility(8);
            }
        });
    }

    public final void setExpandListener(Listener listener) {
        this.listener = listener;
    }
}
