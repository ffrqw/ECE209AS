package com.rachio.iro.ui.fragment.zoneproperties;

import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.Soil;
import com.rachio.iro.model.zoneproperties.ZonePropertyCommon;
import java.util.Arrays;

public class SoilListFragment extends ZonePropertyFragment {
    public final String getDescription() {
        return getString(R.string.zone_help_soil);
    }

    public final ZonePropertyCommon[] getProps() {
        Soil[] soils = User.getLoggedInUser(this.database, this.prefsWrapper).soils;
        Arrays.sort(soils, Soil.comparator);
        return soils;
    }

    public final int getSelectedColourId() {
        return R.color.rachio_orange;
    }

    public final int getSelectorId() {
        return R.drawable.zoneprop_soil;
    }

    public final String getSupportLink() {
        return "262-choosing-soil";
    }
}
