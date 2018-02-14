package com.rachio.iro.async.command;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.utils.UiThreadExecutor;
import dagger.MembersInjector;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class FetchUnbornDataCommand_MembersInjector implements MembersInjector<FetchUnbornDataCommand> {
    static final /* synthetic */ boolean $assertionsDisabled = (!FetchUnbornDataCommand_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;
    private final Provider<UiThreadExecutor> uiThreadExecutorProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        FetchUnbornDataCommand fetchUnbornDataCommand = (FetchUnbornDataCommand) obj;
        if (fetchUnbornDataCommand == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        fetchUnbornDataCommand.backgroundExecutor = (Executor) this.backgroundExecutorProvider.get();
        fetchUnbornDataCommand.uiThreadExecutor = (UiThreadExecutor) this.uiThreadExecutorProvider.get();
        fetchUnbornDataCommand.database = (Database) this.databaseProvider.get();
        fetchUnbornDataCommand.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
        fetchUnbornDataCommand.restClient = (RestClient) this.restClientProvider.get();
    }

    private FetchUnbornDataCommand_MembersInjector(Provider<Executor> backgroundExecutorProvider, Provider<UiThreadExecutor> uiThreadExecutorProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        if ($assertionsDisabled || backgroundExecutorProvider != null) {
            this.backgroundExecutorProvider = backgroundExecutorProvider;
            if ($assertionsDisabled || uiThreadExecutorProvider != null) {
                this.uiThreadExecutorProvider = uiThreadExecutorProvider;
                if ($assertionsDisabled || databaseProvider != null) {
                    this.databaseProvider = databaseProvider;
                    if ($assertionsDisabled || prefsWrapperProvider != null) {
                        this.prefsWrapperProvider = prefsWrapperProvider;
                        if ($assertionsDisabled || restClientProvider != null) {
                            this.restClientProvider = restClientProvider;
                            return;
                        }
                        throw new AssertionError();
                    }
                    throw new AssertionError();
                }
                throw new AssertionError();
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public static MembersInjector<FetchUnbornDataCommand> create(Provider<Executor> backgroundExecutorProvider, Provider<UiThreadExecutor> uiThreadExecutorProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        return new FetchUnbornDataCommand_MembersInjector(backgroundExecutorProvider, uiThreadExecutorProvider, databaseProvider, prefsWrapperProvider, restClientProvider);
    }
}
