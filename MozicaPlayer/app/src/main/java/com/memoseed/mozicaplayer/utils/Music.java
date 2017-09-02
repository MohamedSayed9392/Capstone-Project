package com.memoseed.mozicaplayer.utils;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.memoseed.mozicaplayer.model.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MohamedSayed on 8/27/2017.
 */

public class Music {

    public static ExoPlayer exoPlayer;
    static TrackSelector trackSelector;
    static DefaultDataSourceFactory dataSourceFactory;
    static ExtractorsFactory extractor;

    public static List<Track> allTracks = new ArrayList<>();
    public static List<Track> favTracks = new ArrayList<>();
    public static String currentList="allTracks";
    public static Track currentTrack;

    public static void initMediaPlayer(Context context) {
        trackSelector = new DefaultTrackSelector();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        dataSourceFactory = new DefaultDataSourceFactory(context,"ExoPlayerDemo");
        extractor = new DefaultExtractorsFactory();
    }

    public static void playTrack(Context context,String path){
        stopPlayer();
        initMediaPlayer(context);
        Uri trackUri = Uri.parse(path);
        MediaSource audioSource = new ExtractorMediaSource(trackUri, dataSourceFactory, extractor, null, null);
        exoPlayer.prepare(audioSource);
        exoPlayer.setPlayWhenReady(true);
    }

    public static void stopPlayer(){
        if(exoPlayer!=null){
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
