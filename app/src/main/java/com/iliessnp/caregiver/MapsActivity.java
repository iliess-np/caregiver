package com.iliessnp.caregiver;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static boolean mapsAct;
    private GoogleMap mMap;
    String gps_location,alertType,senderId;
    float lat,lon;
    Button btnShowHome;
    TextView tv_alertType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapsAct = true;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnShowHome = findViewById(R.id.btnShowHome);
        tv_alertType = findViewById(R.id.tv_alertType);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            gps_location = intent.getExtras().getString("gps_location");
            alertType = intent.getExtras().getString("alert_type");
            senderId = intent.getExtras().getString("sender_id");

            String[] gpsArray = gps_location.split(",");
            lat = Float.parseFloat(gpsArray[0]);
            lon = Float.parseFloat(gpsArray[1]);
        }
        btnShowHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mapsAct = false;

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("sender_id", senderId);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng userPosition = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(userPosition).title("Marker in user Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 16.0f));

        tv_alertType.setText(alertType);

    }
}