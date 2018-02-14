package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JsonEOFException extends JsonParseException {
    protected final JsonToken _token;

    public JsonEOFException(JsonParser p, JsonToken token, String msg) {
        super(p, msg);
        this._token = token;
    }
}
