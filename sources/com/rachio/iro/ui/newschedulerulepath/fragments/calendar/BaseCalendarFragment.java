package com.rachio.iro.ui.newschedulerulepath.fragments.calendar;

import com.rachio.iro.async.command.FetchCalendarCommand.ScheduleCalendarMeta;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.newschedulerulepath.activity.ViewScheduleActivity;
import java.util.List;
import java.util.Map;

public class BaseCalendarFragment extends BaseFragment {
    public void setCalendar(ScheduleCalendarMeta calendar) {
    }

    public void setRules(List<ScheduleRule> list, Map<String, Zone> map) {
    }

    protected final ViewScheduleActivity getViewScheduleActivity() {
        return (ViewScheduleActivity) ((BaseActivity) getActivity());
    }
}
