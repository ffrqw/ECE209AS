package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;

public class BaseRunScheduleRuleRequest extends ModelObject {
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public String scheduleRuleId;

    public BaseRunScheduleRuleRequest(String scheduleRuleId) {
        this.scheduleRuleId = scheduleRuleId;
    }
}
