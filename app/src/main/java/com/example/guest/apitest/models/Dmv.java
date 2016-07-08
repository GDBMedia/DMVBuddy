package com.example.guest.apitest.models;

/**
 * Created by Guest on 6/29/16.
 */
import org.parceler.Parcel;

@Parcel
public class Dmv {

    private String mName;
    private double mRating;
    private String mVicinity;
    private String mLocation;

    public Dmv() {}

    public Dmv(String name, double rating, String vicinity,String location){
        this.mName = name;
        this.mRating = rating;
        this.mVicinity = vicinity;
        this.mLocation = location;
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
    public String getLocation() {
        return mLocation;
    }
}
