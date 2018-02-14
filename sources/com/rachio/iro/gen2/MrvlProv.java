package com.rachio.iro.gen2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.rachio.iro.gen2.MrvlProvService.FailureReason;
import com.rachio.iro.gen2.MrvlProvService.State;
import com.rachio.iro.gen2.model.DiscoveredIro;
import com.rachio.iro.gen2.model.FoundNetwork;
import com.rachio.iro.gen2.model.ProvData;
import com.rachio.iro.model.device.ShallowDevice.Model;
import com.rachio.iro.utils.CrashReporterUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MrvlProv implements Parcelable {
    public static final Creator<MrvlProv> CREATOR = new Creator<MrvlProv>() {
        public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
            return new MrvlProv[i];
        }

        public final /* bridge */ /* synthetic */ Object createFromParcel(Parcel parcel) {
            return new MrvlProv(parcel);
        }
    };
    private static final String TAG = MrvlProv.class.getName();
    private static final IntentFilter provServiceIntentFilter = new IntentFilter();
    private static final IntentFilter wifiScanIntentFilter = new IntentFilter("android.net.wifi.SCAN_RESULTS");
    private static final IntentFilter wifiStateintentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
    private LocalBroadcastManager broadcastManager;
    private DeviceSearchListener deviceFoundListener;
    private String deviceMac;
    private final Handler handler;
    private String[] macPrefixes;
    private NetworkSearchListener networkSearchListener;
    private BroadcastReceiver provServiceReceiver;
    private ProvState provState;
    private ProvisionCallback provisioningCallback;
    private ArrayList<ScanResult> scanCache;
    private BroadcastReceiver scanResultsReceiver;
    private long startedScanningAt;
    private TimeoutRunnable timeoutRunnable;
    private WifiEnabledListener wifiEnabledListener;
    private WifiManager wifiManager;

    public interface DeviceSearchListener {
        void onScanningCanceled();

        void onSpecificDeviceFound(DiscoveredIro discoveredIro);

        void onUnProvisionedDeviceFound(DiscoveredIro discoveredIro);
    }

    public interface NetworkSearchListener {
        void onNetworkFound(FoundNetwork foundNetwork);

        void onScanningCanceled();
    }

    public enum ProvState {
        WAITINGFORWIFI,
        SCANNINGFORDEVICES,
        SCANNINGFORNETWORKS,
        PROVISIONING,
        IDLE
    }

    public interface ProvisionCallback {
        void onProvisioningCompleted(String str, String str2, Model model);

        void onProvisioningFailed(FailureReason failureReason);

        void onProvisioningStateChanged(State state);

        void onTimeoutWaitingForInternetConnection();
    }

    public enum Security {
        SECURITY_WEP,
        SECURITY_PSK,
        SECURITY_EAP,
        SECURITY_NONE
    }

    private class TimeoutRunnable implements Runnable {
        private final Context context;

        public TimeoutRunnable(Context context) {
            this.context = context;
        }

        public void run() {
            CrashReporterUtils.logDebug(MrvlProv.TAG, "checking prov state, current state is " + MrvlProv.this.provState);
            switch (MrvlProv.this.provState) {
                case SCANNINGFORDEVICES:
                case SCANNINGFORNETWORKS:
                    long timeScanning = System.currentTimeMillis() - MrvlProv.this.startedScanningAt;
                    CrashReporterUtils.logDebug(MrvlProv.TAG, "have been scanning for " + timeScanning + "ms");
                    if (timeScanning <= 120000) {
                        MrvlProv.this.wifiManager.startScan();
                        break;
                    }
                    CrashReporterUtils.logDebug(MrvlProv.TAG, "stopping scan");
                    if (MrvlProv.this.provState == ProvState.SCANNINGFORDEVICES) {
                        if (MrvlProv.this.deviceFoundListener != null) {
                            MrvlProv.this.deviceFoundListener.onScanningCanceled();
                        }
                    } else if (MrvlProv.this.networkSearchListener != null) {
                        MrvlProv.this.networkSearchListener.onScanningCanceled();
                    }
                    MrvlProv.this.stop();
                    break;
                case PROVISIONING:
                    Intent i = new Intent(this.context, MrvlProvService.class);
                    i.setAction(MrvlProvService.ACTION_PING);
                    this.context.startService(i);
                    break;
            }
            MrvlProv.this.handler.postDelayed(this, 20000);
        }
    }

    public interface WifiEnabledListener {
        void onWifiEnabled();
    }

    static {
        provServiceIntentFilter.addAction(MrvlProvService.BROADCAST_STATECHANGE);
        provServiceIntentFilter.addAction(MrvlProvService.BROADCAST_SUCCESS);
        provServiceIntentFilter.addAction(MrvlProvService.BROADCAST_FAILED);
        provServiceIntentFilter.addAction(MrvlProvService.BROADCAST_TIMEOUTWAITINGFORINTERNET);
    }

    private void reportUsableNetwork(ScanResult scanResult) {
        if (this.networkSearchListener != null) {
            boolean z;
            int level = WifiManager.calculateSignalLevel(scanResult.level, 3);
            String str = scanResult.SSID;
            int i = (scanResult.frequency - (scanResult.frequency % 1000)) / 1000;
            CrashReporterUtils.logDebug(TAG, scanResult.SSID + "'s frequency is " + scanResult.frequency);
            if (i != 2) {
                CrashReporterUtils.logDebug(TAG, "ignoring " + scanResult.SSID + " seems to be 5ghz");
                z = true;
            } else {
                z = false;
            }
            this.networkSearchListener.onNetworkFound(new FoundNetwork(str, level, z));
        }
    }

    public MrvlProv() {
        this.deviceFoundListener = null;
        this.networkSearchListener = null;
        this.wifiEnabledListener = null;
        this.handler = new Handler();
        this.provisioningCallback = null;
        this.startedScanningAt = 0;
        this.deviceMac = null;
        this.provState = ProvState.IDLE;
        this.scanCache = new ArrayList();
        this.scanResultsReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                CrashReporterUtils.logDebug(MrvlProv.TAG, "have scan results");
                synchronized (MrvlProv.this) {
                    if (MrvlProv.this.provState == ProvState.SCANNINGFORDEVICES || MrvlProv.this.provState == ProvState.SCANNINGFORNETWORKS) {
                        List<ScanResult> results;
                        if (MrvlProv.this.provState == ProvState.SCANNINGFORDEVICES) {
                            results = MrvlProv.this.wifiManager.getScanResults();
                            if (results == null || results.size() <= 0) {
                                CrashReporterUtils.logDebug(MrvlProv.TAG, "scan results are empty");
                            } else {
                                for (ScanResult sr : results) {
                                    if (MrvlProv.this.isThisAnIro(sr)) {
                                        String cleanMac = sr.BSSID.replaceAll(":", "").toUpperCase();
                                        if (MrvlProv.this.deviceMac != null) {
                                            CrashReporterUtils.logDebug(MrvlProv.TAG, "found iro " + sr.SSID + " " + cleanMac + ", looking for " + MrvlProv.this.deviceMac);
                                        } else {
                                            CrashReporterUtils.logDebug(MrvlProv.TAG, "found iro " + sr.SSID + " " + cleanMac);
                                        }
                                        if (MrvlProv.this.deviceFoundListener != null) {
                                            DiscoveredIro iro = new DiscoveredIro(sr.SSID, cleanMac);
                                            if (MrvlProv.this.deviceMac == null) {
                                                MrvlProv.this.deviceFoundListener.onUnProvisionedDeviceFound(iro);
                                            } else if (iro.mac.equals(MrvlProv.this.deviceMac)) {
                                                MrvlProv.this.deviceFoundListener.onSpecificDeviceFound(iro);
                                                break;
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        CrashReporterUtils.logDebug(MrvlProv.TAG, "found something that isn't an iro " + sr.SSID + "(" + sr.BSSID + ")");
                                        if (MrvlProv.access$700(MrvlProv.this, sr)) {
                                            MrvlProv.this.scanCache.add(sr);
                                        }
                                    }
                                }
                            }
                        } else if (MrvlProv.this.provState == ProvState.SCANNINGFORNETWORKS) {
                            results = MrvlProv.this.wifiManager.getScanResults();
                            for (ScanResult sr2 : results) {
                                if (!TextUtils.isEmpty(sr2.SSID) && MrvlProv.access$700(MrvlProv.this, sr2)) {
                                    MrvlProv.this.reportUsableNetwork(sr2);
                                }
                            }
                            if (results.size() > 0) {
                                if (MrvlProv.this.networkSearchListener != null) {
                                    MrvlProv.this.networkSearchListener.onScanningCanceled();
                                }
                                MrvlProv.this.stop();
                            }
                        }
                    }
                }
            }
        };
        this.provServiceReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (MrvlProv.this.provState != ProvState.IDLE) {
                    String action = intent.getAction();
                    CrashReporterUtils.logDebug(MrvlProv.TAG, "handing event from prov service: " + action);
                    if (action.equals(MrvlProvService.BROADCAST_STATECHANGE)) {
                        if (MrvlProv.this.provisioningCallback != null) {
                            MrvlProv.this.provisioningCallback.onProvisioningStateChanged((State) intent.getSerializableExtra(MrvlProvService.EXTRA_OUT_NEWSTATE));
                        }
                    } else if (action.equals(MrvlProvService.BROADCAST_SUCCESS)) {
                        MrvlProv.access$1200(MrvlProv.this, context, true, null, intent.getStringExtra(MrvlProvService.EXTRA_OUT_DEVICEID), intent.getStringExtra(MrvlProvService.EXTRA_OUT_DEVICEZIP), (Model) intent.getSerializableExtra(MrvlProvService.EXTRA_OUT_DEVICEMODEL));
                    } else if (action.equals(MrvlProvService.BROADCAST_FAILED)) {
                        MrvlProv.access$1200(MrvlProv.this, context, false, (FailureReason) intent.getSerializableExtra(MrvlProvService.EXTRA_OUT_REASON), null, null, null);
                    } else if (action.equals(MrvlProvService.BROADCAST_TIMEOUTWAITINGFORINTERNET) && MrvlProv.this.provisioningCallback != null) {
                        MrvlProv.this.provisioningCallback.onTimeoutWaitingForInternetConnection();
                    }
                }
            }
        };
    }

    private MrvlProv(Parcel in) {
        this.deviceFoundListener = null;
        this.networkSearchListener = null;
        this.wifiEnabledListener = null;
        this.handler = new Handler();
        this.provisioningCallback = null;
        this.startedScanningAt = 0;
        this.deviceMac = null;
        this.provState = ProvState.IDLE;
        this.scanCache = new ArrayList();
        this.scanResultsReceiver = /* anonymous class already generated */;
        this.provServiceReceiver = /* anonymous class already generated */;
        this.provState = (ProvState) in.readSerializable();
        int macPrefixesLen = in.readInt();
        if (macPrefixesLen == -1) {
            this.macPrefixes = null;
            return;
        }
        this.macPrefixes = new String[macPrefixesLen];
        in.readStringArray(this.macPrefixes);
    }

    private boolean isThisAnIro(ScanResult scanResult) {
        boolean ouiFound = false;
        for (String p : this.macPrefixes) {
            if (scanResult.BSSID.toLowerCase().startsWith(p.toLowerCase())) {
                ouiFound = true;
                break;
            }
        }
        if (scanResult.SSID.startsWith("Rachio") && ouiFound) {
            return true;
        }
        return false;
    }

    private synchronized void moveToState(ProvState newState) {
        CrashReporterUtils.logDebug(TAG, "moving from state " + this.provState + " to " + newState);
        if (this.provState == ProvState.IDLE || newState == ProvState.IDLE) {
            this.provState = newState;
        } else {
            throw new IllegalStateException("bad state change from " + this.provState + " to " + newState);
        }
    }

    public final void setOnDeviceFoundListener(DeviceSearchListener listener) {
        this.deviceFoundListener = listener;
    }

    public final void setOnNetworkFoundListener(NetworkSearchListener listener) {
        this.networkSearchListener = listener;
    }

    public final void startProvision(Context context, ProvData data) {
        moveToState(ProvState.PROVISIONING);
        this.broadcastManager.registerReceiver(this.provServiceReceiver, provServiceIntentFilter);
        CrashReporterUtils.logDebug(TAG, "starting provisioning for " + data.deviceName);
        Intent i = new Intent(context, MrvlProvService.class);
        i.setAction(MrvlProvService.ACTION_PROVISION);
        i.putExtra(MrvlProvService.EXTRA_IN_PROVDATA, data);
        context.startService(i);
    }

    public final void setProvisioningCallback(ProvisionCallback provisioningCallback) {
        this.provisioningCallback = provisioningCallback;
    }

    private void startWifiScan(Context context) {
        context.registerReceiver(this.scanResultsReceiver, wifiScanIntentFilter);
        this.startedScanningAt = System.currentTimeMillis();
        this.wifiManager.startScan();
    }

    private void startSearching(Context context) {
        moveToState(ProvState.SCANNINGFORDEVICES);
        startWifiScan(context);
    }

    public final void startSearchingForUnProvisionedDevices(String[] macPrefixes, Context context) {
        this.macPrefixes = macPrefixes;
        startSearching(context);
    }

    public final void startSearchingForSpecificDevice(Context context, String[] macPrefixes, String mac) {
        this.macPrefixes = macPrefixes;
        if (mac == null) {
            throw new IllegalArgumentException();
        }
        this.deviceMac = mac;
        startSearching(context);
    }

    public final void startSearchingForUsableNetworks(Context context) {
        if (this.scanCache.size() > 0) {
            Iterator it = this.scanCache.iterator();
            while (it.hasNext()) {
                reportUsableNetwork((ScanResult) it.next());
            }
            this.scanCache.clear();
            if (this.networkSearchListener != null) {
                this.networkSearchListener.onScanningCanceled();
                return;
            }
            return;
        }
        moveToState(ProvState.SCANNINGFORNETWORKS);
        startWifiScan(context);
    }

    public final void stop() {
        moveToState(ProvState.IDLE);
    }

    public final ProvState getState() {
        return this.provState;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.provState);
        if (this.macPrefixes != null) {
            dest.writeInt(this.macPrefixes.length);
            dest.writeStringArray(this.macPrefixes);
            return;
        }
        dest.writeInt(-1);
    }

    public final void onPause(Context context) {
        try {
            context.unregisterReceiver(this.scanResultsReceiver);
        } catch (IllegalArgumentException e) {
        }
        this.broadcastManager.unregisterReceiver(this.provServiceReceiver);
        this.handler.removeCallbacks(this.timeoutRunnable);
    }

    public final void onResume(Context context) {
        this.wifiManager = (WifiManager) context.getSystemService("wifi");
        this.broadcastManager = LocalBroadcastManager.getInstance(context);
        switch (this.provState) {
            case SCANNINGFORDEVICES:
                this.provState = ProvState.IDLE;
                startSearchingForUnProvisionedDevices(this.macPrefixes, context);
                break;
            case SCANNINGFORNETWORKS:
                this.provState = ProvState.IDLE;
                startSearchingForUsableNetworks(context);
                break;
            case PROVISIONING:
                this.broadcastManager.registerReceiver(this.provServiceReceiver, provServiceIntentFilter);
                Intent i = new Intent(context, MrvlProvService.class);
                i.setAction(MrvlProvService.ACTION_PING);
                context.startService(i);
                break;
            case WAITINGFORWIFI:
                if (!this.wifiManager.isWifiEnabled()) {
                    this.provState = ProvState.IDLE;
                    waitForWifiToBeEnabled(context);
                    break;
                }
                this.wifiEnabledListener.onWifiEnabled();
                break;
        }
        this.timeoutRunnable = new TimeoutRunnable(context);
        this.handler.post(this.timeoutRunnable);
    }

    public final boolean isWifiEnabled() {
        return this.wifiManager.isWifiEnabled();
    }

    public final void setWifiEnabledListener(WifiEnabledListener listener) {
        this.wifiEnabledListener = listener;
    }

    public final void waitForWifiToBeEnabled(Context context) {
        moveToState(ProvState.WAITINGFORWIFI);
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra("wifi_state", 4) == 3) {
                    MrvlProv.this.moveToState(ProvState.IDLE);
                    context.unregisterReceiver(this);
                    MrvlProv.this.wifiEnabledListener.onWifiEnabled();
                }
            }
        }, wifiStateintentFilter);
    }

    static /* synthetic */ boolean access$700(MrvlProv x0, ScanResult x1) {
        if (!(TextUtils.isEmpty(x1.SSID) || x0.isThisAnIro(x1))) {
            Security security;
            if (x1.capabilities.contains("WEP")) {
                security = Security.SECURITY_WEP;
            } else if (x1.capabilities.contains("PSK")) {
                security = Security.SECURITY_PSK;
            } else if (x1.capabilities.contains("EAP")) {
                security = Security.SECURITY_EAP;
            } else {
                security = Security.SECURITY_NONE;
            }
            if (security != Security.SECURITY_WEP && security != Security.SECURITY_NONE) {
                return true;
            }
            CrashReporterUtils.logDebug(TAG, "unusable security type " + security);
        }
        return false;
    }

    static /* synthetic */ void access$1200(MrvlProv x0, Context x1, boolean x2, FailureReason x3, String x4, String x5, Model x6) {
        synchronized (x0) {
            Intent intent = new Intent(x1, MrvlProvService.class);
            intent.setAction(MrvlProvService.ACTION_SHUTDOWN);
            x1.startService(intent);
            if (x2) {
                x0.moveToState(ProvState.IDLE);
                if (x0.provisioningCallback != null) {
                    x0.provisioningCallback.onProvisioningCompleted(x4, x5, x6);
                }
            } else {
                x0.moveToState(ProvState.IDLE);
                if (x0.provisioningCallback != null) {
                    x0.provisioningCallback.onProvisioningFailed(x3);
                }
            }
        }
    }
}
