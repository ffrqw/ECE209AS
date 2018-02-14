package com.rachio.iro.ui.activity.zone;

import com.rachio.iro.ui.fragment.zoneproperties.CropListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.ZonePropertyFragment;

public class CropListActivity extends ZonePropertyActivity {
    public final ZonePropertyFragment getFragment() {
        return new CropListFragment();
    }
}
