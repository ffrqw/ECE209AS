package com.rachio.iro.ui.newschedulerulepath.fragments.editor;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.FrequencyOperator;
import com.rachio.iro.ui.newschedulerulepath.ExpandableController;
import com.rachio.iro.ui.newschedulerulepath.activity.BaseScheduleRuleActivity;
import com.rachio.iro.ui.newschedulerulepath.activity.EditScheduleRuleActivity;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment.ActionOnClickListener;
import com.rachio.iro.ui.view.settings.RowWithCurrentValueAndChevronView;
import com.rachio.iro.ui.view.settings.RowWithCurrentValueThatExpands;
import com.rachio.iro.utils.TimeStringUtil;

public class RuleDetailsFragment extends BaseScheduleRuleFragment {
    private LinearLayout container;
    private RowWithCurrentValueAndChevronView durations;
    private SwitchCompat enabled;
    private ExpandableController expandableController = new ExpandableController();
    private RowWithCurrentValueAndChevronView name;
    private FrameLayout run;
    private RowWithCurrentValueThatExpands type;
    private TextView typeDescriptionAsNeeded;
    private TextView typeDescriptionFlex;
    private TextView typeDescriptionInterval;
    private TextView typeDescriptionSpecificDays;
    private RowWithCurrentValueAndChevronView willWater;
    private RowWithCurrentValueAndChevronView zones;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newschedulerule_editor, container, false);
        this.container = (LinearLayout) view.findViewById(R.id.container);
        this.run = (FrameLayout) view.findViewById(R.id.run);
        this.enabled = (SwitchCompat) view.findViewById(R.id.enabled);
        this.name = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.name);
        this.type = (RowWithCurrentValueThatExpands) view.findViewById(R.id.type);
        this.typeDescriptionAsNeeded = (TextView) view.findViewById(R.id.typedescription_asneeded);
        this.typeDescriptionInterval = (TextView) view.findViewById(R.id.typedescription_interval);
        this.typeDescriptionSpecificDays = (TextView) view.findViewById(R.id.typedescription_specific);
        this.typeDescriptionFlex = (TextView) view.findViewById(R.id.typedescription_flex);
        this.willWater = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.willwater);
        wireUpFrequencyControls(view);
        wireUpAutomation(view);
        wireUpSmartWatering(view);
        wireUpStartTime(view);
        wireUpStartEndDate(view);
        this.zones = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.zones);
        this.durations = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.durations);
        this.name.setOnClickListener(new ActionOnClickListener(115));
        this.zones.setOnClickListener(new ActionOnClickListener(100));
        this.durations.setOnClickListener(new ActionOnClickListener(114));
        this.willWater.setOnClickListener(new ActionOnClickListener(117));
        this.expandableController.add(this.smartCycle);
        this.expandableController.add(this.type);
        Button delete = (Button) view.findViewById(R.id.delete);
        this.run.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((EditScheduleRuleActivity) ((BaseScheduleRuleActivity) RuleDetailsFragment.this.getActivity())).runRule();
            }
        });
        delete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((EditScheduleRuleActivity) ((BaseScheduleRuleActivity) RuleDetailsFragment.this.getActivity())).deleteRule();
            }
        });
        return view;
    }

    public final void updateState(ScheduleRule state) {
        int i;
        boolean allowZoneChanges;
        super.updateState(state);
        FrameLayout frameLayout = this.run;
        if (state.isFlex()) {
            i = 8;
        } else {
            i = 0;
        }
        frameLayout.setVisibility(i);
        this.enabled.setChecked(state.enabled);
        this.name.setValue(state.getNameOrExternalName());
        this.durations.setValue(TimeStringUtil.getStringForNumberOfHoursMinutesAndSecondsCompact(state.totalDurationNoCycle - (state.totalDurationNoCycle % 60)));
        if (state.isFlex() || state.getFrequencyOperator() == FrequencyOperator.INTERVAL || state.getFrequencyOperator() == FrequencyOperator.WEEKDAY) {
            allowZoneChanges = true;
        } else {
            allowZoneChanges = false;
        }
        RowWithCurrentValueAndChevronView rowWithCurrentValueAndChevronView = this.zones;
        if (allowZoneChanges) {
            i = 0;
        } else {
            i = 8;
        }
        rowWithCurrentValueAndChevronView.setVisibility(i);
        this.zones.setValue(state.zones.size() + " Zones");
        FrequencyOperator frequencyOperator = state.getFrequencyOperator();
        if (state.isFlex()) {
            this.type.setValue(getString(R.string.scheduletype_flexdaily));
            FrequencyOperator op = state.getFrequencyOperator();
            if (op == FrequencyOperator.ASNEEDED) {
                this.willWater.setValue(getResources().getStringArray(R.array.schedulerulewizard_daystowater_flex)[op.ordinal()]);
            } else if (op == FrequencyOperator.WEEKDAY) {
                BaseScheduleRuleFragment.updateWeekdays(this.willWater, state);
            } else if (op == FrequencyOperator.INTERVAL) {
                updateInterval(this.willWater, state);
            }
            this.willWater.setVisibility(0);
            this.typeDescriptionFlex.setVisibility(0);
            this.intervalRepeat.setVisibility(8);
            this.weekdays.setVisibility(8);
        } else {
            int asNeededVisibility;
            int intervalVisibility;
            int weekdaysVisibility;
            this.type.setValue(getResources().getStringArray(R.array.schedulerulewizard_fixedtypenames)[frequencyOperator.ordinal()]);
            if (frequencyOperator == FrequencyOperator.ASNEEDED) {
                asNeededVisibility = 0;
            } else {
                asNeededVisibility = 8;
            }
            this.typeDescriptionAsNeeded.setVisibility(asNeededVisibility);
            if (frequencyOperator == FrequencyOperator.INTERVAL) {
                intervalVisibility = 0;
            } else {
                intervalVisibility = 8;
            }
            this.intervalRepeat.setVisibility(intervalVisibility);
            this.typeDescriptionInterval.setVisibility(intervalVisibility);
            if (frequencyOperator == FrequencyOperator.WEEKDAY) {
                weekdaysVisibility = 0;
            } else {
                weekdaysVisibility = 8;
            }
            this.weekdays.setVisibility(weekdaysVisibility);
            this.typeDescriptionSpecificDays.setVisibility(weekdaysVisibility);
        }
        updateFrequencyControls(state);
        updateStartTime(state);
        updateStartEndDates(state);
        updateAutomation(state);
        updateSmartWatering(state);
        this.container.setLayoutTransition(new LayoutTransition());
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        state.enabled = this.enabled.isChecked();
        commitAutomation(state);
    }
}
