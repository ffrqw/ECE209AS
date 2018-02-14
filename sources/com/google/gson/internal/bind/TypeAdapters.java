package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public final class TypeAdapters {
    public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN = new TypeAdapter<AtomicBoolean>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value(((AtomicBoolean) obj).get());
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            return new AtomicBoolean(jsonReader.nextBoolean());
        }
    }.nullSafe();
    public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
    public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER = new TypeAdapter<AtomicInteger>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((long) ((AtomicInteger) obj).get());
        }

        private static AtomicInteger read(JsonReader in) throws IOException {
            try {
                return new AtomicInteger(in.nextInt());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }
    }.nullSafe();
    public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = new TypeAdapter<AtomicIntegerArray>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            AtomicIntegerArray atomicIntegerArray = (AtomicIntegerArray) obj;
            jsonWriter.beginArray();
            int length = atomicIntegerArray.length();
            for (int i = 0; i < length; i++) {
                jsonWriter.value((long) atomicIntegerArray.get(i));
            }
            jsonWriter.endArray();
        }

        private static AtomicIntegerArray read(JsonReader in) throws IOException {
            List<Integer> list = new ArrayList();
            in.beginArray();
            while (in.hasNext()) {
                try {
                    list.add(Integer.valueOf(in.nextInt()));
                } catch (Throwable e) {
                    throw new JsonSyntaxException(e);
                }
            }
            in.endArray();
            int length = list.size();
            AtomicIntegerArray array = new AtomicIntegerArray(length);
            for (int i = 0; i < length; i++) {
                array.set(i, ((Integer) list.get(i)).intValue());
            }
            return array;
        }
    }.nullSafe();
    public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
    public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger.class, ATOMIC_INTEGER);
    public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((BigDecimal) obj);
        }

        private static BigDecimal read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new BigDecimal(in.nextString());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }
    };
    public static final TypeAdapter<BigInteger> BIG_INTEGER = new TypeAdapter<BigInteger>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((BigInteger) obj);
        }

        private static BigInteger read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return new BigInteger(in.nextString());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }
    };
    public static final TypeAdapter<BitSet> BIT_SET = new TypeAdapter<BitSet>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            BitSet bitSet = (BitSet) obj;
            jsonWriter.beginArray();
            int length = bitSet.length();
            for (int i = 0; i < length; i++) {
                int i2;
                if (bitSet.get(i)) {
                    i2 = 1;
                } else {
                    i2 = 0;
                }
                jsonWriter.value((long) i2);
            }
            jsonWriter.endArray();
        }

        private static BitSet read(JsonReader in) throws IOException {
            BitSet bitset = new BitSet();
            in.beginArray();
            int i = 0;
            JsonToken tokenType = in.peek();
            while (tokenType != JsonToken.END_ARRAY) {
                boolean set;
                switch (tokenType) {
                    case NUMBER:
                        if (in.nextInt() == 0) {
                            set = false;
                            break;
                        }
                        set = true;
                        break;
                    case BOOLEAN:
                        set = in.nextBoolean();
                        break;
                    case STRING:
                        String stringValue = in.nextString();
                        try {
                            set = Integer.parseInt(stringValue) != 0;
                            break;
                        } catch (NumberFormatException e) {
                            throw new JsonSyntaxException("Error: Expecting: bitset number value (1, 0), Found: " + stringValue);
                        }
                    default:
                        throw new JsonSyntaxException("Invalid bitset value type: " + tokenType);
                }
                if (set) {
                    bitset.set(i);
                }
                i++;
                tokenType = in.peek();
            }
            in.endArray();
            return bitset;
        }
    }.nullSafe();
    public static final TypeAdapterFactory BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
    public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((Boolean) obj);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else if (jsonReader.peek() == JsonToken.STRING) {
                return Boolean.valueOf(Boolean.parseBoolean(jsonReader.nextString()));
            } else {
                return Boolean.valueOf(jsonReader.nextBoolean());
            }
        }
    };
    public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            Boolean bool = (Boolean) obj;
            if (bool == null) {
                str = "null";
            } else {
                str = bool.toString();
            }
            jsonWriter.value(str);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return Boolean.valueOf(jsonReader.nextString());
            }
            jsonReader.nextNull();
            return null;
        }
    };
    public static final TypeAdapterFactory BOOLEAN_FACTORY = newFactory(Boolean.TYPE, Boolean.class, BOOLEAN);
    public static final TypeAdapter<Number> BYTE = new TypeAdapter<Number>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((Number) obj);
        }

        private static Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return Byte.valueOf((byte) in.nextInt());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }
    };
    public static final TypeAdapterFactory BYTE_FACTORY = newFactory(Byte.TYPE, Byte.class, BYTE);
    public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter<Calendar>() {
        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            int i = 0;
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            jsonReader.beginObject();
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            while (jsonReader.peek() != JsonToken.END_OBJECT) {
                String nextName = jsonReader.nextName();
                int nextInt = jsonReader.nextInt();
                if ("year".equals(nextName)) {
                    i6 = nextInt;
                } else if ("month".equals(nextName)) {
                    i5 = nextInt;
                } else if ("dayOfMonth".equals(nextName)) {
                    i4 = nextInt;
                } else if ("hourOfDay".equals(nextName)) {
                    i3 = nextInt;
                } else if ("minute".equals(nextName)) {
                    i2 = nextInt;
                } else if ("second".equals(nextName)) {
                    i = nextInt;
                }
            }
            jsonReader.endObject();
            return new GregorianCalendar(i6, i5, i4, i3, i2, i);
        }

        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            Calendar calendar = (Calendar) obj;
            if (calendar == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.beginObject();
            jsonWriter.name("year");
            jsonWriter.value((long) calendar.get(1));
            jsonWriter.name("month");
            jsonWriter.value((long) calendar.get(2));
            jsonWriter.name("dayOfMonth");
            jsonWriter.value((long) calendar.get(5));
            jsonWriter.name("hourOfDay");
            jsonWriter.value((long) calendar.get(11));
            jsonWriter.name("minute");
            jsonWriter.value((long) calendar.get(12));
            jsonWriter.name("second");
            jsonWriter.value((long) calendar.get(13));
            jsonWriter.endObject();
        }
    };
    public static final TypeAdapterFactory CALENDAR_FACTORY;
    public static final TypeAdapter<Character> CHARACTER = new TypeAdapter<Character>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            Character ch = (Character) obj;
            if (ch == null) {
                str = null;
            } else {
                str = String.valueOf(ch);
            }
            jsonWriter.value(str);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            String nextString = jsonReader.nextString();
            if (nextString.length() == 1) {
                return Character.valueOf(nextString.charAt(0));
            }
            throw new JsonSyntaxException("Expecting character, got: " + nextString);
        }
    };
    public static final TypeAdapterFactory CHARACTER_FACTORY = newFactory(Character.TYPE, Character.class, CHARACTER);
    public static final TypeAdapter<Class> CLASS = new TypeAdapter<Class>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + ((Class) obj).getName() + ". Forgot to register a type adapter?");
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
        }
    }.nullSafe();
    public static final TypeAdapterFactory CLASS_FACTORY = newFactory(Class.class, CLASS);
    public static final TypeAdapter<Currency> CURRENCY = new TypeAdapter<Currency>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value(((Currency) obj).getCurrencyCode());
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            return Currency.getInstance(jsonReader.nextString());
        }
    }.nullSafe();
    public static final TypeAdapterFactory CURRENCY_FACTORY = newFactory(Currency.class, CURRENCY);
    public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((Number) obj);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return Double.valueOf(jsonReader.nextDouble());
            }
            jsonReader.nextNull();
            return null;
        }
    };
    public static final TypeAdapterFactory ENUM_FACTORY = new TypeAdapterFactory() {
        public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<? super T> rawType = typeToken.getRawType();
            if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
                return null;
            }
            if (!rawType.isEnum()) {
                rawType = rawType.getSuperclass();
            }
            return new EnumTypeAdapter(rawType);
        }
    };
    public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((Number) obj);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return Float.valueOf((float) jsonReader.nextDouble());
            }
            jsonReader.nextNull();
            return null;
        }
    };
    public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter<InetAddress>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            InetAddress inetAddress = (InetAddress) obj;
            if (inetAddress == null) {
                str = null;
            } else {
                str = inetAddress.getHostAddress();
            }
            jsonWriter.value(str);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return InetAddress.getByName(jsonReader.nextString());
            }
            jsonReader.nextNull();
            return null;
        }
    };
    public static final TypeAdapterFactory INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
    public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((Number) obj);
        }

        private static Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return Integer.valueOf(in.nextInt());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }
    };
    public static final TypeAdapterFactory INTEGER_FACTORY = newFactory(Integer.TYPE, Integer.class, INTEGER);
    public static final TypeAdapter<JsonElement> JSON_ELEMENT = new TypeAdapter<JsonElement>() {
        private JsonElement read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case NUMBER:
                    return new JsonPrimitive(new LazilyParsedNumber(in.nextString()));
                case BOOLEAN:
                    return new JsonPrimitive(Boolean.valueOf(in.nextBoolean()));
                case STRING:
                    return new JsonPrimitive(in.nextString());
                case NULL:
                    in.nextNull();
                    return JsonNull.INSTANCE;
                case BEGIN_ARRAY:
                    JsonElement array = new JsonArray();
                    in.beginArray();
                    while (in.hasNext()) {
                        array.add(read(in));
                    }
                    in.endArray();
                    return array;
                case BEGIN_OBJECT:
                    JsonElement object = new JsonObject();
                    in.beginObject();
                    while (in.hasNext()) {
                        object.add(in.nextName(), read(in));
                    }
                    in.endObject();
                    return object;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private void write(JsonWriter out, JsonElement value) throws IOException {
            if (value == null || (value instanceof JsonNull)) {
                out.nullValue();
            } else if (value instanceof JsonPrimitive) {
                JsonPrimitive primitive = value.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    out.value(primitive.getAsNumber());
                } else if (primitive.isBoolean()) {
                    out.value(primitive.getAsBoolean());
                } else {
                    out.value(primitive.getAsString());
                }
            } else if (value instanceof JsonArray) {
                out.beginArray();
                if (value instanceof JsonArray) {
                    Iterator it = ((JsonArray) value).iterator();
                    while (it.hasNext()) {
                        write(out, (JsonElement) it.next());
                    }
                    out.endArray();
                    return;
                }
                throw new IllegalStateException("Not a JSON Array: " + value);
            } else if (value instanceof JsonObject) {
                out.beginObject();
                if (value instanceof JsonObject) {
                    for (Entry<String, JsonElement> e : ((JsonObject) value).entrySet()) {
                        out.name((String) e.getKey());
                        write(out, (JsonElement) e.getValue());
                    }
                    out.endObject();
                    return;
                }
                throw new IllegalStateException("Not a JSON Object: " + value);
            } else {
                throw new IllegalArgumentException("Couldn't write " + value.getClass());
            }
        }
    };
    public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
    public static final TypeAdapter<Locale> LOCALE = new TypeAdapter<Locale>() {
        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            String nextToken;
            String nextToken2;
            String nextToken3;
            StringTokenizer stringTokenizer = new StringTokenizer(jsonReader.nextString(), "_");
            if (stringTokenizer.hasMoreElements()) {
                nextToken = stringTokenizer.nextToken();
            } else {
                nextToken = null;
            }
            if (stringTokenizer.hasMoreElements()) {
                nextToken2 = stringTokenizer.nextToken();
            } else {
                nextToken2 = null;
            }
            if (stringTokenizer.hasMoreElements()) {
                nextToken3 = stringTokenizer.nextToken();
            } else {
                nextToken3 = null;
            }
            if (nextToken2 == null && nextToken3 == null) {
                return new Locale(nextToken);
            }
            if (nextToken3 == null) {
                return new Locale(nextToken, nextToken2);
            }
            return new Locale(nextToken, nextToken2, nextToken3);
        }

        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            Locale locale = (Locale) obj;
            if (locale == null) {
                str = null;
            } else {
                str = locale.toString();
            }
            jsonWriter.value(str);
        }
    };
    public static final TypeAdapterFactory LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
    public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((Number) obj);
        }

        private static Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return Long.valueOf(in.nextLong());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }
    };
    public static final TypeAdapter<Number> NUMBER = new TypeAdapter<Number>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((Number) obj);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            JsonToken peek = jsonReader.peek();
            switch (peek) {
                case NUMBER:
                case STRING:
                    return new LazilyParsedNumber(jsonReader.nextString());
                case NULL:
                    jsonReader.nextNull();
                    return null;
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + peek);
            }
        }
    };
    public static final TypeAdapterFactory NUMBER_FACTORY = newFactory(Number.class, NUMBER);
    public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((Number) obj);
        }

        private static Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                return Short.valueOf((short) in.nextInt());
            } catch (Throwable e) {
                throw new JsonSyntaxException(e);
            }
        }
    };
    public static final TypeAdapterFactory SHORT_FACTORY = newFactory(Short.TYPE, Short.class, SHORT);
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            jsonWriter.value((String) obj);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            JsonToken peek = jsonReader.peek();
            if (peek == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else if (peek == JsonToken.BOOLEAN) {
                return Boolean.toString(jsonReader.nextBoolean());
            } else {
                return jsonReader.nextString();
            }
        }
    };
    public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            StringBuffer stringBuffer = (StringBuffer) obj;
            if (stringBuffer == null) {
                str = null;
            } else {
                str = stringBuffer.toString();
            }
            jsonWriter.value(str);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return new StringBuffer(jsonReader.nextString());
            }
            jsonReader.nextNull();
            return null;
        }
    };
    public static final TypeAdapterFactory STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
    public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            StringBuilder stringBuilder = (StringBuilder) obj;
            if (stringBuilder == null) {
                str = null;
            } else {
                str = stringBuilder.toString();
            }
            jsonWriter.value(str);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return new StringBuilder(jsonReader.nextString());
            }
            jsonReader.nextNull();
            return null;
        }
    };
    public static final TypeAdapterFactory STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
    public static final TypeAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);
    public static final TypeAdapterFactory TIMESTAMP_FACTORY = new TypeAdapterFactory() {
        public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            if (typeToken.getRawType() != Timestamp.class) {
                return null;
            }
            final TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
            return new TypeAdapter<Timestamp>() {
                public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
                    dateTypeAdapter.write(jsonWriter, (Timestamp) obj);
                }

                public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
                    Date date = (Date) dateTypeAdapter.read(jsonReader);
                    return date != null ? new Timestamp(date.getTime()) : null;
                }
            };
        }
    };
    public static final TypeAdapter<URI> URI = new TypeAdapter<URI>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            URI uri = (URI) obj;
            if (uri == null) {
                str = null;
            } else {
                str = uri.toASCIIString();
            }
            jsonWriter.value(str);
        }

        private static URI read(JsonReader in) throws IOException {
            URI uri = null;
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
            } else {
                try {
                    String nextString = in.nextString();
                    if (!"null".equals(nextString)) {
                        uri = new URI(nextString);
                    }
                } catch (Throwable e) {
                    throw new JsonIOException(e);
                }
            }
            return uri;
        }
    };
    public static final TypeAdapterFactory URI_FACTORY = newFactory(URI.class, URI);
    public static final TypeAdapter<URL> URL = new TypeAdapter<URL>() {
        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            String nextString = jsonReader.nextString();
            if ("null".equals(nextString)) {
                return null;
            }
            return new URL(nextString);
        }

        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            URL url = (URL) obj;
            if (url == null) {
                str = null;
            } else {
                str = url.toExternalForm();
            }
            jsonWriter.value(str);
        }
    };
    public static final TypeAdapterFactory URL_FACTORY = newFactory(URL.class, URL);
    public static final TypeAdapter<UUID> UUID = new TypeAdapter<UUID>() {
        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            UUID uuid = (UUID) obj;
            if (uuid == null) {
                str = null;
            } else {
                str = uuid.toString();
            }
            jsonWriter.value(str);
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return UUID.fromString(jsonReader.nextString());
            }
            jsonReader.nextNull();
            return null;
        }
    };
    public static final TypeAdapterFactory UUID_FACTORY = newFactory(UUID.class, UUID);

    private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
        private final Map<T, String> constantToName = new HashMap();
        private final Map<String, T> nameToConstant = new HashMap();

        public final /* bridge */ /* synthetic */ void write(JsonWriter jsonWriter, Object obj) throws IOException {
            String str;
            Enum enumR = (Enum) obj;
            if (enumR == null) {
                str = null;
            } else {
                str = (String) this.constantToName.get(enumR);
            }
            jsonWriter.value(str);
        }

        public EnumTypeAdapter(Class<T> classOfT) {
            try {
                for (T constant : (Enum[]) classOfT.getEnumConstants()) {
                    String name = constant.name();
                    SerializedName annotation = (SerializedName) classOfT.getField(name).getAnnotation(SerializedName.class);
                    if (annotation != null) {
                        name = annotation.value();
                        for (String alternate : annotation.alternate()) {
                            this.nameToConstant.put(alternate, constant);
                        }
                    }
                    this.nameToConstant.put(name, constant);
                    this.constantToName.put(constant, name);
                }
            } catch (NoSuchFieldException e) {
                throw new AssertionError(e);
            }
        }

        public final /* bridge */ /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() != JsonToken.NULL) {
                return (Enum) this.nameToConstant.get(jsonReader.nextString());
            }
            jsonReader.nextNull();
            return null;
        }
    }

    static {
        final Class cls = Calendar.class;
        final Class cls2 = GregorianCalendar.class;
        final TypeAdapter typeAdapter = CALENDAR;
        CALENDAR_FACTORY = new TypeAdapterFactory() {
            public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                Class<? super T> rawType = typeToken.getRawType();
                return (rawType == cls || rawType == cls2) ? typeAdapter : null;
            }

            public final String toString() {
                return "Factory[type=" + cls.getName() + "+" + cls2.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(final Class<TT> type, final TypeAdapter<TT> typeAdapter) {
        return new TypeAdapterFactory() {
            public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                return typeToken.getRawType() == type ? typeAdapter : null;
            }

            public final String toString() {
                return "Factory[type=" + type.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(final Class<TT> unboxed, final Class<TT> boxed, final TypeAdapter<? super TT> typeAdapter) {
        return new TypeAdapterFactory() {
            public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                Class<? super T> rawType = typeToken.getRawType();
                return (rawType == unboxed || rawType == boxed) ? typeAdapter : null;
            }

            public final String toString() {
                return "Factory[type=" + boxed.getName() + "+" + unboxed.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }

    private static <T1> TypeAdapterFactory newTypeHierarchyFactory(final Class<T1> clazz, final TypeAdapter<T1> typeAdapter) {
        return new TypeAdapterFactory() {
            public final <T2> TypeAdapter<T2> create(Gson gson, TypeToken<T2> typeToken) {
                final Class<? super T2> requestedType = typeToken.getRawType();
                if (clazz.isAssignableFrom(requestedType)) {
                    return new TypeAdapter<T1>() {
                        public final void write(JsonWriter out, T1 value) throws IOException {
                            typeAdapter.write(out, value);
                        }

                        public final T1 read(JsonReader in) throws IOException {
                            T1 result = typeAdapter.read(in);
                            if (result == null || requestedType.isInstance(result)) {
                                return result;
                            }
                            throw new JsonSyntaxException("Expected a " + requestedType.getName() + " but was " + result.getClass().getName());
                        }
                    };
                }
                return null;
            }

            public final String toString() {
                return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }
}
