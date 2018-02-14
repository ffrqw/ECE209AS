package com.rachio.iro.ui.fragment.blinkup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BlinkUpActivity;
import com.rachio.iro.ui.activity.BlinkUpActivity.BlinkUpView;
import com.rachio.iro.utils.WifiUtils;

public class WifiSetupStepThreeFragment extends BaseBlinkupFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_wifi_3, container, false);
        wireUpHelp(view);
        final TextView networkName = (TextView) view.findViewById(R.id.blinkup_connect_wifi_3_network_name);
        final EditText passwordText = (EditText) view.findViewById(R.id.blinkup_connect_wifi_3_password);
        Button continueButton = (Button) view.findViewById(R.id.connect_wifi_3_next_button);
        ((TextView) view.findViewById(R.id.connect_wifi_3_update_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                networkName.setText(WifiUtils.getNetworkName(WifiSetupStepThreeFragment.this.getActivity()));
            }
        });
        continueButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                final String password = passwordText.getEditableText().toString();
                if (!TextUtils.isEmpty(password)) {
                    new Builder(WifiSetupStepThreeFragment.this.getActivity()).setTitle("Important Notice").setMessage(WifiSetupStepThreeFragment.this.getResources().getString(R.string.blinkup_network_warning)).setPositiveButton((CharSequence) "I Understand", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((BlinkUpActivity) WifiSetupStepThreeFragment.this.getActivity()).setBlinkUpPassword(password);
                            ((BlinkUpActivity) WifiSetupStepThreeFragment.this.getActivity()).moveToStep(BlinkUpView.PreBlinkUp);
                        }
                    }).show();
                }
            }
        });
        networkName.setText(WifiUtils.getNetworkName(getActivity()));
        return view;
    }
}
