package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;

public class EnableWifiFragment extends BaseProvisioningFragment {
    public static EnableWifiFragment newInstance() {
        return new EnableWifiFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_gen2_enablewifi, container, false);
    }
}
