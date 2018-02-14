package com.rachio.iro.ui.newschedulerulepath.fragments.typeblurbs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.newschedulerulepath.activity.ScheduleRuleWizardActivity.Type;
import com.rachio.iro.ui.newschedulerulepath.activity.ViewScheduleActivity;

public abstract class BaseBlurbFragment extends BaseFragment {
    private Type type;

    protected abstract int getLayout();

    public static BaseBlurbFragment createFragmentForType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        BaseBlurbFragment fragment = null;
        switch (type) {
            case FIXEDDAYS:
                fragment = new FixedDaysTypeBlurbFragment();
                break;
            case FIXEDINTERVAL:
                fragment = new FixedIntervalTypeBlurbFragment();
                break;
            case SEASONAL:
                fragment = new MonthlyIntervalTypeBlurbFragment();
                break;
            case SOILBASED:
                fragment = new FlexibleTypeBlurbFragment();
                break;
        }
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.type = (Type) getArguments().getSerializable("type");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        ((Button) view.findViewById(R.id.startwizard)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BaseBlurbFragment.this.startWizard();
            }
        });
        return view;
    }

    protected final void startWizard() {
        ((ViewScheduleActivity) getActivity()).startWizard(this.type);
    }

    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle((CharSequence) "Add Watering Schedule");
    }
}
