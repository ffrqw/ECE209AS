package com.rachio.iro.binder;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchWeatherForecastCommand.ForecastHolder;
import com.rachio.iro.binder.viewholder.CardHeaderFooterViewHolder;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.weather.WeatherForecast;
import com.rachio.iro.ui.view.ProgressView;
import com.rachio.iro.ui.view.WeatherForecastView;
import com.rachio.iro.utils.StringUtils;
import com.rachio.iro.utils.UnitUtils;
import com.squareup.picasso.Picasso;

public class LocalWeatherBinder extends BaseModelViewBinder<ForecastHolder> {

    public interface ChooseWeatherStationListener {
        void onChooseWeatherStation();
    }

    public static class ViewHolder extends ModelObjectViewHolder {
        public TextView cityZip = ((TextView) findView(R.id.weather_city_zip_text, false));
        public TextView currentWeatherDescription;
        public TextView currentWeatherHumidity;
        public ImageView currentWeatherIcon = ((ImageView) findView(R.id.current_weather_icon, false));
        public TextView currentWeatherPrecipitation;
        public TextView currentWeatherTemperature;
        public TextView currentWeatherWind;
        public CardHeaderFooterViewHolder headerFooterHolder;
        public ProgressView progressView = ((ProgressView) findView(R.id.progress_view, false));
        public LinearLayout weatherConditionsHeader = ((LinearLayout) findView(R.id.current_weather_conditions_header, false));
        public ViewGroup weatherConditionsViewGroup = ((ViewGroup) findView(R.id.current_weather_conditions, false));
        public WeatherForecastView weatherForecastView;
        public TextView weatherStationName;

        public ViewHolder(View v) {
            super(v);
            this.currentWeatherIcon.setColorFilter(new PorterDuffColorFilter(v.getResources().getColor(R.color.rachio_blue), Mode.SRC_IN));
            this.currentWeatherTemperature = (TextView) findView(R.id.current_weather_temperature, false);
            this.currentWeatherDescription = (TextView) findView(R.id.current_weather_description, false);
            this.currentWeatherPrecipitation = (TextView) findView(R.id.current_weather_precipitation, false);
            this.currentWeatherHumidity = (TextView) findView(R.id.current_weather_humidity, false);
            this.currentWeatherWind = (TextView) findView(R.id.current_weather_wind, false);
            this.weatherForecastView = (WeatherForecastView) findView(R.id.weather_forecast_view, false);
            this.weatherStationName = (TextView) findView(R.id.weather_station_name, false);
            this.headerFooterHolder = new CardHeaderFooterViewHolder(v);
            this.headerFooterHolder.footerContainer.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (v.getContext() instanceof ChooseWeatherStationListener) {
                        ((ChooseWeatherStationListener) v.getContext()).onChooseWeatherStation();
                    }
                }
            });
        }
    }

    public final /* bridge */ /* synthetic */ void onBind(ModelObjectViewHolder modelObjectViewHolder, Object obj) {
        String str;
        ForecastHolder forecastHolder = (ForecastHolder) obj;
        ViewHolder viewHolder = (ViewHolder) modelObjectViewHolder;
        User user = forecastHolder.loggedInUser;
        viewHolder.cityZip.setText(forecastHolder.device.getDeviceLocation());
        WeatherForecast weatherForecast = forecastHolder.forecast;
        Object obj2 = (weatherForecast == null || weatherForecast.current == null) ? null : 1;
        if (obj2 != null) {
            Picasso.with().load(weatherForecast.current.iconUrl).into(viewHolder.currentWeatherIcon);
            viewHolder.currentWeatherIcon.setVisibility(0);
        } else {
            viewHolder.currentWeatherIcon.setVisibility(4);
        }
        viewHolder.currentWeatherTemperature.setText(obj2 != null ? Math.round(UnitUtils.convertTempToUserUnits(user, weatherForecast.current.currentTemperature)) + "°" : "");
        viewHolder.currentWeatherDescription.setText(obj2 != null ? weatherForecast.current.weatherSummary : "");
        viewHolder.currentWeatherPrecipitation.setText(obj2 != null ? "Precipitation: " + toPercent(weatherForecast.current.precipProbability) : "");
        viewHolder.currentWeatherHumidity.setText(obj2 != null ? "Humidity: " + toPercent(weatherForecast.current.humidity) : "");
        double convertMilesPerHourToUserUnits = obj2 != null ? UnitUtils.convertMilesPerHourToUserUnits(user, weatherForecast.current.windSpeed) : 0.0d;
        String nameOfWindSpeedUnits = UnitUtils.getNameOfWindSpeedUnits(user);
        TextView textView = viewHolder.currentWeatherWind;
        StringBuilder stringBuilder = new StringBuilder("Wind: ");
        if (obj2 != null) {
            str = Math.round(convertMilesPerHourToUserUnits) + " " + nameOfWindSpeedUnits;
        } else {
            str = "--";
        }
        textView.setText(stringBuilder.append(str).toString());
        Device device = forecastHolder.device;
        if (forecastHolder != null) {
            if (forecastHolder.forecast.forecast != null && forecastHolder.forecast.forecast.size() > 0) {
                viewHolder.weatherConditionsViewGroup.setVisibility(0);
                viewHolder.weatherForecastView.refreshForecast(user, forecastHolder.forecast);
            }
            if (forecastHolder.forecast.current == null || forecastHolder.station == null || !forecastHolder.station.isValid()) {
                viewHolder.weatherStationName.setVisibility(4);
                return;
            }
            Context context = viewHolder.itemView.getContext();
            viewHolder.weatherStationName.setVisibility(0);
            String string = context.getString(R.string.weather_station_label);
            String str2 = forecastHolder.station.stationId != null ? forecastHolder.station.stationId : "";
            float[] fArr = new float[1];
            Location.distanceBetween(device.latitude, device.longitude, forecastHolder.station.latitude, forecastHolder.station.longitude, fArr);
            convertMilesPerHourToUserUnits = UnitUtils.convertMilesToUserUnits(user, UnitUtils.convertMetersToMiles((double) fArr[0]));
            String nameOfDistanceUnits = UnitUtils.getNameOfDistanceUnits(user);
            str = String.format("%.02f %s", new Object[]{Double.valueOf(convertMilesPerHourToUserUnits), nameOfDistanceUnits});
            Object join = StringUtils.join(" ", string, str2, str);
            CharSequence spannableString = new SpannableString(join);
            spannableString.setSpan(new TextAppearanceSpan(context, R.style.Rachio.TextAppearance.Weather), 0, string.length(), 33);
            spannableString.setSpan(new TextAppearanceSpan(context, R.style.Rachio.TextAppearance.Weather.Black), string.length(), (string.length() + 1) + str2.length(), 33);
            spannableString.setSpan(new TextAppearanceSpan(context, R.style.Rachio.TextAppearance.Weather), (string.length() + 1) + str2.length(), join.length(), 33);
            viewHolder.weatherStationName.setText(spannableString, BufferType.SPANNABLE);
        }
    }

    public final int getLayoutId() {
        return R.layout.view_card_device_local_weather;
    }

    public final ModelObjectViewHolder createViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected final void setContentShown(ModelObjectViewHolder holder, boolean isShown) {
        int color;
        int i = 8;
        ViewHolder viewHolder = (ViewHolder) holder;
        if (TextUtils.isEmpty(viewHolder.headerFooterHolder.headerTextLeft.getText())) {
            color = viewHolder.itemView.getResources().getColor(R.color.rachio_aqua);
            viewHolder.headerFooterHolder.headerTextLeft.setText("Local Weather");
            viewHolder.headerFooterHolder.headerTextRight.setText("");
            viewHolder.headerFooterHolder.headerBackground.setColor(color);
            viewHolder.headerFooterHolder.footerText.setText("Change Weather Station");
        }
        ProgressView progressView = viewHolder.progressView;
        if (isShown) {
            color = 8;
        } else {
            color = 0;
        }
        progressView.setVisibility(color);
        ProgressBar progressBar = viewHolder.headerFooterHolder.headerProgressBar;
        if (!isShown) {
            i = 0;
        }
        progressBar.setVisibility(i);
    }

    private static String toPercent(double number) {
        return Math.round(100.0d * number) + "%";
    }
}
