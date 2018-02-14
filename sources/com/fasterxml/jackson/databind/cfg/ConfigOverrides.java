package com.fasterxml.jackson.databind.cfg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigOverrides implements Serializable {
    private static final long serialVersionUID = 1;
    protected Map<Class<?>, MutableConfigOverride> _overrides;

    public ConfigOverrides() {
        this._overrides = null;
    }

    protected ConfigOverrides(Map<Class<?>, MutableConfigOverride> overrides) {
        this._overrides = overrides;
    }

    public ConfigOverrides copy() {
        if (this._overrides == null) {
            return new ConfigOverrides();
        }
        Map<Class<?>, MutableConfigOverride> newOverrides = _newMap();
        for (Entry<Class<?>, MutableConfigOverride> entry : this._overrides.entrySet()) {
            newOverrides.put(entry.getKey(), ((MutableConfigOverride) entry.getValue()).copy());
        }
        return new ConfigOverrides(newOverrides);
    }

    public ConfigOverride findOverride(Class<?> type) {
        if (this._overrides == null) {
            return null;
        }
        return (ConfigOverride) this._overrides.get(type);
    }

    public MutableConfigOverride findOrCreateOverride(Class<?> type) {
        if (this._overrides == null) {
            this._overrides = _newMap();
        }
        MutableConfigOverride override = (MutableConfigOverride) this._overrides.get(type);
        if (override != null) {
            return override;
        }
        override = new MutableConfigOverride();
        this._overrides.put(type, override);
        return override;
    }

    protected Map<Class<?>, MutableConfigOverride> _newMap() {
        return new HashMap();
    }
}
