package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.ParseAnalytics;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

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
                startActivity(new Intent(MainActivity.this, RegisterOrLoginActivity.class));
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("testing");
//        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        Intent intent;
        if(ParseUser.getCurrentUser() == null) {
            intent = new Intent(this, RegisterOrLoginActivity.class);
            startActivity(intent);
        }
        else {
            String userRole = ParseUser.getCurrentUser().getString("role");
            if(userRole.equals("admin")) {
                startActivity(new Intent(this, AdminActivity.class));
            }
            else if(userRole.equals("passanger")) {
                startActivity(new Intent(this, PassangerActivity.class));
            }
            else if(userRole.equals("driver")) {
                startActivity(new Intent(this, DriverActivity.class));
            }

//            TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
//            titleTextView.setText("Hi "+ParseUser.getCurrentUser().getUsername());
        }


    }




}