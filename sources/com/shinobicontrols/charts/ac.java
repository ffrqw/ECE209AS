package com.shinobicontrols.charts;

abstract class ac<T extends CartesianSeries<?>> extends s<T> {
    float dJ;

    abstract void a(SChartGLDrawer sChartGLDrawer, float[] fArr, float f);

    ac(T t) {
        super(t);
    }

    public void a(bd bdVar, SChartGLDrawer sChartGLDrawer) {
        a((CartesianSeries) this.cZ, this.cY);
        a(sChartGLDrawer, this.cY, bdVar.cs().density);
    }

    public void b(bd bdVar, SChartGLDrawer sChartGLDrawer) {
        a((CartesianSeries) this.cZ, this.cY);
        sChartGLDrawer.updateRenderQueues(((CartesianSeries) this.cZ).ej(), this.cZ, this.cY);
    }

    private void a(T t, float[] fArr) {
        Axis xAxis = t.getXAxis();
        Axis yAxis = t.getYAxis();
        if (xAxis == null || yAxis == null) {
            throw new IllegalStateException("Chart must have an X Axis and a Y Axis to draw a Series.");
        }
        Range range = xAxis.ai;
        Range range2 = yAxis.ai;
        if (!Range.h(range) && !Range.h(range2)) {
            fArr[0] = ((float) range.dF()) / 2.0f;
            fArr[1] = ((float) range2.dF()) / 2.0f;
            fArr[2] = (float) range.nv;
            fArr[3] = (float) range2.nv;
        }
    }
}
