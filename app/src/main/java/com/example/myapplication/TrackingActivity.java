package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class TrackingActivity extends AppCompatActivity {

    List<ParseObject> stops;
    List<String> busstopNumbers;
    List<Integer> distancesBtwBustops;
    ListView track;
    ParseObject bus;
    TrackCustomAdaptor trackCustomAdaptor;

    Handler busTrackingHandler = new Handler();
    Runnable busTrackingRunnable;
    int trackingDelay = 5*1000;
    int busImageOffset = 28;
    ImageView busImageView;
    ConstraintLayout.LayoutParams busImageViewLayoutParams;
    static int currentBusstopIndex;


    @Override
    protected void onResume() {
        //start handler as activity become visible
        System.out.println("on resume");
        busTrackingHandler.postDelayed( busTrackingRunnable = new Runnable() {
            public void run() {
                System.out.println("run");
                //do something
                ParseQuery busQuery = ParseQuery.getQuery("Bus");

                try {
                    bus = busQuery.get(bus.getObjectId());
                    System.out.println(bus.getDouble("distanceCovered"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                trackCustomAdaptor.checkAndNotifyChanges(bus.getInt("nextBusstopIndex"), bus.getDouble("distanceCovered"));
//                double distance = bus.getDouble("distanceCovered");
//                int nextBusstopIndex = bus.getInt("nextBusstopIndex");
//
//                if(nextBusstopIndex != currentBusstopIndex) {
//                    System.out.println("reached busstop");
//                    Toast.makeText(getApplicationContext(), "reached "+stops.get(currentBusstopIndex).getString("name"), Toast.LENGTH_SHORT).show();
//                    currentBusstopIndex = nextBusstopIndex;
//                }
//                if(distance >= AppConstants.NEAR_DISTANCE) {
//                    busImageViewLayoutParams.setMargins(0, (int) ((int)distance*AppConstants.TRACK_SCALING), 0, 0);
//                }
//                else {
//                    busImageViewLayoutParams.setMargins(0, 0, 0, 0);
//                }


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
        setContentView(R.layout.activity_tracking);
        System.out.println("tracking activity");

        Intent intent = getIntent();
        int index = intent.getIntExtra("index", -1);
        bus = FilteredBusesActivity.filteredBuses.get(index);
        currentBusstopIndex = bus.getInt("nextBusstopIndex");
        busstopNumbers = ApiService.getOrderedBusstopNumbers(bus);
        stops = ApiService.getBusstops(busstopNumbers);
        distancesBtwBustops = ApiService.getDistancesBtwBusstopsBasedOnDirection(bus);

        track = (ListView) findViewById(R.id.trackListView);
        trackCustomAdaptor = new TrackCustomAdaptor(getApplicationContext(), bus, stops, distancesBtwBustops);
        track.setAdapter(trackCustomAdaptor);
    }


}