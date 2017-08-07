package com.memoseed.mozicaplayer.activities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.adapters.LibraryPagerAdapter;
import com.memoseed.mozicaplayer.database.DatabaseHandler;
import com.memoseed.mozicaplayer.database.TracksContentProvider;
import com.memoseed.mozicaplayer.fragments.TracksFragment_;
import com.memoseed.mozicaplayer.model.TrackListened;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    LibraryPagerAdapter libraryPagerAdapter;
    String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libraryPagerAdapter = new LibraryPagerAdapter(getSupportFragmentManager(),2);
    }

    @ViewById
    ViewPager viewPager;

    @AfterViews
    void afterViews(){
        viewPager.setAdapter(libraryPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    int oldOptionItemSelectedid=0;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.d(TAG,item.getTitle().toString());
        switch (id) {
            case R.id.sort_file_name:
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(((TracksFragment_) libraryPagerAdapter.getItem(0)).list, (w1, w2) -> w1.getFileName().compareTo(w2.getFileName()));
                }else{
                    Collections.reverse(((TracksFragment_)libraryPagerAdapter.getItem(0)).list);
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_title:
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(((TracksFragment_)libraryPagerAdapter.getItem(0)).list, (w1, w2) -> w1.getTitle().compareTo(w2.getTitle()));
                }else{
                    Collections.reverse(((TracksFragment_)libraryPagerAdapter.getItem(0)).list);
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_duration:
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(((TracksFragment_)libraryPagerAdapter.getItem(0)).list, (w1, w2) -> ((Long)w1.getDuration()).compareTo(w2.getDuration()));
                }else{
                    Collections.reverse(((TracksFragment_)libraryPagerAdapter.getItem(0)).list);
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_added:
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(((TracksFragment_)libraryPagerAdapter.getItem(0)).list, (w1, w2) -> ((Long)w1.getAdded()).compareTo(w2.getAdded()));
                }else{
                    Collections.reverse(((TracksFragment_)libraryPagerAdapter.getItem(0)).list);
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_listened:

                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(((TracksFragment_)libraryPagerAdapter.getItem(0)).list, (w1, w2) -> ((Long)w1.getListened()).compareTo(w2.getListened()));
                }else{
                    Collections.reverse(((TracksFragment_)libraryPagerAdapter.getItem(0)).list);
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void scrollToPosition(int position,int currentTab){
        Log.d(TAG,"scrollToPosition("+position+","+currentTab+")");
        ((TracksFragment_) libraryPagerAdapter.getItem(currentTab)).rView.smoothScrollToPosition(position);
    }
}
