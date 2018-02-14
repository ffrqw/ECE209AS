package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.util.ArrayList;
import java.util.Iterator;

public final class ClassStack {
    protected final Class<?> _current;
    protected final ClassStack _parent;
    private ArrayList<ResolvedRecursiveType> _selfRefs;

    public ClassStack(Class<?> rootType) {
        this(null, rootType);
    }

    private ClassStack(ClassStack parent, Class<?> curr) {
        this._parent = parent;
        this._current = curr;
    }

    public final ClassStack child(Class<?> cls) {
        return new ClassStack(this, cls);
    }

    public final void addSelfReference(ResolvedRecursiveType ref) {
        if (this._selfRefs == null) {
            this._selfRefs = new ArrayList();
        }
        this._selfRefs.add(ref);
    }

    public final void resolveSelfReferences(JavaType resolved) {
        if (this._selfRefs != null) {
            Iterator i$ = this._selfRefs.iterator();
            while (i$.hasNext()) {
                ((ResolvedRecursiveType) i$.next()).setReference(resolved);
            }
        }
    }

    public final ClassStack find(Class<?> cls) {
        if (this._current == cls) {
            return this;
        }
        for (ClassStack curr = this._parent; curr != null; curr = curr._parent) {
            if (curr._current == cls) {
                return curr;
            }
        }
        return null;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ClassStack (self-refs: ").append(this._selfRefs == null ? "0" : String.valueOf(this._selfRefs.size())).append(')');
        for (ClassStack curr = this; curr != null; curr = curr._parent) {
            sb.append(' ').append(curr._current.getName());
        }
        sb.append(']');
        return sb.toString();
    }
}
