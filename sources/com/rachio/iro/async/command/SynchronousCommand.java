package com.rachio.iro.async.command;

import com.rachio.iro.utils.UiThreadExecutor;
import java.util.concurrent.Executor;

public abstract class SynchronousCommand<T> implements Command {
    Executor backgroundExecutor;
    public boolean isCancelled;
    UiThreadExecutor uiThreadExecutor;

    protected abstract void handleResult(T t);

    protected abstract T loadResult();

    public final void execute() {
        this.backgroundExecutor.execute(new Runnable() {
            public void run() {
                final T result = SynchronousCommand.this.loadResult();
                SynchronousCommand.this.uiThreadExecutor.execute(new Runnable() {
                    public void run() {
                        if (!SynchronousCommand.this.isCancelled) {
                            SynchronousCommand.this.handleResult(result);
                        }
                    }
                });
            }
        });
    }
}
