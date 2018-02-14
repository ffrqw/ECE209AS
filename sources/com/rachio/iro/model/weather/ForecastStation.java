package com.rachio.iro.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.annotation.TimeToLive;
import com.rachio.iro.model.user.User;
import com.rachio.iro.utils.UnitUtils;
import java.io.Serializable;

@RestClientOptions(arrayPath = "/1/forecast/forecast_stations")
@TimeToLive(timeToLive = 86400000)
public class ForecastStation extends ModelObject implements Serializable {
    private static final long serialVersionUID = 1;
    public String city;
    public String country;
    public double distance;
    public double elevation;
    public boolean hasPrecip;
    public double latitude = -1.0d;
    public double longitude = -1.0d;
    public boolean personalWeatherStation;
    public String state;
    public String stationId;
    public String url;

    @JsonIgnore
    public String createSnippet(User user) {
        String distanceUnitsName = UnitUtils.getNameOfDistanceUnits(user);
        if (UnitUtils.convertMilesToUserUnits(user, this.distance) <= 0.0d) {
            return "";
        }
        return String.format("%.02f %s", new Object[]{Double.valueOf(UnitUtils.convertMilesToUserUnits(user, this.distance)), distanceUnitsName});
    }

    public boolean equals(Object o) {
        return this.stationId.equals(((ForecastStation) o).stationId);
    }

    @JsonIgnore
    public boolean isValid() {
        return (this.stationId == null || this.latitude == -1.0d || this.longitude == -1.0d) ? false : true;
    }
}
