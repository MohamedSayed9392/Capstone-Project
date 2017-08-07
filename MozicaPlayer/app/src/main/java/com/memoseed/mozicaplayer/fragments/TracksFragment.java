package com.memoseed.mozicaplayer.fragments;

/**
 * Created by MemoSeed on 03/08/2017.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.adapters.LibraryRVAdapter;
import com.memoseed.mozicaplayer.database.DatabaseHandler;
import com.memoseed.mozicaplayer.database.TracksContentProvider;
import com.memoseed.mozicaplayer.model.Track;
import com.memoseed.mozicaplayer.model.TrackListened;
import com.memoseed.mozicaplayer.utils.UTils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EFragment(R.layout.fragment_all_tracks)
public class TracksFragment extends Fragment {

    String TAG = getClass().getSimpleName();

    public LibraryRVAdapter libraryRVAdapter;
    public List<Track> list = new ArrayList<>();

    public static TracksFragment_ newInstance(int currentTab) {
        TracksFragment_ tracksFragment = new TracksFragment_();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("currentTab", currentTab);
        tracksFragment.setArguments(args);

        return tracksFragment;
    }

    int currentTab;
    DatabaseHandler databaseHandler;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentTab = getArguments().getInt("currentTab");
        Log.d(TAG, "currentTab : " + currentTab);
        databaseHandler = new DatabaseHandler(mContext);
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


    @ViewById public RecyclerView rView;
    public LinearLayoutManager linearLayoutManager;
    @AfterViews
    void afterViews(){
        libraryRVAdapter = new LibraryRVAdapter(mContext,list,currentTab);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rView.setLayoutManager(new LinearLayoutManager(mContext));
        rView.setAdapter(libraryRVAdapter);

        if(currentTab == 0) getSongList();

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void getSongList() {

        //retrieve song info
        ContentResolver musicResolver = mContext.getContentResolver();
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

               list.add(new Track(thisTitle,thisFileName,thisFilePath,"", thisArtist,id,0,duration,added));
            }
            while (musicCursor.moveToNext());
        }

        getAllTrackListeneds();
        libraryRVAdapter.notifyDataSetChanged();

    }

    List<TrackListened> trackListenedList = new ArrayList<>();
    @Background
    public void getAllTrackListeneds() {
        Log.d(TAG,"getAllTrackListeneds start");
        Uri contentUri = Uri.withAppendedPath(TracksContentProvider.CONTENT_URI, DatabaseHandler.TABLE_LISTENED_TRACKS);
        Cursor cursor = mContext.getContentResolver().query(contentUri,null, null, null,null);

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
            for(int j=0;j<list.size();j++){
                Track track = list.get(j);
                if(track.getId()==trackListened.getId()){
                    track.setListened(trackListened.getListened());
                    break;
                }
            }
        }
        Log.d(TAG,"getAllTrackListeneds end");
    }

    private void toast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }
}
