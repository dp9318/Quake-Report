package com.example.quakereport;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    ArrayList<Earthquake> earthquakes;
    EarthquakeAdapter madapter;

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=25";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);

        ListView earthquakeListView = findViewById(R.id.list);
        madapter = new EarthquakeAdapter(this,new ArrayList<Earthquake>());
        earthquakeListView.setAdapter(madapter);
        earthquakeListView.setOnItemClickListener((adapterView, view, position, l) -> {
            Earthquake currentEarthquake = earthquakes.get(position);
            Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
            startActivity(websiteIntent);
        });
    }

     private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>>{

        @Override
        protected List<Earthquake> doInBackground(String... urls) {
            if(urls.length<1 || urls[0]==null){
                return null;
            }
            List<Earthquake> result = QueryUtils.backgroundTask(urls[0]);
            return result;
        }
        @Override
        protected void onPostExecute(List<Earthquake> data){
            madapter.clear();
            if(data != null && !data.isEmpty()){
                madapter.addAll(data);
            }
        }
    }
}

