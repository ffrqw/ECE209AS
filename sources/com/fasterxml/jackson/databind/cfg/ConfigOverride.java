package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

public abstract class ConfigOverride {
    protected Value _format;
    protected JsonIgnoreProperties.Value _ignorals;
    protected JsonInclude.Value _include;
    protected Boolean _isIgnoredType;

    protected ConfigOverride() {
    }

    protected ConfigOverride(ConfigOverride src) {
        this._format = src._format;
        this._include = src._include;
        this._ignorals = src._ignorals;
        this._isIgnoredType = src._isIgnoredType;
    }

    public Value getFormat() {
        return this._format;
    }

    public JsonInclude.Value getInclude() {
        return this._include;
    }

    public JsonIgnoreProperties.Value getIgnorals() {
        return this._ignorals;
    }

    public Boolean getIsIgnoredType() {
        return this._isIgnoredType;
    }
}
