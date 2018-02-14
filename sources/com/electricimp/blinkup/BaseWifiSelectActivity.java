package com.electricimp.blinkup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class BaseWifiSelectActivity extends Activity {
    protected String apiKey;
    protected List<NetworkItem> networkListStrings;
    protected String planID;
    protected String preferenceFile;
    protected ArrayList<String> savedNetworks;
    protected String setupToken;
    protected boolean tokenCreate;

    protected static class NetworkItem {
        public String label;
        public Type type;

        public enum Type {
            NETWORK,
            CHANGE_NETWORK,
            CONNECT_USING_WPS,
            CLEAR
        }

        public NetworkItem(Type type, String label) {
            this.type = type;
            this.label = label;
        }

        public final String toString() {
            return this.label;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        this.savedNetworks = new ArrayList();
        this.setupToken = bundle.getString("setupToken");
        this.planID = bundle.getString("planID");
        this.apiKey = bundle.getString("apiKey");
        this.preferenceFile = bundle.getString("preferenceFile");
        this.tokenCreate = bundle.getBoolean("tokenCreate");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            BlinkupController blinkup = BlinkupController.getInstance();
            if ((requestCode == 1 || requestCode == 2) && blinkup.intentBlinkupComplete != null) {
                setResult(-1);
                finish();
            }
        }
        setResult(resultCode, data);
        finish();
    }

    protected final void addFooter(ListView networkListView, int stringResId, int paddingResId) {
        if (this.apiKey == null && getSharedPreferences("preferences", 0).getString("eimpapp:username", null) != null) {
            TextView footer = new TextView(this);
            footer.setText(getString(stringResId, new Object[]{username}));
            footer.setGravity(1);
            footer.setPadding(0, getResources().getDimensionPixelSize(paddingResId), 0, 0);
            networkListView.addFooterView(footer, null, false);
        }
    }

    protected final void sendWirelessConfiguration(String ssid) {
        Intent intent = new Intent();
        intent.putExtra("token", this.setupToken);
        if (ssid != null) {
            intent.putExtra("ssid", ssid);
        }
        intent.putExtra("siteid", this.planID);
        intent.putExtra("apiKey", this.apiKey);
        intent.putStringArrayListExtra("savedNetworks", this.savedNetworks);
        intent.putExtra("preferenceFile", this.preferenceFile);
        intent.putExtra("tokenCreate", this.tokenCreate);
        intent.setClassName(this, "com.electricimp.blinkup.WifiActivity");
        startActivityForResult(intent, 1);
    }

    protected final void connectUsingWPS() {
        Intent intent = new Intent();
        intent.putExtra("token", this.setupToken);
        intent.putExtra("siteid", this.planID);
        intent.putExtra("apiKey", this.apiKey);
        intent.putExtra("tokenCreate", this.tokenCreate);
        intent.setClassName(this, "com.electricimp.blinkup.WPSActivity");
        startActivityForResult(intent, 2);
    }

    protected final void clearWirelessConfiguration() {
        Intent intent = new Intent();
        intent.setClassName(this, "com.electricimp.blinkup.ClearWifiActivity");
        intent.putExtra("siteid", this.planID);
        intent.putExtra("apiKey", this.apiKey);
        intent.putExtra("tokenCreate", this.tokenCreate);
        startActivityForResult(intent, 3);
    }
}
