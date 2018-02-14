package com.rachio.iro.ui.activity.zone;

import com.rachio.iro.ui.fragment.zoneproperties.SoilListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.ZonePropertyFragment;

public class SoilListActivity extends ZonePropertyActivity {
    public final ZonePropertyFragment getFragment() {
        return new SoilListFragment();
    }
}
