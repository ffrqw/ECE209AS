package com.rachio.iro.ui.welcome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;

public class WeatherStationFragment extends BaseVideoWelcomeFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_weatherstation, container, false);
        wireUpVideo(view);
        return view;
    }

    protected final String getVideoPath() {
        return "ani-weather.mp4";
    }
}
