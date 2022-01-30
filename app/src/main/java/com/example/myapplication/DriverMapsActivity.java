package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityDriverMapsBinding;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DriverMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.logoutMenuItem:
                Log.i("logout","Logging out");
                ParseUser.logOut();
                System.out.println(ParseUser.getCurrentUser());
                startActivity(new Intent(DriverMapsActivity.this, LoginActivity.class));
                return true;
            case android.R.id.home:
                System.out.println("going back");
                finish();
                return true;
            default:
                break;
        }
        return false;
    }

    private GoogleMap mMap;
    private ActivityDriverMapsBinding binding;

    String mapPurpose;
    boolean isCenteringOn = true;
    Button mapCenteringButton;
    List<ParseObject> driverBusstops = null;
    Marker driverLocationMarker = null;
    Button toggleBusstopsVisibiliyButton;
    ArrayList<Marker> driverBusstopMarkers = null;
    boolean busstopMarkersVisibility = false;

    Handler busTrackingHandler = new Handler();
    Runnable busTrackingRunnable;
    Location busLocation = new Location(LocationManager.GPS_PROVIDER);
    ParseGeoPoint busGeoPoint;

    @Override
    protected void onResume() {
        //start handler as activity become visible
        System.out.println("on resume");
        busTrackingHandler.postDelayed( busTrackingRunnable = new Runnable() {
            public void run() {
                System.out.println("run");
                //do something

                busGeoPoint = DriverActivity.curBus.getParseGeoPoint("curLocation");
                busLocation.setLatitude(busGeoPoint.getLatitude());
                busLocation.setLongitude(busGeoPoint.getLongitude());
                updateMap(busLocation, isCenteringOn);


                busTrackingHandler.postDelayed(busTrackingRunnable, AppConstants.UPDATE_INTERVAL_IN_MILLISECONDS);
            }
        }, AppConstants.UPDATE_INTERVAL_IN_MILLISECONDS);

        super.onResume();
    }

    @Override
    protected void onPause() {
        System.out.println("on pause");
        busTrackingHandler.removeCallbacks(busTrackingRunnable); //stop handler when activity not visible
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDriverMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mapCenteringButton = (Button) findViewById(R.id.mapCenteringButton);
        toggleBusstopsVisibiliyButton = (Button) findViewById(R.id.toggleBusstopsVisibilityButton);

        Intent intent = getIntent();
        mapPurpose = intent.getStringExtra("purpose");
    }

    public void updateMap(Location location, boolean isCenterOn) {
        System.out.println("updateMap");
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if(driverLocationMarker != null) {
            driverLocationMarker.remove();
        }
        driverLocationMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        if(isCenterOn) {
            isCenteringOn = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.clear();
        markBusstops();

    }

    public void markBusstops() {
        driverBusstops = ApiService.getAllBusstops(DriverActivity.curBus);
        driverBusstopMarkers = new ArrayList<Marker>();
        ParseGeoPoint busstopGeoPoint;
        LatLng busstopLatLng;
        for(ParseObject busstop: driverBusstops) {
            busstopGeoPoint =  busstop.getParseGeoPoint("location");
            busstopLatLng = new LatLng(busstopGeoPoint.getLatitude(), busstopGeoPoint.getLongitude());
            driverBusstopMarkers.add(mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(busstopLatLng).title(busstop.getString("name"))));
        }
        toggleBusstopsVisibility(null);
    }

    public void mapCentering(View view) {
        isCenteringOn = true;
    }

    public void toggleBusstopsVisibility(View view) {
        System.out.println("toggle");
        busstopMarkersVisibility = !busstopMarkersVisibility;
        System.out.println(busstopMarkersVisibility);
        for(Marker busstopMarker: driverBusstopMarkers) {
            busstopMarker.setVisible(busstopMarkersVisibility);
        }
    }
}