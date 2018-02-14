package com.rachio.iro.ui.activity.user;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchUserCommand;
import com.rachio.iro.async.command.FetchUserCommand.Listener;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.user.User.DisplayUnit;
import com.rachio.iro.model.user.User.Setting;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends ActivityThatSavesUser implements Listener {
    private SwitchCompat deviceStatusNotification;
    private FetchUserCommand fetchUserCommand;
    private SwitchCompat global;
    private SwitchCompat rainDelayEmail;
    private SwitchCompat rainDelayNotification;
    private SwitchCompat rainSensor;
    private SwitchCompat scheduleStatusNotifcation;
    private RadioGroup units;
    private User user;
    private String userId;
    private SwitchCompat waterBudgetEmail;
    private SwitchCompat waterBudgetNotification;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_preferences);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.userId = getUserIdFromExtras();
        this.units = (RadioGroup) findViewById(R.id.preferences_units);
        this.global = (SwitchCompat) findViewById(R.id.preferences_global);
        this.rainDelayEmail = (SwitchCompat) findViewById(R.id.preferences_raindelayemail);
        this.waterBudgetEmail = (SwitchCompat) findViewById(R.id.preferences_waterbudgetemail);
        this.rainDelayNotification = (SwitchCompat) findViewById(R.id.preferences_raindelaynotification);
        this.rainSensor = (SwitchCompat) findViewById(R.id.preferences_rainsensor);
        this.waterBudgetNotification = (SwitchCompat) findViewById(R.id.preferences_waterbudgetnotification);
        this.deviceStatusNotification = (SwitchCompat) findViewById(R.id.preferences_devicestatusnotification);
        this.scheduleStatusNotifcation = (SwitchCompat) findViewById(R.id.preferences_schedulestatusnotification);
        final SwitchCompat[] switches = new SwitchCompat[]{this.rainDelayEmail, this.waterBudgetEmail, this.rainDelayNotification, this.rainSensor, this.waterBudgetNotification, this.deviceStatusNotification, this.scheduleStatusNotifcation};
        this.global.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (SwitchCompat s : switches) {
                    s.setEnabled(isChecked);
                }
            }
        });
        this.global.setChecked(true);
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchUserCommand == null) {
            this.fetchUserCommand = new FetchUserCommand(this, this.userId);
            this.fetchUserCommand.execute();
        }
    }

    private boolean userHasSetting(Setting setting) {
        if (this.user.settingNames == null) {
            return false;
        }
        for (Setting s : this.user.settingNames) {
            if (s == setting) {
                return true;
            }
        }
        return false;
    }

    public final boolean isBusy() {
        return this.fetchUserCommand != null;
    }

    public final boolean isValid() {
        return true;
    }

    public final boolean hasChanges() {
        if ((this.units.getCheckedRadioButtonId() != R.id.preferences_units_us || this.user.displayUnit == DisplayUnit.US) && (this.units.getCheckedRadioButtonId() != R.id.preferences_units_metric || this.user.displayUnit == DisplayUnit.METRIC)) {
            if ((!this.global.isChecked()) == userHasSetting(Setting.DISABLE_ALL) && this.rainDelayEmail.isChecked() == userHasSetting(Setting.RAIN_DELAY_EMAIL) && this.waterBudgetEmail.isChecked() == userHasSetting(Setting.WATER_BUDGET_EMAIL) && this.rainDelayNotification.isChecked() == userHasSetting(Setting.WEATHER_INTELLIGENCE_NOTIFICATION) && this.rainSensor.isChecked() == userHasSetting(Setting.RAIN_SENSOR_NOTIFICATION) && this.waterBudgetNotification.isChecked() == userHasSetting(Setting.WATER_BUDGET_NOTIFICATION) && this.deviceStatusNotification.isChecked() == userHasSetting(Setting.DEVICE_STATUS_NOTIFICATION) && this.scheduleStatusNotifcation.isChecked() == userHasSetting(Setting.SCHEDULE_STATUS_NOTIFICATION)) {
                return false;
            }
        }
        return true;
    }

    public final void save() {
        switch (this.units.getCheckedRadioButtonId()) {
            case R.id.preferences_units_us:
                this.user.displayUnit = DisplayUnit.US;
                break;
            case R.id.preferences_units_metric:
                this.user.displayUnit = DisplayUnit.METRIC;
                break;
        }
        List<Setting> settings = new ArrayList();
        if (!this.global.isChecked()) {
            settings.add(Setting.DISABLE_ALL);
        }
        if (this.rainDelayEmail.isChecked()) {
            settings.add(Setting.RAIN_DELAY_EMAIL);
        }
        if (this.waterBudgetEmail.isChecked()) {
            settings.add(Setting.WATER_BUDGET_EMAIL);
        }
        if (this.rainDelayNotification.isChecked()) {
            settings.add(Setting.WEATHER_INTELLIGENCE_NOTIFICATION);
        }
        if (this.rainSensor.isChecked()) {
            settings.add(Setting.RAIN_SENSOR_NOTIFICATION);
        }
        if (this.waterBudgetNotification.isChecked()) {
            settings.add(Setting.WATER_BUDGET_NOTIFICATION);
        }
        if (this.deviceStatusNotification.isChecked()) {
            settings.add(Setting.DEVICE_STATUS_NOTIFICATION);
        }
        if (this.scheduleStatusNotifcation.isChecked()) {
            settings.add(Setting.SCHEDULE_STATUS_NOTIFICATION);
        }
        this.user.settingNames = (Setting[]) settings.toArray(new Setting[0]);
        saveUser(this.user, true);
    }

    public final void onUserLoaded(User user) {
        if (user == null) {
            finish();
            return;
        }
        this.fetchUserCommand = null;
        this.user = user;
        if (user.displayUnit == DisplayUnit.METRIC) {
            this.units.check(R.id.preferences_units_metric);
        }
        if (user.settingNames != null) {
            for (Setting s : user.settingNames) {
                switch (s) {
                    case RAIN_DELAY_EMAIL:
                        this.rainDelayEmail.setChecked(true);
                        break;
                    case WATER_BUDGET_EMAIL:
                        this.waterBudgetEmail.setChecked(true);
                        break;
                    case WEATHER_INTELLIGENCE_NOTIFICATION:
                        this.rainDelayNotification.setChecked(true);
                        break;
                    case RAIN_SENSOR_NOTIFICATION:
                        this.rainSensor.setChecked(true);
                        break;
                    case WATER_BUDGET_NOTIFICATION:
                        this.waterBudgetNotification.setChecked(true);
                        break;
                    case DEVICE_STATUS_NOTIFICATION:
                        this.deviceStatusNotification.setChecked(true);
                        break;
                    case SCHEDULE_STATUS_NOTIFICATION:
                        this.scheduleStatusNotifcation.setChecked(true);
                        break;
                    case DISABLE_ALL:
                        this.global.setChecked(false);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.fetchUserCommand != null) {
            this.fetchUserCommand.isCancelled = true;
            this.fetchUserCommand = null;
        }
    }
}
