package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Array;

public final class ArrayType extends TypeBase {
    private static final long serialVersionUID = 1;
    protected final JavaType _componentType;
    protected final Object _emptyArray;

    protected ArrayType(JavaType componentType, TypeBindings bindings, Object emptyInstance, Object valueHandler, Object typeHandler, boolean asStatic) {
        super(emptyInstance.getClass(), bindings, null, null, componentType.hashCode(), valueHandler, typeHandler, asStatic);
        this._componentType = componentType;
        this._emptyArray = emptyInstance;
    }

    public static ArrayType construct(JavaType componentType, TypeBindings bindings) {
        return construct(componentType, bindings, null, null);
    }

    public static ArrayType construct(JavaType componentType, TypeBindings bindings, Object valueHandler, Object typeHandler) {
        return new ArrayType(componentType, bindings, Array.newInstance(componentType.getRawClass(), 0), valueHandler, typeHandler, false);
    }

    public final JavaType withContentType(JavaType contentType) {
        return new ArrayType(contentType, this._bindings, Array.newInstance(contentType.getRawClass(), 0), this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final ArrayType withTypeHandler(Object h) {
        if (h == this._typeHandler) {
            return this;
        }
        return new ArrayType(this._componentType, this._bindings, this._emptyArray, this._valueHandler, h, this._asStatic);
    }

    public final ArrayType withContentTypeHandler(Object h) {
        return h == this._componentType.getTypeHandler() ? this : new ArrayType(this._componentType.withTypeHandler(h), this._bindings, this._emptyArray, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final ArrayType withValueHandler(Object h) {
        if (h == this._valueHandler) {
            return this;
        }
        return new ArrayType(this._componentType, this._bindings, this._emptyArray, h, this._typeHandler, this._asStatic);
    }

    public final ArrayType withContentValueHandler(Object h) {
        return h == this._componentType.getValueHandler() ? this : new ArrayType(this._componentType.withValueHandler(h), this._bindings, this._emptyArray, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final ArrayType withStaticTyping() {
        return this._asStatic ? this : new ArrayType(this._componentType.withStaticTyping(), this._bindings, this._emptyArray, this._valueHandler, this._typeHandler, true);
    }

    @Deprecated
    protected final JavaType _narrow(Class<?> cls) {
        return _reportUnsupported();
    }

    public final JavaType refine(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        return null;
    }

    private JavaType _reportUnsupported() {
        throw new UnsupportedOperationException("Can not narrow or widen array types");
    }

    public final boolean isArrayType() {
        return true;
    }

    public final boolean isAbstract() {
        return false;
    }

    public final boolean isConcrete() {
        return true;
    }

    public final boolean hasGenericTypes() {
        return this._componentType.hasGenericTypes();
    }

    public final boolean isContainerType() {
        return true;
    }

    public final JavaType getContentType() {
        return this._componentType;
    }

    public final Object getContentValueHandler() {
        return this._componentType.getValueHandler();
    }

    public final Object getContentTypeHandler() {
        return this._componentType.getTypeHandler();
    }

    public final boolean hasHandlers() {
        return super.hasHandlers() || this._componentType.hasHandlers();
    }

    public final StringBuilder getGenericSignature(StringBuilder sb) {
        sb.append('[');
        return this._componentType.getGenericSignature(sb);
    }

    public final StringBuilder getErasedSignature(StringBuilder sb) {
        sb.append('[');
        return this._componentType.getErasedSignature(sb);
    }

    public final String toString() {
        return "[array type, component type: " + this._componentType + "]";
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        return this._componentType.equals(((ArrayType) o)._componentType);
    }
}
