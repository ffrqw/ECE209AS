package io.fabric.sdk.android.services.cache;

public final class MemoryValueCache<T> extends AbstractValueCache<T> {
    private T value;

    public MemoryValueCache() {
        this(null);
    }

    private MemoryValueCache(ValueCache<T> valueCache) {
        super(null);
    }

    protected final T getCached$dc0f261() {
        return this.value;
    }

    protected final void cacheValue$127ac70f(T value) {
        this.value = value;
    }
}
