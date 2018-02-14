package com.rachio.iro.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.weather.WeatherForecast;
import com.rachio.iro.model.weather.WeatherForecast.Forecast;
import com.rachio.iro.ui.widget.WeatherForecastDayWidget;
import com.rachio.iro.utils.CalendarUtil;
import java.util.Calendar;

public class WeatherForecastView extends LinearLayout {
    private static final String TAG = WeatherForecastView.class.getCanonicalName();
    private final WeatherForecastDayWidget[] forecasts;

    public interface Listener {
    }

    public WeatherForecastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.forecasts = new WeatherForecastDayWidget[7];
        inflate(context, R.layout.view_weather_forecast, this);
        for (int i = 0; i < 7; i++) {
            WeatherForecastDayWidget widget = (WeatherForecastDayWidget) findViewById(getResources().getIdentifier("day_forecast_" + (i + 1), "id", getContext().getPackageName()));
            if (widget != null) {
                widget.setTag(Integer.valueOf(i));
                this.forecasts[i] = widget;
                widget.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (null != null) {
                            null;
                            ((Integer) v.getTag()).intValue();
                        }
                    }
                });
            }
        }
    }

    public WeatherForecastView(Context context) {
        this(context, null);
    }

    public final synchronized void refreshForecast(User user, WeatherForecast forecast) {
        if (forecast == null) {
            Log.d(TAG, "forecast is not ready");
        } else {
            Calendar c = Calendar.getInstance();
            CalendarUtil.setToStartOfDay(c);
            for (int i = 0; i < 7; i++) {
                Forecast f = forecast.getForecastForDate(c.getTime());
                if (f != null) {
                    this.forecasts[i].setData(user, f, i);
                }
                c.add(6, 1);
            }
        }
    }
}
