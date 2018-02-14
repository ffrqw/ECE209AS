package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;

public abstract class BaseDelayFragment extends BaseSelectListFragment {
    protected SwitchCompat enabled;

    protected abstract String getDescription();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.enabled = (SwitchCompat) view.findViewById(R.id.enabled);
        ((TextView) view.findViewById(R.id.delaydescription)).setText(getDescription());
        this.enabled.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BaseDelayFragment.this.list.setVisibility(isChecked ? 0 : 4);
            }
        });
        return view;
    }

    final int getChoiceMode() {
        return 1;
    }

    protected final int getLayout() {
        return R.layout.fragment_schedulerulewizard_delay;
    }

    public void updateState(ScheduleRule entity) {
        super.updateState(entity);
        this.list.setVisibility(this.enabled.isChecked() ? 0 : 4);
    }

    protected final boolean moveToNextOnChoice() {
        return false;
    }
}
