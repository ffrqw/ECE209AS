package com.rachio.iro.ui.activity.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.user.LoginActivity;

public class NoDeviceActivity extends BaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_no_device);
        ((Button) findViewById(R.id.blinkup_no_device_add_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NoDeviceActivity.this.startActivity(new Intent(NoDeviceActivity.this, AddDeviceTypeActivity.class));
            }
        });
        ((TextView) findViewById(R.id.blinkup_no_device_sign_out_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                User.logout(NoDeviceActivity.this, NoDeviceActivity.this.database, NoDeviceActivity.this.prefsWrapper, IroApplication.get(NoDeviceActivity.this).getRestClient());
                NoDeviceActivity.this.finish();
                NoDeviceActivity.this.startActivity(new Intent(NoDeviceActivity.this, LoginActivity.class));
            }
        });
    }
}
