package com.shinobicontrols.charts;

import java.util.Iterator;
import java.util.List;

class aw implements an {
    aw() {
    }

    public void a(List<? extends Data<?, ?>> list, am amVar, Series<?> series) {
        if (amVar.a(series)) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                if (amVar.a((Data) it.next(), series)) {
                    it.remove();
                }
            }
        }
    }
}
