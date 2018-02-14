package com.rachio.iro.model.apionly;

public interface ErrorResponse {
    int getCode();

    String getError();

    boolean hasError();

    void setCode(int i);

    void setError(String str);
}
