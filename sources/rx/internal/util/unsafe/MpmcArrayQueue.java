package rx.internal.util.unsafe;

public final class MpmcArrayQueue<E> extends MpmcArrayQueueConsumerField<E> {
    public MpmcArrayQueue(int capacity) {
        super(Math.max(2, capacity));
    }

    public final boolean offer(E e) {
        if (e == null) {
            throw new NullPointerException("Null is not a valid element");
        }
        long capacity = this.mask + 1;
        long[] lSequenceBuffer = this.sequenceBuffer;
        long cIndex = Long.MAX_VALUE;
        while (true) {
            long currentProducerIndex = lvProducerIndex();
            long seqOffset = calcSequenceOffset(currentProducerIndex);
            long delta = ConcurrentSequencedCircularArrayQueue.lvSequence(lSequenceBuffer, seqOffset) - currentProducerIndex;
            if (delta == 0) {
                if (casProducerIndex(currentProducerIndex, 1 + currentProducerIndex)) {
                    spElement(calcElementOffset(currentProducerIndex), e);
                    ConcurrentSequencedCircularArrayQueue.soSequence(lSequenceBuffer, seqOffset, 1 + currentProducerIndex);
                    return true;
                }
            } else if (delta < 0 && currentProducerIndex - capacity <= cIndex) {
                long j = currentProducerIndex - capacity;
                cIndex = lvConsumerIndex();
                if (j <= cIndex) {
                    return false;
                }
            }
        }
    }

    public final E poll() {
        long[] lSequenceBuffer = this.sequenceBuffer;
        long pIndex = -1;
        while (true) {
            long currentConsumerIndex = lvConsumerIndex();
            long seqOffset = calcSequenceOffset(currentConsumerIndex);
            long delta = ConcurrentSequencedCircularArrayQueue.lvSequence(lSequenceBuffer, seqOffset) - (1 + currentConsumerIndex);
            if (delta == 0) {
                if (casConsumerIndex(currentConsumerIndex, 1 + currentConsumerIndex)) {
                    long offset = calcElementOffset(currentConsumerIndex);
                    E e = lpElement(offset);
                    spElement(offset, null);
                    ConcurrentSequencedCircularArrayQueue.soSequence(lSequenceBuffer, seqOffset, (this.mask + currentConsumerIndex) + 1);
                    return e;
                }
            } else if (delta < 0 && currentConsumerIndex >= pIndex) {
                pIndex = lvProducerIndex();
                if (currentConsumerIndex == pIndex) {
                    return null;
                }
            }
        }
    }

    public final E peek() {
        E e;
        long currConsumerIndex;
        do {
            currConsumerIndex = lvConsumerIndex();
            e = lpElement(calcElementOffset(currConsumerIndex));
            if (e != null) {
                break;
            }
        } while (currConsumerIndex != lvProducerIndex());
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
