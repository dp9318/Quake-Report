package com.example.quakereport;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import kotlin.text.UStringsKt;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public class QueryUtils {

    public static List<Earthquake> backgroundTask(String quakeUrl){
        URL url = createUrl(quakeUrl);
        String jsonResponse = "";
        try {
            jsonResponse = httpRequest(url);
        } catch (IOException e) {
            Log.e("QueryUtils", "Error making HTTP request", e);
        }
        List<Earthquake> earthquakes = extractEarthquakes(jsonResponse);
        return earthquakes;
    }

    private static URL createUrl(String Url_str) {
        URL url = null;
        try {
            url = new URL(Url_str);

        } catch (MalformedURLException exception) {
            Log.v( "Error with creating URL", String.valueOf(exception));
        }

        return url;
    }

    private static String httpRequest(URL url) throws IOException {
        String quake_JSON_Response = "";
        if(url == null){
            return quake_JSON_Response;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(8000);
            urlConnection.setReadTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                quake_JSON_Response = readFromStream(inputStream);
            } else {
                Log.e("LOG_TAG", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.v("TAGGY","error in http request"+ e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return quake_JSON_Response;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return  output.toString();
    }

    public static List<Earthquake> extractEarthquakes(String QUAKE_JSON_RESPONSE) {
        if(TextUtils.isEmpty(QUAKE_JSON_RESPONSE)){
            return null;
        }
        List<Earthquake> earthquakes = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(QUAKE_JSON_RESPONSE);

            JSONArray earthquakeArray = root.getJSONArray("features");


            for (int i = 0; i < earthquakeArray.length(); i++) {
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");
                double magnitude = properties.getDouble("mag");
                String location = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);
                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return earthquakes;
    }

}