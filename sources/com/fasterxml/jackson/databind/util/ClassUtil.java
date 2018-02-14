package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.annotation.NoClass;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public final class ClassUtil {
    private static final Class<?> CLS_OBJECT = Object.class;
    private static final EmptyIterator<?> EMPTY_ITERATOR = new EmptyIterator();
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
    private static final Ctor[] NO_CTORS = new Ctor[0];

    public static final class Ctor {
        private Annotation[] _annotations;
        public final Constructor<?> _ctor;
        private Annotation[][] _paramAnnotations;
        private int _paramCount = -1;

        public Ctor(Constructor<?> ctor) {
            this._ctor = ctor;
        }

        public final Constructor<?> getConstructor() {
            return this._ctor;
        }

        public final int getParamCount() {
            int c = this._paramCount;
            if (c >= 0) {
                return c;
            }
            c = this._ctor.getParameterTypes().length;
            this._paramCount = c;
            return c;
        }

        public final Class<?> getDeclaringClass() {
            return this._ctor.getDeclaringClass();
        }

        public final Annotation[] getDeclaredAnnotations() {
            Annotation[] result = this._annotations;
            if (result != null) {
                return result;
            }
            result = this._ctor.getDeclaredAnnotations();
            this._annotations = result;
            return result;
        }

        public final Annotation[][] getParameterAnnotations() {
            Annotation[][] result = this._paramAnnotations;
            if (result != null) {
                return result;
            }
            result = this._ctor.getParameterAnnotations();
            this._paramAnnotations = result;
            return result;
        }
    }

    private static final class EmptyIterator<T> implements Iterator<T> {
        private EmptyIterator() {
        }

        public final boolean hasNext() {
            return false;
        }

        public final T next() {
            throw new NoSuchElementException();
        }

        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class EnumTypeLocator {
        static final EnumTypeLocator instance = new EnumTypeLocator();
        private final Field enumMapTypeField = locateField(EnumMap.class, "elementType", Class.class);
        private final Field enumSetTypeField = locateField(EnumSet.class, "elementType", Class.class);

        private EnumTypeLocator() {
        }

        public Class<? extends Enum<?>> enumTypeFor(EnumSet<?> set) {
            if (this.enumSetTypeField != null) {
                return (Class) get(set, this.enumSetTypeField);
            }
            throw new IllegalStateException("Can not figure out type for EnumSet (odd JDK platform?)");
        }

        public Class<? extends Enum<?>> enumTypeFor(EnumMap<?, ?> set) {
            if (this.enumMapTypeField != null) {
                return (Class) get(set, this.enumMapTypeField);
            }
            throw new IllegalStateException("Can not figure out type for EnumMap (odd JDK platform?)");
        }

        private Object get(Object bean, Field field) {
            try {
                return field.get(bean);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        private static Field locateField(Class<?> fromClass, String expectedName, Class<?> type) {
            int i$;
            Field found = null;
            Field[] fields = ClassUtil.getDeclaredFields(fromClass);
            Field[] arr$ = fields;
            int len$ = fields.length;
            for (i$ = 0; i$ < len$; i$++) {
                Field f = arr$[i$];
                if (expectedName.equals(f.getName()) && f.getType() == type) {
                    found = f;
                    break;
                }
            }
            if (found == null) {
                arr$ = fields;
                len$ = fields.length;
                for (i$ = 0; i$ < len$; i$++) {
                    f = arr$[i$];
                    if (f.getType() == type) {
                        if (found != null) {
                            return null;
                        }
                        found = f;
                    }
                }
            }
            if (found != null) {
                try {
                    found.setAccessible(true);
                } catch (Throwable th) {
                }
            }
            return found;
        }
    }

    public static <T> Iterator<T> emptyIterator() {
        return EMPTY_ITERATOR;
    }

    public static List<JavaType> findSuperTypes(JavaType type, Class<?> endBefore, boolean addClassItself) {
        if (type == null || type.hasRawClass(endBefore) || type.hasRawClass(Object.class)) {
            return Collections.emptyList();
        }
        List<JavaType> result = new ArrayList(8);
        _addSuperTypes(type, endBefore, result, addClassItself);
        return result;
    }

    public static List<Class<?>> findRawSuperTypes(Class<?> cls, Class<?> endBefore, boolean addClassItself) {
        if (cls == null || cls == endBefore || cls == Object.class) {
            return Collections.emptyList();
        }
        List<Class<?>> result = new ArrayList(8);
        _addRawSuperTypes(cls, endBefore, result, addClassItself);
        return result;
    }

    public static List<Class<?>> findSuperClasses(Class<?> cls, Class<?> endBefore, boolean addClassItself) {
        List<Class<?>> result = new LinkedList();
        if (cls != null && cls != endBefore) {
            if (addClassItself) {
                result.add(cls);
            }
            while (true) {
                cls = cls.getSuperclass();
                if (cls == null || cls == endBefore) {
                    break;
                }
                result.add(cls);
            }
        }
        return result;
    }

    @Deprecated
    public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore) {
        return findSuperTypes((Class) cls, (Class) endBefore, new ArrayList(8));
    }

    @Deprecated
    public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result) {
        _addRawSuperTypes(cls, endBefore, result, false);
        return result;
    }

    private static void _addSuperTypes(JavaType type, Class<?> endBefore, Collection<JavaType> result, boolean addClassItself) {
        while (type != null) {
            Class<?> cls = type.getRawClass();
            if (cls != endBefore && cls != Object.class) {
                if (addClassItself) {
                    if (!result.contains(type)) {
                        result.add(type);
                    } else {
                        return;
                    }
                }
                for (JavaType intCls : type.getInterfaces()) {
                    _addSuperTypes(intCls, endBefore, result, true);
                }
                type = type.getSuperClass();
                addClassItself = true;
            } else {
                return;
            }
        }
    }

    private static void _addRawSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself) {
        while (cls != endBefore && cls != null && cls != Object.class) {
            if (addClassItself) {
                if (!result.contains(cls)) {
                    result.add(cls);
                } else {
                    return;
                }
            }
            for (Class<?> intCls : _interfaces(cls)) {
                _addRawSuperTypes(intCls, endBefore, result, true);
            }
            cls = cls.getSuperclass();
            addClassItself = true;
        }
    }

    public static String canBeABeanType(Class<?> type) {
        if (type.isAnnotation()) {
            return "annotation";
        }
        if (type.isArray()) {
            return "array";
        }
        if (type.isEnum()) {
            return "enum";
        }
        if (type.isPrimitive()) {
            return "primitive";
        }
        return null;
    }

    public static String isLocalType(Class<?> type, boolean allowNonStatic) {
        try {
            if (hasEnclosingMethod(type)) {
                return "local/anonymous";
            }
            if (!(allowNonStatic || Modifier.isStatic(type.getModifiers()) || getEnclosingClass(type) == null)) {
                return "non-static member class";
            }
            return null;
        } catch (SecurityException e) {
        } catch (NullPointerException e2) {
        }
    }

    public static Class<?> getOuterClass(Class<?> type) {
        Class<?> cls = null;
        try {
            if (!(hasEnclosingMethod(type) || Modifier.isStatic(type.getModifiers()))) {
                cls = getEnclosingClass(type);
            }
        } catch (SecurityException e) {
        }
        return cls;
    }

    public static boolean isProxyType(Class<?> type) {
        String name = type.getName();
        if (name.startsWith("net.sf.cglib.proxy.") || name.startsWith("org.hibernate.proxy.")) {
            return true;
        }
        return false;
    }

    public static boolean isConcrete(Class<?> type) {
        return (type.getModifiers() & 1536) == 0;
    }

    public static boolean isConcrete(Member member) {
        return (member.getModifiers() & 1536) == 0;
    }

    public static boolean isCollectionMapOrArray(Class<?> type) {
        if (type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    public static String getClassDescription(Object classOrInstance) {
        if (classOrInstance == null) {
            return "unknown";
        }
        return (classOrInstance instanceof Class ? (Class) classOrInstance : classOrInstance.getClass()).getName();
    }

    @Deprecated
    public static Class<?> findClass(String className) throws ClassNotFoundException {
        if (className.indexOf(46) < 0) {
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
        }
        Throwable prob = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                return Class.forName(className, true, loader);
            } catch (Exception e) {
                prob = getRootCause(e);
            }
        }
        try {
            return Class.forName(className);
        } catch (Exception e2) {
            if (prob == null) {
                prob = getRootCause(e2);
            }
            if (prob instanceof RuntimeException) {
                throw ((RuntimeException) prob);
            }
            throw new ClassNotFoundException(prob.getMessage(), prob);
        }
    }

    @Deprecated
    public static boolean hasGetterSignature(Method m) {
        if (Modifier.isStatic(m.getModifiers())) {
            return false;
        }
        Class<?>[] pts = m.getParameterTypes();
        if ((pts == null || pts.length == 0) && Void.TYPE != m.getReturnType()) {
            return true;
        }
        return false;
    }

    public static Throwable getRootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    public static void throwRootCause(Throwable t) throws Exception {
        t = getRootCause(t);
        if (t instanceof Exception) {
            throw ((Exception) t);
        }
        throw ((Error) t);
    }

    public static Throwable throwRootCauseIfIOE(Throwable t) throws IOException {
        t = getRootCause(t);
        if (!(t instanceof IOException)) {
            return t;
        }
        throw ((IOException) t);
    }

    public static void throwAsIAE(Throwable t) {
        throwAsIAE(t, t.getMessage());
    }

    public static void throwAsIAE(Throwable t, String msg) {
        if (t instanceof RuntimeException) {
            throw ((RuntimeException) t);
        } else if (t instanceof Error) {
            throw ((Error) t);
        } else {
            throw new IllegalArgumentException(msg, t);
        }
    }

    public static void unwrapAndThrowAsIAE(Throwable t) {
        throwAsIAE(getRootCause(t));
    }

    public static void unwrapAndThrowAsIAE(Throwable t, String msg) {
        throwAsIAE(getRootCause(t), msg);
    }

    public static void closeOnFailAndThrowAsIAE(JsonGenerator g, Exception fail) throws IOException {
        g.disable(Feature.AUTO_CLOSE_JSON_CONTENT);
        try {
            g.close();
        } catch (Exception e) {
            fail.addSuppressed(e);
        }
        if (fail instanceof IOException) {
            throw ((IOException) fail);
        } else if (fail instanceof RuntimeException) {
            throw ((RuntimeException) fail);
        } else {
            throw new RuntimeException(fail);
        }
    }

    public static void closeOnFailAndThrowAsIAE(JsonGenerator g, Closeable toClose, Exception fail) throws IOException {
        if (g != null) {
            g.disable(Feature.AUTO_CLOSE_JSON_CONTENT);
            try {
                g.close();
            } catch (Exception e) {
                fail.addSuppressed(e);
            }
        }
        if (toClose != null) {
            try {
                toClose.close();
            } catch (Exception e2) {
                fail.addSuppressed(e2);
            }
        }
        if (fail instanceof IOException) {
            throw ((IOException) fail);
        } else if (fail instanceof RuntimeException) {
            throw ((RuntimeException) fail);
        } else {
            throw new RuntimeException(fail);
        }
    }

    public static <T> T createInstance(Class<T> cls, boolean canFixAccess) throws IllegalArgumentException {
        Constructor<T> ctor = findConstructor(cls, canFixAccess);
        if (ctor == null) {
            throw new IllegalArgumentException("Class " + cls.getName() + " has no default (no arg) constructor");
        }
        try {
            return ctor.newInstance(new Object[0]);
        } catch (Exception e) {
            unwrapAndThrowAsIAE(e, "Failed to instantiate class " + cls.getName() + ", problem: " + e.getMessage());
            return null;
        }
    }

    public static <T> Constructor<T> findConstructor(Class<T> cls, boolean canFixAccess) throws IllegalArgumentException {
        try {
            Constructor<T> ctor = cls.getDeclaredConstructor(new Class[0]);
            if (canFixAccess) {
                checkAndFixAccess(ctor);
                return ctor;
            } else if (Modifier.isPublic(ctor.getModifiers())) {
                return ctor;
            } else {
                throw new IllegalArgumentException("Default constructor for " + cls.getName() + " is not accessible (non-public?): not allowed to try modify access via Reflection: can not instantiate type");
            }
        } catch (NoSuchMethodException e) {
            return null;
        } catch (Exception e2) {
            unwrapAndThrowAsIAE(e2, "Failed to find default constructor of class " + cls.getName() + ", problem: " + e2.getMessage());
            return null;
        }
    }

    public static Object defaultValue(Class<?> cls) {
        if (cls == Integer.TYPE) {
            return Integer.valueOf(0);
        }
        if (cls == Long.TYPE) {
            return Long.valueOf(0);
        }
        if (cls == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (cls == Double.TYPE) {
            return Double.valueOf(0.0d);
        }
        if (cls == Float.TYPE) {
            return Float.valueOf(0.0f);
        }
        if (cls == Byte.TYPE) {
            return Byte.valueOf((byte) 0);
        }
        if (cls == Short.TYPE) {
            return Short.valueOf((short) 0);
        }
        if (cls == Character.TYPE) {
            return Character.valueOf('\u0000');
        }
        throw new IllegalArgumentException("Class " + cls.getName() + " is not a primitive type");
    }

    public static Class<?> wrapperType(Class<?> primitiveType) {
        if (primitiveType == Integer.TYPE) {
            return Integer.class;
        }
        if (primitiveType == Long.TYPE) {
            return Long.class;
        }
        if (primitiveType == Boolean.TYPE) {
            return Boolean.class;
        }
        if (primitiveType == Double.TYPE) {
            return Double.class;
        }
        if (primitiveType == Float.TYPE) {
            return Float.class;
        }
        if (primitiveType == Byte.TYPE) {
            return Byte.class;
        }
        if (primitiveType == Short.TYPE) {
            return Short.class;
        }
        if (primitiveType == Character.TYPE) {
            return Character.class;
        }
        throw new IllegalArgumentException("Class " + primitiveType.getName() + " is not a primitive type");
    }

    public static Class<?> primitiveType(Class<?> type) {
        if (type.isPrimitive()) {
            return type;
        }
        if (type == Integer.class) {
            return Integer.TYPE;
        }
        if (type == Long.class) {
            return Long.TYPE;
        }
        if (type == Boolean.class) {
            return Boolean.TYPE;
        }
        if (type == Double.class) {
            return Double.TYPE;
        }
        if (type == Float.class) {
            return Float.TYPE;
        }
        if (type == Byte.class) {
            return Byte.TYPE;
        }
        if (type == Short.class) {
            return Short.TYPE;
        }
        if (type == Character.class) {
            return Character.TYPE;
        }
        return null;
    }

    @Deprecated
    public static void checkAndFixAccess(Member member) {
        checkAndFixAccess(member, false);
    }

    public static void checkAndFixAccess(Member member, boolean force) {
        AccessibleObject ao = (AccessibleObject) member;
        if (!force) {
            try {
                if (Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                    return;
                }
            } catch (SecurityException se) {
                if (!ao.isAccessible()) {
                    throw new IllegalArgumentException("Can not access " + member + " (from class " + member.getDeclaringClass().getName() + "; failed to set access: " + se.getMessage());
                }
                return;
            }
        }
        ao.setAccessible(true);
    }

    public static Class<? extends Enum<?>> findEnumType(EnumSet<?> s) {
        if (s.isEmpty()) {
            return EnumTypeLocator.instance.enumTypeFor((EnumSet) s);
        }
        return findEnumType((Enum) s.iterator().next());
    }

    public static Class<? extends Enum<?>> findEnumType(EnumMap<?, ?> m) {
        if (m.isEmpty()) {
            return EnumTypeLocator.instance.enumTypeFor((EnumMap) m);
        }
        return findEnumType((Enum) m.keySet().iterator().next());
    }

    public static Class<? extends Enum<?>> findEnumType(Enum<?> en) {
        Class<?> ec = en.getClass();
        if (ec.getSuperclass() != Enum.class) {
            return ec.getSuperclass();
        }
        return ec;
    }

    public static Class<? extends Enum<?>> findEnumType(Class<?> cls) {
        if (cls.getSuperclass() != Enum.class) {
            return cls.getSuperclass();
        }
        return cls;
    }

    public static <T extends Annotation> Enum<?> findFirstAnnotatedEnumValue(Class<Enum<?>> enumClass, Class<T> annotationClass) {
        Field[] fields = getDeclaredFields(enumClass);
        Field[] fieldArr = fields;
        int len$ = fields.length;
        for (int i = 0; i < len$; i++) {
            Field field = fieldArr[i];
            if (field.isEnumConstant() && field.getAnnotation(annotationClass) != null) {
                String name = field.getName();
                for (Enum<?> enumValue : (Enum[]) enumClass.getEnumConstants()) {
                    if (name.equals(enumValue.name())) {
                        return enumValue;
                    }
                }
                continue;
            }
        }
        return null;
    }

    public static boolean isJacksonStdImpl(Object impl) {
        return impl != null && isJacksonStdImpl(impl.getClass());
    }

    public static boolean isJacksonStdImpl(Class<?> implClass) {
        return implClass.getAnnotation(JacksonStdImpl.class) != null;
    }

    public static boolean isBogusClass(Class<?> cls) {
        return cls == Void.class || cls == Void.TYPE || cls == NoClass.class;
    }

    public static boolean isNonStaticInnerClass(Class<?> cls) {
        return (Modifier.isStatic(cls.getModifiers()) || getEnclosingClass(cls) == null) ? false : true;
    }

    public static boolean isObjectOrPrimitive(Class<?> cls) {
        return cls == CLS_OBJECT || cls.isPrimitive();
    }

    public static String getPackageName(Class<?> cls) {
        Package pkg = cls.getPackage();
        return pkg == null ? null : pkg.getName();
    }

    public static boolean hasEnclosingMethod(Class<?> cls) {
        return (isObjectOrPrimitive(cls) || cls.getEnclosingMethod() == null) ? false : true;
    }

    public static Field[] getDeclaredFields(Class<?> cls) {
        return cls.getDeclaredFields();
    }

    public static Method[] getDeclaredMethods(Class<?> cls) {
        return cls.getDeclaredMethods();
    }

    public static Annotation[] findClassAnnotations(Class<?> cls) {
        if (isObjectOrPrimitive(cls)) {
            return NO_ANNOTATIONS;
        }
        return cls.getDeclaredAnnotations();
    }

    public static Ctor[] getConstructors(Class<?> cls) {
        if (cls.isInterface() || isObjectOrPrimitive(cls)) {
            return NO_CTORS;
        }
        Constructor<?>[] rawCtors = cls.getDeclaredConstructors();
        int len = rawCtors.length;
        Ctor[] result = new Ctor[len];
        for (int i = 0; i < len; i++) {
            result[i] = new Ctor(rawCtors[i]);
        }
        return result;
    }

    public static Class<?> getDeclaringClass(Class<?> cls) {
        return isObjectOrPrimitive(cls) ? null : cls.getDeclaringClass();
    }

    public static Type getGenericSuperclass(Class<?> cls) {
        return cls.getGenericSuperclass();
    }

    public static Type[] getGenericInterfaces(Class<?> cls) {
        return cls.getGenericInterfaces();
    }

    public static Class<?> getEnclosingClass(Class<?> cls) {
        return isObjectOrPrimitive(cls) ? null : cls.getEnclosingClass();
    }

    private static Class<?>[] _interfaces(Class<?> cls) {
        return cls.getInterfaces();
    }
}
