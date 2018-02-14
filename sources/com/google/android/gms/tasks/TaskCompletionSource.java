package com.google.android.gms.tasks;

public final class TaskCompletionSource<TResult> {
    private final zzn<TResult> zzbMe = new zzn();

    public final void setException(Exception exception) {
        this.zzbMe.setException(exception);
    }

    public final void setResult(TResult tResult) {
        this.zzbMe.setResult(null);
    }

    public final boolean trySetException(Exception exception) {
        return this.zzbMe.trySetException(exception);
    }
}
