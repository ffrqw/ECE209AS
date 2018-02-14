package com.electricimp.blinkup;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import com.rachio.iro.R;

public class WPSActivity extends PreBlinkUpActivity {
    private String apiKey;
    private String siteids;
    private String token;
    private EditText wpsPinView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.__bu_wps);
        init();
        this.wpsPinView = (EditText) findViewById(R.id.__bu_wps_pin_optional);
        BlinkupController.setHint(this.wpsPinView, null, R.string.__bu_wps_pin);
        this.wpsPinView.setTypeface(Typeface.DEFAULT);
        BlinkupController.setText((TextView) findViewById(R.id.__bu_wps_info), null, R.string.__bu_wps_info);
        Bundle bundle = getIntent().getExtras();
        this.token = bundle.getString("token");
        this.siteids = bundle.getString("siteid");
        this.apiKey = bundle.getString("apiKey");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == 4 && this.blinkup.intentBlinkupComplete != null) {
            setResult(-1);
            finish();
        }
    }

    protected final Intent createSendBlinkupIntent() {
        String pin = this.wpsPinView.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("mode", "wps");
        intent.putExtra("pin", pin);
        intent.putExtra("token", this.token);
        intent.putExtra("siteid", this.siteids);
        intent.putExtra("apiKey", this.apiKey);
        return intent;
    }

    protected final void addCreateTokenIntentFields(Intent data) {
        data.putExtra("pin", this.wpsPinView.getText().toString());
    }
}
