package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Collections;

public abstract class AnnotatedMember extends Annotated implements Serializable {
    private static final long serialVersionUID = 1;
    protected final transient AnnotationMap _annotations;
    protected final transient TypeResolutionContext _typeContext;

    public abstract Class<?> getDeclaringClass();

    public abstract Member getMember();

    public abstract Object getValue(Object obj) throws UnsupportedOperationException, IllegalArgumentException;

    public abstract void setValue(Object obj, Object obj2) throws UnsupportedOperationException, IllegalArgumentException;

    protected AnnotatedMember(TypeResolutionContext ctxt, AnnotationMap annotations) {
        this._typeContext = ctxt;
        this._annotations = annotations;
    }

    protected AnnotatedMember(AnnotatedMember base) {
        this._typeContext = base._typeContext;
        this._annotations = base._annotations;
    }

    public TypeResolutionContext getTypeContext() {
        return this._typeContext;
    }

    public final <A extends Annotation> A getAnnotation(Class<A> acls) {
        if (this._annotations == null) {
            return null;
        }
        return this._annotations.get(acls);
    }

    public final boolean hasAnnotation(Class<?> acls) {
        if (this._annotations == null) {
            return false;
        }
        return this._annotations.has(acls);
    }

    public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
        if (this._annotations == null) {
            return false;
        }
        return this._annotations.hasOneOf(annoClasses);
    }

    public Iterable<Annotation> annotations() {
        if (this._annotations == null) {
            return Collections.emptyList();
        }
        return this._annotations.annotations();
    }

    protected AnnotationMap getAllAnnotations() {
        return this._annotations;
    }

    public final boolean addOrOverride(Annotation a) {
        return this._annotations.add(a);
    }

    public final boolean addIfNotPresent(Annotation a) {
        return this._annotations.addIfNotPresent(a);
    }

    public final void fixAccess(boolean force) {
        Member m = getMember();
        if (m != null) {
            ClassUtil.checkAndFixAccess(m, force);
        }
    }

    @Deprecated
    public final void fixAccess() {
        fixAccess(true);
    }
}
