package com.memoseed.mozicaplayer.adapters;

/**
 * Created by MemoSeed on 16/02/2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.memoseed.mozicaplayer.fragments.TracksFragment;
import com.memoseed.mozicaplayer.fragments.TracksFragment_;

import java.util.ArrayList;
import java.util.List;


public class LibraryPagerAdapter extends FragmentStatePagerAdapter {

    List<TracksFragment_ > list = new ArrayList();
    int size;

    public LibraryPagerAdapter(FragmentManager fm, int size) {
        super(fm);
        this.size = size;
        for(int i=0;i<size;i++){
            this.list.add(TracksFragment.newInstance(i));
        }

    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
}

    @Override
    public int getCount() {
        return size;
    }

}