package com.rachio.iro.ui.activity.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.gen2.WifiUpdateButtonsActivity;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.ui.activity.BlinkUpActivity;
import com.rachio.iro.ui.activity.BlinkUpActivity.BlinkUpView;
import com.rachio.iro.ui.dialog.DeleteDeviceDialog;
import com.rachio.iro.ui.view.settings.RowWithChevronView;
import com.rachio.iro.ui.view.settings.RowWithCurrentValueAndChevronView;
import com.rachio.iro.utils.StringUtils;

public class DeviceSettingsActivity extends ActivityThatSavesDevice implements FetchDeviceListener {
    private TextView delete;
    private Device device;
    private String deviceId;
    private TextView deviceLocation;
    private RowWithCurrentValueAndChevronView deviceMacAddress;
    private TextView deviceName;
    private RowWithCurrentValueAndChevronView deviceSerialNumber;
    private FetchDeviceCommand fetchDeviceCommand;
    private double lat;
    private double lon;
    private RowWithChevronView share;
    private SwitchCompat standbySwitch;
    private RowWithChevronView updateWifi;
    private String zip;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deviceId = getDeviceIdFromExtras();
        setContentView((int) R.layout.activity_device_settings);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayout nameAndLocation = (LinearLayout) findViewById(R.id.device_settings_namelocation);
        this.deviceSerialNumber = (RowWithCurrentValueAndChevronView) findViewById(R.id.device_serial_number);
        this.deviceMacAddress = (RowWithCurrentValueAndChevronView) findViewById(R.id.device_mac);
        this.deviceName = (TextView) findViewById(R.id.device_settings_name);
        this.deviceLocation = (TextView) findViewById(R.id.device_settings_location);
        this.standbySwitch = (SwitchCompat) findViewById(R.id.device_settings_sleep_switch);
        RowWithChevronView advanced = (RowWithChevronView) findViewById(R.id.device_settings_advanced);
        this.updateWifi = (RowWithChevronView) findViewById(R.id.device_settings_update_wifi_row);
        this.share = (RowWithChevronView) findViewById(R.id.device_settings_share);
        this.delete = (TextView) findViewById(R.id.device_settings_delete);
        nameAndLocation.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(DeviceSettingsActivity.this, DeviceNameAndLocationActivity.class);
                i.putExtra("DEVICEID", DeviceSettingsActivity.this.deviceId);
                DeviceSettingsActivity.this.startActivity(i);
            }
        });
        this.standbySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (DeviceSettingsActivity.this.device != null && DeviceSettingsActivity.this.device.schedulePause != isChecked) {
                    boolean z;
                    DeviceSettingsActivity.this.standbySwitch.setEnabled(false);
                    Device clone = DeviceSettingsActivity.this.device.getTransmittableVersion();
                    if (DeviceSettingsActivity.this.device.schedulePause) {
                        z = false;
                    } else {
                        z = true;
                    }
                    clone.schedulePause = z;
                    DeviceSettingsActivity.this.saveDevice(clone, false);
                }
            }
        });
        this.share.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(DeviceSettingsActivity.this, ShareActivity.class);
                i.putExtra("DEVICEID", DeviceSettingsActivity.this.deviceId);
                DeviceSettingsActivity.this.startActivity(i);
            }
        });
        advanced.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(DeviceSettingsActivity.this, AdvancedDeviceSettingsActivity.class);
                i.putExtra("DEVICEID", DeviceSettingsActivity.this.deviceId);
                DeviceSettingsActivity.this.startActivity(i);
            }
        });
        this.updateWifi.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DeviceSettingsActivity.access$300(DeviceSettingsActivity.this);
            }
        });
        this.delete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (DeviceSettingsActivity.this.device != null) {
                    DeleteDeviceDialog.show(DeviceSettingsActivity.this.device.user, DeviceSettingsActivity.this, DeviceSettingsActivity.this.device);
                }
            }
        });
        setResult(0);
    }

    private void fetchDevice() {
        if (this.fetchDeviceCommand == null) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    protected void onResume() {
        super.onResume();
        fetchDevice();
    }

    public final void save() {
        this.device.name = this.deviceName.getText().toString();
        this.device.zip = this.zip;
        this.device.latitude = this.lat;
        this.device.longitude = this.lon;
        update();
        setResult(-1);
        saveDevice(this.device, true);
    }

    public final boolean hasChanges() {
        if (this.device == null) {
            return false;
        }
        if (this.deviceName.getText().toString().equals(this.device.name) && StringUtils.equals(this.zip, this.device.zip) && this.lat == this.device.latitude && this.lon == this.device.longitude) {
            return false;
        }
        return true;
    }

    private void update() {
        int i = 0;
        this.standbySwitch.setEnabled(this.device != null);
        if (this.device != null) {
            int i2;
            this.deviceName.setText(this.device.name);
            this.deviceLocation.setText(this.device.getDeviceLocation());
            this.lat = this.device.latitude;
            this.lon = this.device.longitude;
            this.zip = this.device.zip;
            this.deviceSerialNumber.setValue(this.device.serialNumber);
            this.deviceMacAddress.setValue(this.device.macAddress);
            this.standbySwitch.setChecked(this.device.schedulePause);
            boolean deviceBelongsToUser = !this.device.isSomeoneElses();
            RowWithChevronView rowWithChevronView = this.updateWifi;
            if (this.device.isVirtual() || !deviceBelongsToUser) {
                i2 = 8;
            } else {
                i2 = 0;
            }
            rowWithChevronView.setVisibility(i2);
            rowWithChevronView = this.share;
            if (deviceBelongsToUser) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            rowWithChevronView.setVisibility(i2);
            TextView textView = this.delete;
            if (!deviceBelongsToUser) {
                i = 8;
            }
            textView.setVisibility(i);
        }
    }

    public final void onDeviceDataChanged(String deviceId) {
        super.onDeviceDataChanged(deviceId);
        if (this.device != null && StringUtils.equals(deviceId, this.deviceId)) {
            fetchDevice();
        }
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        update();
    }

    public final boolean isBusy() {
        return false;
    }

    public final boolean isValid() {
        return true;
    }

    static /* synthetic */ void access$300(DeviceSettingsActivity x0) {
        if (x0.hasChanges()) {
            x0.save();
        }
        if (x0.device.isGen1()) {
            Intent intent = new Intent(x0.getApplicationContext(), BlinkUpActivity.class);
            intent.putExtra("BLINKUP_STEP", BlinkUpView.WifiStart);
            intent.putExtra("BLINKUP_ACTION", "UPDATE");
            x0.startActivity(intent);
            return;
        }
        intent = new Intent(x0, WifiUpdateButtonsActivity.class);
        intent.putExtra("device_mac", x0.device.macAddress);
        intent.putExtra("device_name", x0.device.name);
        intent.putExtra("device_serialnumber", x0.device.serialNumber);
        x0.startActivity(intent);
    }
}
