package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class DriverActivity extends AppCompatActivity implements View.OnClickListener {

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
                startActivity(new Intent(DriverActivity.this, RegisterOrLoginActivity.class));
                return true;
            default:
                break;
        }
        return false;
    }


    TextView statusTextView;
    Switch locationSwitch;
    Button goToMapButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        ParseUser.logOut();
        startActivity(new Intent(DriverActivity.this, RegisterOrLoginActivity.class));
        System.out.println("driver act");
        locationSwitch = (Switch) findViewById(R.id.locationSwitch);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        goToMapButton = (Button) findViewById(R.id.mapCenteringButton);
        locationSwitch.setOnClickListener(this);
        System.out.println("calling update");
        updateDriverViews();
        System.out.println("");



    }

    public void updateDriverViews() {
        System.out.println("calling get");
        if(getDriverLocationStatus()) {
            statusTextView.setText("Active");
            statusTextView.setTextColor(Color.GREEN);
            locationSwitch.setChecked(true);
            goToMapButton.setVisibility(View.VISIBLE);
        }
        else {
            statusTextView.setText("Inactive");
            statusTextView.setTextColor(Color.RED);
            locationSwitch.setChecked(false);
            goToMapButton.setVisibility(View.GONE);
        }
        System.out.println("after get");
    }


    public boolean getDriverLocationStatus() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Driver");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        System.out.println("query");
        try {
            return query.getFirst().getBoolean("isActive");
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        return query.getFirstInBackground().getResult().getBoolean("isActive");
        return false;
    }

    public void toggleDriverLocationStatus() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Driver");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject driver, ParseException e) {
                if(driver == null) {
                    Log.i("driver status query","failed");
                }
                else {
                    if(driver.getBoolean("isActive")) {
                        driver.put("isActive", false);
                    }
                    else {
                        driver.put("isActive", true);
                    }
                    driver.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            Log.i("driver status","toggled");
                        }
                    });
                }
            }
        });
    }

    public void goToMap(View view) {
        Intent intent = new Intent(getApplicationContext(), DriverMapsActivity.class);
        intent.putExtra("parent", "driver");
        intent.putExtra("purpose", "location");
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.locationSwitch) {
            toggleDriverLocationStatus();
            updateDriverViews();
        }
    }
}