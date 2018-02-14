package com.rachio.iro.async.command;

import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.apionly.ZoneProps;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.Crop;
import com.rachio.iro.model.zoneproperties.Nozzle;
import com.rachio.iro.model.zoneproperties.Shade;
import com.rachio.iro.model.zoneproperties.Slope;
import com.rachio.iro.model.zoneproperties.Soil;

public class FetchZoneCommand extends BaseCommand<ZoneDataHolder> {
    private final FetchZoneListener listener;
    private final String zoneId;

    public interface FetchZoneListener {
        void onZoneFetched(ZoneDataHolder zoneDataHolder);
    }

    public static class ZoneDataHolder {
        public final Crop customCrop;
        public final Nozzle customNozzle;
        public final Shade customShade;
        public final Slope customSlope;
        public final Soil customSoil;
        public final Device device;
        public final boolean isInFlexRule;
        public final User user;
        public final Zone zone;

        public ZoneDataHolder(User user, Device device, Zone zone, Crop customCrop, Soil customSoil, Shade customShade, Nozzle customNozzle, Slope customSlope, boolean isInFlexRule) {
            this.user = user;
            this.device = device;
            this.zone = zone;
            this.customCrop = customCrop;
            this.customSoil = customSoil;
            this.customShade = customShade;
            this.customNozzle = customNozzle;
            this.customSlope = customSlope;
            this.isInFlexRule = isInFlexRule;
        }
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        ZoneDataHolder zoneDataHolder = (ZoneDataHolder) obj;
        if (this.listener != null) {
            this.listener.onZoneFetched(zoneDataHolder);
        }
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        Zone zone = (Zone) this.database.find(Zone.class, this.zoneId);
        if (zone != null) {
            Device device = (Device) this.database.find(Device.class, zone.device.id);
            if (device != null) {
                User user = (User) this.database.find(User.class, device.getLocalUser().id);
                if (user != null) {
                    Crop cropById = user.getCropById(zone.customCrop.id);
                    Soil soilById = user.getSoilById(zone.customSoil.id);
                    Shade shadeById = user.getShadeById(zone.customShade.id);
                    Nozzle nozzleById = user.getNozzleById(zone.customNozzle.id);
                    Slope slopeById = user.getSlopeById(zone.customSlope.id);
                    if (cropById == null || soilById == null || shadeById == null || nozzleById == null || slopeById == null) {
                        ZoneProps zoneProps = (ZoneProps) this.restClient.getObjectById(this.database, zone.id, ZoneProps.class, new HttpResponseErrorHandler());
                        if (zoneProps != null) {
                            cropById = zoneProps.customCrop;
                            soilById = zoneProps.customSoil;
                            shadeById = zoneProps.customShade;
                            nozzleById = zoneProps.customNozzle;
                            slopeById = zoneProps.customSlope;
                        }
                    }
                    boolean isZoneInFlexRule = device.isZoneInFlexRule(this.zoneId, true, null);
                    if (!(cropById == null || soilById == null || shadeById == null || nozzleById == null || slopeById == null)) {
                        return new ZoneDataHolder(user, device, zone, cropById, soilById, shadeById, nozzleById, slopeById, isZoneInFlexRule);
                    }
                }
            }
        }
        return null;
    }

    public FetchZoneCommand(FetchZoneListener listener, String zoneId) {
        if (listener == null || zoneId == null) {
            throw new IllegalArgumentException();
        }
        this.listener = listener;
        this.zoneId = zoneId;
        BaseCommand.component(listener).inject(this);
    }
}
