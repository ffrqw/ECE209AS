package org.springframework.beans;

public abstract class PropertyAccessException extends BeansException {
    public PropertyAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
