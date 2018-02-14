package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(path = "/1/device/rain_delay")
public class RainDelayRequest extends ModelObject {
    @JsonView({TransmittableView.class})
    public final String deviceId;
    @JsonView({TransmittableView.class})
    public final long time;

    public RainDelayRequest(String deviceId, long time) {
        this.deviceId = deviceId;
        this.time = time;
    }
}
