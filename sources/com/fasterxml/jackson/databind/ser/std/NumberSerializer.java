package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

@JacksonStdImpl
public class NumberSerializer extends StdScalarSerializer<Number> {
    public static final NumberSerializer instance = new NumberSerializer(Number.class);
    protected final boolean _isInt;

    public NumberSerializer(Class<? extends Number> rawType) {
        boolean z = false;
        super(rawType, false);
        if (rawType == BigInteger.class) {
            z = true;
        }
        this._isInt = z;
    }

    public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (value instanceof BigDecimal) {
            g.writeNumber((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            g.writeNumber((BigInteger) value);
        } else if (value instanceof Long) {
            g.writeNumber(value.longValue());
        } else if (value instanceof Double) {
            g.writeNumber(value.doubleValue());
        } else if (value instanceof Float) {
            g.writeNumber(value.floatValue());
        } else if ((value instanceof Integer) || (value instanceof Byte) || (value instanceof Short)) {
            g.writeNumber(value.intValue());
        } else {
            g.writeNumber(value.toString());
        }
    }

    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode(this._isInt ? "integer" : "number", true);
    }

    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        if (this._isInt) {
            visitIntFormat(visitor, typeHint, NumberType.BIG_INTEGER);
        } else if (handledType() == BigDecimal.class) {
            visitFloatFormat(visitor, typeHint, NumberType.BIG_DECIMAL);
        } else {
            visitor.expectNumberFormat(typeHint);
        }
    }
}
