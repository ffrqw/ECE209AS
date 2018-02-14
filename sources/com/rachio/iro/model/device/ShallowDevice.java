package com.rachio.iro.model.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonView;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.rachio.iro.R;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.db.DatabaseObject;
import com.rachio.iro.utils.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShallowDevice<T extends DatabaseObject> extends DatabaseObject<T> implements Serializable {
    public static final String STATE_OFFLINE = "OFFLINE";
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String city;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public double latitude;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public double longitude;
    @DatabaseField(canBeNull = false)
    public Model model;
    @JsonView({TransmittableView.class})
    @DatabaseField(canBeNull = false)
    public String name;
    @JsonIgnore
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Owner owner;
    @DatabaseField
    public String ownerName;
    @DatabaseField
    public Date rainDelayExpirationDate;
    @DatabaseField
    public Date rainDelayStartDate;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public boolean schedulePause;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String state;
    @DatabaseField(canBeNull = false)
    public String status;
    @JsonView({TransmittableView.class})
    @DatabaseField
    public String zip;

    public enum Model {
        GEN1EIGHTZONE("8ZULW", 8, false),
        GEN1SIXTEENZONE("16ZULW", 16, false),
        GEN2EIGHTZONE("8ZR2ULW", 8, true),
        GEN2SIXTEENZONE("16ZR2ULW", 16, true);
        
        public final boolean isGen2;
        private final String modelString;
        public final int numZones;

        private Model(String modelString, int numZones, boolean isGen2) {
            this.modelString = modelString;
            this.numZones = numZones;
            this.isGen2 = isGen2;
        }

        public final String toString() {
            return this.modelString;
        }
    }

    public static class Owner implements Serializable {
        private static final long serialVersionUID = 1;
        public String email;
        public String fullName;
        public String id;
        public String username;
    }

    public enum RoughStatus {
        ONLINE(0, "Online", R.color.rachio_blue),
        WATERING(3, "Watering", R.color.rachio_blue),
        PAUSED(1, "Standby Mode", R.color.rachio_bright_orange),
        OFFLINE(2, "Offline", R.color.rachio_red);
        
        public final int statusBadgeLevel;
        public final String statusText;
        public final int statusTextColour;

        private RoughStatus(int statusBadgeLevel, String statusText, int statusTextColour) {
            this.statusBadgeLevel = statusBadgeLevel;
            this.statusText = statusText;
            this.statusTextColour = statusTextColour;
        }
    }

    @JsonIgnore
    public String getDeviceLocation() {
        List<String> parts = new ArrayList();
        if (this.city != null) {
            parts.add(this.city);
        }
        if (this.state != null) {
            parts.add(this.state);
        } else if (this.zip != null) {
            parts.add(this.zip);
        }
        return StringUtils.join(", ", (String[]) parts.toArray(new String[0]));
    }

    @JsonIgnore
    public boolean isSomeoneElses() {
        return this.owner != null;
    }

    @JsonSetter
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @JsonIgnore
    public RoughStatus getRoughStatus() {
        if (isOffline()) {
            return RoughStatus.OFFLINE;
        }
        if (this.schedulePause) {
            return RoughStatus.PAUSED;
        }
        return RoughStatus.ONLINE;
    }

    @JsonIgnore
    public boolean isOffline() {
        return this.status.equals(STATE_OFFLINE);
    }

    @JsonIgnore
    public boolean isInRainDelay() {
        boolean z = false;
        if (!(this.rainDelayStartDate == null || this.rainDelayExpirationDate == null)) {
            if (this.rainDelayExpirationDate.after(new Date()) && this.rainDelayStartDate.compareTo(this.rainDelayExpirationDate) != 0) {
                z = true;
            }
        }
        return z;
    }

    @JsonIgnore
    public boolean isGen1() {
        return (this.model == Model.GEN2SIXTEENZONE || this.model == Model.GEN2EIGHTZONE) ? false : true;
    }

    @JsonIgnore
    public int getDeviceLevel() {
        switch (this.model) {
            case GEN1EIGHTZONE:
            case GEN1SIXTEENZONE:
                return 0;
            case GEN2SIXTEENZONE:
                return 1;
            default:
                throw new RuntimeException();
        }
    }
}
