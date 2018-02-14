package com.rachio.iro.async.command;

import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.weather.ForecastStation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class FetchWeatherStationsCommand extends CommandThatMayNeedToPullADevice<WeatherStationsHolder> {
    private final String deviceId;
    private final FetchForecastStationsListener listener;

    public interface FetchForecastStationsListener {
        void onForecastStationsFetched(WeatherStationsHolder weatherStationsHolder);
    }

    public static class WeatherStationsHolder {
        public final Device device;
        public final List<ForecastStation> forecastStations;
        private TreeMap<String, ForecastStation> stationMap;
        public final User user;

        public WeatherStationsHolder(Device device, List<ForecastStation> forecastStations, User user) {
            this.device = device;
            this.forecastStations = forecastStations;
            this.user = user;
        }

        public final synchronized ForecastStation getStationById(String stationId) {
            if (this.stationMap == null) {
                this.stationMap = new TreeMap();
                for (ForecastStation f : this.forecastStations) {
                    this.stationMap.put(f.stationId, f);
                }
            }
            return (ForecastStation) this.stationMap.get(stationId);
        }
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        WeatherStationsHolder weatherStationsHolder = (WeatherStationsHolder) obj;
        if (this.listener != null) {
            this.listener.onForecastStationsFetched(weatherStationsHolder);
        }
    }

    public FetchWeatherStationsCommand(FetchForecastStationsListener listener, String deviceId) {
        this.listener = listener;
        this.deviceId = deviceId;
        BaseCommand.component(listener).inject(this);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        User user;
        Device fetchDevice = fetchDevice(this.deviceId);
        if (fetchDevice.managerUser != null) {
            user = (User) this.database.find(User.class, fetchDevice.managerUser.id);
        } else {
            user = (User) this.database.find(User.class, fetchDevice.user.id);
        }
        ForecastStation[] forecastStationArr = (ForecastStation[]) this.restClient.getObjectById(this.database, this.deviceId, ForecastStation[].class, new HttpResponseErrorHandler());
        List arrayList = new ArrayList();
        if (forecastStationArr != null) {
            arrayList.addAll(Arrays.asList(forecastStationArr));
        }
        return new WeatherStationsHolder(fetchDevice, arrayList, user);
    }
}
