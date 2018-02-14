package com.fasterxml.jackson.databind.type;

import java.io.Serializable;

public final class ClassKey implements Serializable, Comparable<ClassKey> {
    private static final long serialVersionUID = 1;
    private Class<?> _class;
    private String _className;
    private int _hashCode;

    public ClassKey() {
        this._class = null;
        this._className = null;
        this._hashCode = 0;
    }

    public ClassKey(Class<?> clz) {
        this._class = clz;
        this._className = clz.getName();
        this._hashCode = this._className.hashCode();
    }

    public final void reset(Class<?> clz) {
        this._class = clz;
        this._className = clz.getName();
        this._hashCode = this._className.hashCode();
    }

    public final int compareTo(ClassKey other) {
        return this._className.compareTo(other._className);
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        if (((ClassKey) o)._class != this._class) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        return this._hashCode;
    }

    public final String toString() {
        return this._className;
    }
}
