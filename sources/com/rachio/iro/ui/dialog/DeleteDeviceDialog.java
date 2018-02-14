package com.rachio.iro.ui.dialog;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.rachio.iro.IroApplication;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.R;
import com.rachio.iro.cloud.PushPull;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.apionly.BaseResponse;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.DashboardActivity;
import com.rachio.iro.ui.activity.device.DeviceSettingsActivity;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.StringUtils;

public class DeleteDeviceDialog {
    public static void show(final User user, final DeviceSettingsActivity activity, final Device device) {
        View content = LayoutInflater.from(activity).inflate(R.layout.dialog_delete_device, null);
        final CheckBox check = (CheckBox) content.findViewById(R.id.deletedialog_check);
        AlertDialog d = new Builder(activity).setTitle(String.format("Are your sure your want to remove the device named \"%s\"?", new Object[]{device.name})).setView(content).setNegativeButton(activity.getString(17039360), null).setPositiveButton(activity.getString(17039370), null).create();
        d.show();
        final Button ok = d.getButton(-1);
        ok.setEnabled(false);
        check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public final void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ok.setEnabled(isChecked);
            }
        });
        ok.setOnClickListener(new OnClickListener() {
            public final void onClick(View v) {
                if (check.isChecked()) {
                    new RestClientProgressDialogAsyncTask<String, Void, BaseResponse>(activity) {
                        protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                            if (StringUtils.equals(this.holder.prefsWrapper.getSelectedDeviceId(), ((String[]) objArr)[0])) {
                                this.holder.prefsWrapper.setSelectedDeviceId(null);
                            }
                            this.holder.database.lock();
                            HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                            this.holder.restClient.deleteObjectById(Device.class, device.id, httpResponseErrorHandler);
                            if (!httpResponseErrorHandler.hasError) {
                                user.removeDevice(this.holder.database, device);
                            }
                            PushPull.pullEntityAndSave(this.holder.database, this.holder.restClient, User.class, user.id);
                            this.holder.database.unlock();
                            if (httpResponseErrorHandler.hasError) {
                                return null;
                            }
                            return new BaseResponse();
                        }

                        public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                            PrefsWrapper prefsWrapper = IroApplication.get(activity).getPrefsWrapper();
                            DashboardActivity.goToDashboard(activity, prefsWrapper, user.id, user.haveDevices(), user.hasReadOnlyRole(), null, user.getSelectedDeviceId(prefsWrapper), true, true);
                        }

                        public final void onFailure(ErrorResponse errorResponse) {
                        }
                    }.execute(new String[]{device.id});
                }
            }
        });
        content.requestLayout();
    }
}
