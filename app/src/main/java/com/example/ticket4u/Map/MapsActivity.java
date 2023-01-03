package com.example.ticket4u.Map;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.ticket4u.R;
import com.example.ticket4u.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
          String latitude= getIntent().getStringExtra("latitude");
        String longitude= getIntent().getStringExtra("longitude");
        LatLng sydney = new LatLng(Double.valueOf(latitude),Double.valueOf( longitude));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }
}