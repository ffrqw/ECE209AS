package com.rachio.iro.ui.widget;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.weather.WeatherForecast.Forecast;
import com.rachio.iro.utils.UnitUtils;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.Locale;

public class WeatherForecastDayWidget extends LinearLayout {
    private final Calendar calendar = Calendar.getInstance();
    private final TextView dayName;
    private final int scheduleTypeIconSize;
    private final TextView temperatureHi;
    private final TextView temperatureLo;
    private final ImageView weatherIcon;

    public WeatherForecastDayWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        View widgetView = inflate(context, R.layout.widget_weather_forecast_day, this);
        this.dayName = (TextView) widgetView.findViewById(R.id.widget_forecast_day);
        this.weatherIcon = (ImageView) widgetView.findViewById(R.id.widget_forecast_condition_image);
        this.temperatureHi = (TextView) widgetView.findViewById(R.id.widget_forecast_temperature_hi);
        this.temperatureLo = (TextView) widgetView.findViewById(R.id.widget_forecast_temperature_lo);
        this.scheduleTypeIconSize = UnitUtils.toDp(context, 20);
        this.weatherIcon.setColorFilter(new PorterDuffColorFilter(this.temperatureHi.getCurrentTextColor(), Mode.SRC_IN));
    }

    public final void setData(User user, Forecast forecast, int dateOffset) {
        if (dateOffset > 0) {
            this.calendar.setTimeInMillis(System.currentTimeMillis() + (((long) dateOffset) * 86400000));
        }
        this.dayName.setText(this.calendar.getDisplayName(7, 1, Locale.US).toUpperCase());
        if (forecast != null) {
            this.temperatureHi.setText(String.valueOf(Math.round(UnitUtils.convertTempToUserUnits(user, forecast.temperatureMax))) + "°");
            this.temperatureLo.setText(String.valueOf(Math.round(UnitUtils.convertTempToUserUnits(user, forecast.temperatureMin))) + "°");
            Object obj = forecast.iconUrl;
            if (TextUtils.isEmpty(obj)) {
                this.weatherIcon.setVisibility(4);
                return;
            }
            this.weatherIcon.setVisibility(0);
            Picasso.with().load(obj).fit().centerInside().into(this.weatherIcon);
            return;
        }
        this.temperatureHi.setText("--");
        this.temperatureLo.setText("--");
        this.weatherIcon.setVisibility(4);
    }
}
