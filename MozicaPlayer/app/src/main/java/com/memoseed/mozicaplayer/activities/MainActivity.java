package com.memoseed.mozicaplayer.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.memoseed.mozicaplayer.AppParameters;
import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.adapters.LibraryPagerAdapter;
import com.memoseed.mozicaplayer.database.DatabaseHandler;
import com.memoseed.mozicaplayer.database.TracksContentProvider;
import com.memoseed.mozicaplayer.debugSystem.ExceptionHandler;
import com.memoseed.mozicaplayer.fragments.TracksFragment_;
import com.memoseed.mozicaplayer.model.Track;
import com.memoseed.mozicaplayer.model.TrackAlbumArt;
import com.memoseed.mozicaplayer.model.TrackListened;
import com.memoseed.mozicaplayer.utils.Music;
import com.memoseed.mozicaplayer.utils.UTils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    public LibraryPagerAdapter libraryPagerAdapter;
    String TAG = getClass().getSimpleName();

    AppParameters p;

    static MainActivity mainActivity;
    public static MainActivity getInstance() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        p = new AppParameters(this);

        mainActivity = this;
        if (Build.VERSION.SDK_INT >= 23) {
            if (UTils.permissionCheck(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    UTils.permissionCheck(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                getSongList();
            } else {
                UTils.permissionGrant(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
            }
        }else{
            getSongList();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSongList();
                } else {
                    UTils.show2OptionsDialoge(this, getString(R.string.need_storage_permission), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UTils.permissionGrant(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, getString(R.string.try_again),getString(R.string.cancel));
               //    Toast.makeText(this, R.string.need_storage_permission, Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
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
                    Collections.sort(Music.favTracks, (w1, w2) -> ((Long)w1.getListened()).compareTo(w2.getListened()));
                }else{
                    Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w2.getListened()).compareTo(w1.getListened()));
                    Collections.sort(Music.favTracks, (w1, w2) -> ((Long)w2.getListened()).compareTo(w1.getListened()));
                }

                notifyAllTabsDataChanged();
                if(Music.currentTrack!=null) {
                    for(int i=0;i<Music.allTracks.size();i++){
                        Track track1 = Music.allTracks.get(i);
                        if(track1.getId() == Music.currentTrack.getId()){
                            ((TracksFragment_)libraryPagerAdapter.getItem(0)).rView.scrollToPosition(i);
                            break;
                        }
                    }
                    for(int i=0;i<Music.favTracks.size();i++){
                        Track track1 = Music.favTracks.get(i);
                        if(track1.getId() == Music.currentTrack.getId()){
                            ((TracksFragment_)libraryPagerAdapter.getItem(1)).rView.scrollToPosition(i);
                            break;
                        }
                    }
                }
            }else{
                if(Music.currentTrack!=null) {
                    for(int i=0;i<Music.allTracks.size();i++){
                        Track track1 = Music.allTracks.get(i);
                        if(track1.getId() == Music.currentTrack.getId()){
                            ((TracksFragment_)libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
                            ((TracksFragment_)libraryPagerAdapter.getItem(0)).rView.scrollToPosition(i);
                            break;
                        }
                    }
                    for(int i=0;i<Music.favTracks.size();i++){
                        Track track1 = Music.favTracks.get(i);
                        if(track1.getId() == Music.currentTrack.getId()){
                            ((TracksFragment_)libraryPagerAdapter.getItem(1)).libraryRVAdapter.notifyDataSetChanged();
                            ((TracksFragment_)libraryPagerAdapter.getItem(1)).rView.scrollToPosition(i);
                            break;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @ViewById
    ViewPager viewPager;
    @ViewById
    TabLayout tabLayout;

    @ViewById
    Toolbar toolbar;

    @AfterViews
    void afterViews(){
        setSupportActionBar(toolbar);
        viewPager.setAdapter(libraryPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.all);
        tabLayout.getTabAt(1).setText(R.string.favourites);
    }
    
    public void getSongList() {

        Music.allTracks.clear();
        Music.favTracks.clear();
        getLoaderManager().initLoader(0, null, this);


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
    public void getAllTracksAlbumArts() {
        Log.d(TAG,"getAllTracksAlbumArts start");
        Uri contentUri = Uri.withAppendedPath(TracksContentProvider.CONTENT_URI, DatabaseHandler.TABLE_ALBUM_ART_TRACKS);
        Cursor cursor = getContentResolver().query(contentUri,null, null, null,null);

        List<TrackAlbumArt> trackAlbumArtsList = new ArrayList<TrackAlbumArt>();
        // looping through all rows and adding to listTrackAlbumArt
        if (cursor.moveToFirst()) {
            do {
                int KEY_ID = cursor.getColumnIndex("id");
                int KEY_ALBUM_ART = cursor.getColumnIndex("albumArt");

                TrackAlbumArt trackAlbumArt = new TrackAlbumArt(cursor.getLong(KEY_ID), cursor.getString(KEY_ALBUM_ART));
                Log.d(TAG,trackAlbumArt.getId() +" - "+trackAlbumArt.getAlbumArt());
                // Adding trackListened to list
                trackAlbumArtsList.add(trackAlbumArt);
            } while (cursor.moveToNext());
        }

        for(int i=0;i<trackAlbumArtsList.size();i++){
            TrackAlbumArt trackAlbumArt = trackAlbumArtsList.get(i);
            for(int j=0;j<Music.allTracks.size();j++){
                Track track = Music.allTracks.get(j);
                if(track.getId()==trackAlbumArt.getId()){
                    track.setAlbumArt(trackAlbumArt.getAlbumArt());
                    break;
                }
            }
        }
        Log.d(TAG,"getAllTracksAlbumArts end");
    }

    @Background
    public void getFavTracks() {
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
        Log.d(TAG,"getAllTrackFav : all : "+Music.allTracks.size());
        Log.d(TAG,"getAllTrackFav : fav : "+Music.favTracks.size());
        updateUi();
        Log.d(TAG,"getAllTrackFav end");
    }

    @UiThread
    public void updateUi(){
        descending = p.getBoolean("default_sort_descending", false);
        libraryPagerAdapter = new LibraryPagerAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(libraryPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.all);
        tabLayout.getTabAt(1).setText(R.string.favourites);
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
        if(!sortTracks(id)) return super.onOptionsItemSelected(item);
        else return true;
    }

    public boolean sortTracks(int id){
        Log.d(TAG,"sort allTracks size : "+Music.allTracks.size());
        Log.d(TAG,"sort favTracks size : "+Music.favTracks.size());
        switch (id) {
            case R.id.sort_file_name:Log.d(TAG,"sort 0");
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> w1.getFileName().compareTo(w2.getFileName()));
                    Collections.sort(Music.favTracks, (w1, w2) -> w1.getFileName().compareTo(w2.getFileName()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> w1.getFileName().compareTo(w2.getFileName()));
                        Collections.sort(Music.favTracks, (w1, w2) -> w1.getFileName().compareTo(w2.getFileName()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                        Collections.reverse(Music.favTracks);
                    }
                }
                p.setInt(id,"default_sort");
                p.setBoolean(descending,"default_sort_descending");
                notifyAllTabsDataChanged();
                return true;
            case R.id.sort_title:Log.d(TAG,"sort 1");
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> w1.getTitle().compareTo(w2.getTitle()));
                    Collections.sort(Music.favTracks, (w1, w2) -> w1.getTitle().compareTo(w2.getTitle()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> w1.getTitle().compareTo(w2.getTitle()));
                        Collections.sort(Music.favTracks, (w1, w2) -> w1.getTitle().compareTo(w2.getTitle()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                        Collections.reverse(Music.favTracks);
                    }
                }
                p.setInt(id,"default_sort");
                p.setBoolean(descending,"default_sort_descending");
                notifyAllTabsDataChanged();
                return true;
            case R.id.sort_duration:Log.d(TAG,"sort 2");
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getDuration()).compareTo(w2.getDuration()));
                    Collections.sort(Music.favTracks, (w1, w2) -> ((Long)w1.getDuration()).compareTo(w2.getDuration()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getDuration()).compareTo(w2.getDuration()));
                        Collections.sort(Music.favTracks, (w1, w2) -> ((Long)w1.getDuration()).compareTo(w2.getDuration()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                        Collections.reverse(Music.favTracks);
                    }
                }
                p.setInt(id,"default_sort");
                p.setBoolean(descending,"default_sort_descending");
                notifyAllTabsDataChanged();
                return true;
            case R.id.sort_added:Log.d(TAG,"sort 3");
                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getAdded()).compareTo(w2.getAdded()));
                    Collections.sort(Music.favTracks, (w1, w2) -> ((Long)w1.getAdded()).compareTo(w2.getAdded()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getAdded()).compareTo(w2.getAdded()));
                        Collections.sort(Music.favTracks, (w1, w2) -> ((Long)w1.getAdded()).compareTo(w2.getAdded()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                        Collections.reverse(Music.favTracks);
                    }
                }
                p.setInt(id,"default_sort");
                p.setBoolean(descending,"default_sort_descending");
                notifyAllTabsDataChanged();
                return true;
            case R.id.sort_listened:Log.d(TAG,"sort 4");

                if(oldOptionItemSelectedid!=id) {
                    oldOptionItemSelectedid = id;
                    Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getListened()).compareTo(w2.getListened()));
                    Collections.sort(Music.favTracks, (w1, w2) -> ((Long)w1.getListened()).compareTo(w2.getListened()));
                }else{
                    if(descending){
                        descending = false;
                        Collections.sort(Music.allTracks, (w1, w2) -> ((Long)w1.getListened()).compareTo(w2.getListened()));
                        Collections.sort(Music.favTracks, (w1, w2) -> ((Long)w1.getListened()).compareTo(w2.getListened()));
                    }else{
                        descending = true;
                        Collections.reverse(Music.allTracks);
                        Collections.reverse(Music.favTracks);
                    }
                }
                p.setInt(id,"default_sort");
                p.setBoolean(descending,"default_sort_descending");
                notifyAllTabsDataChanged();
                return true;
                default:Log.d(TAG,"sort 5");
                    return false;
        }
    }

    public void scrollToPosition(int position,int currentTab){
        Log.d(TAG,"scrollToPosition("+position+","+currentTab+")");
        try {
            ((TracksFragment_) libraryPagerAdapter.getItem(currentTab)).rView.scrollToPosition(position);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void notifyAllTabsDataChanged(){
        try {
            ((TracksFragment_) libraryPagerAdapter.getItem(0)).libraryRVAdapter.notifyDataSetChanged();
            scrollToPosition(0, 0);
            ((TracksFragment_) libraryPagerAdapter.getItem(1)).libraryRVAdapter.notifyDataSetChanged();
            scrollToPosition(0, 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(this, musicUri, null, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor musicCursor) {
        Log.d(TAG,"onLoadFinished");
        if(musicCursor!=null && musicCursor.moveToFirst()){
            Log.d(TAG,"onLoadFinished0");
            //get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
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

                if(!thisFilePath.contains("ACRCalls")) Music.allTracks.add(new Track(thisTitle,thisFileName,thisFilePath,"", thisArtist,id,0,duration,added,false));
            }
            while (musicCursor.moveToNext());
        }else{
            Log.d(TAG,"onLoadFinished1");
        }

        Log.d(TAG,"onLoadFinished2 : "+Music.allTracks.size());
        getAllTrackListeneds();
        getAllTracksAlbumArts();
        getFavTracks();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
