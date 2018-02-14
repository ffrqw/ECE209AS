package com.rachio.iro.ui.activity.zone;

import com.rachio.iro.ui.fragment.zoneproperties.SlopeListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.ZonePropertyFragment;

public class SlopeListActivity extends ZonePropertyActivity {
    public final ZonePropertyFragment getFragment() {
        return new SlopeListFragment();
    }
}
