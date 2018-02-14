package com.rachio.iro.ui.fragment.blinkup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BlinkUpActivity;
import com.rachio.iro.ui.activity.BlinkUpActivity.BlinkUpView;
import com.rachio.iro.utils.WifiUtils;

public class WifiSetupStepTwoFragment extends BaseBlinkupFragment {
    private Button continueButton;
    private TextView networkName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_wifi_2, container, false);
        wireUpHelp(view);
        this.networkName = (TextView) view.findViewById(R.id.blinkup_connect_wifi_2_network_name);
        TextView updateButton = (TextView) view.findViewById(R.id.connect_wifi_2_update_button);
        this.continueButton = (Button) view.findViewById(R.id.connect_wifi_2_next_button);
        updateButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                WifiSetupStepTwoFragment.this.update();
            }
        });
        this.continueButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((BlinkUpActivity) WifiSetupStepTwoFragment.this.getActivity()).moveToStep(BlinkUpView.WifiStep3);
            }
        });
        return view;
    }

    public void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        String currentNetwork = WifiUtils.getNetworkName(getActivity());
        this.continueButton.setEnabled(!TextUtils.isEmpty(currentNetwork));
        this.networkName.setText(currentNetwork);
    }
}
