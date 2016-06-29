package com.example.guest.apitest;

/**
 * Created by Guest on 6/29/16.
 */
public class Church {

    private String mName;
    private double mRating;
    private String mVicinity;

    public Church(String name, double rating, String vicinity){
        this.mName = name;
        this.mRating = rating;
        this.mVicinity = vicinity;
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
}
