package com.rachio.iro.cloud;

import com.rachio.iro.R;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.apionly.BaseResponse;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.apionly.RainDelayRequest;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import java.util.Calendar;

public class PutRainDelayAsyncTask extends RestClientProgressDialogAsyncTask<Void, Void, BaseResponse> {
    private final String deviceId;
    private final int seconds;

    public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
    }

    public PutRainDelayAsyncTask(BaseActivity activity, String deviceId, int seconds) {
        super(activity, activity.getResources().getString(R.string.updatingraindelay));
        this.deviceId = deviceId;
        this.seconds = seconds;
    }

    public final void onFailure(ErrorResponse errorResponse) {
    }

    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
        this.holder.database.lock();
        HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
        this.holder.restClient.putObject(BaseResponse.class, new RainDelayRequest(this.deviceId, (long) this.seconds), httpResponseErrorHandler);
        if (!httpResponseErrorHandler.hasError) {
            Device device = (Device) this.holder.database.find(Device.class, this.deviceId);
            Calendar instance = Calendar.getInstance();
            instance.add(13, this.seconds);
            device.rainDelayExpirationDate = instance.getTime();
            this.holder.database.save(device, true);
        }
        this.holder.database.unlock();
        return httpResponseErrorHandler.hasError ? null : new BaseResponse();
    }
}
