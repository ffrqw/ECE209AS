package rx.exceptions;

public final class OnErrorFailedException extends RuntimeException {
    public OnErrorFailedException(String message, Throwable e) {
        super(message, e);
    }

    public OnErrorFailedException(Throwable e) {
        super(e.getMessage(), e);
    }
}
