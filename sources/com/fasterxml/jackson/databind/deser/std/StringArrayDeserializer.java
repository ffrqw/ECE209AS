package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;

@JacksonStdImpl
public final class StringArrayDeserializer extends StdDeserializer<String[]> implements ContextualDeserializer {
    public static final StringArrayDeserializer instance = new StringArrayDeserializer();
    private static final long serialVersionUID = 2;
    protected JsonDeserializer<String> _elementDeserializer;
    protected final Boolean _unwrapSingle;

    public StringArrayDeserializer() {
        this(null, null);
    }

    protected StringArrayDeserializer(JsonDeserializer<?> deser, Boolean unwrapSingle) {
        super(String[].class);
        this._elementDeserializer = deser;
        this._unwrapSingle = unwrapSingle;
    }

    public final JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser = findConvertingContentDeserializer(ctxt, property, this._elementDeserializer);
        JavaType type = ctxt.constructType(String.class);
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(type, property);
        } else {
            deser = ctxt.handleSecondaryContextualization(deser, property, type);
        }
        Boolean unwrapSingle = findFormatFeature(ctxt, property, String[].class, Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        if (deser != null && isDefaultDeserializer(deser)) {
            deser = null;
        }
        if (this._elementDeserializer == deser && this._unwrapSingle == unwrapSingle) {
            return this;
        }
        this(deser, unwrapSingle);
        return this;
    }

    public final String[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt);
        }
        if (this._elementDeserializer != null) {
            return _deserializeCustom(p, ctxt);
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        int ix = 0;
        while (true) {
            try {
                int ix2;
                String value = p.nextTextValue();
                if (value == null) {
                    JsonToken t = p.getCurrentToken();
                    if (t == JsonToken.END_ARRAY) {
                        String[] result = (String[]) buffer.completeAndClearBuffer(chunk, ix, String.class);
                        ctxt.returnObjectBuffer(buffer);
                        return result;
                    } else if (t != JsonToken.VALUE_NULL) {
                        value = _parseString(p, ctxt);
                    }
                }
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix2 = 0;
                } else {
                    ix2 = ix;
                }
                ix = ix2 + 1;
                chunk[ix2] = value;
            } catch (Throwable e) {
                throw JsonMappingException.wrapWithPath(e, (Object) chunk, buffer.bufferedSize() + ix);
            }
        }
    }

    protected final String[] _deserializeCustom(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        JsonDeserializer<String> deser = this._elementDeserializer;
        int ix = 0;
        while (true) {
            String value;
            int ix2;
            if (p.nextTextValue() == null) {
                JsonToken t = p.getCurrentToken();
                if (t == JsonToken.END_ARRAY) {
                    String[] result = (String[]) buffer.completeAndClearBuffer(chunk, ix, String.class);
                    ctxt.returnObjectBuffer(buffer);
                    return result;
                } else if (t == JsonToken.VALUE_NULL) {
                    value = (String) deser.getNullValue(ctxt);
                } else {
                    try {
                        value = (String) deser.deserialize(p, ctxt);
                    } catch (Throwable e) {
                        throw JsonMappingException.wrapWithPath(e, (Object) String.class, ix);
                    }
                }
            }
            value = (String) deser.deserialize(p, ctxt);
            if (ix >= chunk.length) {
                chunk = buffer.appendCompletedChunk(chunk);
                ix2 = 0;
            } else {
                ix2 = ix;
            }
            ix = ix2 + 1;
            chunk[ix2] = value;
        }
    }

    public final Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    private final String[] handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        boolean canWrap;
        String[] strArr = null;
        if (this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY))) {
            canWrap = true;
        } else {
            canWrap = false;
        }
        if (canWrap) {
            String[] strArr2 = new String[1];
            if (!p.hasToken(JsonToken.VALUE_NULL)) {
                strArr = _parseString(p, ctxt);
            }
            strArr2[0] = strArr;
            return strArr2;
        } else if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && p.getText().length() == 0) {
            return null;
        } else {
            return (String[]) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }
}
