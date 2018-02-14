package rx.subscriptions;

import java.util.HashSet;
import java.util.Set;
import rx.Subscription;

public final class CompositeSubscription implements Subscription {
    private Set<Subscription> subscriptions;
    private volatile boolean unsubscribed;

    public final boolean isUnsubscribed() {
        return this.unsubscribed;
    }

    public final void add(Subscription s) {
        if (!s.isUnsubscribed()) {
            if (!this.unsubscribed) {
                synchronized (this) {
                    if (!this.unsubscribed) {
                        if (this.subscriptions == null) {
                            this.subscriptions = new HashSet(4);
                        }
                        this.subscriptions.add(s);
                        return;
                    }
                }
            }
            s.unsubscribe();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void remove(rx.Subscription r3) {
        /*
        r2 = this;
        r1 = r2.unsubscribed;
        if (r1 != 0) goto L_0x000e;
    L_0x0004:
        monitor-enter(r2);
        r1 = r2.unsubscribed;	 Catch:{ all -> 0x001c }
        if (r1 != 0) goto L_0x000d;
    L_0x0009:
        r1 = r2.subscriptions;	 Catch:{ all -> 0x001c }
        if (r1 != 0) goto L_0x000f;
    L_0x000d:
        monitor-exit(r2);	 Catch:{ all -> 0x001c }
    L_0x000e:
        return;
    L_0x000f:
        r1 = r2.subscriptions;	 Catch:{ all -> 0x001c }
        r0 = r1.remove(r3);	 Catch:{ all -> 0x001c }
        monitor-exit(r2);	 Catch:{ all -> 0x001c }
        if (r0 == 0) goto L_0x000e;
    L_0x0018:
        r3.unsubscribe();
        goto L_0x000e;
    L_0x001c:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x001c }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: rx.subscriptions.CompositeSubscription.remove(rx.Subscription):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void unsubscribe() {
        /*
        r5 = this;
        r2 = 0;
        r1 = r5.unsubscribed;
        if (r1 != 0) goto L_0x000b;
    L_0x0005:
        monitor-enter(r5);
        r1 = r5.unsubscribed;	 Catch:{ all -> 0x0038 }
        if (r1 == 0) goto L_0x000c;
    L_0x000a:
        monitor-exit(r5);	 Catch:{ all -> 0x0038 }
    L_0x000b:
        return;
    L_0x000c:
        r1 = 1;
        r5.unsubscribed = r1;	 Catch:{ all -> 0x0038 }
        r0 = r5.subscriptions;	 Catch:{ all -> 0x0038 }
        r1 = 0;
        r5.subscriptions = r1;	 Catch:{ all -> 0x0038 }
        monitor-exit(r5);	 Catch:{ all -> 0x0038 }
        if (r0 == 0) goto L_0x000b;
    L_0x0017:
        r3 = r0.iterator();
    L_0x001b:
        r1 = r3.hasNext();
        if (r1 == 0) goto L_0x003b;
    L_0x0021:
        r1 = r3.next();
        r1 = (rx.Subscription) r1;
        r1.unsubscribe();	 Catch:{ Throwable -> 0x002b }
        goto L_0x001b;
    L_0x002b:
        r4 = move-exception;
        if (r2 != 0) goto L_0x003f;
    L_0x002e:
        r1 = new java.util.ArrayList;
        r1.<init>();
    L_0x0033:
        r1.add(r4);
        r2 = r1;
        goto L_0x001b;
    L_0x0038:
        r1 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0038 }
        throw r1;
    L_0x003b:
        rx.exceptions.Exceptions.throwIfAny(r2);
        goto L_0x000b;
    L_0x003f:
        r1 = r2;
        goto L_0x0033;
        */
        throw new UnsupportedOperationException("Method not decompiled: rx.subscriptions.CompositeSubscription.unsubscribe():void");
    }
}
