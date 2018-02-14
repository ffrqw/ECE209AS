package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class SettableAnyProperty implements Serializable {
    private static final long serialVersionUID = 1;
    protected final BeanProperty _property;
    protected final AnnotatedMember _setter;
    final boolean _setterIsField;
    protected final JavaType _type;
    protected JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;

    private static class AnySetterReferring extends Referring {
        private final SettableAnyProperty _parent;
        private final Object _pojo;
        private final String _propName;

        public AnySetterReferring(SettableAnyProperty parent, UnresolvedForwardReference reference, Class<?> type, Object instance, String propName) {
            super(reference, (Class) type);
            this._parent = parent;
            this._pojo = instance;
            this._propName = propName;
        }

        public void handleResolvedForwardReference(Object id, Object value) throws IOException {
            if (hasId(id)) {
                this._parent.set(this._pojo, this._propName, value);
                return;
            }
            throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id.toString() + "] that wasn't previously registered.");
        }
    }

    public SettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
        this._property = property;
        this._setter = setter;
        this._type = type;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = typeDeser;
        this._setterIsField = setter instanceof AnnotatedField;
    }

    protected SettableAnyProperty(SettableAnyProperty src) {
        this._property = src._property;
        this._setter = src._setter;
        this._type = src._type;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._setterIsField = src._setterIsField;
    }

    public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
        return new SettableAnyProperty(this._property, this._setter, this._type, deser, this._valueTypeDeserializer);
    }

    public void fixAccess(DeserializationConfig config) {
        this._setter.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }

    Object readResolve() {
        if (this._setter != null && this._setter.getAnnotated() != null) {
            return this;
        }
        throw new IllegalArgumentException("Missing method (broken JDK (de)serialization?)");
    }

    public BeanProperty getProperty() {
        return this._property;
    }

    public boolean hasValueDeserializer() {
        return this._valueDeserializer != null;
    }

    public JavaType getType() {
        return this._type;
    }

    public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance, String propName) throws IOException {
        try {
            set(instance, propName, deserialize(p, ctxt));
        } catch (Throwable reference) {
            if (this._valueDeserializer.getObjectIdReader() == null) {
                throw JsonMappingException.from(p, "Unresolved forward reference but no identity info.", reference);
            }
            reference.getRoid().appendReferring(new AnySetterReferring(this, reference, this._type.getRawClass(), instance, propName));
        }
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return this._valueDeserializer.getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer != null) {
            return this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        }
        return this._valueDeserializer.deserialize(p, ctxt);
    }

    public void set(Object instance, String propName, Object value) throws IOException {
        try {
            if (this._setterIsField) {
                Map<Object, Object> val = (Map) this._setter.getValue(instance);
                if (val != null) {
                    val.put(propName, value);
                    return;
                }
                return;
            }
            ((AnnotatedMethod) this._setter).callOnWith(instance, propName, value);
        } catch (Exception e) {
            _throwAsIOE(e, propName, value);
        }
    }

    protected void _throwAsIOE(Exception e, String propName, Object value) throws IOException {
        if (e instanceof IllegalArgumentException) {
            String actType = value == null ? "[NULL]" : value.getClass().getName();
            StringBuilder msg = new StringBuilder("Problem deserializing \"any\" property '").append(propName);
            msg.append("' of class " + getClassName() + " (expected type: ").append(this._type);
            msg.append("; actual type: ").append(actType).append(")");
            String origMsg = e.getMessage();
            if (origMsg != null) {
                msg.append(", problem: ").append(origMsg);
            } else {
                msg.append(" (no error message provided)");
            }
            throw new JsonMappingException(null, msg.toString(), (Throwable) e);
        } else if (e instanceof IOException) {
            throw ((IOException) e);
        } else if (e instanceof RuntimeException) {
            throw ((RuntimeException) e);
        } else {
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            throw new JsonMappingException(null, t.getMessage(), t);
        }
    }

    private String getClassName() {
        return this._setter.getDeclaringClass().getName();
    }

    public String toString() {
        return "[any property on class " + getClassName() + "]";
    }
}
