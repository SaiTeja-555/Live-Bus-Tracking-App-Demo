package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.parse.ParseObject;

import java.util.List;

public class FilteredBusesCustomAdaptor extends BaseAdapter {
    Context context;
    List<ParseObject> filteredBuses;
    LayoutInflater layoutInflater;

    public FilteredBusesCustomAdaptor(Context context, List<ParseObject> filteredBuses){
        this.context = context;
        this.filteredBuses = filteredBuses;
        layoutInflater = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return filteredBuses.size();
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

        view = layoutInflater.inflate(R.layout.filtered_buses_item_view,null);

        TextView busNumberTextView = (TextView) view.findViewById(R.id.busNumberTextView );
        TextView arrivalTimeTextView = (TextView) view.findViewById(R.id.arrivalTimeTextView );
        TextView departTimeTextView  = (TextView) view.findViewById(R.id.departTimeTextView );
        TextView busStatusTextView  = (TextView) view.findViewById(R.id.busStatusTextView);

        ParseObject bus = filteredBuses.get(i);
        Boolean busStatus = bus.getBoolean("isActive");
        String busNumber = bus.getString("busNumber");
        List<String> busstopNumbers = ApiService.getOrderedBusstopNumbers(bus);
        List<String> busTimings = (List<String>) bus.get("timings");
        int fromBusstopIndex = busstopNumbers.indexOf(PassengerActivity.fromBusstop.getString("number"));
        int toBusstopIndex = busstopNumbers.indexOf(PassengerActivity.toBusstop.getString("number"));


        Drawable busStatusActiveDrawable = AppCompatResources.getDrawable(context.getApplicationContext(), R.drawable.bus_status_active_custom);
        Drawable busStatusInactiveDrawable = AppCompatResources.getDrawable(context.getApplicationContext(), R.drawable.bus_status_inactive_custom);
        if(busStatus) {
            busStatusTextView.setText("Active");
            busStatusTextView.setBackground(busStatusActiveDrawable);
        }
        else {
            busStatusTextView.setText("Inactive");
            busStatusTextView.setBackground(busStatusInactiveDrawable);
        }
        busNumberTextView.setText(busNumber);
        arrivalTimeTextView.setText(busTimings.get(fromBusstopIndex));
        departTimeTextView.setText(busTimings.get(toBusstopIndex));

        return view;
    }
}
