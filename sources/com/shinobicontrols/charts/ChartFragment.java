package com.shinobicontrols.charts;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(12)
public class ChartFragment extends Fragment {
    private af J;

    public final ShinobiChart getShinobiChart() {
        return this.J;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        this.J = new af(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.J;
    }

    public final void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) this.J.getParent()).removeView(this.J);
    }

    public void onResume() {
        super.onResume();
        if (this.J != null) {
            this.J.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.J != null) {
            this.J.onPause();
        }
    }
}
