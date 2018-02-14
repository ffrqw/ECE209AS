package com.rachio.iro.model.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rachio.iro.utils.FuzzyEquality;
import java.io.Serializable;

public class FreezeDelay extends Delay implements Serializable {
    private static final long serialVersionUID = 1;

    public enum FreezeDelayThreshold {
        THIRTYTWO(32.0d),
        THIRTYTHREE(33.0d),
        THIRTYTHREEPOINTEIGHT(33.8d),
        THIRTYFOUR(34.0d),
        THIRTYFIVE(35.0d),
        THIRTYFIVEPOINTSIX(35.6d),
        THIRTYSIX(36.0d),
        THIRTYSEVEN(37.0d),
        THIRTYSEVENPOINTFOUR(37.4d),
        THIRTYEIGHT(38.0d),
        THIRTYNINE(39.0d),
        THIRTYNINEPOINTTWO(39.2d),
        FORTY(40.0d),
        FORTYONE(41.0d),
        FORTYTWO(42.0d),
        FORTYTWOPOINTEIGHT(42.8d),
        FORTYTHREE(43.0d),
        FORTYFOUR(44.0d),
        FORTYFOURPOINTSIX(44.6d),
        FORTYFIVE(45.0d);
        
        public static final FreezeDelayThreshold[] imperialValues = null;
        public static final FreezeDelayThreshold[] metricValues = null;
        public final double value;

        static {
            imperialValues = new FreezeDelayThreshold[]{THIRTYTWO, THIRTYTHREE, THIRTYFOUR, THIRTYFIVE, THIRTYSIX, THIRTYSEVEN, THIRTYEIGHT, THIRTYNINE, FORTY, FORTYONE, FORTYTWO, FORTYTHREE, FORTYFOUR, FORTYFIVE};
            metricValues = new FreezeDelayThreshold[]{THIRTYTWO, THIRTYTHREEPOINTEIGHT, THIRTYFIVEPOINTSIX, THIRTYSEVENPOINTFOUR, THIRTYNINEPOINTTWO, FORTYONE, FORTYTWOPOINTEIGHT, FORTYFOURPOINTSIX};
        }

        private FreezeDelayThreshold(double value) {
            this.value = value;
        }
    }

    public FreezeDelay() {
        this.enabled = true;
        this.threshold = FreezeDelayThreshold.THIRTYTWO.value;
    }

    @JsonIgnore
    public FreezeDelayThreshold getThreshold() {
        int i = 0;
        for (FreezeDelayThreshold fdt : FreezeDelayThreshold.values()) {
            FreezeDelayThreshold fdt2;
            if (fdt2.value == this.threshold) {
                return fdt2;
            }
        }
        FreezeDelayThreshold[] values = FreezeDelayThreshold.values();
        int length = values.length;
        while (i < length) {
            fdt2 = values[i];
            if (FuzzyEquality.fuzzyEqual(fdt2.value, this.threshold)) {
                return fdt2;
            }
            i++;
        }
        return FreezeDelayThreshold.THIRTYTWO;
    }

    @JsonIgnore
    public void setFromThreshold(FreezeDelayThreshold threshold) {
        this.threshold = threshold.value;
    }
}
