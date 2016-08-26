package com.example.guest.DMVBuddy.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by Guest on 7/15/16.
 */
public class DmvAdapter extends RecyclerView.Adapter<DmvAdapter.DmvViewHolder> {
    private ArrayList<Dmv> mDmvs = new ArrayList<>();
    private Context mContext;
    private String mOrigin;
    private DatabaseReference mDmvDatabase;
    private int lastPosition = -1;

    public DmvAdapter(Context context, ArrayList<Dmv> dmvs, String origin) {
        mContext = context;
        mDmvs = dmvs;
        mOrigin = origin;
    }

    @Override
    public DmvAdapter.DmvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        DmvViewHolder viewHolder = new DmvViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DmvAdapter.DmvViewHolder holder, int position) {
        holder.bindDmv(mDmvs.get(position));
        setAnimation(holder.cv, position);
    }

    private void setAnimation(CardView cv, int position) {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animate);
            cv.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mDmvs.size();
    }

    public class DmvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.cv) CardView cv;
        @Bind(R.id.dmv_name) TextView dmvName;
        @Bind(R.id.dmv_rating) TextView dmvRating;
        @Bind(R.id.dmv_vicinity) TextView dmvVicinity;
        @Bind(R.id.lastPulled) TextView lastPulled;
        @Bind(R.id.lastserved) TextView lastServed;
        @Bind(R.id.updatedBy) TextView updatedBy;
        @Bind(R.id.updatedAt) TextView updatedAt;
        private Context mContext;

        public DmvViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindDmv(Dmv dmv) {
            Typeface adam = Typeface.createFromAsset(mContext.getAssets(), "fonts/adam.otf");
            dmvName.setTypeface(adam);

            dmvName.setText(dmv.getName());
            dmvRating.setText("Rating: " + Double.toString(dmv.getRating()));
            dmvVicinity.setText(dmv.getVicinity());
//            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animate);
//            itemView.startAnimation(animation);
            mDmvDatabase = FirebaseDatabase.getInstance().getReference("dmvs");
            Query queryRef = mDmvDatabase.orderByKey().equalTo(dmv.getId());
            queryRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                    Dmv dmv = snapshot.getValue(Dmv.class);
                    if(!dmv.getUpdatedBy().equals("N/A")){
                        String formattedDate = FormatDate.formatDate(dmv.getUpdatedAt());
                        lastServed.setVisibility(View.VISIBLE);
                        updatedBy.setVisibility(View.VISIBLE);
                        updatedAt.setVisibility(View.VISIBLE);
                        lastPulled.setText("Last Pulled: " + dmv.getLastPulled());
                        lastServed.setText("Last Served: " + dmv.getLastServed());
                        updatedBy.setText("Updated By: " + dmv.getUpdatedBy());
                        updatedAt.setText("at: " + formattedDate);
                    }else{
                        lastPulled.setText("Not Updated");
                        lastServed.setVisibility(View.GONE);
                        updatedBy.setVisibility(View.GONE);
                        updatedAt.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Dmv dmv = dataSnapshot.getValue(Dmv.class);
                    if(!dmv.getUpdatedBy().equals("N/A")){

                        String formattedDate = FormatDate.formatDate(dmv.getUpdatedAt());

                        lastServed.setVisibility(View.VISIBLE);
                        updatedBy.setVisibility(View.VISIBLE);
                        updatedAt.setVisibility(View.VISIBLE);
                        lastPulled.setText("Last Pulled: " + dmv.getLastPulled());
                        lastServed.setText("Last Served: " + dmv.getLastServed());
                        updatedBy.setText("Updated By: " + dmv.getUpdatedBy());
                        updatedAt.setText("at: " + formattedDate);
                    }else{
                        lastPulled.setText("Not Updated");
                        lastServed.setVisibility(View.GONE);
                        updatedBy.setVisibility(View.GONE);
                        updatedAt.setVisibility(View.GONE);
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
        public void onClick(View v) {
            int itemPosition = getLayoutPosition();
            Intent intent = new Intent(v.getContext(), UpdateDmv.class);
            intent.putExtra("position", itemPosition + "");
            intent.putExtra("dmvs", Parcels.wrap(mDmvs));
            intent.putExtra("origin", mOrigin);
            mContext.startActivity(intent);
        }
    }
}