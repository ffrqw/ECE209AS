package com.rachio.iro.ui.prodevicelist.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchUserCommand;
import com.rachio.iro.async.command.FetchUserCommand.Listener;
import com.rachio.iro.model.device.ShallowDevice;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.device.AddDeviceTypeActivity;
import com.rachio.iro.ui.prodevicelist.fragment.BaseProDeviceListFragment;
import com.rachio.iro.ui.prodevicelist.fragment.DevicesByDistanceFragment;
import com.rachio.iro.ui.prodevicelist.fragment.DevicesByNameFragment;
import com.rachio.iro.ui.prodevicelist.fragment.DevicesByPersonFragment;
import java.util.List;
import java.util.TreeMap;

public class ProDeviceListActivity extends BaseProDeviceActivity implements Listener {
    private boolean allDevicesBelongToUser = false;
    private boolean canUseLocation = false;
    private List<ShallowDevice> devices;
    private FetchUserCommand fetchUserDevicesCommand;
    private BaseProDeviceListFragment[] fragments = new BaseProDeviceListFragment[2];
    protected final TreeMap<String, String> keywords = new TreeMap();
    private int numDevices;
    private int numfrags = 0;
    private SearchView searchView;
    private String selectedDeviceId;
    private boolean showDetails = false;
    private boolean showIndexes = false;
    private boolean showSearch = false;
    private TabLayout tabs;
    private User user;
    private ViewPager viewPager;

    public static void start(Context context, int deviceNumberOverride) {
        Intent intent = new Intent(context, ProDeviceListActivity.class);
        if (deviceNumberOverride != -1) {
            intent.putExtra("overridedevices", deviceNumberOverride);
        }
        context.startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_prodevicelist);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.viewPager = (ViewPager) findViewById(R.id.prodevicelist_viewpager);
        this.tabs = (TabLayout) findViewById(R.id.prodevicelist_tabs);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (this.showSearch) {
            inflater.inflate(R.menu.snippet_search, menu);
            this.searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            this.searchView.setImeOptions(6);
            this.searchView.setOnQueryTextListener(new OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    FragmentPagerAdapter adapter = (FragmentPagerAdapter) ProDeviceListActivity.this.viewPager.getAdapter();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        ((BaseProDeviceListFragment) adapter.getItem(i)).filter(newText);
                    }
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            });
            this.searchView.setOnSearchClickListener(new OnClickListener() {
                public void onClick(View v) {
                    FragmentPagerAdapter adapter = (FragmentPagerAdapter) ProDeviceListActivity.this.viewPager.getAdapter();
                    if (adapter != null) {
                        for (int i = 0; i < adapter.getCount(); i++) {
                            ((BaseProDeviceListFragment) adapter.getItem(i)).startingSearch();
                        }
                    }
                }
            });
            this.searchView.setOnCloseListener(new OnCloseListener() {
                public boolean onClose() {
                    FragmentPagerAdapter adapter = (FragmentPagerAdapter) ProDeviceListActivity.this.viewPager.getAdapter();
                    if (adapter != null) {
                        for (int i = 0; i < adapter.getCount(); i++) {
                            ((BaseProDeviceListFragment) adapter.getItem(i)).stopSearch();
                        }
                    }
                    return false;
                }
            });
        }
        inflater.inflate(R.menu.snippet_add, menu);
        return true;
    }

    protected void onResume() {
        super.onResume();
        this.fetchUserDevicesCommand = new FetchUserCommand(this, this.prefsWrapper.getLoggedInUserId());
        this.fetchUserDevicesCommand.execute();
    }

    protected void onPause() {
        super.onPause();
        if (this.fetchUserDevicesCommand != null) {
            this.fetchUserDevicesCommand.isCancelled = true;
            this.fetchUserDevicesCommand = null;
        }
    }

    public final void onUserLoaded(User user) {
        boolean z = true;
        this.fetchUserDevicesCommand = null;
        if (user != null) {
            boolean z2;
            this.user = user;
            this.devices = user.getAllShadowDevices();
            this.numDevices = getIntent().getIntExtra("overridedevices", this.devices.size());
            if (this.numDevices >= 5) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.showSearch = z2;
            if (this.numDevices >= 5) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.showIndexes = z2;
            if (this.numDevices < 5) {
                z = false;
            }
            this.showDetails = z;
            invalidateOptionsMenu();
            this.selectedDeviceId = user.getSelectedDeviceId(this.prefsWrapper);
            this.keywords.clear();
            for (ShallowDevice device : this.devices) {
                this.keywords.put(device.id, (device.name + " " + device.ownerName + " " + device.getDeviceLocation() + " " + device.getRoughStatus()).toLowerCase());
            }
            this.allDevicesBelongToUser = user.allDevicesBelongToUser();
            if (this.fragments == null || this.fragments[0] == null) {
                this.permissionRequester.requestPermissions();
            } else {
                updateFragments();
            }
            updateLocation();
        }
    }

    public final void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        BaseProDeviceListFragment proDeviceListFragment = (BaseProDeviceListFragment) fragment;
        if (this.user != null) {
            proDeviceListFragment.setDevices(this.user, this.selectedDeviceId, this.numDevices, this.devices, this.keywords, this.showIndexes);
        }
        if (proDeviceListFragment instanceof DevicesByNameFragment) {
            this.fragments[0] = proDeviceListFragment;
        } else if (proDeviceListFragment instanceof DevicesByDistanceFragment) {
            this.fragments[0] = proDeviceListFragment;
        } else if (proDeviceListFragment instanceof DevicesByPersonFragment) {
            this.fragments[1] = proDeviceListFragment;
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                AddDeviceTypeActivity.start(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public final void onDeviceSelected(String deviceId) {
        if (this.showDetails) {
            ProDeviceDetailsActivity.start(this, deviceId);
        } else {
            switchDevice(deviceId);
        }
    }

    public void onBackPressed() {
        if (this.searchView == null || this.searchView.isIconified()) {
            super.onBackPressed();
        } else {
            this.searchView.setIconified(true);
        }
    }

    protected final void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        if (this.viewPager != null) {
            FragmentPagerAdapter adapter = (FragmentPagerAdapter) this.viewPager.getAdapter();
            if (adapter != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    ((BaseProDeviceListFragment) adapter.getItem(i)).setLocation(location);
                }
            }
        }
    }

    public final void refresh() {
        if (this.fetchUserDevicesCommand == null) {
            this.fetchUserDevicesCommand = new FetchUserCommand(this, this.prefsWrapper.getLoggedInUserId());
            this.fetchUserDevicesCommand.execute();
        }
    }

    public final void onPermissionsGranted() {
        super.onPermissionsGranted();
        if (this.viewPager != null) {
            this.canUseLocation = true;
            createTabs();
            FragmentPagerAdapter adapter = (FragmentPagerAdapter) this.viewPager.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                adapter.getItem(i);
                BaseProDeviceListFragment.onPermissionsGranted();
            }
        }
    }

    public final void onPermissionsDenied() {
        super.onPermissionsDenied();
        if (this.viewPager != null) {
            this.canUseLocation = false;
            createTabs();
            FragmentPagerAdapter adapter = (FragmentPagerAdapter) this.viewPager.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                adapter.getItem(i);
                BaseProDeviceListFragment.onPermissionsDenied();
            }
        }
    }

    private void updateFragments() {
        if (this.fragments != null && this.devices != null) {
            for (BaseProDeviceListFragment fragment : this.fragments) {
                if (fragment != null) {
                    fragment.setDevices(this.user, this.selectedDeviceId, this.numDevices, this.devices, this.keywords, this.showIndexes);
                }
            }
        }
    }

    private void createTabs() {
        this.fragments[0] = this.canUseLocation ? DevicesByDistanceFragment.newInstance() : DevicesByNameFragment.newInstance();
        this.numfrags = 1;
        if (!this.allDevicesBelongToUser) {
            this.fragments[1] = DevicesByPersonFragment.newInstance();
            this.numfrags++;
        }
        this.viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            public final Fragment getItem(int position) {
                return ProDeviceListActivity.this.fragments[position];
            }

            public final int getCount() {
                return ProDeviceListActivity.this.numfrags;
            }

            public final CharSequence getPageTitle(int position) {
                return ProDeviceListActivity.this.fragments[position].getTitle();
            }
        });
        if (this.numfrags > 1) {
            this.tabs.setupWithViewPager(this.viewPager);
            this.tabs.setVisibility(0);
        } else {
            this.tabs.removeAllTabs();
            this.tabs.setVisibility(8);
        }
        updateFragments();
    }
}
