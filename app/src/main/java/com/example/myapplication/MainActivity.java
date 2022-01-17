package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.parse.ParseAnalytics;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("testing");
//        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        if(ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, RegisterOrLoginActivity.class);
            startActivity(intent);
        }
        else {
            TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
            titleTextView.setText("Hi "+ParseUser.getCurrentUser().getUsername());
        }


    }




}