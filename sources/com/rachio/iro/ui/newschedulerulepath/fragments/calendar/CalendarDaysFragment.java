package com.rachio.iro.ui.newschedulerulepath.fragments.calendar;

import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.model.WateringScheduleType;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.newschedulerulepath.activity.ViewScheduleActivity;
import com.rachio.iro.ui.newschedulerulepath.views.WateringScheduleCell;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarDaysFragment extends BaseFragment {
    private GridLayout calendar;
    private OnClickListener cellOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            ((ViewScheduleActivity) CalendarDaysFragment.this.getActivity()).selectDay(((WateringScheduleCell) v).timeMillis);
        }
    };
    private TimeZone deviceTimeZone;
    private Calendar end;
    private int month;
    private Calendar start;

    public static CalendarDaysFragment newInstance(TimeZone deviceTimeZone, int month, long start, long end) {
        CalendarDaysFragment fragment = new CalendarDaysFragment();
        Bundle args = new Bundle();
        args.putSerializable("devicetimezone", deviceTimeZone);
        args.putInt("month", month);
        args.putLong("start", start);
        args.putLong("end", end);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.deviceTimeZone = (TimeZone) args.getSerializable("devicetimezone");
        this.month = args.getInt("month");
        long start = args.getLong("start");
        long end = args.getLong("end");
        this.start = Calendar.getInstance(this.deviceTimeZone);
        this.end = Calendar.getInstance(this.deviceTimeZone);
        this.start.setTimeInMillis(start);
        this.end.setTimeInMillis(end);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.calendar = (GridLayout) inflater.inflate(R.layout.fragment_calendardays, container, false);
        return this.calendar;
    }

    public void onResume() {
        super.onResume();
        Calendar today = Calendar.getInstance(this.deviceTimeZone);
        Calendar currentDay = Calendar.getInstance(this.deviceTimeZone);
        currentDay.setTime(this.start.getTime());
        for (int c = 0; c < this.calendar.getChildCount(); c++) {
            boolean isToday;
            boolean z;
            if (currentDay.get(1) == today.get(1) && currentDay.get(6) == today.get(6)) {
                isToday = true;
            } else {
                isToday = false;
            }
            WateringScheduleCell cell = (WateringScheduleCell) this.calendar.getChildAt(c);
            cell.setOnClickListener(this.cellOnClickListener);
            cell.setText(String.valueOf(currentDay.get(5)));
            cell.timeMillis = currentDay.getTimeInMillis();
            cell.set(WateringScheduleType.NONE);
            if (currentDay.get(2) != this.month) {
                z = true;
            } else {
                z = false;
            }
            cell.isDifferentMonth = z;
            cell.isToday = isToday;
            cell.refreshDrawableState();
            currentDay.add(6, 1);
            ((ViewScheduleActivity) getActivity()).loadCell(cell);
        }
    }
}
