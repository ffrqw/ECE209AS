package com.rachio.iro.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.instabug.library.Instabug;
import com.rachio.iro.IroApplication;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.async.command.FetchUserCommand;
import com.rachio.iro.async.command.FetchUserCommand.Listener;
import com.rachio.iro.binder.BaseEventBinder.MoreHistoryListener;
import com.rachio.iro.binder.CurrentlyWateringBinder.CurrentlyWateringListener;
import com.rachio.iro.binder.DeviceStatusBinder.EditDeviceSettingsListener;
import com.rachio.iro.binder.DeviceStatusBinder.SetRainDelayListener;
import com.rachio.iro.binder.LocalWeatherBinder.ChooseWeatherStationListener;
import com.rachio.iro.binder.MyYardBinder.MyYardCardListener;
import com.rachio.iro.binder.WateringScheduleBinder.WateringScheduleBinderListener;
import com.rachio.iro.cloud.DeleteScheduleExecutionAsyncTask;
import com.rachio.iro.cloud.PushPull;
import com.rachio.iro.cloud.PutRainDelayAsyncTask;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.device.ChooseWeatherStationActivity;
import com.rachio.iro.ui.activity.device.DeviceSettingsActivity;
import com.rachio.iro.ui.activity.device.HistoryActivity;
import com.rachio.iro.ui.activity.device.NoDeviceActivity;
import com.rachio.iro.ui.activity.device.RemoteControlActivity;
import com.rachio.iro.ui.activity.user.LoginActivity;
import com.rachio.iro.ui.activity.user.MyNozzlesActivity;
import com.rachio.iro.ui.activity.user.ProfileActivity;
import com.rachio.iro.ui.activity.user.SettingsActivity;
import com.rachio.iro.ui.activity.zone.ZoneMoistureLevelActivity;
import com.rachio.iro.ui.dialog.DemoDialog;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.fragment.OnFragmentSelectedListener;
import com.rachio.iro.ui.fragment.dashboard.DeviceDashboardFragment;
import com.rachio.iro.ui.fragment.dashboard.EventsDashboardFragment;
import com.rachio.iro.ui.fragment.dashboard.HelpDashboardFragment;
import com.rachio.iro.ui.fragment.dashboard.ReportsDashboardFragment;
import com.rachio.iro.ui.newschedulerulepath.activity.ViewScheduleActivity;
import com.rachio.iro.ui.prodevicelist.activity.ProDeviceListActivity;
import com.rachio.iro.ui.welcome.WelcomeActivity;
import com.rachio.iro.ui.zonesetup.ZoneHelpActivity;
import com.rachio.iro.utils.ActionBarDrawerToggle;
import com.rachio.iro.utils.ProgressDialogAsyncTask;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends BaseActivity implements FetchDeviceListener, Listener, MoreHistoryListener, CurrentlyWateringListener, EditDeviceSettingsListener, SetRainDelayListener, ChooseWeatherStationListener, MyYardCardListener, WateringScheduleBinderListener, OnFragmentSelectedListener {
    private static final String TAG = DashboardActivity.class.getCanonicalName();
    private boolean belongsToSomeoneElse = false;
    private TextView currentDeviceLocation;
    private Dialog demoDialog;
    private Device device;
    private String deviceId;
    private TextView devicesSpinner;
    private FetchDeviceCommand fetchDeviceCommand;
    private FetchUserCommand fetchUserCommand;
    private BaseFragment mActiveFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Map<String, ToggleButton> mNavigationButtonMap;
    private Button mRemoteControlButton;
    private boolean once = false;
    private WebView preloadWebView;
    private boolean readOnly;
    private Toolbar toolbar;
    private User user;
    private String userId;

    /* renamed from: com.rachio.iro.ui.activity.DashboardActivity$2 */
    class AnonymousClass2 extends ActionBarDrawerToggle {
        AnonymousClass2(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, R.string.navigation_drawer_closed, R.string.navigation_drawer_closed);
        }

        public final void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            DashboardActivity.this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            DashboardActivity.this.getSupportActionBar().setDisplayShowCustomEnabled(true);
            DashboardActivity.this.invalidateOptionsMenu();
        }

        public final void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            DashboardActivity.this.getSupportActionBar().setDisplayShowTitleEnabled(true);
            DashboardActivity.this.getSupportActionBar().setDisplayShowCustomEnabled(false);
            DashboardActivity.this.toolbar.setTitle((int) R.string.navigation_drawer_open);
            DashboardActivity.this.invalidateOptionsMenu();
        }
    }

    public interface OnDeviceDataChangedListener {
        void onDeviceDataChanged(String str);
    }

    public interface OnSelectedDeviceChangedListener {
        void onSelectedDeviceChanged(String str);
    }

    public interface OnZoneDataChangedListener {
        void onZoneDataChanged$552c4e01();
    }

    private class RemoteControlNavClickListener implements OnClickListener {
        private RemoteControlNavClickListener() {
        }

        public void onClick(View v) {
            if (v instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) v;
                if (v.getParent() instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) v.getParent();
                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
                        View childAt = viewGroup.getChildAt(i);
                        if (childAt instanceof ToggleButton) {
                            ((ToggleButton) childAt).setChecked(false);
                        }
                    }
                }
                toggleButton.setChecked(true);
                DashboardActivity.this.onNavigateToSection((String) toggleButton.getTag());
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        boolean z;
        super.onCreate(savedInstanceState);
        this.userId = this.prefsWrapper.getLoggedInUserId();
        if (savedInstanceState != null) {
            this.once = true;
        }
        setContentView((int) R.layout.activity_device_dashboard);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View spinnerWithText = getLayoutInflater().inflate(R.layout.view_spinnerwithtext, null);
        this.devicesSpinner = (TextView) spinnerWithText.findViewById(R.id.spinner);
        this.currentDeviceLocation = (TextView) spinnerWithText.findViewById(R.id.text);
        spinnerWithText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProDeviceListActivity.start(DashboardActivity.this, -1);
            }
        });
        actionBar.setCustomView(spinnerWithText, new LayoutParams(17));
        actionBar.setDisplayShowCustomEnabled(true);
        this.mNavigationButtonMap = new HashMap(5);
        this.mRemoteControlButton = (Button) findViewById(R.id.remote_floating_action_button);
        this.mRemoteControlButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DashboardActivity.this.onRemoteControlButtonClicked(v);
            }
        });
        DashboardActivity dashboardActivity = this;
        OnClickListener remoteControlNavClickListener = new RemoteControlNavClickListener();
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.remote_control_button_bar);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ToggleButton) {
                this.mNavigationButtonMap.put((String) childAt.getTag(), (ToggleButton) childAt);
                childAt.setOnClickListener(remoteControlNavClickListener);
            }
        }
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.mDrawerToggle = new AnonymousClass2(this, this.mDrawerLayout, R.string.navigation_drawer_closed, R.string.navigation_drawer_closed);
        ActionBarDrawerToggle actionBarDrawerToggle = this.mDrawerToggle;
        if (actionBarDrawerToggle.isHamburger()) {
            z = false;
        } else {
            z = true;
        }
        actionBarDrawerToggle.showHamburger(z);
        this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
        this.mDrawerToggle.setDrawerIndicatorEnabled(true);
        GridLayout nozzles = (GridLayout) findViewById(R.id.dashboard_drawer_nozzles);
        GridLayout help = (GridLayout) findViewById(R.id.dashboard_drawer_help);
        GridLayout feedback = (GridLayout) findViewById(R.id.dashboard_drawer_sendfeedback);
        GridLayout preferences = (GridLayout) findViewById(R.id.dashboard_drawer_preferences);
        GridLayout profile = (GridLayout) findViewById(R.id.dashboard_drawer_profile);
        GridLayout signout = (GridLayout) findViewById(R.id.dashboard_drawer_signout);
        findViewById(R.id.dashboard_drawer_debug);
        help.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, HelpActivity.class));
            }
        });
        feedback.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Instabug.setUserEmail(User.getLoggedInUser(DashboardActivity.this.database, DashboardActivity.this.prefsWrapper).email);
                Instabug.invoke();
            }
        });
        nozzles.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, MyNozzlesActivity.class);
                i.putExtra("USERID", DashboardActivity.this.userId);
                DashboardActivity.this.startActivity(i);
            }
        });
        preferences.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, SettingsActivity.class);
                i.putExtra("USERID", DashboardActivity.this.userId);
                DashboardActivity.this.startActivity(i);
            }
        });
        profile.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            }
        });
        signout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new ProgressDialogAsyncTask<Void, Void, Void>(DashboardActivity.this) {
                    protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                        super.onPostExecute((Void) obj);
                        DashboardActivity.this.finish();
                        DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                    }

                    protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                        User.logout(DashboardActivity.this, DashboardActivity.this.database, DashboardActivity.this.prefsWrapper, ((IroApplication) DashboardActivity.this.getApplication()).getRestClient());
                        return null;
                    }
                }.execute(null);
            }
        });
        Bundle extras = getIntent().getExtras();
        if (savedInstanceState == null) {
            initState(extras);
        }
        z = extras != null && extras.getBoolean("readonly", false);
        this.readOnly = z;
        this.preloadWebView = (WebView) findViewById(R.id.preloadwebview);
    }

    private void initState(Bundle bundle) {
        if (bundle != null) {
            this.deviceId = bundle.getString("DEVICEID");
        }
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mDrawerToggle.onConfigurationChanged$308b225b();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected final void onResumeFragments() {
        super.onResumeFragments();
        if (this.fetchUserCommand == null) {
            this.fetchUserCommand = new FetchUserCommand(this, this.userId);
            this.fetchUserCommand.execute();
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.fetchUserCommand != null) {
            this.fetchUserCommand.isCancelled = true;
            this.fetchUserCommand = null;
        }
        if (this.fetchDeviceCommand != null) {
            this.fetchDeviceCommand.isCancelled = true;
            this.fetchDeviceCommand = null;
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.readOnly && this.demoDialog == null) {
            this.demoDialog = DemoDialog.show(this);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("DEVICEID", this.deviceId);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initState(savedInstanceState);
    }

    public final void onFragmentSelected(Fragment fragment) {
        if (fragment instanceof BaseFragment) {
            this.mActiveFragment = (BaseFragment) fragment;
            updateNavbarButtons();
            return;
        }
        throw new IllegalArgumentException("Error: attempted to add a " + fragment.getClass().getName() + " to this activity but only BaseFragment can be added to this Activity");
    }

    private void updateNavbarButtons() {
        if (this.mNavigationButtonMap != null && this.mActiveFragment != null && !TextUtils.isEmpty(this.mActiveFragment.getSection())) {
            String section = this.mActiveFragment.getSection();
            for (ToggleButton toggleButton : this.mNavigationButtonMap.values()) {
                toggleButton.setChecked(section.equals(toggleButton.getTag()));
            }
        }
    }

    public final boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    private boolean checkForGoogleMaps() {
        try {
            getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public final void onChooseWeatherStation() {
        if (checkForGoogleMaps()) {
            this.mActiveFragment.showProgress(null);
            Intent intent = new Intent(this, ChooseWeatherStationActivity.class);
            intent.putExtra("DEVICEID", this.deviceId);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return;
        }
        Toast.makeText(this, "Please install google maps", 0).show();
    }

    public final void onEditDeviceSettings() {
        if (this.userId == null || this.deviceId == null) {
            throw new IllegalStateException("user id or device id are null; user: " + this.userId + " device: " + this.deviceId);
        }
        Intent intent = new Intent(this, DeviceSettingsActivity.class);
        intent.putExtra("USERID", this.userId);
        intent.putExtra("DEVICEID", this.deviceId);
        startActivity(intent);
    }

    public final void onEditWateringTimes() {
        Intent intent = new Intent(this, ViewScheduleActivity.class);
        intent.putExtra("DEVICEID", this.deviceId);
        startActivity(intent);
    }

    public final void onStampClicked(Date day) {
        Intent intent = new Intent(this, ViewScheduleActivity.class);
        intent.putExtra("DEVICEID", this.deviceId);
        intent.putExtra("extra_selected_day", day.getTime());
        startActivity(intent);
    }

    public final void onConfigureZones() {
        ZoneHelpActivity.start(this, this.deviceId);
    }

    public final void onToggleShowDisabled() {
        if (this.mActiveFragment instanceof DeviceDashboardFragment) {
            ((DeviceDashboardFragment) this.mActiveFragment).toggleShowDisabledZones();
        }
    }

    public final void onWateringStopClicked() {
        if (this.device != null) {
            new DeleteScheduleExecutionAsyncTask(this, this.device, null).execute(new Void[0]);
        }
    }

    public final void onToggleDetails() {
        if (this.mActiveFragment instanceof EventsDashboardFragment) {
            ((EventsDashboardFragment) this.mActiveFragment).toggleCurrentlyWateringDetails();
        }
    }

    public final void onMoreDeviceUpdates(String topic) {
        if (this.deviceId != null) {
            HistoryActivity.showHistory(this, this.deviceId, topic);
        }
    }

    private void onNavigateToSection(String tag) {
        if (tag != null && this.deviceId != null) {
            Fragment fragmentToShow = null;
            if (tag.equalsIgnoreCase(getString(R.string.navigation_section_activity))) {
                fragmentToShow = findFragment(EventsDashboardFragment.class);
                if (fragmentToShow == null) {
                    fragmentToShow = EventsDashboardFragment.newInstance(this.deviceId);
                }
            } else if (tag.equalsIgnoreCase(getString(R.string.navigation_section_dashboard))) {
                fragmentToShow = findFragment(DeviceDashboardFragment.class);
                if (fragmentToShow == null) {
                    fragmentToShow = DeviceDashboardFragment.newInstance(this.deviceId, this.readOnly);
                }
            } else if (tag.equalsIgnoreCase(getString(R.string.navigation_section_reports))) {
                fragmentToShow = findFragment(ReportsDashboardFragment.class);
                if (fragmentToShow == null) {
                    fragmentToShow = ReportsDashboardFragment.newInstance(this.deviceId);
                }
            } else if (tag.equalsIgnoreCase(getString(R.string.navigation_section_help))) {
                fragmentToShow = findFragment(HelpDashboardFragment.class);
                if (fragmentToShow == null) {
                    fragmentToShow = HelpDashboardFragment.newInstance(null);
                }
            }
            if (fragmentToShow != null) {
                fragmentToShow.getTag();
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.replace(R.id.content_frame, fragmentToShow, fragmentToShow.getClass().getName());
                supportFragmentManager.popBackStack(null, 1);
                beginTransaction.commit();
            }
        }
    }

    private Fragment findFragment(Class<? extends Fragment> fragmentClass) {
        return getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());
    }

    public void onRemoteControlButtonClicked(View view) {
        if (this.deviceId != null) {
            Intent intent = new Intent(this, RemoteControlActivity.class);
            intent.putExtra("DEVICEID", this.deviceId);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.slide_in_from_bottom, 17432577);
        }
    }

    public final void onDeviceDataChanged(String deviceId) {
        super.onDeviceDataChanged(deviceId);
        if (this.mActiveFragment instanceof OnDeviceDataChangedListener) {
            ((OnDeviceDataChangedListener) this.mActiveFragment).onDeviceDataChanged(deviceId);
        }
    }

    protected final void onZoneDataChanged() {
        super.onZoneDataChanged();
        if (this.mActiveFragment instanceof OnZoneDataChangedListener) {
            ((OnZoneDataChangedListener) this.mActiveFragment).onZoneDataChanged$552c4e01();
        }
    }

    public final void onRainDelaySet(int days) {
        new PutRainDelayAsyncTask(this, this.deviceId, days * 86400).execute(null);
    }

    public final void onUserLoaded(User user) {
        this.fetchUserCommand = null;
        if (user != null) {
            this.user = user;
            this.deviceId = user.getSelectedDeviceId(this.prefsWrapper);
            this.belongsToSomeoneElse = user.deviceBelongsToSomeoneElse(this.deviceId);
            if (!this.once) {
                this.once = true;
                String section = getIntent().getStringExtra("section");
                if (section != null) {
                    onNavigateToSection(section);
                } else {
                    onNavigateToSection("Dashboard");
                }
            }
            loadDevice();
        }
    }

    private void loadDevice() {
        if (this.fetchDeviceCommand != null) {
            this.fetchDeviceCommand.isCancelled = true;
        }
        this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
        this.fetchDeviceCommand.execute();
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        if (device != null) {
            if (this.user != null) {
                this.devicesSpinner.setText(device.name);
                this.currentDeviceLocation.setText(device.getDeviceLocation());
            }
            if (device.zones != null && device.zones.size() > 0) {
                Zone firstZone = (Zone) device.zones.iterator().next();
                if (firstZone != null && this.preloadWebView != null) {
                    ZoneMoistureLevelActivity.preCache(this.preloadWebView, this.userId, device.id, firstZone.id, this.prefsWrapper.getLoggedInUserAccessToken());
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case 100:
                    onNavigateToSection(data.getStringExtra("section"));
                    return;
                default:
                    throw new RuntimeException();
            }
        }
    }

    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(3)) {
            this.mDrawerLayout.closeDrawer(3);
        } else {
            super.onBackPressed();
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        String deviceId = extras.getString("DEVICEID");
        String section = extras.getString("section");
        if (deviceId != null) {
            if (deviceId == null) {
                throw new IllegalArgumentException("device id cannot be null");
            }
            this.deviceId = deviceId;
            this.prefsWrapper.setSelectedDeviceId(this.deviceId);
            if (this.mActiveFragment instanceof OnSelectedDeviceChangedListener) {
                ((OnSelectedDeviceChangedListener) this.mActiveFragment).onSelectedDeviceChanged(this.deviceId);
            }
            loadDevice();
        }
        if (section != null) {
            onNavigateToSection(section);
        }
        if (intent.getBooleanExtra("reloaddevices", false)) {
            reloadUserDevices();
        }
    }

    public final void reloadUserDevices() {
        if (this.fetchUserCommand == null) {
            this.fetchUserCommand = new FetchUserCommand(this, this.userId);
            this.fetchUserCommand.execute();
        }
    }

    public final void refreshSelectedDevice() {
        PushPull.backgroundPullEntityAndSave(this, Device.class, this.deviceId, this.userId, this.belongsToSomeoneElse);
    }

    public static final void goToDashboard(Context context, PrefsWrapper prefsWrapper, String userId, boolean hasDevices, boolean readOnly, String section, String deviceId, boolean reloadDevices, boolean tryToReuseDashboard) {
        if (userId == null) {
            throw new IllegalArgumentException("user id cannot be null");
        }
        Intent i;
        if (hasDevices) {
            boolean welcomeShown = prefsWrapper.welcomeShown();
            i = new Intent(context, welcomeShown ? DashboardActivity.class : WelcomeActivity.class);
            if (tryToReuseDashboard) {
                i.setFlags(67108864);
            } else {
                i.setFlags(268468224);
            }
            i.putExtra("USERID", userId);
            i.putExtra("readonly", readOnly);
            i.putExtra("section", section);
            i.putExtra("DEVICEID", deviceId);
            i.putExtra("reloaddevices", reloadDevices);
            if (!welcomeShown) {
                prefsWrapper.onWelcomeShown();
            }
        } else {
            i = new Intent(context, NoDeviceActivity.class);
            i.setFlags(268468224);
        }
        context.startActivity(i);
    }
}
