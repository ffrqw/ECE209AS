package com.rachio.iro.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rachio.iro.R;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.annotation.TimeToLive;
import com.rachio.iro.utils.CrashReporterUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestClientOptions(path = "/1/forecast")
@TimeToLive(timeToLive = 3600000)
public class WeatherForecast extends ModelObject {
    private static final long serialVersionUID = 1;
    private Forecast[] cheatTable;
    public CurrentForecast current;
    public List<Forecast> forecast;

    @JsonIgnoreProperties({"icons", "localizedTimeStamp"})
    public static class Forecast extends ModelObject {
        private static final long serialVersionUID = 1;
        public double calculatedPrecip;
        public double cloudCover;
        public double currentTemperature;
        public String dailyWeatherType;
        public double dewPoint;
        public double humidity;
        public String iconUrl;
        public double precipIntensity;
        public double precipProbability;
        public String prettyTime;
        public double temperatureMax;
        public double temperatureMin;
        public long time;
        public UnitType unitType;
        public String weatherStationId;
        public String weatherSummary;
        public WeatherType weatherType;
        public double windSpeed;

        public enum UnitType {
            METRIC
        }

        public enum WeatherType {
            SUN("sun", R.drawable.sun),
            CLEAR_DAY("clear-day", R.drawable.clear_day),
            CLEAR_NIGHT("clear-night", R.drawable.clear_night),
            RAIN("rain", R.drawable.rain),
            SNOW("snow", R.drawable.snow),
            SLEET("sleet", R.drawable.sleet),
            WIND("wind", R.drawable.wind),
            FOG("fog", R.drawable.fog),
            CLOUDY("cloudy", R.drawable.cloudy),
            PARTLY_CLOUDY_DAY("partly-cloudy-day", R.drawable.partly_cloudy_day),
            PARTLY_CLOUDY_NIGHT("partly-cloudy-night", R.drawable.partly_cloudy_night);
            
            private final int icon;
            private final String type;

            private WeatherType(String type, int icon) {
                this.type = type;
                this.icon = icon;
            }

            public final String toString() {
                return this.type;
            }

            public final int getIcon() {
                return this.icon;
            }
        }

        public Date getDate() {
            return new Date(this.time * 1000);
        }

        public double getDisplayTemp() {
            return this.temperatureMax;
        }
    }

    public static class CurrentForecast extends Forecast {
        private static final long serialVersionUID = 1;

        public double getDisplayTemp() {
            return this.currentTemperature;
        }
    }

    public Forecast getForecastForDate(Date date) {
        Calendar c = Calendar.getInstance();
        if (this.cheatTable == null) {
            this.cheatTable = new Forecast[32];
            for (Forecast f : this.forecast) {
                c.setTimeInMillis(f.getDate().getTime());
                this.cheatTable[c.getTime().getDate()] = f;
            }
        }
        int wantedDate = date.getDate();
        if (this.cheatTable[wantedDate] != null) {
            return this.cheatTable[wantedDate];
        }
        if (this.current != null) {
            c.setTimeInMillis(this.current.getDate().getTime());
            if (c.getTime().getDate() == wantedDate) {
                System.out.println("The current forecast is for this date, using it");
                return this.current;
            }
        }
        CrashReporterUtils.silentException(new Exception("I don't have a forecast for " + date.toString()));
        return null;
    }
}
