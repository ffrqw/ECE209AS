package com.rachio.iro.ui.newschedulerulepath.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ZoneInfo;
import com.rachio.iro.ui.newschedulerulepath.views.CheckableTextRowView;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class SelectableZonesAdapter extends Adapter<ViewHolder> {
    private SparseBooleanArray checked = new SparseBooleanArray();
    private SparseBooleanArray enabled = new SparseBooleanArray();
    private final TreeMap<Integer, String> nameMap;
    private OnCheckChangedListener onCheckChangedListener;
    private final List<ZoneInfo> tempZoneInfos = new ArrayList();

    public interface OnCheckChangedListener {
        void onCheckChanged();
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public final CheckableTextRowView view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = (CheckableTextRowView) itemView;
        }
    }

    public /* bridge */ /* synthetic */ void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int i) {
        boolean z;
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        ZoneInfo zoneInfo = (ZoneInfo) this.tempZoneInfos.get(i);
        viewHolder2.view.setText((String) this.nameMap.get(Integer.valueOf(zoneInfo.zoneNumber)));
        viewHolder2.view.setChecked(this.checked.get(zoneInfo.zoneNumber));
        CheckableTextRowView checkableTextRowView = viewHolder2.view;
        if (this.enabled.get(zoneInfo.zoneNumber)) {
            z = false;
        } else {
            z = true;
        }
        checkableTextRowView.setMasked(z);
    }

    public SelectableZonesAdapter(List<Zone> allZones, List<Zone> enabledZones, List<ZoneInfo> zoneInfos, boolean isFlex) {
        for (Zone z : allZones) {
            if (enabledZones.contains(z)) {
                this.enabled.append(z.zoneNumber, true);
            }
        }
        if (zoneInfos != null) {
            ZoneInfo zi;
            this.tempZoneInfos.addAll(zoneInfos);
            for (ZoneInfo zi2 : zoneInfos) {
                this.checked.put(zi2.zoneNumber, true);
            }
            for (Zone ez : allZones) {
                if (!this.checked.get(ez.zoneNumber)) {
                    int insertPos = ez.zoneNumber - 1;
                    zi2 = new ZoneInfo(ez);
                    if (isFlex) {
                        zi2.baseDuration = Integer.valueOf(ez.runtimeNoMultiplier);
                        zi2.multiplier = Double.valueOf(1.0d);
                    } else {
                        zi2.fetchDuration = true;
                    }
                    if (insertPos < this.tempZoneInfos.size()) {
                        this.tempZoneInfos.add(insertPos, zi2);
                    } else {
                        this.tempZoneInfos.add(zi2);
                    }
                }
            }
        } else {
            for (Zone ez2 : enabledZones) {
                this.tempZoneInfos.add(new ZoneInfo(ez2));
                this.checked.put(ez2.zoneNumber, this.enabled.get(ez2.zoneNumber));
            }
        }
        this.nameMap = new TreeMap();
        for (Zone ez22 : allZones) {
            this.nameMap.put(Integer.valueOf(ez22.zoneNumber), ez22.name);
        }
    }

    public int getItemCount() {
        return this.tempZoneInfos.size();
    }

    public final void onMove(android.support.v7.widget.RecyclerView.ViewHolder viewHolder, android.support.v7.widget.RecyclerView.ViewHolder target) {
        int viewHolderPos = viewHolder.getAdapterPosition();
        int targetPos = target.getAdapterPosition();
        this.tempZoneInfos.add(targetPos, (ZoneInfo) this.tempZoneInfos.remove(viewHolderPos));
        notifyItemMoved(viewHolderPos, targetPos);
    }

    public final ArrayList<ZoneInfo> getNewZoneInfos() {
        ArrayList<ZoneInfo> zoneInfos = new ArrayList();
        for (ZoneInfo zi : this.tempZoneInfos) {
            if (this.checked.get(zi.zoneNumber)) {
                zi.sortOrder = zoneInfos.size();
                zoneInfos.add(zi);
            }
        }
        return zoneInfos;
    }

    public final int getCheckedAndEnabledItemCount() {
        int count = 0;
        int i = 0;
        while (i < this.checked.size()) {
            if (this.checked.valueAt(i) && this.enabled.get(this.checked.keyAt(i))) {
                count++;
            }
            i++;
        }
        return count;
    }

    public final void setOnCheckChangedListener(OnCheckChangedListener onCheckChangedListener) {
        this.onCheckChangedListener = onCheckChangedListener;
    }

    public /* bridge */ /* synthetic */ android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View checkableTextRowView = new CheckableTextRowView(viewGroup.getContext());
        checkableTextRowView.setLayoutParams(new LayoutParams(-1, -2));
        final android.support.v7.widget.RecyclerView.ViewHolder viewHolder = new ViewHolder(checkableTextRowView);
        checkableTextRowView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SelectableZonesAdapter.access$000(SelectableZonesAdapter.this, viewHolder);
            }
        });
        return viewHolder;
    }

    static /* synthetic */ void access$000(SelectableZonesAdapter x0, android.support.v7.widget.RecyclerView.ViewHolder x1) {
        int adapterPosition = x1.getAdapterPosition();
        ZoneInfo zoneInfo = (ZoneInfo) x0.tempZoneInfos.get(adapterPosition);
        x0.checked.put(zoneInfo.zoneNumber, !x0.checked.get(zoneInfo.zoneNumber));
        if (x0.onCheckChangedListener != null) {
            x0.onCheckChangedListener.onCheckChanged();
        }
        x0.notifyItemChanged(adapterPosition);
    }
}
