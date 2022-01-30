package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

//    CoordinatorLayout bgCoordinatorLayout;
//    ConstraintLayout bgConstraintLayout;
    LinearLayout bgLinearLayout;
    EditText confirmPasswordEditText;

    public void signup(View view) {
        EditText usernameEditText = (EditText) findViewById(R.id.fnameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        if (usernameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
            Toast.makeText(this, "A username or password cannot be blank", Toast.LENGTH_SHORT).show();
        }
        else if(usernameEditText.getText().toString().length() < 3 || passwordEditText.getText().toString().length() < 3) {
            Toast.makeText(this, "A username or password cannot be too short", Toast.LENGTH_SHORT).show();
        }
        else if(!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            Toast.makeText(this, "Passwords are not matching", Toast.LENGTH_SHORT).show();
        }
        else {
            ParseUser user = new ParseUser();
            user.setUsername(usernameEditText.getText().toString());
            user.setPassword(passwordEditText.getText().toString());
            user.put("role", "passenger");

            user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("signUp", "successful");

                            ParseObject passenger = new ParseObject("Passanger");
                            passenger.put("name", user.getUsername());
                            passenger.put("user", user);

                            passenger.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Log.i("passenger creation", "successful");
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            });
        }
    }


    public void backToLogin(View view){
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        System.out.println("signup activity");
//        bgConstraintLayout = (ConstraintLayout) findViewById(R.id.bgSignupConstraintLayout);
        LinearLayout bgLinearLayout = (LinearLayout) findViewById(R.id.bgLinearLayout);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
//        bgConstraintLayout.setOnClickListener(this);
        bgLinearLayout.setOnClickListener(this);
        confirmPasswordEditText.setOnKeyListener(this);

    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signup(view);
        }
        return false;
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
    }
}