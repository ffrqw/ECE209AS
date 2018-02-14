package com.shinobicontrols.charts;

abstract class s<T extends Series<?>> implements er {
    protected final float[] cY = new float[]{1.0f, 1.0f, 0.0f, 0.0f, -1.0f, -1.0f};
    protected final T cZ;
    private boolean da = true;
    protected bt db;

    s(T t) {
        this.cZ = t;
    }

    public void av() {
        this.da = true;
    }

    public boolean aw() {
        return this.da;
    }

    public void ax() {
        this.da = false;
    }
}
