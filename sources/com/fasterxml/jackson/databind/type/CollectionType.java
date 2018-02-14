package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.TypeVariable;

public final class CollectionType extends CollectionLikeType {
    private static final long serialVersionUID = 1;

    private CollectionType(Class<?> collT, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType elemT, Object valueHandler, Object typeHandler, boolean asStatic) {
        super(collT, bindings, superClass, superInts, elemT, valueHandler, typeHandler, asStatic);
    }

    protected CollectionType(TypeBase base, JavaType elemT) {
        super(base, elemT);
    }

    public static CollectionType construct(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType elemT) {
        return new CollectionType(rawType, bindings, superClass, superInts, elemT, null, null, false);
    }

    @Deprecated
    public static CollectionType construct(Class<?> rawType, JavaType elemT) {
        TypeBindings bindings;
        TypeVariable<?>[] vars = rawType.getTypeParameters();
        if (vars == null || vars.length != 1) {
            bindings = TypeBindings.emptyBindings();
        } else {
            bindings = TypeBindings.create((Class) rawType, elemT);
        }
        return new CollectionType(rawType, bindings, TypeBase._bogusSuperClass(rawType), null, elemT, null, null, false);
    }

    @Deprecated
    protected final JavaType _narrow(Class<?> subclass) {
        return new CollectionType(subclass, this._bindings, this._superClass, this._superInterfaces, this._elementType, null, null, this._asStatic);
    }

    public final JavaType withContentType(JavaType contentType) {
        if (this._elementType == contentType) {
            return this;
        }
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, contentType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final CollectionType withTypeHandler(Object h) {
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, this._valueHandler, h, this._asStatic);
    }

    public final CollectionType withContentTypeHandler(Object h) {
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final CollectionType withValueHandler(Object h) {
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, h, this._typeHandler, this._asStatic);
    }

    public final CollectionType withContentValueHandler(Object h) {
        return new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final CollectionType withStaticTyping() {
        return this._asStatic ? this : new CollectionType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
    }

    public final JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        return new CollectionType(rawType, bindings, superClass, superInterfaces, this._elementType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public final String toString() {
        return "[collection type; class " + this._class.getName() + ", contains " + this._elementType + "]";
    }
}
