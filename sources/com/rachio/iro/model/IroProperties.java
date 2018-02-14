package com.rachio.iro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.annotation.TimeToLive;
import java.io.Serializable;

@TimeToLive(timeToLive = 3600000)
@RestClientOptions(appHeaders = true, path = "/1/property/iro")
@JsonIgnoreProperties({"zoneDefaultData", "zoneData", "gen2ClientEnabled", "errors"})
public class IroProperties extends ModelObject implements Serializable {
    static final long serialVersionUID = 2;
    public String[] macPrefix;
    public Schedule schedule;
    public Sensors sensor;

    public static class Schedule implements Serializable {
        static final long serialVersionUID = 1;
        public int gen2FixedScheduleLimit;
        public int gen2FlexScheduleLimit;
    }

    public static class Sensors implements Serializable {
        static final long serialVersionUID = 1;
        public FlowSensor[] flowSensor;

        public static class FlowSensor implements Serializable {
            static final long serialVersionUID = 1;
            public float kfactor;
            public String make;
            public String model;
            public float sensorOffset;

            public String toString() {
                return String.format("%s %s", new Object[]{this.make, this.model});
            }
        }
    }
}
