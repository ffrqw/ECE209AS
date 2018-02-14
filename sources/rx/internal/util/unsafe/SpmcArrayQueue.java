package rx.internal.util.unsafe;

public final class SpmcArrayQueue<E> extends SpmcArrayQueueL3Pad<E> {
    public SpmcArrayQueue(int capacity) {
        super(capacity);
    }

    public final boolean offer(E e) {
        if (e == null) {
            throw new NullPointerException("Null is not a valid element");
        }
        E[] lb = this.buffer;
        long lMask = this.mask;
        long currProducerIndex = lvProducerIndex();
        long offset = calcElementOffset(currProducerIndex);
        if (ConcurrentCircularArrayQueue.lvElement(lb, offset) == null) {
            ConcurrentCircularArrayQueue.spElement(lb, offset, e);
            soTail(1 + currProducerIndex);
        } else if (currProducerIndex - lvConsumerIndex() > lMask) {
            return false;
        } else {
            do {
            } while (ConcurrentCircularArrayQueue.lvElement(lb, offset) != null);
        }
        ConcurrentCircularArrayQueue.spElement(lb, offset, e);
        soTail(1 + currProducerIndex);
        return true;
    }

    public final E poll() {
        long currentConsumerIndex;
        long currProducerIndexCache = lvProducerIndexCache();
        do {
            currentConsumerIndex = lvConsumerIndex();
            if (currentConsumerIndex >= currProducerIndexCache) {
                long currProducerIndex = lvProducerIndex();
                if (currentConsumerIndex >= currProducerIndex) {
                    return null;
                }
                svProducerIndexCache(currProducerIndex);
            }
        } while (!casHead(currentConsumerIndex, 1 + currentConsumerIndex));
        long offset = calcElementOffset(currentConsumerIndex);
        E[] lb = this.buffer;
        E e = ConcurrentCircularArrayQueue.lpElement(lb, offset);
        ConcurrentCircularArrayQueue.soElement(lb, offset, null);
        return e;
    }

    public final E peek() {
        E e;
        long currProducerIndexCache = lvProducerIndexCache();
        do {
            long currentConsumerIndex = lvConsumerIndex();
            if (currentConsumerIndex >= currProducerIndexCache) {
                long currProducerIndex = lvProducerIndex();
                if (currentConsumerIndex >= currProducerIndex) {
                    return null;
                }
                svProducerIndexCache(currProducerIndex);
            }
            e = lvElement(calcElementOffset(currentConsumerIndex));
        } while (e == null);
        return e;
    }

    public final int size() {
        long currentProducerIndex;
        long after = lvConsumerIndex();
        long before;
        do {
            before = after;
            currentProducerIndex = lvProducerIndex();
            after = lvConsumerIndex();
        } while (before != after);
        return (int) (currentProducerIndex - after);
    }

    public final boolean isEmpty() {
        return lvConsumerIndex() == lvProducerIndex();
    }
}
