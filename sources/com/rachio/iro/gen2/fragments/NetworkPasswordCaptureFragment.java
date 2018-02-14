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
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;

public class NetworkPasswordCaptureFragment extends BaseProvisioningFragment {
    public static NetworkPasswordCaptureFragment newInstance(String currentPassword) {
        NetworkPasswordCaptureFragment fragment = new NetworkPasswordCaptureFragment();
        Bundle args = new Bundle();
        args.putString("currentpassword", currentPassword);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_networkpasswordcapture, container, false);
        final EditText password = (EditText) view.findViewById(R.id.networkpassword_password);
        final Button cont = (Button) view.findViewById(R.id.networkpassword_continue);
        password.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                Button button = cont;
                boolean z = s != null && s.length() >= 8 && s.length() <= 63;
                button.setEnabled(z);
            }
        });
        if (savedInstanceState == null) {
            password.setText(getArguments().getString("currentpassword"));
        }
        cont.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String passwordString = password.getEditableText().toString();
                if (!TextUtils.isEmpty(passwordString)) {
                    NetworkPasswordCaptureFragment.this.onNetworkPasswordCaptured(passwordString);
                }
            }
        });
        wireUpHelpAndExit(view);
        return view;
    }
}
