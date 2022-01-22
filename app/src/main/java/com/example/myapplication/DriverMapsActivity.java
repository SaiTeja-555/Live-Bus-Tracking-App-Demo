package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityDriverMapsBinding;
import com.parse.ParseUser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DriverMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

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
                startActivity(new Intent(DriverMapsActivity.this, RegisterOrLoginActivity.class));
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

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(mapParent.equals("driver") || (mapParent.equals("passanger") && (mapPurpose.equals("fromSelection") || mapPurpose.equals("toSelection")))) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    }
                    else if(mapParent.equals("passanger") && (mapPurpose.equals("fromGetLocation") || mapPurpose.equals("toGetLocation"))) {
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                    }
                }
            }
        }
    }
    String mapParent;
    String mapPurpose;
    boolean isCenteringOn = true;
    String selectAddress;
    LatLng selectLatLng;
    Button setLocationButton;
    Button mapCenteringButton;
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
        setLocationButton = (Button) findViewById(R.id.setLocationButton);
        mapCenteringButton = (Button) findViewById(R.id.mapCenteringButton);

        Intent intent = getIntent();
        mapParent = intent.getStringExtra("parent");
        mapPurpose = intent.getStringExtra("purpose");
        System.out.println(mapParent+" "+mapPurpose);
        if(mapParent.equals("driver")) {
            setLocationButton.setVisibility(View.INVISIBLE);

        }
        if(mapParent.equals("passanger") && (mapPurpose.equals("fromGetLocation") || mapPurpose.equals("toGetLocation"))) {
            mapCenteringButton.setVisibility(View.INVISIBLE);
            setLocationButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public void updateMap(Location location, boolean isMapClearOn, boolean isCenterOn, boolean isMarkingOn) {
        System.out.println("updateMap");
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if(isMapClearOn) {
            mMap.clear();
        }
        if(isCenterOn) {
            isCenteringOn = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        }
        if(isMarkingOn) {
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnMapClickListener(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                System.out.println("location changed");
                if(mapParent.equals("driver")) {
                    updateMap(location, true, isCenteringOn, true);
                }
                else if(mapParent.equals("passanger") && (mapPurpose.equals("fromSelection") || mapPurpose.equals("toSelection"))) {
                    updateMap(location, false, isCenteringOn, false);
                }
                else if(mapParent.equals("passanger") && (mapPurpose.equals("fromGetLocation") || mapPurpose.equals("toGetLocation"))) {
                    updateMap(location, true, isCenteringOn, true);
                    LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    String locationAddress = getLocationAddress(locationLatLng);
                    if(mapPurpose.equals("fromGetLocation")) {
                        PassangerActivity.fromLatLng = locationLatLng;
                        PassangerActivity.fromAddress = locationAddress;
                    }
                    else {
                        PassangerActivity.toLatLng = locationLatLng;
                        PassangerActivity.toAddress = locationAddress;
                    }
                    startActivity(new Intent(getApplicationContext(), PassangerActivity.class));
                }
//                else if(mapParent == "passanger") {
//                    if(mapPurpose == "fromToSelection") {
//                        updateMap(location, false, false, false);
//                    }
//                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            if(mapParent.equals("driver") || (mapParent.equals("passanger") && (mapPurpose.equals("fromSelection") || mapPurpose.equals("toSelection")))) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            else if(mapParent.equals("passanger") && (mapPurpose.equals("fromGetLocation") || mapPurpose.equals("toGetLocation"))) {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            }
        }
    }

    public void mapCentering(View view) {
        isCenteringOn = true;
    }

    public void setLocation(View view) {
        if (mapParent.equals("passanger")) {
            if (mapPurpose.equals("fromSelection")) {
                PassangerActivity.fromAddress = selectAddress;
                PassangerActivity.fromLatLng = selectLatLng;
            }
            else if(mapPurpose.equals("toSelection")){
                PassangerActivity.toAddress = selectAddress;
                PassangerActivity.toLatLng = selectLatLng;
            }
            startActivity(new Intent(getApplicationContext(), PassangerActivity.class));
        }

    }

    public String getLocationAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        selectAddress = "";

        try {
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if( listAddresses != null && listAddresses.size() > 0) {
                if(listAddresses.get(0).getThoroughfare() != null) {
                    if(listAddresses.get(0).getSubThoroughfare() != null) {
                        selectAddress += listAddresses.get(0).getSubThoroughfare() + " ";
                    }
                    selectAddress += listAddresses.get(0).getThoroughfare();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(selectAddress.equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyyMMdd");
            selectAddress = sdf.format(new Date());
        }
        return selectAddress;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        System.out.println("map click");
        if(mapParent.equals("passanger") && (mapPurpose.equals("fromSelection") || mapPurpose.equals("toSelection"))) {
            selectAddress = getLocationAddress(latLng);
            selectLatLng = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title(selectAddress));

        }
    }
}