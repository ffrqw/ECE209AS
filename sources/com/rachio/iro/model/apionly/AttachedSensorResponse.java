package com.rachio.iro.model.apionly;

import com.rachio.iro.model.device.Device.AttachedSensor;

public class AttachedSensorResponse extends AttachedSensor implements ErrorResponse {
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
