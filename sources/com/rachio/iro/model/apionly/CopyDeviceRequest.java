package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(path = "/1/device/copy")
public class CopyDeviceRequest extends ModelObject {
    @JsonView({TransmittableView.class})
    public String destinationId;
    @JsonView({TransmittableView.class})
    public String sourceId;

    public CopyDeviceRequest(String sourceId, String destinationId) {
        this.sourceId = sourceId;
        this.destinationId = destinationId;
    }
}
