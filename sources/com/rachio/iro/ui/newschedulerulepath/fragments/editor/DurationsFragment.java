package com.rachio.iro.ui.newschedulerulepath.fragments.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;

public class DurationsFragment extends BaseScheduleRuleFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedulerule_durations, container, false);
        wireUpDuration(view);
        return view;
    }

    public final void updateState(ScheduleRule entity) {
        updateDurationState(entity);
        super.updateState(entity);
    }

    public final void commitState(ScheduleRule entity) {
        super.commitState(entity);
        BaseScheduleRuleFragment.updateTotalDuration(entity);
    }
}
