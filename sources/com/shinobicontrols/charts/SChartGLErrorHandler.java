package com.shinobicontrols.charts;

class SChartGLErrorHandler {
    SChartGLErrorHandler() {
    }

    void logMessageImpl(String msg) {
        ev.f(msg);
    }

    void handleErrorImpl(String msg) {
        ev.h(msg);
        throw new Error(msg);
    }
}
