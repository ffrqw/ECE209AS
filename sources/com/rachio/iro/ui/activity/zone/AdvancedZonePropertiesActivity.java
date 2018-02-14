package com.rachio.iro.ui.activity.zone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchZoneCommand;
import com.rachio.iro.async.command.FetchZoneCommand.FetchZoneListener;
import com.rachio.iro.async.command.FetchZoneCommand.ZoneDataHolder;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.db.DatabaseObject;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.ui.activity.HelpActivity;
import com.rachio.iro.utils.FuzzyEquality;
import com.rachio.iro.utils.InvalidateOptionsMenuTextWatcher;
import com.rachio.iro.utils.NumberParsingUtils;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask.SimpleCallback;
import com.rachio.iro.utils.UnitUtils;

public class AdvancedZonePropertiesActivity extends ActivityThatSavesZone implements FetchZoneListener {
    private SeekBar allowedDepletion;
    private TextView area;
    private String areaUnits;
    private TextView areaUnitsLabel;
    private TextView availableWater;
    private TextView availableWaterUnits;
    private SeekBar cropCoefficient;
    private SeekBar efficiency;
    private FetchZoneCommand fetchZoneCommand;
    private String lengthUnits;
    private double maxArea;
    private double maxAvailableWater;
    private double maxRootDepth;
    private TextView rootDepth;
    private TextView rootDepthUnits;
    private ZoneDataHolder zoneData;
    private String zoneId;

    private static class BaseOnSeekBarChangeListener implements OnSeekBarChangeListener {
        private BaseOnSeekBarChangeListener() {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_zoneproperties_advanced);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.zoneId = getZoneIdFromExtras();
        this.area = (TextView) findViewById(R.id.zoneproperties_area);
        this.areaUnitsLabel = (TextView) findViewById(R.id.zoneproperties_area_units);
        this.availableWater = (TextView) findViewById(R.id.zoneproperties_availablewater);
        this.availableWaterUnits = (TextView) findViewById(R.id.zoneproperties_availablewater_units);
        this.rootDepth = (TextView) findViewById(R.id.zoneproperties_rootdepth);
        this.rootDepthUnits = (TextView) findViewById(R.id.zoneproperties_rootdepth_units);
        final TextView allowedDepletionValue = (TextView) findViewById(R.id.zoneproperties_advanced_allowdepletion_value);
        this.allowedDepletion = (SeekBar) findViewById(R.id.zoneproperties_advanced_allowdepletion);
        final TextView efficiencyValue = (TextView) findViewById(R.id.zoneproperties_advanced_efficiency_value);
        this.efficiency = (SeekBar) findViewById(R.id.zoneproperties_advanced_efficiency);
        final TextView cropCoefficientValue = (TextView) findViewById(R.id.zoneproperties_advanced_cropcoefficient_value);
        this.cropCoefficient = (SeekBar) findViewById(R.id.zoneproperties_advanced_cropcoefficient);
        Button reset = (Button) findViewById(R.id.zoneproperties_advanced_reset);
        this.area.addTextChangedListener(new InvalidateOptionsMenuTextWatcher(this));
        this.availableWater.addTextChangedListener(new InvalidateOptionsMenuTextWatcher(this));
        this.rootDepth.addTextChangedListener(new InvalidateOptionsMenuTextWatcher(this));
        this.allowedDepletion.setOnSeekBarChangeListener(new BaseOnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                allowedDepletionValue.setText(String.format("%d%%", new Object[]{Integer.valueOf(progress)}));
            }
        });
        this.efficiency.setOnSeekBarChangeListener(new BaseOnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                efficiencyValue.setText(String.format("%d%%", new Object[]{Integer.valueOf(progress)}));
            }
        });
        this.cropCoefficient.setOnSeekBarChangeListener(new BaseOnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cropCoefficientValue.setText(String.format("%d%%", new Object[]{Integer.valueOf(progress)}));
            }
        });
        reset.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new RestClientProgressDialogAsyncTask<Void, Void, Zone>(AdvancedZonePropertiesActivity.this) {
                    public final void onFailure(ErrorResponse errorResponse) {
                    }

                    public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                        AdvancedZonePropertiesActivity.this.update();
                    }

                    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                        AdvancedZonePropertiesActivity.this.database.lock();
                        HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                        DatabaseObject resetZone = this.holder.restClient.resetZone((Zone) ModelObject.transmittableClone(Zone.class, AdvancedZonePropertiesActivity.this.zoneData.zone), httpResponseErrorHandler);
                        if (!(httpResponseErrorHandler.hasError || resetZone == null)) {
                            resetZone.device = AdvancedZonePropertiesActivity.this.zoneData.zone.device;
                            AdvancedZonePropertiesActivity.this.database.save(resetZone);
                            AdvancedZonePropertiesActivity.this.database.refresh(AdvancedZonePropertiesActivity.this.zoneData.zone);
                        }
                        AdvancedZonePropertiesActivity.this.database.unlock();
                        return resetZone;
                    }
                }.execute(new Void[]{null});
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchZoneCommand == null) {
            this.fetchZoneCommand = new FetchZoneCommand(this, this.zoneId);
            this.fetchZoneCommand.execute();
        }
    }

    private void update() {
        this.area.setText(String.format("%.2f", new Object[]{Double.valueOf(UnitUtils.convertSquareYardsToUserUnits(this.zoneData.user, this.zoneData.zone.yardAreaSquareFeet))}));
        this.availableWater.setText(String.format("%.2f", new Object[]{Double.valueOf(UnitUtils.convertInchesToUserUnits(this.zoneData.user, this.zoneData.zone.availableWater))}));
        this.rootDepth.setText(String.format("%.2f", new Object[]{Double.valueOf(UnitUtils.convertInchesToUserUnits(this.zoneData.user, this.zoneData.zone.rootZoneDepth))}));
        this.allowedDepletion.setProgress((int) (this.zoneData.zone.managementAllowedDepletion * 100.0d));
        this.efficiency.setProgress((int) (this.zoneData.zone.efficiency * 100.0d));
        this.cropCoefficient.setProgress((int) (this.zoneData.zone.cropCoefficient * 100.0d));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.snippet_info, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                Intent i = new Intent(this, HelpActivity.class);
                i.putExtra("article", "304-advanced-zone-settings");
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public final boolean hasChanges() {
        if (this.zoneData != null && FuzzyEquality.fuzzyEqual(UnitUtils.converUserUnitsToSquareYards(this.zoneData.user, getArea()), this.zoneData.zone.yardAreaSquareFeet) && FuzzyEquality.fuzzyEqual(UnitUtils.convertUserUnitsToInches(this.zoneData.user, getAvailableWater()), this.zoneData.zone.availableWater) && FuzzyEquality.fuzzyEqual(UnitUtils.convertUserUnitsToInches(this.zoneData.user, getZoneDepth()), this.zoneData.zone.rootZoneDepth) && getEfficiency() == this.zoneData.zone.efficiency && getDepletion() == this.zoneData.zone.managementAllowedDepletion && getCropCoefficient() == this.zoneData.zone.cropCoefficient) {
            return false;
        }
        return true;
    }

    private double getArea() {
        String areaString = this.area.getEditableText().toString();
        if (TextUtils.isEmpty(areaString)) {
            return 0.0d;
        }
        return NumberParsingUtils.parseDouble(areaString, 0.0d);
    }

    private double getAvailableWater() {
        String availableWaterString = this.availableWater.getEditableText().toString();
        if (TextUtils.isEmpty(availableWaterString)) {
            return 0.0d;
        }
        return NumberParsingUtils.parseDouble(availableWaterString, 0.0d);
    }

    private double getZoneDepth() {
        String rootDepthString = this.rootDepth.getEditableText().toString();
        if (TextUtils.isEmpty(rootDepthString)) {
            return 0.0d;
        }
        return NumberParsingUtils.parseDouble(rootDepthString, 0.0d);
    }

    public final boolean isValid() {
        if (getArea() > this.maxArea) {
            Toast.makeText(this, String.format("Area must be less than or equal %.2f %s", new Object[]{Double.valueOf(this.maxArea), this.areaUnits}), 0).show();
            return false;
        } else if (getAvailableWater() > this.maxAvailableWater) {
            Toast.makeText(this, String.format("Available water must be less than or equal %.2f %s", new Object[]{Double.valueOf(this.maxAvailableWater), this.lengthUnits}), 0).show();
            return false;
        } else if (getZoneDepth() <= this.maxRootDepth) {
            return true;
        } else {
            Toast.makeText(this, String.format("Root depth must be less than or equal %.2f %s", new Object[]{Double.valueOf(this.maxRootDepth), this.lengthUnits}), 0).show();
            return false;
        }
    }

    private double getEfficiency() {
        return ((double) this.efficiency.getProgress()) / 100.0d;
    }

    private double getCropCoefficient() {
        return ((double) this.cropCoefficient.getProgress()) / 100.0d;
    }

    private double getDepletion() {
        return ((double) this.allowedDepletion.getProgress()) / 100.0d;
    }

    public final void save() {
        this.zoneData.zone.yardAreaSquareFeet = UnitUtils.converUserUnitsToSquareYards(this.zoneData.user, Math.min(getArea(), this.maxArea));
        this.zoneData.zone.availableWater = UnitUtils.convertUserUnitsToInches(this.zoneData.user, Math.min(getAvailableWater(), this.maxAvailableWater));
        this.zoneData.zone.rootZoneDepth = UnitUtils.convertUserUnitsToInches(this.zoneData.user, Math.min(getZoneDepth(), this.maxRootDepth));
        this.zoneData.zone.managementAllowedDepletion = getDepletion();
        this.zoneData.zone.efficiency = getEfficiency();
        this.zoneData.zone.cropCoefficient = getCropCoefficient();
        saveZone(this.zoneData.zone, null, new SimpleCallback() {
            public final void onSuccess() {
                AdvancedZonePropertiesActivity.this.finish();
            }
        });
    }

    public final void onZoneFetched(ZoneDataHolder zones) {
        this.fetchZoneCommand = null;
        this.zoneData = zones;
        this.maxRootDepth = UnitUtils.convertInchesToUserUnits(zones.user, 36.0d);
        this.maxAvailableWater = UnitUtils.convertInchesToUserUnits(zones.user, 0.20000000298023224d);
        this.maxArea = UnitUtils.convertSquareYardsToUserUnits(zones.user, 10000.0d);
        this.areaUnits = UnitUtils.getNameOfAreaUnits(zones.user);
        this.areaUnitsLabel.setText(this.areaUnits);
        this.lengthUnits = UnitUtils.getMinorLengthUnitName(zones.user);
        this.availableWaterUnits.setText(this.lengthUnits);
        this.rootDepthUnits.setText(this.lengthUnits);
        update();
    }

    public final boolean isBusy() {
        return this.fetchZoneCommand != null;
    }
}
