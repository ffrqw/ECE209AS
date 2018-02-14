package com.rachio.iro.gen2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.rachio.iro.IroApplication;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.PushPull;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.gen2.HttpUtils.HttpResponse;
import com.rachio.iro.gen2.model.BaseResponse;
import com.rachio.iro.gen2.model.BirthDeviceGeneration2;
import com.rachio.iro.gen2.model.ConnectionStatus;
import com.rachio.iro.gen2.model.Epoch;
import com.rachio.iro.gen2.model.KeyPair;
import com.rachio.iro.gen2.model.NetworkRequest;
import com.rachio.iro.gen2.model.NetworkResponse;
import com.rachio.iro.gen2.model.ProvAckRequest;
import com.rachio.iro.gen2.model.ProvData;
import com.rachio.iro.gen2.model.RachioResponse;
import com.rachio.iro.gen2.model.SecureContainer;
import com.rachio.iro.gen2.model.SessionRequest;
import com.rachio.iro.gen2.model.SessionResponse;
import com.rachio.iro.gen2.model.SessionStatus;
import com.rachio.iro.model.apionly.DeviceResponse;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.device.ShallowDevice.Model;
import com.rachio.iro.model.user.User;
import com.rachio.iro.utils.CrashReporterUtils;
import com.rachio.iro.utils.LocationUtils;
import com.rachio.iro.utils.StringUtils;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MrvlProvService extends Service {
    public static final String ACTION_PING = "ping";
    public static final String ACTION_PROVISION = "provision";
    public static final String ACTION_SHUTDOWN = "shutdown";
    public static final String BROADCAST_FAILED = "failed";
    public static final String BROADCAST_STATECHANGE = "statechange";
    public static final String BROADCAST_SUCCESS = "success";
    public static final String BROADCAST_TIMEOUTWAITINGFORINTERNET = "timeoutwaitingforinternet";
    public static final String EXTRA_IN_PROVDATA = "provdata";
    public static final String EXTRA_OUT_DEVICEID = "deviceid";
    public static final String EXTRA_OUT_DEVICEMODEL = "devicemodel";
    public static final String EXTRA_OUT_DEVICEZIP = "devicezip";
    public static final String EXTRA_OUT_NEWSTATE = "newstate";
    public static final String EXTRA_OUT_REASON = "reason";
    private static final String TAG = MrvlProvService.class.getName();
    private LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
    private ConnectivityManager connectivityManager;
    Database database;
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    PrefsWrapper prefsWrapper;
    RestClient restClient;
    private long startedConnectingToIroAPAt = 0;
    private volatile State state = State.IDLE;
    private ProvisioningThread thread;
    private WakeLock wakeLock;
    private WifiLock wifiLock;
    private WifiManager wifiManager;

    public enum FailureReason {
        CONNECTTODEVICE("Check connection to device"),
        CONNECTIONDOESNTWORK("Couldn't communicate with device"),
        CREATESESSION("Please validate serial number is correct, power cycle Iro, wait for 2nd LED to blink and try again."),
        CONFIGURE("Failed to configure device"),
        ALREADYCONFIGURED("Please power cycle device, wait for 2nd LED to blink and try again."),
        CONNECTTOAP("Check WiFi settings"),
        CONNECTTOAP_AUTH("WiFi password was incorrect, please power cycle device and try again."),
        CONNECTTOAP_NOTFOUND("The device was unable to find the selected wifi network."),
        CONNECTTOAP_DHCP("The device was unable to get an IP address from the network."),
        SESSION_EXISTS("Please power cycle device, wait for 2nd LED to blink and try again."),
        NOINTERNET("Timedout waiting for an internet connection to birth with."),
        BIRTH("Failed to assign device to user."),
        DEVICEISALREADYBIRTHED("Device is already registered to a different account. Power cycle Iro and please contact support@rachio.com to transfer the Iro to another account."),
        CONFIRM("Failed to confirm settings on device"),
        COMEONLINE("Device didn't come online"),
        CREATENETWORK("Failed to create network for device"),
        BADSALT("Device has bad salt"),
        CONNECTTODEVICE_IRONETWORKEXISTS("Device exists in wifi settings, please forget the network from your wifi settings");
        
        final String description;

        private FailureReason(String description) {
            this.description = description;
        }
    }

    private static class HandlerResult {
        final long delay;
        final boolean fatal;
        final FailureReason reason;
        final boolean success;

        public HandlerResult() {
            this(true, false, 0, null);
        }

        public HandlerResult(long delay) {
            this(false, false, delay, null);
        }

        public HandlerResult(FailureReason reason) {
            this(false, true, 0, reason);
        }

        private HandlerResult(boolean success, boolean fatal, long delay, FailureReason reason) {
            this.success = success;
            this.fatal = fatal;
            this.delay = delay;
            this.reason = reason;
        }
    }

    private static final class ProvStateBundle {
        int birthTries;
        int checkTries;
        int connectTries;
        public String deviceId;
        public Model deviceModel;
        public String deviceZip;
        FailureReason failureReason;
        private int networkId;
        public int previouslyConnectedNetwork;
        public List<Integer> previouslyEnabledNetworks;
        public int sessionId;
        public byte[] sessionKey;
        int waitingForInternetConnectionTries;

        private ProvStateBundle() {
            this.networkId = -1;
            this.connectTries = 0;
            this.checkTries = 0;
            this.birthTries = 0;
            this.previouslyConnectedNetwork = -1;
            this.previouslyEnabledNetworks = new ArrayList();
        }
    }

    private class ProvisioningThread extends Thread {
        final ProvStateBundle bundle = new ProvStateBundle();
        final ProvData provData;

        public ProvisioningThread(ProvData provData) {
            this.provData = provData;
        }

        private void releaseLocks() {
            MrvlProvService.this.wakeLock.release();
            MrvlProvService.this.wifiLock.release();
        }

        public void run() {
            super.run();
            MrvlProvService.this.wakeLock.acquire();
            MrvlProvService.this.wifiLock.acquire();
            while (MrvlProvService.this.state != State.FINISHED) {
                try {
                    HandlerResult result = (HandlerResult) MrvlProvService.this.state.handler.invoke(MrvlProvService.this, new Object[]{this.provData, this.bundle});
                    if (result.success) {
                        MrvlProvService.this.moveToNextState();
                        if (MrvlProvService.this.state == State.FINISHED) {
                            releaseLocks();
                            MrvlProvService.this.broadcastSuccess();
                        }
                    } else if (result.fatal) {
                        releaseLocks();
                        MrvlProvService.this.disconnectFromIroAP();
                        this.bundle.failureReason = result.reason;
                        MrvlProvService.this.broadcastFailure();
                        CrashReporterUtils.silentException(new ProvFailureException(result.reason));
                    } else {
                        sleep(result.delay);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private enum SecureSessionStatus {
        CANCREATE,
        ACTIVE,
        ERROR
    }

    public enum State {
        IDLE(null, "Starting..."),
        CONNECTINGTODEVICE("connectToDevice", "Connecting to device..."),
        GETTINGSESSION("createSession", "Creating secure session..."),
        SETTINGTIME("setTime", "Updating gen2 time..."),
        CONFIGURING("configure", "Sending WiFi configuration to device..."),
        RECONNECTTODEVICE("connectToDevice", "Reconnecting to device"),
        CHECKING("check", "Waiting for device to connect to WiFi..."),
        WAITINGUNTILONLINE("waitUntilOnline", "Waiting for device to connect to cloud..."),
        CONFIRMING("confirm", "Finalizing settings..."),
        WAITFORINTERNETCONNECTION("waitForInternet", "Waiting for internet connection..."),
        BIRTHING("birth", "Assigning device to user..."),
        FINISHED(null, "Finished");
        
        public final String description;
        final Method handler;

        private State(String handlerName, String description) {
            if (handlerName != null) {
                try {
                    this.handler = MrvlProvService.class.getDeclaredMethod(handlerName, new Class[]{ProvData.class, ProvStateBundle.class});
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                }
            }
            this.handler = null;
            this.description = description;
        }
    }

    public void onCreate() {
        super.onCreate();
        this.wakeLock = ((PowerManager) getSystemService("power")).newWakeLock(26, TAG);
        IroApplication.get(this).component().inject(this);
        this.connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.wifiManager = (WifiManager) getSystemService("wifi");
        this.wifiLock = this.wifiManager.createWifiLock(1, TAG);
    }

    public String generateAndroidKey(String uniqueId, String hkPin) {
        String plainKey = uniqueId + hkPin;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(plainKey.getBytes());
            String digestString = new BigInteger(1, digest.digest()).toString(16);
            int pad = 32 - digestString.length();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < pad; i++) {
                sb.append("0");
            }
            sb.append(digestString);
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private RachioResponse getRachioInfo() {
        String rachioUrl = Uri.parse("http://192.168.10.1/rachio/iroinfo").toString();
        bindToWifiNetwork();
        HttpResponse iroInfoResponse = HttpUtils.doHttpGet(rachioUrl);
        if (iroInfoResponse != null && iroInfoResponse.haveContent()) {
            try {
                return (RachioResponse) this.gson.fromJson(iroInfoResponse.content, RachioResponse.class);
            } catch (JsonSyntaxException jse) {
                CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(jse);
            }
        }
        return null;
    }

    private SecureSessionStatus canCreateSession() {
        SecureSessionStatus sessionStatus = SecureSessionStatus.ERROR;
        HttpResponse httpResponse = HttpUtils.doHttpGet("http://192.168.10.1/prov/secure-session");
        if (httpResponse == null) {
            CrashReporterUtils.logDebug(TAG, "failed to get session status");
            return sessionStatus;
        } else if (httpResponse.code == Callback.DEFAULT_DRAG_ANIMATION_DURATION && httpResponse.haveContent()) {
            SessionStatus status = (SessionStatus) this.gson.fromJson(httpResponse.content, SessionStatus.class);
            if (status == null) {
                CrashReporterUtils.logDebug(TAG, "failed to parse session status");
                return sessionStatus;
            } else if (status.active) {
                CrashReporterUtils.logDebug(TAG, "a session is active");
                return SecureSessionStatus.ACTIVE;
            } else {
                CrashReporterUtils.logDebug(TAG, "can create a new secure session");
                return SecureSessionStatus.CANCREATE;
            }
        } else {
            CrashReporterUtils.logDebug(TAG, "non-200 response code for session status; " + httpResponse.code);
            return sessionStatus;
        }
    }

    private boolean waitForConnectionToSettle() {
        int consecutiveSuccesses = 0;
        int consecutiveFailures = 0;
        for (int i = 0; i < 20; i++) {
            CrashReporterUtils.logDebug(TAG, "waiting for connection to settle, attempt " + i + " consecutive successes " + consecutiveSuccesses + " consecutive failures " + consecutiveFailures);
            HttpResponse httpResponse = HttpUtils.doHttpGet("http://192.168.10.1/prov/secure-session");
            if (httpResponse == null || !httpResponse.haveContent()) {
                consecutiveSuccesses = 0;
                consecutiveFailures++;
                bindToWifiNetwork();
                if (consecutiveFailures == 6) {
                    return false;
                }
            }
            consecutiveSuccesses++;
            consecutiveFailures = 0;
            if (consecutiveSuccesses == 4) {
                return true;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
        return false;
    }

    private void broadcastState() {
        Intent i = new Intent(BROADCAST_STATECHANGE);
        i.putExtra(EXTRA_OUT_NEWSTATE, this.state);
        this.broadcastManager.sendBroadcast(i);
    }

    private void broadcastSuccess() {
        Intent i = new Intent(BROADCAST_SUCCESS);
        i.putExtra(EXTRA_OUT_DEVICEID, this.thread.bundle.deviceId);
        i.putExtra(EXTRA_OUT_DEVICEZIP, this.thread.bundle.deviceZip);
        i.putExtra(EXTRA_OUT_DEVICEMODEL, this.thread.bundle.deviceModel);
        this.broadcastManager.sendBroadcast(i);
    }

    private void moveToNextState() {
        moveToNextState(true);
    }

    private void moveToNextState(boolean broadcast) {
        synchronized (this.state) {
            State last = this.state;
            this.state = State.values()[Math.min(this.state.ordinal() + 1, State.values().length - 1)];
            if (broadcast) {
                broadcastState();
            }
            CrashReporterUtils.logDebug(TAG, "state was " + last + " is now " + this.state);
            this.thread.bundle.connectTries = 0;
        }
    }

    private void broadcastFailure() {
        synchronized (this.state) {
            this.state = State.FINISHED;
            Intent i = new Intent(BROADCAST_FAILED);
            i.putExtra(EXTRA_OUT_REASON, this.thread.bundle.failureReason);
            this.broadcastManager.sendBroadcast(i);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        CrashReporterUtils.logDebug(TAG, "handling action " + action);
        synchronized (this.state) {
            if (action.equals(ACTION_PROVISION)) {
                if (this.state == State.IDLE) {
                    this.thread = new ProvisioningThread((ProvData) intent.getSerializableExtra(EXTRA_IN_PROVDATA));
                    moveToNextState();
                    this.thread.start();
                } else {
                    throw new IllegalStateException("tried to start provisioning when not idle!");
                }
            } else if (action.equals(ACTION_SHUTDOWN)) {
                if (this.state == State.FINISHED) {
                    stopSelf();
                } else {
                    throw new IllegalStateException("tried to shutdown service while in state " + this.state);
                }
            } else if (action.equals(ACTION_PING)) {
                broadcastState();
                if (this.state == State.FINISHED) {
                    if (this.thread.bundle.failureReason != null) {
                        broadcastFailure();
                    } else {
                        broadcastSuccess();
                    }
                }
            }
        }
        return 2;
    }

    private void bindToWifiNetwork() {
        if (VERSION.SDK_INT >= 21) {
            Network wifi = null;
            for (Network n : this.connectivityManager.getAllNetworks()) {
                NetworkInfo ni = this.connectivityManager.getNetworkInfo(n);
                if (ni != null && ni.getType() == 1) {
                    wifi = n;
                    break;
                }
            }
            if (wifi == null) {
                CrashReporterUtils.logDebug(TAG, "didn't find the wifi network :/");
                return;
            }
            boolean apparentlyConnectedToIro = false;
            WifiInfo wifiInfo = this.wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String ssid = wifiInfo.getSSID();
                if (ssid != null) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                    CrashReporterUtils.logDebug(TAG, "connected to " + ssid);
                    if (ssid.equals(this.thread.provData.deviceName)) {
                        CrashReporterUtils.logDebug(TAG, "this seems to be the iro we were looking for");
                        apparentlyConnectedToIro = true;
                    }
                } else {
                    CrashReporterUtils.logDebug(TAG, "ssid is null");
                }
            } else {
                CrashReporterUtils.logDebug(TAG, "wifi info is null");
            }
            if (apparentlyConnectedToIro) {
                CrashReporterUtils.logDebug(TAG, "binding http utils to wifi network");
                HttpUtils.setNetwork(wifi);
                return;
            }
            CrashReporterUtils.logDebug(TAG, "unbinding network");
            HttpUtils.setNetwork(null);
        }
    }

    private boolean isConnectedToDevice(ProvData provData) {
        WifiInfo wi = this.wifiManager.getConnectionInfo();
        if (!(wi == null || wi.getSSID() == null)) {
            String ssid = wi.getSSID().replaceAll("\"", "");
            CrashReporterUtils.logDebug(TAG, "current ssid " + ssid + " want " + provData.deviceName);
            if (ssid.equals(provData.deviceName)) {
                CrashReporterUtils.logDebug(TAG, "connected to device");
                CrashReporterUtils.logDebug(TAG, this.wifiManager.getDhcpInfo().toString());
                return true;
            }
        }
        return false;
    }

    private void clearNetwork() {
        if (this.thread.bundle.networkId != -1) {
            CrashReporterUtils.logDebug(TAG, "removed iro network");
            this.wifiManager.removeNetwork(this.thread.bundle.networkId);
            this.thread.bundle.networkId = -1;
        }
    }

    private void disconnectFromIroAP() {
        if (this.thread.bundle.networkId != -1) {
            if (!this.wifiManager.disconnect()) {
                CrashReporterUtils.logDebug(TAG, "disconnect failed");
            }
            clearNetwork();
            this.prefsWrapper.clearGen2NetworkId();
            List<WifiConfiguration> networks = this.wifiManager.getConfiguredNetworks();
            List<Integer> ids = new ArrayList();
            if (networks != null) {
                for (WifiConfiguration wi : networks) {
                    ids.add(Integer.valueOf(wi.networkId));
                }
            }
            if (this.thread.bundle.previouslyConnectedNetwork != -1) {
                if (ids.contains(Integer.valueOf(this.thread.bundle.previouslyConnectedNetwork))) {
                    if (this.wifiManager.enableNetwork(this.thread.bundle.previouslyConnectedNetwork, false)) {
                        CrashReporterUtils.logDebug(TAG, "previously connected network doesn't exist anymore!");
                    } else {
                        CrashReporterUtils.logDebug(TAG, "failed to reenable previously connected network");
                    }
                }
                this.thread.bundle.previouslyConnectedNetwork = -1;
            }
            if (this.thread.bundle.previouslyEnabledNetworks.size() > 0) {
                for (Integer id : this.thread.bundle.previouslyEnabledNetworks) {
                    if (ids.contains(id)) {
                        CrashReporterUtils.logDebug(TAG, "re-enabling " + id);
                        if (!this.wifiManager.enableNetwork(id.intValue(), false)) {
                            CrashReporterUtils.logDebug(TAG, "failed to re-enable " + id);
                        }
                    } else {
                        CrashReporterUtils.logDebug(TAG, "previously enabled network doesn't exist anymore");
                    }
                }
                this.thread.bundle.previouslyEnabledNetworks.clear();
            }
            if (!this.wifiManager.reconnect()) {
                CrashReporterUtils.logDebug(TAG, "reconnect failed");
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.thread != null) {
            this.thread.interrupt();
        }
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        if (this.wifiLock.isHeld()) {
            this.wifiLock.release();
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private String decryptSecureResponse(String response, byte[] secret) {
        GeneralSecurityException e;
        SecureContainer container = null;
        try {
            container = (SecureContainer) new Gson().fromJson(response, SecureContainer.class);
        } catch (JsonSyntaxException jse) {
            CrashReporterUtils.silentExceptionThatCrashesDebugBuilds(jse);
        }
        if (container == null) {
            return null;
        }
        if (container.iv == null) {
            CrashReporterUtils.logDebug(TAG, "response doesn't contain an iv, probably an error");
            return null;
        }
        try {
            byte[] decrypteddata = createCipher(secret, stringToByteBuff(container.iv), 2).doFinal(stringToByteBuff(container.data));
            try {
                if (Arrays.equals(MessageDigest.getInstance("SHA-512").digest(decrypteddata), stringToByteBuff(container.checksum))) {
                    String decrytpedString = new String(decrypteddata);
                    CrashReporterUtils.logDebug(TAG, "decrypted string: " + decrytpedString);
                    return decrytpedString;
                }
                CrashReporterUtils.logDebug(TAG, "checksum of decrytped data doesn't match checksum from device");
                return null;
            } catch (NoSuchAlgorithmException e2) {
                throw new RuntimeException();
            }
        } catch (IllegalBlockSizeException e3) {
            e = e3;
            throw new RuntimeException(e);
        } catch (BadPaddingException e4) {
            e = e4;
            throw new RuntimeException(e);
        }
    }

    private static Cipher createCipher(byte[] aeskey, byte[] iv, int mode) {
        GeneralSecurityException e;
        SecretKeySpec key = new SecretKeySpec(aeskey, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(mode, key, ivSpec);
            return cipher;
        } catch (NoSuchAlgorithmException e2) {
            e = e2;
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e3) {
            e = e3;
            throw new RuntimeException(e);
        } catch (InvalidKeyException e4) {
            e = e4;
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e5) {
            e = e5;
            throw new RuntimeException(e);
        }
    }

    private static byte[] stringToByteBuff(String str) {
        if (str.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        byte[] result = new byte[(str.length() / 2)];
        for (int i = 0; i < result.length; i++) {
            int offset = i << 1;
            result[i] = (byte) Integer.parseInt(str.substring(offset, offset + 2), 16);
        }
        return result;
    }

    private byte[] generateSharedSecret(SessionResponse sessionResponse, KeyPair keypair, String pin, byte[] randomNumber) {
        GeneralSecurityException e;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            if (sessionResponse.checksum == null) {
                throw new IllegalStateException();
            }
            if (Arrays.equals(md.digest(randomNumber), stringToByteBuff(sessionResponse.checksum))) {
                byte[] devicepub = stringToByteBuff(sessionResponse.device_pub_key);
                byte[] tmpsharedsecret = new byte[32];
                Curve25519.curve(tmpsharedsecret, keypair.privatekey, devicepub);
                byte[] sharedsecrethash = md.digest(tmpsharedsecret);
                try {
                    byte[] pinhash = md.digest(pin.getBytes("US-ASCII"));
                    byte[] aeskey = new byte[16];
                    for (int i = 0; i < 16; i++) {
                        aeskey[i] = (byte) (pinhash[i] ^ sharedsecrethash[i]);
                    }
                    try {
                        if (Arrays.equals(stringToByteBuff(sessionResponse.data), createCipher(aeskey, stringToByteBuff(sessionResponse.iv), 1).doFinal(randomNumber))) {
                            return aeskey;
                        }
                        CrashReporterUtils.logDebug(TAG, "device encrypted data doesn't match ours");
                        return null;
                    } catch (IllegalBlockSizeException e2) {
                        e = e2;
                        throw new RuntimeException(e);
                    } catch (BadPaddingException e3) {
                        e = e3;
                        throw new RuntimeException(e);
                    }
                } catch (UnsupportedEncodingException e1) {
                    throw new RuntimeException(e1);
                }
            }
            CrashReporterUtils.logDebug(TAG, "data checksum from device is bad");
            return null;
        } catch (NoSuchAlgorithmException e4) {
            throw new RuntimeException();
        }
    }

    private String prepareSecureRequest(byte[] secret, Object data) {
        GeneralSecurityException e;
        String dataJson = this.gson.toJson(data);
        CrashReporterUtils.logDebug(TAG, "encrypted payload json: " + dataJson);
        byte[] iv = new byte[16];
        new Random().nextBytes(iv);
        Cipher cipher = createCipher(secret, iv, 1);
        try {
            byte[] rawdata = dataJson.getBytes("US-ASCII");
            try {
                byte[] encryptedData = cipher.doFinal(rawdata);
                try {
                    byte[] checksum = MessageDigest.getInstance("SHA-512").digest(rawdata);
                    SecureContainer request = new SecureContainer();
                    request.data = bufferToString(encryptedData);
                    request.checksum = bufferToString(checksum);
                    request.iv = bufferToString(iv);
                    return this.gson.toJson(request);
                } catch (NoSuchAlgorithmException e2) {
                    throw new RuntimeException();
                }
            } catch (IllegalBlockSizeException e3) {
                e = e3;
                throw new RuntimeException(e);
            } catch (BadPaddingException e4) {
                e = e4;
                throw new RuntimeException(e);
            }
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    private static String bufferToString(byte[] buff) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buff.length; i++) {
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(buff[i])}));
        }
        return sb.toString();
    }

    private static KeyPair generateKeyPair() {
        byte[] priv = new byte[32];
        new Random().nextBytes(priv);
        byte[] pub = new byte[32];
        Curve25519.keygen(pub, null, priv);
        return new KeyPair(priv, pub);
    }

    private static byte[] generateRandomNumber() {
        byte[] output = new byte[64];
        new Random().nextBytes(output);
        return output;
    }

    private void unbindFromWifiNetwork() {
        if (VERSION.SDK_INT >= 21) {
            HttpUtils.setNetwork(null);
        }
    }

    public HandlerResult connectToDevice(ProvData provData, ProvStateBundle bundle) {
        if (isConnectedToDevice(provData)) {
            bindToWifiNetwork();
            if (waitForConnectionToSettle()) {
                return new HandlerResult();
            }
            if (bundle.connectTries >= 10) {
                return new HandlerResult(FailureReason.CONNECTIONDOESNTWORK);
            }
            bundle.connectTries++;
            return new HandlerResult(1000);
        } else if (bundle.connectTries >= 10) {
            return new HandlerResult(FailureReason.CONNECTTODEVICE);
        } else {
            String encodedSSID = String.format("\"%s\"", new Object[]{provData.deviceName});
            if (bundle.networkId == -1) {
                CrashReporterUtils.logDebug(TAG, "Creating network for iro");
                int highestPriority = 0;
                List<WifiConfiguration> configuredNetworks = this.wifiManager.getConfiguredNetworks();
                if (configuredNetworks != null) {
                    int lingeringId = -1;
                    for (WifiConfiguration wc : configuredNetworks) {
                        highestPriority = Math.max(wc.priority, highestPriority);
                        if (TextUtils.equals(encodedSSID, wc.SSID)) {
                            lingeringId = wc.networkId;
                        }
                    }
                    if (lingeringId != -1) {
                        CrashReporterUtils.logDebug(TAG, "found lingering config for this iro, removing");
                        if (!this.wifiManager.removeNetwork(lingeringId)) {
                            return new HandlerResult(FailureReason.CONNECTTODEVICE_IRONETWORKEXISTS);
                        }
                    }
                    for (WifiConfiguration wc2 : configuredNetworks) {
                        CrashReporterUtils.logDebug(TAG, wc2.SSID + " " + wc2.status);
                        if (wc2.status == 2) {
                            CrashReporterUtils.logDebug(TAG, "stashing previously enabled wifi network " + wc2.SSID);
                            bundle.previouslyEnabledNetworks.add(Integer.valueOf(wc2.networkId));
                        } else if (wc2.status == 0) {
                            CrashReporterUtils.logDebug(TAG, "stashing current network " + wc2.SSID);
                            bundle.previouslyConnectedNetwork = wc2.networkId;
                        }
                    }
                }
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = encodedSSID;
                wifiConfig.allowedKeyManagement.set(0);
                wifiConfig.allowedAuthAlgorithms.set(0);
                wifiConfig.priority = highestPriority + 1;
                bundle.networkId = this.wifiManager.addNetwork(wifiConfig);
                this.prefsWrapper.stashGen2NetworkId(bundle.networkId);
            }
            if (bundle.networkId == -1) {
                return new HandlerResult(FailureReason.CREATENETWORK);
            }
            CrashReporterUtils.logDebug(TAG, "trying to connect to iro");
            this.wifiManager.disconnect();
            this.wifiManager.enableNetwork(bundle.networkId, true);
            this.startedConnectingToIroAPAt = System.currentTimeMillis();
            this.wifiManager.reconnect();
            bundle.connectTries++;
            return new HandlerResult(20000);
        }
    }

    private boolean tryToReuseSession(ProvStateBundle bundle) {
        int sessionId = this.prefsWrapper.getStashedSessionId();
        if (sessionId != -1) {
            CrashReporterUtils.logDebug(TAG, "trying to reuse stashed session");
            byte[] sessionKey = this.prefsWrapper.getStashedSessionKey();
            String url = buildSecureUrl("/prov/net-info", sessionId);
            bindToWifiNetwork();
            HttpResponse httpResponse = HttpUtils.doHttpGet(url);
            if (httpResponse != null && httpResponse.haveContent()) {
                if (decryptSecureResponse(httpResponse.content, sessionKey) != null) {
                    CrashReporterUtils.logDebug(TAG, "decrypted response, reusing session");
                    bundle.sessionId = sessionId;
                    bundle.sessionKey = sessionKey;
                    return true;
                }
                CrashReporterUtils.logDebug(TAG, "failed to decrypt response, session probably was probably created by a bad serial number");
            }
        } else {
            CrashReporterUtils.logDebug(TAG, "no stashed session");
        }
        return false;
    }

    public HandlerResult createSession(ProvData provData, ProvStateBundle bundle) {
        switch (canCreateSession()) {
            case CANCREATE:
                RachioResponse iroInfo = getRachioInfo();
                if (iroInfo != null) {
                    if (TextUtils.isEmpty(iroInfo.salt)) {
                        return new HandlerResult(FailureReason.BADSALT);
                    }
                    CrashReporterUtils.logDebug(TAG, "trying to create a session with pin " + provData.deviceSerialNumber);
                    KeyPair keypair = generateKeyPair();
                    byte[] random = generateRandomNumber();
                    SessionRequest sr = new SessionRequest();
                    sr.client_pub_key = keypair.publickeyAsHex;
                    sr.random_sig = bufferToString(random);
                    HttpResponse httpResponse = HttpUtils.doHttpPost("http://192.168.10.1/prov/secure-session", "application/x-www-form-urlencoded", this.gson.toJson(sr));
                    if (httpResponse != null && httpResponse.haveContent()) {
                        SessionResponse response = (SessionResponse) this.gson.fromJson(httpResponse.content, SessionResponse.class);
                        bundle.sessionId = response.session_id;
                        bundle.sessionKey = generateSharedSecret(response, keypair, generateAndroidKey(iroInfo.salt, provData.deviceSerialNumber), random);
                        if (bundle.sessionKey != null) {
                            this.prefsWrapper.stashProvSession(bundle.sessionId, bundle.sessionKey);
                            return new HandlerResult();
                        }
                    }
                }
                break;
            case ACTIVE:
                if (tryToReuseSession(bundle)) {
                    return new HandlerResult();
                }
                return new HandlerResult(FailureReason.SESSION_EXISTS);
        }
        return new HandlerResult(FailureReason.CREATESESSION);
    }

    public HandlerResult setTime(ProvData provData, ProvStateBundle bundle) {
        HttpUtils.doHttpPost("http://192.168.10.1/sys/time", "application/x-www-form-urlencoded", this.gson.toJson(new Epoch()));
        return new HandlerResult();
    }

    public HandlerResult configure(ProvData provData, ProvStateBundle bundle) {
        if (getConnectionStatus(bundle).configured == 1) {
            CrashReporterUtils.logDebug(TAG, "already configured!!");
            return new HandlerResult(FailureReason.ALREADYCONFIGURED);
        }
        NetworkRequest nr = new NetworkRequest();
        nr.ssid = provData.networkSsid;
        nr.key = provData.networkPassword;
        nr.security = provData.networkSecurity;
        HttpResponse httpResponse = HttpUtils.doHttpPost(Uri.parse("http://192.168.10.1/prov/network").buildUpon().appendQueryParameter("session_id", Integer.toString(bundle.sessionId)).toString(), "application/x-www-form-urlencoded", prepareSecureRequest(bundle.sessionKey, nr));
        boolean configured = false;
        if (httpResponse != null && httpResponse.haveContent()) {
            NetworkResponse response = (NetworkResponse) this.gson.fromJson(decryptSecureResponse(httpResponse.content, bundle.sessionKey), NetworkResponse.class);
            if (response != null && response.error == 0) {
                CrashReporterUtils.logDebug(TAG, "network configured");
                configured = true;
            }
        }
        if (configured) {
            return new HandlerResult();
        }
        return new HandlerResult(FailureReason.CONFIGURE);
    }

    private static String buildSecureUrl(String endpoint, int sessionId) {
        return Uri.parse("http://192.168.10.1" + endpoint).buildUpon().appendQueryParameter("session_id", Integer.toString(sessionId)).toString();
    }

    public ConnectionStatus getConnectionStatus(ProvStateBundle bundle) {
        String url = buildSecureUrl("/prov/net-info", bundle.sessionId);
        bindToWifiNetwork();
        HttpResponse httpResponse = HttpUtils.doHttpGet(url);
        if (httpResponse == null || !httpResponse.haveContent()) {
            return null;
        }
        return (ConnectionStatus) this.gson.fromJson(decryptSecureResponse(httpResponse.content, bundle.sessionKey), ConnectionStatus.class);
    }

    public HandlerResult check(ProvData provData, ProvStateBundle bundle) {
        ConnectionStatus response = getConnectionStatus(bundle);
        if (response != null) {
            if (response.configured == 1 && response.status == 2) {
                CrashReporterUtils.logDebug(TAG, "connected to network");
                return new HandlerResult();
            } else if (response.configured == 1 && response.failure_cnt > 0) {
                if (StringUtils.equals(response.failure, "auth_failed")) {
                    CrashReporterUtils.logDebug(TAG, "auth failure");
                    return new HandlerResult(FailureReason.CONNECTTOAP_AUTH);
                } else if (StringUtils.equals(response.failure, "network_not_found")) {
                    CrashReporterUtils.logDebug(TAG, "not found failure");
                    return new HandlerResult(FailureReason.CONNECTTOAP_NOTFOUND);
                } else if (StringUtils.equals(response.failure, "dhcp_failed")) {
                    CrashReporterUtils.logDebug(TAG, "dhcp failure");
                    return new HandlerResult(FailureReason.CONNECTTOAP_DHCP);
                }
            }
        }
        bundle.checkTries++;
        CrashReporterUtils.logDebug(TAG, "failed to connect");
        if (bundle.checkTries < 12) {
            return new HandlerResult(10000);
        }
        return new HandlerResult(FailureReason.CONNECTTOAP);
    }

    public HandlerResult waitUntilOnline(ProvData provData, ProvStateBundle bundle) {
        boolean online = false;
        for (int timeout = 300000; timeout > 0; timeout -= 10000) {
            RachioResponse iroInfo = getRachioInfo();
            if (iroInfo != null && iroInfo.mqttonline) {
                online = true;
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
            }
        }
        if (online) {
            return new HandlerResult();
        }
        return new HandlerResult(FailureReason.COMEONLINE);
    }

    public HandlerResult waitForInternet(ProvData provData, ProvStateBundle bundle) {
        if (bundle.waitingForInternetConnectionTries == 0) {
            disconnectFromIroAP();
            bundle.waitingForInternetConnectionTries++;
            return new HandlerResult(20000);
        } else if (provData.wifiSettingsOnly) {
            return new HandlerResult();
        } else {
            List<WifiConfiguration> wn = this.wifiManager.getConfiguredNetworks();
            boolean haveEnabledWifi = false;
            boolean haveCurrentWifi = false;
            if (wn != null) {
                for (WifiConfiguration wc : wn) {
                    if (wc.status == 2) {
                        haveEnabledWifi = true;
                    } else if (wc.status == 0) {
                        haveCurrentWifi = true;
                    }
                    if (haveEnabledWifi && haveCurrentWifi) {
                        break;
                    }
                }
            }
            CrashReporterUtils.logDebug(TAG, "wifi configuration state haveEnabled " + haveEnabledWifi + " haveCurrent " + haveCurrentWifi);
            NetworkInfo activeNetwork = this.connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                return new HandlerResult();
            }
            bundle.waitingForInternetConnectionTries++;
            if (bundle.waitingForInternetConnectionTries < 20) {
                if (!this.wifiManager.reconnect()) {
                    CrashReporterUtils.logDebug(TAG, "reconnect failed");
                }
                CrashReporterUtils.logDebug(TAG, "still waiting for connection to come back");
                if (bundle.waitingForInternetConnectionTries == 6) {
                    CrashReporterUtils.logDebug(TAG, "Still no connection, toggling wifi");
                    this.wifiManager.setWifiEnabled(false);
                    try {
                        Thread.sleep(15000);
                    } catch (Exception e) {
                    }
                    this.wifiManager.setWifiEnabled(true);
                } else if (bundle.waitingForInternetConnectionTries == 10) {
                    CrashReporterUtils.logDebug(TAG, "Still no connection, tell user to check it out");
                    this.broadcastManager.sendBroadcast(new Intent(BROADCAST_TIMEOUTWAITINGFORINTERNET));
                }
                return new HandlerResult(30000);
            }
            CrashReporterUtils.logDebug(TAG, "timedout waiting for connection to come back");
            return new HandlerResult(FailureReason.NOINTERNET);
        }
    }

    public HandlerResult birth(ProvData provData, ProvStateBundle bundle) {
        if (provData.wifiSettingsOnly) {
            return new HandlerResult();
        }
        double[] location = LocationUtils.getLocation(this, false);
        BirthDeviceGeneration2 birthDevice = new BirthDeviceGeneration2(provData.deviceName, provData.deviceMac, provData.deviceZip, provData.userId, null, location[0], location[1]);
        boolean birthed = false;
        this.database.lock();
        HttpResponseErrorHandler errorHandler = new HttpResponseErrorHandler();
        DeviceResponse device = this.restClient.birthGeneration2(provData.deviceSerialNumber, birthDevice, errorHandler);
        if (!(errorHandler.hasError && errorHandler.responseCode == -1)) {
            birthed = !errorHandler.hasError && errorHandler.responseCode == Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            if (birthed) {
                User user = User.getLoggedInUser(this.database, this.prefsWrapper);
                device.user = user;
                this.database.save(device, true, true, false);
                bundle.deviceId = device.id;
                bundle.deviceZip = device.zip;
                bundle.deviceModel = device.model;
                PushPull.pullEntityAndSave(this.database, this.restClient, User.class, user.id);
            } else if (errorHandler.responseCode == 412) {
                return new HandlerResult(FailureReason.DEVICEISALREADYBIRTHED);
            }
        }
        this.database.unlock();
        bundle.birthTries++;
        if (birthed) {
            return new HandlerResult();
        }
        if (bundle.birthTries < 10) {
            return new HandlerResult(30000);
        }
        return new HandlerResult(FailureReason.BIRTH);
    }

    public HandlerResult confirm(ProvData provData, ProvStateBundle bundle) {
        String data = prepareSecureRequest(bundle.sessionKey, new ProvAckRequest());
        decryptSecureResponse(data, bundle.sessionKey);
        String url = Uri.parse("http://192.168.10.1/prov/net-info").buildUpon().appendQueryParameter("session_id", Integer.toString(bundle.sessionId)).toString();
        bindToWifiNetwork();
        HttpResponse httpResponse = HttpUtils.doHttpPost(url, "application/x-www-form-urlencoded", data);
        if (httpResponse != null && httpResponse.haveContent()) {
            if (((BaseResponse) this.gson.fromJson(decryptSecureResponse(httpResponse.content, bundle.sessionKey), BaseResponse.class)).error == 0) {
                return new HandlerResult();
            }
        }
        return new HandlerResult(FailureReason.CONFIRM);
    }

    public static void cleanUp(Context context) {
        PrefsWrapper pr = new PrefsWrapper(context);
        int networkId = pr.getStashedGen2NetworkId();
        if (networkId != -1) {
            Log.d(TAG, "removing stashed network");
            if (((WifiManager) context.getSystemService("wifi")).removeNetwork(networkId)) {
                Log.d(TAG, "removed stashed network");
            } else {
                Log.d(TAG, "failed to remove stashed network");
            }
            pr.clearGen2NetworkId();
        }
    }
}
