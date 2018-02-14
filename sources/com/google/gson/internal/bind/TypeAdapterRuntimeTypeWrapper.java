package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

final class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {
    private final Gson context;
    private final TypeAdapter<T> delegate;
    private final Type type;

    TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter<T> delegate, Type type) {
        this.context = context;
        this.delegate = delegate;
        this.type = type;
    }

    public final T read(JsonReader in) throws IOException {
        return this.delegate.read(in);
    }

    public final void write(JsonWriter out, T value) throws IOException {
        TypeAdapter chosen = this.delegate;
        Type runtimeType = this.type;
        if (value != null && (runtimeType == Object.class || (runtimeType instanceof TypeVariable) || (runtimeType instanceof Class))) {
            runtimeType = value.getClass();
        }
        if (runtimeType != this.type) {
            TypeAdapter runtimeTypeAdapter = this.context.getAdapter(TypeToken.get(runtimeType));
            if (!(runtimeTypeAdapter instanceof Adapter) || (this.delegate instanceof Adapter)) {
                chosen = runtimeTypeAdapter;
            } else {
                chosen = this.delegate;
            }
        }
        chosen.write(out, value);
    }
}
