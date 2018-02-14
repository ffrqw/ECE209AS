package com.rachio.iro.model.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.TransmittableView;
import java.io.Serializable;
import java.util.Date;

public class Delay implements Serializable {
    private static final long serialVersionUID = 1;
    public Date createDate;
    @JsonView({TransmittableView.class})
    public long duration;
    @JsonView({TransmittableView.class})
    public boolean enabled;
    public long id;
    public Date lastUpdateDate;
    @JsonView({TransmittableView.class})
    public double threshold;
    @JsonView({TransmittableView.class})
    public Type type = Type.SCHEDULE;

    public enum DelayDuration {
        NONE(0),
        SCHEDULEDURATION(-1),
        TWELVEHOURS(43200000),
        ONEDAY(86400000),
        TWODAYS(172800000),
        THREEDAYS(259200000),
        FOURDAYS(345600000),
        FIVEDAYS(432000000),
        SIXDAYS(518400000),
        SEVENDAYS(604800000);
        
        public final long millis;

        private DelayDuration(long millis) {
            this.millis = millis;
        }

        public static DelayDuration getDurationFromMillis(long millis) {
            DelayDuration duration = NONE;
            for (DelayDuration dd : values()) {
                if (dd.millis == millis) {
                    return dd;
                }
            }
            return duration;
        }

        public static DelayDuration[] selectableValues() {
            DelayDuration[] values = values();
            DelayDuration[] selectable = new DelayDuration[(values.length - 1)];
            System.arraycopy(values, 1, selectable, 0, selectable.length);
            return selectable;
        }
    }

    public enum Type {
        SCHEDULE,
        DURATION
    }

    @JsonIgnore
    public void setFromDuration(DelayDuration duration) {
        switch (duration) {
            case SCHEDULEDURATION:
                this.type = Type.SCHEDULE;
                this.duration = 0;
                return;
            case NONE:
                return;
            default:
                this.type = Type.DURATION;
                this.duration = duration.millis;
                return;
        }
    }

    @JsonIgnore
    public DelayDuration getDelayDuration() {
        if (this.type == Type.SCHEDULE) {
            return DelayDuration.SCHEDULEDURATION;
        }
        if (this.type == Type.DURATION) {
            return DelayDuration.getDurationFromMillis(this.duration);
        }
        return DelayDuration.NONE;
    }
}
