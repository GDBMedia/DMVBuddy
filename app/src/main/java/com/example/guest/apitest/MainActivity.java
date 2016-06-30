package com.example.guest.apitest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;

import android.Manifest;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener{

    public static final String TAG = MainActivity.class.getSimpleName();
    public ArrayList<Church> mChurches = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private Location mLastLocation;
    private String mLatitudeText;
    private String mLongitudeText;
    private LocationManager mLocationManager;

    @Bind(R.id.listView) ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
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
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                1000, mLocationListener);
        getGod();

}
    private final LocationListener mLocationListener = new LocationListener() {


        @Override
        public void onLocationChanged(final Location location) {



        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Toast.makeText(MainActivity.this, s+" hey", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            Toast.makeText(this, mGoogleApiClient+"hey", Toast.LENGTH_LONG).show();
            if (location != null) {
                mLatitudeText = String.valueOf(location.getLatitude());
                mLongitudeText = String.valueOf(location.getLongitude());

                Toast.makeText(this, mLongitudeText+"hey", Toast.LENGTH_SHORT).show();

            }

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "suspended", Toast.LENGTH_LONG).show();
    }


    private void getGod() {
        final GooglePlacesService googlePlacesService = new GooglePlacesService();
        googlePlacesService.findGod(mLatitudeText, mLongitudeText, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response){
                mChurches = googlePlacesService.processResults(response);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String[] restaurantNames = new String[mChurches.size()];
                        for (int i = 0; i < restaurantNames.length; i++) {
                            restaurantNames[i] = mChurches.get(i).getName();
                        }

                        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,
                                android.R.layout.simple_list_item_1, restaurantNames);
                        mListView.setAdapter(adapter);

                        for (Church restaurant : mChurches) {
                            Log.d(TAG, "Name: " + restaurant.getName());
                            Log.d(TAG, "Vicinity: " + restaurant.getVicinity());
                            Log.d(TAG, "Rating: " + Double.toString(restaurant.getRating()));
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

    }
}
