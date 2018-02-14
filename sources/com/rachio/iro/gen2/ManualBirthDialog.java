package com.rachio.iro.gen2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.widget.EditText;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.gen2.model.BirthDeviceGeneration2;
import com.rachio.iro.model.user.User;
import com.rachio.iro.utils.LocationUtils;
import com.rachio.iro.utils.ProgressDialogAsyncTask;

public class ManualBirthDialog {

    /* renamed from: com.rachio.iro.gen2.ManualBirthDialog$1 */
    static class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Context val$context;
        final /* synthetic */ EditText val$mac;
        final /* synthetic */ RestClient val$restClient;
        final /* synthetic */ EditText val$serial;
        final /* synthetic */ User val$user;

        public final void onClick(DialogInterface dialog, int which) {
            final String serialString = this.val$serial.getEditableText().toString();
            final String macString = this.val$mac.getEditableText().toString();
            if (!TextUtils.isEmpty(serialString) && !TextUtils.isEmpty(macString)) {
                new ProgressDialogAsyncTask<Void, Void, Void>(this.val$context) {
                    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                        double[] location = LocationUtils.getLocation(AnonymousClass1.this.val$context, false);
                        AnonymousClass1.this.val$restClient.birthGeneration2(serialString, new BirthDeviceGeneration2("iro", macString, "92867", AnonymousClass1.this.val$user.id, AnonymousClass1.this.val$user.externalPlanId, location[0], location[1]), new HttpResponseErrorHandler());
                        return null;
                    }
                }.execute(null);
            }
        }
    }
}
