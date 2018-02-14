package io.fabric.sdk.android.services.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import io.fabric.sdk.android.Fabric;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

final class AdvertisingInfoServiceStrategy implements AdvertisingInfoStrategy {
    private final Context context;

    private static final class AdvertisingConnection implements ServiceConnection {
        private final LinkedBlockingQueue<IBinder> queue;
        private boolean retrieved;

        private AdvertisingConnection() {
            this.retrieved = false;
            this.queue = new LinkedBlockingQueue(1);
        }

        public final void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (InterruptedException e) {
            }
        }

        public final void onServiceDisconnected(ComponentName name) {
            this.queue.clear();
        }

        public final IBinder getBinder() {
            if (this.retrieved) {
                Fabric.getLogger().e("Fabric", "getBinder already called");
            }
            this.retrieved = true;
            try {
                return (IBinder) this.queue.poll(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return null;
            }
        }
    }

    private static final class AdvertisingInterface implements IInterface {
        private final IBinder binder;

        public AdvertisingInterface(IBinder binder) {
            this.binder = binder;
        }

        public final IBinder asBinder() {
            return this.binder;
        }

        public final String getId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id = null;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                this.binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } catch (Exception e) {
                Fabric.getLogger().d("Fabric", "Could not get parcel from Google Play Service to capture AdvertisingId");
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean isLimitAdTrackingEnabled() throws android.os.RemoteException {
            /*
            r8 = this;
            r3 = 1;
            r4 = 0;
            r0 = android.os.Parcel.obtain();
            r2 = android.os.Parcel.obtain();
            r1 = 0;
            r5 = "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService";
            r0.writeInterfaceToken(r5);	 Catch:{ Exception -> 0x002e }
            r5 = 1;
            r0.writeInt(r5);	 Catch:{ Exception -> 0x002e }
            r5 = r8.binder;	 Catch:{ Exception -> 0x002e }
            r6 = 2;
            r7 = 0;
            r5.transact(r6, r0, r2, r7);	 Catch:{ Exception -> 0x002e }
            r2.readException();	 Catch:{ Exception -> 0x002e }
            r5 = r2.readInt();	 Catch:{ Exception -> 0x002e }
            if (r5 == 0) goto L_0x002c;
        L_0x0024:
            r1 = r3;
        L_0x0025:
            r2.recycle();
            r0.recycle();
        L_0x002b:
            return r1;
        L_0x002c:
            r1 = r4;
            goto L_0x0025;
        L_0x002e:
            r3 = move-exception;
            r3 = io.fabric.sdk.android.Fabric.getLogger();	 Catch:{ all -> 0x0041 }
            r4 = "Fabric";
            r5 = "Could not get parcel from Google Play Service to capture Advertising limitAdTracking";
            r3.d(r4, r5);	 Catch:{ all -> 0x0041 }
            r2.recycle();
            r0.recycle();
            goto L_0x002b;
        L_0x0041:
            r3 = move-exception;
            r2.recycle();
            r0.recycle();
            throw r3;
            */
            throw new UnsupportedOperationException("Method not decompiled: io.fabric.sdk.android.services.common.AdvertisingInfoServiceStrategy.AdvertisingInterface.isLimitAdTrackingEnabled():boolean");
        }
    }

    public AdvertisingInfoServiceStrategy(Context context) {
        this.context = context.getApplicationContext();
    }

    public final AdvertisingInfo getAdvertisingInfo() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Fabric.getLogger().d("Fabric", "AdvertisingInfoServiceStrategy cannot be called on the main thread");
            return null;
        }
        try {
            this.context.getPackageManager().getPackageInfo("com.android.vending", 0);
            AdvertisingConnection connection = new AdvertisingConnection();
            Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
            intent.setPackage("com.google.android.gms");
            try {
                if (this.context.bindService(intent, connection, 1)) {
                    AdvertisingInterface adInterface = new AdvertisingInterface(connection.getBinder());
                    AdvertisingInfo advertisingInfo = new AdvertisingInfo(adInterface.getId(), adInterface.isLimitAdTrackingEnabled());
                    this.context.unbindService(connection);
                    return advertisingInfo;
                }
                Fabric.getLogger().d("Fabric", "Could not bind to Google Play Service to capture AdvertisingId");
                return null;
            } catch (Exception e) {
                Fabric.getLogger().w("Fabric", "Exception in binding to Google Play Service to capture AdvertisingId", e);
                this.context.unbindService(connection);
                return null;
            } catch (Throwable t) {
                Fabric.getLogger().d("Fabric", "Could not bind to Google Play Service to capture AdvertisingId", t);
                return null;
            }
        } catch (NameNotFoundException e2) {
            Fabric.getLogger().d("Fabric", "Unable to find Google Play Services package name");
            return null;
        } catch (Exception e3) {
            Fabric.getLogger().d("Fabric", "Unable to determine if Google Play Services is available", e3);
            return null;
        }
    }
}
