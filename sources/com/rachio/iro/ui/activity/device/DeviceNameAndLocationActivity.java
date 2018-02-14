package com.rachio.iro.ui.activity.device;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.utils.LocationUtils;
import com.rachio.iro.utils.StringUtils;
import com.rachio.iro.utils.ValidationUtils;

public class DeviceNameAndLocationActivity extends ActivityThatSavesDevice implements FetchDeviceListener {
    private Device device;
    private String deviceId;
    private FetchDeviceCommand fetchDeviceCommand;
    private TextView geoText;
    private double lat;
    private double lon;
    private EditText nameEdit;
    private EditText zipEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deviceId = getDeviceIdFromExtras();
        setContentView((int) R.layout.activity_device_name);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.nameEdit = (EditText) findViewById(R.id.dialog_device_name_name);
        this.zipEdit = (EditText) findViewById(R.id.dialog_device_name_zip);
        this.geoText = (TextView) findViewById(R.id.dialog_device_name_geolocation);
        ((Button) findViewById(R.id.dialog_device_locationupdate)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                double[] gps = LocationUtils.getLocation(DeviceNameAndLocationActivity.this, false);
                DeviceNameAndLocationActivity.this.lat = gps[0];
                DeviceNameAndLocationActivity.this.lon = gps[1];
                DeviceNameAndLocationActivity.this.updateLocation();
            }
        });
    }

    private String getDeviceName() {
        return this.nameEdit.getText().toString().trim();
    }

    private String getZip() {
        return this.zipEdit.getText().toString().trim();
    }

    public final void save() {
        this.device.name = getDeviceName();
        this.device.zip = getZip();
        this.device.latitude = this.lat;
        this.device.longitude = this.lon;
        saveDevice(this.device, true);
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchDeviceCommand == null) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    public final boolean hasChanges() {
        return (StringUtils.equals(this.device.name, getDeviceName()) && StringUtils.equals(this.device.zip, getZip()) && this.lat == this.device.latitude && this.lon == this.device.longitude) ? false : true;
    }

    public final boolean isValid() {
        return !TextUtils.isEmpty(getDeviceName()) && ValidationUtils.isValidZipCode(getZip());
    }

    private void updateLocation() {
        this.geoText.setText(this.lat + ", " + this.lon);
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        this.nameEdit.setText(device.name);
        this.zipEdit.setText(device.zip);
        this.lat = device.latitude;
        this.lon = device.longitude;
        updateLocation();
    }

    public final boolean isBusy() {
        return this.fetchDeviceCommand != null;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("lat", this.lat);
        outState.putDouble("lon", this.lon);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.lat = savedInstanceState.getDouble("lat");
        this.lon = savedInstanceState.getDouble("lon");
    }
}
