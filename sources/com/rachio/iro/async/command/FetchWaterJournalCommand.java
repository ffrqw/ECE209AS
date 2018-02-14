package com.rachio.iro.async.command;

import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.WaterJournal;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import java.util.TimeZone;

public class FetchWaterJournalCommand extends BaseCommand<WaterJournalData> {
    private Listener listener;
    private final String zoneId;

    public interface Listener {
        void onWaterJournalFetched(WaterJournalData waterJournalData);
    }

    public static final class WaterJournalData {
        public double moistureLevel;

        public WaterJournalData(double moistureLevel) {
            this.moistureLevel = moistureLevel;
        }
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        this.listener.onWaterJournalFetched((WaterJournalData) obj);
    }

    public FetchWaterJournalCommand(Listener listener, String zoneId) {
        if (zoneId == null) {
            throw new IllegalArgumentException();
        }
        this.listener = listener;
        this.zoneId = zoneId;
        BaseCommand.component(listener).inject(this);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        Zone zone = (Zone) this.database.find(Zone.class, this.zoneId);
        Device device = (Device) this.database.find(Device.class, zone.device.id);
        HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
        WaterJournal waterJournal = (WaterJournal) this.restClient.getObjectById(this.database, this.zoneId, WaterJournal.class, httpResponseErrorHandler);
        if (httpResponseErrorHandler.hasError || waterJournal == null) {
            return null;
        }
        return new WaterJournalData(waterJournal.getMoistureLevelForToday(TimeZone.getTimeZone(device.timeZone), zone.depthOfWater));
    }
}
