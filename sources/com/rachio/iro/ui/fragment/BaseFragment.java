package com.rachio.iro.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder;
import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.IroApplication;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.R;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.view.ProgressView;

public class BaseFragment extends Fragment {
    public Database database;
    protected ProgressView mProgressView;
    public PrefsWrapper prefsWrapper;
    public Tracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IroApplication.get(getActivity()).component().inject(this);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mProgressView = (ProgressView) view.findViewById(R.id.progress_view);
    }

    public void onResume() {
        super.onResume();
        if (getActivity() instanceof OnFragmentSelectedListener) {
            ((OnFragmentSelectedListener) getActivity()).onFragmentSelected(this);
        }
        this.tracker.setScreenName(getClass().getSimpleName());
        this.tracker.send(new ScreenViewBuilder().build());
    }

    public String getSection() {
        return "";
    }

    public final void showProgress(int stringResourceId) {
        showProgress(getString(R.string.progress_text_loading_device_information));
    }

    public final void showProgress(String text) {
        if (this.mProgressView != null) {
            this.mProgressView.show(text);
        }
    }

    public final void hideProgress() {
        if (this.mProgressView != null) {
            this.mProgressView.setVisibility(8);
        }
    }
}
