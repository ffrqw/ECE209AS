package com.rachio.iro.ui.welcome.fragments;

import com.rachio.iro.ui.fragment.BaseFragment;

public class BaseWelcomeFragment extends BaseFragment {
    boolean active = false;

    public final void onActive() {
        this.active = true;
    }

    public final void onInactive() {
        this.active = false;
    }

    public void onScrollStopped() {
    }
}
