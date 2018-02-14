package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import java.util.Collection;

public class IgnoredPropertyException extends PropertyBindingException {
    private static final long serialVersionUID = 1;

    public IgnoredPropertyException(JsonParser p, String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds) {
        super(p, msg, loc, referringClass, propName, propertyIds);
    }

    @Deprecated
    public IgnoredPropertyException(String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds) {
        super(msg, loc, referringClass, propName, propertyIds);
    }

    public static IgnoredPropertyException from(JsonParser p, Object fromObjectOrClass, String propertyName, Collection<Object> propertyIds) {
        if (fromObjectOrClass == null) {
            throw new IllegalArgumentException();
        }
        Class<?> ref;
        if (fromObjectOrClass instanceof Class) {
            ref = (Class) fromObjectOrClass;
        } else {
            ref = fromObjectOrClass.getClass();
        }
        IgnoredPropertyException e = new IgnoredPropertyException(p, "Ignored field \"" + propertyName + "\" (class " + ref.getName() + ") encountered; mapper configured not to allow this", p.getCurrentLocation(), ref, propertyName, propertyIds);
        e.prependPath(fromObjectOrClass, propertyName);
        return e;
    }
}
