package com.rachio.iro.async.command;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.utils.UiThreadExecutor;
import dagger.MembersInjector;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class BaseCommand_MembersInjector<T> implements MembersInjector<BaseCommand<T>> {
    static final /* synthetic */ boolean $assertionsDisabled = (!BaseCommand_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;
    private final Provider<UiThreadExecutor> uiThreadExecutorProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        BaseCommand baseCommand = (BaseCommand) obj;
        if (baseCommand == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        baseCommand.backgroundExecutor = (Executor) this.backgroundExecutorProvider.get();
        baseCommand.uiThreadExecutor = (UiThreadExecutor) this.uiThreadExecutorProvider.get();
        baseCommand.database = (Database) this.databaseProvider.get();
        baseCommand.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
        baseCommand.restClient = (RestClient) this.restClientProvider.get();
    }
}
