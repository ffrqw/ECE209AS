package com.rachio.iro.ui.newschedulerulepath.fragments.wizard;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.FrequencyOperator;
import com.rachio.iro.model.schedule.ScheduleRule.ScheduleJobType;
import com.rachio.iro.ui.FragmentNavigationController;
import com.rachio.iro.ui.FragmentNavigationController.Path;
import com.rachio.iro.ui.newschedulerulepath.activity.BaseScheduleRuleActivity;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.IntervalSelectFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.WeekdaysSelectFragment;
import com.rachio.iro.utils.SpinnerUtils;
import java.util.Arrays;

public class HowOftenToWaterFragment extends BaseScheduleRuleFragment {
    public static final Path PATH = FragmentNavigationController.createSingleScreenPathWithABunchOfSingleScreen("willwater", HowOftenToWaterFragment.class, R.string.whentowater, new int[]{101, 103}, new String[]{"select_interval", "select_weekdays"}, new Class[]{IntervalSelectFragment.class, WeekdaysSelectFragment.class}, new int[]{R.string.willwater, R.string.daystowater});
    private boolean isValidAsInterval = false;
    private boolean isValidAsWeekdays = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedulerulewizard_howoften, container, false);
        this.frequencyModifier = (Spinner) view.findViewById(R.id.schedulerulewizard_frequencymodifier);
        SpinnerUtils.fixChevronSpinner(this.frequencyModifier);
        View findViewById = view.findViewById(R.id.schedulerulewizard_asneededcontent);
        View findViewById2 = view.findViewById(R.id.schedulerulewizard_intervalcontent);
        View findViewById3 = view.findViewById(R.id.schedulerulewizard_weekdayscontent);
        final View[] viewArr = new View[]{findViewById, findViewById2, findViewById3};
        this.frequencyModifier.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int i = 0;
                while (i < parent.getCount()) {
                    viewArr[i].setVisibility(i == position ? 0 : 8);
                    i++;
                }
                ((BaseScheduleRuleActivity) HowOftenToWaterFragment.this.getActivity()).changeRuleType(FrequencyOperator.values()[position]);
                HowOftenToWaterFragment.this.onStateChanged();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        wireUpFrequencyControls(view);
        return view;
    }

    public final void updateState(ScheduleRule state) {
        boolean z;
        boolean z2 = true;
        if (this.frequencyModifier.getAdapter() == null) {
            String[] modifierStrings;
            Resources resources = getResources();
            if (state.isFlex()) {
                modifierStrings = resources.getStringArray(R.array.schedulerulewizard_daystowater_flex);
            } else {
                modifierStrings = resources.getStringArray(R.array.schedulerulewizard_daystowater);
            }
            this.frequencyModifier.setAdapter(new ArrayAdapter(getContext(), R.layout.dropdown_settingsspinner, modifierStrings));
        }
        int currentSelection = this.frequencyModifier.getSelectedItemPosition();
        int newSelection = state.getFrequencyOperator().ordinal();
        if (newSelection != currentSelection) {
            this.frequencyModifier.setSelection(newSelection);
        }
        updateFrequencyControls(state);
        if (state.scheduleJobTypes == null || state.scheduleJobTypes.length != 1 || Arrays.binarySearch(ScheduleJobType.intervals, state.scheduleJobTypes[0]) < 0) {
            z = false;
        } else {
            z = true;
        }
        this.isValidAsInterval = z;
        if (state.scheduleJobTypes == null || state.scheduleJobTypes.length <= 0) {
            z2 = false;
        }
        this.isValidAsWeekdays = z2;
        if (this.isValidAsWeekdays) {
            for (ScheduleJobType sjt : state.scheduleJobTypes) {
                if (Arrays.binarySearch(ScheduleRule.weekdays, sjt) <= 0) {
                    this.isValidAsWeekdays = false;
                    break;
                }
            }
        }
        super.updateState(state);
    }

    public final boolean validate() {
        switch (this.frequencyModifier.getSelectedItemPosition()) {
            case 1:
                return this.isValidAsInterval;
            case 2:
                return this.isValidAsWeekdays;
            default:
                return true;
        }
    }
}
