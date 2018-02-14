package com.rachio.iro.model.weather;

import com.rachio.iro.model.annotation.RestClientOptions;
import com.rachio.iro.model.annotation.TimeToLive;

@RestClientOptions(path = "/1/forecast/preferred_station")
@TimeToLive(timeToLive = -1)
public class PreferedForecastStation extends ForecastStation {
}
