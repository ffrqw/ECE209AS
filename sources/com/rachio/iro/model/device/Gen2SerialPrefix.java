package com.rachio.iro.model.device;

import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.apionly.ErrorResponse;

@RestClientOptions(path = "/1/device/retrieve_serial_number_prefix")
public class Gen2SerialPrefix implements ErrorResponse {
    private int code;
    private String error;
    public String serialPrefix;

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return this.error;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public boolean hasError() {
        return false;
    }
}
