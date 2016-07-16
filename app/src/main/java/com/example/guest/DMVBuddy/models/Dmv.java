package com.example.guest.DMVBuddy.models;

/**
 * Created by Guest on 6/29/16.
 */
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
@Parcel
public class Dmv {

    public String name;
    public double rating;
    public String vicinity;
    public String location;
    public String id;
    public String lastPulled;
    public String lastServed;
    public String updatedBy;


    public String updatedAt;

    public Dmv() {}


    public Dmv(String name, double rating, String vicinity, String location, String id, String lastPulled, String lastServed, String updatedBy, String updatedAt){
        this.name = name;
        this.rating = rating;
        this.vicinity = vicinity;
        this.location = location;
        this.id = id;
        this.lastPulled = lastPulled;
        this.lastServed = lastServed;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    public Dmv(String id, String lastPulled, String lastServed, String updatedBy, String updatedAt) {
        this.id = id;
        this.lastPulled = lastPulled;
        this.lastServed = lastServed;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }




    public String getName() {
        return name;
    }
    public double getRating() {
        return rating;
    }
    public String getVicinity() {
        return vicinity;
    }
    public String getLocation() {
        return location;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastPulled() {
        return lastPulled;
    }

    public void setLastPulled(String lastPulled) {
        this.lastPulled = lastPulled;
    }

    public String getLastServed() {
        return lastServed;
    }

    public void setLastServed(String lastServed) {
        this.lastServed = lastServed;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("lastPulled", lastPulled);
        result.put("lastServed", lastServed);
        result.put("updatedBy", updatedBy);
        result.put("updatedAt", updatedAt);

        return result;
    }

}
