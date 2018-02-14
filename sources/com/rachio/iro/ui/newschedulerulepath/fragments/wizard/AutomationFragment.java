package com.rachio.iro.ui.newschedulerulepath.fragments.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.ExpandableController;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;

public class AutomationFragment extends BaseScheduleRuleFragment {
    private ExpandableController expandableController = new ExpandableController();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedulerulewizard_automation, container, false);
        wireUpAutomation(view);
        wireUpSmartWatering(view);
        this.expandableController.add(this.smartCycle);
        return view;
    }

    public final void updateState(ScheduleRule state) {
        super.updateState(state);
        if (state != null) {
            updateAutomation(state);
            updateSmartWatering(state);
        }
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        commitAutomation(state);
    }
}
