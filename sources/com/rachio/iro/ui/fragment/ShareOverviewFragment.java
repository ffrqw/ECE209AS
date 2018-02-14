package com.rachio.iro.ui.fragment;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.device.ShareActivity;

public class ShareOverviewFragment extends BaseFragment {
    private Device device;
    private User user;

    public static ShareOverviewFragment newInstance(User user, Device device) {
        ShareOverviewFragment sof = new ShareOverviewFragment();
        sof.user = user;
        sof.device = device;
        return sof;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share_overview, container, false);
        Button add = (Button) v.findViewById(R.id.share_add);
        ((TextView) v.findViewById(R.id.share_supporttext)).setMovementMethod(LinkMovementMethod.getInstance());
        add.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ShareActivity.addUser((ShareActivity) ShareOverviewFragment.this.getActivity(), ShareOverviewFragment.this.user, ShareOverviewFragment.this.device.id);
            }
        });
        return v;
    }
}
