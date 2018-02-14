package com.rachio.iro.ui.newschedulerulepath.dialog;

import android.app.Dialog;
import android.content.Context;
import com.rachio.iro.R;
import com.rachio.iro.ui.newschedulerulepath.views.RingsView;
import com.rachio.iro.ui.newschedulerulepath.views.RingsView.Listener;
import com.rachio.iro.utils.ProgressDialogAsyncTask.CustomProgressDialog;

public class RingsProgressDialog extends Dialog implements CustomProgressDialog {
    private OnCompleteCallback listener;
    private RingsView rings = ((RingsView) findViewById(R.id.rings));

    public interface OnCompleteCallback {
        void onComplete();
    }

    public RingsProgressDialog(Context context) {
        super(context);
        getWindow().setDimAmount(0.9f);
        getWindow().setBackgroundDrawable(context.getResources().getDrawable(17170445));
        requestWindowFeature(1);
        setContentView(R.layout.dialog_ringsprogress);
        this.rings.setListener(new Listener() {
            public final void onCompleteAnimationComplete() {
                RingsProgressDialog.this.dismiss();
                if (RingsProgressDialog.this.listener != null) {
                    RingsProgressDialog.this.listener.onComplete();
                }
            }
        });
        setCancelable(false);
    }

    public final void onComplete(OnCompleteCallback listener) {
        this.listener = listener;
        this.rings.setComplete(true);
    }

    public void setMessage(CharSequence message) {
    }
}
