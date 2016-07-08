package com.example.guest.apitest.adapters;

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

import com.example.guest.apitest.R;
import com.example.guest.apitest.models.Dmv;
import com.example.guest.apitest.ui.MainActivity;
import com.example.guest.apitest.ui.UpdateDmv;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DmvViewHolder> {
    private ArrayList<Dmv> mDmvs = new ArrayList<>();
    private Context mContext;
    private String mOrigin;

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
    public void onBindViewHolder(DmvViewHolder dmvViewHolder, int i) {
        dmvViewHolder.dmvName.setText(mDmvs.get(i).getName());
        dmvViewHolder.dmvRating.setText("Rating: " + Double.toString(mDmvs.get(i).getRating()));
        dmvViewHolder.dmvVicinity.setText(mDmvs.get(i).getVicinity());
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

