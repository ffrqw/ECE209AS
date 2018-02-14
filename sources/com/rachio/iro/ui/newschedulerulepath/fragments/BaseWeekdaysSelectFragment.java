package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.ScheduleJobType;
import com.rachio.iro.ui.newschedulerulepath.views.CheckableTextRowView;
import com.rachio.iro.utils.DateFormats;
import com.rachio.iro.utils.ScheduleStringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BaseWeekdaysSelectFragment extends BaseSelectListFragment {

    /* renamed from: com.rachio.iro.ui.newschedulerulepath.fragments.BaseWeekdaysSelectFragment$1 */
    class AnonymousClass1 extends ArrayAdapter<String> {
        AnonymousClass1(Context x0, int x1, String[] x2) {
            super(x0, -1, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CheckableTextRowView view = BaseWeekdaysSelectFragment.this.getRowView(convertView);
            view.setText((String) getItem(position));
            return view;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        String[] dayNames = new String[7];
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(7, cal.getFirstDayOfWeek());
        for (int i = 0; i < 7; i++) {
            dayNames[i] = DateFormats.dayOfWeek.format(cal.getTime());
            cal.add(7, 1);
        }
        this.list.setAdapter(new AnonymousClass1(getContext(), -1, dayNames));
        onStateChanged();
        return view;
    }

    public final boolean validate() {
        return this.list.getCheckedItemCount() > 0;
    }

    public final void updateStateFromJobTypes(ScheduleRule state) {
        for (ScheduleJobType jobType : state.scheduleJobTypes) {
            if (Arrays.binarySearch(ScheduleRule.weekdays, jobType) > 0 && jobType != ScheduleJobType.ANY) {
                this.list.setItemChecked(ScheduleRule.getWeekdayPosForJobType(jobType), true);
            }
        }
    }

    public final void commitStateToJobTypes(ScheduleRule state) {
        SparseBooleanArray checked = this.list.getCheckedItemPositions();
        ArrayList<ScheduleJobType> jobTypes = new ArrayList();
        for (int i = 0; i < 7; i++) {
            if (checked.get(i)) {
                jobTypes.add(ScheduleRule.weekdays[i + 1]);
            }
        }
        state.scheduleJobTypes = (ScheduleJobType[]) jobTypes.toArray(new ScheduleJobType[jobTypes.size()]);
        ScheduleStringUtils.buildSummary(getContext(), state);
    }
}
