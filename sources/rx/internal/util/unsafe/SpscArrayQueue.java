package rx.internal.util.unsafe;

public final class SpscArrayQueue<E> extends SpscArrayQueueL3Pad<E> {
    public SpscArrayQueue(int capacity) {
        super(capacity);
    }

    public final boolean offer(E e) {
        E[] lElementBuffer = this.buffer;
        long index = this.producerIndex;
        long offset = calcElementOffset(index);
        if (ConcurrentCircularArrayQueue.lvElement(lElementBuffer, offset) != null) {
            return false;
        }
        UnsafeAccess.UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, index + 1);
        ConcurrentCircularArrayQueue.soElement(lElementBuffer, offset, e);
        return true;
    }

    public final E poll() {
        long index = this.consumerIndex;
        long offset = calcElementOffset(index);
        E[] lElementBuffer = this.buffer;
        E e = ConcurrentCircularArrayQueue.lvElement(lElementBuffer, offset);
        if (e == null) {
            return null;
        }
        UnsafeAccess.UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, index + 1);
        ConcurrentCircularArrayQueue.soElement(lElementBuffer, offset, null);
        return e;
    }

    public final E peek() {
        return lvElement(calcElementOffset(this.consumerIndex));
    }

    public final int size() {
        long currentProducerIndex;
        long after = lvConsumerIndex();
        long before;
        do {
            before = after;
            currentProducerIndex = UnsafeAccess.UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
            after = lvConsumerIndex();
        } while (before != after);
        return (int) (currentProducerIndex - after);
    }

    private long lvConsumerIndex() {
        return UnsafeAccess.UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
    }
}
