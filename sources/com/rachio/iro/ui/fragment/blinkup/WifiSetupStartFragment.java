package com.rachio.iro.ui.fragment.blinkup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BlinkUpActivity;
import com.rachio.iro.ui.activity.BlinkUpActivity.BlinkUpView;

public class WifiSetupStartFragment extends BaseBlinkupFragment {
    private Button continueButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_wifi_start, container, false);
        wireUpHelp(view);
        this.continueButton = (Button) view.findViewById(R.id.connect_wifi_start_next_button);
        this.continueButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ((BlinkUpActivity) WifiSetupStartFragment.this.getActivity()).moveToStep(BlinkUpView.WifiStep1);
            }
        });
        return view;
    }
}
