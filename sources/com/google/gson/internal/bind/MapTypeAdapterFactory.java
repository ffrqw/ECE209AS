package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.C$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class MapTypeAdapterFactory implements TypeAdapterFactory {
    final boolean complexMapKeySerialization;
    private final ConstructorConstructor constructorConstructor;

    private final class Adapter<K, V> extends TypeAdapter<Map<K, V>> {
        private final ObjectConstructor<? extends Map<K, V>> constructor;
        private final TypeAdapter<K> keyTypeAdapter;
        private final TypeAdapter<V> valueTypeAdapter;

        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            int i = 0;
            Map map = (Map) obj;
            if (map == null) {
                jsonWriter.nullValue();
            } else if (MapTypeAdapterFactory.this.complexMapKeySerialization) {
                List arrayList = new ArrayList(map.size());
                List arrayList2 = new ArrayList(map.size());
                int i2 = 0;
                for (Entry entry : map.entrySet()) {
                    int i3;
                    JsonElement toJsonTree = this.keyTypeAdapter.toJsonTree(entry.getKey());
                    arrayList.add(toJsonTree);
                    arrayList2.add(entry.getValue());
                    if ((toJsonTree instanceof JsonArray) || (toJsonTree instanceof JsonObject)) {
                        i3 = 1;
                    } else {
                        i3 = 0;
                    }
                    i2 = i3 | i2;
                }
                if (i2 != 0) {
                    jsonWriter.beginArray();
                    i2 = arrayList.size();
                    while (i < i2) {
                        jsonWriter.beginArray();
                        Streams.write((JsonElement) arrayList.get(i), jsonWriter);
                        this.valueTypeAdapter.write(jsonWriter, arrayList2.get(i));
                        jsonWriter.endArray();
                        i++;
                    }
                    jsonWriter.endArray();
                    return;
                }
                jsonWriter.beginObject();
                i2 = arrayList.size();
                while (i < i2) {
                    String valueOf;
                    JsonElement jsonElement = (JsonElement) arrayList.get(i);
                    if (jsonElement instanceof JsonPrimitive) {
                        JsonPrimitive asJsonPrimitive = jsonElement.getAsJsonPrimitive();
                        if (asJsonPrimitive.isNumber()) {
                            valueOf = String.valueOf(asJsonPrimitive.getAsNumber());
                        } else if (asJsonPrimitive.isBoolean()) {
                            valueOf = Boolean.toString(asJsonPrimitive.getAsBoolean());
                        } else if (asJsonPrimitive.isString()) {
                            valueOf = asJsonPrimitive.getAsString();
                        } else {
                            throw new AssertionError();
                        }
                    } else if (jsonElement instanceof JsonNull) {
                        valueOf = "null";
                    } else {
                        throw new AssertionError();
                    }
                    jsonWriter.name(valueOf);
                    this.valueTypeAdapter.write(jsonWriter, arrayList2.get(i));
                    i++;
                }
                jsonWriter.endObject();
            } else {
                jsonWriter.beginObject();
                for (Entry entry2 : map.entrySet()) {
                    jsonWriter.name(String.valueOf(entry2.getKey()));
                    this.valueTypeAdapter.write(jsonWriter, entry2.getValue());
                }
                jsonWriter.endObject();
            }
        }

        public Adapter(Gson context, Type keyType, TypeAdapter<K> keyTypeAdapter, Type valueType, TypeAdapter<V> valueTypeAdapter, ObjectConstructor<? extends Map<K, V>> constructor) {
            this.keyTypeAdapter = new TypeAdapterRuntimeTypeWrapper(context, keyTypeAdapter, keyType);
            this.valueTypeAdapter = new TypeAdapterRuntimeTypeWrapper(context, valueTypeAdapter, valueType);
            this.constructor = constructor;
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            JsonToken peek = jsonReader.peek();
            if (peek == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            Map map = (Map) this.constructor.construct();
            Object read;
            if (peek == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    jsonReader.beginArray();
                    read = this.keyTypeAdapter.read(jsonReader);
                    if (map.put(read, this.valueTypeAdapter.read(jsonReader)) != null) {
                        throw new JsonSyntaxException("duplicate key: " + read);
                    }
                    jsonReader.endArray();
                }
                jsonReader.endArray();
                return map;
            }
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonReaderInternalAccess.INSTANCE.promoteNameToValue(jsonReader);
                read = this.keyTypeAdapter.read(jsonReader);
                if (map.put(read, this.valueTypeAdapter.read(jsonReader)) != null) {
                    throw new JsonSyntaxException("duplicate key: " + read);
                }
            }
            jsonReader.endObject();
            return map;
        }
    }

    public MapTypeAdapterFactory(ConstructorConstructor constructorConstructor, boolean complexMapKeySerialization) {
        this.constructorConstructor = constructorConstructor;
        this.complexMapKeySerialization = complexMapKeySerialization;
    }

    public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        if (!Map.class.isAssignableFrom(typeToken.getRawType())) {
            return null;
        }
        TypeAdapter<?> keyAdapter;
        Type[] keyAndValueTypes = C$Gson$Types.getMapKeyAndValueTypes(type, C$Gson$Types.getRawType(type));
        Type type2 = keyAndValueTypes[0];
        if (type2 == Boolean.TYPE || type2 == Boolean.class) {
            keyAdapter = TypeAdapters.BOOLEAN_AS_STRING;
        } else {
            keyAdapter = gson.getAdapter(TypeToken.get(type2));
        }
        return new Adapter(gson, keyAndValueTypes[0], keyAdapter, keyAndValueTypes[1], gson.getAdapter(TypeToken.get(keyAndValueTypes[1])), this.constructorConstructor.get(typeToken));
    }
}
