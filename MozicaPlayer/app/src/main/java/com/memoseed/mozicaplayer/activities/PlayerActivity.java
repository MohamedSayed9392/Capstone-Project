package com.memoseed.mozicaplayer.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.memoseed.mozicaplayer.MusicNotification.MusicNotificationConstants;
import com.memoseed.mozicaplayer.MusicNotification.MusicNotificationService;
import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.debugSystem.ExceptionHandler;
import com.memoseed.mozicaplayer.fragments.PlayerFragment_;
import com.memoseed.mozicaplayer.utils.Music;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_player)
public class PlayerActivity extends AppCompatActivity {

    static PlayerActivity playerActivity;
    public static PlayerActivity getInstance() {
        return playerActivity;
    }

    public PlayerFragment_ playerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        playerActivity = this;

        playerFragment = new PlayerFragment_();
       // playerFragment.setHasOptionsMenu(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, playerFragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerActivity = null;
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(Music.exoPlayer.getPlayWhenReady()) startService(new Intent(this, MusicNotificationService.class).setAction(MusicNotificationConstants.ACTION.PLAY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(this, MusicNotificationService.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(MainActivity.getInstance()==null) startActivity(new Intent(this,MainActivity_.class));
    }
}
