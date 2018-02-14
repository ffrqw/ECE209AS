package com.rachio.iro.ui.newschedulerulepath.fragments.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;

public class RuleNameFragment extends BaseScheduleRuleFragment {
    private EditText name;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newschedulerule_name, container, false);
        this.name = (EditText) view.findViewById(R.id.name);
        return view;
    }

    public final void updateState(ScheduleRule entity) {
        super.updateState(entity);
        this.name.setText(entity.name);
        this.name.setHint(entity.externalName);
    }

    public final void commitState(ScheduleRule entity) {
        super.commitState(entity);
        entity.name = this.name.getEditableText().toString();
    }
}
