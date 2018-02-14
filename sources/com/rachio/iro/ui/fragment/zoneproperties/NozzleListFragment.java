package com.rachio.iro.ui.fragment.zoneproperties;

import android.content.Intent;
import com.rachio.iro.R;
import com.rachio.iro.model.PostId;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.Nozzle;
import com.rachio.iro.model.zoneproperties.ZonePropertyCommon;
import com.rachio.iro.ui.activity.zone.NozzleConfigurationActivity;

public class NozzleListFragment extends ZonePropertyFragment {
    public final boolean canAdd() {
        return true;
    }

    public final String getDescription() {
        return getString(R.string.zone_help_sprinkler);
    }

    public final ZonePropertyCommon[] getProps() {
        return User.getLoggedInUser(this.database, this.prefsWrapper).nozzles;
    }

    public final int getSelectedColourId() {
        return R.color.rachio_blue;
    }

    public final int getSelectorId() {
        return R.drawable.zoneprop_nozzle;
    }

    public final String getSupportLink() {
        return "264-choosing-nozzle";
    }

    public final void onAdd() {
        super.onAdd();
        Nozzle n = new Nozzle();
        n.person = new PostId(User.getLoggedInUser(this.database, this.prefsWrapper).id);
        Intent i = new Intent(getActivity(), NozzleConfigurationActivity.class);
        i.putExtra("newNozzle", n);
        startActivity(i);
    }
}
