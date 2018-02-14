package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.gen2.MrvlProvService.State;

public class ProvisioningProgressFragment extends BaseProgressFragment {
    private State stashedState;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        this.finishedText.setText("Device Connected");
        return v;
    }

    protected final void onProvisionStateChanged(State newState) {
        super.onProvisionStateChanged(newState);
        if (!isAdded()) {
            this.stashedState = newState;
        } else if (newState == State.FINISHED) {
            this.progressContainer.setVisibility(8);
            this.finished.setVisibility(0);
        } else {
            this.progressText.setText(newState.description);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.stashedState != null) {
            onProvisionStateChanged(this.stashedState);
            this.stashedState = null;
        }
    }
}
