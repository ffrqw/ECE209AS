package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;

@JacksonStdImpl
public final class StringDeserializer extends StdScalarDeserializer<String> {
    public static final StringDeserializer instance = new StringDeserializer();
    private static final long serialVersionUID = 1;

    public StringDeserializer() {
        super(String.class);
    }

    public final boolean isCachable() {
        return true;
    }

    public final String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return p.getText();
        }
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            p.nextToken();
            String parsed = _parseString(p, ctxt);
            if (p.nextToken() == JsonToken.END_ARRAY) {
                return parsed;
            }
            handleMissingEndArrayForSingle(p, ctxt);
            return parsed;
        } else if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (ob instanceof byte[]) {
                return ctxt.getBase64Variant().encode((byte[]) ob, false);
            }
            return ob.toString();
        } else {
            String text = p.getValueAsString();
            if (text != null) {
                return text;
            }
            return (String) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }

    public final String deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return deserialize(p, ctxt);
    }
}
