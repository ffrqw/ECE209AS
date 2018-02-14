package com.rachio.iro.model.zoneproperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rachio.iro.model.user.User;
import com.rachio.iro.utils.UnitUtils;
import java.util.Comparator;

@JsonIgnoreProperties({"percentAvailableWater"})
public class Soil extends ZonePropertyCommon {
    public static final Comparator<Soil> comparator = new Comparator<Soil>() {
        public final int compare(Soil lhs, Soil rhs) {
            return Double.valueOf(rhs.infiltrationRate).compareTo(Double.valueOf(lhs.infiltrationRate));
        }
    };
    private static final long serialVersionUID = 1;
    public String category;
    public double infiltrationRate;

    public Soil(String id) {
        super(id);
    }

    public String getDescriptiveString(User user) {
        return String.format("%.2f %s", new Object[]{Double.valueOf(UnitUtils.convertInchesToUserUnits(user, this.infiltrationRate)), UnitUtils.getPrecipUnitName(user)});
    }
}
