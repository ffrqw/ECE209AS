package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public final class WritableObjectId {
    public final ObjectIdGenerator<?> generator;
    public Object id;
    protected boolean idWritten = false;

    public WritableObjectId(ObjectIdGenerator<?> generator) {
        this.generator = generator;
    }

    public final boolean writeAsId(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w) throws IOException {
        if (this.id == null || (!this.idWritten && !w.alwaysAsId)) {
            return false;
        }
        if (gen.canWriteObjectId()) {
            gen.writeObjectRef(String.valueOf(this.id));
        } else {
            w.serializer.serialize(this.id, gen, provider);
        }
        return true;
    }

    public final Object generateId(Object forPojo) {
        if (this.id == null) {
            this.id = this.generator.generateId(forPojo);
        }
        return this.id;
    }

    public final void writeAsField(JsonGenerator gen, SerializerProvider provider, ObjectIdWriter w) throws IOException {
        this.idWritten = true;
        if (gen.canWriteObjectId()) {
            gen.writeObjectId(String.valueOf(this.id));
            return;
        }
        SerializableString name = w.propertyName;
        if (name != null) {
            gen.writeFieldName(name);
            w.serializer.serialize(this.id, gen, provider);
        }
    }
}
