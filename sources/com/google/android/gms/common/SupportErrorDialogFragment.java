package com.google.android.gms.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import com.google.android.gms.common.internal.zzbo;

public final class SupportErrorDialogFragment extends DialogFragment {
    private Dialog mDialog = null;
    private OnCancelListener zzazZ = null;

    public static SupportErrorDialogFragment newInstance(Dialog dialog, OnCancelListener onCancelListener) {
        SupportErrorDialogFragment supportErrorDialogFragment = new SupportErrorDialogFragment();
        Dialog dialog2 = (Dialog) zzbo.zzb((Object) dialog, (Object) "Cannot display null dialog");
        dialog2.setOnCancelListener(null);
        dialog2.setOnDismissListener(null);
        supportErrorDialogFragment.mDialog = dialog2;
        if (onCancelListener != null) {
            supportErrorDialogFragment.zzazZ = onCancelListener;
        }
        return supportErrorDialogFragment;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        if (this.zzazZ != null) {
            this.zzazZ.onCancel(dialogInterface);
        }
    }

    public final Dialog onCreateDialog$5709b310() {
        if (this.mDialog == null) {
            setShowsDialog(false);
        }
        return this.mDialog;
    }

    public final void show(FragmentManager fragmentManager, String str) {
        super.show(fragmentManager, str);
    }
}
