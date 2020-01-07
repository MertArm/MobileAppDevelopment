package com.example.mert.newsgateway;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FragAdapter extends FragmentPagerAdapter {
    private ArrayList<ArticleFragment> articleFragmentsList;
    private long baseId= 0;

    public FragAdapter(FragmentManager fm, ArrayList<ArticleFragment> fragmentArrayList) {
        super(fm);
        this.articleFragmentsList = fragmentArrayList;
    }

    @Override
    public Fragment getItem(int i) {
        return articleFragmentsList.get(i);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return articleFragmentsList.indexOf(object);
    }

    @Override
    public int getCount() {
        return articleFragmentsList.size();
    }

    @Override
    public long getItemId(int position) {
        return baseId + position;
    }

    public void notifyChangeInPosition(int n) {
        baseId += getCount() + n;
    }
}