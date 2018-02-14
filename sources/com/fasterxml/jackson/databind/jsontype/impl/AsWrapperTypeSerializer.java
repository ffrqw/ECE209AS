package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import java.io.IOException;

public class AsWrapperTypeSerializer extends TypeSerializerBase {
    public AsWrapperTypeSerializer(TypeIdResolver idRes, BeanProperty property) {
        super(idRes, property);
    }

    public AsWrapperTypeSerializer forProperty(BeanProperty prop) {
        return this._property == prop ? this : new AsWrapperTypeSerializer(this._idResolver, prop);
    }

    public As getTypeInclusion() {
        return As.WRAPPER_OBJECT;
    }

    public void writeTypePrefixForObject(Object value, JsonGenerator g) throws IOException {
        String typeId = idFromValue(value);
        if (g.canWriteTypeId()) {
            if (typeId != null) {
                g.writeTypeId(typeId);
            }
            g.writeStartObject();
            return;
        }
        g.writeStartObject();
        g.writeObjectFieldStart(_validTypeId(typeId));
    }

    public void writeTypePrefixForObject(Object value, JsonGenerator g, Class<?> type) throws IOException {
        String typeId = idFromValueAndType(value, type);
        if (g.canWriteTypeId()) {
            if (typeId != null) {
                g.writeTypeId(typeId);
            }
            g.writeStartObject();
            return;
        }
        g.writeStartObject();
        g.writeObjectFieldStart(_validTypeId(typeId));
    }

    public void writeTypePrefixForArray(Object value, JsonGenerator g) throws IOException {
        String typeId = idFromValue(value);
        if (g.canWriteTypeId()) {
            if (typeId != null) {
                g.writeTypeId(typeId);
            }
            g.writeStartArray();
            return;
        }
        g.writeStartObject();
        g.writeArrayFieldStart(_validTypeId(typeId));
    }

    public void writeTypePrefixForArray(Object value, JsonGenerator g, Class<?> type) throws IOException {
        String typeId = idFromValueAndType(value, type);
        if (g.canWriteTypeId()) {
            if (typeId != null) {
                g.writeTypeId(typeId);
            }
            g.writeStartArray();
            return;
        }
        g.writeStartObject();
        g.writeArrayFieldStart(_validTypeId(typeId));
    }

    public void writeTypePrefixForScalar(Object value, JsonGenerator g) throws IOException {
        String typeId = idFromValue(value);
        if (!g.canWriteTypeId()) {
            g.writeStartObject();
            g.writeFieldName(_validTypeId(typeId));
        } else if (typeId != null) {
            g.writeTypeId(typeId);
        }
    }

    public void writeTypePrefixForScalar(Object value, JsonGenerator g, Class<?> type) throws IOException {
        String typeId = idFromValueAndType(value, type);
        if (!g.canWriteTypeId()) {
            g.writeStartObject();
            g.writeFieldName(_validTypeId(typeId));
        } else if (typeId != null) {
            g.writeTypeId(typeId);
        }
    }

    public void writeTypeSuffixForObject(Object value, JsonGenerator g) throws IOException {
        g.writeEndObject();
        if (!g.canWriteTypeId()) {
            g.writeEndObject();
        }
    }

    public void writeTypeSuffixForArray(Object value, JsonGenerator g) throws IOException {
        g.writeEndArray();
        if (!g.canWriteTypeId()) {
            g.writeEndObject();
        }
    }

    public void writeTypeSuffixForScalar(Object value, JsonGenerator g) throws IOException {
        if (!g.canWriteTypeId()) {
            g.writeEndObject();
        }
    }

    public void writeCustomTypePrefixForObject(Object value, JsonGenerator g, String typeId) throws IOException {
        if (g.canWriteTypeId()) {
            if (typeId != null) {
                g.writeTypeId(typeId);
            }
            g.writeStartObject();
            return;
        }
        g.writeStartObject();
        g.writeObjectFieldStart(_validTypeId(typeId));
    }

    public void writeCustomTypePrefixForArray(Object value, JsonGenerator g, String typeId) throws IOException {
        if (g.canWriteTypeId()) {
            if (typeId != null) {
                g.writeTypeId(typeId);
            }
            g.writeStartArray();
            return;
        }
        g.writeStartObject();
        g.writeArrayFieldStart(_validTypeId(typeId));
    }

    public void writeCustomTypePrefixForScalar(Object value, JsonGenerator g, String typeId) throws IOException {
        if (!g.canWriteTypeId()) {
            g.writeStartObject();
            g.writeFieldName(_validTypeId(typeId));
        } else if (typeId != null) {
            g.writeTypeId(typeId);
        }
    }

    public void writeCustomTypeSuffixForObject(Object value, JsonGenerator g, String typeId) throws IOException {
        if (!g.canWriteTypeId()) {
            writeTypeSuffixForObject(value, g);
        }
    }

    public void writeCustomTypeSuffixForArray(Object value, JsonGenerator g, String typeId) throws IOException {
        if (!g.canWriteTypeId()) {
            writeTypeSuffixForArray(value, g);
        }
    }

    public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator g, String typeId) throws IOException {
        if (!g.canWriteTypeId()) {
            writeTypeSuffixForScalar(value, g);
        }
    }

    protected String _validTypeId(String typeId) {
        return typeId == null ? "" : typeId;
    }
}
