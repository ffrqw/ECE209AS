package com.rachio.iro.gen2.model;

import android.text.TextUtils;
import java.io.Serializable;

public class DiscoveredIro implements Serializable {
    private static final long serialVersionUID = 1;
    public final String mac;
    public final String ssid;

    public DiscoveredIro(String ssid, String mac) {
        this.ssid = ssid;
        this.mac = mac;
    }

    public boolean equals(Object o) {
        if (o instanceof DiscoveredIro) {
            return TextUtils.equals(((DiscoveredIro) o).mac, this.mac);
        }
        return false;
    }

    public String toString() {
        return this.ssid;
    }
}
