package com.rachio.iro.ui.newschedulerulepath.fragments.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.gen2.MrvlProvService;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.newschedulerulepath.activity.ScheduleRuleWizardActivity.Type;
import com.rachio.iro.ui.newschedulerulepath.activity.ViewScheduleActivity;
import com.rachio.iro.utils.OpacityUtil;

public class NewRuleTypeFragment extends BaseFragment {
    private boolean canCreateFixed;
    private boolean canCreateFlex;
    private String deviceId;

    private final class ScheduleTypeClickListener implements OnClickListener {
        private final Type type;

        public ScheduleTypeClickListener(Type type) {
            this.type = type;
        }

        public final void onClick(View v) {
            ((ViewScheduleActivity) NewRuleTypeFragment.this.getActivity()).showTypeBlurb(this.type);
        }
    }

    public static NewRuleTypeFragment newInstance(String deviceId, boolean canCreateFixed, boolean canCreateFlex) {
        Bundle args = new Bundle();
        args.putString(MrvlProvService.EXTRA_OUT_DEVICEID, deviceId);
        args.putBoolean("cancreatefixed", canCreateFixed);
        args.putBoolean("cancreateflex", canCreateFlex);
        NewRuleTypeFragment fragment = new NewRuleTypeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.deviceId = args.getString(MrvlProvService.EXTRA_OUT_DEVICEID);
        this.canCreateFixed = args.getBoolean("cancreatefixed");
        this.canCreateFlex = args.getBoolean("cancreateflex");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View popupContent = inflater.inflate(R.layout.fragment_scheduletype, null);
        View fixedInterval = popupContent.findViewById(R.id.scheduletype_fixedinterval);
        View fixedDays = popupContent.findViewById(R.id.scheduletype_fixeddays);
        View seasonal = popupContent.findViewById(R.id.scheduletype_seasonal);
        View soilBased = popupContent.findViewById(R.id.scheduletype_soilbased);
        View noMoreFlex = popupContent.findViewById(R.id.scheduletype_nomoreflex);
        if (this.canCreateFixed) {
            fixedInterval.setOnClickListener(new ScheduleTypeClickListener(Type.FIXEDINTERVAL));
            fixedDays.setOnClickListener(new ScheduleTypeClickListener(Type.FIXEDDAYS));
            seasonal.setOnClickListener(new ScheduleTypeClickListener(Type.SEASONAL));
        } else {
            OpacityUtil.makeViewLookDisabled(fixedInterval);
            OpacityUtil.makeViewLookDisabled(fixedDays);
            OpacityUtil.makeViewLookDisabled(seasonal);
        }
        if (this.canCreateFlex) {
            soilBased.setOnClickListener(new ScheduleTypeClickListener(Type.SOILBASED));
        } else {
            OpacityUtil.makeViewLookDisabled(soilBased);
            noMoreFlex.setVisibility(0);
        }
        return popupContent;
    }

    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle((CharSequence) "Add Watering Schedule");
    }
}
