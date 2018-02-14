package android.support.v4.app;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader.OnLoadCanceledListener;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;

/* compiled from: LoaderManager */
final class LoaderManagerImpl extends LoaderManager {
    static boolean DEBUG = false;
    FragmentHostCallback mHost;
    final SparseArrayCompat<LoaderInfo> mInactiveLoaders = new SparseArrayCompat();
    final SparseArrayCompat<LoaderInfo> mLoaders = new SparseArrayCompat();
    boolean mRetaining;
    boolean mStarted;
    final String mWho;

    /* compiled from: LoaderManager */
    final class LoaderInfo implements OnLoadCanceledListener<Object>, OnLoadCompleteListener<Object> {
        final Bundle mArgs;
        LoaderCallbacks<Object> mCallbacks;
        Object mData;
        boolean mDeliveredData;
        boolean mDestroyed;
        boolean mHaveData;
        final int mId;
        boolean mListenerRegistered;
        Loader<Object> mLoader;
        boolean mReportNextStart;
        boolean mRetaining;
        boolean mRetainingStarted;
        boolean mStarted;
        final /* synthetic */ LoaderManagerImpl this$0;

        final void start() {
            if (this.mRetaining && this.mRetainingStarted) {
                this.mStarted = true;
            } else if (!this.mStarted) {
                this.mStarted = true;
                if (this.mLoader == null) {
                    return;
                }
                if (!this.mLoader.getClass().isMemberClass() || Modifier.isStatic(this.mLoader.getClass().getModifiers())) {
                    if (!this.mListenerRegistered) {
                        this.mLoader.registerListener(this.mId, this);
                        this.mLoader.registerOnLoadCanceledListener(this);
                        this.mListenerRegistered = true;
                    }
                    this.mLoader.startLoading();
                    return;
                }
                throw new IllegalArgumentException("Object returned from onCreateLoader must not be a non-static inner member class: " + this.mLoader);
            }
        }

        final void stop() {
            this.mStarted = false;
            if (!this.mRetaining && this.mLoader != null && this.mListenerRegistered) {
                this.mListenerRegistered = false;
                this.mLoader.unregisterListener(this);
                this.mLoader.unregisterOnLoadCanceledListener(this);
                this.mLoader.stopLoading();
            }
        }

        final void destroy() {
            this.mDestroyed = true;
            this.mDeliveredData = false;
            this.mCallbacks = null;
            this.mData = null;
            this.mHaveData = false;
            if (this.mLoader != null) {
                if (this.mListenerRegistered) {
                    this.mListenerRegistered = false;
                    this.mLoader.unregisterListener(this);
                    this.mLoader.unregisterOnLoadCanceledListener(this);
                }
                this.mLoader.reset();
            }
        }

        public final void onLoadCanceled$5dda1f52() {
            if (!this.mDestroyed && this.this$0.mLoaders.get(this.mId) != this) {
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void onLoadComplete(android.support.v4.content.Loader<java.lang.Object> r4, java.lang.Object r5) {
            /*
            r3 = this;
            r1 = r3.mDestroyed;
            if (r1 == 0) goto L_0x0005;
        L_0x0004:
            return;
        L_0x0005:
            r1 = r3.this$0;
            r1 = r1.mLoaders;
            r2 = r3.mId;
            r1 = r1.get(r2);
            if (r1 != r3) goto L_0x0004;
        L_0x0011:
            r1 = r3.mData;
            if (r1 != r5) goto L_0x0019;
        L_0x0015:
            r1 = r3.mHaveData;
            if (r1 != 0) goto L_0x0022;
        L_0x0019:
            r3.mData = r5;
            r1 = 1;
            r3.mHaveData = r1;
            r1 = r3.mStarted;
            if (r1 == 0) goto L_0x0022;
        L_0x0022:
            r1 = r3.this$0;
            r1 = r1.mInactiveLoaders;
            r2 = r3.mId;
            r0 = r1.get(r2);
            r0 = (android.support.v4.app.LoaderManagerImpl.LoaderInfo) r0;
            if (r0 == 0) goto L_0x0041;
        L_0x0030:
            if (r0 == r3) goto L_0x0041;
        L_0x0032:
            r1 = 0;
            r0.mDeliveredData = r1;
            r0.destroy();
            r1 = r3.this$0;
            r1 = r1.mInactiveLoaders;
            r2 = r3.mId;
            r1.remove(r2);
        L_0x0041:
            r1 = r3.this$0;
            r1 = r1.mHost;
            if (r1 == 0) goto L_0x0004;
        L_0x0047:
            r1 = r3.this$0;
            r1 = r1.hasRunningLoaders();
            if (r1 != 0) goto L_0x0004;
        L_0x004f:
            r1 = r3.this$0;
            r1 = r1.mHost;
            r1 = r1.mFragmentManager;
            r1.startPendingDeferredFragments();
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.LoaderManagerImpl.LoaderInfo.onLoadComplete(android.support.v4.content.Loader, java.lang.Object):void");
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append("LoaderInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" #");
            sb.append(this.mId);
            sb.append(" : ");
            DebugUtils.buildShortClassTag(this.mLoader, sb);
            sb.append("}}");
            return sb.toString();
        }

        public final void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            writer.print(prefix);
            writer.print("mId=");
            writer.print(this.mId);
            writer.print(" mArgs=");
            writer.println(this.mArgs);
            writer.print(prefix);
            writer.print("mCallbacks=");
            writer.println(null);
            writer.print(prefix);
            writer.print("mLoader=");
            writer.println(this.mLoader);
            if (this.mLoader != null) {
                this.mLoader.dump(prefix + "  ", fd, writer, args);
            }
            if (this.mHaveData || this.mDeliveredData) {
                writer.print(prefix);
                writer.print("mHaveData=");
                writer.print(this.mHaveData);
                writer.print("  mDeliveredData=");
                writer.println(this.mDeliveredData);
                writer.print(prefix);
                writer.print("mData=");
                writer.println(this.mData);
            }
            writer.print(prefix);
            writer.print("mStarted=");
            writer.print(this.mStarted);
            writer.print(" mReportNextStart=");
            writer.print(this.mReportNextStart);
            writer.print(" mDestroyed=");
            writer.println(this.mDestroyed);
            writer.print(prefix);
            writer.print("mRetaining=");
            writer.print(this.mRetaining);
            writer.print(" mRetainingStarted=");
            writer.print(this.mRetainingStarted);
            writer.print(" mListenerRegistered=");
            writer.println(this.mListenerRegistered);
        }
    }

    LoaderManagerImpl(String who, FragmentHostCallback host, boolean started) {
        this.mWho = who;
        this.mHost = host;
        this.mStarted = started;
    }

    final void doStart() {
        if (this.mStarted) {
            RuntimeException e = new RuntimeException("here");
            e.fillInStackTrace();
            Log.w("LoaderManager", "Called doStart when already started: " + this, e);
            return;
        }
        this.mStarted = true;
        for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
            ((LoaderInfo) this.mLoaders.valueAt(i)).start();
        }
    }

    final void doStop() {
        if (this.mStarted) {
            for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
                ((LoaderInfo) this.mLoaders.valueAt(i)).stop();
            }
            this.mStarted = false;
            return;
        }
        RuntimeException e = new RuntimeException("here");
        e.fillInStackTrace();
        Log.w("LoaderManager", "Called doStop when not started: " + this, e);
    }

    final void doRetain() {
        if (this.mStarted) {
            this.mRetaining = true;
            this.mStarted = false;
            for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
                LoaderInfo loaderInfo = (LoaderInfo) this.mLoaders.valueAt(i);
                loaderInfo.mRetaining = true;
                loaderInfo.mRetainingStarted = loaderInfo.mStarted;
                loaderInfo.mStarted = false;
                loaderInfo.mCallbacks = null;
            }
            return;
        }
        RuntimeException e = new RuntimeException("here");
        e.fillInStackTrace();
        Log.w("LoaderManager", "Called doRetain when not started: " + this, e);
    }

    final void doReportNextStart() {
        for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
            ((LoaderInfo) this.mLoaders.valueAt(i)).mReportNextStart = true;
        }
    }

    final void doReportStart() {
        for (int i = this.mLoaders.size() - 1; i >= 0; i--) {
            LoaderInfo loaderInfo = (LoaderInfo) this.mLoaders.valueAt(i);
            if (loaderInfo.mStarted && loaderInfo.mReportNextStart) {
                loaderInfo.mReportNextStart = false;
                if (loaderInfo.mHaveData && !loaderInfo.mRetaining) {
                    Loader loader = loaderInfo.mLoader;
                    Object obj = loaderInfo.mData;
                }
            }
        }
    }

    final void doDestroy() {
        int i;
        if (!this.mRetaining) {
            for (i = this.mLoaders.size() - 1; i >= 0; i--) {
                ((LoaderInfo) this.mLoaders.valueAt(i)).destroy();
            }
            this.mLoaders.clear();
        }
        for (i = this.mInactiveLoaders.size() - 1; i >= 0; i--) {
            ((LoaderInfo) this.mInactiveLoaders.valueAt(i)).destroy();
        }
        this.mInactiveLoaders.clear();
        this.mHost = null;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("LoaderManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        DebugUtils.buildShortClassTag(this.mHost, sb);
        sb.append("}}");
        return sb.toString();
    }

    public final void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        String innerPrefix;
        int i;
        if (this.mLoaders.size() > 0) {
            writer.print(prefix);
            writer.println("Active Loaders:");
            innerPrefix = prefix + "    ";
            for (i = 0; i < this.mLoaders.size(); i++) {
                LoaderInfo li = (LoaderInfo) this.mLoaders.valueAt(i);
                writer.print(prefix);
                writer.print("  #");
                writer.print(this.mLoaders.keyAt(i));
                writer.print(": ");
                writer.println(li.toString());
                li.dump(innerPrefix, fd, writer, args);
            }
        }
        if (this.mInactiveLoaders.size() > 0) {
            writer.print(prefix);
            writer.println("Inactive Loaders:");
            innerPrefix = prefix + "    ";
            for (i = 0; i < this.mInactiveLoaders.size(); i++) {
                li = (LoaderInfo) this.mInactiveLoaders.valueAt(i);
                writer.print(prefix);
                writer.print("  #");
                writer.print(this.mInactiveLoaders.keyAt(i));
                writer.print(": ");
                writer.println(li.toString());
                li.dump(innerPrefix, fd, writer, args);
            }
        }
    }

    public final boolean hasRunningLoaders() {
        boolean loadersRunning = false;
        int count = this.mLoaders.size();
        for (int i = 0; i < count; i++) {
            LoaderInfo li = (LoaderInfo) this.mLoaders.valueAt(i);
            int i2 = (!li.mStarted || li.mDeliveredData) ? 0 : 1;
            loadersRunning |= i2;
        }
        return loadersRunning;
    }
}
