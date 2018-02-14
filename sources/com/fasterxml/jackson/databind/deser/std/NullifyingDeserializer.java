package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;

public class NullifyingDeserializer extends StdDeserializer<Object> {
    public static final NullifyingDeserializer instance = new NullifyingDeserializer();
    private static final long serialVersionUID = 1;

    public NullifyingDeserializer() {
        super(Object.class);
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.FIELD_NAME)) {
            while (true) {
                JsonToken t = p.nextToken();
                if (t == null || t == JsonToken.END_OBJECT) {
                    break;
                }
                p.skipChildren();
            }
        } else {
            p.skipChildren();
        }
        return null;
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 1:
            case 3:
            case 5:
                return typeDeserializer.deserializeTypedFromAny(p, ctxt);
            default:
                return null;
        }
    }
}
