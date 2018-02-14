package com.rachio.iro.ui.newschedulerulepath.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ZoneInfo;
import com.rachio.iro.ui.newschedulerulepath.views.ZoneDurationView;
import java.util.List;
import java.util.Map;

public class ZoneDurationAdapter extends ArrayAdapter<ZoneInfo> {
    private final boolean isFlex;
    private Listener listener;
    private final Map<String, Zone> zoneMap;

    public interface Listener {
        void onTotalDurationChanged(int i);
    }

    public ZoneDurationAdapter(Context context, boolean isFlex, List<ZoneInfo> zoneInfos, Map<String, Zone> zoneMap) {
        super(context, 0, zoneInfos);
        this.isFlex = isFlex;
        this.zoneMap = zoneMap;
    }

    private void increment(ZoneInfo zoneInfo) {
        zoneInfo.increment(this.isFlex);
        if (this.listener != null) {
            this.listener.onTotalDurationChanged(calculateTotalDuration());
        }
    }

    private void decrement(ZoneInfo zoneInfo) {
        zoneInfo.decrement(this.isFlex);
        if (this.listener != null) {
            this.listener.onTotalDurationChanged(calculateTotalDuration());
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ZoneDurationView view;
        if (convertView == null) {
            view = new ZoneDurationView(getContext());
            view.setFlex(this.isFlex);
            view.setListener(new com.rachio.iro.ui.newschedulerulepath.views.ZoneDurationView.Listener() {
                public final void onIncrease(int position) {
                    ZoneDurationAdapter.this.increment((ZoneInfo) ZoneDurationAdapter.this.getItem(position));
                    ZoneDurationAdapter.this.notifyDataSetChanged();
                }

                public final void onDecrease(int position) {
                    ZoneDurationAdapter.this.decrement((ZoneInfo) ZoneDurationAdapter.this.getItem(position));
                    ZoneDurationAdapter.this.notifyDataSetChanged();
                }
            });
        } else {
            view = (ZoneDurationView) convertView;
        }
        ZoneInfo zoneInfo = (ZoneInfo) getItem(position);
        view.set(position, ((Zone) this.zoneMap.get(zoneInfo.zoneId)).name, this.isFlex ? zoneInfo.multiplier.doubleValue() : 1.0d, zoneInfo.getDuration(this.isFlex));
        return view;
    }

    public final void setListener(Listener listener) {
        this.listener = listener;
    }

    public final void decrementAll() {
        for (int i = 0; i < getCount(); i++) {
            decrement((ZoneInfo) getItem(i));
        }
        notifyDataSetChanged();
    }

    public final void incrementAll() {
        for (int i = 0; i < getCount(); i++) {
            increment((ZoneInfo) getItem(i));
        }
        notifyDataSetChanged();
    }

    public final int calculateTotalDuration() {
        int totalZoneDuration = 0;
        for (int i = 0; i < getCount(); i++) {
            totalZoneDuration += ((ZoneInfo) getItem(i)).getDuration(this.isFlex);
        }
        return totalZoneDuration;
    }
}
