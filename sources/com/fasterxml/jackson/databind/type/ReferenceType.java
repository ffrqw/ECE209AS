package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;

public class ReferenceType extends SimpleType {
    private static final long serialVersionUID = 1;
    protected final JavaType _anchorType;
    protected final JavaType _referencedType;

    protected ReferenceType(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType refType, JavaType anchorType, Object valueHandler, Object typeHandler, boolean asStatic) {
        super(cls, bindings, superClass, superInts, refType.hashCode(), valueHandler, typeHandler, asStatic);
        this._referencedType = refType;
        if (anchorType == null) {
            anchorType = this;
        }
        this._anchorType = anchorType;
    }

    protected ReferenceType(TypeBase base, JavaType refType) {
        super(base);
        this._referencedType = refType;
        this._anchorType = this;
    }

    public static ReferenceType upgradeFrom(JavaType baseType, JavaType refdType) {
        if (refdType == null) {
            throw new IllegalArgumentException("Missing referencedType");
        } else if (baseType instanceof TypeBase) {
            return new ReferenceType((TypeBase) baseType, refdType);
        } else {
            throw new IllegalArgumentException("Can not upgrade from an instance of " + baseType.getClass());
        }
    }

    public static ReferenceType construct(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType refType) {
        return new ReferenceType(cls, bindings, superClass, superInts, refType, null, null, null, false);
    }

    @Deprecated
    public static ReferenceType construct(Class<?> cls, JavaType refType) {
        return new ReferenceType(cls, TypeBindings.emptyBindings(), null, null, null, refType, null, null, false);
    }

    public JavaType withContentType(JavaType contentType) {
        if (this._referencedType == contentType) {
            return this;
        }
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, contentType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public ReferenceType withTypeHandler(Object h) {
        if (h == this._typeHandler) {
            return this;
        }
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, this._valueHandler, h, this._asStatic);
    }

    public ReferenceType withContentTypeHandler(Object h) {
        return h == this._referencedType.getTypeHandler() ? this : new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType.withTypeHandler(h), this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public ReferenceType withValueHandler(Object h) {
        if (h == this._valueHandler) {
            return this;
        }
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, h, this._typeHandler, this._asStatic);
    }

    public ReferenceType withContentValueHandler(Object h) {
        if (h == this._referencedType.getValueHandler()) {
            return this;
        }
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType.withValueHandler(h), this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public ReferenceType withStaticTyping() {
        return this._asStatic ? this : new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType.withStaticTyping(), this._anchorType, this._valueHandler, this._typeHandler, true);
    }

    public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        return new ReferenceType(rawType, this._bindings, superClass, superInterfaces, this._referencedType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    protected String buildCanonicalName() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        sb.append('<');
        sb.append(this._referencedType.toCanonical());
        return sb.toString();
    }

    @Deprecated
    protected JavaType _narrow(Class<?> subclass) {
        return new ReferenceType(subclass, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public JavaType getContentType() {
        return this._referencedType;
    }

    public JavaType getReferencedType() {
        return this._referencedType;
    }

    public boolean hasContentType() {
        return true;
    }

    public boolean isReferenceType() {
        return true;
    }

    public StringBuilder getErasedSignature(StringBuilder sb) {
        return TypeBase._classSignature(this._class, sb, true);
    }

    public StringBuilder getGenericSignature(StringBuilder sb) {
        TypeBase._classSignature(this._class, sb, false);
        sb.append('<');
        sb = this._referencedType.getGenericSignature(sb);
        sb.append(">;");
        return sb;
    }

    public JavaType getAnchorType() {
        return this._anchorType;
    }

    public boolean isAnchorType() {
        return this._anchorType == this;
    }

    public String toString() {
        return "[reference type, class " + buildCanonicalName() + '<' + this._referencedType + '>' + ']';
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        ReferenceType other = (ReferenceType) o;
        if (other._class == this._class) {
            return this._referencedType.equals(other._referencedType);
        }
        return false;
    }
}
