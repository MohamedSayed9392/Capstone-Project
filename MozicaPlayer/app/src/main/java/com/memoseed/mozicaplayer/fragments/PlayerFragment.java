package com.memoseed.mozicaplayer.fragments;

/**
 * Created by MemoSeed on 03/08/2017.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.activities.MainActivity;
import com.memoseed.mozicaplayer.activities.MainActivity_;
import com.memoseed.mozicaplayer.adapters.LibraryRVAdapter;
import com.memoseed.mozicaplayer.database.DatabaseHandler;
import com.memoseed.mozicaplayer.database.TracksContentProvider;
import com.memoseed.mozicaplayer.model.Track;
import com.memoseed.mozicaplayer.utils.Music;
import com.memoseed.mozicaplayer.utils.UTils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment(R.layout.fragment_player)
public class PlayerFragment extends Fragment {

    String TAG = getClass().getSimpleName();

    private Handler updateTrackTimeHandler = new Handler();
    private Runnable updateTrackTimeRunnable = new Runnable() {
        public void run() {
            updateTrackTimeHandler.removeCallbacks(updateTrackTimeRunnable);
            
            Music.currentTrack.setCurrentTime(Music.exoPlayer.getCurrentPosition());

            try {
                seekBar.setProgress((int) Music.currentTrack.getCurrentTime());
                txtCurrent.setText(new SimpleDateFormat("mm:ss").format(new Date(Music.currentTrack.getCurrentTime())));
            }catch (Exception e){
                e.printStackTrace();
            }
            
            updateTrackTimeHandler.postDelayed(updateTrackTimeRunnable, 1000);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "OnCreate CurrentTrackTime : " + Music.currentTrack.getCurrentTime());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Activity mContext;

    // called for API equal or above 23
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = (Activity) context;
    }

    /*
* Deprecated on API 23
*/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @ViewById
    TextView txtTitle;
    @ViewById
    ImageView imAlbumArt;
    @ViewById
    ProgressBar progressBar;
    @ViewById
    Button btnPrev;
    @Click void btnPrev(){
     prevTrack();
    }
    @ViewById
    CheckBox checkPlay;
    @ViewById
    Button btnNext;
    @Click void btnNext(){
        nextTrack();
    }
    @ViewById
    TextView txtCurrent;
    @ViewById
    SeekBar seekBar;
    @ViewById
    TextView txtEnd;

    @AfterViews
    void afterViews() {
        txtTitle.setText(Music.currentTrack.getTitle());
        txtCurrent.setText(new SimpleDateFormat("mm:ss").format(new Date(Music.currentTrack.getCurrentTime())));
        txtEnd.setText(new SimpleDateFormat("mm:ss").format(new Date(Music.currentTrack.getDuration())));
        seekBar.setMax((int)Music.currentTrack.getDuration());
        seekBar.setProgress((int)Music.currentTrack.getCurrentTime());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    Music.exoPlayer.seekTo(i);
                    updateTrackTimeHandler.removeCallbacks(updateTrackTimeRunnable);

                    Music.currentTrack.setCurrentTime(Music.exoPlayer.getCurrentPosition());
                    seekBar.setProgress((int) Music.currentTrack.getCurrentTime());
                    txtCurrent.setText(new SimpleDateFormat("mm:ss").format(new Date(Music.currentTrack.getCurrentTime())));

                    updateTrackTimeHandler.post(updateTrackTimeRunnable);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        checkPlay.setOnCheckedChangeListener((compoundButton, b) -> Music.exoPlayer.setPlayWhenReady(b));


        if(Music.exoPlayer==null){
            Music.playTrack(mContext,Music.currentTrack.getFilePath());
        }else{
            checkPlay.setChecked(Music.exoPlayer.getPlayWhenReady());
            if(Music.exoPlayer.getPlayWhenReady())  updateTrackTimeHandler.post(updateTrackTimeRunnable);
        }

        Music.exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playWhenReady){
                    updateTrackTimeHandler.post(updateTrackTimeRunnable);
                }else{
                    updateTrackTimeHandler.removeCallbacks(updateTrackTimeRunnable);
                }

                switch (playbackState) {
                    case ExoPlayer.STATE_ENDED:
                        nextTrack();
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateTrackTimeHandler.removeCallbacks(updateTrackTimeRunnable);
    }

    private void prevTrack(){
        Track track = null;

        if(Music.currentList.matches("favTracks")){
            for(int i=0;i<Music.favTracks.size();i++){
                if(Music.favTracks.get(i).getId()==Music.currentTrack.getId()){
                    if(i!=0){
                        track = Music.favTracks.get(i-1);
                    }else{
                        track = Music.favTracks.get(Music.allTracks.size()-1);
                    }
                    break;
                }
            }
        }else if(Music.currentList.matches("allTracks")){
            for(int i=0;i<Music.allTracks.size();i++){
                if(Music.allTracks.get(i).getId()==Music.currentTrack.getId()){
                    if(i!=0){
                        track = Music.allTracks.get(i-1);
                    }else{
                        track = Music.allTracks.get(Music.allTracks.size()-1);
                    }
                    break;
                }
            }
        }

        playTrack(track);
    }

    private void nextTrack(){
        Track track = null;

        if(Music.currentList.matches("favTracks")){
            for(int i=0;i<Music.favTracks.size();i++){
                if(Music.favTracks.get(i).getId()==Music.currentTrack.getId()){
                    if(i!=(Music.favTracks.size()-1)){
                        track = Music.favTracks.get(i+1);
                    }else{
                        track = Music.favTracks.get(0);
                    }
                    break;
                }
            }
        }else if(Music.currentList.matches("allTracks")){
            for(int i=0;i<Music.allTracks.size();i++){
                if(Music.allTracks.get(i).getId()==Music.currentTrack.getId()){
                    if(i!=(Music.allTracks.size()-1)){
                        track = Music.allTracks.get(i+1);
                    }else{
                        track = Music.allTracks.get(0);
                    }
                    break;
                }
            }
        }

        playTrack(track);
    }
    private void playTrack(Track track){
        Log.d(TAG,"listened : "+track.getListened());

        track.setListened(track.getListened()+1);

        ContentValues values = new ContentValues();
        values.put("listened", track.getListened());

        Uri contentUri = Uri.withAppendedPath(TracksContentProvider.CONTENT_URI, DatabaseHandler.TABLE_LISTENED_TRACKS);
        if(track.getListened()>1) {
            mContext.getContentResolver().update(contentUri, values, "id = ?", new String[]{String.valueOf(track.getId())});
        }else{
            values.put("id", track.getId());
            mContext.getContentResolver().insert(contentUri, values);
        }

        if(Music.currentTrack!=null){
            Music.stopPlayer();
        }

        Music.currentTrack = track;

        txtTitle.setText(Music.currentTrack.getTitle());
        txtCurrent.setText(new SimpleDateFormat("mm:ss").format(new Date(Music.currentTrack.getCurrentTime())));
        txtEnd.setText(new SimpleDateFormat("mm:ss").format(new Date(Music.currentTrack.getDuration())));
        seekBar.setMax((int)Music.currentTrack.getDuration());
        seekBar.setProgress((int)Music.currentTrack.getCurrentTime());

        ((TracksFragment_)MainActivity.getInstance().libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();

        Music.playTrack(mContext,Music.currentTrack.getFilePath());
    }

    private void toast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }
}
