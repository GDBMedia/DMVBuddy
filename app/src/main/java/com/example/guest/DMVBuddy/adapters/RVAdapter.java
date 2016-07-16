package com.example.guest.DMVBuddy.adapters;

/**
 * Created by garrettbiernat on 6/29/16.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guest.DMVBuddy.R;
import com.example.guest.DMVBuddy.models.Dmv;
import com.example.guest.DMVBuddy.services.FormatDate;
import com.example.guest.DMVBuddy.ui.UpdateDmv;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DmvViewHolder> {
    public final String TAG = this.getClass().getSimpleName();
    private ArrayList<Dmv> mDmvs = new ArrayList<>();
    private Context mContext;
    private String mOrigin;
    private DatabaseReference mDmvDatabase;

    public RVAdapter(Context context, ArrayList<Dmv> dmvs, String origin){
        this.mDmvs = dmvs;
        this.mContext = context;
        this.mOrigin = origin;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DmvViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        DmvViewHolder dvh = new DmvViewHolder(v);
        return dvh;
    }


    @Override
    public void onBindViewHolder(final DmvViewHolder dmvViewHolder, final int i) {
        final Dmv dmv = mDmvs.get(i);
        dmvViewHolder.dmvName.setText(dmv.getName());
        dmvViewHolder.dmvRating.setText("Rating: " + Double.toString(mDmvs.get(i).getRating()));
        dmvViewHolder.dmvVicinity.setText(dmv.getVicinity());
        mDmvDatabase = FirebaseDatabase.getInstance().getReference("dmvs");
        Query queryRef = mDmvDatabase.orderByKey().equalTo(dmv.getId());
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Dmv dmv = snapshot.getValue(Dmv.class);
                if(!dmv.getUpdatedBy().equals("N/A")){
                    String formattedDate = FormatDate.formatDate(dmv.getUpdatedAt());
                    dmvViewHolder.lastServed.setVisibility(View.VISIBLE);
                    dmvViewHolder.updatedBy.setVisibility(View.VISIBLE);
                    dmvViewHolder.updatedAt.setVisibility(View.VISIBLE);
                    dmvViewHolder.lastPulled.setText("Last Pulled: " + dmv.getLastPulled());
                    dmvViewHolder.lastServed.setText("Last Served: " + dmv.getLastServed());
                    dmvViewHolder.updatedBy.setText("Updated By: " + dmv.getUpdatedBy());
                    dmvViewHolder.updatedAt.setText("at: " + formattedDate);
                }else{
                    dmvViewHolder.lastPulled.setText("Not Updated");
                    dmvViewHolder.lastServed.setVisibility(View.GONE);
                    dmvViewHolder.updatedBy.setVisibility(View.GONE);
                    dmvViewHolder.updatedAt.setVisibility(View.GONE);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Dmv dmv = dataSnapshot.getValue(Dmv.class);
                if(!dmv.getUpdatedBy().equals("N/A")){

                    String formattedDate = FormatDate.formatDate(dmv.getUpdatedAt());

                    dmvViewHolder.lastServed.setVisibility(View.VISIBLE);
                    dmvViewHolder.updatedBy.setVisibility(View.VISIBLE);
                    dmvViewHolder.updatedAt.setVisibility(View.VISIBLE);
                    dmvViewHolder.lastPulled.setText("Last Pulled: " + dmv.getLastPulled());
                    dmvViewHolder.lastServed.setText("Last Served: " + dmv.getLastServed());
                    dmvViewHolder.updatedBy.setText("Updated By: " + dmv.getUpdatedBy());
                    dmvViewHolder.updatedAt.setText("at: " + formattedDate);
                }else{
                    dmvViewHolder.lastPulled.setText("Not Updated");
                    dmvViewHolder.lastServed.setVisibility(View.GONE);
                    dmvViewHolder.updatedBy.setVisibility(View.GONE);
                    dmvViewHolder.updatedAt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

    }


    @Override
    public int getItemCount() {
        return mDmvs.size();
    }

    public class DmvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.cv) CardView cv;
        @Bind(R.id.dmv_name) TextView dmvName;
        @Bind(R.id.dmv_rating) TextView dmvRating;
        @Bind(R.id.dmv_vicinity) TextView dmvVicinity;
        @Bind(R.id.lastPulled) TextView lastPulled;
        @Bind(R.id.lastserved) TextView lastServed;
        @Bind(R.id.updatedBy) TextView updatedBy;
        @Bind(R.id.updatedAt) TextView updatedAt;

        DmvViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int itemPosition = getLayoutPosition();
            Intent intent = new Intent(view.getContext(), UpdateDmv.class);
            intent.putExtra("position", itemPosition + "");
            intent.putExtra("dmvs", Parcels.wrap(mDmvs));
            intent.putExtra("origin", mOrigin);
            mContext.startActivity(intent);
        }
    }
}

