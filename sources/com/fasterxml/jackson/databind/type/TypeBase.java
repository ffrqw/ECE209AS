package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class TypeBase extends JavaType implements JsonSerializable {
    private static final TypeBindings NO_BINDINGS = TypeBindings.emptyBindings();
    private static final JavaType[] NO_TYPES = new JavaType[0];
    private static final long serialVersionUID = 1;
    protected final TypeBindings _bindings;
    volatile transient String _canonicalName;
    protected final JavaType _superClass;
    protected final JavaType[] _superInterfaces;

    public abstract StringBuilder getErasedSignature(StringBuilder stringBuilder);

    public abstract StringBuilder getGenericSignature(StringBuilder stringBuilder);

    protected TypeBase(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInts, int hash, Object valueHandler, Object typeHandler, boolean asStatic) {
        super(raw, hash, valueHandler, typeHandler, asStatic);
        if (bindings == null) {
            bindings = NO_BINDINGS;
        }
        this._bindings = bindings;
        this._superClass = superClass;
        this._superInterfaces = superInts;
    }

    protected TypeBase(TypeBase base) {
        super(base);
        this._superClass = base._superClass;
        this._superInterfaces = base._superInterfaces;
        this._bindings = base._bindings;
    }

    public String toCanonical() {
        String str = this._canonicalName;
        if (str == null) {
            return buildCanonicalName();
        }
        return str;
    }

    protected String buildCanonicalName() {
        return this._class.getName();
    }

    public TypeBindings getBindings() {
        return this._bindings;
    }

    public int containedTypeCount() {
        return this._bindings.size();
    }

    public JavaType containedType(int index) {
        return this._bindings.getBoundType(index);
    }

    @Deprecated
    public String containedTypeName(int index) {
        return this._bindings.getBoundName(index);
    }

    public JavaType getSuperClass() {
        return this._superClass;
    }

    public List<JavaType> getInterfaces() {
        if (this._superInterfaces == null) {
            return Collections.emptyList();
        }
        switch (this._superInterfaces.length) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(this._superInterfaces[0]);
            default:
                return Arrays.asList(this._superInterfaces);
        }
    }

    public final JavaType findSuperType(Class<?> rawTarget) {
        if (rawTarget == this._class) {
            return this;
        }
        JavaType type;
        if (rawTarget.isInterface() && this._superInterfaces != null) {
            for (JavaType findSuperType : this._superInterfaces) {
                type = findSuperType.findSuperType(rawTarget);
                if (type != null) {
                    return type;
                }
            }
        }
        if (this._superClass != null) {
            type = this._superClass.findSuperType(rawTarget);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    public JavaType[] findTypeParameters(Class<?> expType) {
        JavaType match = findSuperType(expType);
        if (match == null) {
            return NO_TYPES;
        }
        return match.getBindings().typeParameterArray();
    }

    public void serializeWithType(JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForScalar(this, gen);
        serialize(gen, provider);
        typeSer.writeTypeSuffixForScalar(this, gen);
    }

    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeString(toCanonical());
    }

    protected static StringBuilder _classSignature(Class<?> cls, StringBuilder sb, boolean trailingSemicolon) {
        if (!cls.isPrimitive()) {
            sb.append('L');
            String name = cls.getName();
            int len = name.length();
            for (int i = 0; i < len; i++) {
                char c = name.charAt(i);
                if (c == '.') {
                    c = '/';
                }
                sb.append(c);
            }
            if (trailingSemicolon) {
                sb.append(';');
            }
        } else if (cls == Boolean.TYPE) {
            sb.append('Z');
        } else if (cls == Byte.TYPE) {
            sb.append('B');
        } else if (cls == Short.TYPE) {
            sb.append('S');
        } else if (cls == Character.TYPE) {
            sb.append('C');
        } else if (cls == Integer.TYPE) {
            sb.append('I');
        } else if (cls == Long.TYPE) {
            sb.append('J');
        } else if (cls == Float.TYPE) {
            sb.append('F');
        } else if (cls == Double.TYPE) {
            sb.append('D');
        } else if (cls == Void.TYPE) {
            sb.append('V');
        } else {
            throw new IllegalStateException("Unrecognized primitive type: " + cls.getName());
        }
        return sb;
    }

    protected static JavaType _bogusSuperClass(Class<?> cls) {
        if (cls.getSuperclass() == null) {
            return null;
        }
        return TypeFactory.unknownType();
    }
}
