package com.rachio.iro.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import com.electricimp.blinkup.BlinkupController;
import com.electricimp.blinkup.BlinkupController.ServerErrorHandler;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.model.BirthDevice;
import com.rachio.iro.model.PersonIdAndExternalPlanId;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.fragment.blinkup.AddDeviceFragment;
import com.rachio.iro.ui.fragment.blinkup.BlinkUpFragment;
import com.rachio.iro.ui.fragment.blinkup.WifiSetupStartFragment;
import com.rachio.iro.ui.fragment.blinkup.WifiSetupStepFourFragment;
import com.rachio.iro.ui.fragment.blinkup.WifiSetupStepOneFragment;
import com.rachio.iro.ui.fragment.blinkup.WifiSetupStepThreeFragment;
import com.rachio.iro.ui.fragment.blinkup.WifiSetupStepTwoFragment;
import com.rachio.iro.utils.LocationUtils;
import com.rachio.iro.utils.ProgressDialogAsyncTask.CustomProgressDialog;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.WifiUtils;
import java.util.TimeZone;

public class BlinkUpActivity extends BaseActivity {
    private static final String TAG = BlinkUpActivity.class.getCanonicalName();
    private BirthDevice blinkUpDevice;
    private String blinkUpPassword;
    private BlinkupController blinkup;
    private ServerErrorHandler errorHandler = new ServerErrorHandler() {
    };
    private BlinkUpView firstStep = BlinkUpView.AddDevice;
    public BlinkUpFragment statusFragment;
    private boolean updateBlinkup = false;

    public enum BlinkUpView {
        AddDevice,
        WifiStart,
        WifiStep1,
        WifiStep2,
        WifiStep3,
        PreBlinkUp,
        BlinkUp
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_blink_up);
        String action = getIntent().getStringExtra("BLINKUP_ACTION");
        boolean z = !TextUtils.isEmpty(action) && action.equals("UPDATE");
        this.updateBlinkup = z;
        this.blinkup = BlinkupController.getInstance();
        this.blinkup.intentBlinkupComplete = new Intent(this, BlinkupCompleteActivity.class);
        if (savedInstanceState == null && !(this instanceof BlinkupCompleteActivity)) {
            BlinkUpView step = (BlinkUpView) getIntent().getSerializableExtra("BLINKUP_STEP");
            if (step != null) {
                moveToStep(step, true);
            } else {
                moveToStep(this.firstStep, true);
            }
        }
    }

    private void replaceFragment(Fragment frag, String tag, boolean first) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.blinkup_activity_fragment, frag);
        ft.setTransition(4099);
        if (!first) {
            ft.addToBackStack(tag);
        }
        ft.commit();
    }

    public final void moveToStep(BlinkUpView step) {
        moveToStep(step, false);
    }

    private void moveToStep(BlinkUpView step, boolean first) {
        switch (step) {
            case AddDevice:
                replaceFragment(new AddDeviceFragment(), "AddDevice", first);
                return;
            case WifiStart:
                replaceFragment(new WifiSetupStartFragment(), "WifiSetupStart", first);
                return;
            case WifiStep1:
                replaceFragment(new WifiSetupStepOneFragment(), "WifiSetupStepOne", first);
                return;
            case WifiStep2:
                replaceFragment(new WifiSetupStepTwoFragment(), "WifiSetupStepTwo", first);
                return;
            case WifiStep3:
                replaceFragment(new WifiSetupStepThreeFragment(), "WifiSetupStepThree", first);
                return;
            case PreBlinkUp:
                replaceFragment(new WifiSetupStepFourFragment(), "PreBlinkUp", first);
                return;
            default:
                throw new IllegalArgumentException();
        }
    }

    public final void setStatusMessage(String message, boolean showSpinner) {
        if (this.statusFragment != null) {
            this.statusFragment.setStatusMessage(message, showSpinner);
        }
    }

    public final void setBlinkUpSuccess(String deviceId) {
        if (this.statusFragment != null) {
            this.statusFragment.onSuccess(deviceId);
        }
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "requestPermissions!");
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Object string = defaultSharedPreferences.getString("agentUrl", null);
        Object string2 = defaultSharedPreferences.getString("impeeId", null);
        Object string3 = defaultSharedPreferences.getString("planId", null);
        if (((TextUtils.isEmpty(string) | TextUtils.isEmpty(string2)) | TextUtils.isEmpty(string3)) == 0) {
            Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.clear();
            edit.commit();
            if (this.updateBlinkup) {
                setBlinkUpSuccess(null);
                return;
            }
            final User loggedInUser = User.getLoggedInUser(((IroApplication) getApplication()).getDatabase(), ((IroApplication) getApplication()).getPrefsWrapper());
            BirthDevice birthDevice = this.blinkUpDevice;
            birthDevice.setPin(string2);
            birthDevice.setExternalUrl(string);
            birthDevice.setStatus("ONLINE");
            double[] location = LocationUtils.getLocation(this, false);
            birthDevice.setLatitude(location[0]);
            birthDevice.setLongitude(location[1]);
            birthDevice.setTimeZone(TimeZone.getDefault().getID());
            birthDevice.person = new PersonIdAndExternalPlanId(loggedInUser.id, string3);
            Log.d(TAG, "Device: " + birthDevice);
            setStatusMessage("Saving Iro Information...", true);
            new RestClientProgressDialogAsyncTask<BirthDevice, Void, Device>(this) {
                protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                    BirthDevice[] birthDeviceArr = (BirthDevice[]) objArr;
                    BlinkUpActivity.this.database.lock();
                    Device device = (Device) this.holder.restClient.postObject(Device.class, birthDeviceArr[0], this.errorHandler);
                    if (!(this.errorHandler.hasError || device == null)) {
                        device.user = loggedInUser;
                        this.holder.database.save(device);
                    }
                    BlinkUpActivity.this.database.unlock();
                    return device;
                }

                public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                    Device device = (Device) errorResponse;
                    BlinkUpActivity.this.prefsWrapper.setSelectedDeviceId(device.id);
                    BlinkUpActivity.this.setBlinkUpSuccess(device.id);
                }

                public final void onFailure(ErrorResponse error) {
                    String status = "";
                    if (error != null) {
                        if (error.getCode() == 412) {
                            status = "Device already connected to existing account, please contact support@rachio.com";
                        } else {
                            String errorMessage = error.getError();
                            status = errorMessage != null ? "Error: " + errorMessage.toString() + " " : "";
                        }
                    }
                    BlinkUpActivity.this.setStatusMessage("Sorry, but we were unable to setup your device. " + status, false);
                    BlinkUpActivity blinkUpActivity = BlinkUpActivity.this;
                    if (blinkUpActivity.statusFragment != null) {
                        blinkUpActivity.statusFragment.onFailure();
                    }
                }

                public final CustomProgressDialog getCustomProgressDialog() {
                    return new CustomProgressDialog() {
                        public void setCancelable(boolean cancelable) {
                        }

                        public void setMessage(CharSequence message) {
                        }

                        public void show() {
                        }

                        public void dismiss() {
                        }
                    };
                }
            }.execute(new BirthDevice[]{this.blinkUpDevice});
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BlinkupController.handleActivityResult$51b9da64(this, resultCode);
    }

    public final void blinkup() {
        User user = User.getLoggedInUser(((IroApplication) getApplication()).getDatabase(), ((IroApplication) getApplication()).getPrefsWrapper());
        if (user != null) {
            String externalPlanID;
            if (!this.updateBlinkup) {
                this.prefsWrapper.setSelectedDeviceId(null);
            }
            if (TextUtils.isEmpty(user.externalPlanId)) {
                externalPlanID = PreferenceManager.getDefaultSharedPreferences(this).getString("planId", null);
            } else {
                externalPlanID = user.externalPlanId;
            }
            this.blinkup.setPlanID(externalPlanID);
            this.statusFragment = BlinkUpFragment.newInstance(user.id);
            replaceFragment(this.statusFragment, "BlinkUp", false);
            this.blinkup.countdownSeconds = 5;
            this.blinkup.setupDevice$cc378d8(this, WifiUtils.getNetworkName(this), this.blinkUpPassword, "4b2ff662a929d0abe47ec27c4e6da094");
        }
    }

    public final void setBlinkUpDevice(BirthDevice blinkUpDevice) {
        this.blinkUpDevice = blinkUpDevice;
    }

    public final boolean isUpdateBlinkUp() {
        return this.updateBlinkup;
    }

    public final void setBlinkUpPassword(String password) {
        this.blinkUpPassword = password;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("blinkupdevice", this.blinkUpDevice);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.blinkUpDevice = (BirthDevice) savedInstanceState.get("blinkupdevice");
    }
}
