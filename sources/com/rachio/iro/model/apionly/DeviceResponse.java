package com.rachio.iro.model.apionly;

import com.rachio.iro.model.device.Device;

public class DeviceResponse extends Device implements ErrorResponse {
    private static final long serialVersionUID = 1;
    private BaseResponse error = new BaseResponse();

    public void setError(String error) {
        this.error.setError(error);
    }

    public String getError() {
        return this.error.getError();
    }

    public void setCode(int code) {
        this.error.setCode(code);
    }

    public int getCode() {
        return this.error.getCode();
    }

    public boolean hasError() {
        return this.error.hasError();
    }
}
