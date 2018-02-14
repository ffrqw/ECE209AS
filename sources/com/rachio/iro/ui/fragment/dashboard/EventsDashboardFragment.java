package com.rachio.iro.ui.fragment.dashboard;

import android.os.Handler;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.async.command.FetchEventsCommand;
import com.rachio.iro.async.command.FetchEventsCommand.FetchEventsListener;
import com.rachio.iro.binder.CardInfo;
import com.rachio.iro.binder.CurrentlyWateringBinder.CurrentlyWateringData;
import com.rachio.iro.binder.ModelViewType;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.utils.StringUtils;
import java.util.List;

public class EventsDashboardFragment extends DashboardCardsFragment implements FetchDeviceListener, FetchEventsListener {
    private CurrentlyWateringData currentlyWateringData = new CurrentlyWateringData();
    private Device device;
    private FetchEventsCommand fetchDeviceEventsCommand;
    private FetchEventsCommand fetchScheduleEventsCommand;
    private FetchEventsCommand fetchWateringEventsCommand;
    private Handler handler = new Handler();
    private Runnable updateCurrentlyWateringRunnable = new Runnable() {
        public void run() {
            if (EventsDashboardFragment.this.device.scheduleExecution == null || !EventsDashboardFragment.this.device.scheduleExecution.isRunning()) {
                EventsDashboardFragment.this.removeCard(new CardInfo(ModelViewType.CURRENTLY_WATERING, EventsDashboardFragment.this.device));
                return;
            }
            EventsDashboardFragment.this.device.scheduleExecution.getZoneEvents(EventsDashboardFragment.this.database);
            EventsDashboardFragment.this.mAdapter.updateCard(new CardInfo(ModelViewType.CURRENTLY_WATERING, EventsDashboardFragment.this.currentlyWateringData, 0));
            EventsDashboardFragment.this.handler.postDelayed(EventsDashboardFragment.this.updateCurrentlyWateringRunnable, 5000);
        }
    };

    public static EventsDashboardFragment newInstance(String deviceId) {
        EventsDashboardFragment fragment = new EventsDashboardFragment();
        fragment.setArguments(BaseDeviceDashboardFragment.createArgs(deviceId));
        return fragment;
    }

    public final String getSection() {
        return getString(R.string.navigation_section_activity);
    }

    public void onResume() {
        super.onResume();
        clearCards();
        addCard(new CardInfo(ModelViewType.WATERING_HISTORY));
        addCard(new CardInfo(ModelViewType.SCHEDULE_UPDATES));
        addCard(new CardInfo(ModelViewType.DEVICE_UPDATES));
        onDeviceDataChanged(this.mDeviceId);
    }

    public void onPause() {
        super.onPause();
        this.handler.removeCallbacks(this.updateCurrentlyWateringRunnable);
    }

    public final void onDeviceDataChanged(String deviceId) {
        if (this.mDeviceId != null && this.mDeviceId.equals(deviceId) && this.fetchDeviceCommand == null) {
            showProgress((int) R.string.progress_text_loading_device_information);
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.mDeviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        if (isAdded()) {
            if (device != null) {
                if (this.fetchDeviceEventsCommand == null) {
                    this.fetchDeviceEventsCommand = new FetchEventsCommand(this, this.mDeviceId, "DEVICE");
                    this.fetchDeviceEventsCommand.execute();
                }
                if (this.fetchWateringEventsCommand == null) {
                    this.fetchWateringEventsCommand = new FetchEventsCommand(this, this.mDeviceId, "WATERING");
                    this.fetchWateringEventsCommand.execute();
                }
                this.fetchWateringEventsCommand = new FetchEventsCommand(this, this.mDeviceId, "SCHEDULE");
                this.fetchWateringEventsCommand.execute();
                if (device.scheduleExecution != null && device.scheduleExecution.isRunning()) {
                    this.currentlyWateringData.device = device;
                    this.mAdapter.updateCard(new CardInfo(ModelViewType.CURRENTLY_WATERING, this.currentlyWateringData, 0));
                    this.handler.removeCallbacks(this.updateCurrentlyWateringRunnable);
                    this.handler.postDelayed(this.updateCurrentlyWateringRunnable, 5000);
                }
            }
            hideProgress();
        }
    }

    public final synchronized void onEventsLoaded(String topic, List<Event> events) {
        if (StringUtils.equals(topic, "WATERING")) {
            this.mAdapter.updateCard(new CardInfo(ModelViewType.WATERING_HISTORY, events));
            this.fetchWateringEventsCommand = null;
        } else if (StringUtils.equals(topic, "SCHEDULE")) {
            this.mAdapter.updateCard(new CardInfo(ModelViewType.SCHEDULE_UPDATES, events));
            this.fetchScheduleEventsCommand = null;
        } else if (StringUtils.equals(topic, "DEVICE")) {
            this.mAdapter.updateCard(new CardInfo(ModelViewType.DEVICE_UPDATES, events));
            this.fetchDeviceEventsCommand = null;
        }
    }

    public final void toggleCurrentlyWateringDetails() {
        boolean z;
        CurrentlyWateringData currentlyWateringData = this.currentlyWateringData;
        if (this.currentlyWateringData.detailsShown) {
            z = false;
        } else {
            z = true;
        }
        currentlyWateringData.detailsShown = z;
        this.mAdapter.updateCard(new CardInfo(ModelViewType.CURRENTLY_WATERING, this.currentlyWateringData, 0));
    }
}
