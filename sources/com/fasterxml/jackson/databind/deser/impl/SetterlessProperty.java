package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class SetterlessProperty extends SettableBeanProperty {
    private static final long serialVersionUID = 1;
    protected final AnnotatedMethod _annotated;
    protected final Method _getter;

    public SetterlessProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedMethod method) {
        super(propDef, type, typeDeser, contextAnnotations);
        this._annotated = method;
        this._getter = method.getAnnotated();
    }

    protected SetterlessProperty(SetterlessProperty src, JsonDeserializer<?> deser) {
        super((SettableBeanProperty) src, (JsonDeserializer) deser);
        this._annotated = src._annotated;
        this._getter = src._getter;
    }

    protected SetterlessProperty(SetterlessProperty src, PropertyName newName) {
        super((SettableBeanProperty) src, newName);
        this._annotated = src._annotated;
        this._getter = src._getter;
    }

    public final SetterlessProperty withName(PropertyName newName) {
        return new SetterlessProperty(this, newName);
    }

    public final SetterlessProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SetterlessProperty(this, (JsonDeserializer) deser);
    }

    public final void fixAccess(DeserializationConfig config) {
        this._annotated.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }

    public final <A extends Annotation> A getAnnotation(Class<A> acls) {
        return this._annotated.getAnnotation(acls);
    }

    public final AnnotatedMember getMember() {
        return this._annotated;
    }

    public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        if (p.getCurrentToken() != JsonToken.VALUE_NULL) {
            if (this._valueTypeDeserializer != null) {
                ctxt.reportMappingException("Problem deserializing 'setterless' property (\"%s\"): no way to handle typed deser with setterless yet", getName());
            }
            try {
                Object toModify = this._getter.invoke(instance, new Object[0]);
                if (toModify == null) {
                    throw JsonMappingException.from(p, "Problem deserializing 'setterless' property '" + getName() + "': get method returned null");
                }
                this._valueDeserializer.deserialize(p, ctxt, toModify);
            } catch (Exception e) {
                _throwAsIOE(p, e);
            }
        }
    }

    public final Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        deserializeAndSet(p, ctxt, instance);
        return instance;
    }

    public final void set(Object instance, Object value) throws IOException {
        throw new UnsupportedOperationException("Should never call 'set' on setterless property");
    }

    public final Object setAndReturn(Object instance, Object value) throws IOException {
        set(instance, value);
        return null;
    }
}
