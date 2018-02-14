package com.rachio.iro.ui.activity.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.rachio.iro.R;
import com.rachio.iro.gen2.ProvActivity;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.BlinkUpActivity;
import com.rachio.iro.ui.activity.HelpActivity;

public class AddDeviceTypeActivity extends BaseActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_adddevicetype);
        ImageView help = (ImageView) findViewById(R.id.gen2prov_help);
        ImageView exit = (ImageView) findViewById(R.id.gen2prov_exit);
        View gen2 = findViewById(R.id.adddevicetype_gen2);
        View gen1 = findViewById(R.id.adddevicetype_gen1);
        gen2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AddDeviceTypeActivity.this.startActivity(new Intent(AddDeviceTypeActivity.this, ProvActivity.class));
            }
        });
        gen1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AddDeviceTypeActivity.this.startActivity(new Intent(AddDeviceTypeActivity.this, BlinkUpActivity.class));
            }
        });
        help.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(AddDeviceTypeActivity.this, HelpActivity.class);
                i.putExtra("article", "112-registering-iro");
                AddDeviceTypeActivity.this.startActivity(i);
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AddDeviceTypeActivity.this.finish();
            }
        });
    }

    public static final void start(Context context) {
        context.startActivity(new Intent(context, AddDeviceTypeActivity.class));
    }
}
