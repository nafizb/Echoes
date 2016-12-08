package com.nafizb.echoes.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nafiz on 3.12.2016.
 */

public class Records {
    public String id;
    public String title;
    public Location location;

    public void setLocation(double lat, double lon) {
        Location loc = new Location();

        loc.setLatitude(lat);
        loc.setLongtitude(lon);

        this.location = loc;
    }

}
