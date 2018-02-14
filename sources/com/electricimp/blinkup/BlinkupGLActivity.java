package com.electricimp.blinkup;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import com.rachio.iro.R;

public class BlinkupGLActivity extends BaseBlinkupGLActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BlinkupController.getInstance();
        BlinkupController.setText(this.countdownDescView, null, R.string.__bu_countdown_desc);
        startBlinkup();
    }

    protected Dialog onCreateDialog(int id) {
        if (id != 0) {
            return null;
        }
        BlinkupController.getInstance();
        Builder builder = new Builder(this);
        builder.setTitle(BlinkupController.getCustomStringOrDefault(this, null, R.string.__bu_low_frame_rate_title));
        builder.setMessage(BlinkupController.getCustomStringOrDefault(this, null, R.string.__bu_low_frame_rate_desc));
        builder.setCancelable(false);
        builder.setPositiveButton(BlinkupController.getCustomStringOrDefault(this, null, R.string.__bu_low_frame_rate_go_to_settings), new OnClickListener() {
            public final void onClick(DialogInterface dialog, int which) {
                BlinkupGLActivity.this.startActivity(new Intent("android.settings.SETTINGS"));
                BlinkupGLActivity.this.finish();
            }
        });
        builder.setNegativeButton(BlinkupController.getCustomStringOrDefault(this, null, R.string.__bu_low_frame_rate_proceed_anyway), new OnClickListener() {
            public final void onClick(DialogInterface dialog, int which) {
                BlinkupGLActivity.this.updateCountdown();
                dialog.cancel();
            }
        });
        return builder.create();
    }
}
