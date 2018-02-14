package io.fabric.sdk.android;

public interface InitializationCallback<T> {
    public static final InitializationCallback EMPTY = new Empty();

    public static class Empty implements InitializationCallback<Object> {
        private Empty() {
        }

        public final void success$5d527811() {
        }

        public final void failure(Exception exception) {
        }
    }

    void failure(Exception exception);

    void success$5d527811();
}
