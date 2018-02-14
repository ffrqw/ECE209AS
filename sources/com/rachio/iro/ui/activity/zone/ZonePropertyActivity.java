package com.rachio.iro.ui.activity.zone;

import android.os.Bundle;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.fragment.zoneproperties.ZonePropertyFragment;

public abstract class ZonePropertyActivity extends BaseActivity {
    private ZonePropertyFragment frag;

    public abstract ZonePropertyFragment getFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_zoneproperty);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.frag = getFragment();
        this.frag.setSelectedId(getIntent().getStringExtra("selectedid"));
        getSupportFragmentManager().beginTransaction().add((int) R.id.zoneproperty_fragment, this.frag).commit();
        setResult(0);
    }
}
