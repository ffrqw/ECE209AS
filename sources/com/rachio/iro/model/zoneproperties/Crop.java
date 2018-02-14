package com.rachio.iro.model.zoneproperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rachio.iro.model.user.User;
import java.util.Comparator;

@JsonIgnoreProperties({"customCropCoefficientMap"})
public class Crop extends ZonePropertyCommon {
    public static final Comparator<Crop> comparator = new Comparator<Crop>() {
        public final int compare(Crop lhs, Crop rhs) {
            return Double.valueOf(rhs.coefficient).compareTo(Double.valueOf(lhs.coefficient));
        }
    };
    private static final long serialVersionUID = 1;
    public double coefficient;
    public double customCropCoefficient;

    public Crop(String id) {
        super(id);
    }

    public String getDescriptiveString(User user) {
        return String.format("Coefficent: %.2f", new Object[]{Double.valueOf(this.coefficient)});
    }
}
