package com.google.gson;

import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import com.google.gson.internal.bind.TimeTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public final class Gson {
    private static final TypeToken<?> NULL_KEY_SURROGATE = TypeToken.get(Object.class);
    private final ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>> calls;
    private final ConstructorConstructor constructorConstructor;
    private final Excluder excluder;
    private final List<TypeAdapterFactory> factories;
    private final FieldNamingStrategy fieldNamingStrategy;
    private final boolean generateNonExecutableJson;
    private final boolean htmlSafe;
    private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
    private final boolean lenient;
    private final boolean prettyPrinting;
    private final boolean serializeNulls;
    private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache;

    static class FutureTypeAdapter<T> extends TypeAdapter<T> {
        private TypeAdapter<T> delegate;

        FutureTypeAdapter() {
        }

        public final void setDelegate(TypeAdapter<T> typeAdapter) {
            if (this.delegate != null) {
                throw new AssertionError();
            }
            this.delegate = typeAdapter;
        }

        public final T read(JsonReader in) throws IOException {
            if (this.delegate != null) {
                return this.delegate.read(in);
            }
            throw new IllegalStateException();
        }

        public final void write(JsonWriter out, T value) throws IOException {
            if (this.delegate == null) {
                throw new IllegalStateException();
            }
            this.delegate.write(out, value);
        }
    }

    public Gson() {
        this(Excluder.DEFAULT, FieldNamingPolicy.IDENTITY, Collections.emptyMap(), false, false, false, true, false, false, false, LongSerializationPolicy.DEFAULT, Collections.emptyList());
    }

    Gson(Excluder excluder, FieldNamingStrategy fieldNamingStrategy, Map<Type, InstanceCreator<?>> instanceCreators, boolean serializeNulls, boolean complexMapKeySerialization, boolean generateNonExecutableGson, boolean htmlSafe, boolean prettyPrinting, boolean lenient, boolean serializeSpecialFloatingPointValues, LongSerializationPolicy longSerializationPolicy, List<TypeAdapterFactory> typeAdapterFactories) {
        TypeAdapter<Number> longAdapter;
        TypeAdapter typeAdapter;
        this.calls = new ThreadLocal();
        this.typeTokenCache = new ConcurrentHashMap();
        this.constructorConstructor = new ConstructorConstructor(instanceCreators);
        this.excluder = excluder;
        this.fieldNamingStrategy = fieldNamingStrategy;
        this.serializeNulls = serializeNulls;
        this.generateNonExecutableJson = generateNonExecutableGson;
        this.htmlSafe = htmlSafe;
        this.prettyPrinting = prettyPrinting;
        this.lenient = lenient;
        List<TypeAdapterFactory> factories = new ArrayList();
        factories.add(TypeAdapters.JSON_ELEMENT_FACTORY);
        factories.add(ObjectTypeAdapter.FACTORY);
        factories.add(excluder);
        factories.addAll(typeAdapterFactories);
        factories.add(TypeAdapters.STRING_FACTORY);
        factories.add(TypeAdapters.INTEGER_FACTORY);
        factories.add(TypeAdapters.BOOLEAN_FACTORY);
        factories.add(TypeAdapters.BYTE_FACTORY);
        factories.add(TypeAdapters.SHORT_FACTORY);
        if (longSerializationPolicy == LongSerializationPolicy.DEFAULT) {
            longAdapter = TypeAdapters.LONG;
        } else {
            longAdapter = new TypeAdapter<Number>() {
                public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
                    Number number = (Number) obj;
                    if (number == null) {
                        jsonWriter.nullValue();
                    } else {
                        jsonWriter.value(number.toString());
                    }
                }

                public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
                    if (jsonReader.peek() != JsonToken.NULL) {
                        return Long.valueOf(jsonReader.nextLong());
                    }
                    jsonReader.nextNull();
                    return null;
                }
            };
        }
        factories.add(TypeAdapters.newFactory(Long.TYPE, Long.class, longAdapter));
        Class cls = Double.TYPE;
        Class cls2 = Double.class;
        if (serializeSpecialFloatingPointValues) {
            typeAdapter = TypeAdapters.DOUBLE;
        } else {
            typeAdapter = new TypeAdapter<Number>() {
                public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
                    Number number = (Number) obj;
                    if (number == null) {
                        jsonWriter.nullValue();
                        return;
                    }
                    Gson.checkValidFloatingPoint(number.doubleValue());
                    jsonWriter.value(number);
                }

                public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
                    if (jsonReader.peek() != JsonToken.NULL) {
                        return Double.valueOf(jsonReader.nextDouble());
                    }
                    jsonReader.nextNull();
                    return null;
                }
            };
        }
        factories.add(TypeAdapters.newFactory(cls, cls2, typeAdapter));
        cls = Float.TYPE;
        cls2 = Float.class;
        if (serializeSpecialFloatingPointValues) {
            typeAdapter = TypeAdapters.FLOAT;
        } else {
            typeAdapter = new TypeAdapter<Number>() {
                public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
                    Number number = (Number) obj;
                    if (number == null) {
                        jsonWriter.nullValue();
                        return;
                    }
                    Gson.checkValidFloatingPoint((double) number.floatValue());
                    jsonWriter.value(number);
                }

                public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
                    if (jsonReader.peek() != JsonToken.NULL) {
                        return Float.valueOf((float) jsonReader.nextDouble());
                    }
                    jsonReader.nextNull();
                    return null;
                }
            };
        }
        factories.add(TypeAdapters.newFactory(cls, cls2, typeAdapter));
        factories.add(TypeAdapters.NUMBER_FACTORY);
        factories.add(TypeAdapters.ATOMIC_INTEGER_FACTORY);
        factories.add(TypeAdapters.ATOMIC_BOOLEAN_FACTORY);
        factories.add(TypeAdapters.newFactory(AtomicLong.class, new TypeAdapter<AtomicLong>() {
            public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
                longAdapter.write(jsonWriter, Long.valueOf(((AtomicLong) obj).get()));
            }

            public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
                return new AtomicLong(((Number) longAdapter.read(jsonReader)).longValue());
            }
        }.nullSafe()));
        factories.add(TypeAdapters.newFactory(AtomicLongArray.class, new TypeAdapter<AtomicLongArray>() {
            public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
                AtomicLongArray atomicLongArray = (AtomicLongArray) obj;
                jsonWriter.beginArray();
                int length = atomicLongArray.length();
                for (int i = 0; i < length; i++) {
                    longAdapter.write(jsonWriter, Long.valueOf(atomicLongArray.get(i)));
                }
                jsonWriter.endArray();
            }

            public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
                List arrayList = new ArrayList();
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    arrayList.add(Long.valueOf(((Number) longAdapter.read(jsonReader)).longValue()));
                }
                jsonReader.endArray();
                int size = arrayList.size();
                AtomicLongArray atomicLongArray = new AtomicLongArray(size);
                for (int i = 0; i < size; i++) {
                    atomicLongArray.set(i, ((Long) arrayList.get(i)).longValue());
                }
                return atomicLongArray;
            }
        }.nullSafe()));
        factories.add(TypeAdapters.ATOMIC_INTEGER_ARRAY_FACTORY);
        factories.add(TypeAdapters.CHARACTER_FACTORY);
        factories.add(TypeAdapters.STRING_BUILDER_FACTORY);
        factories.add(TypeAdapters.STRING_BUFFER_FACTORY);
        factories.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
        factories.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
        factories.add(TypeAdapters.URL_FACTORY);
        factories.add(TypeAdapters.URI_FACTORY);
        factories.add(TypeAdapters.UUID_FACTORY);
        factories.add(TypeAdapters.CURRENCY_FACTORY);
        factories.add(TypeAdapters.LOCALE_FACTORY);
        factories.add(TypeAdapters.INET_ADDRESS_FACTORY);
        factories.add(TypeAdapters.BIT_SET_FACTORY);
        factories.add(DateTypeAdapter.FACTORY);
        factories.add(TypeAdapters.CALENDAR_FACTORY);
        factories.add(TimeTypeAdapter.FACTORY);
        factories.add(SqlDateTypeAdapter.FACTORY);
        factories.add(TypeAdapters.TIMESTAMP_FACTORY);
        factories.add(ArrayTypeAdapter.FACTORY);
        factories.add(TypeAdapters.CLASS_FACTORY);
        factories.add(new CollectionTypeAdapterFactory(this.constructorConstructor));
        factories.add(new MapTypeAdapterFactory(this.constructorConstructor, complexMapKeySerialization));
        this.jsonAdapterFactory = new JsonAdapterAnnotationTypeAdapterFactory(this.constructorConstructor);
        factories.add(this.jsonAdapterFactory);
        factories.add(TypeAdapters.ENUM_FACTORY);
        factories.add(new ReflectiveTypeAdapterFactory(this.constructorConstructor, fieldNamingStrategy, excluder, this.jsonAdapterFactory));
        this.factories = Collections.unmodifiableList(factories);
    }

    static void checkValidFloatingPoint(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(value + " is not a valid double value as per JSON specification. To override this behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
        }
    }

    public final <T> TypeAdapter<T> getAdapter(TypeToken<T> type) {
        Object obj;
        Map map = this.typeTokenCache;
        if (type == null) {
            obj = NULL_KEY_SURROGATE;
        } else {
            TypeToken<T> typeToken = type;
        }
        TypeAdapter<?> cached = (TypeAdapter) map.get(obj);
        if (cached != null) {
            return cached;
        }
        Map<TypeToken<?>, FutureTypeAdapter<?>> threadCalls = (Map) this.calls.get();
        boolean requiresThreadLocalCleanup = false;
        if (threadCalls == null) {
            threadCalls = new HashMap();
            this.calls.set(threadCalls);
            requiresThreadLocalCleanup = true;
        }
        TypeAdapter ongoingCall = (FutureTypeAdapter) threadCalls.get(type);
        if (ongoingCall != null) {
            return ongoingCall;
        }
        try {
            FutureTypeAdapter<T> call = new FutureTypeAdapter();
            threadCalls.put(type, call);
            for (TypeAdapterFactory factory : this.factories) {
                TypeAdapter<T> candidate = factory.create(this, type);
                if (candidate != null) {
                    call.setDelegate(candidate);
                    this.typeTokenCache.put(type, candidate);
                    return candidate;
                }
            }
            throw new IllegalArgumentException("GSON cannot handle " + type);
        } finally {
            threadCalls.remove(type);
            if (requiresThreadLocalCleanup) {
                this.calls.remove();
            }
        }
    }

    public final <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory skipPast, TypeToken<T> type) {
        if (!this.factories.contains(skipPast)) {
            skipPast = this.jsonAdapterFactory;
        }
        boolean skipPastFound = false;
        for (TypeAdapterFactory factory : this.factories) {
            if (skipPastFound) {
                TypeAdapter<T> candidate = factory.create(this, type);
                if (candidate != null) {
                    return candidate;
                }
            } else if (factory == skipPast) {
                skipPastFound = true;
            }
        }
        throw new IllegalArgumentException("GSON cannot serialize " + type);
    }

    public final <T> TypeAdapter<T> getAdapter(Class<T> type) {
        return getAdapter(TypeToken.get((Class) type));
    }

    public final String toJson(Object src) {
        if (src == null) {
            JsonElement jsonElement = JsonNull.INSTANCE;
            Object stringWriter = new StringWriter();
            toJson(jsonElement, stringWriter);
            return stringWriter.toString();
        }
        Type type = src.getClass();
        stringWriter = new StringWriter();
        toJson(src, type, stringWriter);
        return stringWriter.toString();
    }

    public final void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
        try {
            JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
            TypeAdapter adapter = getAdapter(TypeToken.get(typeOfSrc));
            boolean isLenient = jsonWriter.isLenient();
            jsonWriter.setLenient(true);
            boolean isHtmlSafe = jsonWriter.isHtmlSafe();
            jsonWriter.setHtmlSafe(this.htmlSafe);
            boolean serializeNulls = jsonWriter.getSerializeNulls();
            jsonWriter.setSerializeNulls(this.serializeNulls);
            try {
                adapter.write(jsonWriter, src);
                jsonWriter.setLenient(isLenient);
                jsonWriter.setHtmlSafe(isHtmlSafe);
                jsonWriter.setSerializeNulls(serializeNulls);
            } catch (Throwable e) {
                throw new JsonIOException(e);
            } catch (Throwable th) {
                jsonWriter.setLenient(isLenient);
                jsonWriter.setHtmlSafe(isHtmlSafe);
                jsonWriter.setSerializeNulls(serializeNulls);
            }
        } catch (Throwable e2) {
            throw new JsonIOException(e2);
        }
    }

    public final void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
        try {
            JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
            boolean isLenient = jsonWriter.isLenient();
            jsonWriter.setLenient(true);
            boolean isHtmlSafe = jsonWriter.isHtmlSafe();
            jsonWriter.setHtmlSafe(this.htmlSafe);
            boolean serializeNulls = jsonWriter.getSerializeNulls();
            jsonWriter.setSerializeNulls(this.serializeNulls);
            try {
                Streams.write(jsonElement, jsonWriter);
                jsonWriter.setLenient(isLenient);
                jsonWriter.setHtmlSafe(isHtmlSafe);
                jsonWriter.setSerializeNulls(serializeNulls);
            } catch (Throwable e) {
                throw new JsonIOException(e);
            } catch (Throwable th) {
                jsonWriter.setLenient(isLenient);
                jsonWriter.setHtmlSafe(isHtmlSafe);
                jsonWriter.setSerializeNulls(serializeNulls);
            }
        } catch (Throwable e2) {
            throw new JsonIOException(e2);
        }
    }

    private JsonWriter newJsonWriter(Writer writer) throws IOException {
        if (this.generateNonExecutableJson) {
            writer.write(")]}'\n");
        }
        JsonWriter jsonWriter = new JsonWriter(writer);
        if (this.prettyPrinting) {
            jsonWriter.setIndent("  ");
        }
        jsonWriter.setSerializeNulls(this.serializeNulls);
        return jsonWriter;
    }

    private <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        boolean isEmpty = true;
        boolean oldLenient = reader.isLenient();
        reader.setLenient(true);
        try {
            reader.peek();
            isEmpty = false;
            T object = getAdapter(TypeToken.get(typeOfT)).read(reader);
            reader.setLenient(oldLenient);
            return object;
        } catch (Throwable e) {
            if (isEmpty) {
                reader.setLenient(oldLenient);
                return null;
            }
            throw new JsonSyntaxException(e);
        } catch (Throwable e2) {
            throw new JsonSyntaxException(e2);
        } catch (Throwable e22) {
            throw new JsonSyntaxException(e22);
        } catch (Throwable th) {
            reader.setLenient(oldLenient);
        }
    }

    public final String toString() {
        return "{serializeNulls:" + this.serializeNulls + ",factories:" + this.factories + ",instanceCreators:" + this.constructorConstructor + "}";
    }

    public final <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        Object object;
        if (json == null) {
            object = null;
        } else {
            object = fromJson(new StringReader(json), (Type) classOfT);
        }
        return Primitives.wrap(classOfT).cast(object);
    }

    public final <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        JsonReader jsonReader = new JsonReader(json);
        jsonReader.setLenient(this.lenient);
        T object = fromJson(jsonReader, typeOfT);
        if (object != null) {
            try {
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw new JsonIOException("JSON document was not fully consumed.");
                }
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            } catch (Throwable e2) {
                throw new JsonIOException(e2);
            }
        }
        return object;
    }
}
