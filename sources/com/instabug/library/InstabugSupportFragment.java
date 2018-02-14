package com.instabug.library;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class InstabugSupportFragment extends Fragment {
    private InstabugSupportFragmentDelegate mDelegate;

    public void onCreate(Bundle bundle) {
        this.mDelegate = new InstabugSupportFragmentDelegate(this);
        super.onCreate(bundle);
    }

    public void onResume() {
        this.mDelegate.onResume();
        super.onResume();
    }

    public void onPause() {
        this.mDelegate.onPause();
        super.onPause();
    }
}
