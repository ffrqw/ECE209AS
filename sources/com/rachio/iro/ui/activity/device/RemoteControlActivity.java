package com.rachio.iro.ui.activity.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.rachio.iro.AppVisibilityTracker;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.cloud.DeleteScheduleExecutionAsyncTask;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.apionly.RunMultipleZonesRequest;
import com.rachio.iro.model.apionly.ScheduleExecutionResponse;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.ScheduleExecution;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.view.remote.BaseDialView.ValueListener;
import com.rachio.iro.ui.view.remote.WateringDialView;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.StringUtils;
import com.rachio.iro.utils.TimeStringUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RemoteControlActivity extends BaseActivity implements FetchDeviceListener {
    Database database;
    private Device device;
    private String deviceId;
    private SparseIntArray durations = new SparseIntArray();
    private FetchDeviceCommand fetchDeviceCommand;
    public boolean isRunning;
    private Button mRemoteControlButton;
    RestClient restClient;
    private Button runNow;
    private Button stop;
    private TextView totalDuration;
    private WateringDialView wateringDialView;
    private TextView wateringStatus;
    private Spinner zoneSpinner;

    /* renamed from: com.rachio.iro.ui.activity.device.RemoteControlActivity$9 */
    class AnonymousClass9 extends ArrayAdapter<Zone> {
        AnonymousClass9(Context x0, int x1, List x2) {
            super(x0, -1, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = RemoteControlActivity.this.getLayoutInflater().inflate(R.layout.spinner_remotezone, parent, false);
            ((TextView) v.findViewById(R.id.text1)).setText(((Zone) getItem(position)).name);
            return v;
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View v = RemoteControlActivity.this.getLayoutInflater().inflate(R.layout.spinner_remotezone_dropdown, parent, false);
            TextView duration = (TextView) v.findViewById(R.id.duration);
            Zone z = (Zone) getItem(position);
            ((TextView) v.findViewById(R.id.zonename)).setText(z.name);
            if (z.zoneNumber == -1) {
                boolean allSameDuration = RemoteControlActivity.this.durations.size() == 0;
                if (!allSameDuration) {
                    allSameDuration = true;
                    int first = RemoteControlActivity.this.durations.valueAt(0);
                    for (int i = 0; i < getCount(); i++) {
                        int zoneNumber = ((Zone) getItem(i)).zoneNumber;
                        if (zoneNumber != -1 && RemoteControlActivity.this.durations.get(zoneNumber) != first) {
                            allSameDuration = false;
                            break;
                        }
                    }
                }
                if (allSameDuration) {
                    duration.setVisibility(0);
                    duration.setText(AnonymousClass9.getDurationString(RemoteControlActivity.this.durations.valueAt(0)));
                } else {
                    duration.setVisibility(4);
                }
            } else {
                duration.setVisibility(0);
                duration.setText(AnonymousClass9.getDurationString(RemoteControlActivity.this.durations.get(z.zoneNumber)));
            }
            return v;
        }

        private static String getDurationString(int duration) {
            duration /= 60;
            int minutes = duration % 60;
            int hours = (duration - minutes) / 60;
            if (hours == 0 && minutes == 0) {
                return "0";
            }
            StringBuilder sb = new StringBuilder();
            if (hours > 0) {
                sb.append(String.format("%dh", new Object[]{Integer.valueOf(hours)}));
            }
            if (minutes > 0) {
                sb.append(String.format("%dm", new Object[]{Integer.valueOf(minutes)}));
            }
            return sb.toString();
        }
    }

    static /* synthetic */ void access$600(RemoteControlActivity x0) {
        if (x0.device != null) {
            new DeleteScheduleExecutionAsyncTask(x0, x0.device, null).execute(null);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_remote_control);
        IroApplication.get(getApplicationContext()).component().inject(this);
        this.deviceId = getDeviceIdFromExtras();
        this.zoneSpinner = (Spinner) findViewById(R.id.remote_zonespinner);
        this.wateringStatus = (TextView) findViewById(R.id.remote_wateringstatus);
        this.wateringDialView = (WateringDialView) findViewById(R.id.remote_watering_dial);
        this.totalDuration = (TextView) findViewById(R.id.remote_totalduration);
        this.runNow = (Button) findViewById(R.id.remote_runnow);
        this.stop = (Button) findViewById(R.id.remote_stop);
        ToggleButton activity = (ToggleButton) findViewById(R.id.remote_control_nav_activity_icon);
        ToggleButton dashboard = (ToggleButton) findViewById(R.id.remote_control_nav_dashboard_icon);
        ToggleButton reports = (ToggleButton) findViewById(R.id.remote_control_nav_reports_icon);
        ToggleButton help = (ToggleButton) findViewById(R.id.remote_control_nav_help_icon);
        this.mRemoteControlButton = (Button) findViewById(R.id.remote_floating_action_button);
        activity.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RemoteControlActivity.access$000(RemoteControlActivity.this, "Activity");
            }
        });
        dashboard.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RemoteControlActivity.access$000(RemoteControlActivity.this, "Dashboard");
            }
        });
        reports.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RemoteControlActivity.access$000(RemoteControlActivity.this, "Reports");
            }
        });
        help.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RemoteControlActivity.access$000(RemoteControlActivity.this, "Help");
            }
        });
        this.mRemoteControlButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RemoteControlActivity.this.onRemoteControlButtonClicked(v);
            }
        });
        this.wateringDialView.setValueListener(new ValueListener() {
            public final void onValueChanged(int value) {
                Zone selectedZone = (Zone) RemoteControlActivity.this.zoneSpinner.getSelectedItem();
                if (selectedZone != null) {
                    if (selectedZone.zoneNumber == -1) {
                        Collection<Zone> enabledZones = RemoteControlActivity.this.device.getEnabledZones();
                        if (enabledZones != null) {
                            for (Zone z : enabledZones) {
                                RemoteControlActivity.this.durations.put(z.zoneNumber, value);
                            }
                        }
                    } else if (value == 0) {
                        RemoteControlActivity.this.durations.delete(selectedZone.zoneNumber);
                    } else {
                        RemoteControlActivity.this.durations.put(selectedZone.zoneNumber, value);
                    }
                    ((ArrayAdapter) RemoteControlActivity.this.zoneSpinner.getAdapter()).notifyDataSetChanged();
                    RemoteControlActivity.this.updateTotalDuration();
                }
            }
        });
        this.runNow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new RestClientProgressDialogAsyncTask<Void, Void, ScheduleExecutionResponse>(RemoteControlActivity.this, "Starting...") {
                    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                        RemoteControlActivity.this.database.lock();
                        List arrayList = new ArrayList();
                        List arrayList2 = new ArrayList();
                        Object obj = null;
                        List enabledZones = RemoteControlActivity.this.device.getEnabledZones();
                        if (enabledZones != null && enabledZones.size() > 0) {
                            for (Zone zone : RemoteControlActivity.this.device.getEnabledZones()) {
                                int i = RemoteControlActivity.this.durations.get(zone.zoneNumber);
                                if (i != 0) {
                                    arrayList.add(zone);
                                    arrayList2.add(Integer.valueOf(i));
                                }
                            }
                            HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                            obj = (ScheduleExecutionResponse) RemoteControlActivity.this.restClient.putObject(ScheduleExecutionResponse.class, new RunMultipleZonesRequest(RemoteControlActivity.this.deviceId, arrayList, arrayList2), httpResponseErrorHandler);
                            if (!httpResponseErrorHandler.hasError) {
                                if (obj != null) {
                                    RemoteControlActivity.this.device.scheduleExecution = obj;
                                    RemoteControlActivity.this.database.save(RemoteControlActivity.this.device, true, false, true);
                                } else {
                                    obj = new ScheduleExecutionResponse();
                                }
                            }
                        }
                        RemoteControlActivity.this.database.unlock();
                        return obj;
                    }

                    public final void onFailure(ErrorResponse error) {
                    }

                    public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                        RemoteControlActivity.this.updateRemoteState();
                    }
                }.execute(new Void[0]);
            }
        });
        this.stop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RemoteControlActivity.access$600(RemoteControlActivity.this);
            }
        });
    }

    public void onResume() {
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
    }

    private void updateRemoteState() {
        boolean running;
        int i;
        int i2 = 0;
        ScheduleExecution scheduleExecution = this.device.scheduleExecution;
        if (scheduleExecution == null || !scheduleExecution.isRunning()) {
            running = false;
        } else {
            running = true;
        }
        Spinner spinner = this.zoneSpinner;
        if (running) {
            i = 4;
        } else {
            i = 0;
        }
        spinner.setVisibility(i);
        TextView textView = this.wateringStatus;
        if (running) {
            i = 0;
        } else {
            i = 4;
        }
        textView.setVisibility(i);
        textView = this.totalDuration;
        if (running) {
            i = 4;
        } else {
            i = 0;
        }
        textView.setVisibility(i);
        if (running) {
            this.runNow.setVisibility(4);
        }
        Button button = this.stop;
        if (!running) {
            i2 = 4;
        }
        button.setVisibility(i2);
        if (running) {
            setIsRunning(true);
            switch (scheduleExecution.getType()) {
                case MANUAL:
                    this.wateringStatus.setText("Manual Run");
                    break;
                case FLEX:
                case AUTOMATIC:
                    ScheduleRule scheduleRuleById = this.device.getScheduleRuleById(scheduleExecution.scheduleRuleId);
                    if (scheduleRuleById != null) {
                        this.wateringStatus.setText(scheduleRuleById.getNameOrExternalName());
                        break;
                    }
                    break;
                default:
                    throw new IllegalStateException("unhandled type " + scheduleExecution.getType());
            }
            this.wateringDialView.onWateringStarted(scheduleExecution.startDate.getTime(), scheduleExecution.duration * 1000);
        } else {
            onWateringStopped();
        }
        updateTotalDuration();
    }

    private void updateTotalDuration() {
        int i = 0;
        int total = 0;
        for (int i2 = 0; i2 < this.durations.size(); i2++) {
            total += this.durations.valueAt(i2);
        }
        total = Math.max(total, 0);
        this.totalDuration.setText("Total Duration: " + TimeStringUtil.getStringForNumberOfHoursMinutesAndSeconds(total, true));
        Button button = this.runNow;
        if (this.isRunning || total <= 0) {
            i = 4;
        }
        button.setVisibility(i);
    }

    private void setIsRunning(boolean value) {
        this.isRunning = value;
        this.wateringDialView.setIsRunning(value);
    }

    private void onWateringStopped() {
        this.zoneSpinner.setSelection(0);
        for (Zone z : this.device.getEnabledZones()) {
            this.durations.put(z.zoneNumber, 0);
        }
        setIsRunning(false);
        this.wateringDialView.onWateringStopped();
    }

    public void onRemoteControlButtonClicked(View v) {
        close();
    }

    public void onBackPressed() {
        close();
    }

    private void close() {
        finish();
        overridePendingTransition(17432576, R.anim.slide_out_from_bottom);
    }

    public final void onDeviceDataChanged(String deviceId) {
        super.onDeviceDataChanged(deviceId);
        if (this.device != null && StringUtils.equals(deviceId, deviceId)) {
            this.device = (Device) this.database.refresh(this.device);
            updateRemoteState();
        }
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        if (device != null) {
            List enabledZones = this.device.getEnabledZones();
            Zone zone = new Zone();
            zone.zoneNumber = -1;
            zone.name = "All Zones";
            enabledZones.add(0, zone);
            this.zoneSpinner.setAdapter(new AnonymousClass9(this, -1, enabledZones));
            this.zoneSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Zone z = (Zone) parent.getItemAtPosition(position);
                    if (z.zoneNumber != -1) {
                        RemoteControlActivity.this.wateringDialView.setValue(RemoteControlActivity.this.durations.get(z.zoneNumber), true);
                    }
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            updateRemoteState();
        }
    }

    static /* synthetic */ void access$000(RemoteControlActivity x0, String x1) {
        Intent intent = new Intent();
        intent.putExtra("section", x1);
        x0.setResult(-1, intent);
        x0.close();
    }
}
