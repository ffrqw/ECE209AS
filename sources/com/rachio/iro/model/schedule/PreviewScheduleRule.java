package com.rachio.iro.model.schedule;

import com.rachio.iro.model.annotation.RestClientOptionOverrides;

@RestClientOptionOverrides(pathForPost = "/1/schedulerule/auto_schedule_rule")
public class PreviewScheduleRule extends ScheduleRule {
    public boolean previewed = false;
}
