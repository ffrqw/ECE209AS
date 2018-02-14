package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.FrequencyOperator;
import com.rachio.iro.ui.FragmentNavigationController;
import com.rachio.iro.ui.FragmentNavigationController.Path;
import com.rachio.iro.ui.newschedulerulepath.ExpandableController;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment.ActionOnClickListener;
import com.rachio.iro.ui.view.settings.RowWithCurrentValueAndChevronView;
import com.rachio.iro.ui.view.settings.RowWithCurrentValueThatExpands;

public class WeatherIntelligenceFragment extends BaseScheduleRuleFragment {
    public static final Path PATH = FragmentNavigationController.createSingleScreenPathWithABunchOfSingleScreen("weatherintelligence", WeatherIntelligenceFragment.class, R.string.add_schedule_weather_intelligence, new int[]{111, 108}, new String[]{"rainskip", "freezeskip"}, new Class[]{RainDelaySelectFragment.class, FreezeDelaySelectFragment.class}, new int[]{R.string.rainskipthreshold, R.string.freezeskiptemperature});
    private RowWithCurrentValueThatExpands climateSkip;
    private SwitchCompat climateSkipSwitch;
    private RowWithCurrentValueAndChevronView freezeSkip;
    private RowWithCurrentValueAndChevronView rainSkip;
    private RowWithCurrentValueThatExpands seasonalAdjustment;
    private SwitchCompat seasonalAdjustmentSwitch;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_weatherintelligence, container, false);
        this.seasonalAdjustment = (RowWithCurrentValueThatExpands) view.findViewById(R.id.seasonaladjustment);
        this.seasonalAdjustmentSwitch = (SwitchCompat) view.findViewById(R.id.seasonaladjustmentswitch);
        this.climateSkip = (RowWithCurrentValueThatExpands) view.findViewById(R.id.climateskip);
        this.climateSkipSwitch = (SwitchCompat) view.findViewById(R.id.climateskipswitch);
        this.rainSkip = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.rainskip);
        this.freezeSkip = (RowWithCurrentValueAndChevronView) view.findViewById(R.id.freezeskip);
        this.seasonalAdjustmentSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WeatherIntelligenceFragment.this.updateExpanders();
            }
        });
        this.rainSkip.setOnClickListener(new ActionOnClickListener(111));
        this.freezeSkip.setOnClickListener(new ActionOnClickListener(108));
        ExpandableController expandableController = new ExpandableController();
        expandableController.add(this.seasonalAdjustment);
        expandableController.add(this.climateSkip);
        this.climateSkipSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WeatherIntelligenceFragment.this.updateEnabledDisabled(WeatherIntelligenceFragment.this.climateSkip, isChecked);
            }
        });
        return view;
    }

    private void updateExpanders() {
        updateEnabledDisabled(this.seasonalAdjustment, this.seasonalAdjustmentSwitch.isChecked());
        updateEnabledDisabled(this.climateSkip, this.climateSkipSwitch.isChecked());
    }

    public final void updateState(ScheduleRule entity) {
        boolean showSeasonalShift;
        int i = 0;
        super.updateState(entity);
        if (entity.getFrequencyOperator() != FrequencyOperator.ASNEEDED) {
            showSeasonalShift = true;
        } else {
            showSeasonalShift = false;
        }
        RowWithCurrentValueThatExpands rowWithCurrentValueThatExpands = this.seasonalAdjustment;
        if (!showSeasonalShift) {
            i = 8;
        }
        rowWithCurrentValueThatExpands.setVisibility(i);
        this.seasonalAdjustmentSwitch.setChecked(entity.waterBudget);
        this.climateSkipSwitch.setChecked(entity.etSkip);
        updateEnabledDisabled(this.rainSkip, entity.precipDelay.enabled);
        updateEnabledDisabled(this.freezeSkip, entity.freezeDelay.enabled);
        updateExpanders();
    }

    public final void commitState(ScheduleRule entity) {
        super.commitState(entity);
        entity.waterBudget = this.seasonalAdjustmentSwitch.isChecked();
        entity.etSkip = this.climateSkipSwitch.isChecked();
    }
}
