package io.fabric.sdk.android.services.concurrency;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class DependencyPriorityBlockingQueue<E extends Dependency & Task & PriorityProvider> extends PriorityBlockingQueue<E> {
    final Queue<E> blockedQueue = new LinkedList();
    private final ReentrantLock lock = new ReentrantLock();

    public final /* bridge */ /* synthetic */ Object take() throws InterruptedException {
        return get(0, null, null);
    }

    private E peek() {
        E e = null;
        try {
            e = get(1, null, null);
        } catch (InterruptedException e2) {
        }
        return e;
    }

    private E poll() {
        E e = null;
        try {
            e = get(2, null, null);
        } catch (InterruptedException e2) {
        }
        return e;
    }

    public final int size() {
        try {
            this.lock.lock();
            int size = this.blockedQueue.size() + super.size();
            return size;
        } finally {
            this.lock.unlock();
        }
    }

    public final <T> T[] toArray(T[] a) {
        try {
            this.lock.lock();
            T[] concatenate = concatenate(super.toArray(a), this.blockedQueue.toArray(a));
            return concatenate;
        } finally {
            this.lock.unlock();
        }
    }

    public final Object[] toArray() {
        try {
            this.lock.lock();
            Object[] concatenate = concatenate(super.toArray(), this.blockedQueue.toArray());
            return concatenate;
        } finally {
            this.lock.unlock();
        }
    }

    public final int drainTo(Collection<? super E> c) {
        try {
            this.lock.lock();
            int numberOfItems = super.drainTo(c) + this.blockedQueue.size();
            while (!this.blockedQueue.isEmpty()) {
                c.add(this.blockedQueue.poll());
            }
            return numberOfItems;
        } finally {
            this.lock.unlock();
        }
    }

    public final int drainTo(Collection<? super E> c, int maxElements) {
        try {
            this.lock.lock();
            int numberOfItems = super.drainTo(c, maxElements);
            while (!this.blockedQueue.isEmpty() && numberOfItems <= maxElements) {
                c.add(this.blockedQueue.poll());
                numberOfItems++;
            }
            this.lock.unlock();
            return numberOfItems;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public final boolean contains(Object o) {
        try {
            this.lock.lock();
            boolean z = super.contains(o) || this.blockedQueue.contains(o);
            this.lock.unlock();
            return z;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public final void clear() {
        try {
            this.lock.lock();
            this.blockedQueue.clear();
            super.clear();
        } finally {
            this.lock.unlock();
        }
    }

    public final boolean remove(Object o) {
        try {
            this.lock.lock();
            boolean z = super.remove(o) || this.blockedQueue.remove(o);
            this.lock.unlock();
            return z;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public final boolean removeAll(Collection<?> collection) {
        try {
            this.lock.lock();
            boolean removeAll = super.removeAll(collection) | this.blockedQueue.removeAll(collection);
            return removeAll;
        } finally {
            this.lock.unlock();
        }
    }

    private boolean offerBlockedResult(int operation, E result) {
        try {
            this.lock.lock();
            if (operation == 1) {
                super.remove(result);
            }
            boolean offer = this.blockedQueue.offer(result);
            return offer;
        } finally {
            this.lock.unlock();
        }
    }

    public final void recycleBlockedQueue() {
        try {
            this.lock.lock();
            Iterator<E> iterator = this.blockedQueue.iterator();
            while (iterator.hasNext()) {
                Dependency blockedItem = (Dependency) iterator.next();
                if (blockedItem.areDependenciesMet()) {
                    super.offer(blockedItem);
                    iterator.remove();
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    private static <T> T[] concatenate(T[] arr1, T[] arr2) {
        int arr1Len = arr1.length;
        int arr2Len = arr2.length;
        Object[] C = (Object[]) Array.newInstance(arr1.getClass().getComponentType(), arr1Len + arr2Len);
        System.arraycopy(arr1, 0, C, 0, arr1Len);
        System.arraycopy(arr2, 0, C, arr1Len, arr2Len);
        return C;
    }

    private E get(int operation, Long time, TimeUnit unit) throws InterruptedException {
        while (true) {
            E e;
            E result;
            Dependency dependency;
            switch (operation) {
                case 0:
                    e = (Dependency) super.take();
                    break;
                case 1:
                    dependency = (Dependency) super.peek();
                    break;
                case 2:
                    dependency = (Dependency) super.poll();
                    break;
                case 3:
                    dependency = (Dependency) super.poll(time.longValue(), unit);
                    break;
                default:
                    result = null;
                    break;
            }
            result = e;
            if (result == null || result.areDependenciesMet()) {
                return result;
            }
            offerBlockedResult(operation, result);
        }
    }

    public final /* bridge */ /* synthetic */ Object poll(long j, TimeUnit timeUnit) throws InterruptedException {
        return get(3, Long.valueOf(j), timeUnit);
    }
}
