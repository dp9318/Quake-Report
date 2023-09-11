package com.example.quakereport;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class EarthquakeActivity extends AppCompatActivity {

List<Earthquake> earthquakes = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    EarthquakeAdapter madapter;



    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=25";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);


        Thread backgroundThread = new Thread(() -> {
            try {
                List<Earthquake> Earthquakes = QueryUtils.backgroundTask(USGS_REQUEST_URL); // Fetch data
                earthquakes.clear(); // Clear the existing data
                // Add the new data to the list
                earthquakes.addAll(Earthquakes);
                // Use the handler to update the UI on the main thread
                handler.post(() -> {
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

}

