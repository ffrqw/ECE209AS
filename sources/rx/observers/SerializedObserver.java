package rx.observers;

import rx.Observer;
import rx.exceptions.Exceptions;

public final class SerializedObserver<T> implements Observer<T> {
    private static final Object COMPLETE_SENTINEL = new Object();
    private static final Object NULL_SENTINEL = new Object();
    private final Observer<? super T> actual;
    private boolean emitting = false;
    private FastList queue;
    private boolean terminated = false;

    private static final class ErrorSentinel {
        final Throwable e;

        ErrorSentinel(Throwable e) {
            this.e = e;
        }
    }

    static final class FastList {
        Object[] array;
        int size;

        FastList() {
        }

        public final void add(Object o) {
            int s = this.size;
            Object[] a = this.array;
            if (a == null) {
                a = new Object[16];
                this.array = a;
            } else if (s == a.length) {
                Object[] array2 = new Object[((s >> 2) + s)];
                System.arraycopy(a, 0, array2, 0, s);
                a = array2;
                this.array = a;
            }
            a[s] = o;
            this.size = s + 1;
        }
    }

    public SerializedObserver(Observer<? super T> s) {
        this.actual = s;
    }

    public final void onCompleted() {
        synchronized (this) {
            if (this.terminated) {
                return;
            }
            this.terminated = true;
            if (this.emitting) {
                if (this.queue == null) {
                    this.queue = new FastList();
                }
                this.queue.add(COMPLETE_SENTINEL);
                return;
            }
            this.emitting = true;
            FastList list = this.queue;
            this.queue = null;
            drainQueue(list);
            this.actual.onCompleted();
        }
    }

    public final void onError(Throwable e) {
        Exceptions.throwIfFatal(e);
        synchronized (this) {
            if (this.terminated) {
            } else if (this.emitting) {
                if (this.queue == null) {
                    this.queue = new FastList();
                }
                this.queue.add(new ErrorSentinel(e));
            } else {
                this.emitting = true;
                FastList list = this.queue;
                this.queue = null;
                drainQueue(list);
                this.actual.onError(e);
                synchronized (this) {
                    this.emitting = false;
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onNext(T r6) {
        /*
        r5 = this;
        monitor-enter(r5);
        r3 = r5.terminated;	 Catch:{ all -> 0x001f }
        if (r3 == 0) goto L_0x0007;
    L_0x0005:
        monitor-exit(r5);	 Catch:{ all -> 0x001f }
    L_0x0006:
        return;
    L_0x0007:
        r3 = r5.emitting;	 Catch:{ all -> 0x001f }
        if (r3 == 0) goto L_0x0025;
    L_0x000b:
        r3 = r5.queue;	 Catch:{ all -> 0x001f }
        if (r3 != 0) goto L_0x0016;
    L_0x000f:
        r3 = new rx.observers.SerializedObserver$FastList;	 Catch:{ all -> 0x001f }
        r3.<init>();	 Catch:{ all -> 0x001f }
        r5.queue = r3;	 Catch:{ all -> 0x001f }
    L_0x0016:
        r3 = r5.queue;	 Catch:{ all -> 0x001f }
        if (r6 == 0) goto L_0x0022;
    L_0x001a:
        r3.add(r6);	 Catch:{ all -> 0x001f }
        monitor-exit(r5);	 Catch:{ all -> 0x001f }
        goto L_0x0006;
    L_0x001f:
        r3 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x001f }
        throw r3;
    L_0x0022:
        r6 = NULL_SENTINEL;	 Catch:{ all -> 0x001f }
        goto L_0x001a;
    L_0x0025:
        r3 = 1;
        r5.emitting = r3;	 Catch:{ all -> 0x001f }
        r1 = r5.queue;	 Catch:{ all -> 0x001f }
        r3 = 0;
        r5.queue = r3;	 Catch:{ all -> 0x001f }
        monitor-exit(r5);	 Catch:{ all -> 0x001f }
        r2 = 0;
        r0 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x0032:
        r5.drainQueue(r1);	 Catch:{ all -> 0x0054 }
        r3 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r0 != r3) goto L_0x003f;
    L_0x003a:
        r3 = r5.actual;	 Catch:{ all -> 0x0054 }
        r3.onNext(r6);	 Catch:{ all -> 0x0054 }
    L_0x003f:
        r0 = r0 + -1;
        if (r0 <= 0) goto L_0x0062;
    L_0x0043:
        monitor-enter(r5);	 Catch:{ all -> 0x0054 }
        r1 = r5.queue;	 Catch:{ all -> 0x0051 }
        r3 = 0;
        r5.queue = r3;	 Catch:{ all -> 0x0051 }
        if (r1 != 0) goto L_0x0061;
    L_0x004b:
        r3 = 0;
        r5.emitting = r3;	 Catch:{ all -> 0x0051 }
        r2 = 1;
        monitor-exit(r5);	 Catch:{ all -> 0x0051 }
        goto L_0x0006;
    L_0x0051:
        r3 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0051 }
        throw r3;	 Catch:{ all -> 0x0054 }
    L_0x0054:
        r3 = move-exception;
        if (r2 != 0) goto L_0x0060;
    L_0x0057:
        monitor-enter(r5);
        r4 = r5.terminated;	 Catch:{ all -> 0x007f }
        if (r4 == 0) goto L_0x007b;
    L_0x005c:
        r4 = 0;
        r5.queue = r4;	 Catch:{ all -> 0x007f }
    L_0x005f:
        monitor-exit(r5);	 Catch:{ all -> 0x007f }
    L_0x0060:
        throw r3;
    L_0x0061:
        monitor-exit(r5);	 Catch:{ all -> 0x0051 }
    L_0x0062:
        if (r0 > 0) goto L_0x0032;
    L_0x0064:
        monitor-enter(r5);
        r3 = r5.terminated;	 Catch:{ all -> 0x0078 }
        if (r3 == 0) goto L_0x0073;
    L_0x0069:
        r1 = r5.queue;	 Catch:{ all -> 0x0078 }
        r3 = 0;
        r5.queue = r3;	 Catch:{ all -> 0x0078 }
    L_0x006e:
        monitor-exit(r5);	 Catch:{ all -> 0x0078 }
        r5.drainQueue(r1);
        goto L_0x0006;
    L_0x0073:
        r3 = 0;
        r5.emitting = r3;	 Catch:{ all -> 0x0078 }
        r1 = 0;
        goto L_0x006e;
    L_0x0078:
        r3 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0078 }
        throw r3;
    L_0x007b:
        r4 = 0;
        r5.emitting = r4;	 Catch:{ all -> 0x007f }
        goto L_0x005f;
    L_0x007f:
        r3 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x007f }
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: rx.observers.SerializedObserver.onNext(java.lang.Object):void");
    }

    private void drainQueue(FastList list) {
        if (list != null && list.size != 0) {
            Object[] arr$ = list.array;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                T v = arr$[i$];
                if (v != null) {
                    if (v == NULL_SENTINEL) {
                        this.actual.onNext(null);
                    } else if (v == COMPLETE_SENTINEL) {
                        this.actual.onCompleted();
                    } else if (v.getClass() == ErrorSentinel.class) {
                        this.actual.onError(((ErrorSentinel) v).e);
                    } else {
                        this.actual.onNext(v);
                    }
                    i$++;
                } else {
                    return;
                }
            }
        }
    }
}
