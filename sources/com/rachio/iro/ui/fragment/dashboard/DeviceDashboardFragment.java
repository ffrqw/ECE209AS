package com.rachio.iro.ui.fragment.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchCalendarCommand;
import com.rachio.iro.async.command.FetchCalendarCommand.FetchCalendarListener;
import com.rachio.iro.async.command.FetchCalendarCommand.ScheduleCalendarMeta;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.async.command.FetchWaterUsageCommand;
import com.rachio.iro.async.command.FetchWaterUsageCommand.FetchWaterUsageListener;
import com.rachio.iro.async.command.FetchWaterUsageCommand.WaterUsageHolder;
import com.rachio.iro.async.command.FetchWeatherForecastCommand;
import com.rachio.iro.async.command.FetchWeatherForecastCommand.FetchWeatherForecastListener;
import com.rachio.iro.async.command.FetchWeatherForecastCommand.ForecastHolder;
import com.rachio.iro.async.command.FetchZonesCommand;
import com.rachio.iro.async.command.FetchZonesCommand.FetchZonesListener;
import com.rachio.iro.async.command.FetchZonesCommand.ZonesMetaHolder;
import com.rachio.iro.binder.CardInfo;
import com.rachio.iro.binder.ModelViewType;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.ui.activity.DashboardActivity;
import com.rachio.iro.ui.activity.DashboardActivity.OnZoneDataChangedListener;
import java.util.ArrayList;
import java.util.List;

public class DeviceDashboardFragment extends DashboardCardsFragment implements FetchCalendarListener, FetchDeviceListener, FetchWaterUsageListener, FetchWeatherForecastListener, FetchZonesListener, OnZoneDataChangedListener {
    private static final String TAG = DeviceDashboardFragment.class.getSimpleName();
    private FetchCalendarCommand fetchCalendarCommand;
    private FetchWaterUsageCommand fetchWaterUsageCommand;
    private FetchWeatherForecastCommand fetchWeatherForecastCommand;
    private FetchZonesCommand fetchZonesCommand;
    private boolean showDisabledZones = false;

    public static DeviceDashboardFragment newInstance(String deviceId, boolean readOnly) {
        Bundle arguments = BaseDeviceDashboardFragment.createArgs(deviceId);
        arguments.putBoolean("readonly", readOnly);
        DeviceDashboardFragment fragment = new DeviceDashboardFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public final String getSection() {
        return getString(R.string.navigation_section_dashboard);
    }

    public void onResume() {
        super.onResume();
        List arrayList = new ArrayList();
        arrayList.add(new CardInfo(ModelViewType.CONNECTION_STATUS));
        arrayList.add(new CardInfo(ModelViewType.LOCAL_WEATHER));
        arrayList.add(new CardInfo(ModelViewType.WATERING_SCHEDULE));
        arrayList.add(new CardInfo(ModelViewType.MY_YARD));
        arrayList.add(new CardInfo(ModelViewType.WATER_USE));
        setCards(arrayList);
        onDeviceDataChanged(this.mDeviceId);
    }

    public void onPause() {
        super.onPause();
        hideProgress();
        if (this.fetchDeviceCommand != null) {
            this.fetchDeviceCommand.isCancelled = true;
            this.fetchDeviceCommand = null;
        }
        if (this.fetchWeatherForecastCommand != null) {
            this.fetchWeatherForecastCommand.isCancelled = true;
            this.fetchWeatherForecastCommand = null;
        }
        if (this.fetchWaterUsageCommand != null) {
            this.fetchWaterUsageCommand.isCancelled = true;
            this.fetchWaterUsageCommand = null;
        }
        if (this.fetchCalendarCommand != null) {
            this.fetchCalendarCommand.isCancelled = true;
            this.fetchCalendarCommand = null;
        }
    }

    private void loadZones() {
        if (this.fetchZonesCommand == null) {
            this.fetchZonesCommand = new FetchZonesCommand(this, this.mDeviceId, this.showDisabledZones, getArguments().getBoolean("readonly"));
            this.fetchZonesCommand.execute();
        }
    }

    public final void onDeviceDataChanged(String deviceId) {
        if (this.mDeviceId != null && this.mDeviceId.equals(deviceId) && this.fetchDeviceCommand == null) {
            Log.d(TAG, "Updating device " + this.mDeviceId);
            showProgress((int) R.string.progress_text_loading_device_information);
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.mDeviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        if (getActivity() == null) {
            return;
        }
        if (device != null) {
            hideProgress();
            ZonesMetaHolder zonesMeta = new ZonesMetaHolder(device.getEnabledZones(), 0, 0, this.showDisabledZones, (long) device.getZones().size(), getArguments().getBoolean("readonly"), true);
            this.mAdapter.updateCard(new CardInfo(ModelViewType.CONNECTION_STATUS, device));
            this.mAdapter.updateCard(new CardInfo(ModelViewType.MY_YARD, zonesMeta));
            if (this.fetchWeatherForecastCommand == null) {
                this.fetchWeatherForecastCommand = new FetchWeatherForecastCommand(this, this.mDeviceId);
                this.fetchWeatherForecastCommand.execute();
            }
            if (this.fetchWaterUsageCommand == null) {
                this.fetchWaterUsageCommand = new FetchWaterUsageCommand(this, this.mDeviceId);
                this.fetchWaterUsageCommand.execute();
            }
            if (this.fetchCalendarCommand == null) {
                this.fetchCalendarCommand = new FetchCalendarCommand(this, this.mDeviceId);
                this.fetchCalendarCommand.execute();
            }
            loadZones();
            return;
        }
        ((DashboardActivity) getActivity()).reloadUserDevices();
    }

    public final void onWeatherForecastLoaded(ForecastHolder holder) {
        this.fetchWeatherForecastCommand = null;
        this.mAdapter.updateCard(new CardInfo(ModelViewType.LOCAL_WEATHER, holder));
    }

    public final void onWaterUsageLoaded(WaterUsageHolder waterUsage) {
        this.fetchWaterUsageCommand = null;
        this.mAdapter.updateCard(new CardInfo(ModelViewType.WATER_USE, waterUsage));
    }

    public final void onCalendarLoaded(ScheduleCalendarMeta calendar) {
        this.fetchCalendarCommand = null;
        this.mAdapter.updateCard(new CardInfo(ModelViewType.WATERING_SCHEDULE, calendar));
    }

    public final void onZonesFetched(ZonesMetaHolder zones) {
        this.fetchZonesCommand = null;
        if (zones != null || getActivity() == null || getActivity().isFinishing()) {
            this.mAdapter.updateCard(new CardInfo(ModelViewType.MY_YARD, zones));
        } else {
            Toast.makeText(getActivity(), "Failed to load zones", 0).show();
        }
    }

    public final void toggleShowDisabledZones() {
        this.showDisabledZones = !this.showDisabledZones;
        loadZones();
    }

    public final void onZoneDataChanged$552c4e01() {
        loadZones();
    }
}
