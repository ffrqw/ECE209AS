package rx;

import rx.internal.util.SubscriptionList;

public abstract class Subscriber<T> implements Observer<T>, Subscription {
    private static final Long NOT_SET = Long.valueOf(Long.MIN_VALUE);
    private Producer producer;
    private long requested;
    private final Subscriber<?> subscriber;
    private final SubscriptionList subscriptions;

    protected Subscriber() {
        this(null, false);
    }

    protected Subscriber(Subscriber<?> subscriber) {
        this(subscriber, true);
    }

    protected Subscriber(Subscriber<?> subscriber, boolean shareSubscriptions) {
        this.requested = NOT_SET.longValue();
        this.subscriber = subscriber;
        SubscriptionList subscriptionList = (!shareSubscriptions || subscriber == null) ? new SubscriptionList() : subscriber.subscriptions;
        this.subscriptions = subscriptionList;
    }

    public final void add(Subscription s) {
        this.subscriptions.add(s);
    }

    public final void unsubscribe() {
        this.subscriptions.unsubscribe();
    }

    public final boolean isUnsubscribed() {
        return this.subscriptions.isUnsubscribed();
    }

    public void onStart() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected final void request(long r10) {
        /*
        r9 = this;
        r6 = 0;
        r1 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
        if (r1 >= 0) goto L_0x001b;
    L_0x0006:
        r1 = new java.lang.IllegalArgumentException;
        r2 = new java.lang.StringBuilder;
        r3 = "number requested cannot be negative: ";
        r2.<init>(r3);
        r2 = r2.append(r10);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x001b:
        monitor-enter(r9);
        r1 = r9.producer;	 Catch:{ all -> 0x0037 }
        if (r1 == 0) goto L_0x0027;
    L_0x0020:
        r0 = r9.producer;	 Catch:{ all -> 0x0037 }
        monitor-exit(r9);	 Catch:{ all -> 0x0037 }
        r0.request(r10);
    L_0x0026:
        return;
    L_0x0027:
        r2 = r9.requested;	 Catch:{ all -> 0x0037 }
        r1 = NOT_SET;	 Catch:{ all -> 0x0037 }
        r4 = r1.longValue();	 Catch:{ all -> 0x0037 }
        r1 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r1 != 0) goto L_0x003a;
    L_0x0033:
        r9.requested = r10;	 Catch:{ all -> 0x0037 }
    L_0x0035:
        monitor-exit(r9);	 Catch:{ all -> 0x0037 }
        goto L_0x0026;
    L_0x0037:
        r1 = move-exception;
        monitor-exit(r9);	 Catch:{ all -> 0x0037 }
        throw r1;
    L_0x003a:
        r2 = r9.requested;	 Catch:{ all -> 0x0037 }
        r2 = r2 + r10;
        r1 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r1 >= 0) goto L_0x0049;
    L_0x0041:
        r2 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r9.requested = r2;	 Catch:{ all -> 0x0037 }
        goto L_0x0035;
    L_0x0049:
        r9.requested = r2;	 Catch:{ all -> 0x0037 }
        goto L_0x0035;
        */
        throw new UnsupportedOperationException("Method not decompiled: rx.Subscriber.request(long):void");
    }

    public void setProducer(Producer p) {
        boolean passToSubscriber = false;
        synchronized (this) {
            long toRequest = this.requested;
            this.producer = p;
            if (this.subscriber != null && toRequest == NOT_SET.longValue()) {
                passToSubscriber = true;
            }
        }
        if (passToSubscriber) {
            this.subscriber.setProducer(this.producer);
        } else if (toRequest == NOT_SET.longValue()) {
            this.producer.request(Long.MAX_VALUE);
        } else {
            this.producer.request(toRequest);
        }
    }
}
