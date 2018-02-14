package com.rachio.iro.gen2.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;
import com.rachio.iro.gen2.MrvlProvService;

public class DeviceSettingsCaptureFragment extends BaseProvisioningFragment {
    public static DeviceSettingsCaptureFragment newInstance(String deviceName, String deviceZip) {
        DeviceSettingsCaptureFragment fragment = new DeviceSettingsCaptureFragment();
        Bundle args = new Bundle();
        args.putString("devicename", deviceName);
        args.putString(MrvlProvService.EXTRA_OUT_DEVICEZIP, deviceZip);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_devicesettingscapture, container, false);
        final EditText name = (EditText) view.findViewById(R.id.wifisettings_name);
        final EditText zip = (EditText) view.findViewById(R.id.wifisettings_zip);
        final Button doIt = (Button) view.findViewById(R.id.wifisettings_doit);
        doIt.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String nameString = name.getEditableText().toString();
                String zipString = zip.getEditableText().toString();
                if (TextUtils.isEmpty(nameString)) {
                    Toast.makeText(DeviceSettingsCaptureFragment.this.getContext(), "You must enter all of the network details", 1).show();
                } else {
                    DeviceSettingsCaptureFragment.this.onDeviceSettingsCaptured(nameString, zipString);
                }
            }
        });
        name.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                doIt.setEnabled(s.length() > 0);
            }
        });
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            name.setText(args.getString("devicename"));
            zip.setText(args.getString(MrvlProvService.EXTRA_OUT_DEVICEZIP));
        }
        wireUpHelpAndExit(view);
        return view;
    }
}
