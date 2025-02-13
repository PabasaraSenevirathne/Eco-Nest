package com.example.echo_safari;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullname, tvUsername, tvEmail, tvContact;
    private EditText etFullname, etEmail, etContact;
    private Button btnUpdate, btnDelete, btnLoginProfile;
    private String username;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI elements
        tvFullname = findViewById(R.id.tvFullname);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvContact = findViewById(R.id.tvContact);

        etFullname = findViewById(R.id.etFullname);
        etEmail = findViewById(R.id.etEmail);
        etContact = findViewById(R.id.etContact);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnLoginProfile = findViewById(R.id.buttonLoginProfile);

        // Retrieve logged-in username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        // Debugging: Check if username is retrieved correctly
        Log.d("ProfileActivity", "Logged-in username: " + username);

        db = new Database(this);
        loadUserDetails();

        // Set button actions
        btnUpdate.setOnClickListener(v -> updateUserDetails());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        btnLoginProfile.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void loadUserDetails() {
        try (Cursor cursor = db.getUserDetails(username)) {
            if (cursor != null && cursor.moveToFirst()) {
                String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String contact = cursor.getString(cursor.getColumnIndexOrThrow("contact"));

                // Debugging: Log user details
                Log.d("ProfileActivity", "User details fetched: " + fullname + ", " + email + ", " + contact);

                // Set data to UI components
                tvFullname.setText("Name: " + fullname);
                tvUsername.setText("Username: " + username);
                tvEmail.setText("Email: " + email);
                tvContact.setText("Contact: " + contact);

                // Set editable fields
                etFullname.setText(fullname);
                etEmail.setText(email);
                etContact.setText(contact);
            } else {
                Log.e("ProfileActivity", "Cursor is empty or user not found.");
            }
        } catch (Exception e) {
            Log.e("ProfileActivity", "Error loading user details", e);
            Toast.makeText(this, "Failed to load user details", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserDetails() {
        String updatedFullname = etFullname.getText().toString().trim();
        String updatedEmail = etEmail.getText().toString().trim();
        String updatedContact = etContact.getText().toString().trim();

        // Validate input
        if (updatedFullname.isEmpty() || updatedEmail.isEmpty() || updatedContact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(updatedEmail).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!updatedContact.matches("\\d{10}")) { // Assuming contact number must be 10 digits
            Toast.makeText(this, "Invalid contact number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user details in the database
        ContentValues values = new ContentValues();
        values.put("fullname", updatedFullname);
        values.put("email", updatedEmail);
        values.put("contact", updatedContact);

        try (SQLiteDatabase dbWritable = db.getWritableDatabase()) {
            int rowsAffected = dbWritable.update("users", values, "username = ?", new String[]{username});

            // Debugging: Check if update was successful
            Log.d("ProfileActivity", "Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                // Update UI
                tvFullname.setText("Name: " + updatedFullname);
                tvEmail.setText("Email: " + updatedEmail);
                tvContact.setText("Contact: " + updatedContact);
                Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ProfileActivity", "Database update error", e);
            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", (dialog, which) -> deleteAccount())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAccount() {
        try (SQLiteDatabase dbWritable = db.getWritableDatabase()) {
            int rowsDeleted = dbWritable.delete("users", "username = ?", new String[]{username});

            Log.d("ProfileActivity", "Rows deleted: " + rowsDeleted);

            if (rowsDeleted > 0) {
                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                logoutUser(); // Automatically log out after deleting the account
            } else {
                Toast.makeText(this, "Error deleting account", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", null)
                .show();
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to LoginActivity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
