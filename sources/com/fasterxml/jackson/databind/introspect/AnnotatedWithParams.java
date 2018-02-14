package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public abstract class AnnotatedWithParams extends AnnotatedMember {
    private static final long serialVersionUID = 1;
    protected final AnnotationMap[] _paramAnnotations;

    public abstract Object call() throws Exception;

    public abstract Object call(Object[] objArr) throws Exception;

    public abstract Object call1(Object obj) throws Exception;

    @Deprecated
    public abstract Type getGenericParameterType(int i);

    public abstract int getParameterCount();

    public abstract JavaType getParameterType(int i);

    public abstract Class<?> getRawParameterType(int i);

    protected AnnotatedWithParams(TypeResolutionContext ctxt, AnnotationMap annotations, AnnotationMap[] paramAnnotations) {
        super(ctxt, annotations);
        this._paramAnnotations = paramAnnotations;
    }

    protected AnnotatedWithParams(AnnotatedWithParams base, AnnotationMap[] paramAnnotations) {
        super(base);
        this._paramAnnotations = paramAnnotations;
    }

    public final void addOrOverrideParam(int paramIndex, Annotation a) {
        AnnotationMap old = this._paramAnnotations[paramIndex];
        if (old == null) {
            old = new AnnotationMap();
            this._paramAnnotations[paramIndex] = old;
        }
        old.add(a);
    }

    protected AnnotatedParameter replaceParameterAnnotations(int index, AnnotationMap ann) {
        this._paramAnnotations[index] = ann;
        return getParameter(index);
    }

    public final AnnotationMap getParameterAnnotations(int index) {
        if (this._paramAnnotations == null || index < 0 || index >= this._paramAnnotations.length) {
            return null;
        }
        return this._paramAnnotations[index];
    }

    public final AnnotatedParameter getParameter(int index) {
        return new AnnotatedParameter(this, getParameterType(index), getParameterAnnotations(index), index);
    }

    public final int getAnnotationCount() {
        return this._annotations.size();
    }
}
