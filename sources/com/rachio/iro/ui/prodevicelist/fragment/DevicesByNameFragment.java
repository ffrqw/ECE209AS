package com.rachio.iro.ui.prodevicelist.fragment;

import com.rachio.iro.model.device.ShallowDevice;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.prodevicelist.activity.ProDeviceListActivity;
import com.rachio.iro.ui.prodevicelist.fragment.BaseProDeviceListFragment.DeviceAdapter.Listener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class DevicesByNameFragment extends BaseProDeviceListFragment {

    private class DeviceAdapter extends DeviceAdapter {
        public DeviceAdapter(User user, String selectedDeviceId, int numDevices, List<ShallowDevice> devices, TreeMap<String, String> keywords, Listener listener) {
            super(user, selectedDeviceId, true, false, numDevices, devices, keywords, listener);
        }

        public final String getTopLine(ShallowDevice device) {
            return DevicesByNameFragment.getTopLine$7224620f(device);
        }

        public final String getBottomLine(ShallowDevice device) {
            return DevicesByNameFragment.this.getBottomLine$7224620f(device);
        }

        public final String getResultsText() {
            return "Showing " + (this.filtered.size() - 1) + " Devices";
        }

        public final void sort() {
            boolean hadSelected = this.filtered.remove(this.selected);
            Collections.sort(this.filtered, new Comparator<ShallowDevice>() {
                public /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
                    return ((ShallowDevice) obj).name.toLowerCase().compareTo(((ShallowDevice) obj2).name.toLowerCase());
                }
            });
            if (hadSelected) {
                this.filtered.add(0, this.selected);
            }
        }
    }

    public static DevicesByNameFragment newInstance() {
        return new DevicesByNameFragment();
    }

    public static String getTopLine$7224620f(ShallowDevice device) {
        return device.name;
    }

    public final void setDevices(User user, String selectedDeviceId, int numDevices, List<ShallowDevice> devices, TreeMap<String, String> keywords, boolean showIndexes) {
        super.setDevices(user, selectedDeviceId, numDevices, devices, keywords, showIndexes);
        DeviceAdapter adapter = new DeviceAdapter(user, selectedDeviceId, numDevices, devices, keywords, new Listener() {
            public final void onDeviceSelected(String deviceId) {
                ((ProDeviceListActivity) DevicesByNameFragment.this.getActivity()).onDeviceSelected(deviceId);
            }
        });
        adapter.setLocation(this.location);
        if (this.list != null) {
            this.list.setAdapter(adapter);
        }
    }

    public final String getTitle() {
        return "Devices";
    }
}
