package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import java.io.IOException;
import java.io.Serializable;

public class DefaultPrettyPrinter implements PrettyPrinter, Instantiatable<DefaultPrettyPrinter>, Serializable {
    public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
    protected Indenter _arrayIndenter;
    protected transient int _nesting;
    protected Indenter _objectIndenter;
    protected final SerializableString _rootSeparator;
    protected boolean _spacesInObjectEntries;

    public interface Indenter {
        boolean isInline();

        void writeIndentation(JsonGenerator jsonGenerator, int i) throws IOException;
    }

    public static class NopIndenter implements Indenter, Serializable {
        public static final NopIndenter instance = new NopIndenter();

        public void writeIndentation(JsonGenerator jg, int level) throws IOException {
        }

        public boolean isInline() {
            return true;
        }
    }

    public static class FixedSpaceIndenter extends NopIndenter {
        public static final FixedSpaceIndenter instance = new FixedSpaceIndenter();

        public void writeIndentation(JsonGenerator jg, int level) throws IOException {
            jg.writeRaw(' ');
        }

        public boolean isInline() {
            return true;
        }
    }

    public DefaultPrettyPrinter() {
        this(DEFAULT_ROOT_VALUE_SEPARATOR);
    }

    public DefaultPrettyPrinter(SerializableString rootSeparator) {
        this._arrayIndenter = FixedSpaceIndenter.instance;
        this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
        this._spacesInObjectEntries = true;
        this._rootSeparator = rootSeparator;
    }

    public DefaultPrettyPrinter(DefaultPrettyPrinter base) {
        this(base, base._rootSeparator);
    }

    public DefaultPrettyPrinter(DefaultPrettyPrinter base, SerializableString rootSeparator) {
        this._arrayIndenter = FixedSpaceIndenter.instance;
        this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
        this._spacesInObjectEntries = true;
        this._arrayIndenter = base._arrayIndenter;
        this._objectIndenter = base._objectIndenter;
        this._spacesInObjectEntries = base._spacesInObjectEntries;
        this._nesting = base._nesting;
        this._rootSeparator = rootSeparator;
    }

    public DefaultPrettyPrinter createInstance() {
        return new DefaultPrettyPrinter(this);
    }

    public void writeRootValueSeparator(JsonGenerator jg) throws IOException {
        if (this._rootSeparator != null) {
            jg.writeRaw(this._rootSeparator);
        }
    }

    public void writeStartObject(JsonGenerator jg) throws IOException {
        jg.writeRaw('{');
        if (!this._objectIndenter.isInline()) {
            this._nesting++;
        }
    }

    public void beforeObjectEntries(JsonGenerator jg) throws IOException {
        this._objectIndenter.writeIndentation(jg, this._nesting);
    }

    public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
        if (this._spacesInObjectEntries) {
            jg.writeRaw(" : ");
        } else {
            jg.writeRaw(':');
        }
    }

    public void writeObjectEntrySeparator(JsonGenerator jg) throws IOException {
        jg.writeRaw(',');
        this._objectIndenter.writeIndentation(jg, this._nesting);
    }

    public void writeEndObject(JsonGenerator jg, int nrOfEntries) throws IOException {
        if (!this._objectIndenter.isInline()) {
            this._nesting--;
        }
        if (nrOfEntries > 0) {
            this._objectIndenter.writeIndentation(jg, this._nesting);
        } else {
            jg.writeRaw(' ');
        }
        jg.writeRaw('}');
    }

    public void writeStartArray(JsonGenerator jg) throws IOException {
        if (!this._arrayIndenter.isInline()) {
            this._nesting++;
        }
        jg.writeRaw('[');
    }

    public void beforeArrayValues(JsonGenerator jg) throws IOException {
        this._arrayIndenter.writeIndentation(jg, this._nesting);
    }

    public void writeArrayValueSeparator(JsonGenerator gen) throws IOException {
        gen.writeRaw(',');
        this._arrayIndenter.writeIndentation(gen, this._nesting);
    }

    public void writeEndArray(JsonGenerator gen, int nrOfValues) throws IOException {
        if (!this._arrayIndenter.isInline()) {
            this._nesting--;
        }
        if (nrOfValues > 0) {
            this._arrayIndenter.writeIndentation(gen, this._nesting);
        } else {
            gen.writeRaw(' ');
        }
        gen.writeRaw(']');
    }
}
