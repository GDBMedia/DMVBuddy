package com.example.guest.DMVBuddy.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.example.guest.DMVBuddy.Constants;
import com.example.guest.DMVBuddy.R;
import com.example.guest.DMVBuddy.adapters.DmvAdapter;
import com.example.guest.DMVBuddy.adapters.RVAdapter;
import com.example.guest.DMVBuddy.models.Dmv;
import com.example.guest.DMVBuddy.services.GooglePlacesService;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public final String TAG = this.getClass().getSimpleName();
    public ArrayList<Dmv> mDmvs = new ArrayList<>();
    private final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    @Bind(R.id.rv) RecyclerView rv;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private String mLatitude;
    private String mLongitude;
    private String mOrigin;
    protected LocationManager locationManager;
    protected LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_ACCESS_COARSE_LOCATION);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDmvs(mLongitude, mLatitude);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.UPDATE_LENGTH, Constants.UPDATE_DISTANCE, this);
            try{
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mLongitude = location.getLongitude()+"";
                mLatitude = location.getLatitude()+"";
                getDmvs(mLongitude, mLatitude);
            }catch (NullPointerException e){
                Log.d(TAG, "onStart: " +e);
                Toast.makeText(this, "Can't Get Location", Toast.LENGTH_SHORT).show();
            }

            Log.d(TAG, "onStart: listening");
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onStop: notListening");
            locationManager.removeUpdates(this);
        }
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
                mOrigin = mLatitude + "," +  mLongitude;
                mDmvs = googlePlacesService.processResults(response);
                Log.d(TAG, "onResponse: " + mDmvs.size());
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);

                        rv.setLayoutManager(llm);
                        rv.setHasFixedSize(true);
//                        rv.setItemAnimator(new FadeInAnimator());

                        DmvAdapter adapter = new DmvAdapter(MainActivity.this, mDmvs, mOrigin);
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
        Log.d(TAG, "onLocationChanged: " + mLatitude + "," + mLongitude);
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

}
