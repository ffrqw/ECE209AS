package com.rachio.iro.async.command;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.utils.UiThreadExecutor;
import dagger.MembersInjector;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class FetchAndCopyScheduleRuleCommand_MembersInjector implements MembersInjector<FetchAndCopyScheduleRuleCommand> {
    static final /* synthetic */ boolean $assertionsDisabled = (!FetchAndCopyScheduleRuleCommand_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;
    private final Provider<UiThreadExecutor> uiThreadExecutorProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        FetchAndCopyScheduleRuleCommand fetchAndCopyScheduleRuleCommand = (FetchAndCopyScheduleRuleCommand) obj;
        if (fetchAndCopyScheduleRuleCommand == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        fetchAndCopyScheduleRuleCommand.backgroundExecutor = (Executor) this.backgroundExecutorProvider.get();
        fetchAndCopyScheduleRuleCommand.uiThreadExecutor = (UiThreadExecutor) this.uiThreadExecutorProvider.get();
        fetchAndCopyScheduleRuleCommand.database = (Database) this.databaseProvider.get();
        fetchAndCopyScheduleRuleCommand.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
        fetchAndCopyScheduleRuleCommand.restClient = (RestClient) this.restClientProvider.get();
    }

    private FetchAndCopyScheduleRuleCommand_MembersInjector(Provider<Executor> backgroundExecutorProvider, Provider<UiThreadExecutor> uiThreadExecutorProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
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

    public static MembersInjector<FetchAndCopyScheduleRuleCommand> create(Provider<Executor> backgroundExecutorProvider, Provider<UiThreadExecutor> uiThreadExecutorProvider, Provider<Database> databaseProvider, Provider<PrefsWrapper> prefsWrapperProvider, Provider<RestClient> restClientProvider) {
        return new FetchAndCopyScheduleRuleCommand_MembersInjector(backgroundExecutorProvider, uiThreadExecutorProvider, databaseProvider, prefsWrapperProvider, restClientProvider);
    }
}
