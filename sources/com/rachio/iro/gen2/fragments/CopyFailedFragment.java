package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;
import com.rachio.iro.gen2.MrvlProvService;

public class CopyFailedFragment extends BaseProvisioningFragment {
    public static CopyFailedFragment newInstance(String deviceId) {
        CopyFailedFragment fragment = new CopyFailedFragment();
        Bundle args = new Bundle();
        args.putString(MrvlProvService.EXTRA_OUT_DEVICEID, deviceId);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_copyfailed, container, false);
        Button tryAgain = (Button) view.findViewById(R.id.copyfailed_tryagain);
        Button cont = (Button) view.findViewById(R.id.copyfailed_continue);
        final String deviceId = getArguments().getString(MrvlProvService.EXTRA_OUT_DEVICEID);
        tryAgain.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CopyFailedFragment.this.copyDevice(deviceId);
            }
        });
        cont.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CopyFailedFragment.this.setupDevice();
            }
        });
        return view;
    }
}
