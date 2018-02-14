package com.rachio.iro.ui.fragment.blinkup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.gen2.MrvlProvService;
import com.rachio.iro.ui.activity.BlinkUpActivity;
import com.rachio.iro.ui.activity.BlinkUpActivity.BlinkUpView;
import com.rachio.iro.ui.activity.DashboardActivity;
import com.rachio.iro.ui.zonesetup.ZoneHelpActivity;
import com.rachio.iro.utils.WifiUtils;

public class BlinkUpFragment extends BaseBlinkupFragment {
    private String deviceId;
    private Button doneButton;
    private ProgressBar progressBar;
    private Button setupZonesButton;
    private TextView statusText;
    private Button tryAgainButton;

    public static BlinkUpFragment newInstance(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        BlinkUpFragment fragment = new BlinkUpFragment();
        Bundle args = new Bundle();
        args.putString("USERID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.deviceId = savedInstanceState.getString(MrvlProvService.EXTRA_OUT_DEVICEID);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blinkup_status, container, false);
        this.statusText = (TextView) view.findViewById(R.id.blinkup_status_text);
        this.progressBar = (ProgressBar) view.findViewById(R.id.blinkup_status_progress);
        this.tryAgainButton = (Button) view.findViewById(R.id.blinkup_try_again_button);
        this.tryAgainButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent blinkupIntent = new Intent(BlinkUpFragment.this.getActivity().getApplicationContext(), BlinkUpActivity.class);
                blinkupIntent.putExtra("BLINKUP_STEP", BlinkUpView.PreBlinkUp);
                blinkupIntent.putExtra("BLINKUP_ACTION", ((BlinkUpActivity) BlinkUpFragment.this.getActivity()).isUpdateBlinkUp() ? "UPDATE" : "ADD");
                BlinkUpFragment.this.startActivity(blinkupIntent);
            }
        });
        this.setupZonesButton = (Button) view.findViewById(R.id.blinkup_setup_zones_button);
        this.setupZonesButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ZoneHelpActivity.start(BlinkUpFragment.this.getContext(), BlinkUpFragment.this.deviceId);
            }
        });
        this.doneButton = (Button) view.findViewById(R.id.blinkup_done_button);
        this.doneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DashboardActivity.goToDashboard(BlinkUpFragment.this.getContext(), BlinkUpFragment.this.prefsWrapper, BlinkUpFragment.this.getArguments().getString("USERID"), true, false, null, BlinkUpFragment.this.deviceId, true, false);
            }
        });
        setStatusMessage("Trying to connect Iro to " + WifiUtils.getNetworkName(getActivity()), true);
        return view;
    }

    public final void setStatusMessage(String message, boolean showSpinner) {
        this.statusText.setText(message);
        this.progressBar.setVisibility(showSpinner ? 0 : 4);
    }

    public final void onFailure() {
        this.tryAgainButton.setVisibility(0);
    }

    public final void onSuccess(String deviceId) {
        this.deviceId = deviceId;
        setStatusMessage("Your Iro is now connected to your WiFi network.", false);
        this.setupZonesButton.setVisibility(0);
        this.doneButton.setVisibility(0);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MrvlProvService.EXTRA_OUT_DEVICEID, this.deviceId);
    }
}
