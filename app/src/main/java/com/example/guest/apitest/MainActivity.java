package com.example.guest.apitest;

import android.content.Context;
import android.os.Environment;
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

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public ArrayList<Church> mChurches = new ArrayList<>();
    @Bind(R.id.listView) ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getGod();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void getGod() {
        final GooglePlacesService googlePlacesService = new GooglePlacesService();
        googlePlacesService.findGod(new Callback() {

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

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Music");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
