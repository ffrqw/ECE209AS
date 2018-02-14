package com.shinobicontrols.charts;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateRange extends Range<Date> {
    private static Calendar gz = new GregorianCalendar();

    DateRange() {
        super(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

    public DateRange(Date min, Date max) {
        super((double) min.getTime(), (double) max.getTime());
    }

    private DateRange(double min, double max) {
        super(min, max);
    }

    public Date getMinimum() {
        return new Date((long) this.nv);
    }

    public Date getMaximum() {
        return new Date((long) this.nw);
    }

    public double getSpan() {
        return dF() / 1000.0d;
    }

    Range<Date> bZ() {
        return new DateRange(this.nv, this.nw);
    }
}
