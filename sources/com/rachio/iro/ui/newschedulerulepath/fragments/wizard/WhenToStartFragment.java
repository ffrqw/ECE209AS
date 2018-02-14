package com.rachio.iro.ui.newschedulerulepath.fragments.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;

public class WhenToStartFragment extends BaseScheduleRuleFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedulerulewizard_whentostartfragment, container, false);
        wireUpStartEndDate(view);
        return view;
    }

    public final void updateState(ScheduleRule state) {
        updateStartEndDates(state);
        super.updateState(state);
    }

    public final boolean validate() {
        return this.endDateIsValid;
    }
}
