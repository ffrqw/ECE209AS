package com.rachio.iro.ui.fragment.zoneproperties;

import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.Crop;
import com.rachio.iro.model.zoneproperties.ZonePropertyCommon;
import java.util.Arrays;

public class CropListFragment extends ZonePropertyFragment {
    public final String getDescription() {
        return getString(R.string.zone_help_growing);
    }

    public final String getSupportLink() {
        return "266-choosing-vegetation";
    }

    public final ZonePropertyCommon[] getProps() {
        Crop[] crop = User.getLoggedInUser(this.database, this.prefsWrapper).crops;
        Arrays.sort(crop, Crop.comparator);
        return crop;
    }

    public final int getSelectorId() {
        return R.drawable.zoneprop_crop;
    }

    public final int getSelectedColourId() {
        return R.color.rachio_green;
    }
}
