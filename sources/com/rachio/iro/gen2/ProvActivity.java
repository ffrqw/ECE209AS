package com.rachio.iro.gen2;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.widget.Toast;
import com.rachio.iro.IroApplication;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchIroPropertiesCommand;
import com.rachio.iro.async.command.FetchIroPropertiesCommand.Listener;
import com.rachio.iro.async.command.FetchUnbornDataCommand;
import com.rachio.iro.cloud.PushPull;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.gen2.MrvlProv.DeviceSearchListener;
import com.rachio.iro.gen2.MrvlProv.NetworkSearchListener;
import com.rachio.iro.gen2.MrvlProv.ProvState;
import com.rachio.iro.gen2.MrvlProv.ProvisionCallback;
import com.rachio.iro.gen2.MrvlProv.WifiEnabledListener;
import com.rachio.iro.gen2.MrvlProvService.FailureReason;
import com.rachio.iro.gen2.MrvlProvService.State;
import com.rachio.iro.gen2.fragments.ConfiguringProgressFragment;
import com.rachio.iro.gen2.fragments.CopyDeviceProgressFragment;
import com.rachio.iro.gen2.fragments.CopyFailedFragment;
import com.rachio.iro.gen2.fragments.DeviceScannerFragment;
import com.rachio.iro.gen2.fragments.DeviceSettingsCaptureFragment;
import com.rachio.iro.gen2.fragments.EnableWifiFragment;
import com.rachio.iro.gen2.fragments.MasterValveSettingFragment;
import com.rachio.iro.gen2.fragments.NetworkPasswordCaptureFragment;
import com.rachio.iro.gen2.fragments.NetworkScannerFragment;
import com.rachio.iro.gen2.fragments.ProvisioningCompleteFragment;
import com.rachio.iro.gen2.fragments.ProvisioningFailureFragment;
import com.rachio.iro.gen2.fragments.ProvisioningProgressFragment;
import com.rachio.iro.gen2.fragments.SerialNumberCaptureFragment;
import com.rachio.iro.gen2.fragments.SetupCompleteFragment;
import com.rachio.iro.gen2.model.DiscoveredIro;
import com.rachio.iro.gen2.model.FoundNetwork;
import com.rachio.iro.gen2.model.ProvData;
import com.rachio.iro.model.IroProperties;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.apionly.CopyDeviceRequest;
import com.rachio.iro.model.apionly.DeviceResponse;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Gen2SerialPrefix;
import com.rachio.iro.model.device.ShallowDevice.Model;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.DashboardActivity;
import com.rachio.iro.ui.zonesetup.ZoneHelpActivity;
import com.rachio.iro.utils.CrashReporterUtils;
import com.rachio.iro.utils.PermissionRequester;
import com.rachio.iro.utils.StringUtils;
import java.util.List;

public class ProvActivity extends BaseActivity implements Listener, FetchUnbornDataCommand.Listener {
    private static final String TAG = ProvActivity.class.getName();
    private BaseProvisioningFragment currentFragment;
    private ProvData data;
    Database database;
    private boolean deviceSearchIsWaitingForPrefixes;
    private FetchIroPropertiesCommand fetchIroPropertiesCommand;
    private FetchUnbornDataCommand fetchUnbornDataCommand;
    private boolean isResuming;
    private String[] macPrefixes;
    private boolean networkSearchIsWaitingForPrefix;
    private boolean onPauseCalled;
    private boolean onSaveInstanceStateCalled;
    private PermissionRequester permissionRequester;
    PrefsWrapper prefsWrapper;
    boolean promptedToEnabledLocationServices = false;
    private MrvlProv prov = null;
    RestClient restClient;
    private User user = null;

    static /* synthetic */ void access$500(ProvActivity x0, String x1, String x2, Model x3) {
        x0.data.deviceId = x1;
        x0.data.deviceZip = x2;
        x0.currentFragment = ProvisioningCompleteFragment.newInstance(x1, x3);
        x0.updateFragment(true, true);
    }

    static /* synthetic */ void access$600(ProvActivity x0) {
        x0.currentFragment.onSetupComplete();
        x0.currentFragment = new SetupCompleteFragment();
        x0.updateFragment(true, true);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IroApplication.get(this).component().inject(this);
        setContentView((int) R.layout.activity_gen2prov);
        this.user = User.getLoggedInUser(this.database, this.prefsWrapper);
        if (this.user == null) {
            throw new IllegalStateException("logged in user is null");
        }
        if (savedInstanceState == null) {
            this.prov = new MrvlProv();
            this.data = new ProvData();
            Intent intent = getIntent();
            this.data.wifiSettingsOnly = intent.getBooleanExtra("wifisettingsonly", false);
            this.data.deviceName = intent.getStringExtra("device_name");
            this.data.deviceMac = intent.getStringExtra("device_mac");
            this.data.deviceSerialNumber = intent.getStringExtra("device_serialnumber");
            this.data.userId = this.user.id;
            if (this.data.wifiSettingsOnly) {
                if (this.data.deviceName == null) {
                    throw new IllegalStateException("You must pass the name of the device");
                } else if (this.data.deviceMac == null) {
                    throw new IllegalStateException("You must pass the mac address of the device");
                } else if (this.data.deviceSerialNumber == null) {
                    throw new IllegalStateException("You must pass the serial number of the device");
                }
            }
        }
        this.prov = (MrvlProv) savedInstanceState.getParcelable("mrvlprov");
        this.data = (ProvData) savedInstanceState.getSerializable("data");
        this.isResuming = true;
        this.permissionRequester = new PermissionRequester((BaseActivity) this, "android.permission.ACCESS_COARSE_LOCATION", new PermissionRequester.Listener() {
            public final void onPermissionsGranted() {
                if (ProvActivity.this.prov.getState() != ProvState.IDLE) {
                    return;
                }
                if (ProvActivity.this.currentFragment instanceof NetworkScannerFragment) {
                    ProvActivity.this.startScanningForNetworks();
                } else {
                    ProvActivity.this.startScanningForDevices();
                }
            }

            public final void onPermissionsDenied() {
                Toast.makeText(ProvActivity.this, "You must allow the application to use your location to add a new device.", 1).show();
                ProvActivity.this.finish();
            }
        });
        this.prov.setOnDeviceFoundListener(new DeviceSearchListener() {
            public final void onUnProvisionedDeviceFound(DiscoveredIro device) {
                ProvActivity.this.currentFragment.onDeviceFound(device);
            }

            public final void onSpecificDeviceFound(DiscoveredIro iro) {
                ProvActivity.this.currentFragment.onSpecificDeviceFound(iro);
            }

            public final void onScanningCanceled() {
                ProvActivity.this.currentFragment.onDeviceSearchCanceled();
            }
        });
        this.prov.setOnNetworkFoundListener(new NetworkSearchListener() {
            public final void onNetworkFound(FoundNetwork network) {
                ProvActivity.this.currentFragment.onNetworkFound(network);
            }

            public final void onScanningCanceled() {
                ProvActivity.this.currentFragment.onNetworkSearchCanceled();
            }
        });
        this.prov.setWifiEnabledListener(new WifiEnabledListener() {
            public final void onWifiEnabled() {
                ProvActivity.this.currentFragment = DeviceScannerFragment.newInstance(ProvActivity.this.data.wifiSettingsOnly);
                ProvActivity.this.updateFragment(true, false);
                ProvActivity.this.permissionRequester.requestPermissions();
            }
        });
        this.prov.setProvisioningCallback(new ProvisionCallback() {
            AlertDialog internetConnectionTimeout = null;

            private void hideConnectionTimeoutDialog() {
                if (this.internetConnectionTimeout != null) {
                    this.internetConnectionTimeout.dismiss();
                    this.internetConnectionTimeout = null;
                }
            }

            public final void onProvisioningFailed(FailureReason reason) {
                hideConnectionTimeoutDialog();
                ProvActivity.this.currentFragment = ProvisioningFailureFragment.newInstance(reason.description);
                ProvActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ProvActivity.this.currentFragment).commit();
            }

            public final void onProvisioningStateChanged(State newState) {
                if (newState == State.BIRTHING) {
                    hideConnectionTimeoutDialog();
                }
                ProvActivity.this.currentFragment.onProvisionStateChanged(newState);
            }

            public final void onProvisioningCompleted(String deviceId, String deviceZip, Model deviceModel) {
                if (ProvActivity.this.data.wifiSettingsOnly) {
                    ProvActivity.this.finish();
                } else {
                    ProvActivity.access$500(ProvActivity.this, deviceId, deviceZip, deviceModel);
                }
            }

            public final void onTimeoutWaitingForInternetConnection() {
                this.internetConnectionTimeout = new Builder(ProvActivity.this).setTitle("No internet connection").setMessage((CharSequence) "No internet connection could be found. Please check your device's mobile data and wifi settings.Device activation will continue automatically once a connection is found").setCancelable(false).show();
            }
        });
    }

    private synchronized void checkLocationServicesEnabled() {
        if (!this.promptedToEnabledLocationServices) {
            if (VERSION.SDK_INT >= 23) {
                LocationManager locationManager = (LocationManager) getSystemService("location");
                boolean haveEnabledProvider = false;
                List<String> providers = locationManager.getAllProviders();
                if (providers != null) {
                    for (String provider : providers) {
                        if (!StringUtils.equals(provider, "passive") && locationManager.isProviderEnabled(provider)) {
                            CrashReporterUtils.logDebug(TAG, "location provider " + provider + " enabled");
                            haveEnabledProvider = true;
                            break;
                        }
                    }
                }
                if (!haveEnabledProvider) {
                    this.promptedToEnabledLocationServices = true;
                    Toast.makeText(this, "Please enable location services.", 1).show();
                    startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                }
            }
        }
    }

    protected final void startScanningForDevices() {
        checkLocationServicesEnabled();
        if (this.macPrefixes != null) {
            this.deviceSearchIsWaitingForPrefixes = false;
            if (this.data.wifiSettingsOnly) {
                this.prov.startSearchingForSpecificDevice(this, this.macPrefixes, this.data.deviceMac);
                return;
            } else {
                this.prov.startSearchingForUnProvisionedDevices(this.macPrefixes, this);
                return;
            }
        }
        this.deviceSearchIsWaitingForPrefixes = true;
    }

    protected final void startScanningForNetworks() {
        checkLocationServicesEnabled();
        if (this.data.wifiSettingsOnly || this.data.deviceSerialPrefix != null) {
            this.prov.startSearchingForUsableNetworks(this);
        } else {
            this.networkSearchIsWaitingForPrefix = true;
        }
    }

    protected final void onDeviceSelected(DiscoveredIro iro) {
        if (this.data.deviceSerialPrefix == null || !(StringUtils.equals(iro.mac, this.data.deviceMac) || this.data.wifiSettingsOnly)) {
            this.data.deviceSerialPrefix = null;
            this.fetchUnbornDataCommand = new FetchUnbornDataCommand(this, iro.mac);
            this.fetchUnbornDataCommand.execute();
        }
        this.data.deviceName = iro.ssid;
        this.data.deviceMac = iro.mac;
        this.prov.stop();
        this.currentFragment = NetworkScannerFragment.newInstance();
        updateFragment(false, false);
        startScanningForNetworks();
    }

    protected final void onNetworkSelected(FoundNetwork network) {
        this.data.networkSsid = network.name;
        this.prov.stop();
        this.currentFragment = NetworkPasswordCaptureFragment.newInstance(this.data.networkPassword);
        updateFragment(false, false);
    }

    protected final void onNetworkPasswordCaptured(String networkPassword) {
        this.data.networkPassword = networkPassword;
        if (this.data.wifiSettingsOnly) {
            startProvisioning();
            return;
        }
        this.currentFragment = SerialNumberCaptureFragment.newInstance(this.data.deviceSerialPrefix, this.data.deviceSerialNumber);
        updateFragment(false, false);
    }

    protected final void onSerialNumberCaptured(String serialNumber) {
        this.data.deviceSerialNumber = serialNumber;
        startProvisioning();
    }

    protected final void setupDevice() {
        this.currentFragment = DeviceSettingsCaptureFragment.newInstance(this.data.deviceName, this.data.deviceZip);
        updateFragment(false, false);
    }

    protected final void onDeviceSettingsCaptured(String name, String zip) {
        this.data.deviceName = name;
        this.data.deviceZip = zip;
        this.currentFragment = new MasterValveSettingFragment();
        updateFragment(false, false);
    }

    protected final void onMasterValveCaptured(boolean haveMasterValve) {
        this.data.deviceMasterValve = haveMasterValve;
        this.currentFragment = new ConfiguringProgressFragment();
        updateFragment(true, false);
        new AsyncTask<Void, Void, Void>() {
            protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                super.onPostExecute((Void) obj);
                ProvActivity.access$600(ProvActivity.this);
            }

            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                ModelObject modelObject;
                ProvActivity.this.database.lock();
                ModelObject modelObject2 = (Device) ProvActivity.this.database.find(Device.class, ProvActivity.this.data.deviceId);
                if (modelObject2 == null) {
                    modelObject = (Device) PushPull.pullEntityAndSave(ProvActivity.this.database, ProvActivity.this.restClient, Device.class, ProvActivity.this.data.deviceId, ProvActivity.this.data.userId);
                } else {
                    modelObject = modelObject2;
                }
                modelObject.name = ProvActivity.this.data.deviceName;
                modelObject.zip = ProvActivity.this.data.deviceZip;
                modelObject.masterValve = ProvActivity.this.data.deviceMasterValve;
                DeviceResponse deviceResponse = (DeviceResponse) ProvActivity.this.restClient.putObject(DeviceResponse.class, modelObject, new HttpResponseErrorHandler());
                if (!(deviceResponse == null || deviceResponse.hasError())) {
                    deviceResponse.user = modelObject.user;
                    deviceResponse.managerUser = modelObject.managerUser;
                    ProvActivity.this.database.save(modelObject, true);
                }
                ProvActivity.this.database.unlock();
                return null;
            }
        }.execute(null);
    }

    private void updateFragment(boolean clear, boolean delayed) {
        if (this.onPauseCalled || this.onSaveInstanceStateCalled) {
            CrashReporterUtils.silentException(new Exception("tried to change fragment while paused"));
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (clear) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            for (int backStackEntryCount = supportFragmentManager.getBackStackEntryCount(); backStackEntryCount >= 0; backStackEntryCount--) {
                supportFragmentManager.popBackStack();
            }
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!clear) {
            transaction.addToBackStack(null);
        }
        if (delayed) {
            transaction.setCustomAnimations(0, R.anim.gen2provhold);
        }
        transaction.replace(R.id.fragmentContainer, this.currentFragment);
        transaction.commit();
    }

    protected final void copyDevice(final String sourceDeviceId) {
        this.currentFragment = new CopyDeviceProgressFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, this.currentFragment).commit();
        new AsyncTask<Void, Void, Boolean>() {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                boolean z;
                HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                ProvActivity.this.database.lock();
                Device device = (Device) ProvActivity.this.restClient.putObject(Device.class, new CopyDeviceRequest(sourceDeviceId, ProvActivity.this.data.deviceId), httpResponseErrorHandler);
                if (httpResponseErrorHandler.hasError || device == null) {
                    z = false;
                } else {
                    device.user = User.getLoggedInUser(ProvActivity.this.database, ProvActivity.this.prefsWrapper);
                    ProvActivity.this.database.save(device, true, true, false);
                    z = true;
                }
                ProvActivity.this.database.unlock();
                return Boolean.valueOf(z);
            }

            protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                Boolean bool = (Boolean) obj;
                super.onPostExecute(bool);
                if (bool.booleanValue()) {
                    ProvActivity.access$600(ProvActivity.this);
                } else {
                    ProvActivity.this.onCopyDeviceFailed();
                }
            }
        }.execute(null);
    }

    protected final void onCopyDeviceFailed() {
        this.currentFragment = CopyFailedFragment.newInstance(this.data.deviceId);
        updateFragment(false, false);
    }

    private void startProvisioning() {
        this.prov.stop();
        this.currentFragment = new ProvisioningProgressFragment();
        updateFragment(true, false);
        this.currentFragment.onProvisionStateChanged(State.IDLE);
        this.prov.startProvision(this, this.data);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.onSaveInstanceStateCalled = true;
        outState.putParcelable("mrvlprov", this.prov);
        outState.putSerializable("data", this.data);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.onSaveInstanceStateCalled = false;
    }

    protected void onPause() {
        super.onPause();
        this.onPauseCalled = true;
        this.prov.onPause(this);
    }

    protected void onResume() {
        super.onResume();
        this.onPauseCalled = false;
        this.onSaveInstanceStateCalled = false;
        this.prov.onResume(this);
        if (!this.isResuming) {
            this.isResuming = true;
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (this.prov.isWifiEnabled()) {
                this.currentFragment = DeviceScannerFragment.newInstance(this.data.wifiSettingsOnly);
                this.permissionRequester.requestPermissions();
            } else {
                this.currentFragment = EnableWifiFragment.newInstance();
                this.prov.waitForWifiToBeEnabled(this);
            }
            fragmentManager.beginTransaction().add((int) R.id.fragmentContainer, this.currentFragment).commit();
            fragmentManager.executePendingTransactions();
        }
        if (this.fetchIroPropertiesCommand == null) {
            this.fetchIroPropertiesCommand = new FetchIroPropertiesCommand(this);
            this.fetchIroPropertiesCommand.execute();
        }
    }

    public final void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        this.currentFragment = (BaseProvisioningFragment) fragment;
    }

    protected final void restart() {
        this.currentFragment = new DeviceScannerFragment();
        updateFragment(true, false);
        this.prov.startSearchingForUnProvisionedDevices(this.macPrefixes, this);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.permissionRequester.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public final void exit(OnClickListener exitListener) {
        new Builder(this).setTitle("Device Not Activated").setMessage((CharSequence) "Quitting activation will prevent your device from being connected. Are you sure you want to exit activation now?").setPositiveButton((CharSequence) "Continue Activation", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setNegativeButton((CharSequence) "Exit Activation", exitListener).show();
    }

    public void onBackPressed() {
        ProvState state = this.prov.getState();
        if (state != ProvState.IDLE && state != ProvState.SCANNINGFORDEVICES && state != ProvState.SCANNINGFORNETWORKS && state != ProvState.WAITINGFORWIFI) {
            Toast.makeText(this, "Please wait...", 0).show();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            exit(new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    super.onBackPressed();
                }
            });
        } else {
            this.prov.stop();
            super.onBackPressed();
        }
    }

    protected final User getUser() {
        return this.user;
    }

    protected final void setupZones() {
        if (this.data.deviceId == null) {
            throw new IllegalStateException("tried to setup zones before deviceId was known");
        }
        ZoneHelpActivity.start(this, this.data.deviceId);
    }

    public final void onPropertiesLoaded(IroProperties properties) {
        if (properties != null && properties.macPrefix != null) {
            this.macPrefixes = properties.macPrefix;
            if (this.deviceSearchIsWaitingForPrefixes) {
                startScanningForDevices();
            }
        }
    }

    public final void onUnbornDataLoaded(Gen2SerialPrefix data) {
        this.fetchUnbornDataCommand = null;
        if (data != null) {
            this.data.deviceSerialPrefix = data.serialPrefix;
        } else {
            CrashReporterUtils.logDebug(TAG, "failed to load device serial prefix!!");
        }
        if (this.networkSearchIsWaitingForPrefix) {
            this.networkSearchIsWaitingForPrefix = false;
            this.prov.startSearchingForUsableNetworks(this);
        }
    }

    public final void goToDashboardOrNoDevices() {
        DashboardActivity.goToDashboard(this, this.prefsWrapper, this.user.id, this.user.haveDevices(), this.user.hasReadOnlyRole(), null, this.data.deviceId, true, false);
    }
}
