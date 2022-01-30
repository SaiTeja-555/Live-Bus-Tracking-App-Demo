package com.example.myapplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class TrackCustomAdaptor  extends BaseAdapter {

    Context context;
    List<ParseObject> stopArrayList;
    List<Integer> distancesBtwBustops;
    ArrayList<Short> timeList;
    ArrayList<String> busStopsNo;
    LayoutInflater layoutInflater;
    ParseObject bus;
//    float scaling = (float)0.1;

    public TrackCustomAdaptor(Context context,ParseObject bus, List<ParseObject> arrayList){
        this.context = context;
        this.stopArrayList = arrayList;
        this.bus = bus;
        this.distancesBtwBustops = (List<Integer>) bus.get("distancesBtwBusstops");
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return stopArrayList.size();
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

        stopNameTextView.setText(stopArrayList.get(i).getString("name"));
        ConstraintLayout roadBlackConstraintLayout = (ConstraintLayout) view.findViewById(R.id.roadBlackConstraintLayout) ;
        ConstraintLayout.LayoutParams roadBlackLayoutParams = (ConstraintLayout.LayoutParams) roadBlackConstraintLayout.getLayoutParams();
        ImageView busImageView = (ImageView) view.findViewById(R.id.busImageView);

        System.out.println(distancesBtwBustops.get(i)*AppConstants.TRACK_SCALING);
        int lengthOfTrackItem = (int) (distancesBtwBustops.get(i)*AppConstants.TRACK_SCALING);
        if(lengthOfTrackItem > 28)
            roadBlackLayoutParams.height= lengthOfTrackItem;

//        if(stopNameTextView.getText().toString()=="bpl"){
//            busImageView = (ImageView) view.findViewById(R.id.busImageView);
//            busImageView.setImageResource(R.drawable.redbus);
//            RelativeLayout.LayoutParams busLayoutParams = (RelativeLayout.LayoutParams) busImageView.getLayoutParams();
//            busLayoutParams.setMargins(0,600,0,0);
//        }
        if(i == TrackingActivity.currentBusstopIndex) {
            busImageView.setVisibility(View.VISIBLE);
        }
        else if(busImageView.getVisibility() == View.VISIBLE) {
            busImageView.setVisibility(View.GONE);
        }

        //display busImage only for i == nextBusstopIndex
        return view;
    }
}