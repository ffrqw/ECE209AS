package com.rachio.iro.ui.activity.zone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchWaterJournalCommand;
import com.rachio.iro.async.command.FetchWaterJournalCommand.Listener;
import com.rachio.iro.async.command.FetchWaterJournalCommand.WaterJournalData;
import com.rachio.iro.async.command.FetchZoneCommand;
import com.rachio.iro.async.command.FetchZoneCommand.FetchZoneListener;
import com.rachio.iro.async.command.FetchZoneCommand.ZoneDataHolder;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.zoneproperties.Crop;
import com.rachio.iro.model.zoneproperties.Nozzle;
import com.rachio.iro.model.zoneproperties.Shade;
import com.rachio.iro.model.zoneproperties.Slope;
import com.rachio.iro.model.zoneproperties.Soil;
import com.rachio.iro.model.zoneproperties.ZonePropertyCommon;
import com.rachio.iro.utils.ProgressDialogAsyncTask;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask.SimpleCallback;
import com.rachio.iro.utils.StringUtils;
import com.rachio.iro.utils.ValidationUtils;
import com.squareup.picasso.Picasso;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ZoneDetailsActivity extends ActivityThatSavesZone implements Listener, FetchZoneListener {
    private File captureImageTemp;
    private File croppedImageTemp;
    private Crop customCrop;
    private Nozzle customNozzle;
    private Shade customShade;
    private Slope customSlope;
    private Soil customSoil;
    private SwitchCompat enabledSwitch;
    private FetchWaterJournalCommand fetchWaterJournalCommand;
    private FetchZoneCommand fetchZoneCommand;
    private ImageView image;
    private boolean loaded;
    private TextView moistureLevel;
    private GridLayout moistureLevelContainer;
    private EditText nameEdit;
    private String newPropertyId = null;
    private ImageView nozzleIcon;
    private TextView nozzleLabel;
    private int requestCode = -1;
    private ImageView shadeIcon;
    private TextView shadeLabel;
    private ImageView slopeIcon;
    private TextView slopeLabel;
    private ImageView soilIcon;
    private TextView soilLabel;
    private ImageView typeIcon;
    private TextView typeLabel;
    private ZoneDataHolder zoneData;
    private String zoneId;
    private Bitmap zoneImage;

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("loaded", Boolean.valueOf(this.loaded));
        outState.putSerializable("captureimage", this.captureImageTemp);
        outState.putSerializable("croptempfile", this.croppedImageTemp);
        outState.putParcelable("zoneimage", this.zoneImage);
        outState.putSerializable("crop", this.customCrop);
        outState.putSerializable("soil", this.customSoil);
        outState.putSerializable("shade", this.customShade);
        outState.putSerializable("nozzle", this.customNozzle);
        outState.putSerializable("slope", this.customSlope);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.loaded = savedInstanceState.getBoolean("loaded");
        this.captureImageTemp = (File) savedInstanceState.getSerializable("captureimage");
        this.croppedImageTemp = (File) savedInstanceState.getSerializable("croptempfile");
        this.zoneImage = (Bitmap) savedInstanceState.getParcelable("zoneimage");
        this.customCrop = (Crop) savedInstanceState.getSerializable("crop");
        this.customSoil = (Soil) savedInstanceState.getSerializable("soil");
        this.customShade = (Shade) savedInstanceState.getSerializable("shade");
        this.customNozzle = (Nozzle) savedInstanceState.getSerializable("nozzle");
        this.customSlope = (Slope) savedInstanceState.getSerializable("slope");
        update();
    }

    private void grantPermissions(Intent intent, Uri uri) {
        for (ResolveInfo resolveInfo : getPackageManager().queryIntentActivities(intent, 65536)) {
            grantUriPermission(resolveInfo.activityInfo.packageName, uri, 3);
        }
    }

    private Uri getCaptureTempUri() {
        return FileProvider.getUriForFile(this, "com.rachio.iro.fileprovider", this.captureImageTemp);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_zone_details);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.zoneId = getZoneIdFromExtras();
        this.image = (ImageView) findViewById(R.id.zone_details_image);
        ImageView takePhoto = (ImageView) findViewById(R.id.zone_details_take);
        ImageView selectPhoto = (ImageView) findViewById(R.id.zone_details_select);
        this.nameEdit = (EditText) findViewById(R.id.zone_details_description_edit);
        this.enabledSwitch = (SwitchCompat) findViewById(R.id.zone_details_enabled_switch);
        this.typeLabel = (TextView) findViewById(R.id.zone_details_type_text);
        this.typeIcon = (ImageView) findViewById(R.id.zone_details_type_icon);
        this.soilLabel = (TextView) findViewById(R.id.zone_details_soil_text);
        this.soilIcon = (ImageView) findViewById(R.id.zone_details_soil_icon);
        this.shadeLabel = (TextView) findViewById(R.id.zone_details_shade_text);
        this.shadeIcon = (ImageView) findViewById(R.id.zone_details_shade_icon);
        this.nozzleLabel = (TextView) findViewById(R.id.zone_details_nozzle_text);
        this.nozzleIcon = (ImageView) findViewById(R.id.zone_details_nozzle_icon);
        this.slopeLabel = (TextView) findViewById(R.id.zone_details_slope_text);
        this.slopeIcon = (ImageView) findViewById(R.id.zone_details_slope_icon);
        this.moistureLevel = (TextView) findViewById(R.id.zone_details_moisturelevel_percentage);
        takePhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ZoneDetailsActivity.access$000(ZoneDetailsActivity.this);
            }
        });
        selectPhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ZoneDetailsActivity.access$100(ZoneDetailsActivity.this);
            }
        });
        this.moistureLevelContainer = (GridLayout) findViewById(R.id.zone_details_moisturelevel);
        this.moistureLevelContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZoneDetailsActivity.this.zoneData != null) {
                    Intent i = new Intent(ZoneDetailsActivity.this, ZoneMoistureLevelActivity.class);
                    i.putExtra("USERID", ZoneDetailsActivity.this.zoneData.user.id);
                    i.putExtra("authtoken", ZoneDetailsActivity.this.prefsWrapper.getLoggedInUserAccessToken());
                    i.putExtra("DEVICEID", ZoneDetailsActivity.this.zoneData.zone.device.id);
                    i.putExtra("ZONEID", ZoneDetailsActivity.this.zoneData.zone.id);
                    ZoneDetailsActivity.this.startActivity(i);
                }
            }
        });
        findViewById(R.id.zone_details_type).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZoneDetailsActivity.this.customCrop != null) {
                    Intent i = new Intent(ZoneDetailsActivity.this, CropListActivity.class);
                    i.putExtra("selectedid", ZoneDetailsActivity.this.customCrop.id);
                    ZoneDetailsActivity.this.startActivityForResult(i, 100);
                }
            }
        });
        findViewById(R.id.zone_details_soil).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZoneDetailsActivity.this.customSoil != null) {
                    Intent i = new Intent(ZoneDetailsActivity.this, SoilListActivity.class);
                    i.putExtra("selectedid", ZoneDetailsActivity.this.customSoil.id);
                    ZoneDetailsActivity.this.startActivityForResult(i, 101);
                }
            }
        });
        findViewById(R.id.zone_details_shade).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZoneDetailsActivity.this.customShade != null) {
                    Intent i = new Intent(ZoneDetailsActivity.this, ShadeListActivity.class);
                    i.putExtra("selectedid", ZoneDetailsActivity.this.customShade.id);
                    ZoneDetailsActivity.this.startActivityForResult(i, 102);
                }
            }
        });
        findViewById(R.id.zone_details_nozzle).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZoneDetailsActivity.this.customNozzle != null) {
                    Intent i = new Intent(ZoneDetailsActivity.this, NozzleListActivity.class);
                    i.putExtra("selectedid", ZoneDetailsActivity.this.customNozzle.id);
                    ZoneDetailsActivity.this.startActivityForResult(i, 103);
                }
            }
        });
        findViewById(R.id.zone_details_slope).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZoneDetailsActivity.this.customSlope != null) {
                    Intent i = new Intent(ZoneDetailsActivity.this, SlopeListActivity.class);
                    i.putExtra("selectedid", ZoneDetailsActivity.this.customSlope.id);
                    ZoneDetailsActivity.this.startActivityForResult(i, 104);
                }
            }
        });
        ((LinearLayout) findViewById(R.id.zone_details_advanced)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ZoneDetailsActivity.this, AdvancedZonePropertiesActivity.class);
                i.putExtra("ZONEID", ZoneDetailsActivity.this.zoneId);
                ZoneDetailsActivity.this.startActivity(i);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchZoneCommand == null) {
            this.fetchZoneCommand = new FetchZoneCommand(this, this.zoneId);
            this.fetchZoneCommand.execute();
        }
        if (this.fetchWaterJournalCommand == null) {
            this.fetchWaterJournalCommand = new FetchWaterJournalCommand(this, this.zoneId);
            this.fetchWaterJournalCommand.execute();
        }
    }

    private static void setLabelAndIcon(ZonePropertyCommon prop, TextView label, ImageView icon) {
        if (prop != null) {
            label.setText(prop.name);
            Picasso.with().load(prop.imageUrl).into(icon);
        }
    }

    private void update() {
        setLabelAndIcon(this.customCrop, this.typeLabel, this.typeIcon);
        setLabelAndIcon(this.customSoil, this.soilLabel, this.soilIcon);
        setLabelAndIcon(this.customShade, this.shadeLabel, this.shadeIcon);
        setLabelAndIcon(this.customNozzle, this.nozzleLabel, this.nozzleIcon);
        setLabelAndIcon(this.customSlope, this.slopeLabel, this.slopeIcon);
    }

    private String getName() {
        return StringUtils.nullEmptyString(this.nameEdit.getEditableText().toString());
    }

    public final boolean hasChanges() {
        if (this.zoneData == null) {
            return false;
        }
        if (this.zoneImage == null && StringUtils.equals(getName(), this.zoneData.zone.name) && this.zoneData.zone.enabled == this.enabledSwitch.isChecked() && this.zoneData.zone.customCrop.id.equals(this.customCrop.id) && this.zoneData.zone.customNozzle.id.equals(this.customNozzle.id) && this.zoneData.zone.customShade.id.equals(this.customShade.id) && this.zoneData.zone.customSlope.id.equals(this.customSlope.id) && this.zoneData.zone.customSoil.id.equals(this.customSoil.id)) {
            return false;
        }
        return true;
    }

    public final void save() {
        String newZoneName = getName();
        if (StringUtils.equals(newZoneName, this.zoneData.zone.name) || ValidationUtils.isValidZoneName(newZoneName)) {
            Zone newZone = (Zone) ModelObject.deepClone(Zone.class, this.zoneData.zone);
            newZone.device = this.zoneData.device;
            newZone.name = newZoneName;
            newZone.enabled = this.enabledSwitch.isChecked();
            newZone.customCrop.id = this.customCrop.id;
            newZone.customNozzle.id = this.customNozzle.id;
            newZone.customShade.id = this.customShade.id;
            newZone.customSlope.id = this.customSlope.id;
            newZone.customSoil.id = this.customSoil.id;
            saveZone(newZone, this.zoneImage, new SimpleCallback() {
                public final void onSuccess() {
                    ZoneDetailsActivity.this.finish();
                }
            });
        }
    }

    private void wireUpNewPropertyValue(int requestCode, String newValueId) {
        switch (requestCode) {
            case 100:
                this.customCrop = this.zoneData.user.getCropById(newValueId);
                return;
            case 101:
                this.customSoil = this.zoneData.user.getSoilById(newValueId);
                return;
            case 102:
                this.customShade = this.zoneData.user.getShadeById(newValueId);
                return;
            case 103:
                this.customNozzle = this.zoneData.user.getNozzleById(newValueId);
                return;
            case 104:
                this.customSlope = this.zoneData.user.getSlopeById(newValueId);
                return;
            default:
                return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9162 && resultCode == -1) {
            revokeUriPermission(getCaptureTempUri(), 3);
            new ProgressDialogAsyncTask<Void, Void, Uri[]>(this) {
                protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                    return doInBackground$37a0cdf();
                }

                protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                    Uri[] uriArr = (Uri[]) obj;
                    super.onPostExecute(uriArr);
                    com.soundcloud.android.crop.Crop withAspect = com.soundcloud.android.crop.Crop.of(uriArr[0], uriArr[1]).withAspect(2, 1);
                    Context context = ZoneDetailsActivity.this;
                    context.startActivityForResult(withAspect.getIntent(context), 6709);
                }

                private Uri[] doInBackground$37a0cdf() {
                    try {
                        Uri original = FileProvider.getUriForFile(ZoneDetailsActivity.this, "com.rachio.iro.fileprovider", ZoneDetailsActivity.this.captureImageTemp);
                        if (data != null) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                BufferedInputStream bis = new BufferedInputStream(ZoneDetailsActivity.this.getContentResolver().openInputStream(uri));
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ZoneDetailsActivity.this.captureImageTemp));
                                byte[] buf = new byte[1024];
                                while (true) {
                                    int read = bis.read(buf);
                                    if (read <= 0) {
                                        break;
                                    }
                                    bos.write(buf, 0, read);
                                }
                                bos.close();
                            }
                        }
                        int orientation = new ExifInterface(ZoneDetailsActivity.this.captureImageTemp.getAbsolutePath()).getAttributeInt("Orientation", 0);
                        if (!(orientation == 1 || orientation == 0)) {
                            Bitmap bm = BitmapFactory.decodeFile(ZoneDetailsActivity.this.captureImageTemp.getAbsolutePath());
                            FileOutputStream fos = new FileOutputStream(ZoneDetailsActivity.this.captureImageTemp);
                            Matrix rotationMatrix = new Matrix();
                            switch (orientation) {
                                case 3:
                                    rotationMatrix.postRotate(180.0f);
                                    break;
                                case 6:
                                    rotationMatrix.postRotate(90.0f);
                                    break;
                                case 8:
                                    rotationMatrix.postRotate(270.0f);
                                    break;
                                default:
                                    throw new RuntimeException("unhandled orientation " + orientation);
                            }
                            Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), rotationMatrix, true).compress(CompressFormat.PNG, 100, fos);
                            fos.close();
                        }
                        ZoneDetailsActivity.this.croppedImageTemp = File.createTempFile("image", "crop");
                        return new Uri[]{original, Uri.fromFile(ZoneDetailsActivity.this.croppedImageTemp)};
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }.execute(new Void[]{null});
        } else if (requestCode == 6709 && resultCode == -1) {
            this.zoneImage = BitmapFactory.decodeFile(this.croppedImageTemp.getAbsolutePath());
            this.image.setImageBitmap(this.zoneImage);
        } else {
            if (resultCode == -1) {
                String id = data.getStringExtra("selectedid");
                if (this.zoneData != null) {
                    wireUpNewPropertyValue(requestCode, id);
                } else {
                    this.requestCode = requestCode;
                    this.newPropertyId = id;
                }
            }
            update();
        }
    }

    public final void onZoneFetched(ZoneDataHolder zone) {
        this.fetchZoneCommand = null;
        if (zone != null) {
            this.zoneData = zone;
            if (this.zoneImage == null) {
                Picasso.with().load(this.zoneData.zone.imageUrl).into(this.image);
            }
            if (!this.loaded) {
                this.customCrop = zone.customCrop;
                this.customSoil = zone.customSoil;
                this.customShade = zone.customShade;
                this.customNozzle = zone.customNozzle;
                this.customSlope = zone.customSlope;
                this.nameEdit.setText(this.zoneData.zone.name);
                this.enabledSwitch.setChecked(this.zoneData.zone.enabled);
                this.loaded = true;
            }
            if (this.newPropertyId != null) {
                wireUpNewPropertyValue(this.requestCode, this.newPropertyId);
                this.requestCode = -1;
                this.newPropertyId = null;
            }
            this.moistureLevelContainer.setVisibility(zone.isInFlexRule ? 0 : 8);
            update();
        }
    }

    public final boolean isBusy() {
        return this.fetchZoneCommand != null;
    }

    public final boolean isValid() {
        return true;
    }

    public final void onWaterJournalFetched(WaterJournalData waterJournalData) {
        if (waterJournalData == null || waterJournalData.moistureLevel < 0.0d) {
            this.moistureLevel.setText("Unknown");
            return;
        }
        this.moistureLevel.setText(String.format("%d%%", new Object[]{Integer.valueOf((int) (waterJournalData.moistureLevel * 100.0d))}));
    }

    static /* synthetic */ void access$000(ZoneDetailsActivity x0) {
        try {
            File file = new File(x0.getFilesDir(), "capturetmp");
            file.mkdirs();
            x0.captureImageTemp = File.createTempFile("image", "cap", file);
            Object captureTempUri = x0.getCaptureTempUri();
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra("output", captureTempUri);
            x0.grantPermissions(intent, captureTempUri);
            x0.startActivityForResult(intent, 9162);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static /* synthetic */ void access$100(ZoneDetailsActivity x0) {
        try {
            File file = new File(x0.getFilesDir(), "capturetmp");
            file.mkdirs();
            x0.captureImageTemp = File.createTempFile("image", "cap", file);
            com.soundcloud.android.crop.Crop.pickImage(x0, 9162);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
