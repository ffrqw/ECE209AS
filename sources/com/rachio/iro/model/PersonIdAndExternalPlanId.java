package com.rachio.iro.model;

import com.fasterxml.jackson.annotation.JsonView;

public class PersonIdAndExternalPlanId extends PostId {
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public String externalPlanId;

    public PersonIdAndExternalPlanId(String id, String externalPlanId) {
        super(id);
        this.externalPlanId = externalPlanId;
    }
}
