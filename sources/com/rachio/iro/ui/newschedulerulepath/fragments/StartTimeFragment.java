package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.Operator;
import com.rachio.iro.ui.FragmentNavigationController;
import com.rachio.iro.ui.FragmentNavigationController.Path;
import com.rachio.iro.utils.ScheduleStringUtils;

public class StartTimeFragment extends BaseScheduleRuleFragment implements OnTimeChangedListener {
    public static Path PATH = FragmentNavigationController.createSingleScreenPath("select_start_time", StartTimeFragment.class, R.string.chooseatime);
    private RadioGroup beforeOrAfter;
    private TimePicker timePicker;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedulerulewizard_wateringtime, container, false);
        this.beforeOrAfter = (RadioGroup) view.findViewById(R.id.beforeorafter);
        final TextView beforeText = (TextView) view.findViewById(R.id.before_text);
        final TextView afterText = (TextView) view.findViewById(R.id.after_text);
        this.timePicker = (TimePicker) view.findViewById(R.id.timepicker);
        this.beforeOrAfter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int i;
                int i2 = 0;
                TextView textView = beforeText;
                if (checkedId == R.id.before) {
                    i = 0;
                } else {
                    i = 4;
                }
                textView.setVisibility(i);
                TextView textView2 = afterText;
                if (checkedId != R.id.after) {
                    i2 = 4;
                }
                textView2.setVisibility(i2);
            }
        });
        this.timePicker.setOnTimeChangedListener(this);
        return view;
    }

    public final void updateState(ScheduleRule state) {
        super.updateState(state);
        switch (state.operator) {
            case BEFORE:
                this.beforeOrAfter.check(R.id.before);
                break;
            case AFTER:
            case START_TIME:
                this.beforeOrAfter.check(R.id.after);
                break;
        }
        this.timePicker.setCurrentHour(Integer.valueOf(state.startHour));
        this.timePicker.setCurrentMinute(Integer.valueOf(state.startMinute));
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        switch (this.beforeOrAfter.getCheckedRadioButtonId()) {
            case R.id.before:
                state.operator = Operator.BEFORE;
                break;
            case R.id.after:
                state.operator = Operator.START_TIME;
                break;
        }
        state.startHour = this.timePicker.getCurrentHour().intValue();
        state.startMinute = this.timePicker.getCurrentMinute().intValue();
        ScheduleStringUtils.buildSummary(getContext(), state);
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
    }
}
