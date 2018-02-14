package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.ScheduleJobType;
import com.rachio.iro.ui.newschedulerulepath.views.CheckableTextRowView;
import com.rachio.iro.utils.ScheduleStringUtils;
import java.util.Arrays;

public class IntervalSelectFragment extends BaseSelectListFragment {

    /* renamed from: com.rachio.iro.ui.newschedulerulepath.fragments.IntervalSelectFragment$1 */
    class AnonymousClass1 extends ArrayAdapter<ScheduleJobType> {
        AnonymousClass1(Context x0, int x1, ScheduleJobType[] x2) {
            super(x0, -1, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CheckableTextRowView view = IntervalSelectFragment.this.getRowView(convertView);
            view.setText(((ScheduleJobType) getItem(position)).toReadableString(getContext()));
            return view;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    final int getChoiceMode() {
        return 1;
    }

    public final void updateState(ScheduleRule state) {
        super.updateState(state);
        this.list.setAdapter(new AnonymousClass1(getContext(), -1, state.isFlex() ? ScheduleJobType.flexInterval : ScheduleJobType.intervals));
        if (state.scheduleJobTypes.length == 1) {
            int pos = Arrays.binarySearch(ScheduleJobType.intervals, state.scheduleJobTypes[0]);
            if (pos >= 0) {
                this.list.setItemChecked(pos, true);
            }
        }
        onStateChanged();
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        if (this.list.getCheckedItemPosition() != -1) {
            state.scheduleJobTypes = new ScheduleJobType[]{ScheduleJobType.intervals[this.list.getCheckedItemPosition()]};
            ScheduleStringUtils.buildSummary(getContext(), state);
        }
    }

    public final boolean validate() {
        return this.list.getCheckedItemPosition() != -1;
    }

    protected final int getLayout() {
        return R.layout.fragment_schedulerulewizard_select_interval;
    }
}
