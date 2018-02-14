package com.rachio.iro.ui.activity.device;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchWeatherStationsCommand;
import com.rachio.iro.async.command.FetchWeatherStationsCommand.FetchForecastStationsListener;
import com.rachio.iro.async.command.FetchWeatherStationsCommand.WeatherStationsHolder;
import com.rachio.iro.model.weather.ForecastStation;
import com.rachio.iro.model.weather.PreferedForecastStation;
import com.rachio.iro.model.weather.WeatherForecast;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.view.ProgressView;
import com.rachio.iro.utils.PermissionRequester;
import com.rachio.iro.utils.PermissionRequester.Listener;
import com.rachio.iro.utils.StringUtils;
import com.rachio.iro.utils.UnitUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseWeatherStationActivity extends ActivityThatSavesDevice implements OnItemClickListener, ConnectionCallbacks, OnConnectionFailedListener, OnMapReadyCallback, FetchForecastStationsListener {
    private String deviceId;
    private FetchWeatherStationsCommand fetchWeatherStationsCommand;
    private boolean haveLocationPermission = false;
    private WeatherStationsHolder mForecastStations;
    private GoogleApiClient mGoogleApiClient;
    private TextView mGooglePlayErrorText;
    private Handler mMainHandler = new Handler();
    private GoogleMap mMap;
    private ProgressView mProgressView;
    private String mSelectedStationId;
    private Map<String, Marker> mStationToMarker;
    private Marker mUserMarker;
    private ListView mWeatherStationList;
    private Map<Marker, String> markerToStation;
    private PermissionRequester permissionRequester;
    private SwitchCompat pwsSwitch;
    private Runnable showStationsRunnable = new Runnable() {
        public void run() {
            ChooseWeatherStationActivity.access$000(ChooseWeatherStationActivity.this, 2000);
        }
    };

    private class MoreInfoClickListener implements OnClickListener {
        private final String stationUrl;

        public MoreInfoClickListener(String stationUrl) {
            this.stationUrl = stationUrl;
        }

        public void onClick(View v) {
            ChooseWeatherStationActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.stationUrl)));
        }
    }

    protected class StationAdapter extends ArrayAdapter<ForecastStation> {
        private final LayoutInflater mlayoutInflater;

        public StationAdapter(Context context, List<ForecastStation> stations) {
            super(context, R.layout.list_item_forecast_station, stations);
            this.mlayoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            int i = 0;
            if (convertView == null) {
                convertView = this.mlayoutInflater.inflate(R.layout.list_item_forecast_station, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            ForecastStation forecastStation = (ForecastStation) getItem(position);
            Object obj = forecastStation.stationId;
            viewHolder.stationTitleText.setText(obj);
            viewHolder.stationDescriptionText.setText(forecastStation.createSnippet(ChooseWeatherStationActivity.this.mForecastStations.user));
            if (ChooseWeatherStationActivity.this.mSelectedStationId == null || !ChooseWeatherStationActivity.this.mSelectedStationId.equalsIgnoreCase(obj)) {
                viewHolder.selectionIcon.setImageResource(R.drawable.ic_uncheck_gray);
            } else {
                viewHolder.selectionIcon.setImageResource(R.drawable.ic_check_green);
            }
            TextView textView = viewHolder.precip;
            if (forecastStation.hasPrecip) {
                i = 4;
            }
            textView.setVisibility(i);
            viewHolder.moreInfo.setOnClickListener(new MoreInfoClickListener(forecastStation.url));
            return convertView;
        }
    }

    protected class ViewHolder {
        TextView moreInfo;
        TextView precip;
        ImageView selectionIcon;
        TextView stationDescriptionText;
        TextView stationTitleText;

        public ViewHolder(View view) {
            this.selectionIcon = (ImageView) view.findViewById(R.id.selection_icon);
            this.stationTitleText = (TextView) view.findViewById(R.id.station_title_text);
            this.stationDescriptionText = (TextView) view.findViewById(R.id.station_description_text);
            this.moreInfo = (TextView) view.findViewById(R.id.station_description_moreinfo);
            this.precip = (TextView) view.findViewById(R.id.station_description_noprecip);
        }
    }

    static /* synthetic */ void access$000(ChooseWeatherStationActivity x0, int x1) {
        Builder builder = new Builder();
        builder.include(x0.mUserMarker.getPosition());
        for (Marker position : x0.mStationToMarker.values()) {
            builder.include(position.getPosition());
        }
        x0.mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), UnitUtils.toDp(x0, 64)), 2000, new CancelableCallback() {
            public final void onFinish() {
                ChooseWeatherStationActivity.this.onMarkerAnimationsComplete();
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deviceId = getDeviceIdFromExtras();
        setContentView((int) R.layout.activity_choose_weather_station);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.mProgressView = (ProgressView) findViewById(R.id.progress_view);
        this.mGooglePlayErrorText = (TextView) findViewById(R.id.google_play_error_text);
        this.mGooglePlayErrorText.setVisibility(8);
        this.pwsSwitch = (SwitchCompat) findViewById(R.id.choose_weather_station_pwsswitch);
        this.mWeatherStationList = (ListView) findViewById(R.id.weather_station_list);
        this.mWeatherStationList.setOnItemClickListener(this);
        this.mStationToMarker = new HashMap();
        this.markerToStation = new HashMap();
        ProgressView progressView = this.mProgressView;
        progressView.show(progressView.getContext().getString(R.string.progress_text_loading_device_location));
        this.pwsSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ChooseWeatherStationActivity.this.mForecastStations != null && isChecked != ChooseWeatherStationActivity.this.mForecastStations.device.includeAllWeatherStations) {
                    ChooseWeatherStationActivity.this.mForecastStations.device.includeAllWeatherStations = isChecked;
                    ChooseWeatherStationActivity.this.saveDevice(ChooseWeatherStationActivity.this.mForecastStations.device, false);
                }
            }
        });
        this.permissionRequester = new PermissionRequester((BaseActivity) this, "android.permission.ACCESS_COARSE_LOCATION", new Listener() {
            public final void onPermissionsGranted() {
                if (ChooseWeatherStationActivity.this.mMap != null) {
                    ChooseWeatherStationActivity.this.mMap.setMyLocationEnabled(true);
                    ChooseWeatherStationActivity.this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
                ChooseWeatherStationActivity.this.haveLocationPermission = true;
            }

            public final void onPermissionsDenied() {
            }
        });
        if (savedInstanceState == null) {
            initState(getIntent().getExtras());
        }
        connectToGooglePlay();
    }

    public final boolean hasChanges() {
        if (this.mForecastStations == null || StringUtils.equals(this.mForecastStations.device.preferredStation, this.mSelectedStationId)) {
            return false;
        }
        return true;
    }

    public final void save() {
        removeAllMarkers();
        addUserMarker();
        addMarkersForStations();
        onMarkerAnimationsComplete();
        dropPinEffect((Marker) this.mStationToMarker.get(this.mSelectedStationId));
        this.mForecastStations.device.preferredStation = this.mSelectedStationId;
        saveDevice(this.mForecastStations.device, true, new Class[]{PreferedForecastStation.class, WeatherForecast.class});
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("stationId", this.mSelectedStationId);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initState(savedInstanceState);
    }

    private void initState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mSelectedStationId = savedInstanceState.getString("stationId");
        }
    }

    public final void onMapReady(GoogleMap map) {
        this.mMap = map;
        UiSettings uiSettings = this.mMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        this.mMap.setMyLocationEnabled(this.haveLocationPermission);
        uiSettings.setMyLocationButtonEnabled(this.haveLocationPermission);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        this.mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public final boolean onMarkerClick(Marker marker) {
                String stationId = (String) ChooseWeatherStationActivity.this.markerToStation.get(marker);
                if (stationId != null) {
                    ChooseWeatherStationActivity.this.selectForecastStation(ChooseWeatherStationActivity.this.mForecastStations.getStationById(stationId));
                }
                return true;
            }
        });
        loadStations();
    }

    private synchronized void connectToGooglePlay() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        this.mGoogleApiClient.connect();
    }

    public final void onConnected(Bundle bundle) {
        this.mGooglePlayErrorText.setVisibility(8);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    public final void onConnectionSuspended(int i) {
    }

    public final void onConnectionFailed(ConnectionResult connectionResult) {
        this.mGooglePlayErrorText.setVisibility(0);
        this.mProgressView.setVisibility(8);
        Toast.makeText(this, "Connection to Google failed", 0).show();
    }

    private void loadStations() {
        this.fetchWeatherStationsCommand = new FetchWeatherStationsCommand(this, this.deviceId);
        this.fetchWeatherStationsCommand.execute();
    }

    public final void onForecastStationsFetched(WeatherStationsHolder stations) {
        this.fetchWeatherStationsCommand = null;
        this.mForecastStations = stations;
        this.mSelectedStationId = stations.device.preferredStation;
        this.pwsSwitch.setChecked(this.mForecastStations.device.includeAllWeatherStations);
        if (this.mForecastStations.forecastStations == null || this.mForecastStations.forecastStations.size() == 0) {
            Toast.makeText(this, "Unable to locate nearby stations. Please try again, later", 0).show();
        }
        this.mWeatherStationList.setAdapter(new StationAdapter(this, this.mForecastStations.forecastStations));
        this.mProgressView.setVisibility(8);
        removeAllMarkers();
        this.mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(new LatLng(this.mForecastStations.device.latitude, this.mForecastStations.device.longitude)).zoom(15.5f).tilt(45.0f).bearing(320.0f).build()));
        addUserMarker();
        dropPinEffect(this.mUserMarker);
        addMarkersForStations();
        this.mMainHandler.postDelayed(this.showStationsRunnable, 1000);
    }

    private void onMarkerAnimationsComplete() {
        this.mUserMarker.hideInfoWindow();
        Marker selectedMarker = highlightSelectedStationMarker();
        if (selectedMarker != null) {
            selectMarker(selectedMarker);
        }
    }

    private Marker highlightSelectedStationMarker() {
        Marker selectedMarker = null;
        for (ForecastStation f : this.mForecastStations.forecastStations) {
            Marker m = (Marker) this.mStationToMarker.get(f.stationId);
            if (StringUtils.equals(f.stationId, this.mSelectedStationId)) {
                m.setIcon(BitmapDescriptorFactory.fromResource(f.personalWeatherStation ? R.drawable.mappin_pws_selected : R.drawable.mappin_national_selected));
                selectedMarker = m;
            } else {
                m.setIcon(BitmapDescriptorFactory.fromResource(f.personalWeatherStation ? R.drawable.mappin_pws_default : R.drawable.mappin_national_default));
            }
        }
        return selectedMarker;
    }

    private void addMarkersForStations() {
        for (ForecastStation station : this.mForecastStations.forecastStations) {
            if (!(station == null || TextUtils.isEmpty(station.stationId) || ((Marker) this.mStationToMarker.get(station.stationId)) != null)) {
                Marker addMarker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(station.latitude, station.longitude)).icon(BitmapDescriptorFactory.fromResource(station.personalWeatherStation ? R.drawable.mappin_pws_default : R.drawable.mappin_national_default)).title(station.stationId).snippet(station.createSnippet(this.mForecastStations.user)).anchor(0.5f, 1.0f));
                this.mStationToMarker.put(station.stationId, addMarker);
                this.markerToStation.put(addMarker, station.stationId);
            }
        }
    }

    private void addUserMarker() {
        Color.colorToHSV(getResources().getColor(R.color.rachio_blue), new float[3]);
        this.mUserMarker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(this.mForecastStations.device.latitude, this.mForecastStations.device.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.mappin_home)).title("Your Device").anchor(0.5f, 1.0f));
    }

    private void removeAllMarkers() {
        if (this.mStationToMarker != null && this.mStationToMarker.size() != 0) {
            this.mMap.clear();
            this.mStationToMarker.clear();
        }
    }

    private void selectMarker(Marker marker) {
        this.mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();
    }

    private void dropPinEffect(Marker marker) {
        if (marker == null) {
            throw new IllegalArgumentException();
        }
        final long start = System.currentTimeMillis();
        final Interpolator interpolator = new BounceInterpolator();
        final Marker marker2 = marker;
        this.mMainHandler.post(new Runnable() {
            public void run() {
                float t = Math.max(1.0f - interpolator.getInterpolation(((float) (System.currentTimeMillis() - start)) / 800.0f), 0.0f);
                marker2.setAnchor(0.5f, (6.0f * t) + 1.0f);
                if (((double) t) > 0.0d) {
                    ChooseWeatherStationActivity.this.mMainHandler.postDelayed(this, 15);
                } else {
                    marker2.showInfoWindow();
                }
            }
        });
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        selectForecastStation((ForecastStation) this.mWeatherStationList.getAdapter().getItem(position));
    }

    private void selectForecastStation(ForecastStation item) {
        if (item != null && !TextUtils.isEmpty(item.stationId)) {
            this.mSelectedStationId = item.stationId;
            Marker selectedMarker = (Marker) this.mStationToMarker.get(this.mSelectedStationId);
            highlightSelectedStationMarker();
            selectMarker(selectedMarker);
            ((StationAdapter) this.mWeatherStationList.getAdapter()).notifyDataSetChanged();
        }
    }

    public final void onDeviceDataChanged(String deviceId) {
        super.onDeviceDataChanged(deviceId);
        if (StringUtils.equals(deviceId, this.deviceId)) {
            loadStations();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.permissionRequester.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onResume() {
        super.onResume();
        this.permissionRequester.requestPermissions();
    }

    public final boolean isBusy() {
        return false;
    }

    public final boolean isValid() {
        return true;
    }
}
