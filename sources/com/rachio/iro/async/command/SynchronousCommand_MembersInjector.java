package com.rachio.iro.async.command;

import com.rachio.iro.utils.UiThreadExecutor;
import dagger.MembersInjector;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class SynchronousCommand_MembersInjector<T> implements MembersInjector<SynchronousCommand<T>> {
    static final /* synthetic */ boolean $assertionsDisabled = (!SynchronousCommand_MembersInjector.class.desiredAssertionStatus());
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<UiThreadExecutor> uiThreadExecutorProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        SynchronousCommand synchronousCommand = (SynchronousCommand) obj;
        if (synchronousCommand == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        synchronousCommand.backgroundExecutor = (Executor) this.backgroundExecutorProvider.get();
        synchronousCommand.uiThreadExecutor = (UiThreadExecutor) this.uiThreadExecutorProvider.get();
    }
}
