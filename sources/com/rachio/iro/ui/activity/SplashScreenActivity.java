package com.rachio.iro.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.user.BaseLoginActivity;
import com.rachio.iro.ui.activity.user.LoginActivity;
import com.rachio.iro.ui.activity.user.LoginProgressActivity;

public class SplashScreenActivity extends BaseLoginActivity {
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            Intent i;
            if (SplashScreenActivity.this.prefsWrapper.isUserLoggedIn()) {
                i = new Intent(SplashScreenActivity.this, LoginProgressActivity.class);
                SplashScreenActivity.this.addExtrasToIntent(i);
                SplashScreenActivity.this.startActivity(i);
                SplashScreenActivity.this.overridePendingTransition(0, 0);
            } else {
                i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                SplashScreenActivity.this.addExtrasToIntent(i);
                SplashScreenActivity.this.startActivity(i);
            }
            SplashScreenActivity.this.finish();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_splash);
    }

    protected void onResume() {
        super.onResume();
        this.handler.postDelayed(this.runnable, 1000);
    }

    protected void onStop() {
        super.onStop();
        this.handler.removeCallbacks(this.runnable);
    }
}
