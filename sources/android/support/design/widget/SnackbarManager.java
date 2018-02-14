package android.support.design.widget;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

final class SnackbarManager {
    private static SnackbarManager sSnackbarManager;
    private SnackbarRecord mCurrentSnackbar;
    private final Handler mHandler = new Handler(Looper.getMainLooper(), new android.os.Handler.Callback() {
        public final boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    SnackbarManager.this.handleTimeout((SnackbarRecord) message.obj);
                    return true;
                default:
                    return false;
            }
        }
    });
    private final Object mLock = new Object();

    interface Callback {
    }

    private static class SnackbarRecord {
        final WeakReference<Callback> callback;
        boolean paused;
    }

    static SnackbarManager getInstance() {
        if (sSnackbarManager == null) {
            sSnackbarManager = new SnackbarManager();
        }
        return sSnackbarManager;
    }

    private SnackbarManager() {
    }

    public final void dismiss(Callback callback, int event) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                cancelSnackbarLocked(this.mCurrentSnackbar, event);
            }
        }
    }

    public final void onDismissed(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                this.mCurrentSnackbar = null;
            }
        }
    }

    public final void onShown(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                scheduleTimeoutLocked(this.mCurrentSnackbar);
            }
        }
    }

    public final void pauseTimeout(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback) && !this.mCurrentSnackbar.paused) {
                this.mCurrentSnackbar.paused = true;
                this.mHandler.removeCallbacksAndMessages(this.mCurrentSnackbar);
            }
        }
    }

    public final void restoreTimeoutIfPaused(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback) && this.mCurrentSnackbar.paused) {
                this.mCurrentSnackbar.paused = false;
                scheduleTimeoutLocked(this.mCurrentSnackbar);
            }
        }
    }

    public final boolean isCurrentOrNext(Callback callback) {
        boolean z;
        synchronized (this.mLock) {
            z = isCurrentSnackbarLocked(callback);
        }
        return z;
    }

    private boolean cancelSnackbarLocked(SnackbarRecord record, int event) {
        if (((Callback) record.callback.get()) == null) {
            return false;
        }
        this.mHandler.removeCallbacksAndMessages(record);
        return true;
    }

    private boolean isCurrentSnackbarLocked(Callback callback) {
        if (this.mCurrentSnackbar != null) {
            boolean z;
            SnackbarRecord snackbarRecord = this.mCurrentSnackbar;
            if (callback == null || snackbarRecord.callback.get() != callback) {
                z = false;
            } else {
                z = true;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    private void scheduleTimeoutLocked(SnackbarRecord r) {
        this.mHandler.removeCallbacksAndMessages(r);
        this.mHandler.sendMessageDelayed(Message.obtain(this.mHandler, 0, r), 2750);
    }

    final void handleTimeout(SnackbarRecord record) {
        synchronized (this.mLock) {
            if (this.mCurrentSnackbar == record || record == null) {
                cancelSnackbarLocked(record, 2);
            }
        }
    }
}
