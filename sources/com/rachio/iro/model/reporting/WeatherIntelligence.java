package com.rachio.iro.model.reporting;

import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.annotation.TimeToLive;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@TimeToLive(timeToLive = 86400000)
public class WeatherIntelligence extends ModelObject implements Serializable {
    private static final long serialVersionUID = 1;
    public List<Result> result = new ArrayList();

    public static final class Result implements Serializable {
        private static final long serialVersionUID = 1;
        public TimeFrame timeframe;
        public Value value;
    }

    public static final class Value implements Serializable {
        private static final long serialVersionUID = 1;
        public double totalRainDelayTime;
        public int totalRainDelays;
        public double totalWaterSaved;
    }
}
