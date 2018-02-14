package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.FreezeDelay.FreezeDelayThreshold;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.user.User.DisplayUnit;
import com.rachio.iro.ui.newschedulerulepath.views.CheckableTextRowView;

public class FreezeDelaySelectFragment extends BaseDelayFragment {
    private FreezeDelayThreshold[] values;

    /* renamed from: com.rachio.iro.ui.newschedulerulepath.fragments.FreezeDelaySelectFragment$1 */
    class AnonymousClass1 extends ArrayAdapter<FreezeDelayThreshold> {
        final /* synthetic */ String[] val$strings;

        AnonymousClass1(Context x0, int x1, FreezeDelayThreshold[] x2, String[] strArr) {
            this.val$strings = strArr;
            super(x0, -1, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CheckableTextRowView view = FreezeDelaySelectFragment.this.getRowView(convertView);
            view.setText(this.val$strings[position]);
            return view;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        IroApplication application = IroApplication.get(getContext());
        User user = User.getLoggedInUser(application.getDatabase(), application.getPrefsWrapper());
        String[] strings = user.displayUnit == DisplayUnit.METRIC ? getResources().getStringArray(R.array.freezedelaythreshold_metric) : getResources().getStringArray(R.array.freezedelaythreshold_imperial);
        this.values = user.displayUnit == DisplayUnit.METRIC ? FreezeDelayThreshold.metricValues : FreezeDelayThreshold.imperialValues;
        this.list.setAdapter(new AnonymousClass1(getContext(), -1, this.values, strings));
        onStateChanged();
        return view;
    }

    public final void updateState(ScheduleRule state) {
        this.enabled.setChecked(state.freezeDelay.enabled);
        FreezeDelayThreshold threshold = state.freezeDelay.getThreshold();
        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i] == threshold) {
                this.list.setItemChecked(i, true);
            }
        }
        super.updateState(state);
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        state.freezeDelay.enabled = this.enabled.isChecked();
        state.freezeDelay.setFromThreshold(this.values[this.list.getCheckedItemPosition()]);
    }

    protected final String getDescription() {
        return "What temperature would you like to trigger a freeze skip?";
    }
}
