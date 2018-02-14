package com.rachio.iro.ui.newschedulerulepath.fragments;

import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.utils.CalendarUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class StartDateFragment extends BaseDateFragment {
    private Date endDate;

    public final void updateState(ScheduleRule state) {
        super.updateState(state);
        setDatePickerFromDate(state.absoluteStartDate);
        this.endDate = state.endDate;
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        Date newDate = ScheduleRule.clampStartDate(getDateFromDatePicker(), TimeZone.getDefault());
        state.absoluteStartDate = newDate;
        state.setStartDate(newDate);
    }

    public final boolean validate() {
        boolean todayOrAfter;
        Calendar today = Calendar.getInstance();
        CalendarUtil.setToStartOfDay(today);
        Date newDate = getDateFromDatePicker();
        if (newDate.getTime() >= today.getTimeInMillis()) {
            todayOrAfter = true;
        } else {
            todayOrAfter = false;
        }
        boolean beforeEndDate;
        if (this.endDate == null || newDate.getTime() <= this.endDate.getTime()) {
            beforeEndDate = true;
        } else {
            beforeEndDate = false;
        }
        if (todayOrAfter && beforeEndDate) {
            return true;
        }
        return false;
    }
}
