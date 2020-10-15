package com.example.myswimming.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.myswimming.기타.FragmentA;
import com.example.myswimming.기타.FragmentB;
import com.example.myswimming.기타.FragmentC;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    Fragment[] fragments = new Fragment[3];

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments[0] = new FragmentA();
        fragments[1] = new FragmentB();
        fragments[2] = new FragmentC();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments[arg0];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}

