package com.rachio.iro.ui.fragment.blinkup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BlinkUpActivity;

public class WifiSetupStepFourFragment extends BaseBlinkupFragment {
    private Button continueButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_wifi_4, container, false);
        wireUpHelp(view);
        this.continueButton = (Button) view.findViewById(R.id.connect_wifi_4_blinkup_button);
        this.continueButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                new Builder(WifiSetupStepFourFragment.this.getActivity()).setTitle("Important Notice").setMessage(WifiSetupStepFourFragment.this.getResources().getString(R.string.blinkup_flashing_warning)).setPositiveButton((CharSequence) "I Understand", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((BlinkUpActivity) WifiSetupStepFourFragment.this.getActivity()).blinkup();
                    }
                }).show();
            }
        });
        return view;
    }
}
