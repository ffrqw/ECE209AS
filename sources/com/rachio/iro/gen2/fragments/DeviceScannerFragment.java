package com.rachio.iro.gen2.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;
import com.rachio.iro.gen2.FoundIroView;
import com.rachio.iro.gen2.model.DiscoveredIro;
import com.rachio.iro.ui.activity.HelpActivity;
import com.rachio.iro.utils.CrashReporterUtils;
import java.util.ArrayList;
import java.util.List;

public class DeviceScannerFragment extends BaseProvisioningFragment {
    private TextView availableDevicesHeader;
    private ListView deviceList;
    private ArrayList<DiscoveredIro> devices = new ArrayList();
    private LinearLayout noDeviceFound;
    private LinearLayout searching;

    /* renamed from: com.rachio.iro.gen2.fragments.DeviceScannerFragment$1 */
    class AnonymousClass1 extends ArrayAdapter<DiscoveredIro> {
        AnonymousClass1(Context x0, int x1, List x2) {
            super(x0, 0, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new FoundIroView(getContext());
            }
            ((FoundIroView) convertView).set(1, ((DiscoveredIro) getItem(position)).ssid);
            return convertView;
        }
    }

    public static DeviceScannerFragment newInstance(boolean updating) {
        DeviceScannerFragment fragment = new DeviceScannerFragment();
        Bundle args = new Bundle();
        args.putBoolean("updating", updating);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_devicescanner, container, false);
        this.availableDevicesHeader = (TextView) view.findViewById(R.id.gen2prov_availabledevicesheader);
        FrameLayout empty = (FrameLayout) view.findViewById(R.id.gen2prov_empty);
        TextView searchingText = (TextView) view.findViewById(R.id.gen2prov_searchingtext);
        this.searching = (LinearLayout) view.findViewById(R.id.gen2prov_searching);
        this.noDeviceFound = (LinearLayout) view.findViewById(R.id.gen2prov_nodevicesfound);
        Button tryAgain = (Button) view.findViewById(R.id.gen2prov_tryagain);
        TextView troubleFindingDevice = (TextView) view.findViewById(R.id.gen2prov_troublefindingdevice);
        Bundle args = getArguments();
        if (args != null && args.getBoolean("updating")) {
            searchingText.setText("Searching for your iro...");
        }
        this.deviceList = (ListView) view.findViewById(R.id.gen2prov_devices);
        this.deviceList.setEmptyView(empty);
        ArrayAdapter<DiscoveredIro> adapter = new AnonymousClass1(getContext(), 0, this.devices);
        this.deviceList.setAdapter(adapter);
        this.deviceList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceScannerFragment.this.onDeviceSelected((DiscoveredIro) parent.getItemAtPosition(position));
            }
        });
        tryAgain.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CrashReporterUtils.silentException(new Exception("user clicked retry in device scanner"));
                DeviceScannerFragment.this.searching.setVisibility(0);
                DeviceScannerFragment.this.noDeviceFound.setVisibility(8);
                DeviceScannerFragment.this.startScanningForDevices();
            }
        });
        troubleFindingDevice.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Context context = DeviceScannerFragment.this.getContext();
                Intent i = new Intent(context, HelpActivity.class);
                i.putExtra("article", "447-not-seeing-your-controller");
                context.startActivity(i);
            }
        });
        if (savedInstanceState != null) {
            this.devices.addAll((ArrayList) savedInstanceState.getSerializable("devices"));
            adapter.notifyDataSetChanged();
        }
        wireUpHelpAndExit(view);
        updateState();
        return view;
    }

    private void updateState() {
        if (this.availableDevicesHeader != null && this.deviceList != null) {
            this.availableDevicesHeader.setVisibility(this.devices.size() > 0 ? 0 : 4);
            ((ArrayAdapter) this.deviceList.getAdapter()).notifyDataSetChanged();
        }
    }

    public final void onDeviceFound(DiscoveredIro device) {
        super.onDeviceFound(device);
        if (!this.devices.contains(device)) {
            this.devices.add(device);
        }
        updateState();
    }

    protected final void onDeviceSearchCanceled() {
        super.onDeviceSearchCanceled();
        if (this.devices.size() == 0) {
            this.searching.setVisibility(8);
            this.noDeviceFound.setVisibility(0);
        }
    }

    protected final void onSpecificDeviceFound(DiscoveredIro device) {
        super.onSpecificDeviceFound(device);
        onDeviceSelected(device);
    }

    public void onResume() {
        super.onResume();
        updateState();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("devices", this.devices);
    }
}
