package com.example.quakereport;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {
    ArrayList<Earthquake> earthquakes;
    static String quake_url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2023-09-6&minmag=3.0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);



        QueryUtils task = new QueryUtils(quake_url);
        Runnable runnable = task::backgroundTask;
        Thread thread = new Thread(runnable);
        thread.start();

        earthquakes = task.getEarthquakes();
        if(!earthquakes.isEmpty()){
            for (int i = 0; i < earthquakes.size(); i++) {
                Earthquake earthquake = earthquakes.get(i);
                Log.d("EarthquakeList", "Element " + i + ": " + earthquake.toString());
            }
        }
        else {
            Log.v("dp", "list is empty" + earthquakes);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Runnable runLoadOnScreen = () -> {
            ListView earthquakeListView = findViewById(R.id.list);
            EarthquakeAdapter adapter = new EarthquakeAdapter(this,earthquakes);
            earthquakeListView.setAdapter(adapter);
            earthquakeListView.setOnItemClickListener((adapterView, view, position, l) -> {
                Earthquake currentEarthquake = earthquakes.get(position);
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(websiteIntent);
            });
        };
        Thread threadLoadOnScreen = new Thread(runLoadOnScreen);
        threadLoadOnScreen.start();
    }
}
