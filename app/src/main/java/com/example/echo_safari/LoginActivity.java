package com.example.echo_safari;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText edUserName, edPassword;
    Button btnLogin, btnProfile;
    TextView tv;
    boolean isValidUser = false;  // Track if user entered correct credentials

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        edUserName = findViewById(R.id.editTextLoginUserName);
        edPassword = findViewById(R.id.editTextLoginPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        btnProfile = findViewById(R.id.buttonProfile);
        tv = findViewById(R.id.textViewNewUser);

        // Handle Login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndProceed(true);  // True -> Navigate to HomeActivity
            }
        });

        // Handle Profile button click
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndProceed(false);  // False -> Navigate to ProfileActivity
            }
        });

        // Handle Register text click
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    // Validate user credentials and navigate accordingly
    private void validateAndProceed(boolean isLogin) {
        String username = edUserName.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
            return;  // Stop further execution
        }

        // Initialize the Database instance
        Database db = new Database(getApplicationContext());

        // Validate login credentials
        if (db.login(username, password)) {
            // Save login session
            SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.apply();

            // Navigate to the correct activity
            Intent intent = isLogin ? new Intent(LoginActivity.this, HomeActivity.class)
                    : new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
    }
}
