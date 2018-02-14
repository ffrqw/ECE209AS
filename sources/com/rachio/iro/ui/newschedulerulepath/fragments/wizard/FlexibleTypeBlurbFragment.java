package com.rachio.iro.ui.newschedulerulepath.fragments.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;

public class FlexibleTypeBlurbFragment extends BaseScheduleRuleFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedulerulewizard_typeblurb_flexible, container, false);
    }
}
