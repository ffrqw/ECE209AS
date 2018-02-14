package com.rachio.iro.model.apionly;

import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(path = "/1/flexschedulerule/flex_schedule_rule_run_now")
public class RunFlexScheduleRuleRequest extends BaseRunScheduleRuleRequest {
    public RunFlexScheduleRuleRequest(String scheduleRuleId) {
        super(scheduleRuleId);
    }
}
