package com.fasterxml.jackson.databind.type;

import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.LRUMap;
import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

public final class TypeFactory implements Serializable {
    private static final Class<?> CLS_BOOL = Boolean.TYPE;
    private static final Class<?> CLS_CLASS = Class.class;
    private static final Class<?> CLS_COMPARABLE = Comparable.class;
    private static final Class<?> CLS_ENUM = Enum.class;
    private static final Class<?> CLS_INT = Integer.TYPE;
    private static final Class<?> CLS_LONG = Long.TYPE;
    private static final Class<?> CLS_OBJECT = Object.class;
    private static final Class<?> CLS_STRING = String.class;
    protected static final SimpleType CORE_TYPE_BOOL = new SimpleType(CLS_BOOL);
    protected static final SimpleType CORE_TYPE_CLASS = new SimpleType(CLS_CLASS);
    protected static final SimpleType CORE_TYPE_COMPARABLE = new SimpleType(CLS_COMPARABLE);
    protected static final SimpleType CORE_TYPE_ENUM = new SimpleType(CLS_ENUM);
    protected static final SimpleType CORE_TYPE_INT = new SimpleType(CLS_INT);
    protected static final SimpleType CORE_TYPE_LONG = new SimpleType(CLS_LONG);
    protected static final SimpleType CORE_TYPE_OBJECT = new SimpleType(CLS_OBJECT);
    protected static final SimpleType CORE_TYPE_STRING = new SimpleType(CLS_STRING);
    protected static final TypeBindings EMPTY_BINDINGS = TypeBindings.emptyBindings();
    private static final JavaType[] NO_TYPES = new JavaType[0];
    protected static final TypeFactory instance = new TypeFactory();
    private static final long serialVersionUID = 1;
    protected final ClassLoader _classLoader;
    protected final TypeModifier[] _modifiers;
    protected final TypeParser _parser;
    protected final LRUMap<Object, JavaType> _typeCache;

    private TypeFactory() {
        this(null);
    }

    protected TypeFactory(LRUMap<Object, JavaType> typeCache) {
        if (typeCache == null) {
            typeCache = new LRUMap(16, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
        this._typeCache = typeCache;
        this._parser = new TypeParser(this);
        this._modifiers = null;
        this._classLoader = null;
    }

    protected TypeFactory(LRUMap<Object, JavaType> typeCache, TypeParser p, TypeModifier[] mods, ClassLoader classLoader) {
        if (typeCache == null) {
            typeCache = new LRUMap(16, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
        this._typeCache = typeCache;
        this._parser = p.withFactory(this);
        this._modifiers = mods;
        this._classLoader = classLoader;
    }

    public final TypeFactory withModifier(TypeModifier mod) {
        TypeModifier[] mods;
        LRUMap<Object, JavaType> typeCache = this._typeCache;
        if (mod == null) {
            mods = null;
            typeCache = null;
        } else {
            mods = this._modifiers == null ? new TypeModifier[]{mod} : (TypeModifier[]) ArrayBuilders.insertInListNoDup(this._modifiers, mod);
        }
        return new TypeFactory(typeCache, this._parser, mods, this._classLoader);
    }

    public final TypeFactory withClassLoader(ClassLoader classLoader) {
        return new TypeFactory(this._typeCache, this._parser, this._modifiers, classLoader);
    }

    public final TypeFactory withCache(LRUMap<Object, JavaType> cache) {
        return new TypeFactory(cache, this._parser, this._modifiers, this._classLoader);
    }

    public static TypeFactory defaultInstance() {
        return instance;
    }

    public final void clearCache() {
        this._typeCache.clear();
    }

    public final ClassLoader getClassLoader() {
        return this._classLoader;
    }

    public static JavaType unknownType() {
        return defaultInstance()._unknownType();
    }

    public static Class<?> rawClass(Type t) {
        if (t instanceof Class) {
            return (Class) t;
        }
        return defaultInstance().constructType(t).getRawClass();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Class<?> findClass(java.lang.String r7) throws java.lang.ClassNotFoundException {
        /*
        r6 = this;
        r4 = 46;
        r4 = r7.indexOf(r4);
        if (r4 >= 0) goto L_0x000f;
    L_0x0008:
        r2 = r6._findPrimitive(r7);
        if (r2 == 0) goto L_0x000f;
    L_0x000e:
        return r2;
    L_0x000f:
        r3 = 0;
        r1 = r6.getClassLoader();
        if (r1 != 0) goto L_0x001e;
    L_0x0016:
        r4 = java.lang.Thread.currentThread();
        r1 = r4.getContextClassLoader();
    L_0x001e:
        if (r1 == 0) goto L_0x002b;
    L_0x0020:
        r4 = 1;
        r2 = r6.classForName(r7, r4, r1);	 Catch:{ Exception -> 0x0026 }
        goto L_0x000e;
    L_0x0026:
        r0 = move-exception;
        r3 = com.fasterxml.jackson.databind.util.ClassUtil.getRootCause(r0);
    L_0x002b:
        r2 = r6.classForName(r7);	 Catch:{ Exception -> 0x0030 }
        goto L_0x000e;
    L_0x0030:
        r0 = move-exception;
        if (r3 != 0) goto L_0x0037;
    L_0x0033:
        r3 = com.fasterxml.jackson.databind.util.ClassUtil.getRootCause(r0);
    L_0x0037:
        r4 = r3 instanceof java.lang.RuntimeException;
        if (r4 == 0) goto L_0x003e;
    L_0x003b:
        r3 = (java.lang.RuntimeException) r3;
        throw r3;
    L_0x003e:
        r4 = new java.lang.ClassNotFoundException;
        r5 = r3.getMessage();
        r4.<init>(r5, r3);
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.type.TypeFactory.findClass(java.lang.String):java.lang.Class<?>");
    }

    protected final Class<?> classForName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
        return Class.forName(name, true, loader);
    }

    protected final Class<?> classForName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    protected final Class<?> _findPrimitive(String className) {
        if ("int".equals(className)) {
            return Integer.TYPE;
        }
        if ("long".equals(className)) {
            return Long.TYPE;
        }
        if ("float".equals(className)) {
            return Float.TYPE;
        }
        if ("double".equals(className)) {
            return Double.TYPE;
        }
        if ("boolean".equals(className)) {
            return Boolean.TYPE;
        }
        if ("byte".equals(className)) {
            return Byte.TYPE;
        }
        if ("char".equals(className)) {
            return Character.TYPE;
        }
        if ("short".equals(className)) {
            return Short.TYPE;
        }
        if ("void".equals(className)) {
            return Void.TYPE;
        }
        return null;
    }

    public final JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
        Class<?> rawBase = baseType.getRawClass();
        if (rawBase == subclass) {
            return baseType;
        }
        JavaType newType;
        if (rawBase == Object.class) {
            newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
        } else if (!rawBase.isAssignableFrom(subclass)) {
            throw new IllegalArgumentException(String.format("Class %s not subtype of %s", new Object[]{subclass.getName(), baseType}));
        } else if (baseType.getBindings().isEmpty()) {
            newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
        } else {
            if (baseType.isContainerType()) {
                if (baseType.isMapLikeType()) {
                    if (subclass == HashMap.class || subclass == LinkedHashMap.class || subclass == EnumMap.class || subclass == TreeMap.class) {
                        newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
                    }
                } else if (baseType.isCollectionLikeType()) {
                    if (subclass == ArrayList.class || subclass == LinkedList.class || subclass == HashSet.class || subclass == TreeSet.class) {
                        newType = _fromClass(null, subclass, TypeBindings.create((Class) subclass, baseType.getContentType()));
                    } else if (rawBase == EnumSet.class) {
                        return baseType;
                    }
                }
            }
            int typeParamCount = subclass.getTypeParameters().length;
            if (typeParamCount == 0) {
                newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
            } else {
                TypeBindings tb = _bindingsForSubtype(baseType, typeParamCount, subclass);
                if (baseType.isInterface()) {
                    newType = baseType.refine(subclass, tb, null, new JavaType[]{baseType});
                } else {
                    newType = baseType.refine(subclass, tb, baseType, NO_TYPES);
                }
                if (newType == null) {
                    newType = _fromClass(null, subclass, tb);
                }
            }
        }
        return newType;
    }

    private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass) {
        int baseCount = baseType.containedTypeCount();
        if (baseCount != typeParamCount) {
            return TypeBindings.emptyBindings();
        }
        if (typeParamCount == 1) {
            return TypeBindings.create((Class) subclass, baseType.containedType(0));
        }
        if (typeParamCount == 2) {
            return TypeBindings.create(subclass, baseType.containedType(0), baseType.containedType(1));
        }
        List types = new ArrayList(baseCount);
        for (int i = 0; i < baseCount; i++) {
            types.add(baseType.containedType(i));
        }
        return TypeBindings.create((Class) subclass, types);
    }

    public final JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass) {
        Class<?> rawBase = baseType.getRawClass();
        if (rawBase == superClass) {
            return baseType;
        }
        JavaType superType = baseType.findSuperType(superClass);
        if (superType != null) {
            return superType;
        }
        if (superClass.isAssignableFrom(rawBase)) {
            throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", new Object[]{superClass.getName(), baseType}));
        }
        throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", new Object[]{superClass.getName(), baseType}));
    }

    public final JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
        return this._parser.parse(canonical);
    }

    public final JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
        JavaType match = type.findSuperType(expType);
        if (match == null) {
            return NO_TYPES;
        }
        return match.getBindings().typeParameterArray();
    }

    @Deprecated
    public final JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
        return findTypeParameters(constructType((Type) clz, bindings), (Class) expType);
    }

    @Deprecated
    public final JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
        return findTypeParameters(constructType((Type) clz), (Class) expType);
    }

    public final JavaType moreSpecificType(JavaType type1, JavaType type2) {
        if (type1 == null) {
            return type2;
        }
        if (type2 == null) {
            return type1;
        }
        Class<?> raw1 = type1.getRawClass();
        Class<?> raw2 = type2.getRawClass();
        if (raw1 == raw2 || !raw1.isAssignableFrom(raw2)) {
            return type1;
        }
        return type2;
    }

    public final JavaType constructType(Type type) {
        return _fromAny(null, type, EMPTY_BINDINGS);
    }

    public final JavaType constructType(Type type, TypeBindings bindings) {
        return _fromAny(null, type, bindings);
    }

    public final JavaType constructType(TypeReference<?> typeRef) {
        return _fromAny(null, typeRef.getType(), EMPTY_BINDINGS);
    }

    @Deprecated
    public final JavaType constructType(Type type, Class<?> contextClass) {
        return _fromAny(null, type, contextClass == null ? TypeBindings.emptyBindings() : constructType((Type) contextClass).getBindings());
    }

    @Deprecated
    public final JavaType constructType(Type type, JavaType contextType) {
        return _fromAny(null, type, contextType == null ? TypeBindings.emptyBindings() : contextType.getBindings());
    }

    public final ArrayType constructArrayType(Class<?> elementType) {
        return ArrayType.construct(_fromAny(null, elementType, null), null);
    }

    public final ArrayType constructArrayType(JavaType elementType) {
        return ArrayType.construct(elementType, null);
    }

    public final CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return constructCollectionType((Class) collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
    }

    public final CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
        return (CollectionType) _fromClass(null, collectionClass, TypeBindings.create((Class) collectionClass, elementType));
    }

    public final CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
        return constructCollectionLikeType((Class) collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
    }

    public final CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
        JavaType type = _fromClass(null, collectionClass, TypeBindings.createIfNeeded((Class) collectionClass, elementType));
        if (type instanceof CollectionLikeType) {
            return (CollectionLikeType) type;
        }
        return CollectionLikeType.upgradeFrom(type, elementType);
    }

    public final MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        JavaType vt;
        JavaType kt;
        if (mapClass == Properties.class) {
            vt = CORE_TYPE_STRING;
            kt = vt;
        } else {
            kt = _fromClass(null, keyClass, EMPTY_BINDINGS);
            vt = _fromClass(null, valueClass, EMPTY_BINDINGS);
        }
        return constructMapType((Class) mapClass, kt, vt);
    }

    public final MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
        return (MapType) _fromClass(null, mapClass, TypeBindings.create((Class) mapClass, new JavaType[]{keyType, valueType}));
    }

    public final MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return constructMapLikeType((Class) mapClass, _fromClass(null, keyClass, EMPTY_BINDINGS), _fromClass(null, valueClass, EMPTY_BINDINGS));
    }

    public final MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
        JavaType type = _fromClass(null, mapClass, TypeBindings.createIfNeeded((Class) mapClass, new JavaType[]{keyType, valueType}));
        if (type instanceof MapLikeType) {
            return (MapLikeType) type;
        }
        return MapLikeType.upgradeFrom(type, keyType, valueType);
    }

    public final JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
        return _fromClass(null, rawType, TypeBindings.create((Class) rawType, parameterTypes));
    }

    @Deprecated
    public final JavaType constructSimpleType(Class<?> rawType, Class<?> cls, JavaType[] parameterTypes) {
        return constructSimpleType(rawType, parameterTypes);
    }

    public final JavaType constructReferenceType(Class<?> rawType, JavaType referredType) {
        return ReferenceType.construct(rawType, null, null, null, referredType);
    }

    @Deprecated
    public final JavaType uncheckedSimpleType(Class<?> cls) {
        return _constructSimple(cls, EMPTY_BINDINGS, null, null);
    }

    public final JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
        int len = parameterClasses.length;
        JavaType[] pt = new JavaType[len];
        for (int i = 0; i < len; i++) {
            pt[i] = _fromClass(null, parameterClasses[i], null);
        }
        return constructParametricType((Class) parametrized, pt);
    }

    public final JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
        return _fromClass(null, rawType, TypeBindings.create((Class) rawType, parameterTypes));
    }

    public final JavaType constructParametrizedType(Class<?> parametrized, Class<?> cls, JavaType... parameterTypes) {
        return constructParametricType((Class) parametrized, parameterTypes);
    }

    public final JavaType constructParametrizedType(Class<?> parametrized, Class<?> cls, Class<?>... parameterClasses) {
        return constructParametricType((Class) parametrized, (Class[]) parameterClasses);
    }

    public final CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
        return constructCollectionType((Class) collectionClass, unknownType());
    }

    public final CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
        return constructCollectionLikeType((Class) collectionClass, unknownType());
    }

    public final MapType constructRawMapType(Class<? extends Map> mapClass) {
        return constructMapType((Class) mapClass, unknownType(), unknownType());
    }

    public final MapLikeType constructRawMapLikeType(Class<?> mapClass) {
        return constructMapLikeType((Class) mapClass, unknownType(), unknownType());
    }

    private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        JavaType vt;
        JavaType kt;
        if (rawClass == Properties.class) {
            vt = CORE_TYPE_STRING;
            kt = vt;
        } else {
            List<JavaType> typeParams = bindings.getTypeParameters();
            switch (typeParams.size()) {
                case 0:
                    vt = _unknownType();
                    kt = vt;
                    break;
                case 2:
                    kt = (JavaType) typeParams.get(0);
                    vt = (JavaType) typeParams.get(1);
                    break;
                default:
                    throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": can not determine type parameters");
            }
        }
        return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
    }

    private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        JavaType ct;
        List<JavaType> typeParams = bindings.getTypeParameters();
        if (typeParams.isEmpty()) {
            ct = _unknownType();
        } else if (typeParams.size() == 1) {
            ct = (JavaType) typeParams.get(0);
        } else {
            throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": can not determine type parameters");
        }
        return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
    }

    private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        JavaType ct;
        List<JavaType> typeParams = bindings.getTypeParameters();
        if (typeParams.isEmpty()) {
            ct = _unknownType();
        } else if (typeParams.size() == 1) {
            ct = (JavaType) typeParams.get(0);
        } else {
            throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": can not determine type parameters");
        }
        return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
    }

    protected final JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        if (bindings.isEmpty()) {
            JavaType result = _findWellKnownSimple(raw);
            if (result != null) {
                return result;
            }
        }
        return _newSimpleType(raw, bindings, superClass, superInterfaces);
    }

    protected final JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        return new SimpleType(raw, bindings, superClass, superInterfaces);
    }

    protected final JavaType _unknownType() {
        return CORE_TYPE_OBJECT;
    }

    protected final JavaType _findWellKnownSimple(Class<?> clz) {
        if (clz.isPrimitive()) {
            if (clz == CLS_BOOL) {
                return CORE_TYPE_BOOL;
            }
            if (clz == CLS_INT) {
                return CORE_TYPE_INT;
            }
            if (clz == CLS_LONG) {
                return CORE_TYPE_LONG;
            }
        } else if (clz == CLS_STRING) {
            return CORE_TYPE_STRING;
        } else {
            if (clz == CLS_OBJECT) {
                return CORE_TYPE_OBJECT;
            }
        }
        return null;
    }

    protected final JavaType _fromAny(ClassStack context, Type type, TypeBindings bindings) {
        JavaType resultType;
        if (type instanceof Class) {
            resultType = _fromClass(context, (Class) type, EMPTY_BINDINGS);
        } else if (type instanceof ParameterizedType) {
            resultType = _fromParamType(context, (ParameterizedType) type, bindings);
        } else if (type instanceof JavaType) {
            return (JavaType) type;
        } else {
            if (type instanceof GenericArrayType) {
                resultType = _fromArrayType(context, (GenericArrayType) type, bindings);
            } else if (type instanceof TypeVariable) {
                resultType = _fromVariable(context, (TypeVariable) type, bindings);
            } else if (type instanceof WildcardType) {
                resultType = _fromWildcard(context, (WildcardType) type, bindings);
            } else {
                throw new IllegalArgumentException("Unrecognized Type: " + (type == null ? "[null]" : type.toString()));
            }
        }
        if (this._modifiers != null) {
            TypeBindings b = resultType.getBindings();
            if (b == null) {
                b = EMPTY_BINDINGS;
            }
            for (TypeModifier mod : this._modifiers) {
                JavaType t = mod.modifyType(resultType, type, b, this);
                if (t == null) {
                    throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", new Object[]{mod, mod.getClass().getName(), resultType}));
                }
                resultType = t;
            }
        }
        return resultType;
    }

    protected final JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings) {
        JavaType result = _findWellKnownSimple(rawType);
        if (result != null) {
            return result;
        }
        Object obj;
        if (bindings == null || bindings.isEmpty()) {
            obj = rawType;
        } else {
            obj = bindings.asKey(rawType);
        }
        result = (JavaType) this._typeCache.get(obj);
        if (result != null) {
            return result;
        }
        if (context == null) {
            context = new ClassStack(rawType);
        } else {
            ClassStack prev = context.find(rawType);
            if (prev != null) {
                JavaType resolvedRecursiveType = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
                prev.addSelfReference(resolvedRecursiveType);
                return resolvedRecursiveType;
            }
            context = context.child(rawType);
        }
        if (rawType.isArray()) {
            result = ArrayType.construct(_fromAny(context, rawType.getComponentType(), bindings), bindings);
        } else {
            JavaType superClass;
            JavaType[] superInterfaces;
            if (rawType.isInterface()) {
                superClass = null;
                superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
            } else {
                superClass = _resolveSuperClass(context, rawType, bindings);
                superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
            }
            if (rawType == Properties.class) {
                result = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
            } else if (superClass != null) {
                result = superClass.refine(rawType, bindings, superClass, superInterfaces);
            }
            if (result == null) {
                result = _fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
                if (result == null) {
                    result = _fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
                    if (result == null) {
                        result = _newSimpleType(rawType, bindings, superClass, superInterfaces);
                    }
                }
            }
        }
        context.resolveSelfReferences(result);
        if (!result.hasHandlers()) {
            this._typeCache.putIfAbsent(obj, result);
        }
        return result;
    }

    protected final JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
        Type parent = ClassUtil.getGenericSuperclass(rawType);
        if (parent == null) {
            return null;
        }
        return _fromAny(context, parent, parentBindings);
    }

    protected final JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
        Type[] types = ClassUtil.getGenericInterfaces(rawType);
        if (types == null || types.length == 0) {
            return NO_TYPES;
        }
        int len = types.length;
        JavaType[] resolved = new JavaType[len];
        for (int i = 0; i < len; i++) {
            resolved[i] = _fromAny(context, types[i], parentBindings);
        }
        return resolved;
    }

    protected final JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        if (bindings == null) {
            bindings = TypeBindings.emptyBindings();
        }
        if (rawType == Map.class) {
            return _mapType(rawType, bindings, superClass, superInterfaces);
        }
        if (rawType == Collection.class) {
            return _collectionType(rawType, bindings, superClass, superInterfaces);
        }
        if (rawType == AtomicReference.class) {
            return _referenceType(rawType, bindings, superClass, superInterfaces);
        }
        return null;
    }

    protected final JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        for (JavaType refine : superInterfaces) {
            JavaType result = refine.refine(rawType, bindings, superClass, superInterfaces);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected final JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
        Class rawType = (Class) ptype.getRawType();
        if (rawType == CLS_ENUM) {
            return CORE_TYPE_ENUM;
        }
        if (rawType == CLS_COMPARABLE) {
            return CORE_TYPE_COMPARABLE;
        }
        if (rawType == CLS_CLASS) {
            return CORE_TYPE_CLASS;
        }
        TypeBindings newBindings;
        Type[] args = ptype.getActualTypeArguments();
        int paramCount = args == null ? 0 : args.length;
        if (paramCount == 0) {
            newBindings = EMPTY_BINDINGS;
        } else {
            JavaType[] pt = new JavaType[paramCount];
            for (int i = 0; i < paramCount; i++) {
                pt[i] = _fromAny(context, args[i], parentBindings);
            }
            newBindings = TypeBindings.create(rawType, pt);
        }
        return _fromClass(context, rawType, newBindings);
    }

    protected final JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings) {
        return ArrayType.construct(_fromAny(context, type.getGenericComponentType(), bindings), bindings);
    }

    protected final JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings) {
        String name = var.getName();
        JavaType type = bindings.findBoundType(name);
        if (type != null) {
            return type;
        }
        if (bindings.hasUnbound(name)) {
            return CORE_TYPE_OBJECT;
        }
        return _fromAny(context, var.getBounds()[0], bindings.withUnboundVariable(name));
    }

    protected final JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings) {
        return _fromAny(context, type.getUpperBounds()[0], bindings);
    }
}
