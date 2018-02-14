package com.google.gson.internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public final class Excluder implements TypeAdapterFactory, Cloneable {
    public static final Excluder DEFAULT = new Excluder();
    private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();
    private int modifiers = 136;
    private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
    private boolean serializeInnerClasses = true;
    private double version = -1.0d;

    private Excluder clone() {
        try {
            return (Excluder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<?> rawType = type.getRawType();
        final boolean skipSerialize = excludeClass(rawType, true);
        final boolean skipDeserialize = excludeClass(rawType, false);
        if (!skipSerialize && !skipDeserialize) {
            return null;
        }
        final Gson gson2 = gson;
        final TypeToken<T> typeToken = type;
        return new TypeAdapter<T>() {
            private TypeAdapter<T> delegate;

            public final T read(JsonReader in) throws IOException {
                if (!skipDeserialize) {
                    return delegate().read(in);
                }
                in.skipValue();
                return null;
            }

            public final void write(JsonWriter out, T value) throws IOException {
                if (skipSerialize) {
                    out.nullValue();
                } else {
                    delegate().write(out, value);
                }
            }

            private TypeAdapter<T> delegate() {
                TypeAdapter<T> d = this.delegate;
                if (d != null) {
                    return d;
                }
                d = gson2.getDelegateAdapter(Excluder.this, typeToken);
                this.delegate = d;
                return d;
            }
        };
    }

    public final boolean excludeField(Field field, boolean serialize) {
        if ((this.modifiers & field.getModifiers()) != 0) {
            return true;
        }
        if (this.version != -1.0d && !isValidVersion((Since) field.getAnnotation(Since.class), (Until) field.getAnnotation(Until.class))) {
            return true;
        }
        if (field.isSynthetic()) {
            return true;
        }
        if (!this.serializeInnerClasses && isInnerClass(field.getType())) {
            return true;
        }
        if (isAnonymousOrLocal(field.getType())) {
            return true;
        }
        List<ExclusionStrategy> list = serialize ? this.serializationStrategies : this.deserializationStrategies;
        if (!list.isEmpty()) {
            FieldAttributes fieldAttributes = new FieldAttributes(field);
            for (ExclusionStrategy exclusionStrategy : list) {
                if (exclusionStrategy.shouldSkipField$6e8224bb()) {
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean excludeClass(Class<?> clazz, boolean serialize) {
        if (this.version != -1.0d && !isValidVersion((Since) clazz.getAnnotation(Since.class), (Until) clazz.getAnnotation(Until.class))) {
            return true;
        }
        if (!this.serializeInnerClasses && isInnerClass(clazz)) {
            return true;
        }
        if (isAnonymousOrLocal(clazz)) {
            return true;
        }
        for (ExclusionStrategy exclusionStrategy : serialize ? this.serializationStrategies : this.deserializationStrategies) {
            if (exclusionStrategy.shouldSkipClass$1b2d8b94()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAnonymousOrLocal(Class<?> clazz) {
        return !Enum.class.isAssignableFrom(clazz) && (clazz.isAnonymousClass() || clazz.isLocalClass());
    }

    private boolean isInnerClass(Class<?> clazz) {
        if (clazz.isMemberClass()) {
            boolean z;
            if ((clazz.getModifiers() & 8) != 0) {
                z = true;
            } else {
                z = false;
            }
            if (!z) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidVersion(Since since, Until until) {
        boolean z;
        if (since == null || since.value() <= this.version) {
            z = true;
        } else {
            z = false;
        }
        if (z) {
            if (until == null || until.value() > this.version) {
                z = true;
            } else {
                z = false;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }
}
