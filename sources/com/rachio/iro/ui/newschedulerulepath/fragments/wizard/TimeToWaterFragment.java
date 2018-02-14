package com.rachio.iro.ui.newschedulerulepath.fragments.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;

public class TimeToWaterFragment extends BaseScheduleRuleFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedulerulewizard_timetowater, container, false);
        wireUpStartTime(view);
        return view;
    }

    public final void updateState(ScheduleRule state) {
        updateStartTime(state);
        super.updateState(state);
    }
}
