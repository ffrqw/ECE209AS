package com.rachio.iro.model;

import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;

public class PostId implements Serializable {
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public String id;

    public PostId(String deviceId) {
        this.id = deviceId;
    }
}
