package com.rachio.iro.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder;
import com.google.android.gms.analytics.Tracker;
import com.instabug.library.Instabug;
import com.rachio.iro.AppVisibilityTracker;
import com.rachio.iro.IroApplication;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.R;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.mapping.JsonMapper;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getCanonicalName();
    private static final IntentFilter dataChangedIntentFilter;
    protected Database database;
    private BroadcastReceiver genericDataChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Device.BROADCAST_DATABASE_DEVICE_DATA_CHANGED)) {
                BaseActivity.this.onDeviceDataChanged(intent.getStringExtra(Database.EXTRA_ENTITYID));
            } else if (action.equals(Zone.BROADCAST_DATABASE_ZONE_DATA_CHANGED)) {
                BaseActivity.this.onZoneDataChanged();
            } else {
                throw new IllegalStateException("Shouldn't be getting action " + action);
            }
        }
    };
    protected LocalBroadcastManager localBroadcastManager;
    protected PrefsWrapper prefsWrapper;
    private ProgressDialog progressDialog;
    protected Tracker tracker;

    static {
        IntentFilter intentFilter = new IntentFilter();
        dataChangedIntentFilter = intentFilter;
        intentFilter.addAction(Device.BROADCAST_DATABASE_DEVICE_DATA_CHANGED);
        dataChangedIntentFilter.addAction(Zone.BROADCAST_DATABASE_ZONE_DATA_CHANGED);
    }

    public final void wireupToolbarActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setHomeAsUpIndicator((int) R.drawable.back_arrow);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IroApplication.get(this).component().inject(this);
        if (getRequestedOrientation() == -1) {
            setRequestedOrientation(1);
        }
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage(getString(R.string.please_wait));
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public final void toastError(ErrorResponse error) {
        Toast.makeText(this, error.getError(), 0).show();
    }

    public final void toastGenericError() {
        Toast.makeText(this, "Request failed, please check your connection", 0).show();
    }

    protected void onResume() {
        super.onResume();
        Instabug.notifyActivityResumed(this);
        AppVisibilityTracker.onActivityResume();
        this.localBroadcastManager.registerReceiver(this.genericDataChangedReceiver, dataChangedIntentFilter);
        this.tracker.setScreenName(getClass().getSimpleName());
        this.tracker.send(new ScreenViewBuilder().build());
    }

    protected void onPause() {
        super.onPause();
        Instabug.notifyActivityPaused(this);
        AppVisibilityTracker.onActivityPause();
        this.localBroadcastManager.unregisterReceiver(this.genericDataChangedReceiver);
    }

    public void onDeviceDataChanged(String deviceId) {
        Log.d(TAG, "device data changed for " + deviceId);
    }

    protected void onZoneDataChanged() {
        Log.d(TAG, "zone data changed");
    }

    private String getMandatoryStringFromExtras(String extra, String description) {
        Intent intent = getIntent();
        String userId = intent.getStringExtra(extra);
        if (userId != null) {
            return userId;
        }
        throw new IllegalStateException("you must pass the " + description + " extras: " + JsonMapper.toJsonPretty(intent.getExtras()));
    }

    protected final String getUserIdFromExtras() {
        return getMandatoryStringFromExtras("USERID", "user id");
    }

    protected final String getDeviceIdFromExtras() {
        return getMandatoryStringFromExtras("DEVICEID", "device id");
    }

    public final String getZoneIdFromExtras() {
        return getMandatoryStringFromExtras("ZONEID", "zone id");
    }

    public boolean hasChanges() {
        return false;
    }

    public boolean isBusy() {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        ActivityThatSaves activityThatSaves;
        if (this instanceof ActivityThatSaves) {
            activityThatSaves = (ActivityThatSaves) this;
        } else {
            activityThatSaves = null;
        }
        if (activityThatSaves != null) {
            if (!activityThatSaves.isBusy()) {
                if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
                    if (activityThatSaves.isValid() && activityThatSaves.hasChanges()) {
                        activityThatSaves.save();
                        return;
                    } else {
                        super.onBackPressed();
                        return;
                    }
                }
            }
            return;
        }
        super.onBackPressed();
    }
}
