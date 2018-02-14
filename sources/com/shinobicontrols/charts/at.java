package com.shinobicontrols.charts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

class at {
    Range<Date> gK;
    private final Calendar gz = new GregorianCalendar();

    at() {
        this.gz.clear();
    }

    List<Range<Date>> a(RepeatedTimePeriod repeatedTimePeriod, Range<Date> range) {
        this.gK = range;
        List<Range<Date>> arrayList = new ArrayList();
        if (Range.h(range)) {
            return arrayList;
        }
        Date b = b(repeatedTimePeriod, (Range) range);
        Date date = (Date) range.getMaximum();
        DateFrequency dateFrequency = repeatedTimePeriod.oj;
        DateFrequency dateFrequency2 = repeatedTimePeriod.ok;
        while (b.before(date)) {
            arrayList.add(new DateRange(b, a(b, dateFrequency)));
            b = a(b, dateFrequency2);
        }
        return arrayList;
    }

    private Date b(RepeatedTimePeriod repeatedTimePeriod, Range<Date> range) {
        Date start = repeatedTimePeriod.getStart();
        Date date = (Date) range.getMinimum();
        DateFrequency frequency = repeatedTimePeriod.getFrequency();
        this.gz.clear();
        this.gz.setTime(start);
        while (start.before(date)) {
            start = a(start, frequency);
        }
        while (start.after(date)) {
            start = b(start, frequency);
        }
        return this.gz.getTime();
    }

    private Date a(Date date, DateFrequency dateFrequency) {
        return a(date, dateFrequency, true);
    }

    private Date b(Date date, DateFrequency dateFrequency) {
        return a(date, dateFrequency, false);
    }

    private Date a(Date date, DateFrequency dateFrequency, boolean z) {
        int i = z ? dateFrequency.gu : -dateFrequency.gu;
        this.gz.setTime(date);
        this.gz.add(dateFrequency.gv.value, i);
        return this.gz.getTime();
    }

    boolean g(Range<Date> range) {
        return this.gK == null || !this.gK.k(range);
    }
}
