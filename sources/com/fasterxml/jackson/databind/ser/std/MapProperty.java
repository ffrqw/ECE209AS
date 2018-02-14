package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.lang.annotation.Annotation;

public class MapProperty extends PropertyWriter {
    private static final long serialVersionUID = 1;
    protected Object _key;
    protected JsonSerializer<Object> _keySerializer;
    protected final BeanProperty _property;
    protected final TypeSerializer _typeSerializer;
    protected JsonSerializer<Object> _valueSerializer;

    public MapProperty(TypeSerializer typeSer, BeanProperty prop) {
        super(prop == null ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : prop.getMetadata());
        this._typeSerializer = typeSer;
        this._property = prop;
    }

    public void reset(Object key, JsonSerializer<Object> keySer, JsonSerializer<Object> valueSer) {
        this._key = key;
        this._keySerializer = keySer;
        this._valueSerializer = valueSer;
    }

    public String getName() {
        if (this._key instanceof String) {
            return (String) this._key;
        }
        return String.valueOf(this._key);
    }

    public PropertyName getFullName() {
        return new PropertyName(getName());
    }

    public <A extends Annotation> A getAnnotation(Class<A> acls) {
        return this._property == null ? null : this._property.getAnnotation(acls);
    }

    public <A extends Annotation> A getContextAnnotation(Class<A> acls) {
        return this._property == null ? null : this._property.getContextAnnotation(acls);
    }

    public void serializeAsField(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        this._keySerializer.serialize(this._key, gen, provider);
        if (this._typeSerializer == null) {
            this._valueSerializer.serialize(value, gen, provider);
        } else {
            this._valueSerializer.serializeWithType(value, gen, provider, this._typeSerializer);
        }
    }

    public void serializeAsOmittedField(Object value, JsonGenerator gen, SerializerProvider provider) throws Exception {
        if (!gen.canOmitFields()) {
            gen.writeOmittedField(getName());
        }
    }

    public void serializeAsElement(Object value, JsonGenerator gen, SerializerProvider provider) throws Exception {
        if (this._typeSerializer == null) {
            this._valueSerializer.serialize(value, gen, provider);
        } else {
            this._valueSerializer.serializeWithType(value, gen, provider, this._typeSerializer);
        }
    }

    public void serializeAsPlaceholder(Object value, JsonGenerator gen, SerializerProvider provider) throws Exception {
        gen.writeNull();
    }

    public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) throws JsonMappingException {
        if (this._property != null) {
            this._property.depositSchemaProperty(objectVisitor, provider);
        }
    }

    @Deprecated
    public void depositSchemaProperty(ObjectNode propertiesNode, SerializerProvider provider) throws JsonMappingException {
    }

    public JavaType getType() {
        return this._property == null ? TypeFactory.unknownType() : this._property.getType();
    }

    public PropertyName getWrapperName() {
        return this._property == null ? null : this._property.getWrapperName();
    }

    public AnnotatedMember getMember() {
        return this._property == null ? null : this._property.getMember();
    }
}
