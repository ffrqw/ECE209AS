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
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;
import java.lang.reflect.Array;

@JacksonStdImpl
public class ObjectArrayDeserializer extends ContainerDeserializerBase<Object[]> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected final ArrayType _arrayType;
    protected final Class<?> _elementClass;
    protected JsonDeserializer<Object> _elementDeserializer;
    protected final TypeDeserializer _elementTypeDeserializer;
    protected final boolean _untyped;
    protected final Boolean _unwrapSingle;

    public ObjectArrayDeserializer(ArrayType arrayType, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser) {
        super(arrayType);
        this._arrayType = arrayType;
        this._elementClass = arrayType.getContentType().getRawClass();
        this._untyped = this._elementClass == Object.class;
        this._elementDeserializer = elemDeser;
        this._elementTypeDeserializer = elemTypeDeser;
        this._unwrapSingle = null;
    }

    protected ObjectArrayDeserializer(ObjectArrayDeserializer base, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser, Boolean unwrapSingle) {
        super(base._arrayType);
        this._arrayType = base._arrayType;
        this._elementClass = base._elementClass;
        this._untyped = base._untyped;
        this._elementDeserializer = elemDeser;
        this._elementTypeDeserializer = elemTypeDeser;
        this._unwrapSingle = unwrapSingle;
    }

    public ObjectArrayDeserializer withDeserializer(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser) {
        return withResolved(elemTypeDeser, elemDeser, this._unwrapSingle);
    }

    public ObjectArrayDeserializer withResolved(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser, Boolean unwrapSingle) {
        return (unwrapSingle == this._unwrapSingle && elemDeser == this._elementDeserializer && elemTypeDeser == this._elementTypeDeserializer) ? this : new ObjectArrayDeserializer(this, elemDeser, elemTypeDeser, unwrapSingle);
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser = this._elementDeserializer;
        Boolean unwrapSingle = findFormatFeature(ctxt, property, this._arrayType.getRawClass(), Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        deser = findConvertingContentDeserializer(ctxt, property, deser);
        JavaType vt = this._arrayType.getContentType();
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(vt, property);
        } else {
            deser = ctxt.handleSecondaryContextualization(deser, property, vt);
        }
        TypeDeserializer elemTypeDeser = this._elementTypeDeserializer;
        if (elemTypeDeser != null) {
            elemTypeDeser = elemTypeDeser.forProperty(property);
        }
        return withResolved(elemTypeDeser, deser, unwrapSingle);
    }

    public boolean isCachable() {
        return this._elementDeserializer == null && this._elementTypeDeserializer == null;
    }

    public JavaType getContentType() {
        return this._arrayType.getContentType();
    }

    public JsonDeserializer<Object> getContentDeserializer() {
        return this._elementDeserializer;
    }

    public Object[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt);
        }
        Object[] result;
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        int ix = 0;
        TypeDeserializer typeDeser = this._elementTypeDeserializer;
        while (true) {
            JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                break;
            }
            Object value;
            int ix2;
            if (t == JsonToken.VALUE_NULL) {
                value = this._elementDeserializer.getNullValue(ctxt);
            } else if (typeDeser == null) {
                try {
                    value = this._elementDeserializer.deserialize(p, ctxt);
                } catch (Throwable e) {
                    throw JsonMappingException.wrapWithPath(e, (Object) chunk, buffer.bufferedSize() + ix);
                }
            } else {
                value = this._elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
            }
            if (ix >= chunk.length) {
                chunk = buffer.appendCompletedChunk(chunk);
                ix2 = 0;
            } else {
                ix2 = ix;
            }
            ix = ix2 + 1;
            chunk[ix2] = value;
        }
        if (this._untyped) {
            result = buffer.completeAndClearBuffer(chunk, ix);
        } else {
            result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
        }
        ctxt.returnObjectBuffer(buffer);
        return result;
    }

    public Object[] deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return (Object[]) typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    protected Byte[] deserializeFromBase64(JsonParser p, DeserializationContext ctxt) throws IOException {
        byte[] b = p.getBinaryValue(ctxt.getBase64Variant());
        Byte[] result = new Byte[b.length];
        int len = b.length;
        for (int i = 0; i < len; i++) {
            result[i] = Byte.valueOf(b[i]);
        }
        return result;
    }

    protected Object[] handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && p.getText().length() == 0) {
            return null;
        }
        boolean canWrap;
        if (this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY))) {
            canWrap = true;
        } else {
            canWrap = false;
        }
        if (canWrap) {
            Object value;
            Object[] result;
            if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
                value = this._elementDeserializer.getNullValue(ctxt);
            } else if (this._elementTypeDeserializer == null) {
                value = this._elementDeserializer.deserialize(p, ctxt);
            } else {
                value = this._elementDeserializer.deserializeWithType(p, ctxt, this._elementTypeDeserializer);
            }
            if (this._untyped) {
                result = new Object[1];
            } else {
                result = (Object[]) Array.newInstance(this._elementClass, 1);
            }
            result[0] = value;
            return result;
        } else if (p.getCurrentToken() == JsonToken.VALUE_STRING && this._elementClass == Byte.class) {
            return deserializeFromBase64(p, ctxt);
        } else {
            return (Object[]) ctxt.handleUnexpectedToken(this._arrayType.getRawClass(), p);
        }
    }
}
