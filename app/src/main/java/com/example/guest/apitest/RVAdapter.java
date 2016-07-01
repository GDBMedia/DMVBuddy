package com.example.guest.apitest;

/**
 * Created by garrettbiernat on 6/29/16.
 */
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DmvViewHolder> {

    public static class DmvViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView dmvName;
        TextView dmvRating;
        TextView dmvVicinity;

        DmvViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            dmvName = (TextView)itemView.findViewById(R.id.dmv_name);
            dmvRating = (TextView)itemView.findViewById(R.id.dmv_rating);
            dmvVicinity = (TextView)itemView.findViewById(R.id.dmv_vicinity);
        }
    }

    List<Dmv> dmvs;

    RVAdapter(List<Dmv> dmvs){
        this.dmvs = dmvs;
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
        dmvViewHolder.dmvName.setText(dmvs.get(i).getName());
        dmvViewHolder.dmvRating.setText("Rating: " + Double.toString(dmvs.get(i).getRating()));
        dmvViewHolder.dmvVicinity.setText(dmvs.get(i).getVicinity());
    }

    @Override
    public int getItemCount() {
        return dmvs.size();
    }
}

