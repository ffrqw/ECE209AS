package com.rachio.iro.ui.newschedulerulepath.fragments;

import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;

public class WeekdaysSelectFragment extends BaseWeekdaysSelectFragment {
    public final void updateState(ScheduleRule state) {
        super.updateState(state);
        updateStateFromJobTypes(state);
        onStateChanged();
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        commitStateToJobTypes(state);
    }

    protected final int getLayout() {
        return R.layout.fragment_schedulerulewizard_select_days;
    }
}
