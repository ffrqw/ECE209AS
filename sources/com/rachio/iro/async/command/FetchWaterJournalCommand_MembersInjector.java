package com.rachio.iro.async.command;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.utils.UiThreadExecutor;
import dagger.MembersInjector;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class FetchWaterJournalCommand_MembersInjector implements MembersInjector<FetchWaterJournalCommand> {
    static final /* synthetic */ boolean $assertionsDisabled = (!FetchWaterJournalCommand_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;
    private final Provider<UiThreadExecutor> uiThreadExecutorProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        FetchWaterJournalCommand fetchWaterJournalCommand = (FetchWaterJournalCommand) obj;
        if (fetchWaterJournalCommand == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        fetchWaterJournalCommand.backgroundExecutor = (Executor) this.backgroundExecutorProvider.get();
        fetchWaterJournalCommand.uiThreadExecutor = (UiThreadExecutor) this.uiThreadExecutorProvider.get();
        fetchWaterJournalCommand.database = (Database) this.databaseProvider.get();
        fetchWaterJournalCommand.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
        fetchWaterJournalCommand.restClient = (RestClient) this.restClientProvider.get();
    }

    private FetchWaterJournalCommand_MembersInjector(Provider<Executor> backgroundExecutorProvider, Provider<UiThreadExecutor> uiThreadExecutorProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
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

    public static MembersInjector<FetchWaterJournalCommand> create(Provider<Executor> backgroundExecutorProvider, Provider<UiThreadExecutor> uiThreadExecutorProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        return new FetchWaterJournalCommand_MembersInjector(backgroundExecutorProvider, uiThreadExecutorProvider, databaseProvider, prefsWrapperProvider, restClientProvider);
    }
}
