package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.TypeVariable;

public final class MapType extends MapLikeType {
    private static final long serialVersionUID = 1;

    private MapType(Class<?> mapType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler, boolean asStatic) {
        super(mapType, bindings, superClass, superInts, keyT, valueT, valueHandler, typeHandler, asStatic);
    }

    protected MapType(TypeBase base, JavaType keyT, JavaType valueT) {
        super(base, keyT, valueT);
    }

    public static MapType construct(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT) {
        return new MapType(rawType, bindings, superClass, superInts, keyT, valueT, null, null, false);
    }

    @Deprecated
    public static MapType construct(Class<?> rawType, JavaType keyT, JavaType valueT) {
        TypeBindings bindings;
        TypeVariable<?>[] vars = rawType.getTypeParameters();
        if (vars == null || vars.length != 2) {
            bindings = TypeBindings.emptyBindings();
        } else {
            bindings = TypeBindings.create(rawType, keyT, valueT);
        }
        return new MapType(rawType, bindings, TypeBase._bogusSuperClass(rawType), null, keyT, valueT, null, null, false);
    }

    @Deprecated
    protected final JavaType _narrow(Class<?> subclass) {
        return new MapType(subclass, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final MapType withTypeHandler(Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, h, this._asStatic);
    }

    public final MapType withContentTypeHandler(Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final MapType withValueHandler(Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, h, this._typeHandler, this._asStatic);
    }

    public final MapType withContentValueHandler(Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final MapType withStaticTyping() {
        return this._asStatic ? this : new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withStaticTyping(), this._valueType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
    }

    public final JavaType withContentType(JavaType contentType) {
        if (this._valueType == contentType) {
            return this;
        }
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, contentType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final MapType withKeyType(JavaType keyType) {
        if (keyType == this._keyType) {
            return this;
        }
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        return new MapType(rawType, bindings, superClass, superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final MapType withKeyTypeHandler(Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final MapType withKeyValueHandler(Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final String toString() {
        return "[map type; class " + this._class.getName() + ", " + this._keyType + " -> " + this._valueType + "]";
    }
}
