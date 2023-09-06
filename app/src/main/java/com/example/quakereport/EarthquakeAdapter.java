package com.example.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> data_sets ){
        super(context,0,data_sets);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        View ListItemView = convertView;
        if(ListItemView==null){
            ListItemView= LayoutInflater.from(getContext()).inflate(R.layout.list_items,parent,false);
        }
        Earthquake currentNumberset = getItem(position);

        TextView quakeMagnitude = ListItemView.findViewById(R.id.quake_mag);
        assert currentNumberset != null;
        String FormattedMagnitude = formatMagnitude(currentNumberset.getMag());
        quakeMagnitude.setText(FormattedMagnitude);

        GradientDrawable magnitudeCircle = (GradientDrawable) quakeMagnitude.getBackground();

        int magnitudeColor = getMagnitudeColor(currentNumberset.getMag());

        magnitudeCircle.setColor(magnitudeColor);


        String offsetLocation = "";
        String primaryLocation ="";
        final String separator = " of ";
        String Location = currentNumberset.getPlace();
        String[] parts = Location.split(separator);
        if(Location.contains(separator)){
            offsetLocation = parts[0]+separator;
            primaryLocation = parts[1];
        }
        else{
            offsetLocation=getContext().getString(R.string.near_the);
            primaryLocation=Location;
        }

        TextView quakePrimaryLocationView = ListItemView.findViewById(R.id.quake_country);
        quakePrimaryLocationView.setText(primaryLocation);

        TextView quakeoffsetLocationView = ListItemView.findViewById(R.id.quake_city);
        quakeoffsetLocationView.setText(offsetLocation);

        long timeInMilliseconds = currentNumberset.getTime();
        Date dateObject = new Date(timeInMilliseconds);

        TextView quakeDate = ListItemView.findViewById(R.id.quake_date);
        String FormatedDate = formatDate(dateObject);
        quakeDate.setText(FormatedDate);

        TextView quakeTime = ListItemView.findViewById(R.id.quake_time);
        String FormatedTime = formatTime(dateObject);
        quakeTime.setText(FormatedTime);

        return  ListItemView;
    }

    private String formatMagnitude(double Magnitude){
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(Magnitude);
    }
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}

