package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.Annotations;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;

public final class AnnotationMap implements Annotations {
    protected HashMap<Class<?>, Annotation> _annotations;

    private AnnotationMap(HashMap<Class<?>, Annotation> a) {
        this._annotations = a;
    }

    public final <A extends Annotation> A get(Class<A> cls) {
        if (this._annotations == null) {
            return null;
        }
        return (Annotation) this._annotations.get(cls);
    }

    public final boolean has(Class<?> cls) {
        if (this._annotations == null) {
            return false;
        }
        return this._annotations.containsKey(cls);
    }

    public final boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
        if (this._annotations != null) {
            for (Object containsKey : annoClasses) {
                if (this._annotations.containsKey(containsKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final Iterable<Annotation> annotations() {
        if (this._annotations == null || this._annotations.size() == 0) {
            return Collections.emptyList();
        }
        return this._annotations.values();
    }

    public static AnnotationMap merge(AnnotationMap primary, AnnotationMap secondary) {
        if (primary == null || primary._annotations == null || primary._annotations.isEmpty()) {
            return secondary;
        }
        if (secondary == null || secondary._annotations == null || secondary._annotations.isEmpty()) {
            return primary;
        }
        HashMap<Class<?>, Annotation> annotations = new HashMap();
        for (Annotation ann : secondary._annotations.values()) {
            annotations.put(ann.annotationType(), ann);
        }
        for (Annotation ann2 : primary._annotations.values()) {
            annotations.put(ann2.annotationType(), ann2);
        }
        return new AnnotationMap(annotations);
    }

    public final int size() {
        return this._annotations == null ? 0 : this._annotations.size();
    }

    public final boolean addIfNotPresent(Annotation ann) {
        if (this._annotations != null && this._annotations.containsKey(ann.annotationType())) {
            return false;
        }
        _add(ann);
        return true;
    }

    public final boolean add(Annotation ann) {
        return _add(ann);
    }

    public final String toString() {
        if (this._annotations == null) {
            return "[null]";
        }
        return this._annotations.toString();
    }

    protected final boolean _add(Annotation ann) {
        if (this._annotations == null) {
            this._annotations = new HashMap();
        }
        Annotation previous = (Annotation) this._annotations.put(ann.annotationType(), ann);
        return previous == null || !previous.equals(ann);
    }
}
