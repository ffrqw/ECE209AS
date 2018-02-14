package com.rachio.iro.ui.newschedulerulepath.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.rachio.iro.R;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.schedule.PreviewScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.FlexScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.FrequencyOperator;
import com.rachio.iro.model.schedule.ScheduleRule.ScheduleJobType;
import com.rachio.iro.model.schedule.ScheduleRule.ScheduleRuleType;
import com.rachio.iro.model.schedule.ZoneInfo;
import com.rachio.iro.ui.FragmentNavigationController.Listener;
import com.rachio.iro.ui.FragmentNavigationController.Screen;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.newschedulerulepath.dialog.RingsProgressDialog;
import com.rachio.iro.ui.newschedulerulepath.dialog.RingsProgressDialog.OnCompleteCallback;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.StartTimeFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.WeatherIntelligenceFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.wizard.WizardFragment;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScheduleRuleWizardActivity extends BaseScheduleRuleActivity implements Listener {
    private static final String TAG = ScheduleRuleWizardActivity.class.getName();
    protected static Screen wizardScreen;
    private RingsProgressDialog flexFakePreviewProgressDialog;
    private Handler handler = new Handler();
    private PreviewScheduleRule state = createDefaultRule();

    public enum Type {
        FIXEDINTERVAL,
        FIXEDDAYS,
        SEASONAL,
        SOILBASED
    }

    static {
        Screen screen = new Screen("wizard", WizardFragment.class);
        wizardScreen = screen;
        screen.registerBranch(105, StartTimeFragment.PATH);
        wizardScreen.registerBranch(106, startDateSelectPath);
        wizardScreen.registerBranch(107, endDateSelectPath);
        wizardScreen.registerBranch(116, WeatherIntelligenceFragment.PATH);
        wizardScreen.registerBranch(101, selectIntervalScreen);
        wizardScreen.registerBranch(103, selectWeekdays);
    }

    public static PreviewScheduleRule createDefaultRule() {
        PreviewScheduleRule state = new PreviewScheduleRule();
        state.id = UUID.randomUUID().toString() + "p";
        Date startDate = GregorianCalendar.getInstance().getTime();
        state.setStartDate(startDate);
        state.absoluteStartDate = startDate;
        return state;
    }

    protected void onCreate(Bundle savedInstanceState) {
        this.rootPath.registerScreen(wizardScreen);
        this.rootPath.hasOwnBackControl = true;
        super.onCreate(savedInstanceState);
        this.controller.setListener(this);
        switch ((Type) getIntent().getExtras().getSerializable("type")) {
            case FIXEDINTERVAL:
                BaseScheduleRuleActivity.justChangeRuleType(this.state, FrequencyOperator.INTERVAL);
                return;
            case FIXEDDAYS:
                BaseScheduleRuleActivity.justChangeRuleType(this.state, FrequencyOperator.WEEKDAY);
                return;
            case SOILBASED:
                this.state.type = ScheduleRuleType.FLEX;
                ScheduleJobType[] scheduleJobTypeArr = new ScheduleJobType[]{ScheduleJobType.ANY};
                this.state.scheduleJobTypes = scheduleJobTypeArr;
                return;
            default:
                return;
        }
    }

    public final void onDeviceLoaded(Device device) {
        this.state.device = device;
        if (this.state.zones == null) {
            this.state.zones = new ArrayList();
            List<Zone> enabledZones = device.getEnabledZones(this.state.isFlex(), null);
            for (int i = 0; i < enabledZones.size(); i++) {
                ZoneInfo zi = new ZoneInfo((Zone) enabledZones.get(i));
                if (this.state.isFlex()) {
                    zi.baseDuration = Integer.valueOf(((Zone) enabledZones.get(i)).runtimeNoMultiplier);
                    zi.multiplier = Double.valueOf(1.0d);
                    zi.wateringAdjustmentLevel = Integer.valueOf(0);
                }
                zi.sortOrder = i;
                this.state.zones.add(zi);
            }
        }
        ((BaseScheduleRuleFragment) this.controller.getCurrentFragment()).updateState(this.state);
    }

    public final void onFragmentComing$3993877b(BaseFragment fragment) {
        if (this.state.device != null) {
            ((BaseScheduleRuleFragment) fragment).updateState(this.state);
        }
    }

    public final void onFragmentGoing(BaseFragment fragment) {
        if (fragment != null) {
            BaseScheduleRuleFragment scheduleRuleFragment = (BaseScheduleRuleFragment) fragment;
            if (scheduleRuleFragment.validate()) {
                scheduleRuleFragment.commitState(this.state);
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("state", this.state);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                new Builder(this).setTitle("Exit Schedule Creation?").setMessage((int) R.string.cancelwizardblurb).setPositiveButton((CharSequence) "Exit", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ScheduleRuleWizardActivity.this.finish();
                    }
                }).setNegativeButton((CharSequence) "Continue", null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onPause() {
        super.onPause();
        ((BaseScheduleRuleFragment) this.controller.getCurrentFragment()).commitState(this.state);
        if (this.flexFakePreviewProgressDialog != null) {
            this.flexFakePreviewProgressDialog.dismiss();
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.state = (PreviewScheduleRule) savedInstanceState.getSerializable("state");
        if (this.state != null) {
            Log.d(TAG, "restored rule");
        }
    }

    public static PreviewScheduleRule previewRule(RestClient restClient, PreviewScheduleRule scheduleRule) {
        if (scheduleRule.getFrequencyOperator() == FrequencyOperator.ASNEEDED) {
            scheduleRule.scheduleJobTypes = null;
        }
        Iterator it = scheduleRule.zones.iterator();
        while (it.hasNext()) {
            ((ZoneInfo) it.next()).duration = null;
        }
        HttpResponseErrorHandler errorHandler = new HttpResponseErrorHandler();
        HashMap<String, String> queryParams = new HashMap();
        queryParams.put("preview", "true");
        PreviewScheduleRule response = (PreviewScheduleRule) restClient.postObject(PreviewScheduleRule.class, scheduleRule, queryParams, errorHandler);
        if (!(response == null || response.zones == null)) {
            Collections.sort(response.zones);
        }
        return response;
    }

    public final void getPreview() {
        if (this.state.isFlex()) {
            if (this.state.zones.size() >= this.state.device.getEnabledZones(true, null).size()) {
                this.state.name = "Water all zones";
            } else {
                ArrayList<String> zoneNames = new ArrayList();
                Iterator it = this.state.zones.iterator();
                while (it.hasNext()) {
                    zoneNames.add(((Zone) this.state.device.getZonesMap().get(((ZoneInfo) it.next()).zoneId)).name);
                }
                this.state.name = TextUtils.join(", ", zoneNames);
            }
            this.state.previewed = true;
            this.flexFakePreviewProgressDialog = new RingsProgressDialog(this);
            this.flexFakePreviewProgressDialog.show();
            this.handler.postDelayed(new Runnable() {
                public void run() {
                    ((BaseScheduleRuleFragment) ScheduleRuleWizardActivity.this.controller.getCurrentFragment()).updateState(ScheduleRuleWizardActivity.this.state);
                    ScheduleRuleWizardActivity.this.flexFakePreviewProgressDialog.dismiss();
                }
            }, 2000);
            return;
        }
        RestClientProgressDialogAsyncTask<PreviewScheduleRule, Void, PreviewScheduleRule> task = new RestClientProgressDialogAsyncTask<PreviewScheduleRule, Void, PreviewScheduleRule>(this, "Computing Watering Times...") {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                return ScheduleRuleWizardActivity.previewRule(this.holder.restClient, ((PreviewScheduleRule[]) objArr)[0]);
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                PreviewScheduleRule previewScheduleRule = (PreviewScheduleRule) errorResponse;
                previewScheduleRule.device = ScheduleRuleWizardActivity.this.state.device;
                previewScheduleRule.previewed = true;
                ScheduleRuleWizardActivity.this.state = previewScheduleRule;
                ((BaseScheduleRuleFragment) ScheduleRuleWizardActivity.this.controller.getCurrentFragment()).updateState(ScheduleRuleWizardActivity.this.state);
            }

            public final void onFailure(ErrorResponse errorResponse) {
            }
        };
        task.setProgressDialogClass(RingsProgressDialog.class);
        task.execute(new PreviewScheduleRule[]{this.state});
    }

    public final void changeRuleType(FrequencyOperator type) {
        super.changeRuleType(this.state, type);
    }

    public static void start(Context context, String deviceId, Type type) {
        if (deviceId == null) {
            throw new IllegalArgumentException();
        } else if (type == null) {
            throw new IllegalArgumentException();
        } else {
            Intent i = new Intent(context, ScheduleRuleWizardActivity.class);
            i.putExtra("DEVICEID", deviceId);
            i.putExtra("type", type);
            context.startActivity(i);
        }
    }

    public final void onAllPathsComplete() {
        ((BaseScheduleRuleFragment) this.controller.getCurrentFragment()).commitState(this.state);
        RestClientProgressDialogAsyncTask anonymousClass4 = new RestClientProgressDialogAsyncTask<PreviewScheduleRule, Void, ScheduleRule>(this, "Building Schedule...") {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                PreviewScheduleRule[] previewScheduleRuleArr = (PreviewScheduleRule[]) objArr;
                ScheduleRuleWizardActivity.this.database.lock();
                Map hashMap = new HashMap();
                hashMap.put("preview", "false");
                ModelObject modelObject = previewScheduleRuleArr[0];
                modelObject.id = null;
                Class cls = modelObject.isFlex() ? FlexScheduleRule.class : ScheduleRule.class;
                ScheduleRule scheduleRule = (ScheduleRule) this.holder.restClient.postObject(cls, cls, modelObject, hashMap, this.errorHandler);
                if (!(this.errorHandler.hasError || scheduleRule == null)) {
                    scheduleRule.device = modelObject.device;
                    this.holder.database.save(scheduleRule, true);
                    BaseBaseScheduleRuleActivity.invalidateCalendar(ScheduleRuleWizardActivity.this.database, scheduleRule.device.id);
                }
                ScheduleRuleWizardActivity.this.database.unlock();
                return scheduleRule;
            }

            public final void onFailure(ErrorResponse error) {
                getCustomProgressDialog().dismiss();
            }

            protected void onPreExecute() {
                super.onPreExecute();
                setOptions(false);
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                ScheduleRuleWizardActivity.this.tracker.send(new EventBuilder("schedule", "create").build());
                ((RingsProgressDialog) getCustomProgressDialog()).onComplete(new OnCompleteCallback() {
                    public final void onComplete() {
                        ScheduleRuleWizardActivity.this.finish();
                    }
                });
            }
        };
        anonymousClass4.setProgressDialogClass(RingsProgressDialog.class);
        anonymousClass4.execute(new PreviewScheduleRule[]{this.state});
    }
}
