package com.rachio.iro.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ZoneInfo;
import com.rachio.iro.utils.DateFormats;
import com.rachio.iro.utils.OpacityUtil;
import com.rachio.iro.utils.TimeStringUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ScheduleRowItem extends LinearLayout {
    private TextView description = ((TextView) findViewById(R.id.schedule_list_row_description));
    private ImageView icon = ((ImageView) findViewById(R.id.schedule_list_row_icon));
    private TextView name = ((TextView) findViewById(R.id.schedule_list_row_name));
    private LinearLayout zones = ((LinearLayout) findViewById(R.id.schedule_list_row_zones));

    public ScheduleRowItem(Context context) {
        super(context);
        inflate(context, R.layout.schedule_list_row, this);
    }

    public final void setScheduleRule(ScheduleRule rule, boolean detailsExpanded, int cycles, long totalDuration, long totalDurationNoCycle, Map<String, Zone> zonesMap, List<? extends ZoneInfo> zones) {
        this.icon.setImageLevel(rule.isFlex() ? 1 : 0);
        this.name.setText(rule.getNameOrExternalName());
        this.description.setText(rule.summary);
        Calendar acculatedTime = Calendar.getInstance();
        acculatedTime.setTime(rule.getStartTime());
        this.zones.removeAllViews();
        if (detailsExpanded) {
            int c = 0;
            while (c < cycles) {
                if (zones != null) {
                    View row;
                    Date startTime;
                    for (ZoneInfo z : zones) {
                        row = LayoutInflater.from(getContext()).inflate(R.layout.schedule_list_row_zone, this.zones, false);
                        TextView name = (TextView) row.findViewById(R.id.schedule_list_row_zone_name);
                        int zoneDuration = z.duration.intValue() / cycles;
                        startTime = acculatedTime.getTime();
                        acculatedTime.add(13, zoneDuration);
                        setupDuration(row, startTime, acculatedTime.getTime(), zoneDuration);
                        name.setText(((Zone) zonesMap.get(z.zoneId)).name);
                        this.zones.addView(row);
                    }
                    if (!(totalDuration == totalDurationNoCycle || c == cycles - 1)) {
                        row = LayoutInflater.from(getContext()).inflate(R.layout.schedule_list_row_cycle, this.zones, false);
                        this.zones.addView(row);
                        long cycleDuration = (totalDuration - totalDurationNoCycle) / ((long) (cycles - 1));
                        startTime = acculatedTime.getTime();
                        acculatedTime.add(13, (int) cycleDuration);
                        setupDuration(row, startTime, acculatedTime.getTime(), (int) cycleDuration);
                    }
                }
                c++;
            }
        }
        this.zones.setVisibility(detailsExpanded ? 0 : 8);
        OpacityUtil.makeViewLookEnabledOrDisabled(this, rule.enabled);
    }

    private static void setupDuration(View row, Date startTime, Date endTime, int zoneDuration) {
        TextView end = (TextView) row.findViewById(R.id.schedule_list_row_zone_end);
        ((TextView) row.findViewById(R.id.schedule_list_row_zone_start)).setText(DateFormats.time.format(startTime));
        end.setText(DateFormats.time.format(endTime));
        ((TextView) row.findViewById(R.id.schedule_list_row_zone_duration)).setText(TimeStringUtil.getStringForHoursAndMinutesFromSecondsRounded(zoneDuration, true));
    }
}
