package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ArrayBuilders.BooleanBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.ByteBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.DoubleBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.FloatBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.IntBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.LongBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.ShortBuilder;
import java.io.IOException;

public abstract class PrimitiveArrayDeserializers<T> extends StdDeserializer<T> implements ContextualDeserializer {
    protected final Boolean _unwrapSingle;

    @JacksonStdImpl
    static final class BooleanDeser extends PrimitiveArrayDeserializers<boolean[]> {
        private static final long serialVersionUID = 1;

        public BooleanDeser() {
            super(boolean[].class);
        }

        protected BooleanDeser(BooleanDeser base, Boolean unwrapSingle) {
            super(base, unwrapSingle);
        }

        protected final PrimitiveArrayDeserializers<?> withResolved(Boolean unwrapSingle) {
            return new BooleanDeser(this, unwrapSingle);
        }

        public final boolean[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (!p.isExpectedStartArrayToken()) {
                return (boolean[]) handleNonArray(p, ctxt);
            }
            BooleanBuilder builder = ctxt.getArrayBuilders().getBooleanBuilder();
            Object chunk = (boolean[]) builder.resetAndStart();
            int ix = 0;
            while (p.nextToken() != JsonToken.END_ARRAY) {
                try {
                    int ix2;
                    boolean value = _parseBooleanPrimitive(p, ctxt);
                    if (ix >= chunk.length) {
                        chunk = (boolean[]) builder.appendCompletedChunk(chunk, ix);
                        ix2 = 0;
                    } else {
                        ix2 = ix;
                    }
                    ix = ix2 + 1;
                    chunk[ix2] = value;
                } catch (Throwable e) {
                    throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
                }
            }
            return (boolean[]) builder.completeAndClearBuffer(chunk, ix);
        }

        protected final boolean[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new boolean[]{_parseBooleanPrimitive(p, ctxt)};
        }
    }

    @JacksonStdImpl
    static final class ByteDeser extends PrimitiveArrayDeserializers<byte[]> {
        private static final long serialVersionUID = 1;

        public ByteDeser() {
            super(byte[].class);
        }

        protected ByteDeser(ByteDeser base, Boolean unwrapSingle) {
            super(base, unwrapSingle);
        }

        protected final PrimitiveArrayDeserializers<?> withResolved(Boolean unwrapSingle) {
            return new ByteDeser(this, unwrapSingle);
        }

        public final byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_STRING) {
                return p.getBinaryValue(ctxt.getBase64Variant());
            }
            if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
                Object ob = p.getEmbeddedObject();
                if (ob == null) {
                    return null;
                }
                if (ob instanceof byte[]) {
                    return (byte[]) ob;
                }
            }
            if (!p.isExpectedStartArrayToken()) {
                return (byte[]) handleNonArray(p, ctxt);
            }
            ByteBuilder builder = ctxt.getArrayBuilders().getByteBuilder();
            Object chunk = (byte[]) builder.resetAndStart();
            int ix = 0;
            while (true) {
                try {
                    t = p.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        return (byte[]) builder.completeAndClearBuffer(chunk, ix);
                    }
                    byte value;
                    int ix2;
                    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                        value = p.getByteValue();
                    } else if (t == JsonToken.VALUE_NULL) {
                        value = (byte) 0;
                    } else {
                        value = ((Number) ctxt.handleUnexpectedToken(this._valueClass.getComponentType(), p)).byteValue();
                    }
                    if (ix >= chunk.length) {
                        chunk = (byte[]) builder.appendCompletedChunk(chunk, ix);
                        ix2 = 0;
                    } else {
                        ix2 = ix;
                    }
                    ix = ix2 + 1;
                    chunk[ix2] = value;
                } catch (Throwable e) {
                    throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
                }
            }
        }

        protected final byte[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            byte value;
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                value = p.getByteValue();
            } else if (t == JsonToken.VALUE_NULL) {
                return null;
            } else {
                value = ((Number) ctxt.handleUnexpectedToken(this._valueClass.getComponentType(), p)).byteValue();
            }
            return new byte[]{value};
        }
    }

    @JacksonStdImpl
    static final class CharDeser extends PrimitiveArrayDeserializers<char[]> {
        private static final long serialVersionUID = 1;

        public CharDeser() {
            super(char[].class);
        }

        protected CharDeser(CharDeser base, Boolean unwrapSingle) {
            super(base, unwrapSingle);
        }

        protected final PrimitiveArrayDeserializers<?> withResolved(Boolean unwrapSingle) {
            return this;
        }

        public final char[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_STRING) {
                char[] buffer = p.getTextCharacters();
                int offset = p.getTextOffset();
                int len = p.getTextLength();
                char[] result = new char[len];
                System.arraycopy(buffer, offset, result, 0, len);
                return result;
            } else if (p.isExpectedStartArrayToken()) {
                StringBuilder sb = new StringBuilder(64);
                while (true) {
                    t = p.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        return sb.toString().toCharArray();
                    }
                    String str;
                    if (t == JsonToken.VALUE_STRING) {
                        str = p.getText();
                    } else {
                        str = ((CharSequence) ctxt.handleUnexpectedToken(Character.TYPE, p)).toString();
                    }
                    if (str.length() != 1) {
                        ctxt.reportMappingException("Can not convert a JSON String of length %d into a char element of char array", Integer.valueOf(str.length()));
                    }
                    sb.append(str.charAt(0));
                }
            } else {
                if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
                    Object ob = p.getEmbeddedObject();
                    if (ob == null) {
                        return null;
                    }
                    if (ob instanceof char[]) {
                        return (char[]) ob;
                    }
                    if (ob instanceof String) {
                        return ((String) ob).toCharArray();
                    }
                    if (ob instanceof byte[]) {
                        return Base64Variants.getDefaultVariant().encode((byte[]) ob, false).toCharArray();
                    }
                }
                return (char[]) ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }

        protected final char[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return (char[]) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }

    @JacksonStdImpl
    static final class DoubleDeser extends PrimitiveArrayDeserializers<double[]> {
        private static final long serialVersionUID = 1;

        public DoubleDeser() {
            super(double[].class);
        }

        protected DoubleDeser(DoubleDeser base, Boolean unwrapSingle) {
            super(base, unwrapSingle);
        }

        protected final PrimitiveArrayDeserializers<?> withResolved(Boolean unwrapSingle) {
            return new DoubleDeser(this, unwrapSingle);
        }

        public final double[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return (double[]) handleNonArray(p, ctxt);
            }
            DoubleBuilder builder = ctxt.getArrayBuilders().getDoubleBuilder();
            Object chunk = (double[]) builder.resetAndStart();
            int ix = 0;
            while (p.nextToken() != JsonToken.END_ARRAY) {
                try {
                    int ix2;
                    double value = _parseDoublePrimitive(p, ctxt);
                    if (ix >= chunk.length) {
                        chunk = (double[]) builder.appendCompletedChunk(chunk, ix);
                        ix2 = 0;
                    } else {
                        ix2 = ix;
                    }
                    ix = ix2 + 1;
                    chunk[ix2] = value;
                } catch (Throwable e) {
                    throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
                }
            }
            return (double[]) builder.completeAndClearBuffer(chunk, ix);
        }

        protected final double[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new double[]{_parseDoublePrimitive(p, ctxt)};
        }
    }

    @JacksonStdImpl
    static final class FloatDeser extends PrimitiveArrayDeserializers<float[]> {
        private static final long serialVersionUID = 1;

        public FloatDeser() {
            super(float[].class);
        }

        protected FloatDeser(FloatDeser base, Boolean unwrapSingle) {
            super(base, unwrapSingle);
        }

        protected final PrimitiveArrayDeserializers<?> withResolved(Boolean unwrapSingle) {
            return new FloatDeser(this, unwrapSingle);
        }

        public final float[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (!p.isExpectedStartArrayToken()) {
                return (float[]) handleNonArray(p, ctxt);
            }
            FloatBuilder builder = ctxt.getArrayBuilders().getFloatBuilder();
            Object chunk = (float[]) builder.resetAndStart();
            int ix = 0;
            while (p.nextToken() != JsonToken.END_ARRAY) {
                try {
                    int ix2;
                    float value = _parseFloatPrimitive(p, ctxt);
                    if (ix >= chunk.length) {
                        chunk = (float[]) builder.appendCompletedChunk(chunk, ix);
                        ix2 = 0;
                    } else {
                        ix2 = ix;
                    }
                    ix = ix2 + 1;
                    chunk[ix2] = value;
                } catch (Throwable e) {
                    throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
                }
            }
            return (float[]) builder.completeAndClearBuffer(chunk, ix);
        }

        protected final float[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new float[]{_parseFloatPrimitive(p, ctxt)};
        }
    }

    @JacksonStdImpl
    static final class IntDeser extends PrimitiveArrayDeserializers<int[]> {
        public static final IntDeser instance = new IntDeser();
        private static final long serialVersionUID = 1;

        public IntDeser() {
            super(int[].class);
        }

        protected IntDeser(IntDeser base, Boolean unwrapSingle) {
            super(base, unwrapSingle);
        }

        protected final PrimitiveArrayDeserializers<?> withResolved(Boolean unwrapSingle) {
            return new IntDeser(this, unwrapSingle);
        }

        public final int[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return (int[]) handleNonArray(p, ctxt);
            }
            IntBuilder builder = ctxt.getArrayBuilders().getIntBuilder();
            Object chunk = (int[]) builder.resetAndStart();
            int ix = 0;
            while (p.nextToken() != JsonToken.END_ARRAY) {
                try {
                    int ix2;
                    int value = _parseIntPrimitive(p, ctxt);
                    if (ix >= chunk.length) {
                        chunk = (int[]) builder.appendCompletedChunk(chunk, ix);
                        ix2 = 0;
                    } else {
                        ix2 = ix;
                    }
                    ix = ix2 + 1;
                    chunk[ix2] = value;
                } catch (Throwable e) {
                    throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
                }
            }
            return (int[]) builder.completeAndClearBuffer(chunk, ix);
        }

        protected final int[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new int[]{_parseIntPrimitive(p, ctxt)};
        }
    }

    @JacksonStdImpl
    static final class LongDeser extends PrimitiveArrayDeserializers<long[]> {
        public static final LongDeser instance = new LongDeser();
        private static final long serialVersionUID = 1;

        public LongDeser() {
            super(long[].class);
        }

        protected LongDeser(LongDeser base, Boolean unwrapSingle) {
            super(base, unwrapSingle);
        }

        protected final PrimitiveArrayDeserializers<?> withResolved(Boolean unwrapSingle) {
            return new LongDeser(this, unwrapSingle);
        }

        public final long[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return (long[]) handleNonArray(p, ctxt);
            }
            LongBuilder builder = ctxt.getArrayBuilders().getLongBuilder();
            Object chunk = (long[]) builder.resetAndStart();
            int ix = 0;
            while (p.nextToken() != JsonToken.END_ARRAY) {
                try {
                    int ix2;
                    long value = _parseLongPrimitive(p, ctxt);
                    if (ix >= chunk.length) {
                        chunk = (long[]) builder.appendCompletedChunk(chunk, ix);
                        ix2 = 0;
                    } else {
                        ix2 = ix;
                    }
                    ix = ix2 + 1;
                    chunk[ix2] = value;
                } catch (Throwable e) {
                    throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
                }
            }
            return (long[]) builder.completeAndClearBuffer(chunk, ix);
        }

        protected final long[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new long[]{_parseLongPrimitive(p, ctxt)};
        }
    }

    @JacksonStdImpl
    static final class ShortDeser extends PrimitiveArrayDeserializers<short[]> {
        private static final long serialVersionUID = 1;

        public ShortDeser() {
            super(short[].class);
        }

        protected ShortDeser(ShortDeser base, Boolean unwrapSingle) {
            super(base, unwrapSingle);
        }

        protected final PrimitiveArrayDeserializers<?> withResolved(Boolean unwrapSingle) {
            return new ShortDeser(this, unwrapSingle);
        }

        public final short[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return (short[]) handleNonArray(p, ctxt);
            }
            ShortBuilder builder = ctxt.getArrayBuilders().getShortBuilder();
            Object chunk = (short[]) builder.resetAndStart();
            int ix = 0;
            while (p.nextToken() != JsonToken.END_ARRAY) {
                try {
                    int ix2;
                    short value = _parseShortPrimitive(p, ctxt);
                    if (ix >= chunk.length) {
                        chunk = (short[]) builder.appendCompletedChunk(chunk, ix);
                        ix2 = 0;
                    } else {
                        ix2 = ix;
                    }
                    ix = ix2 + 1;
                    chunk[ix2] = value;
                } catch (Throwable e) {
                    throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
                }
            }
            return (short[]) builder.completeAndClearBuffer(chunk, ix);
        }

        protected final short[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new short[]{_parseShortPrimitive(p, ctxt)};
        }
    }

    protected abstract T handleSingleElementUnwrapped(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    protected abstract PrimitiveArrayDeserializers<?> withResolved(Boolean bool);

    protected PrimitiveArrayDeserializers(Class<T> cls) {
        super((Class) cls);
        this._unwrapSingle = null;
    }

    protected PrimitiveArrayDeserializers(PrimitiveArrayDeserializers<?> base, Boolean unwrapSingle) {
        super(base._valueClass);
        this._unwrapSingle = unwrapSingle;
    }

    public static JsonDeserializer<?> forType(Class<?> rawType) {
        if (rawType == Integer.TYPE) {
            return IntDeser.instance;
        }
        if (rawType == Long.TYPE) {
            return LongDeser.instance;
        }
        if (rawType == Byte.TYPE) {
            return new ByteDeser();
        }
        if (rawType == Short.TYPE) {
            return new ShortDeser();
        }
        if (rawType == Float.TYPE) {
            return new FloatDeser();
        }
        if (rawType == Double.TYPE) {
            return new DoubleDeser();
        }
        if (rawType == Boolean.TYPE) {
            return new BooleanDeser();
        }
        if (rawType == Character.TYPE) {
            return new CharDeser();
        }
        throw new IllegalStateException();
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        Boolean unwrapSingle = findFormatFeature(ctxt, property, this._valueClass, Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return unwrapSingle == this._unwrapSingle ? this : withResolved(unwrapSingle);
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    protected T handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && p.getText().length() == 0) {
            return null;
        }
        boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (canWrap) {
            return handleSingleElementUnwrapped(p, ctxt);
        }
        return ctxt.handleUnexpectedToken(this._valueClass, p);
    }
}
