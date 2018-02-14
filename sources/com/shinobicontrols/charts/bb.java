package com.shinobicontrols.charts;

import java.util.Set;

abstract class bb<THandler extends a> {

    interface a {
    }

    static class b {
        b() {
        }
    }

    abstract void a(THandler tHandler);

    abstract b f();

    bb() {
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() == getClass()) {
            return ((bb) o).f().equals(f());
        }
        return false;
    }

    public int hashCode() {
        return f().hashCode();
    }

    void b(a aVar) {
        a c = c(aVar);
        if (c != null) {
            a(c);
        }
    }

    void a(Set<? extends a> set) {
        for (a b : set) {
            b(b);
        }
    }

    THandler c(a aVar) {
        return aVar;
    }
}
