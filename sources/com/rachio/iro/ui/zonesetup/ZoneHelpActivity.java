package com.rachio.iro.ui.zonesetup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.rachio.iro.AppVisibilityTracker;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.cloud.DeleteScheduleExecutionAsyncTask;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.apionly.BaseResponse;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.apionly.RunMultipleZonesRequest;
import com.rachio.iro.model.apionly.ScheduleExecutionResponse;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.ui.activity.DashboardActivity;
import com.rachio.iro.ui.activity.zone.ActivityThatSavesZone;
import com.rachio.iro.ui.zonesetup.fragments.BaseZoneHelpFragment;
import com.rachio.iro.ui.zonesetup.fragments.ZoneHelpConfigureFragment;
import com.rachio.iro.ui.zonesetup.fragments.ZoneHelpLocateFragment;
import com.rachio.iro.ui.zonesetup.fragments.ZoneHelpStartFragment;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask.Callback;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask.SimpleCallback;
import com.rachio.iro.utils.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ZoneHelpActivity extends ActivityThatSavesZone implements FetchDeviceListener {
    private static final String TAG = ZoneHelpActivity.class.getSimpleName();
    private BaseZoneHelpFragment currentFragment;
    private DeleteScheduleExecutionAsyncTask deleteTask;
    private Device device;
    private String deviceId;
    private FetchDeviceCommand fetchDeviceCommand;
    private FragmentManager fm;
    private RestClientProgressDialogAsyncTask<Zone, Void, Zone> saveTask;
    private State state;
    private RestClientProgressDialogAsyncTask waterZoneTask;

    private static final class State implements Serializable {
        public int currentZone;
        public ArrayList<Zone> zones;

        private State() {
            this.currentZone = 0;
        }

        public final Zone getCurrentZone() {
            return (Zone) this.zones.get(this.currentZone);
        }
    }

    static /* synthetic */ void access$300(ZoneHelpActivity x0) {
        if (x0.isLastZone()) {
            DashboardActivity.goToDashboard(x0, x0.prefsWrapper, x0.device.user.id, true, x0.device.user.hasReadOnlyRole(), null, x0.deviceId, false, true);
            x0.finish();
            return;
        }
        State state = x0.state;
        state.currentZone++;
        x0.showZoneLocateFragment();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deviceId = getDeviceIdFromExtras();
        setContentView((int) R.layout.activity_fragment);
        this.fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            this.fm.beginTransaction().add((int) R.id.fragmentContainer, new ZoneHelpStartFragment()).commit();
            this.fm.executePendingTransactions();
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchDeviceCommand == null) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
            this.fetchDeviceCommand.execute();
        }
        AppVisibilityTracker.setInhibitToasts(true);
    }

    protected void onPause() {
        super.onPause();
        AppVisibilityTracker.setInhibitToasts(false);
        if (!(this.device == null || this.device.scheduleExecution == null || !this.device.scheduleExecution.isRunning())) {
            new DeleteScheduleExecutionAsyncTask(this, this.device, null).execute(null);
        }
        if (this.deleteTask != null) {
            this.deleteTask.cancel(true);
            this.deleteTask = null;
        }
        if (this.saveTask != null) {
            this.saveTask.cancel(true);
            this.saveTask = null;
        }
    }

    public final boolean hasChanges() {
        return false;
    }

    public final void save() {
    }

    public final void onDeviceDataChanged(String deviceId) {
        super.onDeviceDataChanged(deviceId);
        if (this.device != null && StringUtils.equals(deviceId, this.device.id)) {
            this.database.refresh(this.device);
        }
    }

    public final void onDeviceLoaded(final Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        ZoneHelpConfigureFragment.preload$48c7a957(device.user);
        stopWatering(new SimpleCallback() {
            public final void onEither() {
                super.onEither();
                if (ZoneHelpActivity.this.state == null) {
                    ZoneHelpActivity.this.state = new State();
                    Collection<Zone> zones = device.getZones();
                    ZoneHelpActivity.this.state.zones = new ArrayList();
                    for (Zone z : zones) {
                        Zone clone = (Zone) ModelObject.deepClone(Zone.class, z);
                        clone.device = new Device();
                        clone.device.id = z.device.id;
                        ZoneHelpActivity.this.state.zones.add(clone);
                    }
                }
                ZoneHelpActivity.this.updateFragmentState();
            }
        });
    }

    public final void startSetup() {
        showZoneLocateFragment();
    }

    private void stopWatering(Callback callback) {
        this.deleteTask = new DeleteScheduleExecutionAsyncTask(this, this.device, callback);
        this.deleteTask.execute(new Void[0]);
    }

    public final void disableZone() {
        stopWatering(new SimpleCallback() {
            public final void onSuccess() {
                super.onSuccess();
                Zone zone = ZoneHelpActivity.this.state.getCurrentZone();
                zone.enabled = false;
                ZoneHelpActivity.this.saveZone(zone, null, new SimpleCallback() {
                    public final void onSuccess() {
                        ZoneHelpActivity.access$300(ZoneHelpActivity.this);
                    }
                });
            }
        });
    }

    public final void configureZone() {
        stopWatering(new SimpleCallback() {
            public final void onSuccess() {
                super.onSuccess();
                ZoneHelpActivity.this.currentFragment = ZoneHelpConfigureFragment.newInstance();
                ZoneHelpActivity.this.fm.beginTransaction().replace(R.id.fragmentContainer, ZoneHelpActivity.this.currentFragment).commit();
                ZoneHelpActivity.this.fm.executePendingTransactions();
                ZoneHelpActivity.this.updateFragmentState();
            }
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.state != null) {
            this.currentFragment.commitState(this.state.getCurrentZone());
            outState.putSerializable("state", this.state);
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.state = (State) savedInstanceState.getSerializable("state");
    }

    public final void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        this.currentFragment = (BaseZoneHelpFragment) fragment;
    }

    public final void waterZone() {
        final Zone zone = this.state.getCurrentZone();
        if (this.waterZoneTask != null) {
            this.waterZoneTask.cancel(true);
        }
        this.waterZoneTask = new RestClientProgressDialogAsyncTask<Void, Void, BaseResponse>(this) {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                this.holder.database.lock();
                List arrayList = new ArrayList();
                arrayList.add(zone);
                List arrayList2 = new ArrayList();
                arrayList2.add(Integer.valueOf(180));
                ModelObject runMultipleZonesRequest = new RunMultipleZonesRequest(ZoneHelpActivity.this.device.id, arrayList, arrayList2);
                HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                ScheduleExecutionResponse scheduleExecutionResponse = (ScheduleExecutionResponse) this.holder.restClient.putObject(ScheduleExecutionResponse.class, runMultipleZonesRequest, httpResponseErrorHandler);
                if (scheduleExecutionResponse != null) {
                    ZoneHelpActivity.this.device.scheduleExecution = scheduleExecutionResponse;
                    this.holder.database.save(ZoneHelpActivity.this.device, true, false, true);
                }
                this.holder.database.unlock();
                return httpResponseErrorHandler.hasError ? null : new BaseResponse();
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
            }

            public final void onFailure(ErrorResponse errorResponse) {
            }
        };
        this.waterZoneTask.execute(null);
    }

    private void updateFragmentState() {
        if (this.device != null) {
            this.currentFragment.updateState(this.state.currentZone + 1, this.state.getCurrentZone());
        }
    }

    private void showZoneLocateFragment() {
        this.currentFragment = ZoneHelpLocateFragment.newInstance();
        this.fm.beginTransaction().replace(R.id.fragmentContainer, this.currentFragment).commit();
        this.fm.executePendingTransactions();
        updateFragmentState();
    }

    public final void skipZone() {
        Log.d(TAG, "skip");
        stopWatering(new SimpleCallback() {
            public final void onEither() {
                super.onEither();
                Log.d(ZoneHelpActivity.TAG, "here");
                ZoneHelpActivity.access$300(ZoneHelpActivity.this);
            }
        });
    }

    public final boolean isLastZone() {
        boolean z;
        State state = this.state;
        if (state.currentZone + 1 < state.zones.size()) {
            z = true;
        } else {
            z = false;
        }
        return !z;
    }

    public final boolean saveThisZone() {
        if (!this.currentFragment.validate()) {
            return false;
        }
        Zone zone = this.state.getCurrentZone();
        this.currentFragment.commitState(zone);
        this.saveTask = saveZone(zone, null, new SimpleCallback() {
            public final void onSuccess() {
                ZoneHelpActivity.access$300(ZoneHelpActivity.this);
            }
        });
        return true;
    }

    public final boolean isBusy() {
        return false;
    }

    public final boolean isValid() {
        return true;
    }

    public static final void start(Context context, String deviceId) {
        if (deviceId == null) {
            throw new IllegalArgumentException("device id cannot be null");
        }
        Intent intent = new Intent(context, ZoneHelpActivity.class);
        intent.putExtra("DEVICEID", deviceId);
        context.startActivity(intent);
    }
}
