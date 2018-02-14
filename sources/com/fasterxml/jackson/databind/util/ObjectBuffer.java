package com.fasterxml.jackson.databind.util;

import java.lang.reflect.Array;
import java.util.List;

public final class ObjectBuffer {
    private static final int MAX_CHUNK = 262144;
    private static final int SMALL_CHUNK = 16384;
    private Object[] _freeBuffer;
    private LinkedNode<Object[]> _head;
    private int _size;
    private LinkedNode<Object[]> _tail;

    public final Object[] resetAndStart() {
        _reset();
        if (this._freeBuffer == null) {
            return new Object[12];
        }
        return this._freeBuffer;
    }

    public final Object[] appendCompletedChunk(Object[] fullChunk) {
        LinkedNode<Object[]> next = new LinkedNode(fullChunk, null);
        if (this._head == null) {
            this._tail = next;
            this._head = next;
        } else {
            this._tail.linkNext(next);
            this._tail = next;
        }
        int len = fullChunk.length;
        this._size += len;
        if (len < SMALL_CHUNK) {
            len += len;
        } else if (len < MAX_CHUNK) {
            len += len >> 2;
        }
        return new Object[len];
    }

    public final Object[] completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries) {
        int totalSize = lastChunkEntries + this._size;
        Object[] result = new Object[totalSize];
        _copyTo(result, totalSize, lastChunk, lastChunkEntries);
        return result;
    }

    public final <T> T[] completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries, Class<T> componentType) {
        int totalSize = lastChunkEntries + this._size;
        Object[] result = (Object[]) Array.newInstance(componentType, totalSize);
        _copyTo(result, totalSize, lastChunk, lastChunkEntries);
        _reset();
        return result;
    }

    public final void completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries, List<Object> resultList) {
        int i;
        for (LinkedNode<Object[]> n = this._head; n != null; n = n.next()) {
            for (Object add : (Object[]) n.value()) {
                resultList.add(add);
            }
        }
        for (i = 0; i < lastChunkEntries; i++) {
            resultList.add(lastChunk[i]);
        }
    }

    public final int initialCapacity() {
        return this._freeBuffer == null ? 0 : this._freeBuffer.length;
    }

    public final int bufferedSize() {
        return this._size;
    }

    protected final void _reset() {
        if (this._tail != null) {
            this._freeBuffer = (Object[]) this._tail.value();
        }
        this._tail = null;
        this._head = null;
        this._size = 0;
    }

    protected final void _copyTo(Object resultArray, int totalSize, Object[] lastChunk, int lastChunkEntries) {
        int ptr = 0;
        for (LinkedNode<Object[]> n = this._head; n != null; n = n.next()) {
            Object[] curr = (Object[]) n.value();
            int len = curr.length;
            System.arraycopy(curr, 0, resultArray, ptr, len);
            ptr += len;
        }
        System.arraycopy(lastChunk, 0, resultArray, ptr, lastChunkEntries);
        ptr += lastChunkEntries;
        if (ptr != totalSize) {
            throw new IllegalStateException("Should have gotten " + totalSize + " entries, got " + ptr);
        }
    }
}
