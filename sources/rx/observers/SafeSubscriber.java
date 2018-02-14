package rx.observers;

import java.util.Arrays;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorFailedException;
import rx.plugins.RxJavaPlugins;

public final class SafeSubscriber<T> extends Subscriber<T> {
    private final Subscriber<? super T> actual;
    boolean done = false;

    public SafeSubscriber(Subscriber<? super T> actual) {
        super(actual);
        this.actual = actual;
    }

    public final void onCompleted() {
        if (!this.done) {
            this.done = true;
            try {
                this.actual.onCompleted();
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                _onError(e);
            } finally {
                unsubscribe();
            }
        }
    }

    public final void onError(Throwable e) {
        Exceptions.throwIfFatal(e);
        if (!this.done) {
            this.done = true;
            _onError(e);
        }
    }

    public final void onNext(T args) {
        try {
            if (!this.done) {
                this.actual.onNext(args);
            }
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            onError(e);
        }
    }

    private void _onError(Throwable e) {
        try {
            RxJavaPlugins.getInstance().getErrorHandler();
        } catch (Throwable pluginException) {
            handlePluginException(pluginException);
        }
        OnErrorFailedException onErrorFailedException;
        try {
            this.actual.onError(e);
            try {
                unsubscribe();
                return;
            } catch (RuntimeException unsubscribeException) {
                RxJavaPlugins.getInstance().getErrorHandler();
            } catch (Throwable pluginException2) {
                handlePluginException(pluginException2);
            }
            throw new OnErrorFailedException(unsubscribeException);
            unsubscribe();
            onErrorFailedException = new OnErrorFailedException("Error occurred when trying to propagate error to Observer.onError", new CompositeException(Arrays.asList(new Throwable[]{e, e2}), (byte) 0));
        } catch (Throwable unsubscribeException2) {
            try {
                RxJavaPlugins.getInstance().getErrorHandler();
            } catch (Throwable pluginException22) {
                handlePluginException(pluginException22);
            }
            onErrorFailedException = new OnErrorFailedException("Error occurred when trying to propagate error to Observer.onError and during unsubscription.", new CompositeException(Arrays.asList(new Throwable[]{e, e2, unsubscribeException2}), (byte) 0));
        }
    }

    private static void handlePluginException(Throwable pluginException) {
        System.err.println("RxJavaErrorHandler threw an Exception. It shouldn't. => " + pluginException.getMessage());
        pluginException.printStackTrace();
    }
}
