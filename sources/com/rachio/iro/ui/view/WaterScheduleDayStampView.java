package com.rachio.iro.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.WateringScheduleType;

public class WaterScheduleDayStampView extends LinearLayout {
    private TextView dayNameText;
    private TextView dayNumberText;

    public WaterScheduleDayStampView(Context context) {
        this(context, null);
    }

    public WaterScheduleDayStampView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.dayNameText = (TextView) findViewById(R.id.water_schedule_day_name);
        this.dayNumberText = (TextView) findViewById(R.id.water_schedule_day_number);
    }

    public final void setDay(String dayNumber, String dayOfTheWeek, WateringScheduleType iconType) {
        this.dayNameText.setText(dayOfTheWeek);
        this.dayNumberText.setText(dayNumber);
        this.dayNumberText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, iconType.resourceId);
    }

    public final void setToday(boolean isToday) {
        int bodyColor = getResources().getColor(R.color.rachio_blue);
        int headerColor = getResources().getColor(R.color.rachio_blue_dark);
        int textColor = getResources().getColor(R.color.rachio_white);
        ((View) this.dayNumberText.getParent()).setBackgroundColor(bodyColor);
        this.dayNameText.setBackgroundColor(headerColor);
        this.dayNumberText.setTextColor(textColor);
    }
}
