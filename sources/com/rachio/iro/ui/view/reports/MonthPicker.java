package com.rachio.iro.ui.view.reports;

import android.content.Context;
import android.util.AttributeSet;
import com.rachio.iro.utils.CalendarUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MonthPicker extends BasePicker {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM y", Locale.US);

    public final DateFormat getDateFormat() {
        return dateFormat;
    }

    public final void increment(Calendar calendar) {
        calendar.add(2, 1);
    }

    public final void decrement(Calendar calendar) {
        calendar.add(2, -1);
    }

    public MonthPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MonthPicker(Context context) {
        this(context, null);
    }

    public final Date getStart(Calendar calendar) {
        this.startDate.setTime(calendar.getTime());
        CalendarUtil.setToStartOfMonth(this.startDate);
        return this.startDate.getTime();
    }

    public final Date getEnd(Calendar calendar) {
        this.endDate.setTime(calendar.getTime());
        CalendarUtil.setToEndOfMonth(this.endDate);
        return this.endDate.getTime();
    }

    public final long getUpperLimit() {
        Calendar endOfThisMonth = Calendar.getInstance();
        CalendarUtil.setToEndOfMonth(endOfThisMonth);
        return endOfThisMonth.getTimeInMillis();
    }

    public final void clamp(Calendar calendar) {
        CalendarUtil.setToStartOfMonth(calendar);
    }
}
