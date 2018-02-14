package com.rachio.iro.ui.activity.zone;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.ui.activity.ActivityThatSaves;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.newschedulerulepath.activity.BaseBaseScheduleRuleActivity;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask.Callback;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public abstract class ActivityThatSavesZone extends BaseActivity implements ActivityThatSaves {
    public final RestClientProgressDialogAsyncTask<Zone, Void, Zone> saveZone(Zone zone, final Bitmap newZoneImage, final Callback callback) {
        RestClientProgressDialogAsyncTask<Zone, Void, Zone> task = new RestClientProgressDialogAsyncTask<Zone, Void, Zone>(this) {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                ModelObject modelObject = ((Zone[]) objArr)[0];
                HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
                ActivityThatSavesZone.this.database.lock();
                Zone zone = (Zone) this.holder.restClient.putObject(Zone.class, modelObject, httpResponseErrorHandler);
                if (!(zone == null || newZoneImage == null)) {
                    HttpResponseErrorHandler httpResponseErrorHandler2 = new HttpResponseErrorHandler();
                    OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    Bitmap bitmap = newZoneImage;
                    float width = (float) newZoneImage.getWidth();
                    float height = (float) newZoneImage.getHeight();
                    if (width > 600.0f || height > 600.0f) {
                        float max = 600.0f / Math.max(width, height);
                        bitmap = Bitmap.createScaledBitmap(newZoneImage, (int) (width * max), (int) (max * height), true);
                    }
                    bitmap.compress(CompressFormat.JPEG, 70, byteArrayOutputStream);
                    zone.imageUrl = this.holder.restClient.addZoneImage(modelObject.id, byteArrayOutputStream.toByteArray(), httpResponseErrorHandler2);
                }
                if (!(httpResponseErrorHandler.hasError || zone == null || zone.hasError())) {
                    BaseBaseScheduleRuleActivity.invalidateCalendar(ActivityThatSavesZone.this.database, modelObject.device.id);
                    zone.device = modelObject.device;
                    ActivityThatSavesZone.this.database.save(zone);
                }
                ActivityThatSavesZone.this.database.unlock();
                return zone;
            }

            public final void onFailure(ErrorResponse errorResponse) {
                if (!isCancelled() && callback != null) {
                    callback.onEither();
                }
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                if (!isCancelled() && callback != null) {
                    callback.onSuccess();
                    callback.onEither();
                }
            }
        };
        task.execute(new Zone[]{zone});
        return task;
    }
}
