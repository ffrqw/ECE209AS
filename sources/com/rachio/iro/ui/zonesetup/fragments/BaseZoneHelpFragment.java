package com.rachio.iro.ui.zonesetup.fragments;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.ui.activity.HelpActivity;
import com.rachio.iro.ui.fragment.BaseFragment;

public class BaseZoneHelpFragment extends BaseFragment {
    public void updateState(int number, Zone zone) {
    }

    public boolean validate() {
        return true;
    }

    public void commitState(Zone zone) {
    }

    protected final void wireUpHelpExit(View view) {
        ImageView exit = (ImageView) view.findViewById(R.id.gen2prov_exit);
        ((ImageView) view.findViewById(R.id.gen2prov_help)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(BaseZoneHelpFragment.this.getContext(), HelpActivity.class);
                i.putExtra("article", "117-setting-up-zones");
                BaseZoneHelpFragment.this.getContext().startActivity(i);
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BaseZoneHelpFragment.this.getActivity().finish();
            }
        });
    }
}
