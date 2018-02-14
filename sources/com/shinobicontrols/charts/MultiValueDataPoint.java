package com.shinobicontrols.charts;

public class MultiValueDataPoint<Tx, Tv> extends DataPoint<Tx, Tv> implements Data<Tx, Tv>, MultiValueData<Tv>, SelectableData {
    private final Tv lA;
    private final Tv lx;
    private final Tv ly;
    private final Tv lz;

    public MultiValueDataPoint(Tx x, Tv low, Tv high, Tv open, Tv close) {
        super(x, close);
        if (low == null || high == null || open == null || close == null) {
            throw new IllegalArgumentException("You must supply all DataPoint parameter arguments, non-null");
        }
        this.lx = low;
        this.ly = high;
        this.lz = open;
        this.lA = close;
    }

    public MultiValueDataPoint(Tx x, Tv low, Tv high, Tv open, Tv close, boolean selected) {
        super(x, close, selected);
        if (low == null || high == null || open == null || close == null) {
            throw new IllegalArgumentException("You must supply all DataPoint parameter arguments, non-null");
        }
        this.lx = low;
        this.ly = high;
        this.lz = open;
        this.lA = close;
    }

    public MultiValueDataPoint(Tx x, Tv low, Tv high) {
        super(x, high);
        if (low == null || high == null) {
            throw new IllegalArgumentException("You must supply all DataPoint parameter arguments, non-null");
        }
        this.lx = low;
        this.ly = high;
        this.lz = null;
        this.lA = null;
    }

    public MultiValueDataPoint(Tx x, Tv low, Tv high, boolean selected) {
        super(x, high, selected);
        if (low == null || high == null) {
            throw new IllegalArgumentException("You must supply all DataPoint parameter arguments, non-null");
        }
        this.lx = low;
        this.ly = high;
        this.lz = null;
        this.lA = null;
    }

    public Tv getOpen() {
        return this.lz;
    }

    public Tv getHigh() {
        return this.ly;
    }

    public Tv getLow() {
        return this.lx;
    }

    public Tv getClose() {
        return this.lA;
    }
}
