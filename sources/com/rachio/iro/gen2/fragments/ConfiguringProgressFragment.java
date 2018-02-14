package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConfiguringProgressFragment extends BaseProgressFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.progressText.setText("Configuring device...");
        this.finishedText.setText("Device Configured");
        return view;
    }

    public final void onSetupComplete() {
        super.onSetupComplete();
        this.progressContainer.setVisibility(8);
        this.finished.setVisibility(0);
    }
}
