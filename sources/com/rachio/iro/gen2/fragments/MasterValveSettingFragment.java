package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;

public class MasterValveSettingFragment extends BaseProvisioningFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_mastervalve, container, false);
        final Spinner haveMasterValve = (Spinner) view.findViewById(R.id.mastervalve_havemastervalve);
        Button cont = (Button) view.findViewById(R.id.mastervalve_continue);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), R.layout.view_gen2provspinneritem, getResources().getStringArray(R.array.noyes));
        adapter.setDropDownViewResource(17367050);
        haveMasterValve.setAdapter(adapter);
        cont.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean z = true;
                MasterValveSettingFragment masterValveSettingFragment = MasterValveSettingFragment.this;
                if (haveMasterValve.getSelectedItemPosition() != 1) {
                    z = false;
                }
                masterValveSettingFragment.onMasterValveSettingCaptured(z);
            }
        });
        wireUpHelpAndExit(view);
        return view;
    }
}
