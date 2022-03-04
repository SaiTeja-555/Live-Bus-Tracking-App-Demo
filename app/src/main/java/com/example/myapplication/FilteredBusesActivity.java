package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FilteredBusesActivity extends AppCompatActivity {

    static List<ParseObject> filteredBuses;
    ListView filteredBusesListView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logoutMenuItem:
                Log.i("logout","Logging out");
                ParseUser.logOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            case android.R.id.home:
                System.out.println("going back");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_buses);
        System.out.println("filtered buses activity");

        TextView listEmptyTextView = (TextView) findViewById(R.id.listEmptyTextView);
        TextView fromBusstopTextView = (TextView) findViewById(R.id.fromBusstopTextView);
        TextView toBusstopTextView = (TextView) findViewById(R.id.toBusstopTextView);

        fromBusstopTextView.setText(PassengerActivity.fromBusstop.getString("name"));
        toBusstopTextView.setText(PassengerActivity.toBusstop.getString("name"));

        filteredBuses = ApiService.getFilteredBuses(PassengerActivity.fromBusstop, PassengerActivity.toBusstop);
        if(filteredBuses != null && filteredBuses.size() > 0) {
            filteredBusesListView = (ListView) findViewById(R.id.filteredBusesListView);
            FilteredBusesCustomAdaptor filteredBusesAdaptor = new FilteredBusesCustomAdaptor(getApplicationContext(), filteredBuses);
            filteredBusesListView.setAdapter(filteredBusesAdaptor);

            filteredBusesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    System.out.println("item clicked");
                    Intent intent = new Intent(getApplicationContext(), TrackingActivity.class);
                    intent.putExtra("index", i);
                    startActivity(intent);
                }
            });
        }
        else {
            listEmptyTextView.setVisibility(View.VISIBLE);
        }
    }
}