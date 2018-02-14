package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class FieldProperty extends SettableBeanProperty {
    private static final long serialVersionUID = 1;
    protected final AnnotatedField _annotated;
    protected final transient Field _field;

    public FieldProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedField field) {
        super(propDef, type, typeDeser, contextAnnotations);
        this._annotated = field;
        this._field = field.getAnnotated();
    }

    protected FieldProperty(FieldProperty src, JsonDeserializer<?> deser) {
        super((SettableBeanProperty) src, (JsonDeserializer) deser);
        this._annotated = src._annotated;
        this._field = src._field;
    }

    protected FieldProperty(FieldProperty src, PropertyName newName) {
        super((SettableBeanProperty) src, newName);
        this._annotated = src._annotated;
        this._field = src._field;
    }

    protected FieldProperty(FieldProperty src) {
        super(src);
        this._annotated = src._annotated;
        Field f = this._annotated.getAnnotated();
        if (f == null) {
            throw new IllegalArgumentException("Missing field (broken JDK (de)serialization?)");
        }
        this._field = f;
    }

    public final FieldProperty withName(PropertyName newName) {
        return new FieldProperty(this, newName);
    }

    public final FieldProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new FieldProperty(this, (JsonDeserializer) deser);
    }

    public final void fixAccess(DeserializationConfig config) {
        ClassUtil.checkAndFixAccess(this._field, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }

    public final <A extends Annotation> A getAnnotation(Class<A> acls) {
        return this._annotated == null ? null : this._annotated.getAnnotation(acls);
    }

    public final AnnotatedMember getMember() {
        return this._annotated;
    }

    public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        Object value = deserialize(p, ctxt);
        try {
            this._field.set(instance, value);
        } catch (Exception e) {
            _throwAsIOE(p, e, value);
        }
    }

    public final Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        Object value = deserialize(p, ctxt);
        try {
            this._field.set(instance, value);
        } catch (Exception e) {
            _throwAsIOE(p, e, value);
        }
        return instance;
    }

    public final void set(Object instance, Object value) throws IOException {
        try {
            this._field.set(instance, value);
        } catch (Exception e) {
            _throwAsIOE(e, value);
        }
    }

    public final Object setAndReturn(Object instance, Object value) throws IOException {
        try {
            this._field.set(instance, value);
        } catch (Exception e) {
            _throwAsIOE(e, value);
        }
        return instance;
    }

    final Object readResolve() {
        return new FieldProperty(this);
    }
}
