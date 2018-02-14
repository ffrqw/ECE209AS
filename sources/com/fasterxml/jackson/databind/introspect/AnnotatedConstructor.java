package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

public final class AnnotatedConstructor extends AnnotatedWithParams {
    private static final long serialVersionUID = 1;
    protected final Constructor<?> _constructor;
    protected Serialization _serialization;

    private static final class Serialization implements Serializable {
        private static final long serialVersionUID = 1;
        protected Class<?>[] args;
        protected Class<?> clazz;

        public Serialization(Constructor<?> ctor) {
            this.clazz = ctor.getDeclaringClass();
            this.args = ctor.getParameterTypes();
        }
    }

    public AnnotatedConstructor(TypeResolutionContext ctxt, Constructor<?> constructor, AnnotationMap classAnn, AnnotationMap[] paramAnn) {
        super(ctxt, classAnn, paramAnn);
        if (constructor == null) {
            throw new IllegalArgumentException("Null constructor not allowed");
        }
        this._constructor = constructor;
    }

    protected AnnotatedConstructor(Serialization ser) {
        super(null, null, null);
        this._constructor = null;
        this._serialization = ser;
    }

    public final AnnotatedConstructor withAnnotations(AnnotationMap ann) {
        return new AnnotatedConstructor(this._typeContext, this._constructor, ann, this._paramAnnotations);
    }

    public final Constructor<?> getAnnotated() {
        return this._constructor;
    }

    public final int getModifiers() {
        return this._constructor.getModifiers();
    }

    public final String getName() {
        return this._constructor.getName();
    }

    public final JavaType getType() {
        return this._typeContext.resolveType(getRawType());
    }

    public final Class<?> getRawType() {
        return this._constructor.getDeclaringClass();
    }

    public final int getParameterCount() {
        return this._constructor.getParameterTypes().length;
    }

    public final Class<?> getRawParameterType(int index) {
        Class<?>[] types = this._constructor.getParameterTypes();
        return index >= types.length ? null : types[index];
    }

    public final JavaType getParameterType(int index) {
        Type[] types = this._constructor.getGenericParameterTypes();
        if (index >= types.length) {
            return null;
        }
        return this._typeContext.resolveType(types[index]);
    }

    @Deprecated
    public final Type getGenericParameterType(int index) {
        Type[] types = this._constructor.getGenericParameterTypes();
        if (index >= types.length) {
            return null;
        }
        return types[index];
    }

    public final Object call() throws Exception {
        return this._constructor.newInstance(new Object[0]);
    }

    public final Object call(Object[] args) throws Exception {
        return this._constructor.newInstance(args);
    }

    public final Object call1(Object arg) throws Exception {
        return this._constructor.newInstance(new Object[]{arg});
    }

    public final Class<?> getDeclaringClass() {
        return this._constructor.getDeclaringClass();
    }

    public final Member getMember() {
        return this._constructor;
    }

    public final void setValue(Object pojo, Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call setValue() on constructor of " + getDeclaringClass().getName());
    }

    public final Object getValue(Object pojo) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call getValue() on constructor of " + getDeclaringClass().getName());
    }

    public final String toString() {
        return "[constructor for " + getName() + ", annotations: " + this._annotations + "]";
    }

    public final int hashCode() {
        return this._constructor.getName().hashCode();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        if (((AnnotatedConstructor) o)._constructor != this._constructor) {
            return false;
        }
        return true;
    }

    final Object writeReplace() {
        return new AnnotatedConstructor(new Serialization(this._constructor));
    }

    final Object readResolve() {
        Class<?> clazz = this._serialization.clazz;
        try {
            Constructor<?> ctor = clazz.getDeclaredConstructor(this._serialization.args);
            if (!ctor.isAccessible()) {
                ClassUtil.checkAndFixAccess(ctor, false);
            }
            return new AnnotatedConstructor(null, ctor, null, null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find constructor with " + this._serialization.args.length + " args from Class '" + clazz.getName());
        }
    }
}
