package com.instabug.library;

import android.app.Fragment;

public class InstabugFragmentDelegate {
    private final Fragment mFragment;

    public InstabugFragmentDelegate(Fragment fragment) {
        this.mFragment = fragment;
    }

    public void onResume() {
        Instabug.iG().b().a(this.mFragment.getClass().getName(), 2576);
    }

    public void onPause() {
        Instabug.iG().b().a(this.mFragment.getClass().getName(), 2568);
    }

    public void onDetach() {
        Instabug.iG().b().a(this.mFragment.getClass().getName(), 2569);
    }
}
