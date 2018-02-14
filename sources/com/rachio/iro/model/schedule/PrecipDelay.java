package com.rachio.iro.model.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

public class PrecipDelay extends Delay implements Serializable {
    private static final long serialVersionUID = 1;

    public enum RainDelayThreshold {
        SIXTEENTH(0.0625d),
        EIGHT(0.125d),
        QUARTER(0.25d),
        HALF(0.5d),
        THREEQUARTERS(0.75d),
        ONE(1.0d);
        
        public final double threshold;

        private RainDelayThreshold(double threshold) {
            this.threshold = threshold;
        }

        public static RainDelayThreshold getThresholdFromThresholdValue(double value) {
            RainDelayThreshold threshold = SIXTEENTH;
            for (RainDelayThreshold rdt : values()) {
                if (rdt.threshold == value) {
                    return rdt;
                }
            }
            return threshold;
        }
    }

    public PrecipDelay() {
        this.enabled = true;
        this.threshold = RainDelayThreshold.EIGHT.threshold;
    }

    @JsonIgnore
    public RainDelayThreshold getThreshold() {
        return RainDelayThreshold.getThresholdFromThresholdValue(this.threshold);
    }

    @JsonIgnore
    public void setThreshold(RainDelayThreshold threshold) {
        this.threshold = threshold.threshold;
    }
}
