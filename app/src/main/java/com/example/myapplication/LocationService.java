package com.example.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;

public class LocationService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;



    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("oncreate");
        initData();
    }

    protected void initData() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(AppConstants.UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = new FusedLocationProviderClient(getApplicationContext());
    }


    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location currentLocation = locationResult.getLastLocation();
            Log.d("Locations", currentLocation.getLatitude() + "," + currentLocation.getLongitude());

            ApiService.updateBusLocationAndDistanceCovered(currentLocation, DriverActivity.curBus);
            ApiService.checkAnyBusstopReachedAndUpdateBusFields(DriverActivity.curBus);
            ApiService.updateBusTimings(DriverActivity.curBus);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("onstartcommand");
        prepareForegroundNotification();

        startLocationUpdates();

        return START_STICKY;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("location permission is needed");
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());
    }


    private void prepareForegroundNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    AppConstants.CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                AppConstants.SERVICE_LOCATION_REQUEST_CODE,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentTitle(getString(R.string.app_notification_description))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(AppConstants.LOCATION_SERVICE_NOTIF_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
