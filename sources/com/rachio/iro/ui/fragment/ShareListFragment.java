package com.rachio.iro.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Device.Manager;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.device.ShareActivity;
import java.util.ArrayList;
import java.util.List;

public class ShareListFragment extends BaseFragment {
    private ActionMode actionMode;
    private Device device;
    private Callback mActionModeCallback = new Callback() {
        public final boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.snippet_delete, menu);
            return true;
        }

        public final boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public final boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    mode.finish();
                    List<Manager> deletedManagers = new ArrayList();
                    SparseBooleanArray checkedItems = ShareListFragment.this.managers.getCheckedItemPositions();
                    for (int i = 0; i < ShareListFragment.this.managers.getCount(); i++) {
                        if (checkedItems.get(i)) {
                            deletedManagers.add((Manager) ShareListFragment.this.managers.getItemAtPosition(i));
                        }
                    }
                    for (Manager m : deletedManagers) {
                        ShareActivity.deleteManager((BaseActivity) ShareListFragment.this.getActivity(), ShareListFragment.this.device.id, m.email);
                    }
                    return true;
                default:
                    return false;
            }
        }

        public final void onDestroyActionMode(ActionMode mode) {
            ShareListFragment.this.actionMode = null;
        }
    };
    private ListView managers;

    /* renamed from: com.rachio.iro.ui.fragment.ShareListFragment$2 */
    class AnonymousClass2 extends ArrayAdapter<Manager> {
        AnonymousClass2(Context x0, int x1, List x2) {
            super(x0, 0, x2);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_manager, parent, false);
            }
            Manager m = (Manager) getItem(position);
            TextView managerEmail = (TextView) convertView.findViewById(R.id.manager_email);
            ((TextView) convertView.findViewById(R.id.manager_name)).setText(m.fullName);
            managerEmail.setText(m.email);
            return convertView;
        }
    }

    public static ShareListFragment newInstance(Device device) {
        ShareListFragment slf = new ShareListFragment();
        slf.device = device;
        slf.setHasOptionsMenu(true);
        return slf;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share_list, container, false);
        this.managers = (ListView) v.findViewById(R.id.share_manager_list);
        this.managers.setAdapter(new AnonymousClass2(getActivity(), 0, this.device.managers));
        this.managers.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View arg1, int position, long id) {
                if (ShareListFragment.this.actionMode != null) {
                    return false;
                }
                ShareListFragment.this.actionMode = ((BaseActivity) ShareListFragment.this.getActivity()).startSupportActionMode(ShareListFragment.this.mActionModeCallback);
                ShareListFragment.this.managers.setItemChecked(position, true);
                return true;
            }
        });
        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.snippet_add, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                ShareActivity.addUser((ShareActivity) getActivity(), User.getLoggedInUser(this.database, this.prefsWrapper), this.device.id);
                return true;
            default:
                return false;
        }
    }
}
