package com.shinobicontrols.charts;

import java.util.Date;

public class RepeatedTimePeriod {
    final Date oi;
    final DateFrequency oj;
    final DateFrequency ok;

    public RepeatedTimePeriod(Date start, DateFrequency length, DateFrequency frequency) {
        if (start == null) {
            throw new IllegalArgumentException("TimePeriod cannot have null start date.");
        } else if (length == null) {
            throw new IllegalArgumentException("TimePeriod cannot have null lenth.");
        } else if (frequency == null) {
            throw new IllegalArgumentException("RepeatedTimePeriod cannot have null frequency.");
        } else {
            this.oi = start;
            this.oj = length;
            this.ok = frequency;
        }
    }

    public Date getStart() {
        return this.oi;
    }

    public DateFrequency getLength() {
        return this.oj;
    }

    public DateFrequency getFrequency() {
        return this.ok;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepeatedTimePeriod)) {
            return false;
        }
        RepeatedTimePeriod repeatedTimePeriod = (RepeatedTimePeriod) o;
        if (this.oi.equals(repeatedTimePeriod.oi) && this.oj.equals(repeatedTimePeriod.oj) && this.ok.equals(repeatedTimePeriod.ok)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((this.oi.hashCode() + 527) * 31) + this.oj.hashCode()) * 31) + this.ok.hashCode();
    }
}
