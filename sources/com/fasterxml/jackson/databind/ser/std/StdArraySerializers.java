package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

public class StdArraySerializers {
    protected static final HashMap<String, JsonSerializer<?>> _arraySerializers;

    @JacksonStdImpl
    public static class BooleanArraySerializer extends ArraySerializerBase<boolean[]> {
        private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Boolean.class);

        public BooleanArraySerializer() {
            super(boolean[].class);
        }

        protected BooleanArraySerializer(BooleanArraySerializer src, BeanProperty prop, Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }

        public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
            return new BooleanArraySerializer(this, prop, unwrapSingle);
        }

        public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
            return this;
        }

        public JavaType getContentType() {
            return VALUE_TYPE;
        }

        public JsonSerializer<?> getContentSerializer() {
            return null;
        }

        public boolean isEmpty(SerializerProvider prov, boolean[] value) {
            return value == null || value.length == 0;
        }

        public boolean hasSingleElement(boolean[] value) {
            return value.length == 1;
        }

        public final void serialize(boolean[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
            int len = value.length;
            if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
                serializeContents(value, g, provider);
                return;
            }
            g.writeStartArray(len);
            serializeContents(value, g, provider);
            g.writeEndArray();
        }

        public void serializeContents(boolean[] value, JsonGenerator g, SerializerProvider provider) throws IOException, JsonGenerationException {
            for (boolean writeBoolean : value) {
                g.writeBoolean(writeBoolean);
            }
        }

        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = createSchemaNode("array", true);
            o.set("items", createSchemaNode("boolean"));
            return o;
        }

        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitArrayFormat(visitor, typeHint, JsonFormatTypes.BOOLEAN);
        }
    }

    @JacksonStdImpl
    public static class CharArraySerializer extends StdSerializer<char[]> {
        public CharArraySerializer() {
            super(char[].class);
        }

        public boolean isEmpty(SerializerProvider prov, char[] value) {
            return value == null || value.length == 0;
        }

        public void serialize(char[] value, JsonGenerator g, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
                g.writeStartArray(value.length);
                _writeArrayContents(g, value);
                g.writeEndArray();
                return;
            }
            g.writeString(value, 0, value.length);
        }

        public void serializeWithType(char[] value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
            if (provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
                typeSer.writeTypePrefixForArray(value, g);
                _writeArrayContents(g, value);
                typeSer.writeTypeSuffixForArray(value, g);
                return;
            }
            typeSer.writeTypePrefixForScalar(value, g);
            g.writeString(value, 0, value.length);
            typeSer.writeTypeSuffixForScalar(value, g);
        }

        private final void _writeArrayContents(JsonGenerator g, char[] value) throws IOException, JsonGenerationException {
            int len = value.length;
            for (int i = 0; i < len; i++) {
                g.writeString(value, i, 1);
            }
        }

        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            ObjectNode o = createSchemaNode("array", true);
            ObjectNode itemSchema = createSchemaNode("string");
            itemSchema.put("type", "string");
            return o.set("items", itemSchema);
        }

        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitArrayFormat(visitor, typeHint, JsonFormatTypes.STRING);
        }
    }

    @JacksonStdImpl
    public static class DoubleArraySerializer extends ArraySerializerBase<double[]> {
        private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Double.TYPE);

        public DoubleArraySerializer() {
            super(double[].class);
        }

        protected DoubleArraySerializer(DoubleArraySerializer src, BeanProperty prop, Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }

        public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
            return new DoubleArraySerializer(this, prop, unwrapSingle);
        }

        public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
            return this;
        }

        public JavaType getContentType() {
            return VALUE_TYPE;
        }

        public JsonSerializer<?> getContentSerializer() {
            return null;
        }

        public boolean isEmpty(SerializerProvider prov, double[] value) {
            return value == null || value.length == 0;
        }

        public boolean hasSingleElement(double[] value) {
            return value.length == 1;
        }

        public final void serialize(double[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
            if (value.length == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
                serializeContents(value, g, provider);
                return;
            }
            g.setCurrentValue(value);
            g.writeArray(value, 0, value.length);
        }

        public void serializeContents(double[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
            for (double writeNumber : value) {
                g.writeNumber(writeNumber);
            }
        }

        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("array", true).set("items", createSchemaNode("number"));
        }

        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitArrayFormat(visitor, typeHint, JsonFormatTypes.NUMBER);
        }
    }

    protected static abstract class TypedPrimitiveArraySerializer<T> extends ArraySerializerBase<T> {
        protected final TypeSerializer _valueTypeSerializer;

        protected TypedPrimitiveArraySerializer(Class<T> cls) {
            super((Class) cls);
            this._valueTypeSerializer = null;
        }

        protected TypedPrimitiveArraySerializer(TypedPrimitiveArraySerializer<T> src, BeanProperty prop, TypeSerializer vts, Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
            this._valueTypeSerializer = vts;
        }
    }

    @JacksonStdImpl
    public static class FloatArraySerializer extends TypedPrimitiveArraySerializer<float[]> {
        private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Float.TYPE);

        public FloatArraySerializer() {
            super(float[].class);
        }

        public FloatArraySerializer(FloatArraySerializer src, BeanProperty prop, TypeSerializer vts, Boolean unwrapSingle) {
            super(src, prop, vts, unwrapSingle);
        }

        public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
            return new FloatArraySerializer(this, this._property, vts, this._unwrapSingle);
        }

        public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
            return new FloatArraySerializer(this, prop, this._valueTypeSerializer, unwrapSingle);
        }

        public JavaType getContentType() {
            return VALUE_TYPE;
        }

        public JsonSerializer<?> getContentSerializer() {
            return null;
        }

        public boolean isEmpty(SerializerProvider prov, float[] value) {
            return value == null || value.length == 0;
        }

        public boolean hasSingleElement(float[] value) {
            return value.length == 1;
        }

        public final void serialize(float[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
            int len = value.length;
            if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
                serializeContents(value, g, provider);
                return;
            }
            g.writeStartArray(len);
            serializeContents(value, g, provider);
            g.writeEndArray();
        }

        public void serializeContents(float[] value, JsonGenerator g, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (this._valueTypeSerializer != null) {
                for (float writeNumber : value) {
                    this._valueTypeSerializer.writeTypePrefixForScalar(null, g, Float.TYPE);
                    g.writeNumber(writeNumber);
                    this._valueTypeSerializer.writeTypeSuffixForScalar(null, g);
                }
                return;
            }
            for (float writeNumber2 : value) {
                g.writeNumber(writeNumber2);
            }
        }

        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("array", true).set("items", createSchemaNode("number"));
        }

        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitArrayFormat(visitor, typeHint, JsonFormatTypes.NUMBER);
        }
    }

    @JacksonStdImpl
    public static class IntArraySerializer extends ArraySerializerBase<int[]> {
        private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Integer.TYPE);

        public IntArraySerializer() {
            super(int[].class);
        }

        protected IntArraySerializer(IntArraySerializer src, BeanProperty prop, Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }

        public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
            return new IntArraySerializer(this, prop, unwrapSingle);
        }

        public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
            return this;
        }

        public JavaType getContentType() {
            return VALUE_TYPE;
        }

        public JsonSerializer<?> getContentSerializer() {
            return null;
        }

        public boolean isEmpty(SerializerProvider prov, int[] value) {
            return value == null || value.length == 0;
        }

        public boolean hasSingleElement(int[] value) {
            return value.length == 1;
        }

        public final void serialize(int[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
            if (value.length == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
                serializeContents(value, g, provider);
                return;
            }
            g.setCurrentValue(value);
            g.writeArray(value, 0, value.length);
        }

        public void serializeContents(int[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
            for (int writeNumber : value) {
                g.writeNumber(writeNumber);
            }
        }

        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("array", true).set("items", createSchemaNode("integer"));
        }

        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitArrayFormat(visitor, typeHint, JsonFormatTypes.INTEGER);
        }
    }

    @JacksonStdImpl
    public static class LongArraySerializer extends TypedPrimitiveArraySerializer<long[]> {
        private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Long.TYPE);

        public LongArraySerializer() {
            super(long[].class);
        }

        public LongArraySerializer(LongArraySerializer src, BeanProperty prop, TypeSerializer vts, Boolean unwrapSingle) {
            super(src, prop, vts, unwrapSingle);
        }

        public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
            return new LongArraySerializer(this, prop, this._valueTypeSerializer, unwrapSingle);
        }

        public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
            return new LongArraySerializer(this, this._property, vts, this._unwrapSingle);
        }

        public JavaType getContentType() {
            return VALUE_TYPE;
        }

        public JsonSerializer<?> getContentSerializer() {
            return null;
        }

        public boolean isEmpty(SerializerProvider prov, long[] value) {
            return value == null || value.length == 0;
        }

        public boolean hasSingleElement(long[] value) {
            return value.length == 1;
        }

        public final void serialize(long[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
            if (value.length == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
                serializeContents(value, g, provider);
                return;
            }
            g.setCurrentValue(value);
            g.writeArray(value, 0, value.length);
        }

        public void serializeContents(long[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
            if (this._valueTypeSerializer != null) {
                for (long writeNumber : value) {
                    this._valueTypeSerializer.writeTypePrefixForScalar(null, g, Long.TYPE);
                    g.writeNumber(writeNumber);
                    this._valueTypeSerializer.writeTypeSuffixForScalar(null, g);
                }
                return;
            }
            for (long writeNumber2 : value) {
                g.writeNumber(writeNumber2);
            }
        }

        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("array", true).set("items", createSchemaNode("number", true));
        }

        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitArrayFormat(visitor, typeHint, JsonFormatTypes.NUMBER);
        }
    }

    @JacksonStdImpl
    public static class ShortArraySerializer extends TypedPrimitiveArraySerializer<short[]> {
        private static final JavaType VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Short.TYPE);

        public ShortArraySerializer() {
            super(short[].class);
        }

        public ShortArraySerializer(ShortArraySerializer src, BeanProperty prop, TypeSerializer vts, Boolean unwrapSingle) {
            super(src, prop, vts, unwrapSingle);
        }

        public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
            return new ShortArraySerializer(this, prop, this._valueTypeSerializer, unwrapSingle);
        }

        public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
            return new ShortArraySerializer(this, this._property, vts, this._unwrapSingle);
        }

        public JavaType getContentType() {
            return VALUE_TYPE;
        }

        public JsonSerializer<?> getContentSerializer() {
            return null;
        }

        public boolean isEmpty(SerializerProvider prov, short[] value) {
            return value == null || value.length == 0;
        }

        public boolean hasSingleElement(short[] value) {
            return value.length == 1;
        }

        public final void serialize(short[] value, JsonGenerator g, SerializerProvider provider) throws IOException {
            int len = value.length;
            if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
                serializeContents(value, g, provider);
                return;
            }
            g.writeStartArray(len);
            serializeContents(value, g, provider);
            g.writeEndArray();
        }

        public void serializeContents(short[] value, JsonGenerator g, SerializerProvider provider) throws IOException, JsonGenerationException {
            if (this._valueTypeSerializer != null) {
                for (short writeNumber : value) {
                    this._valueTypeSerializer.writeTypePrefixForScalar(null, g, Short.TYPE);
                    g.writeNumber(writeNumber);
                    this._valueTypeSerializer.writeTypeSuffixForScalar(null, g);
                }
                return;
            }
            for (int writeNumber2 : value) {
                g.writeNumber(writeNumber2);
            }
        }

        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("array", true).set("items", createSchemaNode("integer"));
        }

        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitArrayFormat(visitor, typeHint, JsonFormatTypes.INTEGER);
        }
    }

    static {
        HashMap hashMap = new HashMap();
        _arraySerializers = hashMap;
        hashMap.put(boolean[].class.getName(), new BooleanArraySerializer());
        _arraySerializers.put(byte[].class.getName(), new ByteArraySerializer());
        _arraySerializers.put(char[].class.getName(), new CharArraySerializer());
        _arraySerializers.put(short[].class.getName(), new ShortArraySerializer());
        _arraySerializers.put(int[].class.getName(), new IntArraySerializer());
        _arraySerializers.put(long[].class.getName(), new LongArraySerializer());
        _arraySerializers.put(float[].class.getName(), new FloatArraySerializer());
        _arraySerializers.put(double[].class.getName(), new DoubleArraySerializer());
    }

    protected StdArraySerializers() {
    }

    public static JsonSerializer<?> findStandardImpl(Class<?> cls) {
        return (JsonSerializer) _arraySerializers.get(cls.getName());
    }
}
