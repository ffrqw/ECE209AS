package com.rachio.iro.ui.prodevicelist.fragment;

import com.rachio.iro.model.device.ShallowDevice;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.prodevicelist.activity.ProDeviceListActivity;
import com.rachio.iro.ui.prodevicelist.fragment.BaseProDeviceListFragment.DeviceAdapter.Listener;
import com.rachio.iro.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class DevicesByPersonFragment extends BaseProDeviceListFragment {

    private class DeviceAdapter extends DeviceAdapter {
        public DeviceAdapter(User user, String selectedDeviceId, boolean showIndexes, int numDevices, List<ShallowDevice> devices, TreeMap<String, String> keywords, Listener listener) {
            super(user, selectedDeviceId, false, showIndexes, numDevices, devices, keywords, listener);
        }

        public final String getTopLine(ShallowDevice device) {
            return DevicesByPersonFragment.getTopLine$7224620f(device);
        }

        public final String getBottomLine(ShallowDevice device) {
            return DevicesByPersonFragment.this.getBottomLine$7224620f(device);
        }

        public final String getResultsText() {
            return "Showing " + (this.filtered.size() - 1) + " people";
        }

        public final void sort() {
            boolean hadSelected = this.filtered.remove(this.selected);
            Collections.sort(this.filtered, new Comparator<ShallowDevice>() {
                public /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
                    return ((ShallowDevice) obj).ownerName.toUpperCase().compareTo(((ShallowDevice) obj2).ownerName.toUpperCase());
                }
            });
            if (hadSelected) {
                this.filtered.add(0, this.selected);
            }
        }
    }

    public static DevicesByPersonFragment newInstance() {
        return new DevicesByPersonFragment();
    }

    public static String getTopLine$7224620f(ShallowDevice device) {
        return device.ownerName;
    }

    public final String getBottomLine$7224620f(ShallowDevice device) {
        List parts = new ArrayList();
        parts.add(device.name);
        parts.add(device.getDeviceLocation());
        return StringUtils.join(", ", parts);
    }

    public final String getTitle() {
        return "People";
    }

    public final void setDevices(User user, String selectedDeviceId, int numDevices, List<ShallowDevice> devices, TreeMap<String, String> keywords, boolean showIndexes) {
        super.setDevices(user, selectedDeviceId, numDevices, devices, keywords, showIndexes);
        DeviceAdapter adapter = new DeviceAdapter(user, selectedDeviceId, showIndexes, numDevices, devices, keywords, new Listener() {
            public final void onDeviceSelected(String deviceId) {
                ((ProDeviceListActivity) DevicesByPersonFragment.this.getActivity()).onDeviceSelected(deviceId);
            }
        });
        adapter.setLocation(this.location);
        if (this.list != null) {
            this.list.setAdapter(adapter);
        }
    }
}
