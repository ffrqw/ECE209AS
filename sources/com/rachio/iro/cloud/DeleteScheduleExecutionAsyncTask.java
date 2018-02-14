package com.rachio.iro.cloud;

import android.util.Log;
import com.rachio.iro.model.apionly.BaseResponse;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.schedule.ScheduleExecution;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask.Callback;

public class DeleteScheduleExecutionAsyncTask extends RestClientProgressDialogAsyncTask<Void, Void, BaseResponse> {
    private static final String TAG = DeleteScheduleExecutionAsyncTask.class.getSimpleName();
    private final Callback callback;
    private final Device device;
    private boolean finished = false;

    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
        Object deleteObjectById;
        this.holder.database.lock();
        ScheduleExecution scheduleExecution = this.device.scheduleExecution;
        if (scheduleExecution != null) {
            Log.d(TAG, "deleting schedule execution " + scheduleExecution.id);
            deleteObjectById = this.holder.restClient.deleteObjectById(ScheduleExecution.class, this.device.scheduleExecution.id, this.errorHandler);
            if (!this.errorHandler.hasError) {
                this.device.scheduleExecution = null;
                this.holder.database.save(this.device, true, false, true);
                deleteObjectById = new BaseResponse();
            }
        } else {
            Log.d(TAG, "no schedule execution");
            deleteObjectById = new BaseResponse();
        }
        this.holder.database.unlock();
        return deleteObjectById;
    }

    public DeleteScheduleExecutionAsyncTask(BaseActivity activity, Device device, Callback callback) {
        super(activity);
        if (device == null) {
            throw new IllegalArgumentException("device cannot be null");
        }
        this.device = device;
        this.callback = callback;
    }

    private void onPostExecute(BaseResponse baseResponse) {
        super.onPostExecute((ErrorResponse) baseResponse);
        this.finished = true;
    }

    public final void onFailure(ErrorResponse errorResponse) {
        if (!isCancelled() && this.callback != null) {
            this.callback.onEither();
        }
    }

    public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
        if (!isCancelled() && this.callback != null) {
            this.callback.onSuccess();
            this.callback.onEither();
        }
    }
}
