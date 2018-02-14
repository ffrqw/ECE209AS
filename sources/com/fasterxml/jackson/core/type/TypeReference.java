package com.fasterxml.jackson.core.type;

import java.lang.reflect.Type;

public abstract class TypeReference<T> implements Comparable<TypeReference<T>> {
    protected final Type _type;

    public Type getType() {
        return this._type;
    }
}
