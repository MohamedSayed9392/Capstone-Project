package com.memoseed.mozicaplayer.MusicNotification;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.memoseed.mozicaplayer.R;


/**
 * Created by MohamedSayed on 3/8/2017.
 */

public class MusicNotificationConstants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.memoseed.mozicaplayer.action.main";
        public static String PREV_ACTION = "com.memoseed.mozicaplayer.action.prev";
        public static String PAUSE_ACTION = "com.memoseed.mozicaplayer.action.pause";
        public static String PLAY_ACTION = "com.memoseed.mozicaplayer.action.play";
        public static String NEXT_ACTION = "com.memoseed.mozicaplayer.action.next";
        public static String STARTFOREGROUND_ACTION = "com.memoseed.mozicaplayer.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.memoseed.mozicaplayer.action.stopforeground";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 100001;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.track_album_art, options);
        } catch (Error er) {
            er.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bm;
    }

}