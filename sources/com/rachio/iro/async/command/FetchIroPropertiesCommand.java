package com.rachio.iro.async.command;

import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.IroProperties;

public class FetchIroPropertiesCommand extends BaseCommand<IroProperties> {
    private final Listener listener;

    public interface Listener {
        void onPropertiesLoaded(IroProperties iroProperties);
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        this.listener.onPropertiesLoaded((IroProperties) obj);
    }

    public FetchIroPropertiesCommand(Listener listener) {
        this.listener = listener;
        BaseCommand.component(BaseCommand.toContext(listener)).inject(this);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        return (IroProperties) this.restClient.getObject(this.database, IroProperties.class, new HttpResponseErrorHandler());
    }
}
