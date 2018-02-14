package com.rachio.iro.ui.newschedulerulepath.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchCalendarCommand;
import com.rachio.iro.async.command.FetchCalendarCommand.FetchCalendarListener;
import com.rachio.iro.async.command.FetchCalendarCommand.ScheduleCalendarMeta;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.async.command.FetchIroPropertiesCommand;
import com.rachio.iro.async.command.FetchIroPropertiesCommand.Listener;
import com.rachio.iro.model.IroProperties;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.schedule.ScheduleCalendar.OutOfRangeException;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.newschedulerulepath.activity.ScheduleRuleWizardActivity.Type;
import com.rachio.iro.ui.newschedulerulepath.fragments.calendar.CalendarFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.calendar.NewRuleTypeFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.calendar.SelectedDayFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.typeblurbs.BaseBlurbFragment;
import com.rachio.iro.ui.newschedulerulepath.views.WateringScheduleCell;
import com.rachio.iro.utils.CalendarUtil;
import com.rachio.iro.utils.StringUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeMap;

public class ViewScheduleActivity extends BaseBaseScheduleRuleActivity implements FetchCalendarListener, FetchDeviceListener, Listener {
    public static final String TAG = ViewScheduleActivity.class.getName();
    private ScheduleCalendarMeta calendar;
    private boolean canCreateFixed = false;
    private boolean canCreateFlex = false;
    private Device device;
    private String deviceId;
    private TimeZone deviceTimeZone;
    private FetchDeviceCommand fetchDeviceCommand;
    private FetchIroPropertiesCommand fetchIroPropertiesCommand;
    private FragmentManager fragmentManager;
    private boolean isPaused = false;
    private long preselectedDay;
    private ArrayList<WateringScheduleCell> waitingCells = new ArrayList();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_fragmentwithactionbar);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            getIntent().getExtras();
            initState$79e5e33f();
        }
        this.preselectedDay = getIntent().getLongExtra("extra_selected_day", -1);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initState$79e5e33f();
    }

    private void initState$79e5e33f() {
        this.deviceId = getDeviceIdFromExtras();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                slideInFragmentRight(NewRuleTypeFragment.newInstance(this.deviceId, this.canCreateFixed, this.canCreateFlex));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onResume() {
        super.onResume();
        this.isPaused = false;
        if (this.fetchDeviceCommand == null) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    protected void onPause() {
        super.onPause();
        this.isPaused = true;
        if (this.fetchDeviceCommand != null) {
            this.fetchDeviceCommand.isCancelled = true;
            this.fetchDeviceCommand = null;
        }
        if (this.fetchIroPropertiesCommand != null) {
            this.fetchIroPropertiesCommand.isCancelled = true;
            this.fetchIroPropertiesCommand = null;
        }
    }

    private synchronized void loadMonth(int month) {
        if (this.deviceTimeZone != null) {
            Calendar c = Calendar.getInstance(this.deviceTimeZone);
            c.add(2, month);
            CalendarUtil.setToSundayBeforeStartOfMonth(c);
            long start = c.getTimeInMillis();
            c = Calendar.getInstance(this.deviceTimeZone);
            c.add(2, month);
            CalendarUtil.setToSaturdayAfterEndOfMonth(c);
            c.add(6, 7);
            long end = c.getTimeInMillis();
            Log.d(TAG, "start " + start);
            Log.d(TAG, "end " + end);
            new FetchCalendarCommand(this, this.deviceId, start, end).execute();
        }
    }

    public final synchronized void onDeviceDataChanged(String deviceId) {
        super.onDeviceDataChanged(deviceId);
        if (this.fetchDeviceCommand == null && StringUtils.equals(deviceId, this.deviceId)) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    public final void onCalendarLoaded(ScheduleCalendarMeta scheduleCalendar) {
        if (scheduleCalendar != null) {
            this.calendar = scheduleCalendar;
            Iterator<WateringScheduleCell> it = this.waitingCells.iterator();
            Calendar c = Calendar.getInstance(scheduleCalendar.deviceTimeZone);
            while (it.hasNext()) {
                WateringScheduleCell cell = (WateringScheduleCell) it.next();
                c.setTimeInMillis(cell.timeMillis);
                try {
                    cell.set(scheduleCalendar.calendar.getTypeForDate(scheduleCalendar.deviceTimeZone, c.getTime(), scheduleCalendar.rainDelayStart, scheduleCalendar.rainDelayEnd));
                    it.remove();
                } catch (OutOfRangeException e) {
                }
            }
            Log.d(TAG, this.waitingCells.size() + " cells still waiting");
            if (!this.isPaused && this.preselectedDay != -1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ViewScheduleActivity.this.selectDay(ViewScheduleActivity.this.preselectedDay);
                        ViewScheduleActivity.this.preselectedDay = -1;
                    }
                });
            }
        }
    }

    private void slideInFragmentRight(BaseFragment newFragment) {
        this.fragmentManager.beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.calendar_slide_in_from_right, R.anim.calendar_slide_out_to_left, R.anim.calendar_slide_in_from_left, R.anim.calendar_slide_out_to_right).replace(R.id.fragmentContainer, newFragment).commit();
        this.fragmentManager.executePendingTransactions();
    }

    public final void selectDay(long selectedDay) {
        if (this.calendar != null) {
            SelectedDayFragment fragment = SelectedDayFragment.newInstance(selectedDay);
            this.fragmentManager.beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.calendar_slide_in_from_bottom, R.anim.calendar_slide_out_to_top, R.anim.calendar_slide_in_from_top, R.anim.calendar_slide_out_to_bottom).replace(R.id.fragmentContainer, fragment).commit();
            this.fragmentManager.executePendingTransactions();
            fragment.setCalendar(this.calendar);
        }
    }

    public final void showTypeBlurb(Type type) {
        slideInFragmentRight(BaseBlurbFragment.createFragmentForType(type));
    }

    public final void startWizard(Type type) {
        ScheduleRuleWizardActivity.start(this, this.deviceId, type);
    }

    public final void editRule(ScheduleRule rule) {
        Intent intent = new Intent(this, EditScheduleRuleActivity.class);
        intent.putExtra("ruleid", rule.id);
        startActivity(intent);
    }

    public final synchronized void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        if (device != null) {
            this.deviceTimeZone = device.getTimeZoneAsTimeZone();
            CalendarFragment calendarFragment = CalendarFragment.newInstance(this.deviceTimeZone, new ArrayList(device.getAllScheduleRules()), new TreeMap(device.getZonesMap()));
            this.fragmentManager.popBackStack(null, 1);
            this.fragmentManager.beginTransaction().replace(R.id.fragmentContainer, calendarFragment).commit();
            loadMonth(0);
            this.fetchIroPropertiesCommand = new FetchIroPropertiesCommand(this);
            this.fetchIroPropertiesCommand.execute();
        }
    }

    public final void loadCell(WateringScheduleCell cell) {
        this.waitingCells.add(cell);
    }

    public final void onMonthChanged(int newMonth) {
        loadMonth(newMonth);
    }

    public final void onPropertiesLoaded(IroProperties properties) {
        if (properties != null) {
            this.canCreateFixed = this.device.canCreateNewFixedSchedule(properties);
            this.canCreateFlex = this.device.canCreateNewFlexSchedule(properties);
        }
    }
}
