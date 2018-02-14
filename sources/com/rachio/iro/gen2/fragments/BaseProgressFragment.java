package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;

public class BaseProgressFragment extends BaseProvisioningFragment {
    protected LinearLayout finished;
    protected TextView finishedText;
    protected LinearLayout progressContainer;
    protected TextView progressText;
    protected TextView subText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_progress, container, false);
        this.progressContainer = (LinearLayout) view.findViewById(R.id.gen2prov_progresscontainer);
        this.progressText = (TextView) view.findViewById(R.id.gen2prov_progress);
        this.subText = (TextView) view.findViewById(R.id.gen2prov_subtext);
        this.finished = (LinearLayout) view.findViewById(R.id.gen2prov_finished);
        this.finishedText = (TextView) view.findViewById(R.id.gen2prov_finishedtext);
        return view;
    }
}
