package com.rachio.iro.ui.activity.device;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.async.command.FetchIroPropertiesCommand;
import com.rachio.iro.async.command.FetchIroPropertiesCommand.Listener;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.IroProperties;
import com.rachio.iro.model.IroProperties.Sensors.FlowSensor;
import com.rachio.iro.model.apionly.AttachedSensorResponse;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Device.AttachedSensor;
import com.rachio.iro.model.device.Device.AttachedSensor.Type;
import com.rachio.iro.ui.activity.ActivityThatSaves.DisableSave;
import com.rachio.iro.ui.view.settings.DropDownRow;
import com.rachio.iro.ui.view.settings.FlowSensorListItemView;
import com.rachio.iro.ui.view.settings.SwitchRowWithDescriptionView;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.StringUtils;
import java.util.Date;

public class SensorConfigurationActivity extends ActivityThatSavesDevice implements FetchDeviceListener, Listener, DisableSave {
    private Device device;
    private String deviceId;
    private SwitchRowWithDescriptionView enabled;
    private FetchDeviceCommand fetchDeviceCommand;
    private FetchIroPropertiesCommand fetchIroPropertiesCommand;
    private ListView flowSensorTypes;
    private FrameLayout flowSensorTypesContainer;
    private TextView rainSensorText;
    private AttachedSensor sensor;
    private String sensorId;
    private int sensorIndex;
    private DropDownRow type;

    /* renamed from: com.rachio.iro.ui.activity.device.SensorConfigurationActivity$4 */
    class AnonymousClass4 extends ArrayAdapter<FlowSensor> {
        AnonymousClass4(Context x0, int x1, FlowSensor[] x2) {
            super(x0, 17367043, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new FlowSensorListItemView(getContext());
            }
            ((FlowSensorListItemView) convertView).setText(((FlowSensor) getItem(position)).toString());
            return convertView;
        }
    }

    private void onEnabledChanged(boolean enabled) {
        this.type.setEnabled(enabled);
        this.flowSensorTypes.setEnabled(enabled);
        this.rainSensorText.setEnabled(enabled);
        supportInvalidateOptionsMenu();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_device_settings_sensor);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.deviceId = getDeviceIdFromExtras();
        this.sensorId = getIntent().getStringExtra("sensorid");
        if (this.sensorId == null) {
            throw new IllegalStateException("you must pass the sensor id");
        }
        this.enabled = (SwitchRowWithDescriptionView) findViewById(R.id.sensorconfig_enabled);
        this.type = (DropDownRow) findViewById(R.id.sensorconfig_type);
        this.rainSensorText = (TextView) findViewById(R.id.sensorconfig_rainsensortext);
        this.flowSensorTypesContainer = (FrameLayout) findViewById(R.id.sensorconfig_flowsensortype_container);
        this.flowSensorTypes = (ListView) findViewById(R.id.sensorconfig_flowsensortype);
        ProgressBar flowSensorTypesSpinner = (ProgressBar) findViewById(R.id.sensorconfig_flowsensortype_spinner);
        this.enabled.setOnCheckedChangedListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SensorConfigurationActivity.this.onEnabledChanged(isChecked);
            }
        });
        this.type.setEnabled(false);
        this.type.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 1) {
                    SensorConfigurationActivity.this.rainSensorText.setVisibility(8);
                    SensorConfigurationActivity.this.flowSensorTypesContainer.setVisibility(0);
                } else {
                    SensorConfigurationActivity.this.flowSensorTypesContainer.setVisibility(8);
                    SensorConfigurationActivity.this.rainSensorText.setVisibility(0);
                }
                SensorConfigurationActivity.this.supportInvalidateOptionsMenu();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.flowSensorTypes.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SensorConfigurationActivity.this.supportInvalidateOptionsMenu();
            }
        });
        this.flowSensorTypes.setEmptyView(flowSensorTypesSpinner);
    }

    private Type getNewType() {
        return this.type.getSelectedItemPosition() == 1 ? Type.FLOW : Type.RAIN;
    }

    private FlowSensor getSelectedFlowSensor() {
        int position = this.flowSensorTypes.getCheckedItemPosition();
        if (position != -1) {
            return (FlowSensor) this.flowSensorTypes.getAdapter().getItem(position);
        }
        return null;
    }

    public final boolean hasChanges() {
        if (this.sensor == null) {
            return false;
        }
        boolean enableChanged = this.enabled.isChecked() != this.sensor.enabled;
        boolean typeChanged = false;
        boolean flowSensorChanged = false;
        if (this.enabled.isChecked()) {
            if (getNewType() != this.sensor.type) {
                typeChanged = true;
            } else {
                typeChanged = false;
            }
            if (!typeChanged && this.sensor.type == Type.FLOW) {
                FlowSensor selectedFlowSensor = getSelectedFlowSensor();
                flowSensorChanged = (StringUtils.equals(selectedFlowSensor.make, this.sensor.make) && StringUtils.equals(selectedFlowSensor.model, this.sensor.model) && selectedFlowSensor.kfactor == this.sensor.kfactor && selectedFlowSensor.sensorOffset == selectedFlowSensor.sensorOffset) ? false : true;
            }
        }
        if (enableChanged || typeChanged || flowSensorChanged) {
            return true;
        }
        return false;
    }

    public final void save() {
        final AttachedSensor newSensor = new AttachedSensor();
        newSensor.id = this.sensorId;
        newSensor.enabled = this.enabled.isChecked();
        newSensor.type = Type.RAIN;
        if (newSensor.enabled) {
            newSensor.type = getNewType();
            if (newSensor.type == Type.FLOW) {
                FlowSensor selectedFlowSensor = getSelectedFlowSensor();
                newSensor.make = selectedFlowSensor.make;
                newSensor.model = selectedFlowSensor.model;
                newSensor.kfactor = selectedFlowSensor.kfactor;
                newSensor.sensorOffset = selectedFlowSensor.sensorOffset;
            }
        }
        new RestClientProgressDialogAsyncTask<Void, Void, AttachedSensorResponse>(this) {
            public final void onFailure(ErrorResponse errorResponse) {
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                SensorConfigurationActivity.this.finish();
            }

            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                AttachedSensorResponse attachedSensorResponse = (AttachedSensorResponse) this.holder.restClient.putObject(AttachedSensorResponse.class, newSensor, new HttpResponseErrorHandler());
                if (attachedSensorResponse != null) {
                    SensorConfigurationActivity.this.device.lastUpdateDate = new Date();
                    SensorConfigurationActivity.this.device.attachedSensors[SensorConfigurationActivity.this.sensorIndex] = attachedSensorResponse;
                    SensorConfigurationActivity.this.database.save(SensorConfigurationActivity.this.device, true);
                }
                return attachedSensorResponse;
            }
        }.execute(null);
    }

    public final boolean isValid() {
        if (this.enabled.isChecked() && getNewType() == Type.FLOW && getSelectedFlowSensor() == null) {
            return false;
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchDeviceCommand == null) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
            this.fetchDeviceCommand.execute();
        }
        if (this.fetchIroPropertiesCommand == null) {
            this.fetchIroPropertiesCommand = new FetchIroPropertiesCommand(this);
            this.fetchIroPropertiesCommand.execute();
        }
    }

    public final void onPropertiesLoaded(IroProperties properties) {
        if (properties != null && properties.sensor != null && properties.sensor.flowSensor != null) {
            this.flowSensorTypes.setAdapter(new AnonymousClass4(this, 17367043, properties.sensor.flowSensor));
            setSelectedFlowSensor();
        }
    }

    public final void onDeviceLoaded(Device device) {
        this.device = device;
        for (int i = 0; i < device.attachedSensors.length; i++) {
            AttachedSensor as = device.attachedSensors[i];
            if (StringUtils.equals(as.id, this.sensorId)) {
                this.sensorIndex = i;
                this.sensor = as;
            }
        }
        if (this.sensor != null) {
            this.enabled.setChecked(this.sensor.enabled);
            onEnabledChanged(this.sensor.enabled);
            this.rainSensorText.setText(String.format("Enable this rain sensor only if you connected a sensor wire to the terminal labeled S%d. Enabling a rain sensor that is not connected to a sensor terminal will cause your watering to be skipped.", new Object[]{Integer.valueOf(this.sensor.physicalSensorId)}));
            if (this.sensor.type == Type.FLOW) {
                this.type.setSelection(1);
                setSelectedFlowSensor();
                return;
            }
            return;
        }
        throw new IllegalStateException("couldn't find sensor to configure in loaded device");
    }

    private void setSelectedFlowSensor() {
        if (this.sensor != null && this.flowSensorTypes.getAdapter() != null) {
            ListAdapter adapter = this.flowSensorTypes.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                FlowSensor fs = (FlowSensor) adapter.getItem(i);
                if (StringUtils.equals(fs.make, this.sensor.make) && StringUtils.equals(fs.model, this.sensor.model)) {
                    this.flowSensorTypes.setItemChecked(i, true);
                    return;
                }
            }
        }
    }

    public final boolean isBusy() {
        return false;
    }
}
