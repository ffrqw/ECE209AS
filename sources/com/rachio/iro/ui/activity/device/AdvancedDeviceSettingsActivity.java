package com.rachio.iro.ui.activity.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Device.AttachedSensor;
import com.rachio.iro.ui.view.settings.RowWithCurrentValueAndChevronView;
import com.rachio.iro.ui.view.settings.SwitchRowWithDescriptionView;
import com.rachio.iro.utils.StringUtils;

public class AdvancedDeviceSettingsActivity extends ActivityThatSavesDevice implements FetchDeviceListener {
    private Device device;
    private String deviceId;
    private FetchDeviceCommand fetchDeviceCommand;
    private SwitchRowWithDescriptionView masterValveSwitch;
    private RowWithCurrentValueAndChevronView rainSensor1Switch;
    private RowWithCurrentValueAndChevronView rainSensor2Switch;
    private SwitchRowWithDescriptionView rainSensorSwitch;
    private SwitchRowWithDescriptionView waterHammer;

    private class SensorOnClickListener implements OnClickListener {
        private final String sensorId;

        public SensorOnClickListener(String sensorId) {
            this.sensorId = sensorId;
        }

        public void onClick(View v) {
            Context context = v.getContext();
            Intent i = new Intent(context, SensorConfigurationActivity.class);
            i.putExtra("DEVICEID", AdvancedDeviceSettingsActivity.this.deviceId);
            i.putExtra("sensorid", this.sensorId);
            context.startActivity(i);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deviceId = getDeviceIdFromExtras();
        setContentView((int) R.layout.activity_device_settings_advanced);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.masterValveSwitch = (SwitchRowWithDescriptionView) findViewById(R.id.device_settings_master_valve_switch);
        this.rainSensorSwitch = (SwitchRowWithDescriptionView) findViewById(R.id.device_settings_rain_sensor_switch);
        this.rainSensor1Switch = (RowWithCurrentValueAndChevronView) findViewById(R.id.device_settings_sensor1);
        this.rainSensor2Switch = (RowWithCurrentValueAndChevronView) findViewById(R.id.device_settings_sensor2);
        this.waterHammer = (SwitchRowWithDescriptionView) findViewById(R.id.device_waterhammer);
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchDeviceCommand == null) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    public final boolean isBusy() {
        return this.fetchDeviceCommand != null;
    }

    public final boolean isValid() {
        return true;
    }

    public final void save() {
        this.device.rainSensor = this.rainSensorSwitch.isChecked();
        this.device.masterValve = this.masterValveSwitch.isChecked();
        this.device.waterHammer = this.waterHammer.isChecked();
        saveDevice(this.device, true);
    }

    public final boolean hasChanges() {
        if (this.device == null) {
            return false;
        }
        if (this.masterValveSwitch.isChecked() == this.device.masterValve && this.rainSensorSwitch.isChecked() == this.device.rainSensor && this.waterHammer.isChecked() == this.device.waterHammer) {
            return false;
        }
        return true;
    }

    private static String sensorValue(AttachedSensor sensor) {
        if (!sensor.enabled) {
            return "Off";
        }
        switch (sensor.type) {
            case RAIN:
                return "Rain";
            case FLOW:
                return "Flow";
            default:
                return null;
        }
    }

    private void update() {
        if (this.device.isGen1()) {
            this.rainSensorSwitch.setVisibility(0);
            this.rainSensor1Switch.setVisibility(8);
            this.rainSensor2Switch.setVisibility(8);
            this.rainSensorSwitch.setChecked(this.device.rainSensor);
        } else {
            this.rainSensor1Switch.setVisibility(0);
            this.rainSensor2Switch.setVisibility(0);
            this.rainSensorSwitch.setVisibility(8);
            this.rainSensor1Switch.setValue(sensorValue(this.device.attachedSensors[0]));
            this.rainSensor2Switch.setValue(sensorValue(this.device.attachedSensors[1]));
            this.rainSensor1Switch.setOnClickListener(new SensorOnClickListener(this.device.attachedSensors[0].id));
            this.rainSensor2Switch.setOnClickListener(new SensorOnClickListener(this.device.attachedSensors[1].id));
        }
        this.masterValveSwitch.setEnabled(true);
        this.rainSensorSwitch.setEnabled(true);
        this.waterHammer.setEnabled(true);
        this.rainSensor1Switch.setEnabled(true);
        this.rainSensor2Switch.setEnabled(true);
        this.masterValveSwitch.setChecked(this.device.masterValve);
        this.waterHammer.setChecked(this.device.waterHammer);
    }

    public final void onDeviceDataChanged(String deviceId) {
        super.onDeviceDataChanged(deviceId);
        if (this.device != null && StringUtils.equals(deviceId, this.deviceId)) {
            this.database.refresh(this.device);
            update();
        }
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        update();
    }
}
