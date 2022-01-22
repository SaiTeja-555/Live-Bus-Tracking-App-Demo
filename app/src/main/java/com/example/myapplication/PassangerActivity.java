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
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;

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
    ConstraintLayout backgroundConstraintLayout;

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

        fromAddressEditText = (EditText) findViewById(R.id.fromAddrEditText);
        toAddressEditText = (EditText) findViewById(R.id.toAddrEditText);
        fromLocateOnMapButton = (Button) findViewById(R.id.fromLocateOnMapButton);
        toLocateOnMapButton = (Button) findViewById(R.id.toLocateOnMapButton);
        fromSelectYourLocationButton = (Button) findViewById(R.id.fromSelectYourLocationButton);
        toSelectYourLocationButton = (Button) findViewById(R.id.toSelectYourLocationButton);
        backgroundConstraintLayout = (ConstraintLayout) findViewById(R.id.backgroundConstraintLayout);
        backgroundConstraintLayout.setOnClickListener(this);

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


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.backgroundConstraintLayout) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}