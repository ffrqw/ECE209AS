package com.rachio.iro.ui.activity;

import android.os.Bundle;
import com.rachio.iro.R;
import com.rachio.iro.ui.fragment.dashboard.HelpDashboardFragment;

public class HelpActivity extends BaseActivity {
    private static final String TAG = HelpActivity.class.getCanonicalName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_help);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add((int) R.id.fragmentContainer, HelpDashboardFragment.newInstance(getIntent().getStringExtra("article"))).commit();
        }
    }
}
