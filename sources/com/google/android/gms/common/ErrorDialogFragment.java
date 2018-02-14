package com.google.android.gms.common;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import com.google.android.gms.common.internal.zzbo;

public final class ErrorDialogFragment extends DialogFragment {
    private Dialog mDialog = null;
    private OnCancelListener zzazZ = null;

    public static ErrorDialogFragment newInstance(Dialog dialog, OnCancelListener onCancelListener) {
        ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
        Dialog dialog2 = (Dialog) zzbo.zzb((Object) dialog, (Object) "Cannot display null dialog");
        dialog2.setOnCancelListener(null);
        dialog2.setOnDismissListener(null);
        errorDialogFragment.mDialog = dialog2;
        if (onCancelListener != null) {
            errorDialogFragment.zzazZ = onCancelListener;
        }
        return errorDialogFragment;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        if (this.zzazZ != null) {
            this.zzazZ.onCancel(dialogInterface);
        }
    }

    public final Dialog onCreateDialog(Bundle bundle) {
        if (this.mDialog == null) {
            setShowsDialog(false);
        }
        return this.mDialog;
    }

    public final void show(FragmentManager fragmentManager, String str) {
        super.show(fragmentManager, str);
    }
}
