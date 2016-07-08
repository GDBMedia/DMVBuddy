package com.example.guest.apitest.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.guest.apitest.models.Dmv;
import com.example.guest.apitest.ui.UpdateDmvFragment;

import java.util.ArrayList;

/**
 * Created by Guest on 7/8/16.
 */
public class DmvPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Dmv> mDmvs;
    private String mOrigin;

    public DmvPagerAdapter(FragmentManager fm, ArrayList<Dmv> dmvs, String origin) {
        super(fm);
        mDmvs = dmvs;
        mOrigin = origin;
    }

    @Override
    public Fragment getItem(int position) {
        return UpdateDmvFragment.newInstance(mDmvs.get(position), mOrigin);
    }

    @Override
    public int getCount() {
        return mDmvs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDmvs.get(position).getName();
    }
}
