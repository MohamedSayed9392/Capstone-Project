package com.memoseed.mozicaplayer.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.adapters.LibraryPagerAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    LibraryPagerAdapter libraryPagerAdapter;

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
}
