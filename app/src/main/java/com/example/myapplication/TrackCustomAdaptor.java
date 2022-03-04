package com.example.myapplication;
import static com.parse.Parse.getApplicationContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class TrackCustomAdaptor  extends BaseAdapter {

    Context context;
//    List<ParseObject> stopArrayList;
    List<Integer> distancesBtwBustops;
    ArrayList<Short> timeList;
    ArrayList<String> busStopsNo;
    List<String> timings;
    List<ParseObject> busstops;
    LayoutInflater layoutInflater;
    ParseObject bus;
    int currentBusstopIndex;
    boolean curDirection;
    double busDistanceCovered;
//    boolean intializingDone = false;
//    float scaling = (float)0.1;

    public TrackCustomAdaptor(Context context,ParseObject bus, List<ParseObject> busstops, List<Integer> distancesBtwBustops){
        this.context = context;
        this.busstops = busstops;
        this.bus = bus;
        this.busDistanceCovered = bus.getDouble("distanceCovered");
        this.curDirection = bus.getBoolean("curDirection");
        this.currentBusstopIndex = bus.getInt("nextBusstopIndex");
        this.distancesBtwBustops = distancesBtwBustops;
        this.timings = (List<String>) bus.get("timings");
        layoutInflater = LayoutInflater.from(context);
    }

    public void checkAndNotifyChanges(int nextBusstopIndex, double distanceCovered) {
        System.out.println("cur index "+ currentBusstopIndex);
        if(currentBusstopIndex != nextBusstopIndex) {
            System.out.println("busstop reached");
            Toast.makeText(getApplicationContext(), "reached "+busstops.get(currentBusstopIndex).getString("name"), Toast.LENGTH_SHORT).show();
            currentBusstopIndex = nextBusstopIndex;

        }
        busDistanceCovered = distanceCovered;
        timings = (List<String>) bus.get("timings");
        System.out.println("dist covered "+busDistanceCovered);
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return busstops.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        System.out.println("get view");
        view = layoutInflater.inflate(R.layout.tracking_stop_item_view,null);

        TextView stopNameTextView  = (TextView) view.findViewById(R.id.stopNameTextView);
        stopNameTextView.setText(busstops.get(i).getString("name"));

        TextView timeTextView = (TextView) view.findViewById(R.id.arrivalTimeTextView);
        timeTextView.setText(timings.get(i));


        ConstraintLayout roadBlackConstraintLayout = (ConstraintLayout) view.findViewById(R.id.roadBlackConstraintLayout) ;
        ConstraintLayout.LayoutParams roadBlackLayoutParams = (ConstraintLayout.LayoutParams) roadBlackConstraintLayout.getLayoutParams();


        int lengthOfTrackItem = (int) (distancesBtwBustops.get(i)*AppConstants.TRACK_SCALING);
        System.out.println("length of track "+ lengthOfTrackItem);
        if(lengthOfTrackItem < 28)
            lengthOfTrackItem = 28;

        roadBlackLayoutParams.height= lengthOfTrackItem;

        ImageView busImageView = (ImageView) view.findViewById(R.id.busImageView);
        ConstraintLayout.LayoutParams busImageViewLayoutParams = (ConstraintLayout.LayoutParams) busImageView.getLayoutParams();

        if(busDistanceCovered >= AppConstants.NEAR_DISTANCE) {
            int busTopMargin = (int) ((int) busDistanceCovered * AppConstants.TRACK_SCALING);
            System.out.println("busmargin "+busTopMargin);
            if(busTopMargin> lengthOfTrackItem) {
                busImageViewLayoutParams.setMargins(0, lengthOfTrackItem-28, 0, 0);
            }
            else {
                busImageViewLayoutParams.setMargins(0, busTopMargin-28, 0, 0);
            }
        }
        else {
            busImageViewLayoutParams.setMargins(0, -28, 0, 0);
        }

        if(i == currentBusstopIndex) {
            busImageView.setVisibility(View.VISIBLE);
        }
        else if(busImageView.getVisibility() == View.VISIBLE) {
            busImageView.setVisibility(View.GONE);
        }

        //display busImage only for i == nextBusstopIndex
        return view;
    }


}