package com.memoseed.mozicaplayer.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.debugSystem.ExceptionHandler;
import com.memoseed.mozicaplayer.fragments.PlayerFragment_;
import com.memoseed.mozicaplayer.utils.Music;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_player)
public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        PlayerFragment_ playerFragment = new PlayerFragment_();
       // playerFragment.setHasOptionsMenu(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, playerFragment);
        fragmentTransaction.commit();
    }
}
