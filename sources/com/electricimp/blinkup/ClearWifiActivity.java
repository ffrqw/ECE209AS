package com.electricimp.blinkup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.rachio.iro.R;

public class ClearWifiActivity extends PreBlinkUpActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.__bu_clear_wifi);
        init();
        BlinkupController.setText((TextView) findViewById(R.id.__bu_blinkup_desc), null, R.string.__bu_blinkup_desc);
        BlinkupController.setText((TextView) findViewById(R.id.__bu_clear_wifi_header), null, R.string.__bu_clear_device_settings);
        BlinkupController.setText(this.blinkupButton, null, R.string.__bu_clear_wireless);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            setResult(resultCode, data);
            finish();
        }
    }

    public final Intent createSendBlinkupIntent() {
        Intent intent = new Intent();
        intent.putExtra("mode", "clear");
        return intent;
    }
}
