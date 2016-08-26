package com.example.guest.DMVBuddy.services;

/**
 * Created by Guest on 6/29/16.
 */

import android.util.Log;

import com.example.guest.DMVBuddy.Constants;
import com.example.guest.DMVBuddy.models.Dmv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GooglePlacesService {
    public final String TAG = this.getClass().getSimpleName();
    private DatabaseReference mDatabase;
    private DatabaseReference mDmvDatabase;
    public static void findDmvs(String longitude, String latitude, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.GOOGLE_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.LOCATIONP,  latitude+ "," +  longitude)
                  .addQueryParameter(Constants.KEYWORDP, Constants.KEYWORD)
                  .addQueryParameter(Constants.RANKBYP, Constants.RANKBY)
                  .addQueryParameter("type", "local_government_office")
                  .addQueryParameter(Constants.KEYP, Constants.KEY);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void findDistance(String origin, String destination, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.GOOGLE_DISTANCE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.ORIGINP, origin)
                  .addQueryParameter(Constants.DESTINATIONP,destination)
                  .addQueryParameter(Constants.KEYP, Constants.KEY);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
    public String[] processTravel(Response response){
        String[] travelInfo = new String[2];
        String distance = "";
        String duration = "";
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject googleJSON = new JSONObject(jsonData);
                JSONArray routesJSON = googleJSON.getJSONArray("routes");
                distance = routesJSON.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                duration = routesJSON.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        travelInfo[0] = distance;
        travelInfo[1] = duration;
        return travelInfo;
    }

    public ArrayList<Dmv> processResults(Response response) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ArrayList<Dmv> dmvs = new ArrayList<>();
        final Map<String, Object> childUpdates = new HashMap<>();

        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject googleJSON = new JSONObject(jsonData);
                JSONArray placesJSON = googleJSON.getJSONArray("results");
                for (int i = 0; i < placesJSON.length(); i++) {
                    JSONObject dmvJSON = placesJSON.getJSONObject(i);
                    String name = dmvJSON.getString("name");
                    double rating = dmvJSON.optDouble("rating", 0);
                    String vicinity = dmvJSON.getString("vicinity");
                    String lat = dmvJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
                    String lng = dmvJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
                    String location = lat + "," + lng;
                    String id = dmvJSON.getString("id");
                    if(name.toLowerCase().contains("dmv") || name.toLowerCase().contains("department of motor vehicles") || name.toLowerCase().contains("motor vehicles division")){
                        final Dmv dmv = new Dmv(name, rating,  vicinity, location, id, "0", "0", "N/A", "N/A");

                        mDmvDatabase = FirebaseDatabase.getInstance().getReference("dmvs");
                        Query queryRef = mDmvDatabase.child(dmv.getId());

                        queryRef.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Dmv dmvtest = dataSnapshot.getValue(Dmv.class);
                                        if(dmvtest == null){
                                            Log.d(TAG, "onDataChange: " +dmv.getId());
                                            Map<String, Object> dmvValues = dmv.toMap();
                                            childUpdates.put("/dmvs/" + dmv.getId(), dmvValues);
                                            mDatabase.updateChildren(childUpdates);
                                        }

                                        // ...
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });

                        dmvs.add(dmv);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dmvs;
    }
}
