package com.rachio.iro.ui.newschedulerulepath.fragments.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchCalendarCommand.ScheduleCalendarMeta;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.newschedulerulepath.adapters.ScheduleListAdapter;
import com.rachio.iro.utils.DateFormats;
import com.rachio.iro.utils.Duration;
import java.util.ArrayList;
import java.util.Date;

public class SelectedDayFragment extends BaseCalendarFragment {
    private ListView list;
    private TextView rainDelayHeader;
    private long selectedDay;

    public static SelectedDayFragment newInstance(long selectedDay) {
        Bundle args = new Bundle();
        args.putLong("selecteday", selectedDay);
        SelectedDayFragment fragment = new SelectedDayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.selectedDay = getArguments().getLong("selecteday");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_selectedday, container, false);
        TextView selectedDateText = (TextView) view.findViewById(R.id.schedule_selecteddatetext);
        ImageView selectedDateCancel = (ImageView) view.findViewById(R.id.schedule_selecteddatecancel);
        this.rainDelayHeader = (TextView) view.findViewById(R.id.viewschedule_raindelayheader);
        this.list = (ListView) view.findViewById(R.id.schedule_list);
        selectedDateText.setText(DateFormats.getDayOfWeekMonthAndDate.format(Long.valueOf(this.selectedDay)));
        selectedDateCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SelectedDayFragment.this.getFragmentManager().popBackStack();
            }
        });
        this.list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedDayFragment.this.getViewScheduleActivity().editRule((ScheduleRule) parent.getItemAtPosition(position));
            }
        });
        return view;
    }

    public final void setCalendar(ScheduleCalendarMeta scheduleCalendar) {
        boolean z;
        super.setCalendar(scheduleCalendar);
        if (scheduleCalendar.rainDelayStart == null || scheduleCalendar.rainDelayEnd == null) {
            z = false;
        } else {
            z = Duration.isContainedWithin(scheduleCalendar.rainDelayStart, scheduleCalendar.rainDelayEnd, new Date());
        }
        if (!z || this.selectedDay <= scheduleCalendar.rainDelayStart.getTime() || this.selectedDay >= scheduleCalendar.rainDelayEnd.getTime()) {
            this.rainDelayHeader.setVisibility(8);
        } else {
            this.rainDelayHeader.setText("Rain delay until " + DateFormats.dayMonthAndtime.format(scheduleCalendar.rainDelayEnd));
            this.rainDelayHeader.setVisibility(0);
        }
        ScheduleListAdapter adapter = new ScheduleListAdapter(getContext(), new ArrayList(scheduleCalendar.scheduleRules), scheduleCalendar);
        adapter.getFilter().filter(Long.toString(this.selectedDay));
        this.list.setAdapter(adapter);
    }
}
