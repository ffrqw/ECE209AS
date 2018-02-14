package com.rachio.iro.async.command;

import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.device.Gen2SerialPrefix;
import com.rachio.iro.utils.CrashReporterUtils;

public class FetchUnbornDataCommand extends BaseCommand<Gen2SerialPrefix> {
    private static final String TAG = FetchUnbornDataCommand.class.getSimpleName();
    private final Listener listener;
    private final String mac;

    public interface Listener {
        void onUnbornDataLoaded(Gen2SerialPrefix gen2SerialPrefix);
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        this.listener.onUnbornDataLoaded((Gen2SerialPrefix) obj);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        for (int i = 0; i < 3; i++) {
            HttpResponseErrorHandler httpResponseErrorHandler = new HttpResponseErrorHandler();
            Gen2SerialPrefix gen2SerialPrefix = (Gen2SerialPrefix) this.restClient.getObjectById(null, this.mac.replaceAll(":", ""), Gen2SerialPrefix.class, httpResponseErrorHandler);
            if (gen2SerialPrefix != null && !httpResponseErrorHandler.hasError) {
                return gen2SerialPrefix;
            }
            CrashReporterUtils.logDebug(TAG, "trying again");
        }
        return null;
    }

    public FetchUnbornDataCommand(Listener listener, String mac) {
        this.listener = listener;
        this.mac = mac;
        BaseCommand.component(BaseCommand.toContext(listener)).inject(this);
    }
}
