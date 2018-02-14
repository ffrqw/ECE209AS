package com.rachio.iro.async.command;

import com.rachio.iro.model.Event;
import com.rachio.iro.model.Event.Type;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.reporting.WaterUseResponse;
import com.rachio.iro.model.reporting.WaterUseResponse.Result;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.user.User.DisplayUnit;
import com.rachio.iro.reporting.ReportingUtils;
import com.rachio.iro.utils.CalendarUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FetchWaterUsageCommand extends CommandThatMayNeedToPullADevice<WaterUsageHolder> {
    private final String deviceId;
    private final FetchWaterUsageListener listener;

    public interface FetchWaterUsageListener {
        void onWaterUsageLoaded(WaterUsageHolder waterUsageHolder);
    }

    public static final class WaterUsageHolder implements Serializable {
        public DisplayUnit displayUnit;
        public double gallonsSaved;
        public double gallonsSavedLast;
        public double gallonsUsed;
        public double gallonsUsedLast;
        public long timestamp = System.currentTimeMillis();
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        WaterUsageHolder waterUsageHolder = (WaterUsageHolder) obj;
        if (this.listener != null) {
            this.listener.onWaterUsageLoaded(waterUsageHolder);
        }
    }

    public FetchWaterUsageCommand(FetchWaterUsageListener listener, String deviceId) {
        this.listener = listener;
        this.deviceId = deviceId;
        BaseCommand.component(listener).inject(this);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        Device fetchDevice = fetchDevice(this.deviceId);
        if (fetchDevice != null) {
            long j;
            long time;
            User user = (User) this.database.find(User.class, fetchDevice.getLocalUser().id);
            Calendar instance = Calendar.getInstance();
            CalendarUtil.setToEndOfMonth(instance);
            Date time2 = instance.getTime();
            instance.add(2, -1);
            CalendarUtil.setToStartOfMonth(instance);
            Date time3 = instance.getTime();
            FetchEventsCommand.fetchEvents(this.database, this.prefsWrapper, this.restClient, this.deviceId, "SCHEDULE", 0, 1);
            List findEventsByDevice = Event.findEventsByDevice(this.database, this.deviceId, Type.WEATHER_INTELLIGENCE);
            if (findEventsByDevice == null || findEventsByDevice.size() <= 0) {
                j = -1;
            } else {
                j = ((Event) findEventsByDevice.get(0)).eventDate.getTime();
            }
            Date lastRunDate = fetchDevice.getLastRunDate();
            if (lastRunDate != null) {
                time = lastRunDate.getTime();
            } else {
                time = 0;
            }
            ArrayList monthlyUseData = ReportingUtils.getMonthlyUseData(this.database, this.deviceId, Math.max(time, j), time3, time2);
            if (monthlyUseData != null) {
                WaterUsageHolder waterUsageHolder = new WaterUsageHolder();
                if (monthlyUseData.size() == 2 && ((WaterUseResponse) monthlyUseData.get(0)).result.size() == 2 && ((WaterUseResponse) monthlyUseData.get(1)).result.size() == 2) {
                    waterUsageHolder.gallonsUsedLast = ((Result) ((WaterUseResponse) monthlyUseData.get(0)).result.get(0)).value;
                    waterUsageHolder.gallonsSavedLast = ((Result) ((WaterUseResponse) monthlyUseData.get(1)).result.get(0)).value;
                    waterUsageHolder.gallonsUsed = ((Result) ((WaterUseResponse) monthlyUseData.get(0)).result.get(1)).value;
                    waterUsageHolder.gallonsSaved = ((Result) ((WaterUseResponse) monthlyUseData.get(1)).result.get(1)).value;
                } else if (monthlyUseData.size() == 2 && ((WaterUseResponse) monthlyUseData.get(0)).result.size() == 1 && ((WaterUseResponse) monthlyUseData.get(1)).result.size() == 1) {
                    waterUsageHolder.gallonsUsed = ((Result) ((WaterUseResponse) monthlyUseData.get(0)).result.get(0)).value;
                    waterUsageHolder.gallonsSaved = ((Result) ((WaterUseResponse) monthlyUseData.get(1)).result.get(0)).value;
                }
                waterUsageHolder.displayUnit = user.displayUnit;
                return waterUsageHolder;
            }
        }
        return null;
    }
}
