package com.example.guest.apitest.models;

/**
 * Created by Guest on 6/29/16.
 */
public class Dmv {

    private String mName;
    private double mRating;
    private String mVicinity;
    private String mLat;
    private String mLng;

    public Dmv(String name, double rating, String vicinity,String lat, String lng){
        this.mName = name;
        this.mRating = rating;
        this.mVicinity = vicinity;
        this.mLat = lat;
        this.mLng = lng;
    }

    public String getName() {
        return mName;
    }
    public double getRating() {
        return mRating;
    }
    public String getVicinity() {
        return mVicinity;
    }
    public String getLat() {
        return mLat;
    }
    public String getLng() {
        return mLng;
    }
}
