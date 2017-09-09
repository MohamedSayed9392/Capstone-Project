package com.memoseed.mozicaplayer.MusicNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.activities.PlayerActivity_;
import com.memoseed.mozicaplayer.model.Track;
import com.memoseed.mozicaplayer.utils.Music;


/**
 * Created by MohamedSayed on 9/9/2017.
 */

public class MusicNotificationService extends Service {

    String TAG = getClass().getSimpleName();

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(MusicNotificationConstants.NOTIFICATION_ID.FOREGROUND_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (intent.getAction().equals(MusicNotificationConstants.ACTION.STARTFOREGROUND_ACTION)) {
                Log.i(TAG, "Startforground");
                showNotification(MusicNotificationConstants.ACTION.STARTFOREGROUND_ACTION, Music.currentTrack.getTitle(), Music.currentTrack.getArtist());
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
                Music.playTrack(this, track.getFilePath());
                Log.i(TAG, "Clicked Previous");
                showNotification(MusicNotificationConstants.ACTION.NEXT_ACTION, Music.currentTrack.getTitle(), Music.currentTrack.getArtist());
            } else if (intent.getAction().equals(MusicNotificationConstants.ACTION.PLAY_ACTION)) {
                Log.i(TAG, "Clicked Play");
                showNotification(MusicNotificationConstants.ACTION.PLAY_ACTION, Music.currentTrack.getTitle(), Music.currentTrack.getArtist());
                try {
                    Music.exoPlayer.setPlayWhenReady(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals(MusicNotificationConstants.ACTION.PAUSE_ACTION)) {
                Log.i(TAG, "Clicked Pause");
                showNotification(MusicNotificationConstants.ACTION.PAUSE_ACTION, Music.currentTrack.getTitle(), Music.currentTrack.getArtist());
                try {
                    Music.exoPlayer.setPlayWhenReady(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                Music.playTrack(this, track.getFilePath());
                Log.i(TAG, "Clicked Next");
                showNotification(MusicNotificationConstants.ACTION.NEXT_ACTION, Music.currentTrack.getTitle(), Music.currentTrack.getArtist());
            } else if (intent.getAction().equals(MusicNotificationConstants.ACTION.STOPFOREGROUND_ACTION)) {
                Log.i(TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }


    NotificationCompat.Builder builder;
    Intent notificationIntent;
    PendingIntent pendingIntent;

    private void showNotification(String action, String title, String artist) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.music_player_notification);

        // showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        views.setViewVisibility(R.id.status_bar_next, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_prev, View.VISIBLE);

        notificationIntent = new Intent(this, PlayerActivity_.class);
        notificationIntent.setAction(MusicNotificationConstants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Intent previousIntent = new Intent(this, MusicNotificationService.class);
        previousIntent.setAction(MusicNotificationConstants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, MusicNotificationService.class);
        playIntent.setAction(MusicNotificationConstants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent pauseIntent = new Intent(this, MusicNotificationService.class);
        pauseIntent.setAction(MusicNotificationConstants.ACTION.PAUSE_ACTION);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        Intent nextIntent = new Intent(this, MusicNotificationService.class);
        nextIntent.setAction(MusicNotificationConstants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);


        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        views.setOnClickPendingIntent(R.id.linItem, pendingIntent);

        if (action.matches(MusicNotificationConstants.ACTION.PLAY_ACTION) || action.matches(MusicNotificationConstants.ACTION.NEXT_ACTION)
                || action.matches(MusicNotificationConstants.ACTION.PREV_ACTION)) {
            views.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
            views.setOnClickPendingIntent(R.id.status_bar_play, ppauseIntent);
            Log.d(TAG, "Playy");
        } else if (action.matches(MusicNotificationConstants.ACTION.PAUSE_ACTION)) {
            views.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_play);
            views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
            Log.d(TAG, "Pausee");
        }


        views.setTextViewText(R.id.status_bar_track_name, title);
        views.setTextViewText(R.id.status_bar_track_artist, artist);

        builder = new NotificationCompat.Builder(this);
        builder.setContent(views);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        if (action.matches(MusicNotificationConstants.ACTION.PAUSE_ACTION)) {
            builder.setOngoing(false);
        } else {
            builder.setOngoing(true);
        }

        Notification notification = builder.build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MusicNotificationConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

    }
}