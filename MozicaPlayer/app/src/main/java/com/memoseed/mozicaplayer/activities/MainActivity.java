package com.memoseed.mozicaplayer.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.adapters.LibraryPagerAdapter;
import com.memoseed.mozicaplayer.database.DatabaseHandler;
import com.memoseed.mozicaplayer.database.TracksContentProvider;
import com.memoseed.mozicaplayer.debugSystem.ExceptionHandler;
import com.memoseed.mozicaplayer.fragments.TracksFragment_;
import com.memoseed.mozicaplayer.model.Track;
import com.memoseed.mozicaplayer.model.TrackListened;
import com.memoseed.mozicaplayer.utils.Music;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    public LibraryPagerAdapter libraryPagerAdapter;
    String TAG = getClass().getSimpleName();

    static MainActivity mainActivity;
    public static MainActivity getInstance() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        mainActivity = this;
        getSongList();
        libraryPagerAdapter = new LibraryPagerAdapter(getSupportFragmentManager(),2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivity = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (oldOptionItemSelectedid == R.id.sort_listened) {
                if(!descending){
                    Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getListened()).compareTo(w2.getListened()));
                }else{
                    Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w2.getListened()).compareTo(w1.getListened()));
                }

                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                for(int i=0;i<Music.allTracks.size();i++){
                    Track track1 = Music.allTracks.get(i);
                    if(track1.getId() == Music.currentTrack.getId()){
                        ((TracksFragment_)libraryPagerAdapter.getItem(0)).rView.scrollToPosition(i);
                        break;
                    }
                }
            }else{
                for(int i=0;i<Music.allTracks.size();i++){
                    Track track1 = Music.allTracks.get(i);
                    if(track1.getId() == Music.currentTrack.getId()){
                        ((TracksFragment_)libraryPagerAdapter.getItem(0)).rView.scrollToPosition(i);
                        break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @ViewById
    ViewPager viewPager;

    @AfterViews
    void afterViews(){
        viewPager.setAdapter(libraryPagerAdapter);
    }
    
    public void getSongList() {

        Music.allTracks.clear();
        Music.favTracks.clear();

        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int fileNameColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int filePathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
      /*      int albumArtColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int listenedColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);*/
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int addedColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);
            //add songs to list
            do {
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisFileName = musicCursor.getString(fileNameColumn);
                String thisFilePath = "file:///"+musicCursor.getString(filePathColumn);
               /* String thisAlbumArt = musicCursor.getString(fileNameColumn);
                long listened = musicCursor.getLong(fileNameColumn);*/
                long id = musicCursor.getLong(idColumn); //  Log.d(TAG,"track_id : "+id);
                long duration = musicCursor.getLong(durationColumn);
                long added = musicCursor.getLong(addedColumn);

                Music.allTracks.add(new Track(thisTitle,thisFileName,thisFilePath,"", thisArtist,id,0,duration,added,false));
            }
            while (musicCursor.moveToNext());
        }

        getAllTrackListeneds();
        getFavTrackListeneds();

    }

    @Background
    public void getAllTrackListeneds() {
        Log.d(TAG,"getAllTrackListeneds start");
        Uri contentUri = Uri.withAppendedPath(TracksContentProvider.CONTENT_URI, DatabaseHandler.TABLE_LISTENED_TRACKS);
        Cursor cursor = getContentResolver().query(contentUri,null, null, null,null);

        List<TrackListened> trackListenedList = new ArrayList<TrackListened>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int KEY_ID = cursor.getColumnIndex("id");
                int KEY_LISTENED = cursor.getColumnIndex("listened");

                TrackListened trackListened = new TrackListened(cursor.getLong(KEY_ID), cursor.getLong(KEY_LISTENED));
                // Adding trackListened to list
                trackListenedList.add(trackListened);
            } while (cursor.moveToNext());
        }

        for(int i=0;i<trackListenedList.size();i++){
            TrackListened trackListened = trackListenedList.get(i);
            for(int j=0;j<Music.allTracks.size();j++){
                Track track = Music.allTracks.get(j);
                if(track.getId()==trackListened.getId()){
                    track.setListened(trackListened.getListened());
                    break;
                }
            }
        }
        Log.d(TAG,"getAllTrackListeneds end");
    }

    @Background
    public void getFavTrackListeneds() {
        Log.d(TAG,"getAllTrackFav start");
        Uri contentUri = Uri.withAppendedPath(TracksContentProvider.CONTENT_URI, DatabaseHandler.TABLE_FAV_TRACKS);
        Cursor cursor = getContentResolver().query(contentUri,null, null, null,null);

        List<Long> trackFavList = new ArrayList<>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int KEY_ID = cursor.getColumnIndex("id");
                trackFavList.add(cursor.getLong(KEY_ID));

            } while (cursor.moveToNext());
        }

        for(int i=0;i<trackFavList.size();i++){
            long fav = trackFavList.get(i);
            for(int j=0;j<Music.allTracks.size();j++){
                Track track = Music.allTracks.get(j);
                if(track.getId()==fav){
                    track.setFav(true);
                    Music.favTracks.add(track);
                    break;
                }
            }
        }
        Log.d(TAG,"getAllTrackFav end");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public int oldOptionItemSelectedid=0;
    public boolean descending = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.d(TAG,item.getTitle().toString());
        switch (id) {
            case R.id.sort_file_name:
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> w1.getFileName().compareTo(w2.getFileName()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> w1.getFileName().compareTo(w2.getFileName()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                    }
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_title:
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> w1.getTitle().compareTo(w2.getTitle()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> w1.getTitle().compareTo(w2.getTitle()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                    }
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_duration:
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getDuration()).compareTo(w2.getDuration()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getDuration()).compareTo(w2.getDuration()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                    }
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_added:
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getAdded()).compareTo(w2.getAdded()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getAdded()).compareTo(w2.getAdded()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                    }
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_listened:

                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getListened()).compareTo(w2.getListened()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getListened()).compareTo(w2.getListened()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                    }
                }
                ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void scrollToPosition(int position,int currentTab){
        Log.d(TAG,"scrollToPosition("+position+","+currentTab+")");
        ((TracksFragment_) libraryPagerAdapter.getItem(currentTab)).rView.scrollToPosition(position);
    }
}
