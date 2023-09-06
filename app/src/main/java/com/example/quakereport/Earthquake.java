package com.example.quakereport;

import androidx.annotation.NonNull;

public class Earthquake {
    private final Double mag;
    private final String place;
    private final Long time;
    private final String url;

    public Earthquake(Double mag, String place, Long time, String url) {
        this.mag = mag;
        this.place = place;
        this.time = time;
        this.url = url;
    }

    public Double getMag() {
        return mag;
    }

    public String getPlace() {
        return place;
    }

    public Long getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    @NonNull
    @Override
    public String toString() {
        return "Magnitude: " + mag + ", Location: " + place + " , Time: " + time + " , Url: " + url;
    }
}
