package com.rachio.iro.binder;

public class WateringHistoryBinder extends BaseEventBinder {
    protected final String getTitle() {
        return "Watering Updates";
    }

    protected final String getTopic() {
        return "WATERING";
    }
}
