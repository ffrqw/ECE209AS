package rx.internal.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public final class SynchronizedQueue<T> implements Queue<T> {
    private final LinkedList<T> list;
    private final int size;

    public SynchronizedQueue() {
        this.list = new LinkedList();
        this.size = -1;
    }

    public SynchronizedQueue(int size) {
        this.list = new LinkedList();
        this.size = size;
    }

    public final synchronized boolean isEmpty() {
        return this.list.isEmpty();
    }

    public final synchronized boolean contains(Object o) {
        return this.list.contains(o);
    }

    public final synchronized Iterator<T> iterator() {
        return this.list.iterator();
    }

    public final synchronized int size() {
        return this.list.size();
    }

    public final synchronized boolean add(T e) {
        return this.list.add(e);
    }

    public final synchronized boolean remove(Object o) {
        return this.list.remove(o);
    }

    public final synchronized boolean containsAll(Collection<?> c) {
        return this.list.containsAll(c);
    }

    public final synchronized boolean addAll(Collection<? extends T> c) {
        return this.list.addAll(c);
    }

    public final synchronized boolean removeAll(Collection<?> c) {
        return this.list.removeAll(c);
    }

    public final synchronized boolean retainAll(Collection<?> c) {
        return this.list.retainAll(c);
    }

    public final synchronized void clear() {
        this.list.clear();
    }

    public final synchronized String toString() {
        return this.list.toString();
    }

    public final synchronized boolean equals(Object o) {
        return this.list.equals(o);
    }

    public final synchronized int hashCode() {
        return this.list.hashCode();
    }

    public final synchronized T peek() {
        return this.list.peek();
    }

    public final synchronized T element() {
        return this.list.element();
    }

    public final synchronized T poll() {
        return this.list.poll();
    }

    public final synchronized T remove() {
        return this.list.remove();
    }

    public final synchronized boolean offer(T e) {
        boolean offer;
        if (this.size < 0 || this.list.size() + 1 <= this.size) {
            offer = this.list.offer(e);
        } else {
            offer = false;
        }
        return offer;
    }

    public final synchronized Object clone() {
        SynchronizedQueue<T> q;
        q = new SynchronizedQueue(this.size);
        q.addAll(this.list);
        return q;
    }

    public final synchronized Object[] toArray() {
        return this.list.toArray();
    }

    public final synchronized <R> R[] toArray(R[] a) {
        return this.list.toArray(a);
    }
}
