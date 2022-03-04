package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PassengerActivity extends AppCompatActivity implements View.OnClickListener {

    static String fromAddress = "";
    static LatLng fromLatLng = null;
    static String toAddress = "";
    static LatLng toLatLng = null;
    static ParseObject fromBusstop = null;
    static ParseObject toBusstop = null;

    Button fromLocateOnMapButton;
    Button toLocateOnMapButton;
    Button fromSelectYourLocationButton;
    Button toSelectYourLocationButton;
//    ConstraintLayout passengerConstraintLayout;
    LinearLayout bgLinearLayout;
    List<ParseObject> allBusstops = null;
    List<String> busstopSearchResults = null;
    ArrayAdapter<String> searchArrayAdapter;
    ListView fromSearchListView;
    ListView toSearchListView;
    SearchView fromSearchView;
    SearchView toSearchView;
//    TextView fromAddrTextView;
//    TextView toAddrTextView;
    Button goButton;

    List<ParseObject> filteredBuses = null;


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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        System.out.println("passenger activity");

        fromLocateOnMapButton = (Button) findViewById(R.id.fromLocateOnMapButton);
        toLocateOnMapButton = (Button) findViewById(R.id.toLocateOnMapButton);
        fromSelectYourLocationButton = (Button) findViewById(R.id.fromSelectYourLocationButton);
        toSelectYourLocationButton = (Button) findViewById(R.id.toSelectYourLocationButton);
        bgLinearLayout = (LinearLayout) findViewById(R.id.bgLinearLayout);
        fromSearchListView = (ListView) findViewById(R.id.fromSearchListView);
        toSearchListView = (ListView) findViewById(R.id.toSearchListView);
        fromSearchView = (SearchView) findViewById(R.id.fromSearchView);
        toSearchView = (SearchView) findViewById(R.id.toSearchView);
        goButton = (Button) findViewById(R.id.goButton);
        bgLinearLayout.setOnClickListener(this);
        fromSearchView.setOnClickListener(this);
        toSearchView.setOnClickListener(this);

        fromLocateOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiService.futureFeature();
//                Intent intent = new Intent(getApplicationContext(), PassengerMapsActivity.class);
//                intent.putExtra("purpose","fromSelection");
//                startActivity(intent);
            }
        });
        toLocateOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiService.futureFeature();
//                Intent intent = new Intent(getApplicationContext(), PassengerMapsActivity.class);
//                intent.putExtra("purpose","toSelection");
//                startActivity(intent);
            }
        });
        fromSelectYourLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiService.futureFeature();
//                Intent intent = new Intent(getApplicationContext(), PassengerMapsActivity.class);
//                intent.putExtra("purpose","fromGetLocation");
//                startActivity(intent);
            }
        });
        toSelectYourLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiService.futureFeature();
//                Intent intent = new Intent(getApplicationContext(), PassengerMapsActivity.class);
//                intent.putExtra("purpose","toGetLocation");
//                startActivity(intent);
            }
        });

        busstopSearchResults = new ArrayList<String>();
        allBusstops = ApiService.getAllBusstops(null);
        if(allBusstops != null) {
            for (ParseObject busstop : allBusstops) {
                busstopSearchResults.add(busstop.getString("name"));
            }
        }
        else {
            System.out.println("busstops is null");
        }
        searchArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, busstopSearchResults);
        fromSearchListView.setAdapter(searchArrayAdapter);
        toSearchListView.setAdapter(searchArrayAdapter);

        fromSearchListView.setVisibility(View.GONE);
        toSearchListView.setVisibility(View.GONE);

        fromSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("submit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(fromSearchListView.getVisibility() == View.GONE)
                    fromSearchListView.setVisibility(View.VISIBLE);
                searchArrayAdapter.getFilter().filter(s);
                return false;
            }
        });

        fromSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                fromSearchListView.setVisibility(View.GONE);
                return false;
            }
        });


        toSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("submit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(toSearchListView.getVisibility() == View.GONE)
                    toSearchListView.setVisibility(View.VISIBLE);
                searchArrayAdapter.getFilter().filter(s);
                return false;
            }
        });

        toSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toSearchListView.setVisibility(View.GONE);
                return false;
            }
        });

        fromSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long l) {
                System.out.println(busstopSearchResults.indexOf(searchArrayAdapter.getItemId(pos)));
                fromBusstop = allBusstops.get(busstopSearchResults.indexOf(searchArrayAdapter.getItem(pos)));
                fromSearchView.setQuery(fromBusstop.getString("name"), false);
                fromSearchListView.setVisibility(View.GONE);
//                fromSearchView.onActionViewCollapsed();

            }
        });

        toSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long l) {
                System.out.println(busstopSearchResults.indexOf(searchArrayAdapter.getItem(pos)));
                toBusstop = allBusstops.get(busstopSearchResults.indexOf(searchArrayAdapter.getItem(pos)));
                toSearchView.setQuery(toBusstop.getString("name"), false);
                toSearchListView.setVisibility(View.GONE);
//                toSearchView.onActionViewCollapsed();
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromBusstop != null && toBusstop != null ) {
                    Intent intent = new Intent(getApplicationContext(),FilteredBusesActivity.class);
                    startActivity(intent);
//                    filteredBuses = ApiService.getFilteredBuses(fromBusstop, toBusstop);
//                    System.out.println(filteredBuses);
//                    if(filteredBuses != null && filteredBuses.size() > 0) {
//                        startActivity(new Intent(getApplicationContext(), TrackingActivity.class));
//                    }
//                    else {
//                        Toast.makeText(getApplicationContext(), "No buses available", Toast.LENGTH_SHORT).show();
//                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "from bustop and to bustop must be selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bgLinearLayout) {
            View currentFocus = this.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        else if(view.getId() == R.id.fromSearchView) {
            fromSearchView.setIconified(false);
        }
        else if(view.getId() == R.id.toSearchView) {
            toSearchView.setIconified(false);
        }
    }


}