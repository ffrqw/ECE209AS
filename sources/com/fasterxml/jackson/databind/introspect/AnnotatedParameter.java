package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

public final class AnnotatedParameter extends AnnotatedMember {
    private static final long serialVersionUID = 1;
    protected final int _index;
    protected final AnnotatedWithParams _owner;
    protected final JavaType _type;

    public AnnotatedParameter(AnnotatedWithParams owner, JavaType type, AnnotationMap annotations, int index) {
        super(owner == null ? null : owner.getTypeContext(), annotations);
        this._owner = owner;
        this._type = type;
        this._index = index;
    }

    public final AnnotatedParameter withAnnotations(AnnotationMap ann) {
        return ann == this._annotations ? this : this._owner.replaceParameterAnnotations(this._index, ann);
    }

    public final AnnotatedElement getAnnotated() {
        return null;
    }

    public final int getModifiers() {
        return this._owner.getModifiers();
    }

    public final String getName() {
        return "";
    }

    public final Class<?> getRawType() {
        return this._type.getRawClass();
    }

    public final JavaType getType() {
        return this._type;
    }

    @Deprecated
    public final Type getGenericType() {
        return this._owner.getGenericParameterType(this._index);
    }

    public final Class<?> getDeclaringClass() {
        return this._owner.getDeclaringClass();
    }

    public final Member getMember() {
        return this._owner.getMember();
    }

    public final void setValue(Object pojo, Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call setValue() on constructor parameter of " + getDeclaringClass().getName());
    }

    public final Object getValue(Object pojo) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call getValue() on constructor parameter of " + getDeclaringClass().getName());
    }

    public final Type getParameterType() {
        return this._type;
    }

    public final AnnotatedWithParams getOwner() {
        return this._owner;
    }

    public final int getIndex() {
        return this._index;
    }

    public final int hashCode() {
        return this._owner.hashCode() + this._index;
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        AnnotatedParameter other = (AnnotatedParameter) o;
        if (other._owner.equals(this._owner) && other._index == this._index) {
            return true;
        }
        return false;
    }

    public final String toString() {
        return "[parameter #" + getIndex() + ", annotations: " + this._annotations + "]";
    }
}
