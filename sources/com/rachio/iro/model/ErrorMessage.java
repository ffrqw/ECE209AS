package com.rachio.iro.model;

public class ErrorMessage extends ModelObject {
    private static final long serialVersionUID = 1;
    public int code;
    public String message;

    public ErrorMessage(String message) {
        this(0, message);
    }

    public ErrorMessage(int code) {
        this(code, null);
    }

    public ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
