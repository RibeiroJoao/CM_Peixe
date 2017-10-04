package com.petuniversal.joaoribeiro.petuniversal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joao Ribeiro on 01/10/2017./**
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragList = new ArrayList<>();
    private final List<String> mFragTitleList = new ArrayList<>();

    public void addFragment (Fragment fragment, String title){
        mFragList.add(fragment);
        mFragTitleList.add(title);
    }

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragList.get(position);
    }

    @Override
    public int getCount() {
        return mFragList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragTitleList.get(position);
    }
}
