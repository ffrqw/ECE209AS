package com.google.gson.internal;

import com.j256.ormlite.stmt.query.SimpleComparison;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

/* renamed from: com.google.gson.internal.$Gson$Types */
public final class C$Gson$Types {
    static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    /* compiled from: $Gson$Types */
    private static final class GenericArrayTypeImpl implements Serializable, GenericArrayType {
        private final Type componentType;

        public GenericArrayTypeImpl(Type componentType) {
            this.componentType = C$Gson$Types.canonicalize(componentType);
        }

        public final Type getGenericComponentType() {
            return this.componentType;
        }

        public final boolean equals(Object o) {
            return (o instanceof GenericArrayType) && C$Gson$Types.equals(this, (GenericArrayType) o);
        }

        public final int hashCode() {
            return this.componentType.hashCode();
        }

        public final String toString() {
            return C$Gson$Types.typeToString(this.componentType) + "[]";
        }
    }

    /* compiled from: $Gson$Types */
    private static final class ParameterizedTypeImpl implements Serializable, ParameterizedType {
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;

        public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
            boolean z = false;
            if (rawType instanceof Class) {
                Class<?> rawTypeAsClass = (Class) rawType;
                boolean isStaticOrTopLevelClass;
                if (Modifier.isStatic(rawTypeAsClass.getModifiers()) || rawTypeAsClass.getEnclosingClass() == null) {
                    isStaticOrTopLevelClass = true;
                } else {
                    isStaticOrTopLevelClass = false;
                }
                if (ownerType != null || isStaticOrTopLevelClass) {
                    z = true;
                }
                C$Gson$Preconditions.checkArgument(z);
            }
            this.ownerType = ownerType == null ? null : C$Gson$Types.canonicalize(ownerType);
            this.rawType = C$Gson$Types.canonicalize(rawType);
            this.typeArguments = (Type[]) typeArguments.clone();
            int length = this.typeArguments.length;
            for (int t = 0; t < length; t++) {
                C$Gson$Preconditions.checkNotNull(this.typeArguments[t]);
                C$Gson$Types.checkNotPrimitive(this.typeArguments[t]);
                this.typeArguments[t] = C$Gson$Types.canonicalize(this.typeArguments[t]);
            }
        }

        public final Type[] getActualTypeArguments() {
            return (Type[]) this.typeArguments.clone();
        }

        public final Type getRawType() {
            return this.rawType;
        }

        public final Type getOwnerType() {
            return this.ownerType;
        }

        public final boolean equals(Object other) {
            return (other instanceof ParameterizedType) && C$Gson$Types.equals(this, (ParameterizedType) other);
        }

        public final int hashCode() {
            return (Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode()) ^ C$Gson$Types.hashCodeOrZero(this.ownerType);
        }

        public final String toString() {
            int length = this.typeArguments.length;
            if (length == 0) {
                return C$Gson$Types.typeToString(this.rawType);
            }
            StringBuilder stringBuilder = new StringBuilder((length + 1) * 30);
            stringBuilder.append(C$Gson$Types.typeToString(this.rawType)).append(SimpleComparison.LESS_THAN_OPERATION).append(C$Gson$Types.typeToString(this.typeArguments[0]));
            for (int i = 1; i < length; i++) {
                stringBuilder.append(", ").append(C$Gson$Types.typeToString(this.typeArguments[i]));
            }
            return stringBuilder.append(SimpleComparison.GREATER_THAN_OPERATION).toString();
        }
    }

    /* compiled from: $Gson$Types */
    private static final class WildcardTypeImpl implements Serializable, WildcardType {
        private final Type lowerBound;
        private final Type upperBound;

        public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            boolean z;
            boolean z2 = true;
            C$Gson$Preconditions.checkArgument(lowerBounds.length <= 1);
            if (upperBounds.length == 1) {
                z = true;
            } else {
                z = false;
            }
            C$Gson$Preconditions.checkArgument(z);
            if (lowerBounds.length == 1) {
                C$Gson$Preconditions.checkNotNull(lowerBounds[0]);
                C$Gson$Types.checkNotPrimitive(lowerBounds[0]);
                if (upperBounds[0] != Object.class) {
                    z2 = false;
                }
                C$Gson$Preconditions.checkArgument(z2);
                this.lowerBound = C$Gson$Types.canonicalize(lowerBounds[0]);
                this.upperBound = Object.class;
                return;
            }
            C$Gson$Preconditions.checkNotNull(upperBounds[0]);
            C$Gson$Types.checkNotPrimitive(upperBounds[0]);
            this.lowerBound = null;
            this.upperBound = C$Gson$Types.canonicalize(upperBounds[0]);
        }

        public final Type[] getUpperBounds() {
            return new Type[]{this.upperBound};
        }

        public final Type[] getLowerBounds() {
            if (this.lowerBound == null) {
                return C$Gson$Types.EMPTY_TYPE_ARRAY;
            }
            return new Type[]{this.lowerBound};
        }

        public final boolean equals(Object other) {
            return (other instanceof WildcardType) && C$Gson$Types.equals(this, (WildcardType) other);
        }

        public final int hashCode() {
            return (this.lowerBound != null ? this.lowerBound.hashCode() + 31 : 1) ^ (this.upperBound.hashCode() + 31);
        }

        public final String toString() {
            if (this.lowerBound != null) {
                return "? super " + C$Gson$Types.typeToString(this.lowerBound);
            }
            if (this.upperBound == Object.class) {
                return "?";
            }
            return "? extends " + C$Gson$Types.typeToString(this.upperBound);
        }
    }

    private static GenericArrayType arrayOf(Type componentType) {
        return new GenericArrayTypeImpl(componentType);
    }

    public static Type canonicalize(Type type) {
        if (type instanceof Class) {
            GenericArrayTypeImpl genericArrayTypeImpl;
            Class<?> c = (Class) type;
            if (c.isArray()) {
                genericArrayTypeImpl = new GenericArrayTypeImpl(C$Gson$Types.canonicalize(c.getComponentType()));
            } else {
                Object obj = c;
            }
            return genericArrayTypeImpl;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return new ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());
        } else if (type instanceof GenericArrayType) {
            return new GenericArrayTypeImpl(((GenericArrayType) type).getGenericComponentType());
        } else {
            if (!(type instanceof WildcardType)) {
                return type;
            }
            WildcardType w = (WildcardType) type;
            return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
        }
    }

    public static Class<?> getRawType(Type type) {
        while (!(type instanceof Class)) {
            if (type instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) type).getRawType();
                C$Gson$Preconditions.checkArgument(rawType instanceof Class);
                return (Class) rawType;
            } else if (type instanceof GenericArrayType) {
                return Array.newInstance(C$Gson$Types.getRawType(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
            } else {
                if (type instanceof TypeVariable) {
                    return Object.class;
                }
                if (type instanceof WildcardType) {
                    type = ((WildcardType) type).getUpperBounds()[0];
                } else {
                    throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + (type == null ? "null" : type.getClass().getName()));
                }
            }
        }
        return (Class) type;
    }

    public static boolean equals(Type a, Type b) {
        while (a != b) {
            if (a instanceof Class) {
                return a.equals(b);
            }
            if (a instanceof ParameterizedType) {
                if (!(b instanceof ParameterizedType)) {
                    return false;
                }
                boolean z;
                ParameterizedType pa = (ParameterizedType) a;
                ParameterizedType pb = (ParameterizedType) b;
                Type ownerType = pa.getOwnerType();
                Type ownerType2 = pb.getOwnerType();
                if (ownerType == ownerType2 || (ownerType != null && ownerType.equals(ownerType2))) {
                    z = true;
                } else {
                    z = false;
                }
                if (z && pa.getRawType().equals(pb.getRawType()) && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments())) {
                    return true;
                }
                return false;
            } else if (a instanceof GenericArrayType) {
                if (!(b instanceof GenericArrayType)) {
                    return false;
                }
                GenericArrayType gb = (GenericArrayType) b;
                a = ((GenericArrayType) a).getGenericComponentType();
                b = gb.getGenericComponentType();
            } else if (a instanceof WildcardType) {
                if (!(b instanceof WildcardType)) {
                    return false;
                }
                WildcardType wa = (WildcardType) a;
                WildcardType wb = (WildcardType) b;
                if (Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds()) && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds())) {
                    return true;
                }
                return false;
            } else if (!(a instanceof TypeVariable)) {
                return false;
            } else {
                if (!(b instanceof TypeVariable)) {
                    return false;
                }
                TypeVariable<?> va = (TypeVariable) a;
                TypeVariable<?> vb = (TypeVariable) b;
                if (va.getGenericDeclaration() == vb.getGenericDeclaration() && va.getName().equals(vb.getName())) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    static int hashCodeOrZero(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static String typeToString(Type type) {
        return type instanceof Class ? ((Class) type).getName() : type.toString();
    }

    private static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
        while (toResolve != rawType) {
            if (toResolve.isInterface()) {
                Class<?>[] interfaces = rawType.getInterfaces();
                int length = interfaces.length;
                for (int i = 0; i < length; i++) {
                    if (interfaces[i] == toResolve) {
                        return rawType.getGenericInterfaces()[i];
                    }
                    if (toResolve.isAssignableFrom(interfaces[i])) {
                        context = rawType.getGenericInterfaces()[i];
                        rawType = interfaces[i];
                        break;
                    }
                }
            }
            if (!rawType.isInterface()) {
                while (rawType != Object.class) {
                    Class<?> rawSupertype = rawType.getSuperclass();
                    if (rawSupertype == toResolve) {
                        return rawType.getGenericSuperclass();
                    }
                    if (toResolve.isAssignableFrom(rawSupertype)) {
                        context = rawType.getGenericSuperclass();
                        rawType = rawSupertype;
                    } else {
                        rawType = rawSupertype;
                    }
                }
            }
            return toResolve;
        }
        return context;
    }

    private static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
        C$Gson$Preconditions.checkArgument(supertype.isAssignableFrom(contextRawType));
        return C$Gson$Types.resolve(context, contextRawType, C$Gson$Types.getGenericSupertype(context, contextRawType, supertype));
    }

    public static Type getArrayComponentType(Type array) {
        if (array instanceof GenericArrayType) {
            return ((GenericArrayType) array).getGenericComponentType();
        }
        return ((Class) array).getComponentType();
    }

    public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
        Type collectionType = C$Gson$Types.getSupertype(context, contextRawType, Collection.class);
        if (collectionType instanceof WildcardType) {
            collectionType = ((WildcardType) collectionType).getUpperBounds()[0];
        }
        if (collectionType instanceof ParameterizedType) {
            return ((ParameterizedType) collectionType).getActualTypeArguments()[0];
        }
        return Object.class;
    }

    public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
        if (context == Properties.class) {
            return new Type[]{String.class, String.class};
        }
        Type mapType = C$Gson$Types.getSupertype(context, contextRawType, Map.class);
        if (mapType instanceof ParameterizedType) {
            return ((ParameterizedType) mapType).getActualTypeArguments();
        }
        return new Type[]{Object.class, Object.class};
    }

    public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
        while (toResolve instanceof TypeVariable) {
            Class cls;
            TypeVariable<?> typeVariable = (TypeVariable) toResolve;
            GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
            if (genericDeclaration instanceof Class) {
                cls = (Class) genericDeclaration;
            } else {
                cls = null;
            }
            if (cls != null) {
                Type genericSupertype = C$Gson$Types.getGenericSupertype(context, contextRawType, cls);
                if (genericSupertype instanceof ParameterizedType) {
                    TypeVariable[] typeParameters = cls.getTypeParameters();
                    int length = typeParameters.length;
                    for (int i = 0; i < length; i++) {
                        if (typeVariable.equals(typeParameters[i])) {
                            toResolve = ((ParameterizedType) genericSupertype).getActualTypeArguments()[i];
                            if (toResolve == typeVariable) {
                                return toResolve;
                            }
                        }
                    }
                    throw new NoSuchElementException();
                }
            }
            Object toResolve2 = typeVariable;
            if (toResolve == typeVariable) {
                return toResolve;
            }
        }
        Type componentType;
        Type newComponentType;
        if ((toResolve instanceof Class) && ((Class) toResolve).isArray()) {
            Class<?> original = (Class) toResolve;
            componentType = original.getComponentType();
            newComponentType = C$Gson$Types.resolve(context, contextRawType, componentType);
            if (componentType == newComponentType) {
                return original;
            }
            return C$Gson$Types.arrayOf(newComponentType);
        } else if (toResolve instanceof GenericArrayType) {
            GenericArrayType original2 = (GenericArrayType) toResolve;
            componentType = original2.getGenericComponentType();
            newComponentType = C$Gson$Types.resolve(context, contextRawType, componentType);
            if (componentType != newComponentType) {
                return C$Gson$Types.arrayOf(newComponentType);
            }
            return original2;
        } else if (toResolve instanceof ParameterizedType) {
            ParameterizedType original3 = (ParameterizedType) toResolve;
            Type ownerType = original3.getOwnerType();
            Type newOwnerType = C$Gson$Types.resolve(context, contextRawType, ownerType);
            boolean changed = newOwnerType != ownerType;
            Type[] args = original3.getActualTypeArguments();
            int length2 = args.length;
            for (int t = 0; t < length2; t++) {
                Type resolvedTypeArgument = C$Gson$Types.resolve(context, contextRawType, args[t]);
                if (resolvedTypeArgument != args[t]) {
                    if (!changed) {
                        args = (Type[]) args.clone();
                        changed = true;
                    }
                    args[t] = resolvedTypeArgument;
                }
            }
            if (changed) {
                return new ParameterizedTypeImpl(newOwnerType, original3.getRawType(), args);
            }
            return original3;
        } else if (!(toResolve instanceof WildcardType)) {
            return toResolve;
        } else {
            WildcardType original4 = (WildcardType) toResolve;
            Type[] originalLowerBound = original4.getLowerBounds();
            Type[] originalUpperBound = original4.getUpperBounds();
            if (originalLowerBound.length == 1) {
                Type lowerBound = C$Gson$Types.resolve(context, contextRawType, originalLowerBound[0]);
                if (lowerBound == originalLowerBound[0]) {
                    return original4;
                }
                return new WildcardTypeImpl(new Type[]{Object.class}, lowerBound instanceof WildcardType ? ((WildcardType) lowerBound).getLowerBounds() : new Type[]{lowerBound});
            } else if (originalUpperBound.length != 1) {
                return original4;
            } else {
                Type upperBound = C$Gson$Types.resolve(context, contextRawType, originalUpperBound[0]);
                if (upperBound == originalUpperBound[0]) {
                    return original4;
                }
                return new WildcardTypeImpl(upperBound instanceof WildcardType ? ((WildcardType) upperBound).getUpperBounds() : new Type[]{upperBound}, EMPTY_TYPE_ARRAY);
            }
        }
    }

    static void checkNotPrimitive(Type type) {
        boolean z = ((type instanceof Class) && ((Class) type).isPrimitive()) ? false : true;
        C$Gson$Preconditions.checkArgument(z);
    }
}
