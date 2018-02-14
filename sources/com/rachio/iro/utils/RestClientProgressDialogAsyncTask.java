package com.rachio.iro.utils;

import com.rachio.iro.IroApplication;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity;

public abstract class RestClientProgressDialogAsyncTask<Params, Progress, Result extends ErrorResponse> extends ProgressDialogAsyncTask<Params, Progress, Result> {
    public static boolean READONLY = false;
    private final BaseActivity activity;
    protected HttpResponseErrorHandler errorHandler;
    protected final Holder holder;

    public interface Callback {
        void onEither();

        void onSuccess();
    }

    public static class SimpleCallback implements Callback {
        public void onSuccess() {
        }

        public void onEither() {
        }
    }

    public static final class Holder {
        public Database database;
        public PrefsWrapper prefsWrapper;
        public RestClient restClient;
    }

    public abstract void onFailure(ErrorResponse errorResponse);

    public abstract void onSuccess(Result result);

    public RestClientProgressDialogAsyncTask(BaseActivity activity) {
        this(activity, null);
    }

    public RestClientProgressDialogAsyncTask(BaseActivity activity, String message) {
        super(activity, message);
        this.errorHandler = new HttpResponseErrorHandler();
        this.activity = activity;
        this.holder = new Holder();
        IroApplication.get(activity).component().inject(this.holder);
    }

    protected void onPostExecute(Result result) {
        if (!this.activity.isFinishing() && !isCancelled()) {
            super.onPostExecute(result);
            boolean failed = true;
            if (result == null) {
                this.activity.toastGenericError();
            } else if (result.hasError()) {
                Object obj;
                if (READONLY && result.getCode() == 101) {
                    obj = null;
                } else {
                    obj = 1;
                }
                if (obj != null) {
                    this.activity.toastError(result);
                }
            } else {
                failed = false;
                onSuccess(result);
            }
            if (failed) {
                onFailure(result);
            }
        }
    }
}
