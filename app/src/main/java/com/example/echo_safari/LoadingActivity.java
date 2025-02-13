package com.example.echo_safari;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.echo_safari.LoginActivity;

public class LoadingActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading); // Make sure this is the correct layout

        // Initialize the ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // Start the simulated loading process
        startLoading();
    }

    private void startLoading() {
        // Show the ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Simulate a loading process (e.g., fetching data or preparing the app)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simulate a network operation (e.g., login check, loading data)
                    Thread.sleep(5000); // Simulating a 3-second loading time

                    // Once loading is complete, run this on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Hide the ProgressBar
                            progressBar.setVisibility(View.GONE);

                            // Optionally, show a message
                            Toast.makeText(LoadingActivity.this, "Loading Complete", Toast.LENGTH_SHORT).show();

                            // Navigate to the LoginActivity after the delay
                            Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                            startActivity(intent);

                            // Close this activity so the user can't go back to the loading screen
                            finish();
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
