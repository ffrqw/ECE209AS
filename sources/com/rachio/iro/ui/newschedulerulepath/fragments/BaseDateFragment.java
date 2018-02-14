package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class BaseDateFragment extends BaseScheduleRuleFragment implements OnDateChangedListener {
    protected boolean changed = false;
    protected DatePicker datePicker;
    protected TimeZone deviceTimeZone;
    protected boolean settingDate = false;

    protected final synchronized void setDatePickerFromDate(Date date) {
        this.settingDate = true;
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        this.datePicker.init(calendar.get(1), calendar.get(2), calendar.get(5), this);
        this.datePicker.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        this.settingDate = false;
    }

    protected final Date getDateFromDatePicker() {
        Calendar calendar = Calendar.getInstance(this.deviceTimeZone);
        calendar.set(this.datePicker.getYear(), this.datePicker.getMonth(), this.datePicker.getDayOfMonth());
        return calendar.getTime();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        this.datePicker = (DatePicker) view.findViewById(R.id.datepicker);
        setDatePickerFromDate(null);
        return view;
    }

    public synchronized void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (!(VERSION.SDK_INT < 23 || this.settingDate || this.changed)) {
            this.changed = true;
            moveToNextStage();
        }
    }

    protected int getLayout() {
        return R.layout.fragment_schedulerulewizard_date;
    }

    public void updateState(ScheduleRule entity) {
        super.updateState(entity);
        this.deviceTimeZone = entity.device.getTimeZoneAsTimeZone();
    }
}
