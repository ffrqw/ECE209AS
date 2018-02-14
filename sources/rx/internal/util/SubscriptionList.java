package rx.internal.util;

import java.util.Arrays;
import java.util.LinkedList;
import rx.Subscription;

public final class SubscriptionList implements Subscription {
    private LinkedList<Subscription> subscriptions;
    private volatile boolean unsubscribed;

    public SubscriptionList(Subscription... subscriptions) {
        this.subscriptions = new LinkedList(Arrays.asList(subscriptions));
    }

    public SubscriptionList(Subscription s) {
        this.subscriptions = new LinkedList();
        this.subscriptions.add(s);
    }

    public final boolean isUnsubscribed() {
        return this.unsubscribed;
    }

    public final void add(Subscription s) {
        if (!s.isUnsubscribed()) {
            if (!this.unsubscribed) {
                synchronized (this) {
                    if (!this.unsubscribed) {
                        LinkedList<Subscription> subs = this.subscriptions;
                        if (subs == null) {
                            subs = new LinkedList();
                            this.subscriptions = subs;
                        }
                        subs.add(s);
                        return;
                    }
                }
            }
            s.unsubscribe();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void remove(rx.Subscription r4) {
        /*
        r3 = this;
        r2 = r3.unsubscribed;
        if (r2 != 0) goto L_0x000e;
    L_0x0004:
        monitor-enter(r3);
        r0 = r3.subscriptions;	 Catch:{ all -> 0x001a }
        r2 = r3.unsubscribed;	 Catch:{ all -> 0x001a }
        if (r2 != 0) goto L_0x000d;
    L_0x000b:
        if (r0 != 0) goto L_0x000f;
    L_0x000d:
        monitor-exit(r3);	 Catch:{ all -> 0x001a }
    L_0x000e:
        return;
    L_0x000f:
        r1 = r0.remove(r4);	 Catch:{ all -> 0x001a }
        monitor-exit(r3);	 Catch:{ all -> 0x001a }
        if (r1 == 0) goto L_0x000e;
    L_0x0016:
        r4.unsubscribe();
        goto L_0x000e;
    L_0x001a:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x001a }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: rx.internal.util.SubscriptionList.remove(rx.Subscription):void");
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
        throw new UnsupportedOperationException("Method not decompiled: rx.internal.util.SubscriptionList.unsubscribe():void");
    }
}
