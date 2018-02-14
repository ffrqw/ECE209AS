package com.rachio.iro.ui.prodevicelist.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.ShallowDevice.RoughStatus;
import com.rachio.iro.model.user.User.DisplayUnit;
import com.rachio.iro.utils.UnitUtils;
import java.util.List;

public class ProDeviceDetailsActivity extends BaseProDeviceActivity implements FetchDeviceListener {
    private String deviceId;
    private String deviceLocationString = "";
    private DisplayUnit displayUnit;
    private FetchDeviceCommand fetchDeviceCommand;
    private double lat;
    private TextView location;
    private double lon;
    private Intent mapsIntent;
    private TextView name;
    private TextView owner;
    private TextView status;
    private Location userLocation;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, ProDeviceDetailsActivity.class);
        intent.putExtra("DEVICEID", deviceId);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deviceId = getDeviceIdFromExtras();
        setContentView((int) R.layout.activity_prodevicedetails);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.status = (TextView) findViewById(R.id.prodevicedetails_status);
        this.owner = (TextView) findViewById(R.id.prodevicedetails_owner);
        this.location = (TextView) findViewById(R.id.prodevicedetails_location);
        this.name = (TextView) findViewById(R.id.prodevicedetails_name);
        TextView launchMaps = (TextView) findViewById(R.id.prodevicedetails_launchmaps);
        ((TextView) findViewById(R.id.prodevicedetails_selectdevice)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProDeviceDetailsActivity.this.switchDevice(ProDeviceDetailsActivity.this.deviceId);
            }
        });
        launchMaps.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProDeviceDetailsActivity.this.startActivity(ProDeviceDetailsActivity.this.mapsIntent);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        this.permissionRequester.requestPermissions();
        this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
        this.fetchDeviceCommand.execute();
    }

    protected void onPause() {
        super.onPause();
        if (this.fetchDeviceCommand != null) {
            this.fetchDeviceCommand.isCancelled = true;
        }
    }

    public final void onDeviceLoaded(Device device) {
        boolean z = true;
        this.fetchDeviceCommand = null;
        if (device != null) {
            getSupportActionBar().setTitle(device.name);
            RoughStatus roughStatus = device.getRoughStatus();
            this.status.setText(roughStatus.statusText);
            this.status.setTextColor(getResources().getColor(roughStatus.statusTextColour));
            this.owner.setText(device.ownerName);
            this.name.setText(device.name);
            this.deviceLocationString = device.getDeviceLocation();
            this.lat = device.latitude;
            this.lon = device.longitude;
            this.displayUnit = device.getLocalUser().displayUnit;
            updateDeviceLocation();
            String str = "android.intent.action.VIEW";
            this.mapsIntent = new Intent(str, Uri.parse(String.format("geo:%f,%f?q=%f,%f(%s)", new Object[]{Double.valueOf(this.lat), Double.valueOf(this.lon), Double.valueOf(this.lat), Double.valueOf(this.lon), device.name})));
            this.mapsIntent.setPackage("com.google.android.apps.maps");
            List<ResolveInfo> activities = getPackageManager().queryIntentActivities(this.mapsIntent, 65536);
            TextView textView = this.location;
            if (activities == null || activities.size() <= 0) {
                z = false;
            }
            textView.setEnabled(z);
            updateLocation();
        }
    }

    private void updateDeviceLocation() {
        String distanceString = "";
        if (!(this.userLocation == null || this.displayUnit == null)) {
            String units = UnitUtils.getNameOfDistanceUnits(this.displayUnit);
            float[] result = new float[1];
            Location.distanceBetween(this.lat, this.lon, this.userLocation.getLatitude(), this.userLocation.getLongitude(), result);
            double userUnits = UnitUtils.convertMilesToUserUnits(this.displayUnit, UnitUtils.convertMetersToMiles((double) result[0]));
            distanceString = String.format("%.1f %s Away", new Object[]{Double.valueOf(userUnits), units});
        }
        if (this.location != null) {
            this.location.setText(this.deviceLocationString + "  " + distanceString);
        }
    }

    protected final void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        this.userLocation = location;
        updateDeviceLocation();
    }
}
