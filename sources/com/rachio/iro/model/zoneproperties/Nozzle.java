package com.rachio.iro.model.zoneproperties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.user.User;
import com.rachio.iro.utils.UnitUtils;

@RestClientOptions(path = "/1/nozzle/model")
public class Nozzle extends ZonePropertyCommon {
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public Category category;
    @JsonView({TransmittableView.class})
    public boolean customizable;
    @JsonView({TransmittableView.class})
    public double inchesPerHour = 1.4d;
    @JsonView({TransmittableView.class})
    public String manufacturer;
    @JsonView({TransmittableView.class})
    public String model;
    @JsonIgnore
    public String userId;

    public enum Category {
        ROTARY_NOZZLE(5.0d),
        FIXED_SPRAY_HEAD(5.0d),
        ROTOR_HEAD(5.0d),
        DRIP(20.0d),
        EMITTER(20.0d),
        MISTER(20.0d),
        BUBBLER(20.0d);
        
        public final double maxFlowRate;

        private Category(double maxFlowRate) {
            this.maxFlowRate = maxFlowRate;
        }
    }

    public Nozzle(String id) {
        super(id);
    }

    public String getDescriptiveString(User user) {
        return String.format("%.2f %s", new Object[]{Double.valueOf(UnitUtils.convertPrecipToUserUnits(user, this.inchesPerHour)), UnitUtils.getPrecipUnitName(user)});
    }
}
