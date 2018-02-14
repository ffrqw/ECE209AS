package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

public final class AnnotatedMethodMap implements Iterable<AnnotatedMethod> {
    protected LinkedHashMap<MemberKey, AnnotatedMethod> _methods;

    public final void add(AnnotatedMethod am) {
        if (this._methods == null) {
            this._methods = new LinkedHashMap();
        }
        this._methods.put(new MemberKey(am.getAnnotated()), am);
    }

    public final AnnotatedMethod remove(AnnotatedMethod am) {
        return remove(am.getAnnotated());
    }

    public final AnnotatedMethod remove(Method m) {
        if (this._methods != null) {
            return (AnnotatedMethod) this._methods.remove(new MemberKey(m));
        }
        return null;
    }

    public final boolean isEmpty() {
        return this._methods == null || this._methods.size() == 0;
    }

    public final int size() {
        return this._methods == null ? 0 : this._methods.size();
    }

    public final AnnotatedMethod find(String name, Class<?>[] paramTypes) {
        if (this._methods == null) {
            return null;
        }
        return (AnnotatedMethod) this._methods.get(new MemberKey(name, paramTypes));
    }

    public final AnnotatedMethod find(Method m) {
        if (this._methods == null) {
            return null;
        }
        return (AnnotatedMethod) this._methods.get(new MemberKey(m));
    }

    public final Iterator<AnnotatedMethod> iterator() {
        if (this._methods != null) {
            return this._methods.values().iterator();
        }
        return Collections.emptyList().iterator();
    }
}
