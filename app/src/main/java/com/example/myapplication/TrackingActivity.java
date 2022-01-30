package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseObject;

import java.util.List;

public class TrackingActivity extends AppCompatActivity {

    List<ParseObject> stops;
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


                double distance = bus.getDouble("distanceCovered");
                int nextBusstopIndex = bus.getInt("nextBusstopIndex");

                if(nextBusstopIndex != currentBusstopIndex) {
                    System.out.println("reached busstop");
                    Toast.makeText(getApplicationContext(), "reached "+stops.get(currentBusstopIndex).getString("name"), Toast.LENGTH_SHORT).show();
                    currentBusstopIndex = nextBusstopIndex;
                }
                if(distance >= AppConstants.NEAR_DISTANCE) {
                    busImageViewLayoutParams.setMargins(0, (int) ((int)distance*AppConstants.TRACK_SCALING), 0, 0);
                }
                else {
                    busImageViewLayoutParams.setMargins(0, 0, 0, 0);
                }


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

        busImageView = (ImageView) findViewById(R.id.busImageView);
        busImageViewLayoutParams = (ConstraintLayout.LayoutParams) busImageView.getLayoutParams();
        bus = PassengerActivity.filteredBuses.get(0);
        currentBusstopIndex = bus.getInt("nextBusstopIndex");
        stops = ApiService.getAllBusstops(bus);

        track = (ListView) findViewById(R.id.trackListView);
        trackCustomAdaptor = new TrackCustomAdaptor(getApplicationContext(), bus, stops);
        track.setAdapter(trackCustomAdaptor);
    }


}