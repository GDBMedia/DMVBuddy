package com.example.guest.DMVBuddy.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.guest.DMVBuddy.R;
import com.example.guest.DMVBuddy.adapters.RVAdapter;
import com.example.guest.DMVBuddy.models.Dmv;
import com.example.guest.DMVBuddy.services.GooglePlacesService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends Activity implements LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public ArrayList<Dmv> mDmvs = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    @Bind(R.id.rv) RecyclerView rv;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private String mLatitude;
    private String mLongitude;
    private String mOrigin;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String provider;
    protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;


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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDmvs(mLongitude, mLatitude);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//
//    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//}


//    @Override
//    public void onConnected(Bundle connectionHint) {
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            if (location != null) {
//                mLatitude = String.valueOf(location.getLatitude());
//                mLongitude = String.valueOf(location.getLongitude());
//                getDmvs(mLongitude, mLatitude);
//
//
//            }else{
//                mLatitude = "45.520606";
//                mLongitude = "-122.707413";
//                getDmvs(mLongitude, mLatitude);
//            }
//
//        }
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Toast.makeText(this, "suspended", Toast.LENGTH_LONG).show();
//    }


    private void getDmvs(String longitude, String latitude) {
        final GooglePlacesService googlePlacesService = new GooglePlacesService();
        googlePlacesService.findDmvs(longitude, latitude, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response){
                mOrigin = mLatitude + "," +  mLongitude;
                mDmvs = googlePlacesService.processResults(response);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        RVAdapter adapter = new RVAdapter(MainActivity.this, mDmvs, mOrigin);
                        rv.setAdapter(adapter);
                        swipeContainer.setRefreshing(false);

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
    public void onLocationChanged(Location location) {
       mLatitude = location.getLatitude()+"";
        mLongitude = location.getLongitude()+"";
        getDmvs(mLongitude, mLatitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Toast.makeText(this, "failed", Toast.LENGTH_LONG).show();
//
//    }

}
