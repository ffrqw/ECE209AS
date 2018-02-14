package com.electricimp.blinkup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.rachio.iro.R;

public class InterstitialActivity extends Activity {
    private View blinkupButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.__bu_interstitial);
        this.blinkupButton = findViewById(R.id.__bu_blinkup_button);
        BlinkupController.getInstance();
        ((ImageView) findViewById(R.id.__bu_interstitial)).setImageResource(0);
    }

    protected void onResume() {
        super.onResume();
        this.blinkupButton.setEnabled(true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            setResult(-1);
            finish();
        }
    }

    public void sendBlinkup(View view) {
        this.blinkupButton.setEnabled(false);
        Intent intent = new Intent(this, BlinkupGLActivity.class);
        intent.replaceExtras(getIntent().getExtras());
        startActivityForResult(intent, 4);
    }
}
