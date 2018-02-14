package com.rachio.iro.ui.view;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.utils.TimeStringUtil;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyYardStampView extends LinearLayout {
    private TextView descriptionLeft;
    private TextView descriptionRight;
    private View divider;
    private ImageView iconLeft;
    private ImageView iconRight;
    private View leftContainer;
    private View rightContainer;
    private LinearLayout wateringNowContainer;
    private TextView wateringNowText;
    private ImageView yardImage;
    private TextView zoneNameAndIcon;

    public MyYardStampView(Context context) {
        this(context, null);
    }

    public MyYardStampView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.wateringNowContainer = (LinearLayout) findViewById(R.id.yard_watering_now_container);
        this.wateringNowText = (TextView) findViewById(R.id.yard_watering_now_text);
        this.leftContainer = findViewById(R.id.yard_description_left_container);
        this.divider = findViewById(R.id.yard_description_divider);
        this.rightContainer = findViewById(R.id.yard_description_right_container);
        this.yardImage = (ImageView) findViewById(R.id.yard_image);
        this.iconLeft = (ImageView) findViewById(R.id.yard_description_left_icon);
        this.iconRight = (ImageView) findViewById(R.id.yard_description_right_icon);
        this.descriptionLeft = (TextView) findViewById(R.id.yard_description_left_text);
        this.descriptionRight = (TextView) findViewById(R.id.yard_description_right_text);
        this.zoneNameAndIcon = (TextView) findViewById(R.id.yard_name_and_icon);
    }

    public final void setZone(Zone zone, int activeZone, int activeZoneDuration) {
        Picasso.with().load(zone.imageUrl).into(this.yardImage);
        if (activeZone == zone.zoneNumber) {
            this.leftContainer.setVisibility(0);
            this.rightContainer.setVisibility(8);
            String str = "Watering Now\n";
            String str2 = str + TimeStringUtil.getStringForHoursAndMinutesFromSecondsRounded(activeZoneDuration, true);
            CharSequence spannableString = new SpannableString(str2);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.rachio_black)), str.length(), str2.length(), 17);
            spannableString.setSpan(new RelativeSizeSpan(1.3f), str.length(), str2.length(), 17);
            this.wateringNowText.setText(spannableString);
            this.wateringNowContainer.setVisibility(0);
            this.iconLeft.setImageResource(R.drawable.ic_badge_watering_blue);
            this.descriptionLeft.setText(null);
        } else {
            this.wateringNowContainer.setVisibility(8);
            if (zone.enabled) {
                Date date;
                boolean hasWatered = zone.lastWateredDate != null;
                boolean scheduled = zone.nextWaterDate != null;
                boolean willEventuallyWater = zone.isInFlexSchedule;
                this.leftContainer.setVisibility(hasWatered ? 0 : 8);
                this.rightContainer.setVisibility(0);
                this.divider.setVisibility(hasWatered ? 0 : 8);
                if (hasWatered) {
                    date = zone.lastWateredDate;
                    if (date != null) {
                        setWaterDateText(this.descriptionLeft, "Watered\n\n", R.style.Rachio.TextAppearance.MyYard.Light, new SimpleDateFormat("MMMM").format(date) + "\n", R.style.Rachio.TextAppearance.MyYard.Month.Light, new SimpleDateFormat("d").format(date), R.style.Rachio.TextAppearance.MyYard.Day.Light);
                        this.iconLeft.setImageResource(R.drawable.ic_badge_check_green);
                    }
                }
                if (scheduled) {
                    date = zone.nextWaterDate;
                    if (date != null) {
                        setWaterDateText(this.descriptionRight, "Will Water\n\n", R.style.Rachio.TextAppearance.MyYard, new SimpleDateFormat("MMMM").format(date) + "\n", R.style.Rachio.TextAppearance.MyYard.Month, new SimpleDateFormat("d").format(date), R.style.Rachio.TextAppearance.MyYard.Day);
                        this.iconRight.setImageResource(R.drawable.ic_badge_calendar_gray);
                    }
                } else if (willEventuallyWater) {
                    Calendar instance = Calendar.getInstance();
                    instance.add(6, 13);
                    date = instance.getTime();
                    setWaterDateText(this.descriptionRight, "Will Water After\n\n", R.style.Rachio.TextAppearance.MyYard, new SimpleDateFormat("MMMM").format(date) + "\n", R.style.Rachio.TextAppearance.MyYard.Month, new SimpleDateFormat("d").format(date), R.style.Rachio.TextAppearance.MyYard.Day);
                    this.iconRight.setImageResource(R.drawable.ic_badge_calendar_gray);
                } else {
                    this.iconRight.setImageResource(R.drawable.ic_badge_exclamation_orange);
                    setZoneDescription(this.descriptionRight, "Not Scheduled");
                }
            } else {
                this.rightContainer.setVisibility(8);
                this.iconLeft.setImageResource(R.drawable.ic_badge_disabled_gray);
                setZoneDescription(this.descriptionLeft, "Disabled");
            }
        }
        this.zoneNameAndIcon.setText(zone.name);
        if (zone.scheduledWateringTypes != null) {
            this.zoneNameAndIcon.setCompoundDrawablesWithIntrinsicBounds(0, zone.scheduledWateringTypes.resourceId, 0, 0);
        }
    }

    private static void setZoneDescription(TextView descriptionTextView, String description) {
        descriptionTextView.setText(Html.fromHtml(description));
    }

    private void setWaterDateText(TextView textView, String description, int styleDescription, String month, int styleMonth, String day, int styleDay) {
        String message = description + month + day;
        SpannableString span = new SpannableString(message);
        span.setSpan(new TextAppearanceSpan(getContext(), styleDescription), 0, description.length(), 33);
        span.setSpan(new TextAppearanceSpan(getContext(), styleMonth), description.length(), message.length() - 2, 33);
        span.setSpan(new TextAppearanceSpan(getContext(), styleDay), message.length() - 2, message.length(), 33);
        textView.setText(span, BufferType.SPANNABLE);
    }
}
