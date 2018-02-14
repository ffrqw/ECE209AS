package rx.subjects;

import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.internal.operators.NotificationLite;
import rx.subscriptions.Subscriptions;

final class SubjectSubscriptionManager<T> implements OnSubscribe<T> {
    static final AtomicReferenceFieldUpdater<SubjectSubscriptionManager, Object> LATEST_UPDATER = AtomicReferenceFieldUpdater.newUpdater(SubjectSubscriptionManager.class, Object.class, "latest");
    static final AtomicReferenceFieldUpdater<SubjectSubscriptionManager, State> STATE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(SubjectSubscriptionManager.class, State.class, "state");
    boolean active = true;
    volatile Object latest;
    public final NotificationLite<T> nl = NotificationLite.instance();
    Action1<SubjectObserver<T>> onAdded = Actions.empty();
    Action1<SubjectObserver<T>> onStart = Actions.empty();
    Action1<SubjectObserver<T>> onTerminated = Actions.empty();
    volatile State<T> state = State.EMPTY;

    protected static final class State<T> {
        static final State EMPTY = new State(false, NO_OBSERVERS);
        static final SubjectObserver[] NO_OBSERVERS = new SubjectObserver[0];
        static final State TERMINATED = new State(true, NO_OBSERVERS);
        final SubjectObserver[] observers;
        final boolean terminated;

        public State(boolean terminated, SubjectObserver[] observers) {
            this.terminated = terminated;
            this.observers = observers;
        }
    }

    protected static final class SubjectObserver<T> implements Observer<T> {
        final Observer<? super T> actual;
        boolean emitting;
        boolean fastPath;
        boolean first = true;
        List<Object> queue;

        public SubjectObserver(Observer<? super T> actual) {
            this.actual = actual;
        }

        public final void onNext(T t) {
            this.actual.onNext(t);
        }

        public final void onError(Throwable e) {
            this.actual.onError(e);
        }

        public final void onCompleted() {
            this.actual.onCompleted();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected final void emitNext(java.lang.Object r2, rx.internal.operators.NotificationLite<T> r3) {
            /*
            r1 = this;
            r0 = r1.fastPath;
            if (r0 != 0) goto L_0x0022;
        L_0x0004:
            monitor-enter(r1);
            r0 = 0;
            r1.first = r0;	 Catch:{ all -> 0x0028 }
            r0 = r1.emitting;	 Catch:{ all -> 0x0028 }
            if (r0 == 0) goto L_0x001e;
        L_0x000c:
            r0 = r1.queue;	 Catch:{ all -> 0x0028 }
            if (r0 != 0) goto L_0x0017;
        L_0x0010:
            r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0028 }
            r0.<init>();	 Catch:{ all -> 0x0028 }
            r1.queue = r0;	 Catch:{ all -> 0x0028 }
        L_0x0017:
            r0 = r1.queue;	 Catch:{ all -> 0x0028 }
            r0.add(r2);	 Catch:{ all -> 0x0028 }
            monitor-exit(r1);	 Catch:{ all -> 0x0028 }
        L_0x001d:
            return;
        L_0x001e:
            monitor-exit(r1);	 Catch:{ all -> 0x0028 }
            r0 = 1;
            r1.fastPath = r0;
        L_0x0022:
            r0 = r1.actual;
            rx.internal.operators.NotificationLite.accept(r0, r2);
            goto L_0x001d;
        L_0x0028:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0028 }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.subjects.SubjectSubscriptionManager.SubjectObserver.emitNext(java.lang.Object, rx.internal.operators.NotificationLite):void");
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected final void emitFirst(java.lang.Object r6, rx.internal.operators.NotificationLite<T> r7) {
            /*
            r5 = this;
            r0 = 0;
            r1 = 1;
            r2 = 0;
            monitor-enter(r5);
            r3 = r5.first;	 Catch:{ all -> 0x003a }
            if (r3 == 0) goto L_0x000c;
        L_0x0008:
            r3 = r5.emitting;	 Catch:{ all -> 0x003a }
            if (r3 == 0) goto L_0x000e;
        L_0x000c:
            monitor-exit(r5);	 Catch:{ all -> 0x003a }
        L_0x000d:
            return;
        L_0x000e:
            r3 = 0;
            r5.first = r3;	 Catch:{ all -> 0x003a }
            if (r6 == 0) goto L_0x0038;
        L_0x0013:
            r3 = r1;
        L_0x0014:
            r5.emitting = r3;	 Catch:{ all -> 0x003a }
            monitor-exit(r5);	 Catch:{ all -> 0x003a }
            if (r6 == 0) goto L_0x000d;
        L_0x0019:
            r3 = r0;
            r0 = r1;
        L_0x001b:
            if (r3 == 0) goto L_0x003d;
        L_0x001d:
            r3 = r3.iterator();	 Catch:{ all -> 0x002f }
        L_0x0021:
            r4 = r3.hasNext();	 Catch:{ all -> 0x002f }
            if (r4 == 0) goto L_0x003d;
        L_0x0027:
            r4 = r3.next();	 Catch:{ all -> 0x002f }
            r5.accept(r4, r7);	 Catch:{ all -> 0x002f }
            goto L_0x0021;
        L_0x002f:
            r0 = move-exception;
        L_0x0030:
            if (r2 != 0) goto L_0x0037;
        L_0x0032:
            monitor-enter(r5);
            r1 = 0;
            r5.emitting = r1;	 Catch:{ all -> 0x005b }
            monitor-exit(r5);	 Catch:{ all -> 0x005b }
        L_0x0037:
            throw r0;
        L_0x0038:
            r3 = r2;
            goto L_0x0014;
        L_0x003a:
            r0 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x003a }
            throw r0;
        L_0x003d:
            if (r0 == 0) goto L_0x0043;
        L_0x003f:
            r5.accept(r6, r7);	 Catch:{ all -> 0x002f }
            r0 = r2;
        L_0x0043:
            monitor-enter(r5);	 Catch:{ all -> 0x002f }
            r3 = r5.queue;	 Catch:{ all -> 0x0058 }
            r4 = 0;
            r5.queue = r4;	 Catch:{ all -> 0x0058 }
            if (r3 != 0) goto L_0x0056;
        L_0x004b:
            r0 = 0;
            r5.emitting = r0;	 Catch:{ all -> 0x0058 }
            monitor-exit(r5);	 Catch:{ all -> 0x0050 }
            goto L_0x000d;
        L_0x0050:
            r0 = move-exception;
        L_0x0051:
            monitor-exit(r5);	 Catch:{ all -> 0x0050 }
            throw r0;	 Catch:{ all -> 0x0053 }
        L_0x0053:
            r0 = move-exception;
            r2 = r1;
            goto L_0x0030;
        L_0x0056:
            monitor-exit(r5);	 Catch:{ all -> 0x0058 }
            goto L_0x001b;
        L_0x0058:
            r0 = move-exception;
            r1 = r2;
            goto L_0x0051;
        L_0x005b:
            r0 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x005b }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.subjects.SubjectSubscriptionManager.SubjectObserver.emitFirst(java.lang.Object, rx.internal.operators.NotificationLite):void");
        }

        private void accept(Object n, NotificationLite<T> notificationLite) {
            if (n != null) {
                NotificationLite.accept(this.actual, n);
            }
        }
    }

    public final /* bridge */ /* synthetic */ void call(Object x0) {
        int i = 0;
        Subscriber subscriber = (Subscriber) x0;
        final SubjectObserver subjectObserver = new SubjectObserver(subscriber);
        subscriber.add(Subscriptions.create(new Action0() {
            public final void call() {
                SubjectSubscriptionManager.this.remove(subjectObserver);
            }
        }));
        this.onStart.call(subjectObserver);
        if (!subscriber.isUnsubscribed()) {
            State state;
            Object obj;
            do {
                state = this.state;
                if (state.terminated) {
                    this.onTerminated.call(subjectObserver);
                    break;
                }
                int length = state.observers.length;
                obj = new SubjectObserver[(length + 1)];
                System.arraycopy(state.observers, 0, obj, 0, length);
                obj[length] = subjectObserver;
            } while (!STATE_UPDATER.compareAndSet(this, state, new State(state.terminated, obj)));
            this.onAdded.call(subjectObserver);
            i = 1;
            if (i != 0 && subscriber.isUnsubscribed()) {
                remove(subjectObserver);
            }
        }
    }

    SubjectSubscriptionManager() {
    }

    final void remove(SubjectObserver<T> o) {
        State oldState;
        State newState;
        do {
            oldState = this.state;
            if (!oldState.terminated) {
                SubjectObserver[] subjectObserverArr = oldState.observers;
                int length = subjectObserverArr.length;
                if (length == 1 && subjectObserverArr[0] == o) {
                    newState = State.EMPTY;
                } else if (length == 0) {
                    newState = oldState;
                } else {
                    Object obj = new SubjectObserver[(length - 1)];
                    int i = 0;
                    int i2 = 0;
                    while (i < length) {
                        int i3;
                        SubjectObserver<T> subjectObserver = subjectObserverArr[i];
                        if (subjectObserver == o) {
                            i3 = i2;
                        } else if (i2 == length - 1) {
                            newState = oldState;
                            break;
                        } else {
                            i3 = i2 + 1;
                            obj[i2] = subjectObserver;
                        }
                        i++;
                        i2 = i3;
                    }
                    if (i2 == 0) {
                        newState = State.EMPTY;
                    } else {
                        SubjectObserver[] subjectObserverArr2;
                        if (i2 < length - 1) {
                            subjectObserverArr2 = new SubjectObserver[i2];
                            System.arraycopy(obj, 0, subjectObserverArr2, 0, i2);
                        } else {
                            Object obj2 = obj;
                        }
                        newState = new State(oldState.terminated, subjectObserverArr2);
                    }
                }
                if (newState == oldState) {
                    return;
                }
            } else {
                return;
            }
        } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));
    }

    final SubjectObserver<T>[] terminate(Object n) {
        this.latest = n;
        this.active = false;
        if (this.state.terminated) {
            return State.NO_OBSERVERS;
        }
        return ((State) STATE_UPDATER.getAndSet(this, State.TERMINATED)).observers;
    }
}
