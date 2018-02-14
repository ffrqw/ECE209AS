package com.rachio.iro.ui.welcome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;

public class ScheduleFragment extends BaseVideoWelcomeFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_schedule, container, false);
        wireUpVideo(view);
        return view;
    }

    protected final String getVideoPath() {
        return "ani-sched.mp4";
    }
}
