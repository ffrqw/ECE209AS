package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.device.Zone;
import java.util.ArrayList;
import java.util.List;

@RestClientOptions(path = "/1/device/manual_run_multiple_durations")
public class RunMultipleZonesRequest extends ModelObject {
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public final String deviceId;
    @JsonView({TransmittableView.class})
    public final List<ZoneRunDuration> zoneRunDurations = new ArrayList();

    public static final class ZoneRunDuration extends ModelObject {
        private static final long serialVersionUID = 1;
        @JsonView({TransmittableView.class})
        public final int duration;
        @JsonView({TransmittableView.class})
        public final int zoneNumber;

        public ZoneRunDuration(int zoneNumber, int duration) {
            this.zoneNumber = zoneNumber;
            this.duration = duration;
        }
    }

    public RunMultipleZonesRequest(String deviceId, List<Zone> zones, List<Integer> durations) {
        this.deviceId = deviceId;
        for (int i = 0; i < zones.size(); i++) {
            this.zoneRunDurations.add(new ZoneRunDuration(((Zone) zones.get(i)).zoneNumber, ((Integer) durations.get(i)).intValue()));
        }
    }
}
