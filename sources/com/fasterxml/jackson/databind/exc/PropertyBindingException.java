package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public abstract class PropertyBindingException extends JsonMappingException {
    private static final int MAX_DESC_LENGTH = 1000;
    protected transient String _propertiesAsString;
    protected final Collection<Object> _propertyIds;
    protected final String _propertyName;
    protected final Class<?> _referringClass;

    protected PropertyBindingException(JsonParser p, String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds) {
        super((Closeable) p, msg, loc);
        this._referringClass = referringClass;
        this._propertyName = propName;
        this._propertyIds = propertyIds;
    }

    @Deprecated
    protected PropertyBindingException(String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds) {
        this(null, msg, loc, referringClass, propName, propertyIds);
    }

    public String getMessageSuffix() {
        String suffix = this._propertiesAsString;
        if (suffix != null || this._propertyIds == null) {
            return suffix;
        }
        StringBuilder sb = new StringBuilder(100);
        int len = this._propertyIds.size();
        if (len == 1) {
            sb.append(" (one known property: \"");
            sb.append(String.valueOf(this._propertyIds.iterator().next()));
            sb.append('\"');
        } else {
            sb.append(" (").append(len).append(" known properties: ");
            Iterator<Object> it = this._propertyIds.iterator();
            while (it.hasNext()) {
                sb.append('\"');
                sb.append(String.valueOf(it.next()));
                sb.append('\"');
                if (sb.length() > MAX_DESC_LENGTH) {
                    sb.append(" [truncated]");
                    break;
                } else if (it.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        sb.append("])");
        suffix = sb.toString();
        this._propertiesAsString = suffix;
        return suffix;
    }

    public Class<?> getReferringClass() {
        return this._referringClass;
    }

    public String getPropertyName() {
        return this._propertyName;
    }

    public Collection<Object> getKnownPropertyIds() {
        if (this._propertyIds == null) {
            return null;
        }
        return Collections.unmodifiableCollection(this._propertyIds);
    }
}
