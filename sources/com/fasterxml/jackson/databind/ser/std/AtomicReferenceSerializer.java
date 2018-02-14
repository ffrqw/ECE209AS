package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceSerializer extends ReferenceTypeSerializer<AtomicReference<?>> {
    private static final long serialVersionUID = 1;

    public AtomicReferenceSerializer(ReferenceType fullType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> ser) {
        super(fullType, staticTyping, vts, ser);
    }

    protected AtomicReferenceSerializer(AtomicReferenceSerializer base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, Include contentIncl) {
        super(base, property, vts, valueSer, unwrapper, contentIncl);
    }

    protected AtomicReferenceSerializer withResolved(BeanProperty prop, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, Include contentIncl) {
        return (this._property == prop && contentIncl == this._contentInclusion && this._valueTypeSerializer == vts && this._valueSerializer == valueSer && this._unwrapper == unwrapper) ? this : new AtomicReferenceSerializer(this, prop, vts, valueSer, unwrapper, contentIncl);
    }

    protected boolean _isValueEmpty(AtomicReference<?> value) {
        return value.get() == null;
    }

    protected Object _getReferenced(AtomicReference<?> value) {
        return value.get();
    }

    protected Object _getReferencedIfPresent(AtomicReference<?> value) {
        return value.get();
    }
}
