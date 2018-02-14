package android.support.v4.app;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.RemoteInputCompatBase.RemoteInput;

@TargetApi(16)
final class RemoteInputCompatJellybean {
    static Bundle[] toBundleArray(RemoteInput[] remoteInputs) {
        if (remoteInputs == null) {
            return null;
        }
        Bundle[] bundles = new Bundle[remoteInputs.length];
        for (int i = 0; i < remoteInputs.length; i++) {
            RemoteInput remoteInput = remoteInputs[i];
            Bundle bundle = new Bundle();
            bundle.putString("resultKey", remoteInput.getResultKey());
            bundle.putCharSequence("label", remoteInput.getLabel());
            bundle.putCharSequenceArray("choices", remoteInput.getChoices());
            bundle.putBoolean("allowFreeFormInput", remoteInput.getAllowFreeFormInput());
            bundle.putBundle("extras", remoteInput.getExtras());
            bundles[i] = bundle;
        }
        return bundles;
    }
}
