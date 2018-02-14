package com.rachio.iro.ui.newschedulerulepath.activity;

import android.os.Bundle;
import android.util.Log;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchAndCopyScheduleRuleCommand;
import com.rachio.iro.async.command.FetchAndCopyScheduleRuleCommand.Listener;
import com.rachio.iro.async.command.FetchAndCopyScheduleRuleCommand.ScheduleRuleAndCopy;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.schedule.PreviewScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.FrequencyOperator;
import com.rachio.iro.model.schedule.ZoneInfo;
import com.rachio.iro.ui.FragmentNavigationController;
import com.rachio.iro.ui.FragmentNavigationController.Path;
import com.rachio.iro.ui.FragmentNavigationController.Screen;
import com.rachio.iro.ui.activity.ActivityThatSaves;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.newschedulerulepath.activity.BaseBaseScheduleRuleActivity.AnonymousClass5;
import com.rachio.iro.ui.newschedulerulepath.dialog.RingsProgressDialog;
import com.rachio.iro.ui.newschedulerulepath.fragments.BaseScheduleRuleFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.StartTimeFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.WeatherIntelligenceFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.editor.DurationsFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.editor.RuleDetailsFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.editor.RuleNameFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.editor.ZoneSelectFragment;
import com.rachio.iro.ui.newschedulerulepath.fragments.wizard.HowOftenToWaterFragment;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class EditScheduleRuleActivity extends BaseScheduleRuleActivity implements Listener, ActivityThatSaves {
    private static final String TAG = EditScheduleRuleActivity.class.getSimpleName();
    private static Path nameSelectPath = FragmentNavigationController.createSingleScreenPath("select_name", RuleNameFragment.class, R.string.schedulename);
    private static Path selectDurationsPath = FragmentNavigationController.createSingleScreenPath("select_durations", DurationsFragment.class, R.string.zonedurations);
    private static Path zoneSelectPath = FragmentNavigationController.createSingleScreenPath("select_zone", ZoneSelectFragment.class, R.string.zones);
    private FetchAndCopyScheduleRuleCommand fetchAndCopyScheduleRuleCommand;
    private ScheduleRuleAndCopy scheduleRuleAndCopy;

    protected void onCreate(Bundle savedInstanceState) {
        Screen rootScreen = new Screen("ruledetails", RuleDetailsFragment.class);
        rootScreen.registerBranches(new int[]{115, 100, 114, 101, 117, 105, 106, 107, 116, 103}, new Path[]{nameSelectPath, zoneSelectPath, selectDurationsPath, selectIntervalScreen, HowOftenToWaterFragment.PATH, StartTimeFragment.PATH, startDateSelectPath, endDateSelectPath, WeatherIntelligenceFragment.PATH, selectWeekdays});
        this.rootPath.registerScreen(rootScreen);
        super.onCreate(savedInstanceState);
        this.controller.setListener(new FragmentNavigationController.Listener() {
            public final void onFragmentComing$3993877b(BaseFragment fragment) {
                EditScheduleRuleActivity.access$000(EditScheduleRuleActivity.this, fragment);
            }

            public final void onFragmentGoing(BaseFragment fragment) {
                if (EditScheduleRuleActivity.this.scheduleRuleAndCopy != null) {
                    BaseScheduleRuleFragment scheduleRuleFragment = (BaseScheduleRuleFragment) fragment;
                    if (scheduleRuleFragment.validate()) {
                        scheduleRuleFragment.commitState(EditScheduleRuleActivity.this.scheduleRuleAndCopy.copy);
                        EditScheduleRuleActivity.access$200(EditScheduleRuleActivity.this, EditScheduleRuleActivity.this.scheduleRuleAndCopy.copy);
                    }
                }
            }

            public final void onAllPathsComplete() {
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchAndCopyScheduleRuleCommand == null) {
            this.fetchAndCopyScheduleRuleCommand = new FetchAndCopyScheduleRuleCommand(getIntent().getStringExtra("ruleid"), this);
            this.fetchAndCopyScheduleRuleCommand.execute();
        }
    }

    public final void onDeviceLoaded(Device device) {
    }

    public final void onScheduleRuleAndCopyLoaded(ScheduleRuleAndCopy scheduleRuleAndCopy) {
        this.fetchAndCopyScheduleRuleCommand = null;
        if (scheduleRuleAndCopy != null) {
            this.scheduleRuleAndCopy = scheduleRuleAndCopy;
            ((BaseScheduleRuleFragment) this.controller.getCurrentFragment()).updateState(scheduleRuleAndCopy.copy);
            return;
        }
        finish();
    }

    public final void runRule() {
        runRule(this.scheduleRuleAndCopy.copy, this.prefsWrapper.getLoggedInUserId());
    }

    public final void deleteRule() {
        deleteRule(this.scheduleRuleAndCopy.copy, true);
    }

    public final void save() {
        ScheduleRule scheduleRule = this.scheduleRuleAndCopy.copy;
        new AnonymousClass5(this, this).execute(new ScheduleRule[]{scheduleRule});
    }

    public final boolean hasChanges() {
        ((BaseScheduleRuleFragment) this.controller.getCurrentFragment()).commitState(this.scheduleRuleAndCopy.copy);
        return ModelObject.deepCompare(this.scheduleRuleAndCopy.original, this.scheduleRuleAndCopy.copy) != 0;
    }

    public final boolean isBusy() {
        return this.fetchAndCopyScheduleRuleCommand != null;
    }

    public final boolean isValid() {
        return true;
    }

    public final void changeRuleType(FrequencyOperator type) {
        super.changeRuleType(this.scheduleRuleAndCopy.copy, type);
    }

    static /* synthetic */ void access$000(EditScheduleRuleActivity x0, BaseFragment x1) {
        if (x0.scheduleRuleAndCopy != null) {
            ((BaseScheduleRuleFragment) x1).updateState(x0.scheduleRuleAndCopy.copy);
        }
    }

    static /* synthetic */ void access$200(EditScheduleRuleActivity x0, ScheduleRule x1) {
        ArrayList arrayList = new ArrayList();
        final TreeMap treeMap = new TreeMap();
        Iterator it = x0.scheduleRuleAndCopy.copy.zones.iterator();
        while (it.hasNext()) {
            ZoneInfo zoneInfo = (ZoneInfo) it.next();
            if (zoneInfo.fetchDuration) {
                arrayList.add(zoneInfo);
                treeMap.put(zoneInfo.zoneId, zoneInfo);
            }
        }
        if (arrayList.size() > 0) {
            Log.d(TAG, "need to get durations for " + arrayList.size());
            ScheduleRule createDefaultRule = ScheduleRuleWizardActivity.createDefaultRule();
            createDefaultRule.device = x0.scheduleRuleAndCopy.copy.device;
            BaseScheduleRuleActivity.justChangeRuleType(createDefaultRule, x0.scheduleRuleAndCopy.copy.getFrequencyOperator());
            createDefaultRule.setStartDate(x0.scheduleRuleAndCopy.copy.getStartDate());
            createDefaultRule.setEndDateJson(x0.scheduleRuleAndCopy.copy.endDate);
            createDefaultRule.zones = arrayList;
            RestClientProgressDialogAsyncTask anonymousClass2 = new RestClientProgressDialogAsyncTask<PreviewScheduleRule, Void, PreviewScheduleRule>(x0, "Computing Watering Times...") {
                protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                    return ScheduleRuleWizardActivity.previewRule(this.holder.restClient, ((PreviewScheduleRule[]) objArr)[0]);
                }

                public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                    Iterator it = ((PreviewScheduleRule) errorResponse).zones.iterator();
                    while (it.hasNext()) {
                        ZoneInfo zoneInfo = (ZoneInfo) it.next();
                        ZoneInfo zoneInfo2 = (ZoneInfo) treeMap.get(zoneInfo.zoneId);
                        zoneInfo2.duration = zoneInfo.duration;
                        zoneInfo2.fetchDuration = false;
                    }
                    BaseScheduleRuleFragment.updateTotalDuration(EditScheduleRuleActivity.this.scheduleRuleAndCopy.copy);
                    BaseFragment baseFragment = (BaseFragment) EditScheduleRuleActivity.this.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                    if (baseFragment != null) {
                        EditScheduleRuleActivity.access$000(EditScheduleRuleActivity.this, baseFragment);
                    }
                }

                public final void onFailure(ErrorResponse error) {
                }
            };
            anonymousClass2.setProgressDialogClass(RingsProgressDialog.class);
            anonymousClass2.execute(new PreviewScheduleRule[]{createDefaultRule});
        }
    }
}
