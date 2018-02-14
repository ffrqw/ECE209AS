package com.rachio.iro.binder;

public enum ModelViewType {
    UNKNOWN(new UnknownBinder()),
    CONNECTION_STATUS(new DeviceStatusBinder()),
    WATERING_SCHEDULE(new WateringScheduleBinder()),
    MY_YARD(new MyYardBinder()),
    WATER_USE(new WaterUseBinder()),
    LOCAL_WEATHER(new LocalWeatherBinder()),
    CURRENTLY_WATERING(new CurrentlyWateringBinder()),
    WATERING_HISTORY(new WateringHistoryBinder()),
    SCHEDULE_UPDATES(new ScheduleUpdatesBinder()),
    DEVICE_UPDATES(new DeviceUpdatesBinder());
    
    private ModelViewBinder mBinder;

    private ModelViewType(ModelViewBinder binder) {
        this.mBinder = binder;
    }

    public final ModelViewBinder getBinder() {
        return this.mBinder;
    }

    public static ModelViewType fromOrdinal(int ordinal) {
        ModelViewType[] allTypes = (ModelViewType[]) $VALUES.clone();
        if (ordinal < 0 || ordinal >= allTypes.length) {
            return UNKNOWN;
        }
        return allTypes[ordinal];
    }
}
