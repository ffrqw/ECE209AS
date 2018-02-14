package com.rachio.iro.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.rachio.iro.IroApplication;
import com.rachio.iro.R;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.newschedulerulepath.dialog.RingsProgressDialog;
import com.rachio.iro.ui.prodevicelist.activity.ProDeviceListActivity;
import com.rachio.iro.ui.welcome.WelcomeActivity;

public class DebugActivity extends BaseActivity {
    private Handler handler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IroApplication.get(this).component().inject(this);
        setContentView((int) R.layout.activity_debug);
        ((Button) findViewById(R.id.ringsprogressdialog)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                final RingsProgressDialog rpd = new RingsProgressDialog(DebugActivity.this);
                rpd.show();
                DebugActivity.this.handler.postDelayed(new Runnable() {
                    public void run() {
                        rpd.onComplete(null);
                    }
                }, 15000);
            }
        });
        ((Button) findViewById(R.id.newuserwelcome)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(DebugActivity.this, WelcomeActivity.class);
                i.putExtra("USERID", DebugActivity.this.prefsWrapper.getLoggedInUserId());
                DebugActivity.this.startActivity(i);
            }
        });
        ((Button) findViewById(R.id.newintent)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                User user = User.getLoggedInUser(DebugActivity.this.database, DebugActivity.this.prefsWrapper);
                DashboardActivity.goToDashboard(DebugActivity.this, DebugActivity.this.prefsWrapper, DebugActivity.this.prefsWrapper.getLoggedInUserId(), user.haveDevices(), user.hasReadOnlyRole(), null, null, false, true);
            }
        });
        final EditText override = (EditText) findViewById(R.id.prodevicelist_deviceoverride);
        ((Button) findViewById(R.id.prodevicelist)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int numDevices = -1;
                String overrideString = override.getEditableText().toString();
                if (!TextUtils.isEmpty(overrideString)) {
                    numDevices = Integer.parseInt(overrideString);
                }
                ProDeviceListActivity.start(DebugActivity.this, numDevices);
            }
        });
    }
}
