package com.example.guest.apitest.ui;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.guest.apitest.R;
import com.example.guest.apitest.adapters.DmvPagerAdapter;
import com.example.guest.apitest.models.Dmv;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdateDmv extends AppCompatActivity {

    @Bind(R.id.viewPager) ViewPager mViewPager;
    private DmvPagerAdapter adapterViewPager;
    ArrayList<Dmv> mDmvs = new ArrayList<>();
    private String mOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dmv);

        ButterKnife.bind(this);

        mDmvs = Parcels.unwrap(getIntent().getParcelableExtra("dmvs"));
        int startingPosition = Integer.parseInt(getIntent().getStringExtra("position"));
        mOrigin = getIntent().getStringExtra("origin");

        adapterViewPager = new DmvPagerAdapter(getSupportFragmentManager(), mDmvs, mOrigin);
        mViewPager.setAdapter(adapterViewPager);
        mViewPager.setCurrentItem(startingPosition);
    }
}
