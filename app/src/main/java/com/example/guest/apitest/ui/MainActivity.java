package com.example.guest.apitest.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.apitest.R;
import com.example.guest.apitest.adapters.RVAdapter;
import com.example.guest.apitest.models.Dmv;
import com.example.guest.apitest.services.GooglePlacesService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener{

    public static final String TAG = MainActivity.class.getSimpleName();
    public List<Dmv> mDmvs = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    @Bind(R.id.rv) RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

}
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                Toast.makeText(this, longitude+","+latitude, Toast.LENGTH_LONG).show();
                getDmvs(longitude, latitude);


            }else{
                String latitude = "45.520606";
                String longitude = "-122.707413";
                getDmvs(longitude, latitude);
                Toast.makeText(this, "Cant Find Location", Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "suspended", Toast.LENGTH_LONG).show();
    }


    private void getDmvs(String longitude, String latitude) {
        final GooglePlacesService googlePlacesService = new GooglePlacesService();
        googlePlacesService.findDmvs(longitude, latitude, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response){
                mDmvs = googlePlacesService.processResults(response);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        RVAdapter adapter = new RVAdapter(mDmvs);
                        rv.setAdapter(adapter);



                        for (Dmv dmv : mDmvs) {
                            Log.d(TAG, "Name: " + dmv.getName());
                            Log.d(TAG, "Vicinity: " + dmv.getVicinity());
                            Log.d(TAG, "Rating: " + Double.toString(dmv.getRating()));
                            Log.d(TAG, "Vicinity: " + dmv.getLat());
                            Log.d(TAG, "Vicinity: " + dmv.getLng());
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "failed", Toast.LENGTH_LONG).show();

    }
}
