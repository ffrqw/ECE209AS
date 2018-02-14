package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.Closeable;

public class InvalidFormatException extends JsonMappingException {
    private static final long serialVersionUID = 1;
    protected final Class<?> _targetType;
    protected final Object _value;

    @Deprecated
    public InvalidFormatException(String msg, Object value, Class<?> targetType) {
        super(null, msg);
        this._value = value;
        this._targetType = targetType;
    }

    @Deprecated
    public InvalidFormatException(String msg, JsonLocation loc, Object value, Class<?> targetType) {
        super(null, msg, loc);
        this._value = value;
        this._targetType = targetType;
    }

    public InvalidFormatException(JsonParser p, String msg, Object value, Class<?> targetType) {
        super((Closeable) p, msg);
        this._value = value;
        this._targetType = targetType;
    }

    public static InvalidFormatException from(JsonParser p, String msg, Object value, Class<?> targetType) {
        return new InvalidFormatException(p, msg, value, (Class) targetType);
    }

    public Object getValue() {
        return this._value;
    }

    public Class<?> getTargetType() {
        return this._targetType;
    }
}
