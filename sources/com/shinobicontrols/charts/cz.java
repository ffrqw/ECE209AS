package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.Collection;

class cz<E> extends ArrayList<E> {
    private static final long serialVersionUID = 8109538979144194735L;

    static <E> cz<E> a(Collection<? extends E> collection) {
        return new cz(collection);
    }

    private cz(Collection<? extends E> collection) {
        super(collection);
    }

    cz() {
    }

    public boolean add(E object) {
        e(object == null);
        return super.add(object);
    }

    public void add(int index, E object) {
        e(object == null);
        super.add(index, object);
    }

    public boolean addAll(Collection<? extends E> collection) {
        e(collection.contains(null));
        return super.addAll(collection);
    }

    public boolean addAll(int index, Collection<? extends E> collection) {
        e(collection.contains(null));
        return super.addAll(index, collection);
    }

    public E set(int index, E object) {
        e(object == null);
        return super.set(index, object);
    }

    private void e(boolean z) {
        if (z) {
            throw new NullPointerException("Cannot add null elements.");
        }
    }
}
