package com.rachio.iro.binder;

public class ScheduleUpdatesBinder extends BaseEventBinder {
    protected final String getTitle() {
        return "Schedule Updates";
    }

    protected final String getTopic() {
        return "SCHEDULE";
    }
}
