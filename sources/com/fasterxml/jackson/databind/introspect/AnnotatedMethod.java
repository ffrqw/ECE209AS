package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class AnnotatedMethod extends AnnotatedWithParams implements Serializable {
    private static final long serialVersionUID = 1;
    protected final transient Method _method;
    protected Class<?>[] _paramClasses;
    protected Serialization _serialization;

    private static final class Serialization implements Serializable {
        private static final long serialVersionUID = 1;
        protected Class<?>[] args;
        protected Class<?> clazz;
        protected String name;

        public Serialization(Method setter) {
            this.clazz = setter.getDeclaringClass();
            this.name = setter.getName();
            this.args = setter.getParameterTypes();
        }
    }

    public AnnotatedMethod(TypeResolutionContext ctxt, Method method, AnnotationMap classAnn, AnnotationMap[] paramAnnotations) {
        super(ctxt, classAnn, paramAnnotations);
        if (method == null) {
            throw new IllegalArgumentException("Can not construct AnnotatedMethod with null Method");
        }
        this._method = method;
    }

    protected AnnotatedMethod(Serialization ser) {
        super(null, null, null);
        this._method = null;
        this._serialization = ser;
    }

    public final AnnotatedMethod withMethod(Method m) {
        return new AnnotatedMethod(this._typeContext, m, this._annotations, this._paramAnnotations);
    }

    public final AnnotatedMethod withAnnotations(AnnotationMap ann) {
        return new AnnotatedMethod(this._typeContext, this._method, ann, this._paramAnnotations);
    }

    public final Method getAnnotated() {
        return this._method;
    }

    public final int getModifiers() {
        return this._method.getModifiers();
    }

    public final String getName() {
        return this._method.getName();
    }

    public final JavaType getType() {
        return this._typeContext.resolveType(this._method.getGenericReturnType());
    }

    public final Class<?> getRawType() {
        return this._method.getReturnType();
    }

    @Deprecated
    public final Type getGenericType() {
        return this._method.getGenericReturnType();
    }

    public final Object call() throws Exception {
        return this._method.invoke(null, new Object[0]);
    }

    public final Object call(Object[] args) throws Exception {
        return this._method.invoke(null, args);
    }

    public final Object call1(Object arg) throws Exception {
        return this._method.invoke(null, new Object[]{arg});
    }

    public final Object callOn(Object pojo) throws Exception {
        return this._method.invoke(pojo, new Object[0]);
    }

    public final Object callOnWith(Object pojo, Object... args) throws Exception {
        return this._method.invoke(pojo, args);
    }

    public final int getParameterCount() {
        return getRawParameterTypes().length;
    }

    public final Class<?> getRawParameterType(int index) {
        Class<?>[] types = getRawParameterTypes();
        return index >= types.length ? null : types[index];
    }

    public final JavaType getParameterType(int index) {
        Type[] types = this._method.getGenericParameterTypes();
        if (index >= types.length) {
            return null;
        }
        return this._typeContext.resolveType(types[index]);
    }

    @Deprecated
    public final Type getGenericParameterType(int index) {
        Type[] types = getGenericParameterTypes();
        if (index >= types.length) {
            return null;
        }
        return types[index];
    }

    public final Class<?> getDeclaringClass() {
        return this._method.getDeclaringClass();
    }

    public final Method getMember() {
        return this._method;
    }

    public final void setValue(Object pojo, Object value) throws IllegalArgumentException {
        try {
            this._method.invoke(pojo, new Object[]{value});
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to setValue() with method " + getFullName() + ": " + e.getMessage(), e);
        } catch (InvocationTargetException e2) {
            throw new IllegalArgumentException("Failed to setValue() with method " + getFullName() + ": " + e2.getMessage(), e2);
        }
    }

    public final Object getValue(Object pojo) throws IllegalArgumentException {
        try {
            return this._method.invoke(pojo, new Object[0]);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to getValue() with method " + getFullName() + ": " + e.getMessage(), e);
        } catch (InvocationTargetException e2) {
            throw new IllegalArgumentException("Failed to getValue() with method " + getFullName() + ": " + e2.getMessage(), e2);
        }
    }

    public final String getFullName() {
        return getDeclaringClass().getName() + "#" + getName() + "(" + getParameterCount() + " params)";
    }

    public final Class<?>[] getRawParameterTypes() {
        if (this._paramClasses == null) {
            this._paramClasses = this._method.getParameterTypes();
        }
        return this._paramClasses;
    }

    @Deprecated
    public final Type[] getGenericParameterTypes() {
        return this._method.getGenericParameterTypes();
    }

    public final Class<?> getRawReturnType() {
        return this._method.getReturnType();
    }

    public final boolean hasReturnType() {
        Class<?> rt = getRawReturnType();
        return (rt == Void.TYPE || rt == Void.class) ? false : true;
    }

    public final String toString() {
        return "[method " + getFullName() + "]";
    }

    public final int hashCode() {
        return this._method.getName().hashCode();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        if (((AnnotatedMethod) o)._method != this._method) {
            return false;
        }
        return true;
    }

    final Object writeReplace() {
        return new AnnotatedMethod(new Serialization(this._method));
    }

    final Object readResolve() {
        Class<?> clazz = this._serialization.clazz;
        try {
            Method m = clazz.getDeclaredMethod(this._serialization.name, this._serialization.args);
            if (!m.isAccessible()) {
                ClassUtil.checkAndFixAccess(m, false);
            }
            return new AnnotatedMethod(null, m, null, null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz.getName());
        }
    }
}
