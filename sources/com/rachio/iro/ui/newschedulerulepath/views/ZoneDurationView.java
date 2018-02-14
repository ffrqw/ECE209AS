package com.rachio.iro.ui.newschedulerulepath.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.ui.view.RepeatClickableImageView;
import com.rachio.iro.utils.TimeStringUtil;

public class ZoneDurationView extends FrameLayout {
    private RepeatClickableImageView decrease;
    private TextView duration;
    private TextView flexAdjust;
    private LinearLayout flexAdjustDuration;
    private TextView flexDuration;
    private RepeatClickableImageView increase;
    private boolean isFlex;
    private Listener listener;
    private int position;
    private TextView zoneName;

    public interface Listener {
        void onDecrease(int i);

        void onIncrease(int i);
    }

    public ZoneDurationView(Context context) {
        this(context, null);
    }

    private ZoneDurationView(Context context, AttributeSet attrs) {
        this(context, null, -1);
    }

    private ZoneDurationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, -1);
        this.isFlex = false;
        inflate(context, R.layout.view_schedulerule_zoneduration, this);
        this.zoneName = (TextView) findViewById(R.id.zonename);
        this.decrease = (RepeatClickableImageView) findViewById(R.id.decrease);
        this.duration = (TextView) findViewById(R.id.duration);
        this.flexAdjustDuration = (LinearLayout) findViewById(R.id.flexadjustduration);
        this.flexAdjust = (TextView) this.flexAdjustDuration.findViewById(R.id.adjust);
        this.flexDuration = (TextView) this.flexAdjustDuration.findViewById(R.id.duration_flex);
        this.increase = (RepeatClickableImageView) findViewById(R.id.increase);
        this.decrease.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZoneDurationView.this.listener != null) {
                    ZoneDurationView.this.listener.onDecrease(ZoneDurationView.this.position);
                }
            }
        });
        this.increase.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZoneDurationView.this.listener != null) {
                    ZoneDurationView.this.listener.onIncrease(ZoneDurationView.this.position);
                }
            }
        });
    }

    public final void setFlex(boolean isFlex) {
        this.isFlex = isFlex;
        if (isFlex) {
            this.flexAdjustDuration.setVisibility(0);
            this.duration.setVisibility(8);
            return;
        }
        this.duration.setVisibility(0);
        this.flexAdjustDuration.setVisibility(8);
    }

    public final void set(int position, String zoneName, double multiplier, int duration) {
        this.position = position;
        this.zoneName.setText(zoneName);
        int percent = (int) Math.round(multiplier * 100.0d);
        this.flexAdjust.setText(String.format("%d%%", new Object[]{Integer.valueOf(percent)}));
        String durationString = TimeStringUtil.getStringForNumberOfHoursMinutesAndSecondsCompact(duration - (duration % 60));
        this.duration.setText(durationString);
        this.flexDuration.setText(durationString);
    }

    public final void setListener(Listener listener) {
        this.listener = listener;
    }
}
