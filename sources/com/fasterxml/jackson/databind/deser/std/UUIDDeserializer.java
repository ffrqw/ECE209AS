package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class UUIDDeserializer extends FromStringDeserializer<UUID> {
    static final int[] HEX_DIGITS;
    private static final long serialVersionUID = 1;

    static {
        int i;
        int[] iArr = new int[127];
        HEX_DIGITS = iArr;
        Arrays.fill(iArr, -1);
        for (i = 0; i < 10; i++) {
            HEX_DIGITS[i + 48] = i;
        }
        for (i = 0; i < 6; i++) {
            HEX_DIGITS[i + 97] = i + 10;
            HEX_DIGITS[i + 65] = i + 10;
        }
    }

    public UUIDDeserializer() {
        super(UUID.class);
    }

    protected UUID _deserialize(String id, DeserializationContext ctxt) throws IOException {
        if (id.length() == 36) {
            if (!(id.charAt(8) == '-' && id.charAt(13) == '-' && id.charAt(18) == '-' && id.charAt(23) == '-')) {
                _badFormat(id, ctxt);
            }
            return new UUID((((long) intFromChars(id, 0, ctxt)) << 32) + ((((long) shortFromChars(id, 9, ctxt)) << 16) | ((long) shortFromChars(id, 14, ctxt))), (((long) ((shortFromChars(id, 19, ctxt) << 16) | shortFromChars(id, 24, ctxt))) << 32) | ((((long) intFromChars(id, 28, ctxt)) << 32) >>> 32));
        } else if (id.length() == 24) {
            return _fromBytes(Base64Variants.getDefaultVariant().decode(id), ctxt);
        } else {
            return _badFormat(id, ctxt);
        }
    }

    protected UUID _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException {
        if (ob instanceof byte[]) {
            return _fromBytes((byte[]) ob, ctxt);
        }
        super._deserializeEmbedded(ob, ctxt);
        return null;
    }

    private UUID _badFormat(String uuidStr, DeserializationContext ctxt) throws IOException {
        return (UUID) ctxt.handleWeirdStringValue(handledType(), uuidStr, "UUID has to be represented by standard 36-char representation", new Object[0]);
    }

    int intFromChars(String str, int index, DeserializationContext ctxt) throws JsonMappingException {
        return (((byteFromChars(str, index, ctxt) << 24) + (byteFromChars(str, index + 2, ctxt) << 16)) + (byteFromChars(str, index + 4, ctxt) << 8)) + byteFromChars(str, index + 6, ctxt);
    }

    int shortFromChars(String str, int index, DeserializationContext ctxt) throws JsonMappingException {
        return (byteFromChars(str, index, ctxt) << 8) + byteFromChars(str, index + 2, ctxt);
    }

    int byteFromChars(String str, int index, DeserializationContext ctxt) throws JsonMappingException {
        char c1 = str.charAt(index);
        char c2 = str.charAt(index + 1);
        if (c1 <= '' && c2 <= '') {
            int hex = (HEX_DIGITS[c1] << 4) | HEX_DIGITS[c2];
            if (hex >= 0) {
                return hex;
            }
        }
        if (c1 > '' || HEX_DIGITS[c1] < 0) {
            return _badChar(str, index, ctxt, c1);
        }
        return _badChar(str, index + 1, ctxt, c2);
    }

    int _badChar(String uuidStr, int index, DeserializationContext ctxt, char c) throws JsonMappingException {
        throw ctxt.weirdStringException(uuidStr, handledType(), String.format("Non-hex character '%c' (value 0x%s), not valid for UUID String", new Object[]{Character.valueOf(c), Integer.toHexString(c)}));
    }

    private UUID _fromBytes(byte[] bytes, DeserializationContext ctxt) throws JsonMappingException {
        if (bytes.length == 16) {
            return new UUID(_long(bytes, 0), _long(bytes, 8));
        }
        throw InvalidFormatException.from(ctxt.getParser(), "Can only construct UUIDs from byte[16]; got " + bytes.length + " bytes", bytes, handledType());
    }

    private static long _long(byte[] b, int offset) {
        return (((long) _int(b, offset)) << 32) | ((((long) _int(b, offset + 4)) << 32) >>> 32);
    }

    private static int _int(byte[] b, int offset) {
        return (((b[offset] << 24) | ((b[offset + 1] & 255) << 16)) | ((b[offset + 2] & 255) << 8)) | (b[offset + 3] & 255);
    }
}
