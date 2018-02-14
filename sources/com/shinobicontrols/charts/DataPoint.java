package com.shinobicontrols.charts;

public class DataPoint<Tx, Ty> implements Data<Tx, Ty>, SelectableData {
    private boolean dM;
    private final Tx gq;
    private final Ty gr;

    public DataPoint(Tx x, Ty y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("You must supply all DataPoint parameter arguments, non-null");
        }
        this.gq = x;
        this.gr = y;
    }

    public DataPoint(Tx x, Ty y, boolean selected) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("You must supply all DataPoint parameter arguments, non-null");
        }
        this.gq = x;
        this.gr = y;
        this.dM = selected;
    }

    public Tx getX() {
        return this.gq;
    }

    public Ty getY() {
        return this.gr;
    }

    public boolean getSelected() {
        return this.dM;
    }
}
