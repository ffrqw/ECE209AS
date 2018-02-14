package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;
import com.rachio.iro.gen2.MrvlProvService;

public class ProvisioningFailureFragment extends BaseProvisioningFragment {
    public static ProvisioningFailureFragment newInstance(String failureReason) {
        ProvisioningFailureFragment fragment = new ProvisioningFailureFragment();
        Bundle args = new Bundle();
        args.putString(MrvlProvService.EXTRA_OUT_REASON, failureReason);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_failed, container, false);
        Button retry = (Button) view.findViewById(R.id.gen2_retry);
        Button goToDashBoard = (Button) view.findViewById(R.id.gen2_gotodashboard);
        ((TextView) view.findViewById(R.id.gen2_failurereason)).setText(getArguments().getString(MrvlProvService.EXTRA_OUT_REASON));
        retry.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProvisioningFailureFragment.this.restart();
            }
        });
        goToDashBoard.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProvisioningFailureFragment.this.getProvActivity().goToDashboardOrNoDevices();
            }
        });
        return view;
    }
}
