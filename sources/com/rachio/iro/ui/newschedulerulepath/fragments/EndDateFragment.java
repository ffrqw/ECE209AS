package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EndDateFragment extends BaseDateFragment {
    private RadioGroup neverSelect;
    private ScrollView radioContainer;
    private Date startDate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.radioContainer = (ScrollView) view.findViewById(R.id.enddate_radiocontainer);
        this.neverSelect = (RadioGroup) view.findViewById(R.id.enddate_neverselect);
        this.datePicker.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                EndDateFragment.this.radioContainer.fullScroll(130);
            }
        });
        this.neverSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.never:
                        EndDateFragment.this.datePicker.setVisibility(8);
                        return;
                    case R.id.selectdate:
                        EndDateFragment.this.datePicker.setVisibility(0);
                        return;
                    default:
                        return;
                }
            }
        });
        return view;
    }

    public final boolean validate() {
        return this.neverSelect.getCheckedRadioButtonId() == R.id.never || getDateFromDatePicker().getTime() >= this.startDate.getTime();
    }

    public final void updateState(ScheduleRule state) {
        super.updateState(state);
        this.startDate = state.getStartDate();
        Date endDate = state.endDate;
        if (state.endDate == null) {
            Calendar defaultEndDate = Calendar.getInstance();
            defaultEndDate.add(2, 3);
            endDate = defaultEndDate.getTime();
        } else {
            this.neverSelect.check(R.id.selectdate);
        }
        setDatePickerFromDate(endDate);
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        switch (this.neverSelect.getCheckedRadioButtonId()) {
            case R.id.never:
                state.endDate = null;
                return;
            case R.id.selectdate:
                state.endDate = ScheduleRule.clampEndDate(getDateFromDatePicker(), TimeZone.getDefault());
                return;
            default:
                return;
        }
    }

    protected final int getLayout() {
        return R.layout.fragment_schedulerulewizard_enddate;
    }
}
