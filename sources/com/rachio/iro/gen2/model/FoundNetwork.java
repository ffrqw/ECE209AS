package com.rachio.iro.gen2.model;

import com.rachio.iro.utils.StringUtils;
import java.io.Serializable;

public class FoundNetwork implements Serializable {
    private static final long serialVersionUID = 1;
    public final int level;
    public final boolean mightBe5Gig;
    public final String name;
    public final boolean probablyDualBand;

    public FoundNetwork(String name, int level, boolean mightBe5Gig) {
        this(name, level, mightBe5Gig, false);
    }

    private FoundNetwork(String name, int level, boolean mightBe5Gig, boolean probablyDualBand) {
        this.name = name;
        this.level = level;
        this.mightBe5Gig = mightBe5Gig;
        this.probablyDualBand = probablyDualBand;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        return StringUtils.equals(this.name, ((FoundNetwork) o).name);
    }

    public static FoundNetwork merge(FoundNetwork existing, FoundNetwork updated) {
        boolean mightBe5Gig = existing.mightBe5Gig || updated.mightBe5Gig;
        return new FoundNetwork(existing.name, updated.level, mightBe5Gig, existing.mightBe5Gig ^ updated.mightBe5Gig);
    }
}
