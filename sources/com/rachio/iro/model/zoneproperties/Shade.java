package com.rachio.iro.model.zoneproperties;

import com.rachio.iro.model.user.User;
import java.util.Comparator;

public class Shade extends ZonePropertyCommon {
    public static final Comparator<Shade> comparator = new Comparator<Shade>() {
        public final int compare(Shade lhs, Shade rhs) {
            return Double.valueOf(rhs.exposure).compareTo(Double.valueOf(lhs.exposure));
        }
    };
    private static final long serialVersionUID = 1;
    public double exposure;

    public Shade(String id) {
        super(id);
    }

    public String getDescriptiveString(User user) {
        if (this.exposure == 1.0d) {
            return "6-8 hours of sun";
        }
        if (this.exposure == 0.88d) {
            return "4-6 hours of sun";
        }
        if (this.exposure == 0.76d) {
            return "2-4 hours of sun";
        }
        if (this.exposure == 0.71d) {
            return "2 hours or less of sun";
        }
        return null;
    }
}
