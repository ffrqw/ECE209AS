package com.rachio.iro.ui.activity.zone;

import com.rachio.iro.ui.fragment.zoneproperties.ShadeListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.ZonePropertyFragment;

public class ShadeListActivity extends ZonePropertyActivity {
    public final ZonePropertyFragment getFragment() {
        return new ShadeListFragment();
    }
}
