package com.rachio.iro.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchCalendarCommand.ScheduleCalendarMeta;
import com.rachio.iro.binder.viewholder.CardHeaderFooterViewHolder;
import com.rachio.iro.model.WateringScheduleType;
import com.rachio.iro.model.schedule.ScheduleCalendar.OutOfRangeException;
import com.rachio.iro.ui.view.WaterScheduleDayStampView;
import com.rachio.iro.utils.CalendarUtil;
import com.rachio.iro.utils.DateFormats;
import com.rachio.iro.utils.UnitUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WateringScheduleBinder implements ModelViewBinder<ScheduleCalendarMeta> {
    private OnClickListener stampClickListener = new OnClickListener() {
        public void onClick(View v) {
            Date day = (Date) v.getTag();
            if (v.getContext() instanceof WateringScheduleBinderListener) {
                ((WateringScheduleBinderListener) v.getContext()).onStampClicked(day);
            }
        }
    };

    public static class ViewHolder extends ModelObjectViewHolder {
        LinearLayout dayContainer = ((LinearLayout) findView(R.id.water_schedule_day_container, false));
        public CardHeaderFooterViewHolder headerFooterHolder;
        TextView monthYearText = ((TextView) findView(R.id.water_schedule_month_text, false));
        TextView nextWateringTimeText = ((TextView) findView(R.id.next_watering_time_text, false));

        public ViewHolder(View v) {
            super(v);
            this.headerFooterHolder = new CardHeaderFooterViewHolder(v);
            this.headerFooterHolder.footerContainer.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (v.getContext() instanceof WateringScheduleBinderListener) {
                        ((WateringScheduleBinderListener) v.getContext()).onEditWateringTimes();
                    }
                }
            });
        }
    }

    public interface WateringScheduleBinderListener {
        void onEditWateringTimes();

        void onStampClicked(Date date);
    }

    public final /* bridge */ /* synthetic */ void bind(ModelObjectViewHolder modelObjectViewHolder, Object obj) {
        ScheduleCalendarMeta scheduleCalendarMeta = (ScheduleCalendarMeta) obj;
        ViewHolder viewHolder = (ViewHolder) modelObjectViewHolder;
        viewHolder.headerFooterHolder.headerBackground.setColor(viewHolder.itemView.getResources().getColor(R.color.rachio_blue));
        viewHolder.headerFooterHolder.headerTextLeft.setText("Watering Schedule");
        viewHolder.headerFooterHolder.headerTextRight.setText(null);
        viewHolder.headerFooterHolder.footerText.setText("Edit Watering Schedule");
        if (scheduleCalendarMeta != null) {
            int abs;
            int i;
            WaterScheduleDayStampView waterScheduleDayStampView;
            ViewGroup viewGroup = viewHolder.dayContainer;
            int childCount = viewGroup.getChildCount();
            if (14 != childCount) {
                int i2 = 14 - childCount;
                abs = i2 / Math.abs(i2);
                for (i = 0; i != i2; i += abs) {
                    if (abs > 0) {
                        waterScheduleDayStampView = (WaterScheduleDayStampView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_stamp_watering_schedule_day, viewGroup, false);
                        ((LayoutParams) waterScheduleDayStampView.getLayoutParams()).leftMargin = UnitUtils.toDp(viewGroup.getContext(), 4);
                        waterScheduleDayStampView.setOnClickListener(this.stampClickListener);
                        viewGroup.addView(waterScheduleDayStampView);
                    } else {
                        viewGroup.removeView(viewGroup.getChildAt(viewGroup.getChildCount() - 1));
                    }
                }
            }
            Calendar instance = Calendar.getInstance(scheduleCalendarMeta.deviceTimeZone);
            CalendarUtil.setToStartOfDay(instance);
            String displayName = instance.getDisplayName(2, 2, Locale.getDefault());
            viewHolder.monthYearText.setText(String.format("%s %d", new Object[]{displayName, Integer.valueOf(instance.get(1))}));
            for (i = 0; i < 14; i++) {
                Date time = instance.getTime();
                waterScheduleDayStampView = (WaterScheduleDayStampView) viewHolder.dayContainer.getChildAt(i);
                abs = instance.get(5);
                String toUpperCase = instance.getDisplayName(7, 1, Locale.getDefault()).toUpperCase();
                WateringScheduleType wateringScheduleType = WateringScheduleType.NONE;
                try {
                    wateringScheduleType = scheduleCalendarMeta.calendar.getTypeForDate(scheduleCalendarMeta.deviceTimeZone, time, scheduleCalendarMeta.rainDelayStart, scheduleCalendarMeta.rainDelayEnd);
                } catch (OutOfRangeException e) {
                }
                waterScheduleDayStampView.setDay(String.format("%d", new Object[]{Integer.valueOf(abs)}), toUpperCase, wateringScheduleType);
                waterScheduleDayStampView.setTag(time);
                if (i == 0) {
                    waterScheduleDayStampView.setToday(true);
                }
                instance.add(6, 1);
            }
        }
        if (scheduleCalendarMeta == null) {
            return;
        }
        if (scheduleCalendarMeta.rainDelayEnd == null || !scheduleCalendarMeta.rainDelayEnd.after(new Date())) {
            Calendar instance2 = Calendar.getInstance(scheduleCalendarMeta.deviceTimeZone);
            CalendarUtil.setToEndOfDay(instance2);
            instance2.add(6, 13);
            Date nextWateringTime = scheduleCalendarMeta != null ? scheduleCalendarMeta.calendar.nextWateringTime(scheduleCalendarMeta.deviceTimeZone, instance2.getTime()) : null;
            if (nextWateringTime != null) {
                viewHolder.nextWateringTimeText.setVisibility(0);
                viewHolder.nextWateringTimeText.setText("Next scheduled watering time is " + DateFormats.formatDayMonthAtTimeWithTodayYesterday(nextWateringTime));
                return;
            }
            viewHolder.nextWateringTimeText.setText("Nothing scheduled for the next two weeks");
            return;
        }
        viewHolder.nextWateringTimeText.setText("Rain Delay until " + DateFormats.dayMonthAndtime.format(scheduleCalendarMeta.rainDelayEnd));
    }

    public final int getLayoutId() {
        return R.layout.view_card_device_watering_schedule;
    }

    public final ModelObjectViewHolder createViewHolder(View v) {
        return new ViewHolder(v);
    }
}
