package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.FrequencyOperator;
import com.rachio.iro.model.schedule.ScheduleRule.ScheduleJobType;
import com.rachio.iro.model.schedule.ZoneInfo;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.fragment.FragmentThatUpdatesState;
import com.rachio.iro.ui.newschedulerulepath.activity.BaseScheduleRuleActivity;
import com.rachio.iro.ui.newschedulerulepath.adapters.ZoneDurationAdapter;
import com.rachio.iro.ui.view.RepeatClickableImageView;
import com.rachio.iro.ui.view.settings.RowWithCurrentValueAndChevronView;
import com.rachio.iro.ui.view.settings.RowWithCurrentValueThatExpands;
import com.rachio.iro.utils.ScheduleStringUtils;
import com.rachio.iro.utils.TimeStringUtil;
import java.util.Arrays;
import java.util.Iterator;

public class BaseScheduleRuleFragment extends BaseFragment implements FragmentThatUpdatesState<ScheduleRule> {
    protected boolean endDateIsValid;
    protected RowWithCurrentValueAndChevronView endOn;
    protected Spinner frequencyModifier;
    protected RowWithCurrentValueAndChevronView intervalRepeat;
    public ListView list;
    private Listener listener;
    protected RowWithCurrentValueThatExpands smartCycle;
    protected SwitchCompat smartCycleSwitch;
    protected RowWithCurrentValueAndChevronView startOn;
    private TextView totalDuration;
    protected RowWithCurrentValueAndChevronView weatherIntelligence;
    protected RowWithCurrentValueAndChevronView weekdays;
    protected RowWithCurrentValueAndChevronView whenToStart;

    public class ActionOnClickListener implements OnClickListener {
        private int action;

        public ActionOnClickListener(int action) {
            this.action = action;
        }

        public void onClick(View v) {
            ((BaseScheduleRuleActivity) BaseScheduleRuleFragment.this.getActivity()).onAction(this.action);
        }
    }

    public interface Listener {
        void onComing(BaseScheduleRuleFragment baseScheduleRuleFragment);

        void onGoing$5cc6ebe();

        void onStateChanged$5cc6ebe();
    }

    protected final void wireUpFrequencyControls(View view) {
        this.intervalRepeat = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.schedulerulewizard_interval_repeat);
        this.weekdays = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.schedulerulewizard_weekdays);
        this.intervalRepeat.setOnClickListener(new ActionOnClickListener(101));
        this.weekdays.setOnClickListener(new ActionOnClickListener(103));
    }

    protected final void wireUpAutomation(View view) {
        this.smartCycle = (RowWithCurrentValueThatExpands) view.findViewById(R.id.schedulerulewizard_automation_smartcycle);
        this.smartCycleSwitch = (SwitchCompat) view.findViewById(R.id.schedulerulewizard_automation_smartcycle_switch);
        this.smartCycleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BaseScheduleRuleFragment.this.updateEnabledDisabled(BaseScheduleRuleFragment.this.smartCycle, isChecked);
            }
        });
    }

    public void updateState(ScheduleRule entity) {
    }

    public void commitState(ScheduleRule entity) {
    }

    public boolean validate() {
        return true;
    }

    public final void onStateChanged() {
        if (this.listener != null) {
            this.listener.onStateChanged$5cc6ebe();
        }
    }

    public final void setListener(Listener listener) {
        this.listener = listener;
    }

    protected final void updateStartEndDates(ScheduleRule state) {
        if (state.absoluteStartDate != null) {
            this.startOn.setValue(TimeStringUtil.getDisplayDate$47d5fde(state.absoluteStartDate));
        } else {
            this.startOn.setValue(TimeStringUtil.getDisplayDate$47d5fde(state.getStartDate()));
        }
        if (state.endDate != null) {
            this.endDateIsValid = state.endDate.after(state.getStartDate());
            this.endOn.setValue(TimeStringUtil.getDisplayDate$47d5fde(state.endDate));
            return;
        }
        this.endDateIsValid = true;
        this.endOn.setValue("Never");
    }

    protected final void wireUpStartEndDate(View view) {
        this.startOn = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.schedulerulewizard_startdate);
        this.endOn = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.schedulerulewizard_enddate);
        this.startOn.setOnClickListener(new ActionOnClickListener(106));
        this.endOn.setOnClickListener(new ActionOnClickListener(107));
    }

    protected final void updateEnabledDisabled(RowWithCurrentValueAndChevronView view, boolean checked) {
        view.setValue(getString(checked ? R.string.on : R.string.off));
    }

    protected final void updateSmartWatering(ScheduleRule state) {
        int i;
        boolean z = false;
        RowWithCurrentValueAndChevronView rowWithCurrentValueAndChevronView = this.weatherIntelligence;
        if (state.isFlex()) {
            i = 8;
        } else {
            i = 0;
        }
        rowWithCurrentValueAndChevronView.setVisibility(i);
        if ((state.getFrequencyOperator() != FrequencyOperator.ASNEEDED && state.waterBudget) || state.etSkip || state.precipDelay.enabled || state.freezeDelay.enabled) {
            z = true;
        }
        updateEnabledDisabled(this.weatherIntelligence, z);
    }

    protected final void wireUpSmartWatering(View view) {
        this.weatherIntelligence = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.schedulerulewizard_automation_weatherintelligence);
        this.weatherIntelligence.setOnClickListener(new ActionOnClickListener(116));
    }

    protected final void updateAutomation(ScheduleRule state) {
        this.smartCycleSwitch.setChecked(state.cycleSoak);
        updateEnabledDisabled(this.smartCycle, state.cycleSoak);
    }

    protected final void commitAutomation(ScheduleRule state) {
        state.cycleSoak = this.smartCycleSwitch.isChecked();
    }

    protected final void updateStartTime(ScheduleRule state) {
        String[] modifierStrings = getResources().getStringArray(R.array.schedule_afterbefore);
        String op = null;
        if (state.operator != null) {
            op = modifierStrings[state.operator.ordinal()];
        }
        this.whenToStart.setText(op);
        this.whenToStart.setValue(TimeStringUtil.getTimeOfDay(state.startHour, state.startMinute));
    }

    protected final void wireUpStartTime(View view) {
        this.whenToStart = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.schedulerulewizard_starttime);
        this.whenToStart.setOnClickListener(new ActionOnClickListener(105));
    }

    public static void updateWeekdays(RowWithCurrentValueAndChevronView view, ScheduleRule state) {
        view.setValue(ScheduleStringUtils.dayOfWeekJobsToString(state.scheduleJobTypes));
    }

    public final void updateInterval(RowWithCurrentValueAndChevronView view, ScheduleRule state) {
        ScheduleJobType jobType = null;
        if (state.scheduleJobTypes != null && state.scheduleJobTypes.length == 1 && state.scheduleJobTypes[0] != null && Arrays.binarySearch(ScheduleJobType.intervals, state.scheduleJobTypes[0]) >= 0) {
            jobType = state.scheduleJobTypes[0];
        }
        if (jobType != null) {
            view.setValue(jobType.toReadableString(getContext()));
        } else {
            view.setValue(null);
        }
    }

    protected final void updateFrequencyControls(ScheduleRule state) {
        updateInterval(this.intervalRepeat, state);
        updateWeekdays(this.weekdays, state);
    }

    private void updateTotalDuration(int totalZoneDuration) {
        this.totalDuration.setText(TimeStringUtil.getStringForNumberOfHoursMinutesAndSecondsCompact(totalZoneDuration - (totalZoneDuration % 60)));
        this.list.invalidateViews();
    }

    protected final void wireUpDuration(View view) {
        this.totalDuration = (TextView) view.findViewById(R.id.duration);
        RepeatClickableImageView minus = (RepeatClickableImageView) view.findViewById(R.id.decrease);
        RepeatClickableImageView plus = (RepeatClickableImageView) view.findViewById(R.id.increase);
        this.list = (ListView) view.findViewById(R.id.schedulerulewizard_zonedurations);
        minus.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((ZoneDurationAdapter) BaseScheduleRuleFragment.this.list.getAdapter()).decrementAll();
            }
        });
        plus.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((ZoneDurationAdapter) BaseScheduleRuleFragment.this.list.getAdapter()).incrementAll();
            }
        });
    }

    protected final void updateDurationState(ScheduleRule state) {
        ZoneDurationAdapter adapter = new ZoneDurationAdapter(getContext(), state.isFlex(), state.zones, state.device.getZonesMap());
        adapter.setListener(new com.rachio.iro.ui.newschedulerulepath.adapters.ZoneDurationAdapter.Listener() {
            public final void onTotalDurationChanged(int totalDuration) {
                BaseScheduleRuleFragment.this.updateTotalDuration(totalDuration);
            }
        });
        this.list.setAdapter(adapter);
        updateTotalDuration(adapter.calculateTotalDuration());
    }

    public static void updateTotalDuration(ScheduleRule state) {
        state.totalDuration = 0;
        Iterator it = state.zones.iterator();
        while (it.hasNext()) {
            state.totalDuration += ((ZoneInfo) it.next()).getDuration(state.isFlex());
        }
        state.totalDurationNoCycle = state.totalDuration;
    }

    public void onResume() {
        super.onResume();
        if (this.listener != null) {
            this.listener.onComing(this);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.listener != null) {
            this.listener.onGoing$5cc6ebe();
        }
    }

    public final void moveToNextStage() {
        ((BaseScheduleRuleActivity) getActivity()).onAction(0);
    }
}
