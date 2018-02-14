package com.rachio.iro.model.apionly;

import com.rachio.iro.model.schedule.ScheduleExecution;

public class ScheduleExecutionResponse extends ScheduleExecution implements ErrorResponse {
    private static final long serialVersionUID = 1;

    public void setError(String error) {
    }

    public String getError() {
        return null;
    }

    public void setCode(int code) {
    }

    public int getCode() {
        return 0;
    }

    public boolean hasError() {
        return false;
    }
}
