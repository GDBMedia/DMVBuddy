package com.example.guest.DMVBuddy.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guest.DMVBuddy.R;
import com.example.guest.DMVBuddy.models.Dmv;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 7/15/16.
 */
public class DmvAdapter extends RecyclerView.Adapter<DmvAdapter.RestaurantViewHolder> {
    private ArrayList<Dmv> mDmvs = new ArrayList<>();
    private Context mContext;

    public DmvAdapter(Context context, ArrayList<Dmv> dmvs) {
        mContext = context;
        mDmvs = dmvs;
    }

    @Override
    public DmvAdapter.DmvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        DmvViewHolder viewHolder = new DmvViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DmvAdapter.DmvViewHolder holder, int position) {
        holder.bindRestaurant(mDmvs.get(position));
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public class DmvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.restaurantImageView)
        ImageView mRestaurantImageView;
        @Bind(R.id.restaurantNameTextView)
        TextView mNameTextView;
        @Bind(R.id.categoryTextView) TextView mCategoryTextView;
        @Bind(R.id.ratingTextView) TextView mRatingTextView;

        private Context mContext;

        public DmvViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindDmv(Restaurant restaurant) {
            Picasso.with(mContext).load(restaurant.getImageUrl()).into(mRestaurantImageView);
            mNameTextView.setText(restaurant.getName());
            mCategoryTextView.setText(restaurant.getCategories().get(0));
            mRatingTextView.setText("Rating: " + restaurant.getRating() + "/5");
        }

        @Override
        public void onClick(View v) {
            Log.d("click listener", "working!");
            int itemPosition = getLayoutPosition();
            Intent intent = new Intent(mContext, RestaurantDetailActivity.class);
            intent.putExtra("position", itemPosition + "");
            intent.putExtra("restaurants", Parcels.wrap(mRestaurants));
            mContext.startActivity(intent);
        }
    }
}