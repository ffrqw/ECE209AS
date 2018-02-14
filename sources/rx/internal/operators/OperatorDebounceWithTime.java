package rx.internal.operators;

import java.util.concurrent.TimeUnit;
import rx.Observable.Operator;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.observers.SerializedSubscriber;
import rx.subscriptions.SerialSubscription;

public final class OperatorDebounceWithTime<T> implements Operator<T, T> {
    final Scheduler scheduler;
    final long timeout;
    final TimeUnit unit;

    static final class DebounceState<T> {
        boolean emitting;
        boolean hasValue;
        int index;
        boolean terminate;
        T value;

        DebounceState() {
        }

        public final synchronized int next(T value) {
            int i;
            this.value = value;
            this.hasValue = true;
            i = this.index + 1;
            this.index = i;
            return i;
        }

        public final synchronized void clear() {
            this.index++;
            this.value = null;
            this.hasValue = false;
        }
    }

    public final /* bridge */ /* synthetic */ Object call(Object x0) {
        Subscriber subscriber = (Subscriber) x0;
        final Object createWorker = this.scheduler.createWorker();
        final SerializedSubscriber serializedSubscriber = new SerializedSubscriber(subscriber);
        final Object serialSubscription = new SerialSubscription();
        serializedSubscriber.add(createWorker);
        serializedSubscriber.add(serialSubscription);
        return new Subscriber<T>(subscriber) {
            final Subscriber<?> self = this;
            final DebounceState<T> state = new DebounceState();

            public final void onStart() {
                request(Long.MAX_VALUE);
            }

            public final void onNext(T t) {
                final int index = this.state.next(t);
                serialSubscription.set(createWorker.schedule(new Action0() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public final void call() {
                        /*
                        r5 = this;
                        r0 = rx.internal.operators.OperatorDebounceWithTime.AnonymousClass1.this;
                        r1 = r0.state;
                        r0 = r0;
                        r2 = rx.internal.operators.OperatorDebounceWithTime.AnonymousClass1.this;
                        r2 = r5;
                        r3 = rx.internal.operators.OperatorDebounceWithTime.AnonymousClass1.this;
                        r3 = r3.self;
                        monitor-enter(r1);
                        r4 = r1.emitting;	 Catch:{ all -> 0x0039 }
                        if (r4 != 0) goto L_0x001b;
                    L_0x0013:
                        r4 = r1.hasValue;	 Catch:{ all -> 0x0039 }
                        if (r4 == 0) goto L_0x001b;
                    L_0x0017:
                        r4 = r1.index;	 Catch:{ all -> 0x0039 }
                        if (r0 == r4) goto L_0x001d;
                    L_0x001b:
                        monitor-exit(r1);	 Catch:{ all -> 0x0039 }
                    L_0x001c:
                        return;
                    L_0x001d:
                        r0 = r1.value;	 Catch:{ all -> 0x0039 }
                        r4 = 0;
                        r1.value = r4;	 Catch:{ all -> 0x0039 }
                        r4 = 0;
                        r1.hasValue = r4;	 Catch:{ all -> 0x0039 }
                        r4 = 1;
                        r1.emitting = r4;	 Catch:{ all -> 0x0039 }
                        monitor-exit(r1);	 Catch:{ all -> 0x0039 }
                        r2.onNext(r0);	 Catch:{ Throwable -> 0x003c }
                        monitor-enter(r1);
                        r0 = r1.terminate;	 Catch:{ all -> 0x0036 }
                        if (r0 != 0) goto L_0x0041;
                    L_0x0031:
                        r0 = 0;
                        r1.emitting = r0;	 Catch:{ all -> 0x0036 }
                        monitor-exit(r1);	 Catch:{ all -> 0x0036 }
                        goto L_0x001c;
                    L_0x0036:
                        r0 = move-exception;
                        monitor-exit(r1);	 Catch:{ all -> 0x0036 }
                        throw r0;
                    L_0x0039:
                        r0 = move-exception;
                        monitor-exit(r1);	 Catch:{ all -> 0x0039 }
                        throw r0;
                    L_0x003c:
                        r0 = move-exception;
                        r3.onError(r0);
                        goto L_0x001c;
                    L_0x0041:
                        monitor-exit(r1);	 Catch:{ all -> 0x0036 }
                        r2.onCompleted();
                        goto L_0x001c;
                        */
                        throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.OperatorDebounceWithTime.1.1.call():void");
                    }
                }, OperatorDebounceWithTime.this.timeout, OperatorDebounceWithTime.this.unit));
            }

            public final void onError(Throwable e) {
                serializedSubscriber.onError(e);
                unsubscribe();
                this.state.clear();
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public final void onCompleted() {
                /*
                r5 = this;
                r1 = r5.state;
                r0 = r5;
                monitor-enter(r1);
                r2 = r1.emitting;	 Catch:{ all -> 0x0025 }
                if (r2 == 0) goto L_0x000e;
            L_0x0009:
                r0 = 1;
                r1.terminate = r0;	 Catch:{ all -> 0x0025 }
                monitor-exit(r1);	 Catch:{ all -> 0x0025 }
            L_0x000d:
                return;
            L_0x000e:
                r2 = r1.value;	 Catch:{ all -> 0x0025 }
                r3 = r1.hasValue;	 Catch:{ all -> 0x0025 }
                r4 = 0;
                r1.value = r4;	 Catch:{ all -> 0x0025 }
                r4 = 0;
                r1.hasValue = r4;	 Catch:{ all -> 0x0025 }
                r4 = 1;
                r1.emitting = r4;	 Catch:{ all -> 0x0025 }
                monitor-exit(r1);	 Catch:{ all -> 0x0025 }
                if (r3 == 0) goto L_0x0021;
            L_0x001e:
                r0.onNext(r2);	 Catch:{ Throwable -> 0x0028 }
            L_0x0021:
                r0.onCompleted();
                goto L_0x000d;
            L_0x0025:
                r0 = move-exception;
                monitor-exit(r1);	 Catch:{ all -> 0x0025 }
                throw r0;
            L_0x0028:
                r0 = move-exception;
                r5.onError(r0);
                goto L_0x000d;
                */
                throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.OperatorDebounceWithTime.1.onCompleted():void");
            }
        };
    }

    public OperatorDebounceWithTime(long timeout, TimeUnit unit, Scheduler scheduler) {
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
    }
}
