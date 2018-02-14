package com.rachio.iro.ui.zonesetup.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.zonesetup.ZoneHelpActivity;

public class ZoneHelpLocateFragment extends BaseZoneHelpFragment {
    private static Handler handler = new Handler();
    private static int sCountDownFrom = 5;
    private CountRunnable countRunnable;
    private TextView mCounterTextView;
    private Button mDisableButton;
    private TextView mLocateTitleTextView;
    private TextView mMessageTextView;
    private Button mNoButton;
    private Button mSkipButton;
    private Button mYesButton;
    private int zoneNumber = -1;

    private class CountRunnable implements Runnable {
        private boolean cancelled = false;
        private int mCount;

        public CountRunnable(int count) {
            this.mCount = count;
        }

        public void run() {
            if (!this.cancelled && ((BaseActivity) ZoneHelpLocateFragment.this.getActivity()) != null) {
                ZoneHelpLocateFragment.this.mCounterTextView.setText(Integer.toString(this.mCount));
                this.mCount--;
                if (this.mCount >= 0) {
                    ZoneHelpLocateFragment.handler.postDelayed(this, 1000);
                    return;
                }
                ZoneHelpLocateFragment.this.mYesButton.setVisibility(0);
                ZoneHelpLocateFragment.this.mNoButton.setVisibility(0);
                ZoneHelpLocateFragment.this.mCounterTextView.setVisibility(8);
                ZoneHelpLocateFragment.this.mMessageTextView.setText(R.string.zone_help_locate_question);
                ((ZoneHelpActivity) ZoneHelpLocateFragment.this.getActivity()).waterZone();
            }
        }
    }

    public static final class NoZonesEnabledException extends RuntimeException {
    }

    public static ZoneHelpLocateFragment newInstance() {
        return new ZoneHelpLocateFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        parent.removeAllViews();
        View v = inflater.inflate(R.layout.fragment_zone_help_locate, parent, false);
        wireUpHelpExit(v);
        this.mCounterTextView = (TextView) v.findViewById(R.id.count_down_text_view);
        this.mLocateTitleTextView = (TextView) v.findViewById(R.id.locate_title);
        this.mMessageTextView = (TextView) v.findViewById(R.id.locate_message);
        this.mYesButton = (Button) v.findViewById(R.id.yes_button);
        this.mYesButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ((ZoneHelpActivity) ZoneHelpLocateFragment.this.getActivity()).configureZone();
            }
        });
        this.mNoButton = (Button) v.findViewById(R.id.no_button);
        this.mNoButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ZoneHelpLocateFragment.access$000(ZoneHelpLocateFragment.this);
            }
        });
        this.mSkipButton = (Button) v.findViewById(R.id.skip_button);
        this.mSkipButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ZoneHelpLocateFragment.this.countRunnable.cancelled = true;
                ((ZoneHelpActivity) ZoneHelpLocateFragment.this.getActivity()).skipZone();
            }
        });
        this.mDisableButton = (Button) v.findViewById(R.id.disable_button);
        this.mDisableButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ZoneHelpLocateFragment.this.countRunnable.cancelled = true;
                ((ZoneHelpActivity) ZoneHelpLocateFragment.this.getActivity()).disableZone();
            }
        });
        return v;
    }

    private void countDown() {
        this.mMessageTextView.setText("Turning sprinklers on in Zone " + this.zoneNumber + "...");
        this.mYesButton.setVisibility(8);
        this.mNoButton.setVisibility(8);
        this.mCounterTextView.setVisibility(0);
        this.mCounterTextView.setText(Integer.toString(sCountDownFrom));
        this.countRunnable = new CountRunnable(sCountDownFrom);
        this.countRunnable.run();
    }

    public final void updateState(int number, Zone zone) {
        super.updateState(number, zone);
        this.zoneNumber = number;
        this.mLocateTitleTextView.setText("Locate Zone " + this.zoneNumber);
        countDown();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    static /* synthetic */ void access$000(ZoneHelpLocateFragment x0) {
        Builder builder = new Builder(x0.getActivity());
        CharSequence[] stringArray = x0.getResources().getStringArray(R.array.zone_help_options);
        builder.setTitle(x0.getResources().getString(R.string.zone_help_options_title));
        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        ZoneHelpLocateFragment.this.countDown();
                        break;
                    case 1:
                        ((ZoneHelpActivity) ZoneHelpLocateFragment.this.getActivity()).skipZone();
                        break;
                    case 2:
                        ((ZoneHelpActivity) ZoneHelpLocateFragment.this.getActivity()).disableZone();
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
