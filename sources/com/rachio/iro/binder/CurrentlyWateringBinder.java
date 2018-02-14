package com.rachio.iro.binder;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.binder.viewholder.CardHeaderFooterViewHolder;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.schedule.ScheduleExecution;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.widget.MeterWidget;
import com.rachio.iro.utils.TimeStringUtil;

public class CurrentlyWateringBinder extends BaseModelViewBinder<CurrentlyWateringData> {

    public static final class CurrentlyWateringData {
        public boolean detailsShown;
        public Device device;
    }

    public interface CurrentlyWateringListener {
        void onToggleDetails();

        void onWateringStopClicked();
    }

    private static class SubCardHolder {
        final TextView descriptionText;
        final ImageView icon;
        final TextView timestampText;
        final MeterWidget wateringMeter;

        public SubCardHolder(View view) {
            this.icon = (ImageView) view.findViewById(R.id.watering_status_icon);
            this.timestampText = (TextView) view.findViewById(R.id.watering_timestamp_text);
            this.descriptionText = (TextView) view.findViewById(R.id.watering_description_text);
            this.wateringMeter = (MeterWidget) view.findViewById(R.id.watering_meter);
        }
    }

    public static class ViewHolder extends ModelObjectViewHolder {
        public final TextView descriptionText = ((TextView) findView(R.id.watering_description_text, false));
        public final LinearLayout details = ((LinearLayout) findView(R.id.details, false));
        public final TextView detailsText = ((TextView) findView(R.id.watering_details_text, false));
        public final CardHeaderFooterViewHolder headerFooterHolder;
        public final ImageView statusIcon = ((ImageView) findView(R.id.watering_status_icon, false));
        public final TextView stopText = ((TextView) findView(R.id.watering_stop_text, false));
        public final int textColourBlue;
        public final TextView timestampText = ((TextView) findView(R.id.watering_timestamp_text, false));
        public final MeterWidget wateringMeter = ((MeterWidget) findView(R.id.watering_meter, false));

        public ViewHolder(View v) {
            super(v);
            this.textColourBlue = v.getResources().getColor(R.color.rachio_blue);
            this.headerFooterHolder = new CardHeaderFooterViewHolder(v);
            this.detailsText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    BaseActivity activity = BaseModelViewBinder.findActivity(v.getContext());
                    if (activity instanceof CurrentlyWateringListener) {
                        ((CurrentlyWateringListener) activity).onToggleDetails();
                    }
                }
            });
            this.stopText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    BaseActivity activity = BaseModelViewBinder.findActivity(v.getContext());
                    if (activity instanceof CurrentlyWateringListener) {
                        ((CurrentlyWateringListener) activity).onWateringStopClicked();
                    }
                }
            });
        }
    }

    public final /* bridge */ /* synthetic */ void onBind(ModelObjectViewHolder modelObjectViewHolder, Object obj) {
        CurrentlyWateringData currentlyWateringData = (CurrentlyWateringData) obj;
        ViewHolder viewHolder = (ViewHolder) modelObjectViewHolder;
        ScheduleExecution scheduleExecution = currentlyWateringData.device.scheduleExecution;
        if (scheduleExecution != null && scheduleExecution.isRunning()) {
            viewHolder.timestampText.setText(TimeStringUtil.getDisplayDateTimeOfEvent(scheduleExecution.startDate));
            String str;
            switch (scheduleExecution.getType()) {
                case MANUAL:
                    str = "Manual Run";
                    CharSequence spannableString = new SpannableString(str + " started with a duration of " + (scheduleExecution.estimatedDuration / 60) + " minutes ");
                    spannableString.setSpan(new ForegroundColorSpan(viewHolder.textColourBlue), 0, str.length(), 17);
                    viewHolder.descriptionText.setText(spannableString);
                    break;
                case FLEX:
                case AUTOMATIC:
                    str = "Schedule rule";
                    ScheduleRule scheduleRuleById = currentlyWateringData.device.getScheduleRuleById(scheduleExecution.scheduleRuleId);
                    if (scheduleRuleById != null) {
                        str = scheduleRuleById.getNameOrExternalName();
                    }
                    viewHolder.descriptionText.setText(str + " started with a duration of " + (scheduleExecution.estimatedDuration / 60) + " minutes ");
                    break;
                default:
                    throw new IllegalStateException("unhandled type " + scheduleExecution.getType());
            }
            viewHolder.wateringMeter.setMaxValue((float) scheduleExecution.estimatedDuration);
            viewHolder.wateringMeter.setCurrentValue((float) (((double) scheduleExecution.estimatedDuration) - scheduleExecution.timeRemaining()));
            subCards(viewHolder, currentlyWateringData, scheduleExecution);
        }
    }

    public final int getLayoutId() {
        return R.layout.view_card_events_currently_watering;
    }

    public final ModelObjectViewHolder createViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected final void setContentShown(ModelObjectViewHolder holder, boolean isShown) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (TextUtils.isEmpty(viewHolder.headerFooterHolder.headerTextLeft.getText())) {
            int color = viewHolder.itemView.getResources().getColor(R.color.rachio_blue);
            viewHolder.headerFooterHolder.headerTextLeft.setText("Currently Watering");
            viewHolder.headerFooterHolder.headerTextRight.setText("");
            viewHolder.headerFooterHolder.headerBackground.setColor(color);
        }
        viewHolder.headerFooterHolder.headerProgressBar.setVisibility(isShown ? 8 : 0);
    }

    private static void subCards(ViewHolder viewHolder, CurrentlyWateringData data, ScheduleExecution scheduleExecution) {
        viewHolder.details.setVisibility(data.detailsShown ? 0 : 8);
        if (data.detailsShown) {
            int i;
            int currentNumberOfCards = viewHolder.details.getChildCount();
            int numberOfEvents = scheduleExecution.zoneEvents != null ? scheduleExecution.zoneEvents.size() : 0;
            if (currentNumberOfCards < numberOfEvents) {
                int needed = numberOfEvents - currentNumberOfCards;
                for (i = 0; i < needed; i++) {
                    View subCard = LayoutInflater.from(viewHolder.descriptionText.getContext()).inflate(R.layout.widget_watering_history_item_subcard, null);
                    subCard.setTag(new SubCardHolder(subCard));
                    viewHolder.details.addView(subCard);
                }
            } else if (numberOfEvents > currentNumberOfCards) {
                for (i = currentNumberOfCards; i > numberOfEvents; i--) {
                    viewHolder.details.removeViewAt(i - 1);
                }
            }
            for (i = 0; i < numberOfEvents; i++) {
                Event e = (Event) scheduleExecution.zoneEvents.get(i);
                SubCardHolder holder = (SubCardHolder) viewHolder.details.getChildAt(i).getTag();
                holder.timestampText.setText(TimeStringUtil.getDisplayDateTimeOfEvent(e.eventDate));
                String zoneName = e.getStringValue("zoneName");
                if (zoneName == null || e.summary == null) {
                    holder.descriptionText.setText(null);
                } else {
                    int zonePos = e.summary.indexOf(zoneName);
                    SpannableString text = new SpannableString(e.summary);
                    text.setSpan(new ForegroundColorSpan(viewHolder.textColourBlue), zonePos, zoneName.length() + zonePos, 17);
                    holder.descriptionText.setText(text);
                }
                int duration = e.getIntValue("duration") * 60;
                int elapsed = Math.min((int) ((System.currentTimeMillis() - e.getLongValue("zoneStartDate")) / 1000), duration);
                holder.wateringMeter.setMaxValue((float) duration);
                holder.wateringMeter.setCurrentValue((float) elapsed);
            }
        }
    }
}
