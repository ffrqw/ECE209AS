package com.rachio.iro.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shinobicontrols.charts.ChartView;
import com.shinobicontrols.charts.ShinobiChart;

public class NestableChartFragment extends Fragment {
    private ChartView chartView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.chartView = new ChartView(getActivity());
        return this.chartView;
    }

    public void onResume() {
        super.onResume();
        this.chartView.onResume();
    }

    public void onPause() {
        super.onPause();
        this.chartView.onPause();
    }

    public final ShinobiChart getShinobiChart() {
        return this.chartView.getShinobiChart();
    }
}
