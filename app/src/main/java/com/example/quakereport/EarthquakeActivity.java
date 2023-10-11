package com.example.quakereport;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class EarthquakeActivity extends AppCompatActivity {
List<Earthquake> earthquakes = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    EarthquakeAdapter madapter;

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=4.5&limit=30";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo currentNetwork = connectivityManager.getActiveNetworkInfo();
        

        Thread backgroundThread = new Thread(() -> {
            try {

                runOnUiThread(() -> {
                    ProgressBar progressBarMain = findViewById(R.id.progress_bar_main);
                    progressBarMain.setVisibility(View.VISIBLE);
                    TextView loadingText = findViewById(R.id.loading_text);
                    loadingText.setVisibility(View.VISIBLE);
                });

                List<Earthquake> Earthquakes = QueryUtils.backgroundTask(USGS_REQUEST_URL); // Fetch data

                runOnUiThread(() ->{

                });
                earthquakes.addAll(Earthquakes);

                // Use the handler to update the UI on the main thread
                handler.post(() -> {
                    ProgressBar progressBarMain = findViewById(R.id.progress_bar_main);
                    progressBarMain.setVisibility(View.GONE);
                    TextView loadingText = findViewById(R.id.loading_text);
                    loadingText.setVisibility(View.GONE);
                    // Set empty state text if there are no earthquakes


                    madapter.clear(); // Clear the adapter
                    madapter.addAll(earthquakes); // Add the new data to the adapter
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        backgroundThread.start();


        ListView earthquakeListView = findViewById(R.id.list);
        madapter = new EarthquakeAdapter(this, new ArrayList<>());
        earthquakeListView.setAdapter(madapter);
        earthquakeListView.setOnItemClickListener((adapterView, view, position, l) -> {
            Earthquake currentEarthquake = earthquakes.get(position);
            Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
            startActivity(websiteIntent);
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        madapter.clear();
    }

}

