package com.rachio.iro.ui.newschedulerulepath.fragments.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.PreviewScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;

public class DurationFragment extends BaseScheduleRuleFragment {
    private EditText name;
    private boolean previewed;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedulerulewizard_duration, container, false);
        wireUpDuration(view);
        this.name = (EditText) view.findViewById(R.id.name);
        return view;
    }

    public final void commitState(ScheduleRule entity) {
        super.commitState(entity);
        entity.name = this.name.getEditableText().toString();
    }

    public final void updateState(ScheduleRule state) {
        this.name.setText(state.getNameOrExternalName());
        updateDurationState(state);
        this.previewed = ((PreviewScheduleRule) state).previewed;
        super.updateState(state);
    }

    public final boolean validate() {
        return this.previewed;
    }
}
