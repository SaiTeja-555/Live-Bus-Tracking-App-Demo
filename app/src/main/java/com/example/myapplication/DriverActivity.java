package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class DriverActivity extends AppCompatActivity implements View.OnClickListener {

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
                if(getBusStatus()) {
                    stopService(locationServiceIntent);
                    ApiService.toggleBusStatus(DriverActivity.curBus);
                }
                ParseUser.logOut();
                startActivity(new Intent(DriverActivity.this, LoginActivity.class));
                return true;
            default:
                break;
        }
        return false;
    }



    TextView statusTextView;
    SwitchCompat locationSwitch;
    Button goToMapButton;
    Button resetButton;
    static ParseObject curBus;
    Intent locationServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        System.out.println("driver act");
        curBus = ApiService.getCurrentBus();

        locationSwitch = (SwitchCompat) findViewById(R.id.locationSwitch);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        goToMapButton = (Button) findViewById(R.id.goToMapButton);
//        resetButton = (Button) findViewById(R.id.resetButton);
        locationServiceIntent = new Intent(this, LocationService.class);

        locationSwitch.setOnClickListener(this);
        updateDriverViews();

//        resetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Service.resetBusDirection(curBus);
//            }
//        });
    }

    public void updateDriverViews() {
        System.out.println("calling update");
        if(getBusStatus()) {
            statusTextView.setText("Active");
            statusTextView.setTextColor(Color.GREEN);
            locationSwitch.setChecked(true);
            goToMapButton.setVisibility(View.VISIBLE);
//            resetButton.setVisibility(View.VISIBLE);
        }
        else {
            statusTextView.setText("Inactive");
            statusTextView.setTextColor(Color.RED);
            locationSwitch.setChecked(false);
            goToMapButton.setVisibility(View.INVISIBLE);
//            resetButton.setVisibility(View.INVISIBLE);
        }
        System.out.println("after get");
    }


    public boolean getBusStatus() {
        return DriverActivity.curBus.getBoolean("isActive");
    }



    public void goToMap(View view) {
        Intent intent = new Intent(getApplicationContext(), DriverMapsActivity.class);
        intent.putExtra("purpose", "location");
        startActivity(intent);
    }

    public boolean areLocationServicesGranted() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkPermissionsAndStartLocationService() {
        if(areLocationServicesGranted()) {
            startService(locationServiceIntent);
        }
        else {

            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                    AppConstants.LOCATION_SERVICE_REQUEST_TAG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(AppConstants.LOCATION_SERVICE_REQUEST_TAG == requestCode){
            checkPermissionsAndStartLocationService();
        }
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.locationSwitch) {
            //uncomment this code in production environment
//            if(!getBusStatus()) {
//                //switch on
//                if(ApiService.checkBusStartAndUpdateBusFields(DriverActivity.curBus)) {
//                    System.out.println(("started bus fields"));
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "You can start location service only at any terminal", Toast.LENGTH_SHORT).show();
//                    // need to write code to start service even if he did not reach terminal by asking him the curDirection manually
//                    locationSwitch.setChecked(false);
//                    return;
//                }
//            }
            ApiService.toggleBusStatus(DriverActivity.curBus);
            if(getBusStatus()) {
                updateDriverViews();
                requestLocation();
                checkPermissionsAndStartLocationService();
            }
            else {
                //stop location service
                updateDriverViews();
                stopService(locationServiceIntent);
            }

        }
    }

    public void requestLocation() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(DriverActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(DriverActivity.this,AppConstants.REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppConstants.REQUEST_CHECK_SETTINGS) {

            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, "GPS is tured on", Toast.LENGTH_SHORT).show();
                    break;

                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "GPS required to be tured on", Toast.LENGTH_SHORT).show();
                    requestLocation();
            }
        }
    }

}