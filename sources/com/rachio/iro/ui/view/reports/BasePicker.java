package com.rachio.iro.ui.view.reports;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class BasePicker extends LinearLayout {
    protected final Calendar endDate;
    private Listener listener;
    private Date lowerLimit;
    private Calendar month;
    private TextView monthYear;
    private TextView next;
    private TextView prev;
    protected final Calendar startDate;

    public interface Listener {
        void onMonthChanged$ed1f2c9();
    }

    public abstract void clamp(Calendar calendar);

    public abstract void decrement(Calendar calendar);

    public abstract DateFormat getDateFormat();

    public abstract Date getEnd(Calendar calendar);

    public abstract Date getStart(Calendar calendar);

    public abstract long getUpperLimit();

    public abstract void increment(Calendar calendar);

    public BasePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.month = Calendar.getInstance();
        this.startDate = Calendar.getInstance();
        this.endDate = Calendar.getInstance();
        clamp(this.month);
        inflate(context, R.layout.view_monthpicker, this);
        this.prev = (TextView) findViewById(R.id.monthpicker_prev);
        this.monthYear = (TextView) findViewById(R.id.monthpicker_monthyear);
        this.next = (TextView) findViewById(R.id.monthpicker_next);
        this.prev.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BasePicker.this.decrement(BasePicker.this.month);
                BasePicker.this.set();
                if (BasePicker.this.listener != null) {
                    Listener access$200 = BasePicker.this.listener;
                    BasePicker.this.getStart(BasePicker.this.month);
                    BasePicker.this.getEnd(BasePicker.this.month);
                    access$200.onMonthChanged$ed1f2c9();
                }
            }
        });
        this.next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BasePicker.this.increment(BasePicker.this.month);
                BasePicker.this.set();
                if (BasePicker.this.listener != null) {
                    Listener access$200 = BasePicker.this.listener;
                    BasePicker.this.getStart(BasePicker.this.month);
                    BasePicker.this.getEnd(BasePicker.this.month);
                    access$200.onMonthChanged$ed1f2c9();
                }
            }
        });
        set();
    }

    public BasePicker(Context context) {
        this(context, null);
    }

    private void updateArrows() {
        int i = 4;
        this.next.setVisibility(getUpperLimit() == getEnd(this.month).getTime() ? 4 : 0);
        if (this.lowerLimit != null) {
            TextView textView = this.prev;
            if (getStart(this.month).getTime() != this.lowerLimit.getTime()) {
                i = 0;
            }
            textView.setVisibility(i);
        }
    }

    private void set() {
        this.monthYear.setText(getDateFormat().format(this.month.getTime()));
        updateArrows();
    }

    public final void setMonthAndYear(int month, int year) {
        this.month.set(year, month, 1);
        clamp(this.month);
        set();
    }

    public final void setListener(Listener listener) {
        this.listener = listener;
    }

    public final Date getStart() {
        return getStart(this.month);
    }

    public final Date getEnd() {
        return getEnd(this.month);
    }

    public final void setLowerLimit(Date lowerLimit) {
        Calendar c = Calendar.getInstance();
        c.setTime(lowerLimit);
        clamp(c);
        this.lowerLimit = c.getTime();
        updateArrows();
    }
}
