package com.electricimp.blinkup;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.electricimp.blinkup.BlinkupController.TokenAcquireCallback;
import com.rachio.iro.R;

public abstract class PreBlinkUpActivity extends Activity {
    protected BlinkupController blinkup;
    protected Button blinkupButton;
    protected CheckBox legacyModeCheckBox;

    protected abstract Intent createSendBlinkupIntent();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.blinkup = BlinkupController.getInstance();
    }

    protected void onResume() {
        super.onResume();
        this.blinkupButton.setEnabled(true);
    }

    protected final void init() {
        this.blinkupButton = (Button) findViewById(R.id.__bu_blinkup_button);
        this.legacyModeCheckBox = (CheckBox) findViewById(R.id.__bu_legacy_mode_checkbox);
        BlinkupController.setText(this.legacyModeCheckBox, null, R.string.__bu_legacy_mode);
        TextView blinkupDesc = (TextView) findViewById(R.id.__bu_blinkup_desc);
        if (blinkupDesc != null) {
            BlinkupController.setText(blinkupDesc, null, R.string.__bu_blinkup_desc);
        }
        BlinkupController.setText(this.blinkupButton, null, R.string.__bu_send_blinkup);
    }

    protected void addCreateTokenIntentFields(Intent data) {
    }

    public void sendBlinkup(View view) {
        this.blinkupButton.setEnabled(false);
        Intent intent = createSendBlinkupIntent();
        if (getIntent().getBooleanExtra("tokenCreate", false)) {
            String mode = intent.getStringExtra("mode");
            final Intent data = new Intent();
            data.putExtra("mode", mode);
            if (this.legacyModeCheckBox.isChecked()) {
                data.putExtra("slow", true);
            }
            if ("clear".equals(mode)) {
                setResult(-1, data);
                finish();
                return;
            }
            TokenAcquireCallback tokenAcquireCallback = new TokenAcquireCallback() {
                public final void onSuccess(String planID, String setupToken) {
                    data.putExtra("token", setupToken);
                    PreBlinkUpActivity.this.addCreateTokenIntentFields(data);
                    PreBlinkUpActivity.this.setResult(-1, data);
                    PreBlinkUpActivity.this.finish();
                }

                public final void onError(String errorMsg) {
                    Intent error = new Intent();
                    error.putExtra("error", errorMsg);
                    PreBlinkUpActivity.this.setResult(1, error);
                    PreBlinkUpActivity.this.finish();
                }
            };
            this.blinkup.acquireSetupToken(this, getIntent().getStringExtra("apiKey"), tokenAcquireCallback);
            return;
        }
        if (this.legacyModeCheckBox.isChecked()) {
            intent.putExtra("slow", true);
        }
        this.blinkup.addBlinkupIntentFields(this, intent);
        startActivityForResult(intent, 4);
    }

    public void legacyModeLink(View v) {
        showDialog(1000);
    }

    protected Dialog onCreateDialog(int id) {
        Builder builder = new Builder(this);
        if (id == 1000) {
            builder.setTitle(BlinkupController.getCustomStringOrDefault(this, null, R.string.__bu_legacy_mode));
            builder.setMessage(BlinkupController.getCustomStringOrDefault(this, null, R.string.__bu_legacy_mode_desc));
            builder.setNeutralButton(BlinkupController.getCustomStringOrDefault(this, null, R.string.__bu_ok), new OnClickListener() {
                public final void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        return builder.create();
    }
}
