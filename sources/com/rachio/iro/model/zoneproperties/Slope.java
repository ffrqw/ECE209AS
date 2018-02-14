package com.rachio.iro.model.zoneproperties;

import com.rachio.iro.model.user.User;
import java.util.Comparator;

public class Slope extends ZonePropertyCommon {
    public static final Comparator<Slope> comparator = new Comparator<Slope>() {
        public final int compare(Slope lhs, Slope rhs) {
            return lhs.variance.compareTo(rhs.variance);
        }
    };
    private static final long serialVersionUID = 1;
    public Variance variance;

    public enum Variance {
        ZERO_THREE,
        FOUR_SIX,
        SEVEN_TWELVE,
        OVER_TWELVE
    }

    public Slope(String id) {
        super(id);
    }

    public String getDescriptiveString(User user) {
        return null;
    }
}
