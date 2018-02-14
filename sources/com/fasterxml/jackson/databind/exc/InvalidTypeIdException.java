package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.Closeable;

public class InvalidTypeIdException extends JsonMappingException {
    private static final long serialVersionUID = 1;
    protected final JavaType _baseType;
    protected final String _typeId;

    public InvalidTypeIdException(JsonParser p, String msg, JavaType baseType, String typeId) {
        super((Closeable) p, msg);
        this._baseType = baseType;
        this._typeId = typeId;
    }

    public static InvalidTypeIdException from(JsonParser p, String msg, JavaType baseType, String typeId) {
        return new InvalidTypeIdException(p, msg, baseType, typeId);
    }

    public JavaType getBaseType() {
        return this._baseType;
    }

    public String getTypeId() {
        return this._typeId;
    }
}
