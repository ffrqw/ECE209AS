package com.rachio.iro.ui.newschedulerulepath.fragments.editor;

import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseZoneSelectFragment;

public class ZoneSelectFragment extends BaseZoneSelectFragment {
    public final int getLayout() {
        return R.layout.fragment_schedulerulewizard_zoneselect;
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        BaseScheduleRuleFragment.updateTotalDuration(state);
    }
}
