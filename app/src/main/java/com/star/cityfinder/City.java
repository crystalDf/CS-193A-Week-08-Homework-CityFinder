package com.star.cityfinder;


import com.google.android.gms.maps.model.LatLng;

public class City {

    private String mName;
    private LatLng mLatLng;

    public City(String name, double latitude, double longitude) {
        mName = name;
        mLatLng = new LatLng(latitude, longitude);
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    @Override
    public String toString() {
        return mName;
    }
}
