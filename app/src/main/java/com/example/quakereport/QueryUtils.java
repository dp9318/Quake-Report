package com.example.quakereport;


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

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public class QueryUtils {
    private final String quakeUrl;

    public QueryUtils(String quakeUrl) {
        this.quakeUrl = quakeUrl;
    }
    ArrayList<Earthquake> earthquakes =  new ArrayList<>();



    protected void backgroundTask(){
        URL url = createUrl(quakeUrl);
        if (url == null) {
            return; // Handle the error gracefully
        }

        String jsonResponse = "";
        try {
            jsonResponse = httpRequest(url);
        } catch (IOException e) {
            Log.e("QueryUtils", "Error making HTTP request", e);
        }

        extractEarthquakes(jsonResponse);
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
            inputStream=urlConnection.getInputStream();
            quake_JSON_Response = readFromStream(inputStream);

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



    private URL createUrl(String Url_str) {
        URL url;
        try {
            url = new URL(Url_str);

        } catch (MalformedURLException exception) {
            Log.v( "Error with creating URL", String.valueOf(exception));
            return null;
        }

        return url;
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
            inputStreamReader.close();
            reader.close();

        }
        inputStream.close();
        return  output.toString();
    }

    public void extractEarthquakes(String SAMPLE_JSON_RESPONSE) {
        try {
            JSONObject root = new JSONObject(SAMPLE_JSON_RESPONSE);

            JSONArray earthquakeArray = root.getJSONArray("features");



            if (earthquakeArray.length() > 0) {
                for (int i = 0; i < earthquakeArray.length(); i++) {
                    JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                    JSONObject properties = currentEarthquake.getJSONObject("properties");
                    double magnitude = properties.getDouble("mag");
                    String location = properties.getString("place");
                    long time = properties.getLong("time");
                    String url = properties.getString("url");
                    earthquakes.add(new Earthquake(magnitude, location, time, url));
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        finally {
            if (earthquakes.isEmpty()){
                Log.v("earth", "list is empty" + earthquakes);
            }

        }
    }
    public ArrayList<Earthquake> getEarthquakes() {
        return earthquakes;
    }

}