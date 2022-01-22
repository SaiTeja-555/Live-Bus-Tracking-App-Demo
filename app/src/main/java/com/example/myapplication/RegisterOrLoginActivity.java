package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegisterOrLoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Boolean signUpModeActive = true;
    TextView swapSubmitTextView;
    Button submitButton;
    EditText passwordEditText;
    ConstraintLayout backgroundConstraintLayout;
    Switch aSwitch;
    String roleUser;
    TextView passangerSwitchTextView;
    TextView driverSwitchTextView;

    public void authenticated() {
        startActivity(new Intent(RegisterOrLoginActivity.this, MainActivity.class));
    }

    public void submit(View view) {

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        if(signUpModeActive) {
//            if (usernameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
//                Toast.makeText(this, "A username or password cannot be blank", Toast.LENGTH_SHORT).show();
//            } else {
                ParseUser user = new ParseUser();
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());

                roleUser = "passanger";
                if(aSwitch.isChecked()) {
                    roleUser = "driver";
                }
                user.put("role", roleUser);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("signUp", "successful");
                            signUpModeActive = false;
                            ParseObject obj;
                            if(user.get("role").equals("driver")) {
                                obj = new ParseObject("Driver");
                            }
                            else {
                                obj = new ParseObject("Passanger");
                            }
                            obj.put("name", user.getUsername());
                            obj.put("user", user);
                            obj.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Log.i("driver or passanger creation", "successful");
                                }
                            });

                            submit(view); //login

//                            ParseCloud.callFunctionInBackground("addUserRole",);

                        } else {
                            Toast.makeText(RegisterOrLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
        backgroundConstraintLayout = (ConstraintLayout) findViewById(R.id.backgroundConstraintLayout);
        passangerSwitchTextView = (TextView) findViewById(R.id.rolePassangerTextView);
        driverSwitchTextView = (TextView) findViewById(R.id.roleDriverTextView);
        swapSubmitTextView.setOnClickListener(this);
        backgroundConstraintLayout.setOnClickListener(this);
        passwordEditText.setOnKeyListener(this);

//        ParseInstallation.getCurrentInstallation().saveInBackground();

    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.swapSubmitTextView) {

            if(signUpModeActive) {

                signUpModeActive = false;
                submitButton.setText("Login");
                swapSubmitTextView.setText("or Sign Up");
                aSwitch.setVisibility(View.GONE);
                passangerSwitchTextView.setVisibility(View.GONE);
                driverSwitchTextView.setVisibility(View.GONE);

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


    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            submit(submitButton);
//            background
        }
        return false;
    }
}