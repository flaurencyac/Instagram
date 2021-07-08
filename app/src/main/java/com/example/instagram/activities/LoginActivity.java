package com.example.instagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;


public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Switch switchPasswordVisibility;
    private Boolean showPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // check if someone is signed in already
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        showPassword = false;
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        switchPasswordVisibility = findViewById(R.id.switchPasswordVisibility);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                // method to login the user
                login(username, password);
            }
        });
        switchPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick password visibility switch");
                if (showPassword == true) {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPassword = false;
                } else {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword = true;
                }
            }
        });

    }

    private void login(String username, String password) {
        Log.i(TAG, "Attempting to login user " +username);
        // TODO navigate to the main activity if the user signed in properly
        // LogInInBackground is preferred bc it executes this in the background thread
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            // if request succeeds exception e should be null
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login"+e, e);
                }
                // navigate to the main activity if the user has signed in properly
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
