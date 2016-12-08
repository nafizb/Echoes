package com.nafizb.echoes.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nafiz on 28.04.2016.
 */
public class Location {
    @SerializedName("lat")
    double latitude;

    @SerializedName("lon")
    double longtitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longtitude;
    }


    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
