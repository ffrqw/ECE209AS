package com.rachio.iro.ui.fragment.zoneproperties;

import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.Shade;
import com.rachio.iro.model.zoneproperties.ZonePropertyCommon;
import java.util.Arrays;

public class ShadeListFragment extends ZonePropertyFragment {
    public final String getDescription() {
        return getString(R.string.zone_help_shade);
    }

    public final ZonePropertyCommon[] getProps() {
        Shade[] shades = User.getLoggedInUser(this.database, this.prefsWrapper).shades;
        Arrays.sort(shades, Shade.comparator);
        return shades;
    }

    public final int getSelectedColourId() {
        return R.color.rachio_yellow;
    }

    public final int getSelectorId() {
        return R.drawable.zoneprop_shade;
    }

    public final String getSupportLink() {
        return "265-choosing-shade";
    }
}
