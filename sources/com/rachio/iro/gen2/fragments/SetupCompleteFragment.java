package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;

public class SetupCompleteFragment extends BaseProvisioningFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_setupcomplete, container, false);
        Button setupZones = (Button) view.findViewById(R.id.setupcomplete_setupzones);
        ((Button) view.findViewById(R.id.setupcomplete_gotodashboard)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SetupCompleteFragment.this.getProvActivity().goToDashboardOrNoDevices();
            }
        });
        setupZones.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SetupCompleteFragment.this.setupZones();
            }
        });
        return view;
    }
}
