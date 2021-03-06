package com.hcmus_csc13009.nowwakealarm.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.utils.MapUtil;
import com.hcmus_csc13009.nowwakealarm.utils.SettingConstant;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {
    private AlarmViewModel alarmViewModel = null;
    private List<Alarm> allAlarms;

    private FusedLocationProviderClient fusedLocationClient;
    private Location deviceLocation = null;
    private SearchView searchView;

    private Marker currentSeach = null;
    private Marker currentSelected = null;

    private GoogleMap mMap;
    private Geocoder geocoder = null;

    private TextView textView; // support listen address change
    private ProgressBar progressBar;

    private LatLng setPosition;


    final private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;
            Toast.makeText(getContext(), "Map is ready", Toast.LENGTH_SHORT).show();
            if (setPosition != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(setPosition));
                if (textView != null)
                    textView.setText(getCurrentSelected());
            } else {
                LatLng defaultPosition = new LatLng(10.8230989, 106.62696638);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultPosition));
            }

            mMap.setOnMapClickListener(MapFragment.this);
            mMap.setOnMarkerClickListener(MapFragment.this);
            // setup data in database
            if (allAlarms != null)
                getDeviceLocation();
            alarmViewModel = new ViewModelProvider(MapFragment.this).get(AlarmViewModel.class);
            alarmViewModel.getAllAlarms().observe(MapFragment.this, alarms -> {
                if (alarms != null && allAlarms == null) {
                    allAlarms = alarms;
                    getDeviceLocation();
                }
                allAlarms = alarms;
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        searchView = view.findViewById(R.id.searchBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        prepareSearchBar();
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            // setup position for my location button
            View myLocationButton = ((View)mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.setMargins(0, 100, 30, 0);
        }


    }

    private void prepareSearchBar() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location  = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location.length() == 0)
                    return false;
                if (geocoder == null)
                    geocoder = new Geocoder(MapFragment.this.getContext());
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng position = new LatLng(address.getLatitude(), address.getLongitude());
                    if (currentSeach != null)
                        currentSeach.remove();
                    currentSeach = mMap.addMarker(new MarkerOptions().position(position).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
                } else {
                    Toast.makeText(MapFragment.this.getContext(), "Not found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void attachAlarmPosition(List<Alarm> alarms) {
//        mMap.clear();
        for (Alarm alarm : alarms) {
            if (alarm.getPosition() != null && !alarm.getPosition().isEmpty()) {
                LatLng alarmLocation = alarm.getLatLngPosition();
                MarkerOptions options = new MarkerOptions().position(alarmLocation)
                        .title(alarm.getTitle());
                if (!alarm.isEnable())
                    options.alpha(0.3f);
                else if (!alarm.isHardMode())
                    options.alpha(0.8f);
                if (deviceLocation != null && MapUtil.getDistance(deviceLocation.getLatitude(), deviceLocation.getLongitude(),
                        alarmLocation.latitude, alarmLocation.longitude) <= SettingConstant.getNearbyRange(getActivity().getApplicationContext())) {
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_alarm_nearby));
                } else {
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_clock_pointer));
                }
//                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_clock_pointer));
                mMap.addMarker(options);
            }
        }
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, 1234);
            // return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), (OnSuccessListener<Location>) location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        deviceLocation = location;
                        LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addCircle(new CircleOptions().center(locationLatLng)
                                .radius(SettingConstant.getNearbyRange(getActivity().getApplicationContext()))
                                .clickable(false)
                                .fillColor(0x220000FF)
                                .strokeWidth(2));
                        if (setPosition == null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
                            if (textView != null)
                                textView.setText(getCurrentSelected());
                            currentSelected = addMarkerOnMap(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                        setPosition = null;
                    }
                    attachAlarmPosition(allAlarms);

                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1234: {
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    getDeviceLocation();
                }
            }
        }
    }

    private void updateCurrentSelected(LatLng latLng) {
        if (currentSelected != null) {
            currentSelected.remove();
        }
        currentSelected = addMarkerOnMap(latLng);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        updateCurrentSelected(latLng);
        if (textView != null)
            textView.setText(getCurrentSelected());
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        updateCurrentSelected(marker.getPosition());
        return false;
    }

    public String getCurrentSelected() {
        if (currentSelected == null)
            return null;

        String result =  locationToAddress(currentSelected.getPosition().latitude, currentSelected.getPosition().longitude);
        return result == null ? getCurrentSelectedPosition() : result;
    }

    public String getCurrentSelectedPosition() {
        return currentSelected.getPosition().latitude + "," + currentSelected.getPosition().longitude;
    }

    private Marker addMarkerOnMap(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .alpha(0.8f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
        return mMap.addMarker(markerOptions);
    }

    public void setTextViewListener(TextView textView) {
        this.textView = textView;
    }

    // utils
    private String locationToAddress(double lat, double lng) {
        if (geocoder == null)
            geocoder = new Geocoder(MapFragment.this.getContext());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            return null;
        }
        if (addressList != null && addressList.size() > 0) {
            Address address = addressList.get(0);
            return address.getAddressLine(0);
        }
        return null;
    }

    public String positionToAddress(String position) {
        if (position == null) return null;
        if (position.startsWith("(")) {
            position = position.substring(1, position.length() - 1);
        }
        String[] parts = position.split(",");
        if (parts.length != 2) return null;
        return locationToAddress(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }

    public void setPosition(String position) {
        if (position == null) return;
        String[] parts = position.split(",");
        if (parts.length != 2) return;
        setPosition = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(setPosition));
        if (textView != null)
            textView.setText(getCurrentSelected());
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}