package io.fabric.sdk.android;

import io.fabric.sdk.android.services.common.TimingMetric;
import io.fabric.sdk.android.services.concurrency.Priority;
import io.fabric.sdk.android.services.concurrency.PriorityAsyncTask;
import io.fabric.sdk.android.services.concurrency.UnmetDependencyException;

final class InitializationTask<Result> extends PriorityAsyncTask<Void, Void, Result> {
    final Kit<Result> kit;

    public InitializationTask(Kit<Result> kit) {
        this.kit = kit;
    }

    protected final void onPreExecute() {
        super.onPreExecute();
        TimingMetric timingMetric = createAndStartTimingMetric("onPreExecute");
        try {
            boolean result = this.kit.onPreExecute();
            timingMetric.stopMeasuring();
            if (!result) {
                cancel(true);
            }
        } catch (UnmetDependencyException ex) {
            throw ex;
        } catch (Exception ex2) {
            Fabric.getLogger().e("Fabric", "Failure onPreExecute()", ex2);
            timingMetric.stopMeasuring();
            cancel(true);
        } catch (Throwable th) {
            timingMetric.stopMeasuring();
            cancel(true);
        }
    }

    protected final void onPostExecute(Result result) {
        this.kit.initializationCallback.success$5d527811();
    }

    protected final void onCancelled(Result result) {
        this.kit.initializationCallback.failure(new InitializationException(this.kit.getIdentifier() + " Initialization was cancelled"));
    }

    public final int getPriority$16699175() {
        return Priority.HIGH$4601d4ec;
    }

    private TimingMetric createAndStartTimingMetric(String event) {
        TimingMetric timingMetric = new TimingMetric(this.kit.getIdentifier() + "." + event, "KitInitialization");
        timingMetric.startMeasuring();
        return timingMetric;
    }

    protected final /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
        TimingMetric createAndStartTimingMetric = createAndStartTimingMetric("doInBackground");
        Object obj = null;
        if (!isCancelled()) {
            obj = this.kit.doInBackground();
        }
        createAndStartTimingMetric.stopMeasuring();
        return obj;
    }
}
