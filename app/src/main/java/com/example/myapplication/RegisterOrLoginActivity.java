package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterOrLoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Boolean signUpModeActive = true;
    TextView swapSubmitTextView;
    Button submitButton;
    EditText passwordEditText;
    ScrollView backgroundConstraintLayout;
    Switch aSwitch;

    public void authenticated() {
        startActivity(new Intent(RegisterOrLoginActivity.this, MainActivity.class));
    }

    public void submit(View view) {
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        if(signUpModeActive) {
            if (usernameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
                Toast.makeText(this, "A username or password cannot be blank", Toast.LENGTH_SHORT).show();
            } else {
                ParseUser user = new ParseUser();
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("signUp", "successful");
                            signUpModeActive = false;
                            submit(view); //login

                        } else {
                            Toast.makeText(RegisterOrLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else {
            ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user != null)  {
                        Log.i("login","successful");
                        authenticated();
                    }
                    else {
                        Toast.makeText(RegisterOrLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_or_login);
        System.out.println("register");
        swapSubmitTextView = (TextView) findViewById(R.id.swapSubmitTextView);
        submitButton = (Button) findViewById(R.id.submitButton);
        aSwitch = (Switch) findViewById(R.id.roleSwitch);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        backgroundConstraintLayout = (ScrollView) findViewById(R.id.backgroundConstraintLayout);
        swapSubmitTextView.setOnClickListener(this);
        backgroundConstraintLayout.setOnClickListener(this);
        passwordEditText.setOnKeyListener(this);

//        ParseInstallation.getCurrentInstallation().saveInBackground();

    }
    public void signup(View view){
        Intent intent = new Intent(getApplicationContext(),CreateAccount.class);
        startActivity(intent);
    }

    /*@Override
    public void onClick(View view) {
        if(view.getId() == R.id.swapSubmitTextView) {

            if(signUpModeActive) {

                signUpModeActive = false;
                submitButton.setText("Login");
                swapSubmitTextView.setText("or Sign Up");

            }
            else {
                signUpModeActive = true;
                submitButton.setText("Sign Up");
                swapSubmitTextView.setText("or Login");
            }

        }
        else if(view.getId() == R.id.backgroundConstraintLayout) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }


    }*/

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            submit(submitButton);
//            background
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(),CreateAccount.class);
        startActivity(intent);

    }
}