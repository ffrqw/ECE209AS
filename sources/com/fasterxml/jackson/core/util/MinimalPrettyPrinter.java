package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import java.io.IOException;
import java.io.Serializable;

public class MinimalPrettyPrinter implements PrettyPrinter, Serializable {
    protected String _rootValueSeparator;

    public MinimalPrettyPrinter() {
        this(" ");
    }

    public MinimalPrettyPrinter(String rootValueSeparator) {
        this._rootValueSeparator = " ";
        this._rootValueSeparator = rootValueSeparator;
    }

    public void writeRootValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
        if (this._rootValueSeparator != null) {
            jg.writeRaw(this._rootValueSeparator);
        }
    }

    public void writeStartObject(JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw('{');
    }

    public void beforeObjectEntries(JsonGenerator jg) throws IOException, JsonGenerationException {
    }

    public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(':');
    }

    public void writeObjectEntrySeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(',');
    }

    public void writeEndObject(JsonGenerator jg, int nrOfEntries) throws IOException, JsonGenerationException {
        jg.writeRaw('}');
    }

    public void writeStartArray(JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw('[');
    }

    public void beforeArrayValues(JsonGenerator jg) throws IOException, JsonGenerationException {
    }

    public void writeArrayValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(',');
    }

    public void writeEndArray(JsonGenerator jg, int nrOfValues) throws IOException, JsonGenerationException {
        jg.writeRaw(']');
    }
}
