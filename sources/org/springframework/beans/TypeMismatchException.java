package org.springframework.beans;

import org.springframework.util.ClassUtils;

public final class TypeMismatchException extends PropertyAccessException {
    private Class<?> requiredType;
    private transient Object value;

    public TypeMismatchException(Object value, Class<?> requiredType) {
        this(value, requiredType, null);
    }

    private TypeMismatchException(Object value, Class<?> requiredType, Throwable cause) {
        super("Failed to convert value of type '" + ClassUtils.getDescriptiveType(value) + "'" + (requiredType != null ? " to required type '" + ClassUtils.getQualifiedName(requiredType) + "'" : ""), null);
        this.value = value;
        this.requiredType = requiredType;
    }
}
