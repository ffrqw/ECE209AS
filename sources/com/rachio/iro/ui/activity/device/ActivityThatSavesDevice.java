package com.rachio.iro.ui.activity.device;

import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.ResponseCacheItem;
import com.rachio.iro.model.apionly.DeviceResponse;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.ui.activity.ActivityThatSaves;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;

public abstract class ActivityThatSavesDevice extends BaseActivity implements ActivityThatSaves {
    protected final void saveDevice(Device device, boolean finishOnSuccess) {
        saveDevice(device, finishOnSuccess, null);
    }

    protected final void saveDevice(Device device, final boolean finishOnSuccess, final Class<?>[] invalidates) {
        new RestClientProgressDialogAsyncTask<Device, Void, DeviceResponse>(this) {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                int i = 0;
                ModelObject modelObject = ((Device[]) objArr)[0];
                ActivityThatSavesDevice.this.database.lock();
                DeviceResponse deviceResponse = (DeviceResponse) this.holder.restClient.putObject(DeviceResponse.class, modelObject, new HttpResponseErrorHandler());
                if (!(deviceResponse == null || deviceResponse.hasError())) {
                    if (invalidates != null) {
                        Class[] clsArr = invalidates;
                        int length = clsArr.length;
                        while (i < length) {
                            ResponseCacheItem.invalidate(ActivityThatSavesDevice.this.database, modelObject.id, clsArr[i]);
                            i++;
                        }
                    }
                    deviceResponse.user = modelObject.user;
                    deviceResponse.managerUser = modelObject.managerUser;
                    ActivityThatSavesDevice.this.database.save(deviceResponse, true);
                }
                ActivityThatSavesDevice.this.database.unlock();
                return deviceResponse;
            }

            public final void onFailure(ErrorResponse errorResponse) {
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                if (finishOnSuccess) {
                    ActivityThatSavesDevice.this.finish();
                }
            }
        }.execute(new Device[]{device});
    }
}
