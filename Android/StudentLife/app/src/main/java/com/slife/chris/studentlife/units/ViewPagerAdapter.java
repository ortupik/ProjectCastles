package com.slife.chris.studentlife.units;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by jorge on 3/17/14.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;
    private int count;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<Fragment>();
        count = 0;
    }

    @Override
    public Fragment getItem(int position) {

        if (fragments.size() == 0) {
            return new DummyFragment();
        }
        return fragments.get(position);
    }

    public void addFragment(Fragment fragmentToAdd) {
        fragments.add(fragmentToAdd);
        count++;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return count;
    }
}
