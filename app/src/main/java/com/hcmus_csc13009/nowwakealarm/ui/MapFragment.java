package com.hcmus_csc13009.nowwakealarm.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

import java.util.List;

public class MapFragment extends Fragment {
    private AlarmViewModel alarmViewModel = null;
    private List<Alarm> allAlarms;

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
            LatLng myHouse = new LatLng(16.4058107,107.6754465);
            googleMap.addMarker(new MarkerOptions().position(myHouse).title("Marker in My House"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myHouse));
            alarmViewModel = new ViewModelProvider(MapFragment.this).get(AlarmViewModel.class);
            alarmViewModel.getAllAlarms().observe(MapFragment.this, new Observer<List<Alarm>>() {
                @Override
                public void onChanged(List<Alarm> alarms) {
                    if (alarms != null && allAlarms == null) {
                        for (Alarm alarm : alarms) {
                            Log.i("@@@ titile", alarm.getTitle());
                            Log.i("@@@ pos:", alarm.getPosition() == null ? "null" : "ok");
                            if (alarm.getPosition() != null) {
                                Log.i("@@@ position: ", alarm.getPosition());
                                googleMap.addMarker(new MarkerOptions().position(alarm.getLatLngPosition())
                                        .title(alarm.getTitle())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_clock_pointer))
                                        );

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



        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}