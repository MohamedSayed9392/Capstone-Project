package com.memoseed.mozicaplayer.database;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.memoseed.mozicaplayer.model.Track;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tracksDB";
    public static final String TABLE_FAV_TRACKS = "favourite_tracks";
    public static final String TABLE_LISTENED_TRACKS = "listened_tracks";
    public static final String TABLE_ALBUM_ART_TRACKS = "album_art_tracks";

    private static final String KEY_ID = "id";
    private static final String KEY_LISTENED = "listened";
    private static final String KEY_ALBUM_ART = "albumArt";

 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LISTENED_TRACKS_TABLE = "CREATE TABLE " + TABLE_LISTENED_TRACKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LISTENED + " INTEGER"
                + ")";

        String CREATE_ALBUM_ART_TRACKS_TABLE = "CREATE TABLE " + TABLE_ALBUM_ART_TRACKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ALBUM_ART + " TEXT"
                + ")";

        String CREATE_FAV_TRACKS_TABLE = "CREATE TABLE " + TABLE_FAV_TRACKS + "(" + KEY_ID + " INTEGER PRIMARY KEY)";

        db.execSQL(CREATE_LISTENED_TRACKS_TABLE);
        db.execSQL(CREATE_ALBUM_ART_TRACKS_TABLE);
        db.execSQL(CREATE_FAV_TRACKS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTENED_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM_ART_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV_TRACKS);
        // Create tables again
        onCreate(db);
    }

    public boolean isRowExist(String table,long id) {
        String countQuery = "SELECT  * FROM " + table +" where id = "+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        boolean found;
        if(cursor.getCount()>0){
            found = true;
        }else{
            found = false;
        }
        cursor.close();
        return found;
    }
}