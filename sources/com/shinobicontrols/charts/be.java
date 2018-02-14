package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.List;

abstract class be<T> {
    private final List<T> hp = new ArrayList();

    abstract void a(T t);

    be() {
    }

    void update() {
        for (Object a : this.hp) {
            a(a);
        }
    }

    void clear() {
        this.hp.clear();
    }
}
