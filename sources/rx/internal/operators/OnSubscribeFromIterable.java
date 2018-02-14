package rx.internal.operators;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;

public final class OnSubscribeFromIterable<T> implements OnSubscribe<T> {
    final Iterable<? extends T> is;

    private static final class IterableProducer<T> implements Producer {
        private static final AtomicLongFieldUpdater<IterableProducer> REQUESTED_UPDATER = AtomicLongFieldUpdater.newUpdater(IterableProducer.class, "requested");
        private final Iterator<? extends T> it;
        private final Subscriber<? super T> o;
        private volatile long requested;

        private IterableProducer(Subscriber<? super T> o, Iterator<? extends T> it) {
            this.requested = 0;
            this.o = o;
            this.it = it;
        }

        public final void request(long n) {
            if (this.requested != Long.MAX_VALUE) {
                if (n == Long.MAX_VALUE && REQUESTED_UPDATER.compareAndSet(this, 0, Long.MAX_VALUE)) {
                    while (!this.o.isUnsubscribed()) {
                        if (this.it.hasNext()) {
                            this.o.onNext(this.it.next());
                        } else if (!this.o.isUnsubscribed()) {
                            this.o.onCompleted();
                            return;
                        } else {
                            return;
                        }
                    }
                } else if (n > 0 && BackpressureUtils.getAndAddRequest(REQUESTED_UPDATER, this, n) == 0) {
                    long r;
                    do {
                        r = this.requested;
                        long numToEmit = r;
                        while (!this.o.isUnsubscribed()) {
                            if (this.it.hasNext()) {
                                numToEmit--;
                                if (numToEmit >= 0) {
                                    this.o.onNext(this.it.next());
                                }
                            } else if (!this.o.isUnsubscribed()) {
                                this.o.onCompleted();
                                return;
                            } else {
                                return;
                            }
                        }
                        return;
                    } while (REQUESTED_UPDATER.addAndGet(this, -r) != 0);
                }
            }
        }
    }

    public final /* bridge */ /* synthetic */ void call(Object x0) {
        Subscriber subscriber = (Subscriber) x0;
        Iterator it = this.is.iterator();
        if (it.hasNext() || subscriber.isUnsubscribed()) {
            subscriber.setProducer(new IterableProducer(subscriber, it));
        } else {
            subscriber.onCompleted();
        }
    }

    public OnSubscribeFromIterable(Iterable<? extends T> iterable) {
        if (iterable == null) {
            throw new NullPointerException("iterable must not be null");
        }
        this.is = iterable;
    }
}
