package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

public class MutableConfigOverride extends ConfigOverride implements Serializable {
    private static final long serialVersionUID = 1;

    protected MutableConfigOverride(MutableConfigOverride src) {
        super(src);
    }

    protected MutableConfigOverride copy() {
        return new MutableConfigOverride(this);
    }

    public MutableConfigOverride setFormat(Value v) {
        this._format = v;
        return this;
    }

    public MutableConfigOverride setInclude(JsonInclude.Value v) {
        this._include = v;
        return this;
    }

    public MutableConfigOverride setIgnorals(JsonIgnoreProperties.Value v) {
        this._ignorals = v;
        return this;
    }

    public MutableConfigOverride setIsIgnoredType(Boolean v) {
        this._isIgnoredType = v;
        return this;
    }
}
