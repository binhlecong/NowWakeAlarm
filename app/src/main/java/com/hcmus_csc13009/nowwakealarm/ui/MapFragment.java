package com.hcmus_csc13009.nowwakealarm.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

public class MapFragment extends Fragment {
    private AlarmViewModel alarmViewModel = null;
    private List<Alarm> allAlarms;

    private FusedLocationProviderClient fusedLocationClient;
    private Location deviceLocation = null;
    private SearchView searchView;

    private GoogleMap mMap;

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
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            Toast.makeText(getContext(), "Map is ready", Toast.LENGTH_SHORT).show();
            LatLng myHouse = new LatLng(16.4058107,107.6754465);
            googleMap.addMarker(new MarkerOptions().position(myHouse).title("Marker in My House"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myHouse));
            getDeviceLocation();
            alarmViewModel = new ViewModelProvider(MapFragment.this).get(AlarmViewModel.class);
            alarmViewModel.getAllAlarms().observe(MapFragment.this, new Observer<List<Alarm>>() {
                @Override
                public void onChanged(List<Alarm> alarms) {
                    if (alarms != null && allAlarms == null) {
                        for (Alarm alarm : alarms) {
                            if (alarm.getPosition() != null) {
                                googleMap.addMarker(new MarkerOptions().position(alarm.getLatLngPosition())
                                        .title(alarm.getTitle())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_clock_pointer)));
                            }
                        }
                    }
                    allAlarms = alarms;
                }
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        searchView = view.findViewById(R.id.searchBar);
        prepareSearchBar();
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
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
                Geocoder geocoder = new Geocoder(MapFragment.this.getContext());
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng position = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(position).title(location));
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

    private void getDeviceLocation() {
        Log.i("@@@ preGetPosition", "true");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, 1234);
        }
        Log.i("@@@ afterCheckPermission", "true");

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), (OnSuccessListener<Location>) location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        deviceLocation = location;
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1234: {
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            return;
                        }
                    }
                }
            }
        }
    }
}