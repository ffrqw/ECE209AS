package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.io.CharTypes;

public final class JsonReadContext extends JsonStreamContext {
    protected JsonReadContext _child;
    protected int _columnNr;
    protected String _currentName;
    protected Object _currentValue;
    protected DupDetector _dups;
    protected int _lineNr;
    protected final JsonReadContext _parent;

    public JsonReadContext(JsonReadContext parent, DupDetector dups, int type, int lineNr, int colNr) {
        this._parent = parent;
        this._dups = dups;
        this._type = type;
        this._lineNr = lineNr;
        this._columnNr = colNr;
        this._index = -1;
    }

    protected final void reset(int type, int lineNr, int colNr) {
        this._type = type;
        this._index = -1;
        this._lineNr = lineNr;
        this._columnNr = colNr;
        this._currentName = null;
        this._currentValue = null;
        if (this._dups != null) {
            this._dups.reset();
        }
    }

    public final JsonReadContext withDupDetector(DupDetector dups) {
        this._dups = dups;
        return this;
    }

    public final Object getCurrentValue() {
        return this._currentValue;
    }

    public final void setCurrentValue(Object v) {
        this._currentValue = v;
    }

    public static JsonReadContext createRootContext(DupDetector dups) {
        return new JsonReadContext(null, dups, 0, 1, 0);
    }

    public final JsonReadContext createChildArrayContext(int lineNr, int colNr) {
        JsonReadContext ctxt = this._child;
        if (ctxt == null) {
            ctxt = new JsonReadContext(this, this._dups == null ? null : this._dups.child(), 1, lineNr, colNr);
            this._child = ctxt;
        } else {
            ctxt.reset(1, lineNr, colNr);
        }
        return ctxt;
    }

    public final JsonReadContext createChildObjectContext(int lineNr, int colNr) {
        JsonReadContext ctxt = this._child;
        if (ctxt == null) {
            ctxt = new JsonReadContext(this, this._dups == null ? null : this._dups.child(), 2, lineNr, colNr);
            this._child = ctxt;
            return ctxt;
        }
        ctxt.reset(2, lineNr, colNr);
        return ctxt;
    }

    public final String getCurrentName() {
        return this._currentName;
    }

    public final JsonReadContext getParent() {
        return this._parent;
    }

    public final JsonReadContext clearAndGetParent() {
        this._currentValue = null;
        return this._parent;
    }

    public final JsonLocation getStartLocation(Object srcRef) {
        return new JsonLocation(srcRef, -1, this._lineNr, this._columnNr);
    }

    public final DupDetector getDupDetector() {
        return this._dups;
    }

    public final boolean expectComma() {
        int ix = this._index + 1;
        this._index = ix;
        return this._type != 0 && ix > 0;
    }

    public final void setCurrentName(String name) throws JsonProcessingException {
        this._currentName = name;
        if (this._dups != null) {
            _checkDup(this._dups, name);
        }
    }

    private void _checkDup(DupDetector dd, String name) throws JsonProcessingException {
        if (dd.isDup(name)) {
            Object src = dd.getSource();
            throw new JsonParseException(src instanceof JsonParser ? (JsonParser) src : null, "Duplicate field '" + name + "'");
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(64);
        switch (this._type) {
            case 0:
                sb.append("/");
                break;
            case 1:
                sb.append('[');
                sb.append(getCurrentIndex());
                sb.append(']');
                break;
            default:
                sb.append('{');
                if (this._currentName != null) {
                    sb.append('\"');
                    CharTypes.appendQuoted(sb, this._currentName);
                    sb.append('\"');
                } else {
                    sb.append('?');
                }
                sb.append('}');
                break;
        }
        return sb.toString();
    }
}
