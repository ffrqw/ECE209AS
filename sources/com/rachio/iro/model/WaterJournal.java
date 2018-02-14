package com.rachio.iro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.utils.CalendarUtil;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

@RestClientOptions(path = "/1/waterjournal/zone")
@JsonIgnoreProperties({"hourlyMoistureLevelData"})
public class WaterJournal extends ModelObject implements Serializable {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final long serialVersionUID = 1;
    public TreeMap<String, WaterEntry> waterEntries;
    public String zoneId;

    @JsonIgnoreProperties({"irrigations"})
    public static class WaterEntry implements Serializable {
        private static final long serialVersionUID = 1;
        public double cropCoefficient;
        public double cropEvapotranspiration;
        public Date date;
        public double depletion;
        public double depthOfWater;
        public double effectiveRain;
        public double evapotranspiration;
        public double exposure;
        public boolean fullFlexIrrigation;
        public double soilMoistureLevelAtEndOfDay;
        public double soilMoistureLevelAtStartOfDay;
        public String stationId;
        public double temperatureMax;
        public double temperatureMin;
        public double totalIrrigation;

        public static class Irrigation {
        }
    }

    public double getMoistureLevelForToday(TimeZone deviceTimeZone, double zoneDepthOfWater) {
        Calendar c = Calendar.getInstance(deviceTimeZone);
        CalendarUtil.setToStartOfDay(c);
        String today = dateFormat.format(c.getTime());
        if (this.waterEntries.containsKey(today)) {
            return ((WaterEntry) this.waterEntries.get(today)).soilMoistureLevelAtEndOfDay / zoneDepthOfWater;
        }
        return -1.0d;
    }
}
