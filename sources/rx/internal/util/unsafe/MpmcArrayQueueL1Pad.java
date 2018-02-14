package rx.internal.util.unsafe;

/* compiled from: MpmcArrayQueue */
abstract class MpmcArrayQueueL1Pad<E> extends ConcurrentSequencedCircularArrayQueue<E> {
    public MpmcArrayQueueL1Pad(int capacity) {
        super(capacity);
    }
}
