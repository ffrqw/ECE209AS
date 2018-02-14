package com.rachio.iro.async.command;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.user.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FetchEventsCommand extends BaseCommand<List<Event>> {
    private final String deviceid;
    private Comparator<Event> eventComparator;
    private final int first;
    private final FetchEventsListener mListener;
    private final int max;
    private final String topic;

    public interface FetchEventsListener {
        void onEventsLoaded(String str, List<Event> list);
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        List list = (List) obj;
        if (this.mListener != null) {
            this.mListener.onEventsLoaded(this.topic, list);
        }
    }

    public FetchEventsCommand(FetchEventsListener listener, String deviceId, String topic) {
        this(listener, deviceId, topic, 0, 30);
    }

    public FetchEventsCommand(FetchEventsListener listener, String deviceId, String topic, int first, int max) {
        this.eventComparator = Collections.reverseOrder(new Comparator<Event>() {
            public /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
                return ((Event) obj).eventDate.compareTo(((Event) obj2).eventDate);
            }
        });
        if (deviceId == null) {
            throw new IllegalArgumentException("event data cannot be null");
        }
        this.mListener = listener;
        this.deviceid = deviceId;
        this.topic = topic;
        this.first = first;
        this.max = max;
        BaseCommand.component(listener).inject(this);
    }

    public static List<Event> fetchEvents(Database database, PrefsWrapper prefsWrapper, RestClient restClient, String deviceid, String topic, int first, int max) {
        User u = User.getLoggedInUser(database, prefsWrapper);
        HttpResponseErrorHandler errorHandler = new HttpResponseErrorHandler();
        Event[] result = restClient.getEvents(deviceid, topic, first, max, errorHandler);
        if (errorHandler.hasError) {
            return null;
        }
        List<Event> asList = Arrays.asList(result);
        for (Event e : asList) {
            e.deviceId = deviceid;
            e.units = u.displayUnit;
            database.save(e);
        }
        return asList;
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        return fetchEvents(this.database, this.prefsWrapper, this.restClient, this.deviceid, this.topic, this.first, this.max);
    }
}
