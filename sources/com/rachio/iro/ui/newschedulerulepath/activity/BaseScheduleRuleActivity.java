package com.rachio.iro.ui.newschedulerulepath.activity;

import android.os.Bundle;
import android.view.MenuItem;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.FrequencyOperator;
import com.rachio.iro.model.schedule.ScheduleRule.ScheduleJobType;
import com.rachio.iro.ui.FragmentNavigationController;
import com.rachio.iro.ui.FragmentNavigationController.Path;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.EndDateFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.IntervalSelectFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.StartDateFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.WeekdaysSelectFragment;

public abstract class BaseScheduleRuleActivity extends BaseBaseScheduleRuleActivity implements FetchDeviceListener {
    protected static Path endDateSelectPath = FragmentNavigationController.createSingleScreenPath("select_date_end", EndDateFragment.class, R.string.scheduleenddate);
    protected static Path selectIntervalScreen = FragmentNavigationController.createSingleScreenPath("select_interval", IntervalSelectFragment.class, R.string.willwater);
    protected static Path selectWeekdays = FragmentNavigationController.createSingleScreenPath("select_weekdays", WeekdaysSelectFragment.class, R.string.daystowater);
    protected static Path startDateSelectPath = FragmentNavigationController.createSingleScreenPath("select_date_start", StartDateFragment.class, R.string.schedulestartdate);
    protected FragmentNavigationController<BaseScheduleRuleFragment> controller = new FragmentNavigationController(this.rootPath);
    private FetchDeviceCommand fetchDeviceCommand;
    protected Path rootPath = new Path("root");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_fragmentwithactionbar);
        wireupToolbarActionBar();
        this.controller.onCreate(this, savedInstanceState);
    }

    public final void onAction(int action) {
        this.controller.onAction(action);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.controller.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.controller.onRestoreInstanceState(savedInstanceState);
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchDeviceCommand == null) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.prefsWrapper.getSelectedDeviceId());
            this.fetchDeviceCommand.execute();
        }
        this.controller.onResume();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeRuleType(FrequencyOperator type) {
    }

    public final void changeRuleType(ScheduleRule state, FrequencyOperator type) {
        justChangeRuleType(state, type);
        ((BaseScheduleRuleFragment) this.controller.getCurrentFragment()).updateState(state);
    }

    public static void justChangeRuleType(ScheduleRule state, FrequencyOperator type) {
        if (type != state.getFrequencyOperator()) {
            switch (type) {
                case ASNEEDED:
                    if (state.isFlex()) {
                        state.anyDay = false;
                        state.scheduleJobTypes = new ScheduleJobType[]{ScheduleJobType.ANY};
                        return;
                    }
                    state.anyDay = true;
                    state.scheduleJobTypes = null;
                    return;
                case INTERVAL:
                    if (state.isFlex()) {
                        state.anyDay = false;
                        state.scheduleJobTypes = new ScheduleJobType[]{ScheduleJobType.ODD};
                        return;
                    }
                    state.anyDay = false;
                    state.scheduleJobTypes = new ScheduleJobType[]{ScheduleJobType.INTERVAL_3};
                    return;
                case WEEKDAY:
                    state.anyDay = false;
                    state.scheduleJobTypes = new ScheduleJobType[]{ScheduleJobType.DAY_OF_WEEK_2, ScheduleJobType.DAY_OF_WEEK_5};
                    return;
                default:
                    return;
            }
        }
    }

    public void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
    }
}
