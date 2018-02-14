package com.fasterxml.jackson.databind.jsontype;

import java.io.Serializable;

public final class NamedType implements Serializable {
    private static final long serialVersionUID = 1;
    protected final Class<?> _class;
    protected final int _hashCode;
    protected String _name;

    public NamedType(Class<?> c) {
        this(c, null);
    }

    public NamedType(Class<?> c, String name) {
        this._class = c;
        this._hashCode = c.getName().hashCode();
        setName(name);
    }

    public final Class<?> getType() {
        return this._class;
    }

    public final String getName() {
        return this._name;
    }

    public final void setName(String name) {
        if (name == null || name.length() == 0) {
            name = null;
        }
        this._name = name;
    }

    public final boolean hasName() {
        return this._name != null;
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
        if (this._class != ((NamedType) o)._class) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        return this._hashCode;
    }

    public final String toString() {
        return "[NamedType, class " + this._class.getName() + ", name: " + (this._name == null ? "null" : "'" + this._name + "'") + "]";
    }
}
