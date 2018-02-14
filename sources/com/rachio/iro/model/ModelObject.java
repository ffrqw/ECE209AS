package com.rachio.iro.model;

import android.util.Log;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.mapping.JsonMapper;
import java.io.Serializable;

public abstract class ModelObject<T extends ModelObject> implements ErrorResponse, Serializable {
    private static final String TAG = ModelObject.class.getName();
    private static final long serialVersionUID = 1;
    public int code = 0;
    public String error;

    protected ModelObject() {
    }

    public String toString() {
        return JsonMapper.toJsonPretty(this);
    }

    @JsonIgnore
    public boolean isValid() {
        return true;
    }

    @JsonIgnore
    public T getTransmittableVersion() {
        return null;
    }

    public static <T extends ModelObject> T transmittableClone(Class<T> type, T source) {
        Log.d(TAG, "full " + JsonMapper.toJson(source));
        String json = JsonMapper.toJson(source, TransmittableView.class);
        Log.d(TAG, "transmittable json " + json);
        return (ModelObject) JsonMapper.fromJson(json, type);
    }

    public static <T extends ModelObject> T deepClone(Class<T> type, T source) {
        return deepClone(type, source, false);
    }

    public static <T extends ModelObject> T deepClone(Class<T> type, T source, boolean lax) {
        return (ModelObject) JsonMapper.fromJson(JsonMapper.toJson(source), type, lax);
    }

    public static int deepCompare(ModelObject lhs, ModelObject rhs) {
        return JsonMapper.toJson(lhs).compareTo(JsonMapper.toJson(rhs));
    }

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
        return this.code != 0;
    }
}
