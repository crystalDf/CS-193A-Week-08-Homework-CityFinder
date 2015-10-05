package com.star.cityfinder;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class City {

    private String mName;
    private double mLatitude;
    private double mLongitude;
    private LatLng mLatLng;
    private Marker mMarker;

    public City(String name, double latitude, double longitude) {
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
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
