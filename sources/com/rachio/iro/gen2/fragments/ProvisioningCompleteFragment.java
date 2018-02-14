package com.rachio.iro.gen2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;
import com.rachio.iro.gen2.FoundIroView;
import com.rachio.iro.gen2.MrvlProvService;
import com.rachio.iro.model.device.ShallowDevice;
import com.rachio.iro.model.device.ShallowDevice.Model;
import com.rachio.iro.model.user.User;
import com.rachio.iro.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProvisioningCompleteFragment extends BaseProvisioningFragment {

    /* renamed from: com.rachio.iro.gen2.fragments.ProvisioningCompleteFragment$2 */
    class AnonymousClass2 extends ArrayAdapter<ShallowDevice> {
        AnonymousClass2(Context x0, int x1, List x2) {
            super(x0, -1, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new FoundIroView(getContext());
            }
            ((FoundIroView) convertView).set((ShallowDevice) getItem(position));
            return convertView;
        }
    }

    public static final ProvisioningCompleteFragment newInstance(String deviceId, Model deviceModel) {
        ProvisioningCompleteFragment fragment = new ProvisioningCompleteFragment();
        Bundle args = new Bundle();
        args.putString(MrvlProvService.EXTRA_OUT_DEVICEID, deviceId);
        args.putSerializable(MrvlProvService.EXTRA_OUT_DEVICEMODEL, deviceModel);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_complete, container, false);
        Button gotoSetup = (Button) view.findViewById(R.id.gen2_gotosetup);
        LinearLayout copyExistingContainer = (LinearLayout) view.findViewById(R.id.gen2_copyexistingcontainer);
        ListView existingDevices = (ListView) view.findViewById(R.id.gen2_existingdevices);
        User user = getUser();
        gotoSetup.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProvisioningCompleteFragment.this.setupDevice();
            }
        });
        Bundle args = getArguments();
        String deviceId = args.getString(MrvlProvService.EXTRA_OUT_DEVICEID);
        Model deviceModel = (Model) args.getSerializable(MrvlProvService.EXTRA_OUT_DEVICEMODEL);
        ArrayList<ShallowDevice> devices = new ArrayList();
        Collection<ShallowDevice> userDevices = user.getAllShadowDevices();
        if (userDevices != null) {
            for (ShallowDevice d : userDevices) {
                if (!StringUtils.equals(deviceId, d.id) && d.isGen1() && d.model.numZones <= deviceModel.numZones) {
                    devices.add(d);
                }
            }
        }
        if (devices.size() > 0) {
            existingDevices.setAdapter(new AnonymousClass2(getActivity(), -1, devices));
            existingDevices.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ProvisioningCompleteFragment.this.copyDevice(((ShallowDevice) parent.getItemAtPosition(position)).id);
                }
            });
            copyExistingContainer.setVisibility(0);
        }
        wireUpHelpAndExit(view);
        return view;
    }
}
