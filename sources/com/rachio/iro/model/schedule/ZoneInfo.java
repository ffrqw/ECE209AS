package com.rachio.iro.model.schedule;

import android.util.Log;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.device.Zone;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ZoneInfo extends ModelObject implements Serializable, Comparable<ZoneInfo> {
    public static final int DEFAULTDURATION = 600;
    public static final int DURATIONINCREMENT = 60;
    public static final int MAXIMUMDURATION = 600;
    public static final int MAXIMUMDURATION_SECONDS = 36000;
    public static final int MINIMUMDURATION = 0;
    public static final double MULTIPLIERINCREMENT = 0.05d;
    public static final double MULTIPLIERMAX = 1.5d;
    public static final double MULTIPLIERMIN = 0.5d;
    private static final String TAG = ZoneInfo.class.getSimpleName();
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public Integer baseDuration;
    @JsonView({TransmittableView.class})
    public Integer duration = Integer.valueOf(600);
    public boolean fetchDuration = false;
    @JsonView({TransmittableView.class})
    public Double multiplier;
    @JsonView({TransmittableView.class})
    public int sortOrder;
    @JsonView({TransmittableView.class})
    public Integer wateringAdjustmentLevel = null;
    @JsonView({TransmittableView.class})
    public String zoneId;
    @JsonView({TransmittableView.class})
    public int zoneNumber;

    @JsonIgnore
    public int getDuration(boolean flex) {
        if (flex) {
            return (int) (((double) this.baseDuration.intValue()) * this.multiplier.doubleValue());
        }
        return this.duration != null ? this.duration.intValue() : 600;
    }

    public ZoneInfo(Zone zone) {
        this.zoneId = zone.id;
        this.zoneNumber = zone.zoneNumber;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ZoneInfo)) {
            return super.equals(o);
        }
        ZoneInfo zi = (ZoneInfo) o;
        return zi.duration == this.duration && zi.sortOrder == this.sortOrder && zi.zoneId.equals(this.zoneId) && zi.zoneNumber == this.zoneNumber;
    }

    public int compareTo(ZoneInfo another) {
        return Integer.valueOf(this.sortOrder).compareTo(Integer.valueOf(another.sortOrder));
    }

    public static String[] createArrayOfZoneNames(List<ZoneInfo> zoneInfos, Map<String, Zone> zoneMap) {
        String[] names = new String[zoneInfos.size()];
        for (int z = 0; z < zoneInfos.size(); z++) {
            names[z] = ((Zone) zoneMap.get(((ZoneInfo) zoneInfos.get(z)).zoneId)).name;
        }
        return names;
    }

    @JsonSetter
    public void setWateringAdjustmentLevel(int wateringAdjustmentLevel) {
        this.wateringAdjustmentLevel = Integer.valueOf(0);
    }

    private double change(double delta) {
        return ((double) (Math.round(this.multiplier.doubleValue() * 100.0d) + Math.round(delta * 100.0d))) / 100.0d;
    }

    @JsonIgnore
    public void increment(boolean isFlex) {
        if (isFlex) {
            double old = this.multiplier.doubleValue();
            this.multiplier = Double.valueOf(Math.min(change(0.05d), 1.5d));
            Log.d(TAG, "increase; was " + old + " now " + this.multiplier);
            return;
        }
        this.duration = Integer.valueOf(Math.min(getDuration(isFlex) + 60, MAXIMUMDURATION_SECONDS));
    }

    @JsonIgnore
    public void decrement(boolean isFlex) {
        if (isFlex) {
            double old = this.multiplier.doubleValue();
            this.multiplier = Double.valueOf(Math.max(change(-0.05d), 0.5d));
            Log.d(TAG, "decrease; was " + old + " now " + this.multiplier);
            return;
        }
        this.duration = Integer.valueOf(Math.max(getDuration(isFlex) - 60, 0));
    }
}
