package com.rachio.iro.ui.welcome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rachio.iro.R;

public class TopFragment extends BaseWelcomeFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_top, container, false);
    }
}
