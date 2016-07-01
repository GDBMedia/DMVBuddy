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

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DmvViewHolder> {

    public static class DmvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cv;
        TextView dmvName;
        TextView dmvRating;
        TextView dmvVicinity;
        TextView update;

        DmvViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);
            dmvName = (TextView)itemView.findViewById(R.id.dmv_name);
            dmvRating = (TextView)itemView.findViewById(R.id.dmv_rating);
            dmvVicinity = (TextView)itemView.findViewById(R.id.dmv_vicinity);
            update = (TextView)itemView.findViewById(R.id.update);
            update.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), UpdateDmv.class);
            intent.putExtra("name", dmvName.getText().toString());
            view.getContext().startActivity(intent);
        }
    }

    public List<Dmv> dmvs;

    public RVAdapter(List<Dmv> dmvs){
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

