package com.instabug.library;

import android.support.v4.app.Fragment;

public class InstabugSupportFragmentDelegate {
    private final Fragment mFragment;

    public InstabugSupportFragmentDelegate(Fragment fragment) {
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
