package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PassangerActivity extends AppCompatActivity implements View.OnClickListener {

    static String fromAddress = "";
    static LatLng fromLatLng = null;
    static String toAddress = "";
    static LatLng toLatLng = null;
    EditText fromAddressEditText;
    EditText toAddressEditText;
    Button fromLocateOnMapButton;
    Button toLocateOnMapButton;
    Button fromSelectYourLocationButton;
    Button toSelectYourLocationButton;
    ConstraintLayout passengerConstraintLayout;
    List<ParseObject> allBusstops = null;
    List<String> busstopSearchResults = null;
    ArrayAdapter<String> searchArrayAdapter;
    ListView fromSearchListView;
    ListView toSearchListView;
    SearchView fromSearchView;
    SearchView toSearchView;
    ParseObject fromBusstop = null;
    ParseObject toBusstop = null;

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
                startActivity(new Intent(PassangerActivity.this, RegisterOrLoginActivity.class));
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passanger);

        fromLocateOnMapButton = (Button) findViewById(R.id.fromLocateOnMapButton);
        toLocateOnMapButton = (Button) findViewById(R.id.toLocateOnMapButton);
        fromSelectYourLocationButton = (Button) findViewById(R.id.fromSelectYourLocationButton);
        toSelectYourLocationButton = (Button) findViewById(R.id.toSelectYourLocationButton);
        passengerConstraintLayout = (ConstraintLayout) findViewById(R.id.passengerConstraintLayout);
        fromSearchListView = (ListView) findViewById(R.id.fromSearchListView);
        toSearchListView = (ListView) findViewById(R.id.toSearchListView);

        passengerConstraintLayout.setOnClickListener(this);

        fromAddressEditText.setText(fromAddress);
        toAddressEditText.setText(toAddress);
        Log.i("from",fromAddress);
        Log.i("to",toAddress);
//        Log.i("fromLoc",fromLatLng);
//        Log.i("toLoc",toLatLng.toString());
        fromLocateOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DriverMapsActivity.class);
                intent.putExtra("parent", "passanger");
                intent.putExtra("purpose","fromSelection");
                startActivity(intent);
            }
        });
        toLocateOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DriverMapsActivity.class);
                intent.putExtra("parent", "passanger");
                intent.putExtra("purpose","toSelection");
                startActivity(intent);
            }
        });
        fromSelectYourLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("fromselect");
                Intent intent = new Intent(getApplicationContext(), DriverMapsActivity.class);
                intent.putExtra("parent", "passanger");
                intent.putExtra("purpose","fromGetLocation");
                startActivity(intent);
            }
        });
        toSelectYourLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DriverMapsActivity.class);
                intent.putExtra("parent", "passanger");
                intent.putExtra("purpose","toGetLocation");
                startActivity(intent);
            }
        });

        busstopSearchResults = new ArrayList<String>();
        getAllBusstops();
        searchArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, busstopSearchResults);
        fromSearchListView.setAdapter(searchArrayAdapter);
        toSearchListView.setAdapter(searchArrayAdapter);

        fromSearchListView.setVisibility(View.INVISIBLE);
        toSearchListView.setVisibility(View.INVISIBLE);

        fromSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromSearchListView.setVisibility(View.VISIBLE);
            }
        });
        fromSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchArrayAdapter.getFilter().filter(s);
                return false;
            }
        });

        toSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSearchListView.setVisibility(View.VISIBLE);
            }
        });
        toSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchArrayAdapter.getFilter().filter(s);
                return false;
            }
        });

        fromSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fromBusstop = allBusstops.get(i);
                fromSearchListView.setVisibility(View.INVISIBLE);
            }
        });

        toSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toBusstop = allBusstops.get(i);
                toSearchListView.setVisibility(View.INVISIBLE);

            }
        });




    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.passengerConstraintLayout) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void getAllBusstops() {
        ParseQuery query = ParseQuery.getQuery("Busstop");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List objects, ParseException e) {
                if(e == null) {
                    allBusstops = objects;
                    for(ParseObject busstop: allBusstops) {
                        busstopSearchResults.add(busstop.getString("name"));
                    }
                }
                else {
                    e.printStackTrace();
                }
            }
        });

    }
}