package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import java.util.Collection;

public class UnrecognizedPropertyException extends PropertyBindingException {
    private static final long serialVersionUID = 1;

    public UnrecognizedPropertyException(JsonParser p, String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds) {
        super(p, msg, loc, referringClass, propName, propertyIds);
    }

    @Deprecated
    public UnrecognizedPropertyException(String msg, JsonLocation loc, Class<?> referringClass, String propName, Collection<Object> propertyIds) {
        super(msg, loc, referringClass, propName, propertyIds);
    }

    public static UnrecognizedPropertyException from(JsonParser p, Object fromObjectOrClass, String propertyName, Collection<Object> propertyIds) {
        if (fromObjectOrClass == null) {
            throw new IllegalArgumentException();
        }
        Class<?> ref;
        if (fromObjectOrClass instanceof Class) {
            ref = (Class) fromObjectOrClass;
        } else {
            ref = fromObjectOrClass.getClass();
        }
        UnrecognizedPropertyException e = new UnrecognizedPropertyException(p, "Unrecognized field \"" + propertyName + "\" (class " + ref.getName() + "), not marked as ignorable", p.getCurrentLocation(), ref, propertyName, propertyIds);
        e.prependPath(fromObjectOrClass, propertyName);
        return e;
    }
}
