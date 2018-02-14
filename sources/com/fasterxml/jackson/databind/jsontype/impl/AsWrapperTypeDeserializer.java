package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.io.Serializable;

public class AsWrapperTypeDeserializer extends TypeDeserializerBase implements Serializable {
    private static final long serialVersionUID = 1;

    public AsWrapperTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
    }

    protected AsWrapperTypeDeserializer(AsWrapperTypeDeserializer src, BeanProperty property) {
        super(src, property);
    }

    public TypeDeserializer forProperty(BeanProperty prop) {
        return prop == this._property ? this : new AsWrapperTypeDeserializer(this, prop);
    }

    public As getTypeInclusion() {
        return As.WRAPPER_OBJECT;
    }

    public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return _deserialize(jp, ctxt);
    }

    public Object deserializeTypedFromArray(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return _deserialize(jp, ctxt);
    }

    public Object deserializeTypedFromScalar(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return _deserialize(jp, ctxt);
    }

    public Object deserializeTypedFromAny(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return _deserialize(jp, ctxt);
    }

    protected Object _deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.canReadTypeId()) {
            Object typeId = p.getTypeId();
            if (typeId != null) {
                return _deserializeWithNativeTypeId(p, ctxt, typeId);
            }
        }
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            if (p.nextToken() != JsonToken.FIELD_NAME) {
                ctxt.reportWrongTokenException(p, JsonToken.FIELD_NAME, "need JSON String that contains type id (for subtype of " + baseTypeName() + ")", new Object[0]);
            }
        } else if (t != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(p, JsonToken.START_OBJECT, "need JSON Object to contain As.WRAPPER_OBJECT type information for class " + baseTypeName(), new Object[0]);
        }
        String typeId2 = p.getText();
        JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId2);
        p.nextToken();
        if (this._typeIdVisible && p.getCurrentToken() == JsonToken.START_OBJECT) {
            TokenBuffer tb = new TokenBuffer(null, false);
            tb.writeStartObject();
            tb.writeFieldName(this._typePropertyName);
            tb.writeString(typeId2);
            p.clearCurrentToken();
            p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
            p.nextToken();
        }
        Object value = deser.deserialize(p, ctxt);
        if (p.nextToken() == JsonToken.END_OBJECT) {
            return value;
        }
        ctxt.reportWrongTokenException(p, JsonToken.END_OBJECT, "expected closing END_OBJECT after type information and deserialized value", new Object[0]);
        return value;
    }
}
