package org.springframework.util;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class ClassUtils {
    private static final Map<String, Class<?>> commonClassCache = new HashMap(32);
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap(32);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap(8);
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap(8);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
        primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
        for (Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
            registerCommonClasses((Class) entry.getKey());
        }
        Set<Class<?>> primitiveTypes = new HashSet(32);
        primitiveTypes.addAll(primitiveWrapperTypeMap.values());
        primitiveTypes.addAll(Arrays.asList(new Class[]{boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class}));
        primitiveTypes.add(Void.TYPE);
        for (Class<?> primitiveType : primitiveTypes) {
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }
        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Object.class, Object[].class, Class.class, Class[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class, StackTraceElement.class, StackTraceElement[].class);
    }

    private static void registerCommonClasses(Class<?>... commonClasses) {
        Class<?>[] arr$ = commonClasses;
        int len$ = commonClasses.length;
        for (int i$ = 0; i$ < len$; i$++) {
            Class<?> clazz = arr$[i$];
            commonClassCache.put(clazz.getName(), clazz);
        }
    }

    private static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable th) {
        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable th2) {
                }
            }
        }
        return cl;
    }

    private static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");
        Class<?> cls = null;
        if (name != null && name.length() <= 8) {
            cls = (Class) primitiveTypeNameMap.get(name);
        }
        if (cls == null) {
            cls = (Class) commonClassCache.get(name);
        }
        if (cls != null) {
            return cls;
        }
        if (name.endsWith("[]")) {
            return Array.newInstance(forName(name.substring(0, name.length() - 2), classLoader), 0).getClass();
        }
        if (name.startsWith("[L") && name.endsWith(";")) {
            return Array.newInstance(forName(name.substring(2, name.length() - 1), classLoader), 0).getClass();
        }
        if (name.startsWith("[")) {
            return Array.newInstance(forName(name.substring(1), classLoader), 0).getClass();
        }
        ClassLoader clToUse = classLoader;
        if (classLoader == null) {
            clToUse = getDefaultClassLoader();
        }
        if (clToUse == null) {
            return Class.forName(name);
        }
        try {
            return clToUse.loadClass(name);
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = name.lastIndexOf(46);
            if (lastDotIndex != -1) {
                String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
                if (clToUse == null) {
                    return Class.forName(innerClassName);
                }
                try {
                    return clToUse.loadClass(innerClassName);
                } catch (ClassNotFoundException e) {
                    throw ex;
                }
            }
            throw ex;
        }
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    public static String getQualifiedName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        }
        return clazz.getName();
    }

    private static String getQualifiedNameForArray(Class<?> clazz) {
        StringBuilder result = new StringBuilder();
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            result.append("[]");
        }
        result.insert(0, clazz.getName());
        return result.toString();
    }

    public static String getDescriptiveType(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (Proxy.isProxyClass(clazz)) {
            StringBuilder result = new StringBuilder(clazz.getName());
            result.append(" implementing ");
            Class<?>[] ifcs = clazz.getInterfaces();
            for (int i = 0; i < ifcs.length; i++) {
                result.append(ifcs[i].getName());
                if (i < ifcs.length - 1) {
                    result.append(',');
                }
            }
            return result.toString();
        } else if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        } else {
            return clazz.getName();
        }
    }
}
