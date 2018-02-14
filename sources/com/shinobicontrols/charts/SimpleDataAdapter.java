package com.shinobicontrols.charts;

import java.util.Collection;

public class SimpleDataAdapter<Tx, Ty> extends DataAdapter<Tx, Ty> {
    public boolean add(Data<Tx, Ty> dataPoint) {
        boolean add = super.add(dataPoint);
        if (add) {
            notifyDataChanged();
        }
        return add;
    }

    public void add(int location, Data<Tx, Ty> dataPoint) {
        super.add(location, dataPoint);
        notifyDataChanged();
    }

    public boolean addAll(Collection<? extends Data<Tx, Ty>> dataPoints) {
        boolean addAll = super.addAll(dataPoints);
        if (addAll) {
            notifyDataChanged();
        }
        return addAll;
    }

    public boolean addAll(int location, Collection<? extends Data<Tx, Ty>> dataPoints) {
        boolean addAll = super.addAll(location, dataPoints);
        if (addAll) {
            notifyDataChanged();
        }
        return addAll;
    }

    public void clear() {
        int size = size();
        super.clear();
        if (size > 0) {
            notifyDataChanged();
        }
    }

    public Data<Tx, Ty> remove(int location) {
        Data<Tx, Ty> remove = super.remove(location);
        notifyDataChanged();
        return remove;
    }

    public boolean remove(Object object) {
        boolean remove = super.remove(object);
        if (remove) {
            notifyDataChanged();
        }
        return remove;
    }

    public boolean removeAll(Collection<?> collection) {
        boolean removeAll = super.removeAll(collection);
        if (removeAll) {
            notifyDataChanged();
        }
        return removeAll;
    }

    public boolean retainAll(Collection<?> collection) {
        boolean retainAll = super.retainAll(collection);
        if (retainAll) {
            notifyDataChanged();
        }
        return retainAll;
    }

    public Data<Tx, Ty> set(int location, Data<Tx, Ty> dataPoint) {
        Data<Tx, Ty> data = super.set(location, dataPoint);
        notifyDataChanged();
        return data;
    }
}
