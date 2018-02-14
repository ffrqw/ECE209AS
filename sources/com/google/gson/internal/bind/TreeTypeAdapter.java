package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public final class TreeTypeAdapter<T> extends TypeAdapter<T> {
    private final GsonContextImpl context = new GsonContextImpl();
    private TypeAdapter<T> delegate;
    private final JsonDeserializer<T> deserializer;
    final Gson gson;
    private final JsonSerializer<T> serializer;
    private final TypeAdapterFactory skipPast;
    private final TypeToken<T> typeToken;

    private final class GsonContextImpl {
        private GsonContextImpl() {
        }
    }

    public TreeTypeAdapter(JsonSerializer<T> serializer, JsonDeserializer<T> deserializer, Gson gson, TypeToken<T> typeToken, TypeAdapterFactory skipPast) {
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.gson = gson;
        this.typeToken = typeToken;
        this.skipPast = null;
    }

    public final T read(JsonReader in) throws IOException {
        if (this.deserializer == null) {
            return delegate().read(in);
        }
        if (Streams.parse(in) instanceof JsonNull) {
            return null;
        }
        return this.deserializer.deserialize$140ae884();
    }

    public final void write(JsonWriter out, T value) throws IOException {
        if (this.serializer == null) {
            delegate().write(out, value);
        } else if (value == null) {
            out.nullValue();
        } else {
            Streams.write(this.serializer.serialize$117eb95b(), out);
        }
    }

    private TypeAdapter<T> delegate() {
        TypeAdapter<T> d = this.delegate;
        if (d != null) {
            return d;
        }
        d = this.gson.getDelegateAdapter(this.skipPast, this.typeToken);
        this.delegate = d;
        return d;
    }
}
