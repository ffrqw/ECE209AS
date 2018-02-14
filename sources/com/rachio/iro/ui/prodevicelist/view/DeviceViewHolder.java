package com.rachio.iro.ui.prodevicelist.view;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.device.ShallowDevice.RoughStatus;
import com.rachio.iro.model.user.User.DisplayUnit;
import com.rachio.iro.utils.UnitUtils;

public class DeviceViewHolder extends ViewHolder {
    public final TextView bottomLine;
    public final ImageView check;
    public final FrameLayout checkIndex;
    public final TextView distance;
    public final TextView index;
    public final View itemView;
    public final TextView locationServices;
    public TextView results;
    public final TextView statusDisabled;
    public final TextView statusOffline;
    public final TextView statusOnline;
    public final TextView statusRainDelay;
    public final TextView topLine;

    public DeviceViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.locationServices = (TextView) itemView.findViewById(R.id.prodevicelist_locationservices);
        this.checkIndex = (FrameLayout) itemView.findViewById(R.id.prodevicelistitem_checkindexholder);
        this.check = (ImageView) itemView.findViewById(R.id.prodevicelistitem_check);
        this.index = (TextView) itemView.findViewById(R.id.prodevicelistitem_index);
        this.topLine = (TextView) itemView.findViewById(R.id.prodevicelistitem_topline);
        this.bottomLine = (TextView) itemView.findViewById(R.id.prodevicelistitem_bottomline);
        this.distance = (TextView) itemView.findViewById(R.id.prodevicelistitem_distance);
        this.statusOnline = (TextView) itemView.findViewById(R.id.prodevicelistitem_status_online);
        this.statusRainDelay = (TextView) itemView.findViewById(R.id.prodevicelistitem_status_raindelay);
        this.statusDisabled = (TextView) itemView.findViewById(R.id.prodevicelistitem_status_disabled);
        this.statusOffline = (TextView) itemView.findViewById(R.id.prodevicelistitem_status_offline);
        this.results = (TextView) itemView.findViewById(R.id.prodevicelist_results);
    }

    public final void set(boolean locationServicesDisabled, boolean showIndex, String index, boolean selected, String topLine, String bottomLine, float distance, DisplayUnit displayUnit, RoughStatus status, boolean raindelayed) {
        this.locationServices.setVisibility(locationServicesDisabled ? 0 : 8);
        this.index.setVisibility(8);
        this.check.setVisibility(8);
        this.checkIndex.setVisibility(8);
        if (selected) {
            this.check.setVisibility(0);
            this.checkIndex.setVisibility(0);
        } else if (showIndex) {
            this.index.setText(index);
            this.index.setVisibility(0);
            this.checkIndex.setVisibility(0);
        }
        this.topLine.setText(topLine);
        this.bottomLine.setText(bottomLine);
        double userUnits = UnitUtils.convertMilesToUserUnits(displayUnit, UnitUtils.convertMetersToMiles((double) distance));
        String unitString = UnitUtils.getNameOfDistanceUnits(displayUnit);
        boolean showDistance = distance != -1.0f;
        if (showDistance) {
            this.distance.setText(String.format("%.1f %s", new Object[]{Double.valueOf(userUnits), unitString}));
        }
        this.distance.setVisibility(showDistance ? 0 : 4);
        this.statusOnline.setVisibility(4);
        this.statusRainDelay.setVisibility(4);
        this.statusDisabled.setVisibility(4);
        this.statusOffline.setVisibility(4);
        TextView statusView = null;
        switch (status) {
            case ONLINE:
            case WATERING:
                if (!raindelayed) {
                    statusView = this.statusOnline;
                    break;
                } else {
                    statusView = this.statusRainDelay;
                    break;
                }
            case PAUSED:
                statusView = this.statusDisabled;
                break;
            case OFFLINE:
                statusView = this.statusOffline;
                break;
        }
        statusView.setVisibility(0);
    }
}
