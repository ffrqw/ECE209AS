package com.rachio.iro.ui.newschedulerulepath.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog.Builder;
import com.rachio.iro.R;
import com.rachio.iro.cloud.PushPull;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.ResponseCacheItem;
import com.rachio.iro.model.apionly.BaseResponse;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.apionly.RunFlexScheduleRuleRequest;
import com.rachio.iro.model.apionly.RunScheduleRuleRequest;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.schedule.ScheduleCalendar;
import com.rachio.iro.model.schedule.ScheduleRule;
import com.rachio.iro.model.schedule.ScheduleRule.FlexScheduleRule;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.DashboardActivity;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;

public class BaseBaseScheduleRuleActivity extends BaseActivity {

    /* renamed from: com.rachio.iro.ui.newschedulerulepath.activity.BaseBaseScheduleRuleActivity$5 */
    static class AnonymousClass5 extends RestClientProgressDialogAsyncTask<ScheduleRule, Void, ScheduleRule> {
        final /* synthetic */ BaseActivity val$activity;

        AnonymousClass5(BaseActivity activity, BaseActivity baseActivity) {
            this.val$activity = baseActivity;
            super(activity);
        }

        protected final /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
            ScheduleRule[] scheduleRuleArr = (ScheduleRule[]) objArr;
            this.holder.database.lock();
            ModelObject modelObject = scheduleRuleArr[0];
            modelObject.lastUpdateDate = null;
            ScheduleRule scheduleRule = (ScheduleRule) this.holder.restClient.putObject(ScheduleRule.class, modelObject.isFlex() ? FlexScheduleRule.class : ScheduleRule.class, modelObject, this.errorHandler);
            if (!(this.errorHandler.hasError || scheduleRule == null)) {
                scheduleRule.device = modelObject.device;
                BaseBaseScheduleRuleActivity.invalidateCalendar(this.holder.database, scheduleRule.device.id);
                this.holder.database.save(scheduleRule);
            }
            this.holder.database.unlock();
            return scheduleRule;
        }

        public final void onFailure(ErrorResponse errorResponse) {
        }

        public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
            this.val$activity.finish();
        }
    }

    public static void invalidateCalendar(Database database, String deviceId) {
        ResponseCacheItem.invalidate(database, deviceId, ScheduleCalendar.class);
    }

    public final void runRule(final ScheduleRule sr, final String userId) {
        new RestClientProgressDialogAsyncTask<String, Void, BaseResponse>(this) {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                ModelObject runFlexScheduleRuleRequest;
                String[] strArr = (String[]) objArr;
                this.holder.database.lock();
                String str = strArr[0];
                String str2 = strArr[1];
                if (sr.isFlex()) {
                    runFlexScheduleRuleRequest = new RunFlexScheduleRuleRequest(str2);
                } else {
                    runFlexScheduleRuleRequest = new RunScheduleRuleRequest(str2);
                }
                Object obj = (BaseResponse) this.holder.restClient.putObject(BaseResponse.class, runFlexScheduleRuleRequest, this.errorHandler);
                if (!this.errorHandler.hasError) {
                    PushPull.updateEntityAndSave(this.holder.database, this.holder.restClient, Device.class, (Device) this.holder.database.find(Device.class, str));
                    if (obj == null) {
                        obj = new BaseResponse();
                    }
                }
                this.holder.database.unlock();
                return obj;
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                DashboardActivity.goToDashboard(BaseBaseScheduleRuleActivity.this, BaseBaseScheduleRuleActivity.this.prefsWrapper, userId, BaseBaseScheduleRuleActivity.this.prefsWrapper.welcomeShown(), false, "Activity", null, false, true);
            }

            public final void onFailure(ErrorResponse errorResponse) {
            }
        }.execute(new String[]{sr.device.id, sr.id});
    }

    public final void deleteRule(final ScheduleRule rule, final boolean finishOnSuccess) {
        new Builder(this).setTitle("Are you sure?").setPositiveButton((int) R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new RestClientProgressDialogAsyncTask<ScheduleRule, Void, BaseResponse>(BaseBaseScheduleRuleActivity.this, finishOnSuccess, BaseBaseScheduleRuleActivity.this) {
                    protected final /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                        int i;
                        ScheduleRule[] scheduleRuleArr = (ScheduleRule[]) objArr;
                        this.holder.database.lock();
                        ScheduleRule scheduleRule = scheduleRuleArr[0];
                        HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                        this.holder.restClient.deleteObjectById(scheduleRule.isFlex() ? FlexScheduleRule.class : ScheduleRule.class, scheduleRule.id, httpResponseErrorHandler);
                        if (httpResponseErrorHandler.hasError) {
                            i = 0;
                        } else {
                            this.holder.database.deleteById(ScheduleRule.class, scheduleRule.id);
                            i = 1;
                        }
                        if (i != 0) {
                            BaseBaseScheduleRuleActivity.invalidateCalendar(this.holder.database, scheduleRule.device.id);
                        }
                        this.holder.database.unlock();
                        if (httpResponseErrorHandler.hasError) {
                            return null;
                        }
                        return new BaseResponse();
                    }

                    public final void onFailure(ErrorResponse errorResponse) {
                    }

                    public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                        if (x2) {
                            x0.finish();
                        }
                    }
                }.execute(new ScheduleRule[]{rule});
            }
        }).setNegativeButton((int) R.string.no, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
}
