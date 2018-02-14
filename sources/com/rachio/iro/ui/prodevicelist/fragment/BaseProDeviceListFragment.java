package com.rachio.iro.ui.prodevicelist.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.model.device.ShallowDevice;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.newschedulerulepath.DividerItemDecoration;
import com.rachio.iro.ui.prodevicelist.activity.ProDeviceListActivity;
import com.rachio.iro.ui.prodevicelist.view.DeviceViewHolder;
import com.rachio.iro.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public abstract class BaseProDeviceListFragment extends BaseFragment {
    protected RecyclerView list;
    protected Location location;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected static abstract class DeviceAdapter extends Adapter<DeviceViewHolder> {
        protected final List<ShallowDevice> devices;
        protected final TreeMap<String, Float> distances = new TreeMap();
        protected final List<ShallowDevice> filtered;
        protected final HashMap<ShallowDevice, Long> ids = new HashMap();
        protected final TreeMap<String, String> keywords;
        private final Listener listener;
        private final boolean locationDisabled;
        protected final int numDevices;
        private boolean searching = false;
        protected ShallowDevice selected;
        private final String selectedDeviceId;
        private final boolean showIndex;
        protected final User user;

        public interface Listener {
            void onDeviceSelected(String str);
        }

        public abstract String getBottomLine(ShallowDevice shallowDevice);

        public abstract String getResultsText();

        public abstract String getTopLine(ShallowDevice shallowDevice);

        public abstract void sort();

        public /* bridge */ /* synthetic */ void onBindViewHolder(ViewHolder viewHolder, int i) {
            Float valueOf;
            int i2;
            String str = null;
            DeviceViewHolder deviceViewHolder = (DeviceViewHolder) viewHolder;
            final ShallowDevice shallowDevice = (ShallowDevice) this.filtered.get(i);
            Float f = (Float) this.distances.get(shallowDevice.id);
            if (f == null) {
                valueOf = Float.valueOf(-1.0f);
            } else {
                valueOf = f;
            }
            if (shallowDevice == this.selected) {
                deviceViewHolder.results.setText(getResultsText());
                i2 = this.numDevices >= 5 ? 1 : 0;
            } else {
                i2 = 0;
            }
            deviceViewHolder.results.setVisibility(i2 != 0 ? 0 : 8);
            if (this.showIndex) {
                String index = getIndex(shallowDevice);
                if (((ShallowDevice) this.filtered.get(0)) != this.selected) {
                    i2 = 0;
                } else {
                    i2 = 1;
                }
                if (i <= i2 || !index.equals(getIndex((ShallowDevice) this.filtered.get(i - 1)))) {
                    str = index;
                }
            }
            boolean z = this.locationDisabled && i == 0;
            deviceViewHolder.set(z, this.showIndex, str, StringUtils.equals(shallowDevice.id, this.selectedDeviceId), getTopLine(shallowDevice), getBottomLine(shallowDevice), valueOf.floatValue(), this.user.displayUnit, shallowDevice.getRoughStatus(), shallowDevice.isInRainDelay());
            deviceViewHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    DeviceAdapter.this.listener.onDeviceSelected(shallowDevice.id);
                }
            });
        }

        public DeviceAdapter(User user, String selectedDeviceId, boolean locationDisabled, boolean showIndex, int numDevices, List<ShallowDevice> devices, TreeMap<String, String> keywords, Listener listener) {
            this.user = user;
            this.selectedDeviceId = selectedDeviceId;
            this.locationDisabled = locationDisabled;
            this.showIndex = showIndex;
            this.numDevices = numDevices;
            this.devices = devices;
            this.filtered = new ArrayList(devices);
            this.listener = listener;
            this.keywords = keywords;
            setHasStableIds(true);
            long id = 0;
            for (ShallowDevice d : devices) {
                long id2 = id + 1;
                this.ids.put(d, Long.valueOf(id));
                if (StringUtils.equals(d.id, selectedDeviceId)) {
                    this.selected = d;
                }
                id = id2;
            }
        }

        private static String getIndex(ShallowDevice device) {
            return device.ownerName.substring(0, 1).toUpperCase();
        }

        public long getItemId(int position) {
            return ((Long) this.ids.get((ShallowDevice) this.filtered.get(position))).longValue();
        }

        public int getItemCount() {
            return this.filtered.size();
        }

        public final void setLocation(Location location) {
            this.distances.clear();
            if (location != null) {
                for (int i = 0; i < this.devices.size(); i++) {
                    ShallowDevice device = (ShallowDevice) this.devices.get(i);
                    float[] result = new float[1];
                    Location.distanceBetween(device.latitude, device.longitude, location.getLatitude(), location.getLongitude(), result);
                    this.distances.put(device.id, Float.valueOf(result[0]));
                }
            }
            sort();
        }

        public final void filter(String filter) {
            filter = filter.toLowerCase();
            this.filtered.clear();
            if (filter != null) {
                for (ShallowDevice d : this.devices) {
                    String deviceKeywords = (String) this.keywords.get(d.id);
                    boolean match = true;
                    for (String k : filter.split(" ")) {
                        if (!deviceKeywords.contains(k)) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        this.filtered.add(d);
                    }
                }
            } else {
                this.filtered.addAll(this.devices);
            }
            if (this.searching) {
                this.filtered.remove(this.selected);
            }
            sort();
            notifyDataSetChanged();
        }

        public final void onStartSearch() {
            this.searching = true;
            this.filtered.remove(this.selected);
            notifyItemRemoved(0);
        }

        public final void onEndSearch() {
            this.filtered.add(0, this.selected);
            notifyItemInserted(0);
            this.searching = false;
        }

        public /* bridge */ /* synthetic */ ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new DeviceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_prodevicelistitem, viewGroup, false));
        }
    }

    public abstract String getTitle();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prodevicelist, container, false);
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        this.list = (RecyclerView) view.findViewById(R.id.prodevicelist_list);
        this.swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public final void onRefresh() {
                ((ProDeviceListActivity) BaseProDeviceListFragment.this.getActivity()).refresh();
            }
        });
        this.list.setLayoutManager(new LinearLayoutManager(getContext()));
        this.list.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        return view;
    }

    public void setDevices(User user, String selectedDevice, int numDevices, List<ShallowDevice> list, TreeMap<String, String> treeMap, boolean showIndexes) {
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    public final void filter(String filter) {
        DeviceAdapter adapter = (DeviceAdapter) this.list.getAdapter();
        if (adapter != null) {
            adapter.filter(filter);
        }
    }

    public final void setLocation(Location location) {
        if (this.location == null || location.distanceTo(this.location) >= 100.0f) {
            if (this.list != null) {
                DeviceAdapter adapter = (DeviceAdapter) this.list.getAdapter();
                if (adapter != null) {
                    adapter.setLocation(location);
                }
            }
            this.location = location;
        }
    }

    public String getBottomLine$7224620f(ShallowDevice device) {
        List parts = new ArrayList();
        parts.add(device.ownerName);
        parts.add(device.getDeviceLocation());
        return StringUtils.join(", ", parts);
    }

    public final void startingSearch() {
        ((DeviceAdapter) this.list.getAdapter()).onStartSearch();
    }

    public final void stopSearch() {
        ((DeviceAdapter) this.list.getAdapter()).onEndSearch();
    }

    public static void onPermissionsGranted() {
    }

    public static void onPermissionsDenied() {
    }
}
