package com.shinobicontrols.charts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class bg {
    private final HashMap<b, HashSet<a>> iJ = new HashMap();

    bg() {
    }

    bh a(b bVar, a aVar) {
        HashSet hashSet = (HashSet) this.iJ.get(bVar);
        if (hashSet == null) {
            hashSet = new HashSet();
            this.iJ.put(bVar, hashSet);
        }
        return a(hashSet, aVar);
    }

    void a(bb<?> bbVar) {
        a(bbVar.f(), (bb) bbVar);
    }

    private bh a(final HashSet<a> hashSet, final a aVar) {
        hashSet.add(aVar);
        return new bh(this) {
            final /* synthetic */ bg iM;

            public void cP() {
                hashSet.remove(aVar);
            }
        };
    }

    private void a(b bVar, bb<?> bbVar) {
        Set set = (HashSet) this.iJ.get(bVar);
        if (set != null && set.size() > 0) {
            bbVar.a(set);
        }
    }
}
