package com.rachio.iro.ui.fragment.zoneproperties;

import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.Slope;
import com.rachio.iro.model.zoneproperties.ZonePropertyCommon;
import java.util.Arrays;

public class SlopeListFragment extends ZonePropertyFragment {
    public final String getDescription() {
        return getString(R.string.zone_help_slope);
    }

    public final ZonePropertyCommon[] getProps() {
        Slope[] slopes = User.getLoggedInUser(this.database, this.prefsWrapper).slopes;
        Arrays.sort(slopes, Slope.comparator);
        return slopes;
    }

    public final int getSelectedColourId() {
        return R.color.rachio_aqua;
    }

    public final int getSelectorId() {
        return R.drawable.zoneprop_slope;
    }

    public final String getSupportLink() {
        return "263-choosing-slope";
    }
}
