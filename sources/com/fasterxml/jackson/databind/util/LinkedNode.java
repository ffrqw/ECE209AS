package com.fasterxml.jackson.databind.util;

public final class LinkedNode<T> {
    private LinkedNode<T> next;
    private final T value;

    public LinkedNode(T value, LinkedNode<T> next) {
        this.value = value;
        this.next = next;
    }

    public final void linkNext(LinkedNode<T> n) {
        if (this.next != null) {
            throw new IllegalStateException();
        }
        this.next = n;
    }

    public final LinkedNode<T> next() {
        return this.next;
    }

    public final T value() {
        return this.value;
    }

    public static <ST> boolean contains(LinkedNode<ST> node, ST value) {
        while (node != null) {
            if (node.value() == value) {
                return true;
            }
            node = node.next();
        }
        return false;
    }
}
