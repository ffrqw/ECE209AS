package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.zoneproperties.Crop;
import com.rachio.iro.model.zoneproperties.Nozzle;
import com.rachio.iro.model.zoneproperties.Shade;
import com.rachio.iro.model.zoneproperties.Slope;
import com.rachio.iro.model.zoneproperties.Soil;

@RestClientOptions(path = "/1/zone/model")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneProps extends ModelObject {
    public Crop customCrop;
    public Nozzle customNozzle;
    public Shade customShade;
    public Slope customSlope;
    public Soil customSoil;
}
