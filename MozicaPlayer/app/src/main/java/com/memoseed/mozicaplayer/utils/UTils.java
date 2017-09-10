package com.memoseed.mozicaplayer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.Display;

import com.memoseed.mozicaplayer.AppWidget;
import com.memoseed.mozicaplayer.activities.MainActivity;
import com.memoseed.mozicaplayer.database.DatabaseHandler;
import com.memoseed.mozicaplayer.database.TracksContentProvider;
import com.memoseed.mozicaplayer.model.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed Sayed on 2/28/2016.
 */
public class UTils {

    public static boolean isOnline(Context context) {
        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    connected = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    connected = true;
        }
        return connected;
    }

    public static   int convertDpToPixel(int dp,Context cont){
        Resources resources = cont.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * (metrics.densityDpi / 160f));
        return px;
    }

    public static  float convertPxToDP(int px,Context cont){
        Resources resources = cont.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = (float) (px / (metrics.densityDpi / 160f));
        return dp;
    }

    public static void openUrl(Activity activity, String Url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(Url));
        activity.startActivity(i);
    }

    public static void shareText(Activity activity, String text, String title) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
        activity.startActivity(Intent.createChooser(sharingIntent, title));
    }


    public static void showProgressDialog(String title, String message, ProgressDialog progressDialog) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog(ProgressDialog progressDialog) {
        progressDialog.hide();
    }

    public static int getScreenOrientation(Activity activity)
    {
        Display getOrient = activity.getWindowManager().getDefaultDisplay();
        int orientation;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    public static long getListenedCount(Context context,int id) {
        long listened = 0;
        try {
            Uri contentUri = Uri.withAppendedPath(TracksContentProvider.CONTENT_URI, DatabaseHandler.TABLE_LISTENED_TRACKS);
            Cursor cursor = context.getContentResolver().query(contentUri, null, "id = ?", new String[]{String.valueOf(id)}, null);

            if (cursor.moveToFirst()) {
                do {
                    int KEY_LISTENED = cursor.getColumnIndex("listened");
                    listened = cursor.getLong(KEY_LISTENED);

                } while (cursor.moveToNext());
            }

        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return listened;
    }

    public static String  getAlbumArt(Context context,int id) {
        String albumArt = "";
        try {
            Uri contentUri = Uri.withAppendedPath(TracksContentProvider.CONTENT_URI, DatabaseHandler.TABLE_ALBUM_ART_TRACKS);
            Cursor cursor = context.getContentResolver().query(contentUri, null, "id = ?", new String[]{String.valueOf(id)}, null);

            if (cursor.moveToFirst()) {
                do {
                    int KEY_ALBUM_ART = cursor.getColumnIndex("albumArt");
                    albumArt = cursor.getString(KEY_ALBUM_ART);

                } while (cursor.moveToNext());
            }

        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return albumArt;
    }

    public static void updateWidget(Context context) {

        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] ids = man.getAppWidgetIds(new ComponentName(context, AppWidget.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidget.WIDGET_IDS_KEY, ids);
        context.sendBroadcast(updateIntent);
    }
    public static boolean permissionCheck(Activity activity, String permission) {
        if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean permissionCheckContext(Context context, String permission) {
        if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void permissionGrant(Activity activity, String permission, int permissionCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionCode);
    }


    public static void show2OptionsDialoge(Activity activity, String msg, DialogInterface.OnClickListener listenerPos, DialogInterface.OnClickListener listenerNeg, String txtbtnP, String txtbtnN) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(txtbtnP, listenerPos)
                .setNegativeButton(txtbtnN, listenerNeg);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
