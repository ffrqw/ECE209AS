package com.rachio.iro.utils;

import android.os.Build.VERSION;
import android.util.Log;
import com.rachio.iro.ui.activity.BaseActivity;
import java.util.ArrayList;

public class PermissionRequester {
    private static final String TAG = PermissionRequester.class.getCanonicalName();
    private final BaseActivity activity;
    private final Listener listener;
    private final String[] permissions;
    private boolean requested;
    private boolean requestingPermissions;

    public interface Listener {
        void onPermissionsDenied();

        void onPermissionsGranted();
    }

    public PermissionRequester(BaseActivity activity, String permission, Listener listener) {
        this(activity, new String[]{permission}, listener);
    }

    private PermissionRequester(BaseActivity activity, String[] permissions, Listener listener) {
        this.requestingPermissions = false;
        this.requested = false;
        this.activity = activity;
        this.permissions = permissions;
        this.listener = listener;
    }

    public final void requestPermissions() {
        if (!this.requested && !this.requestingPermissions) {
            this.requested = true;
            if (VERSION.SDK_INT >= 23) {
                ArrayList<String> neededPermissions = new ArrayList(this.permissions.length);
                ArrayList<String> neededRationale = new ArrayList(this.permissions.length);
                for (String p : this.permissions) {
                    if (this.activity.checkSelfPermission(p) != 0) {
                        Log.d(TAG, "need to request permission " + p);
                        neededPermissions.add(p);
                        if (this.activity.shouldShowRequestPermissionRationale(p)) {
                            neededRationale.add(p);
                        }
                    } else {
                        Log.d(TAG, "already have permission " + p);
                    }
                }
                if (neededPermissions.size() > 0) {
                    this.requestingPermissions = true;
                    this.activity.requestPermissions((String[]) neededPermissions.toArray(new String[neededPermissions.size()]), 1);
                    return;
                }
                this.listener.onPermissionsGranted();
                return;
            }
            this.listener.onPermissionsGranted();
        }
    }

    public final void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            boolean allGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                boolean z;
                String str = TAG;
                StringBuilder append = new StringBuilder("permission ").append(permissions[i]).append(":");
                if (grantResults[i] == 0) {
                    z = true;
                } else {
                    z = false;
                }
                Log.d(str, append.append(z).toString());
                if (grantResults[i] != 0) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                this.listener.onPermissionsGranted();
            } else {
                this.listener.onPermissionsDenied();
            }
            this.requestingPermissions = false;
        }
    }
}
