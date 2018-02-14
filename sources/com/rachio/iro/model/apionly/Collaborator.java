package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(path = "/1/device/collaborator")
public class Collaborator extends ModelObject {
    @JsonView({TransmittableView.class})
    public String deviceId;
    @JsonView({TransmittableView.class})
    public String email;

    public Collaborator(String email, String deviceId) {
        this.email = email;
        this.deviceId = deviceId;
    }
}
