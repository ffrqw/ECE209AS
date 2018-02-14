package com.rachio.iro.gen2.model;

import com.google.gson.annotations.SerializedName;

public class NetworkResponse extends BaseResponse {
    @SerializedName("error-code")
    public int error;
    public int success;
}
