package com.rachio.iro.ui.newschedulerulepath.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.model.schedule.PrecipDelay.RainDelayThreshold;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.user.User.DisplayUnit;
import com.rachio.iro.ui.newschedulerulepath.views.CheckableTextRowView;

public class RainDelaySelectFragment extends BaseDelayFragment {

    /* renamed from: com.rachio.iro.ui.newschedulerulepath.fragments.RainDelaySelectFragment$1 */
    class AnonymousClass1 extends ArrayAdapter<RainDelayThreshold> {
        final /* synthetic */ String[] val$strings;

        AnonymousClass1(Context x0, int x1, RainDelayThreshold[] x2, String[] strArr) {
            this.val$strings = strArr;
            super(x0, -1, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CheckableTextRowView view = RainDelaySelectFragment.this.getRowView(convertView);
            view.setText(this.val$strings[position]);
            return view;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String[] strings;
        View view = super.onCreateView(inflater, container, savedInstanceState);
        IroApplication application = IroApplication.get(getContext());
        if (User.getLoggedInUser(application.getDatabase(), application.getPrefsWrapper()).displayUnit == DisplayUnit.METRIC) {
            strings = getResources().getStringArray(R.array.raindelaythresholds_metric);
        } else {
            strings = getResources().getStringArray(R.array.raindelaythresholds_imperial);
        }
        this.list.setAdapter(new AnonymousClass1(getContext(), -1, RainDelayThreshold.values(), strings));
        onStateChanged();
        return view;
    }

    protected final String getDescription() {
        return "How much rain would you like to trigger a rain skip?";
    }

    public final void updateState(ScheduleRule state) {
        this.enabled.setChecked(state.precipDelay.enabled);
        this.list.setItemChecked(state.precipDelay.getThreshold().ordinal(), true);
        super.updateState(state);
    }

    public final void commitState(ScheduleRule state) {
        super.commitState(state);
        state.precipDelay.enabled = this.enabled.isChecked();
        state.precipDelay.setThreshold(RainDelayThreshold.values()[this.list.getCheckedItemPosition()]);
    }
}
