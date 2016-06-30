package com.example.guest.apitest;

/**
 * Created by garrettbiernat on 6/29/16.
 */
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ChurchViewHolder> {

    public static class ChurchViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView churchName;
        TextView churchRating;
        TextView churchVicinity;

        ChurchViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            churchName = (TextView)itemView.findViewById(R.id.church_name);
            churchRating = (TextView)itemView.findViewById(R.id.church_rating);
            churchVicinity = (TextView)itemView.findViewById(R.id.church_vicinity);
        }
    }

    List<Church> churches;

    RVAdapter(List<Church> churches){
        this.churches = churches;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ChurchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ChurchViewHolder pvh = new ChurchViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ChurchViewHolder personViewHolder, int i) {
        personViewHolder.churchName.setText(churches.get(i).getName());
        personViewHolder.churchRating.setText(Double.toString(churches.get(i).getRating()));
        personViewHolder.churchVicinity.setText(churches.get(i).getVicinity());
    }

    @Override
    public int getItemCount() {
        return churches.size();
    }
}

