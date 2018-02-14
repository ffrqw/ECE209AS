package com.rachio.iro.ui.activity.zone;

import com.rachio.iro.ui.fragment.zoneproperties.NozzleListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.ZonePropertyFragment;

public class NozzleListActivity extends ZonePropertyActivity {
    public final ZonePropertyFragment getFragment() {
        return new NozzleListFragment();
    }
}
