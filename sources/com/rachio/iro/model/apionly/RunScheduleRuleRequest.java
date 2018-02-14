package com.rachio.iro.model.apionly;

import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(path = "/1/schedulerule/schedule_rule_run")
public class RunScheduleRuleRequest extends BaseRunScheduleRuleRequest {
    public RunScheduleRuleRequest(String scheduleRuleId) {
        super(scheduleRuleId);
    }
}
