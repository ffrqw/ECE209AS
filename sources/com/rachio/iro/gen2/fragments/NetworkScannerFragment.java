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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.gen2.BaseProvisioningFragment;
import com.rachio.iro.gen2.model.FoundNetwork;
import com.rachio.iro.utils.CrashReporterUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworkScannerFragment extends BaseProvisioningFragment {
    private TextView availableNetworksHeader;
    private boolean cancelled = false;
    private ListView networkList;
    private ArrayList<FoundNetwork> networks = new ArrayList();
    private Button tryAgain;

    /* renamed from: com.rachio.iro.gen2.fragments.NetworkScannerFragment$1 */
    class AnonymousClass1 extends ArrayAdapter<FoundNetwork> {
        final /* synthetic */ LayoutInflater val$inflater;

        AnonymousClass1(Context x0, int x1, List x2, LayoutInflater layoutInflater) {
            this.val$inflater = layoutInflater;
            super(x0, -1, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            NetworkViewHolder holder;
            View view;
            if (convertView != null) {
                holder = (NetworkViewHolder) convertView.getTag();
                view = convertView;
            } else {
                view = this.val$inflater.inflate(R.layout.view_foundnetwork, parent, false);
                holder = new NetworkViewHolder();
                holder.name = (TextView) view.findViewById(16908308);
                holder.level = (ImageView) view.findViewById(R.id.level);
                view.setTag(holder);
            }
            FoundNetwork network = (FoundNetwork) getItem(position);
            holder.name.setText(network.toString());
            holder.level.setImageLevel(network.level);
            return view;
        }
    }

    private static final class NetworkViewHolder {
        public ImageView level;
        public TextView name;

        private NetworkViewHolder() {
        }
    }

    public static NetworkScannerFragment newInstance() {
        return new NetworkScannerFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gen2_networkscanner, container, false);
        this.availableNetworksHeader = (TextView) view.findViewById(R.id.gen2prov_availablenetworksheader);
        this.networkList = (ListView) view.findViewById(R.id.gen2prov_networks);
        this.networkList.setEmptyView(view.findViewById(R.id.gen2prov_empty));
        this.tryAgain = (Button) view.findViewById(R.id.gen2prov_tryagain);
        final ArrayAdapter<FoundNetwork> adapter = new AnonymousClass1(getActivity(), -1, this.networks, inflater);
        this.networkList.setAdapter(adapter);
        this.networkList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                NetworkScannerFragment.this.onNetworkSelected((FoundNetwork) adapter.getItem(position));
            }
        });
        this.tryAgain.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CrashReporterUtils.silentException(new Exception("user clicked try again in network scanner"));
                adapter.clear();
                NetworkScannerFragment.this.startScanningForNetworks();
                NetworkScannerFragment.this.tryAgain.setVisibility(8);
            }
        });
        if (this.cancelled) {
            onNetworkSearchCanceled();
            this.cancelled = false;
        }
        if (savedInstanceState != null) {
            this.networks.addAll((ArrayList) savedInstanceState.getSerializable("networks"));
            adapter.notifyDataSetChanged();
        }
        updateState();
        wireUpHelpAndExit(view);
        return view;
    }

    private void updateState() {
        if (this.networkList != null && this.networkList != null) {
            this.availableNetworksHeader.setVisibility(this.networkList.getCount() > 0 ? 0 : 4);
            ((ArrayAdapter) this.networkList.getAdapter()).notifyDataSetChanged();
        }
    }

    protected final void onNetworkFound(FoundNetwork network) {
        super.onNetworkFound(network);
        int index = this.networks.indexOf(network);
        if (index >= 0) {
            this.networks.add(index, FoundNetwork.merge((FoundNetwork) this.networks.remove(index), network));
        } else {
            this.networks.add(network);
        }
        Collections.sort(this.networks, Collections.reverseOrder(new Comparator<FoundNetwork>() {
            public /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
                return Double.compare((double) ((FoundNetwork) obj).level, (double) ((FoundNetwork) obj2).level);
            }
        }));
        updateState();
    }

    protected final void onNetworkSearchCanceled() {
        super.onNetworkSearchCanceled();
        if (this.tryAgain != null) {
            this.tryAgain.setVisibility(0);
        } else {
            this.cancelled = true;
        }
    }

    public void onResume() {
        super.onResume();
        updateState();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("networks", this.networks);
    }
}
