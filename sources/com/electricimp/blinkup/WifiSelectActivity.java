package com.electricimp.blinkup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.electricimp.blinkup.BaseWifiSelectActivity.NetworkItem.Type;
import com.rachio.iro.R;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;

public class WifiSelectActivity extends BaseWifiSelectActivity {
    private ListView networkListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.__bu_wifi_select);
        BlinkupController.getInstance();
        this.networkListView = (ListView) findViewById(R.id.__bu_network_list);
        this.networkListView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.__bu_wifi_select_header, this.networkListView, false), null, false);
        BlinkupController.setText((TextView) findViewById(R.id.__bu_wifi_select_header), null, R.string.__bu_choose_wifi_network);
        addFooter(this.networkListView, R.string.__bu_logged_in_as, R.dimen.__bu_padding);
        this.networkListStrings = new ArrayList();
        this.networkListView.setAdapter(new ArrayAdapter(this, R.layout.__bu_network_list_item, this.networkListStrings));
        this.networkListView.setOnItemClickListener(new OnItemClickListener() {
            public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NetworkItem item = (NetworkItem) ((ListAdapter) parent.getAdapter()).getItem(position);
                if (item != null) {
                    switch (item.type) {
                        case CHANGE_NETWORK:
                            WifiSelectActivity.this.sendWirelessConfiguration(null);
                            return;
                        case CONNECT_USING_WPS:
                            WifiSelectActivity.this.connectUsingWPS();
                            return;
                        case CLEAR:
                            WifiSelectActivity.this.clearWirelessConfiguration();
                            return;
                        default:
                            WifiSelectActivity.this.sendWirelessConfiguration(item.label);
                            return;
                    }
                }
            }
        });
    }

    public void onResume() {
        String currentSSID = BlinkupController.getCurrentWifiSSID(this);
        this.savedNetworks.clear();
        try {
            JSONArray savedNetworksJSON = new JSONArray(getSharedPreferences(this.preferenceFile, 0).getString("eimp:savedNetworks", ""));
            for (int i = 0; i < savedNetworksJSON.length(); i++) {
                this.savedNetworks.add(savedNetworksJSON.getString(i));
            }
        } catch (JSONException e) {
            Log.v("BlinkUp", "Error parsing saved networks JSON string: " + e);
        }
        this.networkListStrings.clear();
        if (currentSSID != null) {
            this.networkListStrings.add(new NetworkItem(Type.NETWORK, currentSSID));
        }
        Iterator it = this.savedNetworks.iterator();
        while (it.hasNext()) {
            String s = (String) it.next();
            if (!s.equals(currentSSID)) {
                this.networkListStrings.add(new NetworkItem(Type.NETWORK, s));
            }
        }
        BlinkupController blinkup = BlinkupController.getInstance();
        this.networkListStrings.add(new NetworkItem(Type.CHANGE_NETWORK, getString(R.string.__bu_change_network)));
        this.networkListStrings.add(new NetworkItem(Type.CONNECT_USING_WPS, getString(R.string.__bu_connect_using_wps)));
        if (blinkup.showClearConfig) {
            this.networkListStrings.add(new NetworkItem(Type.CLEAR, getString(R.string.__bu_clear_device_settings)));
        }
        this.networkListView.invalidateViews();
        super.onResume();
    }
}
