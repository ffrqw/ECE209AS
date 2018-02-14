package com.rachio.iro.async.command;

import android.widget.Toast;
import com.rachio.iro.async.command.FetchCalendarCommand.ScheduleCalendarMeta;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.schedule.ScheduleCalendar;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.weather.ForecastStation;
import com.rachio.iro.model.weather.PreferedForecastStation;
import com.rachio.iro.model.weather.WeatherForecast;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;

public class FetchWeatherForecastCommand extends CommandThatMayNeedToPullADevice<ForecastHolder> {
    private final String mDeviceId;
    private final FetchWeatherForecastListener mListener;

    public interface FetchWeatherForecastListener {
        void onWeatherForecastLoaded(ForecastHolder forecastHolder);
    }

    public static class ForecastHolder {
        public ScheduleCalendar calendar;
        public Device device;
        public WeatherForecast forecast;
        public User loggedInUser;
        public ForecastStation station;
    }

    private class WeatherErrorHandler extends HttpResponseErrorHandler {
        private WeatherErrorHandler() {
        }

        public final void handleError(ClientHttpResponse response) throws IOException {
            super.handleError(response);
            Toast.makeText(BaseCommand.toContext(FetchWeatherForecastCommand.this.mListener), "Error loading weather forecast", 0).show();
        }
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        ForecastHolder forecastHolder = (ForecastHolder) obj;
        if (this.mListener != null) {
            this.mListener.onWeatherForecastLoaded(forecastHolder);
        }
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        ScheduleCalendar scheduleCalendar = null;
        String loggedInUserId = this.prefsWrapper.getLoggedInUserId();
        User user = loggedInUserId != null ? (User) this.database.find(User.class, loggedInUserId) : null;
        Device fetchDevice = fetchDevice(this.mDeviceId);
        if (user == null || fetchDevice == null) {
            return null;
        }
        ForecastHolder forecastHolder = new ForecastHolder();
        ForecastStation forecastStation = (ForecastStation) this.restClient.getObjectById(this.database, this.mDeviceId, PreferedForecastStation.class, new HttpResponseErrorHandler());
        WeatherForecast weatherForecast = (WeatherForecast) this.restClient.getObjectById(this.database, this.mDeviceId, WeatherForecast.class, new WeatherErrorHandler());
        if (weatherForecast == null) {
            return null;
        }
        forecastHolder.device = fetchDevice;
        forecastHolder.station = forecastStation;
        forecastHolder.loggedInUser = user;
        forecastHolder.forecast = weatherForecast;
        long[] defaultStartEnd = FetchCalendarCommand.defaultStartEnd();
        ScheduleCalendarMeta loadCalendar = FetchCalendarCommand.loadCalendar(this.database, this.prefsWrapper, this.restClient, fetchDevice, defaultStartEnd[0], defaultStartEnd[1]);
        if (loadCalendar != null) {
            scheduleCalendar = loadCalendar.calendar;
        }
        forecastHolder.calendar = scheduleCalendar;
        return forecastHolder;
    }

    public FetchWeatherForecastCommand(FetchWeatherForecastListener listener, String deviceId) {
        if (deviceId == null) {
            throw new IllegalArgumentException("deviceId cannot be null");
        }
        this.mListener = listener;
        this.mDeviceId = deviceId;
        BaseCommand.component(listener).inject(this);
    }
}
