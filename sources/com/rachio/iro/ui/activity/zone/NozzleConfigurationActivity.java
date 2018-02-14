package com.rachio.iro.ui.activity.zone;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.apionly.BaseResponse;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.Nozzle;
import com.rachio.iro.ui.activity.ActivityThatSaves;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.utils.BaseTextWatcher;
import com.rachio.iro.utils.NumberParsingUtils;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.StringUtils;
import com.rachio.iro.utils.UnitUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NozzleConfigurationActivity extends BaseActivity implements ActivityThatSaves {
    private EditText flowRate;
    private EditText manfacturer;
    private EditText model;
    private EditText name;
    private Nozzle newNozzle;
    private Nozzle originalNozzle;
    private Spinner type;
    private User user;

    /* renamed from: com.rachio.iro.ui.activity.zone.NozzleConfigurationActivity$3 */
    class AnonymousClass3 extends ArrayAdapter<Nozzle> {
        AnonymousClass3(Context x0, int x1, List x2) {
            super(x0, -1, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout view = (LinearLayout) NozzleConfigurationActivity.this.getLayoutInflater().inflate(R.layout.list_usernozzle, null);
            Nozzle n = (Nozzle) getItem(position);
            Picasso.with().load(n.imageUrl).into((ImageView) view.findViewById(R.id.usernozzle_icon));
            ((TextView) view.findViewById(R.id.usernozzle_name)).setText(n.name);
            return view;
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IroApplication.get(this).component().inject(this);
        this.originalNozzle = (Nozzle) getIntent().getSerializableExtra("newNozzle");
        if (this.originalNozzle == null) {
            throw new IllegalStateException();
        }
        this.newNozzle = (Nozzle) ModelObject.deepClone(Nozzle.class, this.originalNozzle);
        setContentView((int) R.layout.activity_nozzleconfiguration);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.name = (EditText) findViewById(R.id.nozzleconfiguration_name);
        this.type = (Spinner) findViewById(R.id.nozzleconfiguration_type);
        this.manfacturer = (EditText) findViewById(R.id.nozzleconfiguration_manufacturer);
        this.model = (EditText) findViewById(R.id.nozzleconfiguration_model);
        this.flowRate = (EditText) findViewById(R.id.nozzleconfiguration_flowrate);
        TextView flowRateLabel = (TextView) findViewById(R.id.nozzleconfiguration_flowrate_units);
        this.user = User.getLoggedInUser(((IroApplication) getApplication()).getDatabase(), ((IroApplication) getApplication()).getPrefsWrapper());
        if (TextUtils.isEmpty(this.newNozzle.name)) {
            this.newNozzle.name = String.format("Nozzle-%d", new Object[]{Integer.valueOf(this.user.nozzles.length + 1)});
        }
        this.name.setText(this.newNozzle.name);
        this.flowRate.setText(String.format("%.2f", new Object[]{Float.valueOf((float) UnitUtils.convertPrecipToUserUnits(this.user, this.newNozzle.inchesPerHour))}));
        this.name.addTextChangedListener(new BaseTextWatcher() {
            public void afterTextChanged(Editable s) {
                NozzleConfigurationActivity.this.supportInvalidateOptionsMenu();
            }
        });
        this.flowRate.addTextChangedListener(new BaseTextWatcher() {
            public void afterTextChanged(Editable s) {
                NozzleConfigurationActivity.this.supportInvalidateOptionsMenu();
            }
        });
        List<Nozzle> customizableNozzles = new ArrayList();
        for (Nozzle n : this.user.nozzles) {
            if (n.customizable) {
                customizableNozzles.add(n);
            }
        }
        Collections.sort(customizableNozzles);
        int selectedType = 0;
        for (int i = 0; i < customizableNozzles.size(); i++) {
            if (((Nozzle) customizableNozzles.get(i)).category.equals(this.newNozzle.category)) {
                selectedType = i;
                break;
            }
        }
        this.type.setAdapter(new AnonymousClass3(this, -1, customizableNozzles));
        this.type.setSelection(selectedType);
        this.model.setText(this.newNozzle.model);
        this.manfacturer.setText(this.newNozzle.manufacturer);
        flowRateLabel.setText(UnitUtils.getPrecipUnitName(this.user));
    }

    private double getFlowRate() {
        String flowRate = this.flowRate.getEditableText().toString();
        if (TextUtils.isEmpty(flowRate)) {
            return 0.0d;
        }
        return UnitUtils.convertUserUnitsToPrecip(this.user, NumberParsingUtils.parseDouble(flowRate, 0.0d));
    }

    private void commit() {
        this.newNozzle.name = this.name.getEditableText().toString();
        this.newNozzle.manufacturer = this.manfacturer.getEditableText().toString();
        this.newNozzle.model = this.model.getEditableText().toString();
        Nozzle base = (Nozzle) this.type.getSelectedItem();
        this.newNozzle.category = base.category;
        this.newNozzle.imageUrl = base.imageUrl;
        this.newNozzle.inchesPerHour = getFlowRate();
    }

    public final void save() {
        commit();
        new RestClientProgressDialogAsyncTask<Nozzle, Void, BaseResponse>(this) {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                Boolean valueOf;
                int i = 0;
                Nozzle[] nozzleArr = (Nozzle[]) objArr;
                this.holder.database.lock();
                ModelObject modelObject = nozzleArr[0];
                modelObject.userId = NozzleConfigurationActivity.this.user.id;
                Boolean valueOf2 = Boolean.valueOf(false);
                HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                Nozzle nozzle;
                Nozzle[] nozzleArr2;
                if (modelObject.id != null) {
                    nozzle = (Nozzle) this.holder.restClient.putObject(Nozzle.class, modelObject, httpResponseErrorHandler);
                    if (!(httpResponseErrorHandler.hasError || nozzle == null)) {
                        nozzleArr2 = (Nozzle[]) Arrays.copyOf(NozzleConfigurationActivity.this.user.nozzles, NozzleConfigurationActivity.this.user.nozzles.length);
                        nozzle.editable = true;
                        while (i < nozzleArr2.length) {
                            if (StringUtils.equals(nozzleArr2[i].id, nozzle.id)) {
                                nozzleArr2[i] = nozzle;
                                break;
                            }
                            i++;
                        }
                        NozzleConfigurationActivity.this.user.nozzles = nozzleArr2;
                        valueOf = Boolean.valueOf(true);
                    }
                    valueOf = valueOf2;
                } else {
                    nozzle = (Nozzle) this.holder.restClient.postObject(Nozzle.class, NozzleConfigurationActivity.this.newNozzle, httpResponseErrorHandler);
                    if (!(httpResponseErrorHandler.hasError || nozzle == null)) {
                        nozzleArr2 = (Nozzle[]) Arrays.copyOf(NozzleConfigurationActivity.this.user.nozzles, NozzleConfigurationActivity.this.user.nozzles.length + 1);
                        nozzle.editable = true;
                        nozzleArr2[nozzleArr2.length - 1] = nozzle;
                        NozzleConfigurationActivity.this.user.nozzles = nozzleArr2;
                        valueOf = Boolean.valueOf(true);
                    }
                    valueOf = valueOf2;
                }
                if (valueOf.booleanValue()) {
                    this.holder.database.save(NozzleConfigurationActivity.this.user);
                }
                this.holder.database.unlock();
                if (valueOf.booleanValue()) {
                    return new BaseResponse();
                }
                return null;
            }

            public final void onFailure(ErrorResponse response) {
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                NozzleConfigurationActivity.this.finish();
            }
        }.execute(new Nozzle[]{this.newNozzle});
    }

    public final boolean isValid() {
        Nozzle selectedNozzle = (Nozzle) this.type.getSelectedItem();
        if (TextUtils.isEmpty(this.name.getEditableText().toString())) {
            Toast.makeText(this, "Name must not be empty", 0).show();
            return false;
        } else if (TextUtils.isEmpty(this.flowRate.getEditableText().toString())) {
            Toast.makeText(this, "Flow rate must not be empty", 0).show();
            return false;
        } else if (getFlowRate() <= selectedNozzle.category.maxFlowRate) {
            return true;
        } else {
            Toast.makeText(this, String.format("Flow rate must be less than %f", new Object[]{Double.valueOf(selectedNozzle.category.maxFlowRate)}), 0).show();
            return false;
        }
    }

    public final boolean hasChanges() {
        if (this.newNozzle.id == null) {
            return true;
        }
        commit();
        if (ModelObject.deepCompare(this.originalNozzle, this.newNozzle) == 0) {
            return false;
        }
        return true;
    }

    public final boolean isBusy() {
        return false;
    }
}
