package com.rachio.iro.gen2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.fragment.BaseFragment;

public class WifiUpdateButtonsActivity extends BaseActivity {

    public static class ButtonsFragment extends BaseFragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_gen2_wifiupdatebuttons, container, false);
            view.findViewById(R.id.gen2_gotoleds).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ((BaseActivity) ButtonsFragment.this.getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new LedsFragment()).addToBackStack(null).commit();
                }
            });
            return view;
        }
    }

    public static class LedsFragment extends BaseFragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_gen2_wifiupdateleds, container, false);
            view.findViewById(R.id.gen2_gotoupdate).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    WifiUpdateButtonsActivity.access$200((WifiUpdateButtonsActivity) ((BaseActivity) LedsFragment.this.getActivity()));
                }
            });
            return view;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_wifiupdateinstructions);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ButtonsFragment()).commit();
        }
    }

    static /* synthetic */ void access$200(WifiUpdateButtonsActivity x0) {
        Bundle extras = x0.getIntent().getExtras();
        Intent intent = new Intent(x0, ProvActivity.class);
        intent.putExtra("wifisettingsonly", true);
        intent.putExtra("device_mac", extras.getString("device_mac"));
        intent.putExtra("device_name", extras.getString("device_name"));
        intent.putExtra("device_serialnumber", extras.getString("device_serialnumber"));
        x0.startActivity(intent);
        x0.finish();
    }
}
