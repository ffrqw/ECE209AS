package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class DataAdapter<Tx, Ty> implements Iterable<Data<Tx, Ty>> {
    private final List<Data<Tx, Ty>> gn = new cz();
    private final List<OnDataChangedListener> go = new ArrayList();

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    protected DataAdapter() {
    }

    public boolean add(Data<Tx, Ty> dataPoint) {
        return this.gn.add(dataPoint);
    }

    public void add(int location, Data<Tx, Ty> dataPoint) {
        this.gn.add(location, dataPoint);
    }

    public boolean addAll(Collection<? extends Data<Tx, Ty>> dataPoints) {
        return this.gn.addAll(dataPoints);
    }

    public boolean addAll(int location, Collection<? extends Data<Tx, Ty>> dataPoints) {
        return this.gn.addAll(location, dataPoints);
    }

    public void clear() {
        this.gn.clear();
    }

    public boolean contains(Object object) {
        return this.gn.contains(object);
    }

    public boolean containsAll(Collection<?> collection) {
        return this.gn.containsAll(collection);
    }

    public Data<Tx, Ty> get(int location) {
        return (Data) this.gn.get(location);
    }

    public int hashCode() {
        return this.gn.hashCode();
    }

    public int indexOf(Object object) {
        return this.gn.indexOf(object);
    }

    public boolean isEmpty() {
        return this.gn.isEmpty();
    }

    public Iterator<Data<Tx, Ty>> iterator() {
        return this.gn.iterator();
    }

    public int lastIndexOf(Object object) {
        return this.gn.lastIndexOf(object);
    }

    public Data<Tx, Ty> remove(int location) {
        return (Data) this.gn.remove(location);
    }

    public boolean remove(Object object) {
        return this.gn.remove(object);
    }

    public boolean removeAll(Collection<?> collection) {
        return this.gn.removeAll(collection);
    }

    public boolean retainAll(Collection<?> collection) {
        return this.gn.retainAll(collection);
    }

    public Data<Tx, Ty> set(int location, Data<Tx, Ty> dataPoint) {
        return (Data) this.gn.set(location, dataPoint);
    }

    public int size() {
        return this.gn.size();
    }

    public Object[] toArray() {
        return this.gn.toArray();
    }

    public List<Data<Tx, Ty>> getDataPointsForDisplay() {
        return cz.a(this.gn);
    }

    public <T> T[] toArray(T[] array) {
        return this.gn.toArray(array);
    }

    @Deprecated
    protected final void fireUpdateHandler() {
        notifyDataChanged();
    }

    public void notifyDataChanged() {
        for (OnDataChangedListener onDataChanged : this.go) {
            onDataChanged.onDataChanged();
        }
    }

    public void addOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        this.go.add(onDataChangedListener);
    }

    public void removeOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        this.go.remove(onDataChangedListener);
    }
}
