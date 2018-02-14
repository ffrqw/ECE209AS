package com.rachio.iro.ui.newschedulerulepath.fragments.calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchCalendarCommand.ScheduleCalendarMeta;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.newschedulerulepath.activity.ViewScheduleActivity;
import com.rachio.iro.ui.newschedulerulepath.adapters.ScheduleListAdapter;
import com.rachio.iro.utils.CalendarUtil;
import com.rachio.iro.utils.DateFormats;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class CalendarFragment extends BaseCalendarFragment {
    private ActionMode actionMode;
    private ScheduleListAdapter adapter;
    private TextView calendarMonth;
    private TextView calendarYear;
    private ViewPager days;
    private TimeZone deviceTimeZone;
    private ListView list;
    private Callback mActionModeCallback = new Callback() {
        public final boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.snippet_delete, menu);
            SparseBooleanArray checkedItems = CalendarFragment.this.list.getCheckedItemPositions();
            if (checkedItems.size() == 1 && !((ScheduleRule) CalendarFragment.this.list.getItemAtPosition(checkedItems.keyAt(0))).isFlex()) {
                inflater.inflate(R.menu.snippet_run, menu);
            }
            return true;
        }

        public final boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public final boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            ViewScheduleActivity activity = CalendarFragment.this.getViewScheduleActivity();
            if (activity == null) {
                return false;
            }
            ScheduleRule sr;
            switch (item.getItemId()) {
                case R.id.action_delete:
                    List<ScheduleRule> selectedRules = new ArrayList();
                    SparseBooleanArray checkedItems = CalendarFragment.this.list.getCheckedItemPositions();
                    for (int i = 0; i < CalendarFragment.this.list.getCount(); i++) {
                        if (checkedItems.get(i)) {
                            selectedRules.add((ScheduleRule) CalendarFragment.this.list.getItemAtPosition(i));
                        }
                    }
                    mode.finish();
                    for (ScheduleRule sr2 : selectedRules) {
                        activity.deleteRule(sr2, false);
                    }
                    return true;
                case R.id.action_run:
                    sr2 = (ScheduleRule) CalendarFragment.this.list.getItemAtPosition(CalendarFragment.this.list.getCheckedItemPosition());
                    mode.finish();
                    activity.runRule(sr2, CalendarFragment.this.prefsWrapper.getLoggedInUserId());
                    return true;
                default:
                    return false;
            }
        }

        public final void onDestroyActionMode(ActionMode mode) {
            CalendarFragment.this.list.clearChoices();
            ((ArrayAdapter) CalendarFragment.this.list.getAdapter()).notifyDataSetChanged();
            CalendarFragment.this.actionMode = null;
        }
    };
    private ImageView next;
    private ImageView prev;

    public static CalendarFragment newInstance(TimeZone deviceTimeZone, ArrayList<ScheduleRule> rules, TreeMap<String, Zone> zonesMap) {
        Bundle args = new Bundle();
        args.putSerializable("devicetimezone", deviceTimeZone);
        args.putSerializable("rules", rules);
        args.putSerializable("zoneMap", zonesMap);
        CalendarFragment fragment = new CalendarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.deviceTimeZone = (TimeZone) getArguments().getSerializable("devicetimezone");
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.snippet_add, menu);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_calendar, container, false);
        this.prev = (ImageView) view.findViewById(R.id.watering_schedule_prev);
        this.calendarMonth = (TextView) view.findViewById(R.id.watering_schedule_header_month);
        this.calendarYear = (TextView) view.findViewById(R.id.watering_schedule_header_year);
        this.next = (ImageView) view.findViewById(R.id.watering_schedule_next);
        this.days = (ViewPager) view.findViewById(R.id.calendar_days);
        this.list = (ListView) view.findViewById(R.id.schedule_list);
        this.prev.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CalendarFragment.access$200(CalendarFragment.this, -1);
            }
        });
        this.next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CalendarFragment.access$200(CalendarFragment.this, 1);
            }
        });
        this.days.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            public final Fragment getItem(int position) {
                Calendar c = Calendar.getInstance(CalendarFragment.this.deviceTimeZone);
                c.add(2, position);
                int month = c.get(2);
                CalendarUtil.setToSundayBeforeStartOfMonth(c);
                long start = c.getTimeInMillis();
                c = Calendar.getInstance();
                c.add(2, position);
                CalendarUtil.setToSaturdayAfterEndOfMonth(c);
                return CalendarDaysFragment.newInstance(CalendarFragment.this.deviceTimeZone, month, start, c.getTimeInMillis());
            }

            public final int getCount() {
                return 12;
            }
        });
        this.days.setCurrentItem(0, false);
        onMonthChanged(0);
        this.days.addOnPageChangeListener(new OnPageChangeListener() {
            public final void onPageSelected(int position) {
                CalendarFragment.this.onMonthChanged(position);
            }

            public final void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public final void onPageScrollStateChanged(int state) {
            }
        });
        this.list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                if (CalendarFragment.this.actionMode != null) {
                    CalendarFragment.this.actionMode.finish();
                }
                CalendarFragment.this.getViewScheduleActivity().editRule((ScheduleRule) adapter.getAdapter().getItem(position));
            }
        });
        this.list.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (CalendarFragment.this.actionMode != null) {
                    return false;
                }
                CalendarFragment.this.list.setItemChecked(position, true);
                CalendarFragment.this.actionMode = ((BaseActivity) CalendarFragment.this.getActivity()).startSupportActionMode(CalendarFragment.this.mActionModeCallback);
                return true;
            }
        });
        Bundle args = getArguments();
        setRules((List) args.getSerializable("rules"), (Map) args.getSerializable("zoneMap"));
        return view;
    }

    private void updateHeaders() {
        boolean z;
        boolean z2 = true;
        Calendar month = Calendar.getInstance(this.deviceTimeZone);
        month.add(2, this.days.getCurrentItem());
        ImageView imageView = this.prev;
        if (this.days.getCurrentItem() != 0) {
            z = true;
        } else {
            z = false;
        }
        imageView.setEnabled(z);
        ImageView imageView2 = this.next;
        if (this.days.getCurrentItem() + 1 == this.days.getAdapter().getCount()) {
            z2 = false;
        }
        imageView2.setEnabled(z2);
        this.calendarMonth.setText(DateFormats.month.format(month.getTime()));
        this.calendarYear.setText(DateFormats.year.format(month.getTime()));
    }

    public final void setCalendar(ScheduleCalendarMeta calendar) {
        super.setCalendar(calendar);
        updateHeaders();
        ArrayAdapter currentAdapter = (ArrayAdapter) this.list.getAdapter();
        if (currentAdapter != null) {
            currentAdapter.notifyDataSetInvalidated();
        }
    }

    public final void setRules(List<ScheduleRule> rules, Map<String, Zone> zonesMap) {
        super.setRules(rules, zonesMap);
        this.adapter = new ScheduleListAdapter(getContext(), (List) rules, (Map) zonesMap);
        this.list.setAdapter(this.adapter);
    }

    private void onMonthChanged(int newMonth) {
        getViewScheduleActivity().onMonthChanged(newMonth);
        updateHeaders();
    }

    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle((int) R.string.title_activity_schedule);
    }

    static /* synthetic */ void access$200(CalendarFragment x0, int x1) {
        int currentItem = x0.days.getCurrentItem() + x1;
        x0.onMonthChanged(currentItem);
        x0.days.setCurrentItem(currentItem, true);
    }
}
