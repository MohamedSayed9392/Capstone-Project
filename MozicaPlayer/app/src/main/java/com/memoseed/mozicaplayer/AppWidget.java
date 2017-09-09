package com.memoseed.mozicaplayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.memoseed.mozicaplayer.MusicNotification.MusicNotificationConstants;
import com.memoseed.mozicaplayer.activities.MainActivity_;
import com.memoseed.mozicaplayer.activities.PlayerActivity;
import com.memoseed.mozicaplayer.activities.PlayerActivity_;
import com.memoseed.mozicaplayer.model.Track;
import com.memoseed.mozicaplayer.utils.Music;
import com.memoseed.mozicaplayer.utils.UTils;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by MohamedSayed on 9/9/2017.
 */


public class AppWidget extends AppWidgetProvider {

    String TAG = getClass().getSimpleName();
    Context cont;
    public static final String WIDGET_IDS_KEY ="AppWidget";
    public String currentAction = MusicNotificationConstants.ACTION.STARTFOREGROUND_ACTION;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"intent action : "+intent.getAction());
        if (intent.hasExtra(WIDGET_IDS_KEY)) {
            int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
            onUpdate(context, AppWidgetManager.getInstance(context), ids);
        } else {
            if(intent.getAction()!=null) {
                if (intent.getAction().equals(MusicNotificationConstants.ACTION.STARTFOREGROUND_ACTION)) {
                    UTils.updateWidget(context);
                } else if (intent.getAction().equals(MusicNotificationConstants.ACTION.PREV_ACTION)) {
                    Track track = null;

                    if (Music.currentList.matches("favTracks")) {
                        for (int i = 0; i < Music.favTracks.size(); i++) {
                            if (Music.favTracks.get(i).getId() == Music.currentTrack.getId()) {
                                if (i != 0) {
                                    track = Music.favTracks.get(i - 1);
                                } else {
                                    track = Music.favTracks.get(Music.allTracks.size() - 1);
                                }
                                break;
                            }
                        }
                    } else if (Music.currentList.matches("allTracks")) {
                        for (int i = 0; i < Music.allTracks.size(); i++) {
                            if (Music.allTracks.get(i).getId() == Music.currentTrack.getId()) {
                                if (i != 0) {
                                    track = Music.allTracks.get(i - 1);
                                } else {
                                    track = Music.allTracks.get(Music.allTracks.size() - 1);
                                }
                                break;
                            }
                        }
                    }

                    Music.currentTrack = track;
                    Music.playTrack(context, track.getFilePath());
                    Log.i(TAG, "Clicked Previous");
                    UTils.updateWidget(context);
                } else if (intent.getAction().equals(MusicNotificationConstants.ACTION.PLAY_ACTION)) {
                    Log.i(TAG, "Clicked Play");
                    try {
                        Music.exoPlayer.setPlayWhenReady(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    UTils.updateWidget(context);
                } else if (intent.getAction().equals(MusicNotificationConstants.ACTION.PAUSE_ACTION)) {
                    Log.i(TAG, "Clicked Pause");
                    try {
                        Music.exoPlayer.setPlayWhenReady(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    UTils.updateWidget(context);
                } else if (intent.getAction().equals(MusicNotificationConstants.ACTION.NEXT_ACTION)) {
                    Track track = null;

                    if (Music.currentList.matches("favTracks")) {
                        for (int i = 0; i < Music.favTracks.size(); i++) {
                            if (Music.favTracks.get(i).getId() == Music.currentTrack.getId()) {
                                if (i != (Music.favTracks.size() - 1)) {
                                    track = Music.favTracks.get(i + 1);
                                } else {
                                    track = Music.favTracks.get(0);
                                }
                                break;
                            }
                        }
                    } else if (Music.currentList.matches("allTracks")) {
                        for (int i = 0; i < Music.allTracks.size(); i++) {
                            if (Music.allTracks.get(i).getId() == Music.currentTrack.getId()) {
                                if (i != (Music.allTracks.size() - 1)) {
                                    track = Music.allTracks.get(i + 1);
                                } else {
                                    track = Music.allTracks.get(0);
                                }
                                break;
                            }
                        }
                    }

                    Music.currentTrack = track;
                    Music.playTrack(context, track.getFilePath());
                    UTils.updateWidget(context);
                }
            } else {
                super.onReceive(context, intent);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        cont = context;
        appWidgetManager.updateAppWidget(appWidgetIds[0], updateUi());

    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }



    private RemoteViews updateUi() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(cont.getPackageName(), R.layout.music_player_notification);

        // showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        views.setViewVisibility(R.id.status_bar_next, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_prev, View.VISIBLE);

        if(Music.currentTrack==null){
            views.setViewVisibility(R.id.txtNoTrack, View.VISIBLE);
        }else{
            views.setTextViewText(R.id.status_bar_track_name, Music.currentTrack.getTitle());
            views.setTextViewText(R.id.status_bar_track_artist, Music.currentTrack.getArtist());
            views.setViewVisibility(R.id.txtNoTrack, View.GONE);
        }

        Intent previousIntent = new Intent(cont, getClass());
        previousIntent.setAction(MusicNotificationConstants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getBroadcast(cont, 0, previousIntent, 0);

        Intent playIntent = new Intent(cont, getClass());
        playIntent.setAction(MusicNotificationConstants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getBroadcast(cont, 0, playIntent, 0);

        Intent pauseIntent = new Intent(cont, getClass());
        pauseIntent.setAction(MusicNotificationConstants.ACTION.PAUSE_ACTION);
        PendingIntent ppauseIntent = PendingIntent.getBroadcast(cont, 0, pauseIntent, 0);

        Intent nextIntent = new Intent(cont, getClass());
        nextIntent.setAction(MusicNotificationConstants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getBroadcast(cont, 0, nextIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        if(Music.exoPlayer!=null && !Music.exoPlayer.getPlayWhenReady()){
            views.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_play);
            views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        }else if(Music.exoPlayer!=null && Music.exoPlayer.getPlayWhenReady()){
            views.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
            views.setOnClickPendingIntent(R.id.status_bar_play, ppauseIntent);
        }

        return views;
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


}
