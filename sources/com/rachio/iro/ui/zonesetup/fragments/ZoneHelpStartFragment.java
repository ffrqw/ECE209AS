package com.rachio.iro.ui.zonesetup.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.ui.zonesetup.ZoneHelpActivity;

public class ZoneHelpStartFragment extends BaseZoneHelpFragment {
    private Button startButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zone_help_start, parent, false);
        wireUpHelpExit(view);
        this.startButton = (Button) view.findViewById(R.id.start_button);
        this.startButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((ZoneHelpActivity) ZoneHelpStartFragment.this.getActivity()).startSetup();
            }
        });
        return view;
    }

    public final void updateState(int number, Zone zone) {
        super.updateState(number, zone);
        this.startButton.setEnabled(true);
    }
}
