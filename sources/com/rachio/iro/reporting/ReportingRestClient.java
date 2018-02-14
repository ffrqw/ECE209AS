package com.rachio.iro.reporting;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.rachio.iro.Keys;
import com.rachio.iro.cloud.BaseRestClient;
import com.rachio.iro.model.ResponseCacheItem;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.mapping.JsonMapper;
import com.rachio.iro.model.reporting.AnnualSummary;
import com.rachio.iro.model.reporting.WaterUseResponse;
import com.rachio.iro.model.reporting.WeatherIntelligence;
import com.rachio.iro.utils.CrashReporterUtils;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ReportingRestClient extends BaseRestClient {
    private static final String KEEN_URL = String.format("https://keen.rach.io/projects/%s/queries", new Object[]{Keys.IRO_KEEN_PROJECT_ID});
    private static final String TAG = ReportingRestClient.class.getName();
    private static final ObjectMapper objectMapper = JsonMapper.createMapperForRestClient();
    public static final SimpleDateFormat resultFormatWithTZ = new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601, Locale.US);

    private static String getTimezoneOffset() {
        return String.format("%d", new Object[]{Integer.valueOf(TimeZone.getDefault().getRawOffset() / 1000)});
    }

    private static String createFilterForDeviceId(String deviceId) {
        ArrayList<Map<String, String>> filters = new ArrayList();
        HashMap<String, String> deviceFilter = new HashMap();
        deviceFilter.put("property_name", "event.deviceId");
        deviceFilter.put("property_value", deviceId);
        deviceFilter.put("operator", "eq");
        filters.add(deviceFilter);
        return JsonMapper.toJson(filters);
    }

    public static String createTimeFrame(Date start, Date end) {
        Map<String, String> timeFrame = new HashMap();
        timeFrame.put("start", resultFormatWithTZ.format(start));
        timeFrame.put("end", resultFormatWithTZ.format(end));
        return JsonMapper.toJson(timeFrame);
    }

    private static URI buildUri(String base, String filters, String timezone, String timeframe, String analyses, String interval, String eventCollection, String targetProperty) {
        Builder uriBuilder = Uri.parse(KEEN_URL + base).buildUpon();
        uriBuilder.appendQueryParameter("api_key", Keys.IRO_KEEN_READ_KEY);
        if (filters != null) {
            uriBuilder.appendQueryParameter("filters", filters);
        }
        if (timezone != null) {
            uriBuilder.appendQueryParameter("timezone", timezone);
        }
        if (timeframe != null) {
            uriBuilder.appendQueryParameter("timeframe", timeframe);
        }
        if (analyses != null) {
            uriBuilder.appendQueryParameter("analyses", analyses);
        }
        if (interval != null) {
            uriBuilder.appendQueryParameter("interval", interval);
        }
        if (eventCollection != null) {
            uriBuilder.appendQueryParameter("event_collection", eventCollection);
        }
        if (targetProperty != null) {
            uriBuilder.appendQueryParameter("target_property", targetProperty);
        }
        try {
            return new URI(uriBuilder.build().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends Serializable> T doIt(Database database, String deviceId, long lastRan, Class<? extends T> responseType, String urlBase, String filters, String timezone, String timeFrame, String analyses, String interval, String eventCollection, String targetProperty) {
        URI uri = buildUri(urlBase, filters, timezone, timeFrame, analyses, interval, eventCollection, targetProperty);
        Serializable cachedResponse = (Serializable) BaseRestClient.getResponseFromCache(database, responseType, uri.toString(), lastRan);
        if (cachedResponse != null) {
            return cachedResponse;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Encoding", ContentCodingType.toString(Collections.singletonList(ContentCodingType.GZIP)));
        HttpEntity requestEntity = new HttpEntity(null, headers);
        int t = 0;
        while (t < 3) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
                mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
                List messageConverters = restTemplate.getMessageConverters();
                messageConverters.clear();
                messageConverters.add(mappingJackson2HttpMessageConverter);
                ClientHttpRequestFactory okHttpClientHttpRequestFactory = new OkHttpClientHttpRequestFactory();
                okHttpClientHttpRequestFactory.setConnectTimeout(5000);
                okHttpClientHttpRequestFactory.setReadTimeout(30000);
                restTemplate.setRequestFactory(okHttpClientHttpRequestFactory);
                ResponseEntity<? extends T> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, responseType);
                Log.d(TAG, uri.toString() + " " + ((Serializable) responseEntity.getBody()).toString());
                T response = (Serializable) responseEntity.getBody();
                ResponseCacheItem.put(database, deviceId, uri.toString(), responseType, response);
                return response;
            } catch (RestClientException ex) {
                if (!(ex.getRootCause() instanceof SocketTimeoutException)) {
                    CrashReporterUtils.silentException(ex);
                    break;
                }
                Log.d(TAG, "socket timeout, try " + t);
            } catch (HttpMessageConversionException ex2) {
                CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(ex2);
            }
        }
        CrashReporterUtils.silentException(new Exception("Request to " + uri.toString() + " failed"));
        return null;
        t++;
    }

    public static AnnualSummary getAnnualWaterUseSummaryStatsForTimeFrame(Database database, Date start, Date end, String deviceId, long lastRan, String analyses) {
        return (AnnualSummary) doIt(database, deviceId, 0, AnnualSummary.class, "/multi_analysis", createFilterForDeviceId(deviceId), getTimezoneOffset(), createTimeFrame(start, end), analyses, "yearly", "zoneDuration", null);
    }

    public static WeatherIntelligence getAnnualWaterWeatherIntelligenceForCurrentYear(Database database, String deviceId, long lastRan, String analyses) {
        return (WeatherIntelligence) doIt(database, deviceId, 0, WeatherIntelligence.class, "/multi_analysis", createFilterForDeviceId(deviceId), getTimezoneOffset(), "this_year", analyses, "yearly", "weatherIntelligence", null);
    }

    public static WeatherIntelligence getCommunityWaterWeatherIntelligenceForTimeFrame(Database database, Date start, Date end, String analyses) {
        return (WeatherIntelligence) doIt(database, null, 0, WeatherIntelligence.class, "/multi_analysis", null, getTimezoneOffset(), createTimeFrame(start, end), analyses, "yearly", "weatherIntelligence", null);
    }

    public static WaterUseResponse getDailyWaterUseDataForDateRangeSchedule(Database database, String timeframe, String deviceId, long lastRan) {
        return (WaterUseResponse) doIt(database, deviceId, lastRan, WaterUseResponse.class, "/sum", createFilterForDeviceId(deviceId), getTimezoneOffset(), timeframe, null, "daily", "zoneDuration", "event.estimatedGallons");
    }

    public static WaterUseResponse getDailyWaterUseDataForDateRangeIntelligence(Database database, String timeframe, String deviceId, long lastRan) {
        return (WaterUseResponse) doIt(database, deviceId, lastRan, WaterUseResponse.class, "/sum", createFilterForDeviceId(deviceId), getTimezoneOffset(), timeframe, null, "daily", "weatherIntelligence", "event.estimatedGallons");
    }

    public static WaterUseResponse getMonthlyWaterUseDataForCurrentYearSchedule(Database database, String timeframe, String deviceId, long lastRan) {
        return (WaterUseResponse) doIt(database, deviceId, lastRan, WaterUseResponse.class, "/sum", createFilterForDeviceId(deviceId), getTimezoneOffset(), timeframe, null, "monthly", "zoneDuration", "event.estimatedGallons");
    }

    public static WaterUseResponse getMonthlyWaterUseDataForCurrentYearIntelligence(Database database, String timeframe, String deviceId, long lastRan) {
        return (WaterUseResponse) doIt(database, deviceId, lastRan, WaterUseResponse.class, "/sum", createFilterForDeviceId(deviceId), getTimezoneOffset(), timeframe, null, "monthly", "weatherIntelligence", "event.estimatedGallons");
    }
}
