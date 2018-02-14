package com.instabug.library;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Bundle;

@TargetApi(11)
public class InstabugFragment extends Fragment {
    private InstabugFragmentDelegate mDelegate;

    public void onCreate(Bundle bundle) {
        this.mDelegate = new InstabugFragmentDelegate(this);
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
