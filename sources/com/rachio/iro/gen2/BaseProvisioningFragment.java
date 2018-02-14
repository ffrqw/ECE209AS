package com.rachio.iro.gen2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.view.View;
import android.widget.ImageView;
import com.rachio.iro.R;
import com.rachio.iro.gen2.MrvlProvService.State;
import com.rachio.iro.gen2.model.DiscoveredIro;
import com.rachio.iro.gen2.model.FoundNetwork;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.HelpActivity;
import com.rachio.iro.ui.fragment.BaseFragment;

public class BaseProvisioningFragment extends BaseFragment {
    protected final void onDeviceSelected(DiscoveredIro iro) {
        getProvActivity().onDeviceSelected(iro);
    }

    protected final void onNetworkSelected(final FoundNetwork network) {
        if (!network.mightBe5Gig || network.probablyDualBand) {
            getProvActivity().onNetworkSelected(network);
        } else {
            new Builder(getContext()).setTitle("Dedicated 5GHz Networks Not Supported").setMessage((CharSequence) "Rachio supports 2.4GHz and dual band networks. Please make sure you do not select a dedicated 5GHz network.").setNegativeButton((CharSequence) "Cancel", null).setPositiveButton((CharSequence) "Continue", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    BaseProvisioningFragment.this.getProvActivity().onNetworkSelected(network);
                }
            }).setCancelable(false).show();
        }
    }

    protected final void onNetworkPasswordCaptured(String password) {
        getProvActivity().onNetworkPasswordCaptured(password);
    }

    protected final void onSerialNumberCaptured(String serialNumber) {
        getProvActivity().onSerialNumberCaptured(serialNumber);
    }

    protected final void onDeviceSettingsCaptured(String name, String zip) {
        getProvActivity().onDeviceSettingsCaptured(name, zip);
    }

    protected final void onMasterValveSettingCaptured(boolean haveMasterValve) {
        getProvActivity().onMasterValveCaptured(haveMasterValve);
    }

    protected void onDeviceFound(DiscoveredIro device) {
    }

    protected void onSpecificDeviceFound(DiscoveredIro device) {
    }

    protected void onDeviceSearchCanceled() {
    }

    protected void onNetworkFound(FoundNetwork network) {
    }

    protected void onNetworkSearchCanceled() {
    }

    protected void onProvisionStateChanged(State newState) {
    }

    protected final void copyDevice(String sourceDeviceId) {
        getProvActivity().copyDevice(sourceDeviceId);
    }

    protected final void setupDevice() {
        getProvActivity().setupDevice();
    }

    protected final User getUser() {
        return getProvActivity().getUser();
    }

    protected final void setupZones() {
        getProvActivity().setupZones();
    }

    protected final void restart() {
        getProvActivity().restart();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        getView().requestFocus();
    }

    public final void startScanningForDevices() {
        getProvActivity().startScanningForDevices();
    }

    public final void startScanningForNetworks() {
        getProvActivity().startScanningForNetworks();
    }

    public void onSetupComplete() {
    }

    public final void wireUpHelpAndExit(View view) {
        ImageView exit = (ImageView) view.findViewById(R.id.gen2prov_exit);
        ((ImageView) view.findViewById(R.id.gen2prov_help)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = BaseProvisioningFragment.this.getContext();
                Intent i = new Intent(context, HelpActivity.class);
                i.putExtra("article", "455-how-to-activate-your-controller");
                context.startActivity(i);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BaseProvisioningFragment.this.getProvActivity().exit(new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BaseProvisioningFragment.this.getProvActivity().goToDashboardOrNoDevices();
                    }
                });
            }
        });
    }

    protected final ProvActivity getProvActivity() {
        return (ProvActivity) ((BaseActivity) getActivity());
    }
}
