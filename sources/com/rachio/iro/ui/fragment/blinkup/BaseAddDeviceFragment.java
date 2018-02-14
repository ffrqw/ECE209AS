package com.rachio.iro.ui.fragment.blinkup;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.rachio.iro.R;
import com.rachio.iro.utils.ValidationUtils;

public abstract class BaseAddDeviceFragment extends BaseBlinkupFragment {
    private Button continueButton;
    private EditText deviceName;
    private EditText deviceZip;
    private SwitchCompat masterValve;

    public abstract void onContinue(String str, String str2, boolean z);

    static /* synthetic */ void access$000(BaseAddDeviceFragment x0) {
        Object trim = x0.deviceName.getText().toString().trim();
        String trim2 = x0.deviceZip.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            Toast.makeText(x0.getActivity().getApplicationContext(), "Device name required", 1).show();
        } else if (ValidationUtils.isValidZipCode(trim2)) {
            x0.onContinue(trim, trim2, x0.masterValve != null ? x0.masterValve.isChecked() : false);
        } else {
            Toast.makeText(x0.getActivity().getApplicationContext(), "Zipcode invalid", 1).show();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_device_start, container, false);
        wireUpHelp(view);
        this.deviceName = (EditText) view.findViewById(R.id.blinkup_name_edit);
        this.deviceZip = (EditText) view.findViewById(R.id.blinkup_zip_edit);
        this.masterValve = (SwitchCompat) view.findViewById(R.id.blinkup_master_valve);
        this.continueButton = (Button) view.findViewById(R.id.add_device_continue_btn);
        this.continueButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                BaseAddDeviceFragment.access$000(BaseAddDeviceFragment.this);
            }
        });
        return view;
    }
}
