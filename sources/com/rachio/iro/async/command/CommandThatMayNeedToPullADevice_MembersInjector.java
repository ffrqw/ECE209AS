package com.rachio.iro.async.command;

import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.utils.UiThreadExecutor;
import dagger.MembersInjector;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class CommandThatMayNeedToPullADevice_MembersInjector<T> implements MembersInjector<CommandThatMayNeedToPullADevice<T>> {
    static final /* synthetic */ boolean $assertionsDisabled = (!CommandThatMayNeedToPullADevice_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<Database> databaseProvider;
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;
    private final Provider<UiThreadExecutor> uiThreadExecutorProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        CommandThatMayNeedToPullADevice commandThatMayNeedToPullADevice = (CommandThatMayNeedToPullADevice) obj;
        if (commandThatMayNeedToPullADevice == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        commandThatMayNeedToPullADevice.backgroundExecutor = (Executor) this.backgroundExecutorProvider.get();
        commandThatMayNeedToPullADevice.uiThreadExecutor = (UiThreadExecutor) this.uiThreadExecutorProvider.get();
        commandThatMayNeedToPullADevice.database = (Database) this.databaseProvider.get();
        commandThatMayNeedToPullADevice.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
        commandThatMayNeedToPullADevice.restClient = (RestClient) this.restClientProvider.get();
    }
}
