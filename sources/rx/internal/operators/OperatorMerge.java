package rx.internal.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.exceptions.MissingBackpressureException;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.ScalarSynchronousObservable;
import rx.subscriptions.CompositeSubscription;

public final class OperatorMerge<T> implements Operator<T, Observable<? extends T>> {
    final boolean delayErrors;
    final int maxConcurrent;

    private static final class HolderDelayErrors {
        static final OperatorMerge<Object> INSTANCE = new OperatorMerge(true);
    }

    private static final class HolderNoDelay {
        static final OperatorMerge<Object> INSTANCE = new OperatorMerge(false);
    }

    static final class InnerSubscriber<T> extends Subscriber<T> {
        static final int limit = (RxRingBuffer.SIZE / 4);
        volatile boolean done;
        final long id;
        int outstanding;
        final MergeSubscriber<T> parent;
        volatile RxRingBuffer queue;

        public InnerSubscriber(MergeSubscriber<T> parent, long id) {
            this.parent = parent;
            this.id = id;
        }

        public final void onStart() {
            this.outstanding = RxRingBuffer.SIZE;
            request((long) RxRingBuffer.SIZE);
        }

        public final void onNext(T t) {
            this.parent.tryEmit(this, t);
        }

        public final void onError(Throwable e) {
            this.done = true;
            this.parent.getOrCreateErrorQueue().offer(e);
            this.parent.emit();
        }

        public final void onCompleted() {
            this.done = true;
            this.parent.emit();
        }

        public final void requestMore(long n) {
            int r = this.outstanding - ((int) n);
            if (r > limit) {
                this.outstanding = r;
                return;
            }
            this.outstanding = RxRingBuffer.SIZE;
            int k = RxRingBuffer.SIZE - r;
            if (k > 0) {
                request((long) k);
            }
        }
    }

    static final class MergeProducer<T> extends AtomicLong implements Producer {
        final MergeSubscriber<T> subscriber;

        public MergeProducer(MergeSubscriber<T> subscriber) {
            this.subscriber = subscriber;
        }

        public final void request(long n) {
            if (n > 0) {
                if (get() != Long.MAX_VALUE) {
                    long j;
                    long j2;
                    do {
                        j = get();
                        j2 = j + n;
                        if (j2 < 0) {
                            j2 = Long.MAX_VALUE;
                        }
                    } while (!compareAndSet(j, j2));
                    this.subscriber.emit();
                }
            } else if (n < 0) {
                throw new IllegalArgumentException("n >= 0 required");
            }
        }
    }

    static final class MergeSubscriber<T> extends Subscriber<Observable<? extends T>> {
        static final InnerSubscriber<?>[] EMPTY = new InnerSubscriber[0];
        final Subscriber<? super T> child;
        final boolean delayErrors;
        volatile boolean done;
        boolean emitting;
        volatile ConcurrentLinkedQueue<Throwable> errors;
        final Object innerGuard = new Object();
        volatile InnerSubscriber<?>[] innerSubscribers = EMPTY;
        long lastId;
        int lastIndex;
        final int maxConcurrent;
        boolean missed;
        final NotificationLite<T> nl = NotificationLite.instance();
        MergeProducer<T> producer;
        volatile RxRingBuffer queue;
        volatile CompositeSubscription subscriptions;
        long uniqueId;

        public final /* bridge */ /* synthetic */ void onNext(Object x0) {
            Object obj = null;
            Observable observable = (Observable) x0;
            if (observable == null) {
                return;
            }
            Object obj2;
            if (observable instanceof ScalarSynchronousObservable) {
                obj2 = ((ScalarSynchronousObservable) observable).get();
                long j = this.producer.get();
                if (j != 0) {
                    synchronized (this) {
                        if (!this.emitting) {
                            this.emitting = true;
                            obj = 1;
                        }
                    }
                }
                if (obj != null) {
                    emitScalar(obj2, j);
                    return;
                } else {
                    queueScalar(obj2);
                    return;
                }
            }
            long j2 = this.uniqueId;
            this.uniqueId = 1 + j2;
            obj = new InnerSubscriber(this, j2);
            getOrCreateComposite().add(obj);
            synchronized (this.innerGuard) {
                obj2 = this.innerSubscribers;
                int length = obj2.length;
                Object obj3 = new InnerSubscriber[(length + 1)];
                System.arraycopy(obj2, 0, obj3, 0, length);
                obj3[length] = obj;
                this.innerSubscribers = obj3;
            }
            observable.unsafeSubscribe(obj);
            emit();
        }

        public MergeSubscriber(Subscriber<? super T> child, boolean delayErrors, int maxConcurrent) {
            this.child = child;
            this.delayErrors = delayErrors;
            this.maxConcurrent = maxConcurrent;
            request((long) Math.min(maxConcurrent, RxRingBuffer.SIZE));
        }

        final Queue<Throwable> getOrCreateErrorQueue() {
            ConcurrentLinkedQueue<Throwable> q = this.errors;
            if (q == null) {
                synchronized (this) {
                    try {
                        q = this.errors;
                        if (q == null) {
                            ConcurrentLinkedQueue<Throwable> q2 = new ConcurrentLinkedQueue();
                            try {
                                this.errors = q2;
                                q = q2;
                            } catch (Throwable th) {
                                Throwable th2 = th;
                                q = q2;
                                throw th2;
                            }
                        }
                    } catch (Throwable th3) {
                        th2 = th3;
                        throw th2;
                    }
                }
            }
            return q;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private rx.subscriptions.CompositeSubscription getOrCreateComposite() {
            /*
            r4 = this;
            r0 = r4.subscriptions;
            if (r0 != 0) goto L_0x0019;
        L_0x0004:
            r2 = 0;
            monitor-enter(r4);
            r0 = r4.subscriptions;	 Catch:{ all -> 0x001a }
            if (r0 != 0) goto L_0x0013;
        L_0x000a:
            r1 = new rx.subscriptions.CompositeSubscription;	 Catch:{ all -> 0x001a }
            r1.<init>();	 Catch:{ all -> 0x001a }
            r4.subscriptions = r1;	 Catch:{ all -> 0x001d }
            r2 = 1;
            r0 = r1;
        L_0x0013:
            monitor-exit(r4);	 Catch:{ all -> 0x001a }
            if (r2 == 0) goto L_0x0019;
        L_0x0016:
            r4.add(r0);
        L_0x0019:
            return r0;
        L_0x001a:
            r3 = move-exception;
        L_0x001b:
            monitor-exit(r4);	 Catch:{ all -> 0x001a }
            throw r3;
        L_0x001d:
            r3 = move-exception;
            r0 = r1;
            goto L_0x001b;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.OperatorMerge.MergeSubscriber.getOrCreateComposite():rx.subscriptions.CompositeSubscription");
        }

        private void reportError() {
            List<Throwable> list = new ArrayList(this.errors);
            if (list.size() == 1) {
                this.child.onError((Throwable) list.get(0));
            } else {
                this.child.onError(new CompositeException(list, (byte) 0));
            }
        }

        public final void onError(Throwable e) {
            getOrCreateErrorQueue().offer(e);
            this.done = true;
            emit();
        }

        public final void onCompleted() {
            this.done = true;
            emit();
        }

        final void tryEmit(InnerSubscriber<T> subscriber, T value) {
            Throwable th;
            int i = 0;
            boolean success = false;
            long r = this.producer.get();
            if (r != 0) {
                synchronized (this) {
                    if (!this.emitting) {
                        this.emitting = true;
                        success = true;
                    }
                }
            }
            if (success) {
                try {
                    this.child.onNext(value);
                } catch (Throwable th2) {
                    th = th2;
                    i = 1;
                }
                if (r != Long.MAX_VALUE) {
                    this.producer.addAndGet((long) (-1));
                }
                subscriber.requestMore(1);
                synchronized (this) {
                    if (this.missed) {
                        this.missed = false;
                        emitLoop();
                        return;
                    }
                    this.emitting = false;
                    return;
                }
            }
            RxRingBuffer rxRingBuffer = subscriber.queue;
            if (rxRingBuffer == null) {
                rxRingBuffer = RxRingBuffer.getSpscInstance();
                subscriber.add(rxRingBuffer);
                subscriber.queue = rxRingBuffer;
            }
            try {
                rxRingBuffer.onNext(NotificationLite.next(value));
                emit();
                return;
            } catch (Throwable th3) {
                subscriber.unsubscribe();
                subscriber.onError(th3);
                return;
            } catch (Throwable th32) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.unsubscribe();
                    subscriber.onError(th32);
                    return;
                }
                return;
            }
            if (i == 0) {
                synchronized (this) {
                    this.emitting = false;
                }
            }
            throw th32;
        }

        private void queueScalar(T value) {
            RxRingBuffer q = this.queue;
            if (q == null) {
                q = RxRingBuffer.getSpscInstance();
                add(q);
                this.queue = q;
            }
            try {
                q.onNext(NotificationLite.next(value));
                emit();
            } catch (MissingBackpressureException ex) {
                unsubscribe();
                onError(ex);
            } catch (IllegalStateException ex2) {
                if (!isUnsubscribed()) {
                    unsubscribe();
                    onError(ex2);
                }
            }
        }

        private void emitScalar(T value, long r) {
            boolean skipFinal = false;
            try {
                this.child.onNext(value);
            } catch (Throwable th) {
                if (!skipFinal) {
                    synchronized (this) {
                        this.emitting = false;
                    }
                }
            }
            if (r != Long.MAX_VALUE) {
                this.producer.addAndGet((long) (-1));
            }
            request(1);
            synchronized (this) {
                skipFinal = true;
                if (this.missed) {
                    this.missed = false;
                    emitLoop();
                    return;
                }
                this.emitting = false;
            }
        }

        final void emit() {
            synchronized (this) {
                if (this.emitting) {
                    this.missed = true;
                    return;
                }
                this.emitting = true;
                emitLoop();
            }
        }

        private void emitLoop() {
            boolean skipFinal = false;
            Subscriber<? super T> child = this.child;
            while (!checkTerminate()) {
                Object obj;
                RxRingBuffer svq = this.queue;
                long j = this.producer.get();
                boolean unbounded = j == Long.MAX_VALUE;
                int i = 0;
                if (svq != null) {
                    do {
                        int scalarEmission = 0;
                        obj = null;
                        while (j > 0) {
                            obj = svq.poll();
                            if (!checkTerminate()) {
                                if (obj == null) {
                                    break;
                                }
                                try {
                                    child.onNext(NotificationLite.getValue(obj));
                                } catch (Throwable th) {
                                    if (!skipFinal) {
                                        synchronized (this) {
                                            this.emitting = false;
                                        }
                                    }
                                }
                                i++;
                                scalarEmission++;
                                j--;
                            } else {
                                return;
                            }
                        }
                        if (scalarEmission > 0) {
                            if (unbounded) {
                                j = Long.MAX_VALUE;
                            } else {
                                j = this.producer.addAndGet((long) (-scalarEmission));
                            }
                        }
                        if (j == 0) {
                            break;
                        }
                    } while (obj != null);
                }
                boolean d = this.done;
                svq = this.queue;
                InnerSubscriber<?>[] inner = this.innerSubscribers;
                int n = inner.length;
                if (d && ((svq == null || svq.isEmpty()) && n == 0)) {
                    Queue<Throwable> e = this.errors;
                    if (e == null || e.isEmpty()) {
                        child.onCompleted();
                    } else {
                        reportError();
                    }
                    if (svq != null) {
                        svq.release();
                        return;
                    }
                    return;
                }
                boolean innerCompleted = false;
                if (n > 0) {
                    int j2;
                    int i2;
                    long startId = this.lastId;
                    int index = this.lastIndex;
                    if (n <= index || inner[index].id != startId) {
                        if (n <= index) {
                            index = 0;
                        }
                        j2 = index;
                        for (i2 = 0; i2 < n && inner[j2].id != startId; i2++) {
                            j2++;
                            if (j2 == n) {
                                j2 = 0;
                            }
                        }
                        index = j2;
                        this.lastIndex = j2;
                        this.lastId = inner[j2].id;
                    }
                    j2 = index;
                    i2 = 0;
                    while (i2 < n) {
                        if (!checkTerminate()) {
                            InnerSubscriber<T> is = inner[j2];
                            obj = null;
                            do {
                                int produced = 0;
                                while (j > 0) {
                                    if (!checkTerminate()) {
                                        RxRingBuffer q = is.queue;
                                        if (q == null) {
                                            break;
                                        }
                                        obj = q.poll();
                                        if (obj == null) {
                                            break;
                                        }
                                        try {
                                            child.onNext(NotificationLite.getValue(obj));
                                            j--;
                                            produced++;
                                        } catch (Throwable th2) {
                                            unsubscribe();
                                        }
                                    } else {
                                        return;
                                    }
                                }
                                if (produced > 0) {
                                    if (unbounded) {
                                        j = Long.MAX_VALUE;
                                    } else {
                                        j = this.producer.addAndGet((long) (-produced));
                                    }
                                    is.requestMore((long) produced);
                                }
                                if (j == 0) {
                                    break;
                                }
                            } while (obj != null);
                            boolean innerDone = is.done;
                            RxRingBuffer innerQueue = is.queue;
                            if (innerDone && (innerQueue == null || innerQueue.isEmpty())) {
                                RxRingBuffer rxRingBuffer = is.queue;
                                if (rxRingBuffer != null) {
                                    rxRingBuffer.release();
                                }
                                this.subscriptions.remove(is);
                                synchronized (this.innerGuard) {
                                    Object obj2 = this.innerSubscribers;
                                    int length = obj2.length;
                                    int i3 = 0;
                                    while (i3 < length) {
                                        if (is.equals(obj2[i3])) {
                                            break;
                                        }
                                        i3++;
                                    }
                                    i3 = -1;
                                    if (i3 < 0) {
                                    } else if (length == 1) {
                                        this.innerSubscribers = EMPTY;
                                    } else {
                                        Object obj3 = new InnerSubscriber[(length - 1)];
                                        System.arraycopy(obj2, 0, obj3, 0, i3);
                                        System.arraycopy(obj2, i3 + 1, obj3, i3, (length - i3) - 1);
                                        this.innerSubscribers = obj3;
                                    }
                                }
                                if (!checkTerminate()) {
                                    i++;
                                    innerCompleted = true;
                                } else {
                                    return;
                                }
                            }
                            if (j != 0) {
                                j2++;
                                if (j2 == n) {
                                    j2 = 0;
                                }
                                i2++;
                            }
                        } else {
                            return;
                        }
                    }
                    this.lastIndex = j2;
                    this.lastId = inner[j2].id;
                }
                if (i > 0) {
                    request((long) i);
                }
                if (!innerCompleted) {
                    synchronized (this) {
                        if (this.missed) {
                            this.missed = false;
                        } else {
                            this.emitting = false;
                            return;
                        }
                    }
                }
            }
        }

        private boolean checkTerminate() {
            if (this.child.isUnsubscribed()) {
                return true;
            }
            Queue<Throwable> e = this.errors;
            if (this.delayErrors || e == null || e.isEmpty()) {
                return false;
            }
            try {
                reportError();
                return true;
            } finally {
                unsubscribe();
            }
        }
    }

    public final /* bridge */ /* synthetic */ Object call(Object x0) {
        Subscriber subscriber = (Subscriber) x0;
        Subscription mergeSubscriber = new MergeSubscriber(subscriber, this.delayErrors, this.maxConcurrent);
        Producer mergeProducer = new MergeProducer(mergeSubscriber);
        mergeSubscriber.producer = mergeProducer;
        subscriber.add(mergeSubscriber);
        subscriber.setProducer(mergeProducer);
        return mergeSubscriber;
    }

    public static <T> OperatorMerge<T> instance(boolean delayErrors) {
        if (delayErrors) {
            return HolderDelayErrors.INSTANCE;
        }
        return HolderNoDelay.INSTANCE;
    }

    public static <T> OperatorMerge<T> instance(boolean delayErrors, int maxConcurrent) {
        if (maxConcurrent == ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) {
            return instance(false);
        }
        return new OperatorMerge(false, maxConcurrent);
    }

    private OperatorMerge(boolean delayErrors, int maxConcurrent) {
        this.delayErrors = delayErrors;
        this.maxConcurrent = maxConcurrent;
    }
}
