package dagger.internal;

import javax.inject.Provider;

public final class DoubleCheck<T> implements Provider<T> {
    static final /* synthetic */ boolean $assertionsDisabled = (!DoubleCheck.class.desiredAssertionStatus());
    private static final Object UNINITIALIZED = new Object();
    private volatile Object instance = UNINITIALIZED;
    private volatile Provider<T> provider;

    private DoubleCheck(Provider<T> provider) {
        if ($assertionsDisabled || provider != null) {
            this.provider = provider;
            return;
        }
        throw new AssertionError();
    }

    public final T get() {
        Object result = this.instance;
        if (result == UNINITIALIZED) {
            synchronized (this) {
                result = this.instance;
                if (result == UNINITIALIZED) {
                    result = this.provider.get();
                    Object currentInstance = this.instance;
                    if (currentInstance == UNINITIALIZED || currentInstance == result) {
                        this.instance = result;
                        this.provider = null;
                    } else {
                        throw new IllegalStateException("Scoped provider was invoked recursively returning different results: " + currentInstance + " & " + result + ". This is likely due to a circular dependency.");
                    }
                }
            }
        }
        return result;
    }

    public static <T> Provider<T> provider(Provider<T> delegate) {
        Preconditions.checkNotNull(delegate);
        return delegate instanceof DoubleCheck ? delegate : new DoubleCheck(delegate);
    }
}
