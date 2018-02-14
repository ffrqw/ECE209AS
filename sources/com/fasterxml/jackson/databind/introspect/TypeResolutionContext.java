package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;

public interface TypeResolutionContext {

    public static class Basic implements TypeResolutionContext {
        private final TypeBindings _bindings;
        private final TypeFactory _typeFactory;

        public Basic(TypeFactory tf, TypeBindings b) {
            this._typeFactory = tf;
            this._bindings = b;
        }

        public JavaType resolveType(Type type) {
            return this._typeFactory.constructType(type, this._bindings);
        }
    }

    JavaType resolveType(Type type);
}
