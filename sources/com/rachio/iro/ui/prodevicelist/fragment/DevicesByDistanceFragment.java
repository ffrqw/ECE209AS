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

public class DevicesByDistanceFragment extends BaseProDeviceListFragment {

    private class DeviceAdapter extends DeviceAdapter {
        public DeviceAdapter(User user, String selectedDeviceId, int numDevices, List<ShallowDevice> devices, TreeMap<String, String> keywords, Listener listener) {
            super(user, selectedDeviceId, false, false, numDevices, devices, keywords, listener);
        }

        public final String getTopLine(ShallowDevice device) {
            return DevicesByDistanceFragment.getTopLine$7224620f(device);
        }

        public final String getBottomLine(ShallowDevice device) {
            return DevicesByDistanceFragment.this.getBottomLine$7224620f(device);
        }

        public final String getResultsText() {
            return "Showing " + (this.filtered.size() - 1) + " Devices Near Me";
        }

        public final void sort() {
            boolean hadSelected = this.filtered.remove(this.selected);
            Collections.sort(this.filtered, new Comparator<ShallowDevice>() {
                public /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
                    Float valueOf;
                    ShallowDevice shallowDevice = (ShallowDevice) obj2;
                    Float f = (Float) DeviceAdapter.this.distances.get(((ShallowDevice) obj).id);
                    if (f == null) {
                        valueOf = Float.valueOf(0.0f);
                    } else {
                        valueOf = f;
                    }
                    f = (Float) DeviceAdapter.this.distances.get(shallowDevice.id);
                    if (f == null) {
                        f = Float.valueOf(0.0f);
                    }
                    return valueOf.compareTo(f);
                }
            });
            if (hadSelected) {
                this.filtered.add(0, this.selected);
            }
        }
    }

    public static DevicesByDistanceFragment newInstance() {
        return new DevicesByDistanceFragment();
    }

    public static String getTopLine$7224620f(ShallowDevice device) {
        return device.name;
    }

    public final String getBottomLine$7224620f(ShallowDevice device) {
        List parts = new ArrayList();
        parts.add(device.ownerName);
        parts.add(device.getDeviceLocation());
        return StringUtils.join(", ", parts);
    }

    public final void setDevices(User user, String selectedDeviceId, int numDevices, List<ShallowDevice> devices, TreeMap<String, String> keywords, boolean showIndexes) {
        super.setDevices(user, selectedDeviceId, numDevices, devices, keywords, showIndexes);
        DeviceAdapter adapter = new DeviceAdapter(user, selectedDeviceId, numDevices, devices, keywords, new Listener() {
            public final void onDeviceSelected(String deviceId) {
                ((ProDeviceListActivity) DevicesByDistanceFragment.this.getActivity()).onDeviceSelected(deviceId);
            }
        });
        adapter.setLocation(this.location);
        if (this.list != null) {
            this.list.setAdapter(adapter);
        }
    }

    public final String getTitle() {
        return "Near Me";
    }
}
