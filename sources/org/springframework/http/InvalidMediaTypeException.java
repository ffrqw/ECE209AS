package org.springframework.http;

public final class InvalidMediaTypeException extends IllegalArgumentException {
    private String mediaType;

    public InvalidMediaTypeException(String mediaType, String msg) {
        super("Invalid media type \"" + mediaType + "\": " + msg);
        this.mediaType = mediaType;
    }
}
