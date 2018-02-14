package com.rachio.iro.model;

import java.io.Serializable;

public class EventData extends ModelObject implements Serializable {
    private static final long serialVersionUID = 1;
    public String convertedValue;
    public long createDate;
    public DeltaContainer deltaContainer;
    public String id;
    public String key;
    public long lastUpdateDate;

    public static class DeltaContainer {
        public Delta[] deltas;

        public static class Delta {
            public Object newValue;
            public Object oldValue;
            public String propertyName;
        }
    }
}
