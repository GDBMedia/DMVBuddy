package com.example.guest.DMVBuddy.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.DMVBuddy.R;
import com.example.guest.DMVBuddy.models.Dmv;
import com.example.guest.DMVBuddy.models.User;
import com.example.guest.DMVBuddy.services.FormatDate;
import com.example.guest.DMVBuddy.services.GooglePlacesService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.apache.commons.lang3.text.WordUtils;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class UpdateDmvFragment extends Fragment implements View.OnClickListener{
    public final String TAG = this.getClass().getSimpleName();
    @Bind(R.id.name) TextView mName;
    @Bind(R.id.address) TextView mAddress;
    @Bind(R.id.travel) TextView mTravel;
    @Bind(R.id.submit) Button mSubmit;
    @Bind(R.id.lastPulledUpdate) TextView mLastpulled;
    @Bind(R.id.lastServedUpdate) TextView mLastServed;
    @Bind(R.id.updatedBy) TextView mUpdatedBy;
    @Bind(R.id.updatedAt) TextView mUpdatedAt;
    @Bind(R.id.pulled) EditText mPulled;
    @Bind(R.id.serving) EditText mServing;

    private Dmv mDmv;
    private String mOrigin;
    private String[] mTravelInfo;
    private DatabaseReference mDmvDatabase;
    private DatabaseReference mDatabase;
    private SharedPreferences mSharedPreferences;
    private User mCurrentUser;

    public static UpdateDmvFragment newInstance(Dmv dmv, String origin) {
        UpdateDmvFragment updateDmvFragment = new UpdateDmvFragment();
        Bundle args = new Bundle();
        args.putParcelable("dmv", Parcels.wrap(dmv));
        args.putString("origin", origin);
        updateDmvFragment.setArguments(args);
        return updateDmvFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDmv = Parcels.unwrap(getArguments().getParcelable("dmv"));
        mOrigin = getArguments().getString("origin");
    }
    @Override
    public void onClick(View view) {

        if (view == mAddress) {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:" + mDmv.getLocation()
                            + "?q=(" + mDmv.getName() + ")"));
            startActivity(mapIntent);
        }
        if (view == mSubmit) {
            String numberPulled = mPulled.getText().toString();
            String numberServing = mServing.getText().toString();
            if(!numberPulled.equals("") && !numberServing.equals("")){
                mPulled.setText("");
                mServing.setText("");

                Dmv dmv = new Dmv(mDmv.getId(), numberPulled, numberServing, mCurrentUser.getUsername(), new Date().toString());
                Map<String, Object> dmvValues = dmv.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/dmvs/" + mDmv.getId(), dmvValues);
                mDatabase.updateChildren(childUpdates);
                Toast.makeText(getContext(), "Updated", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getContext(), "Please fill out form", Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_dmv, container, false);
        ButterKnife.bind(this, view);

        getTravelInfo();

        Gson gson = new Gson();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String json = mSharedPreferences.getString("currentUser", null);
        mCurrentUser = gson.fromJson(json, User.class);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAddress.setOnClickListener(this);
        mSubmit.setOnClickListener(this);

        return view;
    }

    private void getTravelInfo() {
        final GooglePlacesService googlePlacesService = new GooglePlacesService();
        googlePlacesService.findDistance(mOrigin, mDmv.getLocation(), new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response){
                mTravelInfo = googlePlacesService.processTravel(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mDmvDatabase = FirebaseDatabase.getInstance().getReference("dmvs");
                        Query queryRef = mDmvDatabase.orderByKey().equalTo(mDmv.getId());

                        queryRef.addChildEventListener(new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Dmv dmv = dataSnapshot.getValue(Dmv.class);
                                if(!dmv.getUpdatedBy().equals("N/A")){
                                    String formattedDate = FormatDate.formatDate(dmv.getUpdatedAt());
                                    mLastpulled.setText("Last Pulled: " + dmv.getLastPulled());
                                    mLastServed.setText("Last Served: " + dmv.getLastServed());
                                    mUpdatedBy.setText("Updated By: " + dmv.getUpdatedBy());
                                    mUpdatedAt.setText("at: " + formattedDate);

                                }else{
                                    mLastpulled.setText("Not Updated");
                                }



                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Dmv dmv = dataSnapshot.getValue(Dmv.class);
                                String formattedDate = FormatDate.formatDate(dmv.getUpdatedAt());
                                mLastpulled.setText("Last Pulled: " + dmv.getLastPulled());
                                mLastServed.setText("Last Served: " + dmv.getLastServed());
                                mUpdatedBy.setText("Updated By: " + dmv.getUpdatedBy());
                                mUpdatedAt.setText("at: " + formattedDate);

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });



                        mName.setText(mDmv.getName());
                        mAddress.setText(mDmv.getVicinity());
                        mTravel.setText(mTravelInfo[0] + "(" + mTravelInfo[1] + ")");
                    }
                });
            }
        });
    }


}
