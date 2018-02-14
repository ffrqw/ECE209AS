package io.fabric.sdk.android.services.cache;

import android.content.Context;

public abstract class AbstractValueCache<T> implements ValueCache<T> {
    private final ValueCache<T> childCache;

    protected abstract void cacheValue$127ac70f(T t);

    protected abstract T getCached$dc0f261();

    public AbstractValueCache(ValueCache<T> childCache) {
        this.childCache = childCache;
    }

    public final synchronized T get(Context context, ValueLoader<T> loader) throws Exception {
        T value;
        value = getCached$dc0f261();
        if (value == null) {
            if (this.childCache != null) {
                value = this.childCache.get(context, loader);
            } else {
                value = loader.load(context);
            }
            if (value == null) {
                throw new NullPointerException();
            }
            cacheValue$127ac70f(value);
        }
        return value;
    }
}
