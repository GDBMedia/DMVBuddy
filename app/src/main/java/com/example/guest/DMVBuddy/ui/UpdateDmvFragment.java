package com.example.guest.DMVBuddy.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guest.DMVBuddy.R;
import com.example.guest.DMVBuddy.models.Dmv;
import com.example.guest.DMVBuddy.services.GooglePlacesService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.text.WordUtils;
import org.parceler.Parcels;

import java.io.IOException;
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
    @Bind(R.id.lastPulledUpdate) TextView mLastpulled;
    @Bind(R.id.lastServedUpdate) TextView mLastServed;

    private Dmv mDmv;
    private String mOrigin;
    private String[] mTravelInfo;
    private DatabaseReference mDmvDatabase;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_dmv, container, false);
        ButterKnife.bind(this, view);

        getTravelInfo();

        mAddress.setOnClickListener(this);

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
                        Query queryRef = mDmvDatabase.child(mDmv.getId());

                        queryRef.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Dmv dmv = dataSnapshot.getValue(Dmv.class);
//                                        mLastpulled
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
