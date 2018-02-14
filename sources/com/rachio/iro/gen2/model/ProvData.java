package com.rachio.iro.gen2.model;

import java.io.Serializable;

public class ProvData implements Serializable {
    private static final long serialVersionUID = 1;
    public String deviceId;
    public String deviceMac;
    public boolean deviceMasterValve;
    public String deviceName;
    public String deviceSerialNumber;
    public String deviceSerialPrefix;
    public String deviceZip;
    public String networkPassword;
    public int networkSecurity = 5;
    public String networkSsid;
    public String userId;
    public boolean wifiSettingsOnly;
}
