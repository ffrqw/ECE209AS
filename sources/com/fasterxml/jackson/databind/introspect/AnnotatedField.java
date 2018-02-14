package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public final class AnnotatedField extends AnnotatedMember implements Serializable {
    private static final long serialVersionUID = 1;
    protected final transient Field _field;
    protected Serialization _serialization;

    private static final class Serialization implements Serializable {
        private static final long serialVersionUID = 1;
        protected Class<?> clazz;
        protected String name;

        public Serialization(Field f) {
            this.clazz = f.getDeclaringClass();
            this.name = f.getName();
        }
    }

    public AnnotatedField(TypeResolutionContext contextClass, Field field, AnnotationMap annMap) {
        super(contextClass, annMap);
        this._field = field;
    }

    public final AnnotatedField withAnnotations(AnnotationMap ann) {
        return new AnnotatedField(this._typeContext, this._field, ann);
    }

    protected AnnotatedField(Serialization ser) {
        super(null, null);
        this._field = null;
        this._serialization = ser;
    }

    public final Field getAnnotated() {
        return this._field;
    }

    public final int getModifiers() {
        return this._field.getModifiers();
    }

    public final String getName() {
        return this._field.getName();
    }

    public final Class<?> getRawType() {
        return this._field.getType();
    }

    @Deprecated
    public final Type getGenericType() {
        return this._field.getGenericType();
    }

    public final JavaType getType() {
        return this._typeContext.resolveType(this._field.getGenericType());
    }

    public final Class<?> getDeclaringClass() {
        return this._field.getDeclaringClass();
    }

    public final Member getMember() {
        return this._field;
    }

    public final void setValue(Object pojo, Object value) throws IllegalArgumentException {
        try {
            this._field.set(pojo, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to setValue() for field " + getFullName() + ": " + e.getMessage(), e);
        }
    }

    public final Object getValue(Object pojo) throws IllegalArgumentException {
        try {
            return this._field.get(pojo);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to getValue() for field " + getFullName() + ": " + e.getMessage(), e);
        }
    }

    public final String getFullName() {
        return getDeclaringClass().getName() + "#" + getName();
    }

    public final int getAnnotationCount() {
        return this._annotations.size();
    }

    public final boolean isTransient() {
        return Modifier.isTransient(getModifiers());
    }

    public final int hashCode() {
        return this._field.getName().hashCode();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        if (((AnnotatedField) o)._field != this._field) {
            return false;
        }
        return true;
    }

    public final String toString() {
        return "[field " + getFullName() + "]";
    }

    final Object writeReplace() {
        return new AnnotatedField(new Serialization(this._field));
    }

    final Object readResolve() {
        Class<?> clazz = this._serialization.clazz;
        try {
            Field f = clazz.getDeclaredField(this._serialization.name);
            if (!f.isAccessible()) {
                ClassUtil.checkAndFixAccess(f, false);
            }
            return new AnnotatedField(null, f, null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz.getName());
        }
    }
}
